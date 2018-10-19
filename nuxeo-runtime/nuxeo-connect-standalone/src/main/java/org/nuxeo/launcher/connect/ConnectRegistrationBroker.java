/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     akervern, jcarsique
 */

package org.nuxeo.launcher.connect;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.connect.NuxeoConnectClient;
import org.nuxeo.connect.connector.NuxeoClientInstanceType;
import org.nuxeo.connect.data.ConnectProject;
import org.nuxeo.connect.identity.LogicalInstanceIdentifier;
import org.nuxeo.connect.registration.ConnectRegistrationService;
import org.nuxeo.connect.registration.RegistrationException;
import org.nuxeo.connect.registration.RegistrationHelper;
import org.nuxeo.launcher.config.ConfigurationException;

/**
 * @author <a href="mailto:ak@nuxeo.com">Arnaud Kervern</a>
 * @since 8.3
 */
public class ConnectRegistrationBroker {

    public static final String REGISTRATION_EMAIL = "email";

    public static final String REGISTRATION_COMPANY = "company";

    public static final String REGISTRATION_PROJECT = "connectreg:projectName";

    public static final String REGISTRATION_DESCRIPTION = "description";

    public static final String REGISTRATION_PASSWORD = "password";

    public static final String REGISTRATION_PASSWORDND = "password_verif";

    public static final String REGISTRATION_TERMSNCONDITIONS = "termsAndConditions";

    public static final String REGISTRATION_COMPANY_REGEX = "^\\w+$";

    public static final String REGISTRATION_USERNAME_REGEX = "^\\w+$";

    public static final String REGISTRATION_PROJECT_REGEX = "^(?:[-\\w]+|)$";

    private static final Log log = LogFactory.getLog(ConnectRegistrationBroker.class);

    protected static ConnectRegistrationService registration() {
        return NuxeoConnectClient.getConnectRegistrationService();
    }

    public void registerTrial(Map<String, String> parameters) throws IOException, RegistrationException,
            ConfigurationException {
        try {
            registration().remoteTrialInstanceRegistration(parameters);
        } catch (LogicalInstanceIdentifier.InvalidCLID e) {
            log.debug(e, e);
            throw new ConfigurationException("Instance registration failed.", e);
        }
    }

    public void registerLocal(String strCLID, String description) throws IOException, ConfigurationException {
        try {
            registration().localRegisterInstance(strCLID, description);
        } catch (LogicalInstanceIdentifier.InvalidCLID e) {
            log.debug(e, e);
            throw new ConfigurationException("Instance registration failed.", e);
        }
    }

    public void registerRemote(String username, char[] password, String projectId, NuxeoClientInstanceType type,
            String description) throws IOException, ConfigurationException {
        String strCLID = RegistrationHelper.remoteRegisterInstance(username, new String(password), projectId, type,
                description);
        registerLocal(strCLID, description);
    }

    /**
     * Find a project by its symbolic name, ignoring case
     *
     * @param projectName project symbolic name
     * @param availableProjects projects in which to to look for {@code project}
     * @return the project or null if not found
     */
    public ConnectProject getProjectByName(final String projectName, List<ConnectProject> availableProjects) {
        Stream<ConnectProject> projectStream = availableProjects.stream();
        Optional<ConnectProject> pkg = projectStream.filter(
                availProject -> projectName.equalsIgnoreCase(availProject.getSymbolicName())).findFirst();
        if (!pkg.isPresent()) {
            return null;
        }
        return pkg.get();
    }

    public List<ConnectProject> getAvailableProjects(String username, char[] password) throws ConfigurationException {
        List<ConnectProject> studioProjects = registration().getAvailableProjectsForRegistration(username,
                new String(password));
        if (studioProjects.isEmpty()) {
            throw new ConfigurationException("Wrong login or password.");
        }
        return studioProjects;
    }
}
