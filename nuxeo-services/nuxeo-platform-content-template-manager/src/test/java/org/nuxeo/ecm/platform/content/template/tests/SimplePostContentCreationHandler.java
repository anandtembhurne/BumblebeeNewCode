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
 *     Thomas Roger <troger@nuxeo.com>
 */

package org.nuxeo.ecm.platform.content.template.tests;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.content.template.service.PostContentCreationHandler;

/**
 * Simple {@link PostContentCreationHandler} creating only one document in the root.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public class SimplePostContentCreationHandler implements PostContentCreationHandler {

    public static final String DOC_TYPE = "Domain";

    public static final String DOC_NAME = "postContentCreationDoc";

    @Override
    public void execute(CoreSession session) {
        DocumentModel root = session.getRootDocument();
        DocumentModel doc = session.createDocumentModel(root.getPathAsString(), DOC_NAME, DOC_TYPE);
        session.createDocument(doc);
    }

}
