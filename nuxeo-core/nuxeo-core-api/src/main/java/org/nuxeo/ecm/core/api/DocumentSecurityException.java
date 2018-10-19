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
 *     Thierry Delprat
 */
package org.nuxeo.ecm.core.api;

/**
 * Exception thrown when access to a document is denied.
 *
 * @since 5.6
 */
public class DocumentSecurityException extends NuxeoException {

    private static final long serialVersionUID = 1L;

    public DocumentSecurityException() {
        super();
    }

    public DocumentSecurityException(String message) {
        super(message);
    }

    public DocumentSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentSecurityException(Throwable cause) {
        super(cause);
    }

}
