/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.versioning;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.model.Document;
import org.nuxeo.ecm.core.model.Session;

/**
 * Interface for the policy that decides which versions have to be removed when a working document is removed. This
 * policy is called at the AbstractSession level.
 *
 * @author Florent Guillaume
 */
public interface VersionRemovalPolicy {

    /**
     * Removes the versions when a given working document is about to be removed.
     *
     * @param session the current session
     * @param doc the document that is about to be removed
     */
    void removeVersions(Session session, Document doc, CoreSession coreSession);

}
