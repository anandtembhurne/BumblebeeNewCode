/*
 * (C) Copyright 2006-2008 Nuxeo SA (http://nuxeo.com/) and others.
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

package org.nuxeo.ecm.webengine.ui.tree;

public class FakeContentProvider implements ContentProvider {

    private static final long serialVersionUID = -5447072937714133528L;

    public Object[] getChildren(Object obj) {
        return null;
    }

    public Object[] getElements(Object input) {
        return null;
    }

    public String[] getFacets(Object object) {
        return null;
    }

    public String getLabel(Object obj) {
        return null;
    }

    public String getName(Object obj) {
        return null;
    }

    public boolean isContainer(Object obj) {
        return false;
    }

}
