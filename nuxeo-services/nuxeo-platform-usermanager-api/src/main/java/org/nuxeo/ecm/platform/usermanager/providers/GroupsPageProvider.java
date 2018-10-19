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
 *     Thomas Roger
 */

package org.nuxeo.ecm.platform.usermanager.providers;

import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Default Groups Provider
 *
 * @since 5.4.2
 */
public class GroupsPageProvider extends AbstractGroupsPageProvider<DocumentModel> {

    @Override
    public List<DocumentModel> getCurrentPage() {
        return computeCurrentPage();
    }

}
