/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Benoit Delbosc
 */
package org.nuxeo.ecm.core.redis.contribs;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.model.Repository;
import org.nuxeo.ecm.core.redis.RedisAdmin;
import org.nuxeo.ecm.core.redis.RedisExecutor;
import org.nuxeo.ecm.core.storage.dbs.DBSClusterInvalidator;
import org.nuxeo.ecm.core.storage.dbs.DBSInvalidations;
import org.nuxeo.runtime.api.Framework;

import redis.clients.jedis.JedisPubSub;

/**
 * Redis implementation of {@link DBSClusterInvalidator}. Use a single channel pubsub to send invalidations. Use an HSET
 * to register nodes, only for debug purpose so far.
 *
 * @since 8.10
 * @deprecated since 9.1, use DBSPubSubInvalidator instead
 */
@Deprecated
public class RedisDBSClusterInvalidator implements DBSClusterInvalidator {

    protected static final String PREFIX = "inval";

    // PubSub channel: nuxeo:inval:<repositoryName>:channel
    protected static final String INVALIDATION_CHANNEL = "channel";

    // Node HSET key: nuxeo:inval:<repositoryName>:nodes:<nodeId>
    protected static final String CLUSTER_NODES_KEY = "nodes";

    // Keep info about a cluster node for one day
    protected static final int TIMEOUT_REGISTER_SECOND = 24 * 3600;

    // Max delay to wait for a channel subscription
    protected static final long TIMEOUT_SUBSCRIBE_SECOND = 10;

    protected static final String STARTED_FIELD = "started";

    protected static final String LAST_INVAL_FIELD = "lastInvalSent";

    protected String nodeId;

    protected String repositoryName;

    protected RedisExecutor redisExecutor;

    protected DBSInvalidations receivedInvals;

    protected Thread subscriberThread;

    protected String namespace;

    protected String startedDateTime;

    private static final Log log = LogFactory.getLog(RedisDBSClusterInvalidator.class);

    private CountDownLatch subscribeLatch;

    private String registerSha;

    private String sendSha;

    @Override
    public void initialize(String nodeId, String repositoryName) {
        this.nodeId = nodeId;
        this.repositoryName = repositoryName;
        redisExecutor = Framework.getService(RedisExecutor.class);
        RedisAdmin redisAdmin = Framework.getService(RedisAdmin.class);
        namespace = redisAdmin.namespace(PREFIX, repositoryName);
        try {
            registerSha = redisAdmin.load("org.nuxeo.ecm.core.redis", "register-node-inval");
            sendSha = redisAdmin.load("org.nuxeo.ecm.core.redis", "send-inval");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        receivedInvals = new DBSInvalidations();
        createSubscriberThread();
        registerNode();
    }

    protected void createSubscriberThread() {
        subscribeLatch = new CountDownLatch(1);
        String name = "RedisDBSClusterInvalidatorSubscriber:" + repositoryName + ":" + nodeId;
        subscriberThread = new Thread(this::subscribeToInvalidationChannel, name);
        subscriberThread.setUncaughtExceptionHandler((t, e) -> log.error("Uncaught error on thread " + t.getName(), e));
        subscriberThread.setPriority(Thread.NORM_PRIORITY);
        subscriberThread.start();
        try {
            if (!subscribeLatch.await(TIMEOUT_SUBSCRIBE_SECOND, TimeUnit.SECONDS)) {
                log.error("Redis channel subscription timeout after " + TIMEOUT_SUBSCRIBE_SECOND
                        + "s, continuing but this node may not receive cluster invalidations");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    protected void subscribeToInvalidationChannel() {
        log.info("Subscribing to channel: " + getChannelName());
        redisExecutor.subscribe(new JedisPubSub() {
                @Override
                public void onSubscribe(String channel, int subscribedChannels) {
                    super.onSubscribe(channel, subscribedChannels);
                    if (subscribeLatch != null) {
                        subscribeLatch.countDown();
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Subscribed to channel: " + getChannelName());
                    }
                }

                @Override
                public void onMessage(String channel, String message) {
                    try {
                        RedisDBSInvalidations rInvals = new RedisDBSInvalidations(nodeId, message);
                        if (log.isTraceEnabled()) {
                            log.trace("Receive invalidations: " + rInvals);
                        }
                        DBSInvalidations invals = rInvals.getInvalidations();
                        synchronized (RedisDBSClusterInvalidator.this) {
                            receivedInvals.add(invals);
                        }
                    } catch (IllegalArgumentException e) {
                        log.error("Fail to read message: " + message, e);
                    }
                }
            }, getChannelName());
    }

    protected String getChannelName() {
        return namespace + INVALIDATION_CHANNEL;
    }

    protected void registerNode() {
        startedDateTime = getCurrentDateTime();
        List<String> keys = Collections.singletonList(getNodeKey());
        List<String> args = Arrays.asList(STARTED_FIELD, startedDateTime,
                Integer.valueOf(TIMEOUT_REGISTER_SECOND).toString());
        if (log.isDebugEnabled()) {
            log.debug("Registering node: " + nodeId);
        }

        redisExecutor.evalsha(registerSha, keys, args);
        if (log.isInfoEnabled()) {
            log.info("Node registered: " + nodeId);
        }
    }

    protected String getNodeKey() {
        return namespace + CLUSTER_NODES_KEY + ":" + nodeId;
    }

    @Override
    public void close() {
        log.debug("Closing");
        unsubscribeToInvalidationChannel();
        // The Jedis pool is already closed when the repository is shutdowned
        receivedInvals.clear();
    }

    protected void unsubscribeToInvalidationChannel() {
        subscriberThread.interrupt();
    }

    @Override
    public DBSInvalidations receiveInvalidations() {
        DBSInvalidations newInvals = new DBSInvalidations();
        DBSInvalidations ret;
        synchronized (this) {
            ret = receivedInvals;
            receivedInvals = newInvals;
        }
        return ret;
    }

    @Override
    public void sendInvalidations(DBSInvalidations invals) {
        RedisDBSInvalidations rInvals = new RedisDBSInvalidations(nodeId, invals);
        if (log.isTraceEnabled()) {
            log.trace("Sending invalidations: " + rInvals);
        }
        List<String> keys = Arrays.asList(getChannelName(), getNodeKey());
        List<String> args = Arrays.asList(rInvals.serialize(), STARTED_FIELD, startedDateTime, LAST_INVAL_FIELD,
                getCurrentDateTime(), Integer.valueOf(TIMEOUT_REGISTER_SECOND).toString());

        redisExecutor.evalsha(sendSha, keys, args);
        log.trace("invals sent");
    }

    protected String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }
}
