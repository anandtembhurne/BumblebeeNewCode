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
 *     bstefanescu
 */
package org.nuxeo.ecm.automation.core.operations.document;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@Operation(id = RemoveDocumentBlob.ID, category = Constants.CAT_BLOB, label = "Remove File", description = "Remove the file attached to the input document as specified by the 'xpath' parameter. If the 'xpath' point to a blob list then the list will be cleared. If the file to remove is part of a list it will be removed from the list otherwise the 'xpath' should point to a blob property that will be removed. If the save parameter is set the document modification will be automatically saved. Return the document.", aliases = { "Blob.Remove" })
public class RemoveDocumentBlob {

    public static final String ID = "Blob.RemoveFromDocument";

    @Context
    protected CoreSession session;

    @Param(name = "xpath", required = false, values = "file:content")
    protected String xpath = "file:content";

    @Param(name = "save", required = false, values = "true")
    protected boolean save = true;

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) {
        DocumentHelper.removeProperty(doc, xpath);
        if (save) {
            doc = session.saveDocument(doc);
        }
        return doc;
    }

}
