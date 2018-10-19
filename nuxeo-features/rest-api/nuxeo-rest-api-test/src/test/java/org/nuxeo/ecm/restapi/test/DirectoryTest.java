/*
 * (C) Copyright 2013 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     dmetzler
 */
package org.nuxeo.ecm.restapi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.io.registry.MarshallerHelper;
import org.nuxeo.ecm.core.io.registry.context.RenderingContext.CtxBuilder;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryEntry;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.directory.io.DirectoryEntryJsonWriter;
import org.nuxeo.ecm.directory.io.DirectoryEntryListJsonWriter;
import org.nuxeo.ecm.directory.io.DirectoryListJsonWriter;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.transaction.TransactionHelper;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @since 5.7.3
 */
@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class })
@Jetty(port = 18090)
@RepositoryConfig(init = RestServerInit.class, cleanup = Granularity.METHOD)
@LocalDeploy("org.nuxeo.ecm.platform.restapi.test:test-directory-contrib.xml")
public class DirectoryTest extends BaseTest {

    @Inject
    DirectoryService ds;

    /**
    *
    */
    private static final String TESTDIRNAME = "testdir";

    Session dirSession = null;

    @Override
    @Before
    public void doBefore() throws Exception {
        super.doBefore();
        dirSession = ds.open(TESTDIRNAME);
        // see committed directory changes (init)
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
    }

    @After
    public void doAfter() throws Exception {
        if (dirSession != null) {
            dirSession.close();
        }
    }

    protected void nextTransaction() {
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
    }

    @Test
    public void itCanQueryDirectoryEntry() throws Exception {
        // Given a directoryEntry
        DocumentModel docEntry = dirSession.getEntry("test1");
        // When I call the Rest endpoint
        JsonNode node = getResponseAsJson(RequestType.GET, "/directory/" + TESTDIRNAME + "/test1");

        assertEquals(DirectoryEntryJsonWriter.ENTITY_TYPE, node.get("entity-type").getValueAsText());
        assertEquals(TESTDIRNAME, node.get("directoryName").getValueAsText());
        assertEquals(docEntry.getPropertyValue("vocabulary:label"),
                node.get("properties").get("label").getValueAsText());

    }

    /**
     * @since 8.4
     */
    @Test
    public void itCanQueryDirectoryNames() throws Exception {
        // When I call the Rest endpoint
        JsonNode node = getResponseAsJson(RequestType.GET, "/directory");

        // It should not return system directories
        assertEquals(DirectoryListJsonWriter.ENTITY_TYPE, node.get("entity-type").getValueAsText());
        assertEquals(2, node.get("entries").size());
        assertEquals("continent", node.get("entries").get(0).get("name").getTextValue());
        assertEquals("country", node.get("entries").get(1).get("name").getTextValue());

        // It should not retrieve directory with unknown type
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.put("types", Arrays.asList(new String[] { "notExistingType" }));
        node = getResponseAsJson(RequestType.GET, "/directory", queryParams);
        assertEquals(DirectoryListJsonWriter.ENTITY_TYPE, node.get("entity-type").getValueAsText());
        assertEquals(0, node.get("entries").size());

        // It should not retrieve system directories
        queryParams = new MultivaluedMapImpl();
        queryParams.put("types", Arrays.asList(new String[] { DirectoryService.SYSTEM_DIRECTORY_TYPE }));
        node = getResponseAsJson(RequestType.GET, "/directory", queryParams);
        assertEquals(DirectoryListJsonWriter.ENTITY_TYPE, node.get("entity-type").getValueAsText());
        assertEquals(0, node.get("entries").size());

        // It should be able to retrieve a single type
        queryParams = new MultivaluedMapImpl();
        queryParams.put("types", Arrays.asList(new String[] { "toto" }));
        node = getResponseAsJson(RequestType.GET, "/directory", queryParams);
        assertEquals(DirectoryListJsonWriter.ENTITY_TYPE, node.get("entity-type").getValueAsText());
        assertEquals(1, node.get("entries").size());

        // It should be able to retrieve many types
        queryParams = new MultivaluedMapImpl();
        queryParams.put("types", Arrays.asList(new String[] { "toto", "pouet" }));
        node = getResponseAsJson(RequestType.GET, "/directory", queryParams);
        assertEquals(DirectoryListJsonWriter.ENTITY_TYPE, node.get("entity-type").getValueAsText());
        assertEquals(2, node.get("entries").size());
    }

    /**
     * @since 8.4
     */
    @Test
    public void itCannotDeleteDirectoryEntryWithConstraints() throws Exception {
        // When I try to delete an entry which has contraints
        ClientResponse response = getResponse(RequestType.DELETE, "/directory/continent/europe");
        // It should fail
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        // When I remove all the contraints
        JsonNode node = getResponseAsJson(RequestType.GET, "/directory/country");
        ArrayNode jsonEntries = (ArrayNode) node.get("entries");
        Iterator<JsonNode> it = jsonEntries.getElements();
        while (it.hasNext()) {
            JsonNode props = it.next().get("properties");
            if ("europe".equals(props.get("parent").getTextValue())) {
                response = getResponse(RequestType.DELETE, "/directory/country/" + props.get("id").getTextValue());
                assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
            }
        }
        // I should be able to delete the entry
        response = getResponse(RequestType.DELETE, "/directory/continent/europe");
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void itCanQueryDirectoryEntries() throws Exception {
        // Given a directory
        DocumentModelList entries = dirSession.query(Collections.emptyMap());

        // When i do a request on the directory endpoint
        JsonNode node = getResponseAsJson(RequestType.GET, "/directory/" + TESTDIRNAME);

        // Then i receive the response as json
        assertEquals(DirectoryEntryListJsonWriter.ENTITY_TYPE, node.get("entity-type").getValueAsText());
        ArrayNode jsonEntries = (ArrayNode) node.get("entries");
        assertEquals(entries.size(), jsonEntries.size());

    }

    @Test
    public void itCanUpdateADirectoryEntry() throws Exception {
        // Given a directory modified entry as Json
        DocumentModel docEntry = dirSession.getEntry("test1");
        docEntry.setPropertyValue("vocabulary:label", "newlabel");
        String jsonEntry = getDirectoryEntryAsJson(docEntry);

        // When i do an update request on the entry endpoint
        ClientResponse response = getResponse(RequestType.PUT, "/directory/" + TESTDIRNAME + "/" + docEntry.getId(),
                jsonEntry);

        // Then the entry is updated
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        nextTransaction(); // see committed changes
        docEntry = dirSession.getEntry("test1");
        assertEquals("newlabel", docEntry.getPropertyValue("vocabulary:label"));

    }

    @Test
    public void itCanCreateADirectoryEntry() throws Exception {
        // Given a directory modified entry as Json
        DocumentModel docEntry = dirSession.getEntry("test1");
        docEntry.setPropertyValue("vocabulary:id", "newtest");
        docEntry.setPropertyValue("vocabulary:label", "newlabel");
        assertNull(dirSession.getEntry("newtest"));
        String jsonEntry = getDirectoryEntryAsJson(docEntry);

        // When i do an update request on the entry endpoint
        ClientResponse response = getResponse(RequestType.POST, "/directory/" + TESTDIRNAME, jsonEntry);

        // Then the entry is updated
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        nextTransaction(); // see committed changes
        docEntry = dirSession.getEntry("newtest");
        assertEquals("newlabel", docEntry.getPropertyValue("vocabulary:label"));

    }

    @Test
    public void itCanDeleteADirectoryEntry() throws Exception {
        // Given an existent entry
        DocumentModel docEntry = dirSession.getEntry("test2");
        assertNotNull(docEntry);

        // When i do a DELETE request on the entry endpoint
        getResponse(RequestType.DELETE, "/directory/" + TESTDIRNAME + "/" + docEntry.getId());

        // Then the entry is deleted
        nextTransaction(); // see committed changes
        assertNull(dirSession.getEntry("test2"));

    }

    @Test
    public void itSends404OnnotExistentDirectory() throws Exception {
        ClientResponse response = getResponse(RequestType.GET, "/directory/nonexistendirectory");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void itSends404OnnotExistentDirectoryEntry() throws Exception {
        ClientResponse response = getResponse(RequestType.GET, "/directory/" + TESTDIRNAME + "/nonexistententry");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void genericUserCanNotEditDirectories() throws Exception {
        // As a generic user
        service = getServiceFor("user1", "user1");

        // Given a directory entry as Json
        DocumentModel docEntry = dirSession.getEntry("test1");
        String jsonEntry = getDirectoryEntryAsJson(docEntry);

        // When i do an update request on the entry endpoint
        ClientResponse response = getResponse(RequestType.PUT, "/directory/" + TESTDIRNAME + "/" + docEntry.getId(),
                jsonEntry);
        // Then it is unauthorized
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        // When i do an create request on the entry endpoint
        response = getResponse(RequestType.POST, "/directory/" + TESTDIRNAME, jsonEntry);
        // Then it is unauthorized
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        // When i do an delete request on the entry endpoint
        response = getResponse(RequestType.DELETE, "/directory/" + TESTDIRNAME + "/" + docEntry.getId());
        // Then it is unauthorized
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

    }

    @Test
    public void userDirectoryAreNotEditable() throws Exception {

        // Given a user directory entry
        UserManager um = Framework.getLocalService(UserManager.class);
        DocumentModel model = um.getUserModel("user1");
        String userDirectoryName = um.getUserDirectoryName();
        String jsonEntry = getDirectoryEntryAsJson(userDirectoryName, model);

        // When i do an update request on it
        ClientResponse response = getResponse(RequestType.POST, "/directory/" + userDirectoryName, jsonEntry);
        // Then it is unauthorized
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

    }

    @Test
    public void itShouldNotWritePasswordFieldInResponse() throws Exception {
        // Given a user directory entry
        UserManager um = Framework.getLocalService(UserManager.class);
        String userDirectoryName = um.getUserDirectoryName();

        // When i do an update request on it
        JsonNode node = getResponseAsJson(RequestType.GET, "/directory/" + userDirectoryName + "/user1");

        assertEquals("", node.get("properties").get("password").getValueAsText());

    }

    @Test
    public void groupDirectoryAreNotEditable() throws Exception {

        // Given a user directory entry
        UserManager um = Framework.getLocalService(UserManager.class);
        DocumentModel model = um.getGroupModel("group1");
        String groupDirectoryName = um.getGroupDirectoryName();
        String jsonEntry = getDirectoryEntryAsJson(groupDirectoryName, model);

        // When i do an create request on it
        ClientResponse response = getResponse(RequestType.POST, "/directory/" + groupDirectoryName, jsonEntry);
        // Then it is unauthorized
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

    }

    private String getDirectoryEntryAsJson(DocumentModel dirEntry) throws IOException, JsonGenerationException {
        return getDirectoryEntryAsJson(TESTDIRNAME, dirEntry);
    }

    private String getDirectoryEntryAsJson(String dirName, DocumentModel dirEntry) throws IOException,
            JsonGenerationException {
        return MarshallerHelper.objectToJson(new DirectoryEntry(dirName, dirEntry), CtxBuilder.get());
    }

}
