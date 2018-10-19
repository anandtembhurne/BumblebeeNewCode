/*
 * (C) Copyright 2006-2009 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.annotations.repository.service;

import java.net.URI;

import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.annotations.repository.URNDocumentViewTranslator;
import org.nuxeo.ecm.platform.annotations.service.AnnotabilityManager;
import org.nuxeo.ecm.platform.url.api.DocumentView;
import org.nuxeo.runtime.api.Framework;

public class RepositoryAnnotabilityManager implements AnnotabilityManager {

    private final URNDocumentViewTranslator translator = new URNDocumentViewTranslator();

    private AnnotationsRepositoryService service;

    public RepositoryAnnotabilityManager() {
        service = Framework.getService(AnnotationsRepositoryService.class);
    }

    public boolean isAnnotable(URI uri) {
        DocumentView view = translator.getDocumentViewFromUri(uri);
        if (view == null) { // not a nuxeo uri
            return service.isAnnotable(null);
        }
        try (CoreSession session = CoreInstance.openCoreSession(null)) {
            DocumentModel model = session.getDocument(view.getDocumentLocation().getDocRef());
            return service.isAnnotable(model);
        }
    }
}
