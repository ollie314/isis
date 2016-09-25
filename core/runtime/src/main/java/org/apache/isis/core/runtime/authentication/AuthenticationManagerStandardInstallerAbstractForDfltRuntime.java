/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.runtime.authentication;

import org.apache.isis.applib.fixtures.LogonFixture;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.runtime.authentication.exploration.ExplorationAuthenticator;
import org.apache.isis.core.runtime.authentication.exploration.ExplorationSession;
import org.apache.isis.core.runtime.authentication.fixture.LogonFixtureAuthenticator;
import org.apache.isis.core.runtime.authentication.standard.AuthenticationManagerStandard;
import org.apache.isis.core.runtime.authentication.standard.AuthenticationManagerStandardInstallerAbstract;

public abstract class AuthenticationManagerStandardInstallerAbstractForDfltRuntime extends AuthenticationManagerStandardInstallerAbstract {


    public AuthenticationManagerStandardInstallerAbstractForDfltRuntime(
            final String name,
            final IsisConfigurationDefault isisConfiguration) {
        super(name, isisConfiguration);
    }

    /**
     * Returns an instance of {@link AuthenticationManagerStandard} that has no need to log in when running in
     * exploration mode.
     *
     * <p>
     * Specifically:
     * <ul>
     * <li> the {@link ExplorationAuthenticator} will always provide a special {@link ExplorationSession} if running
     *      in the exploration mode.
     * <li> the {@link LogonFixtureAuthenticator} will set up a session using the login provided by a
     *      {@link LogonFixture}, provided running in exploration or prototyping mode.
     * </ul>
     */
    @Override
    protected AuthenticationManagerStandard createAuthenticationManagerStandard() {
        final IsisConfiguration configuration = getConfiguration();

        final AuthenticationManagerStandard authenticationManager = new AuthenticationManagerStandard(configuration);

        // we add to start to ensure that these special case authenticators are always consulted first
        authenticationManager.addAuthenticatorToStart(new ExplorationAuthenticator(configuration));
        authenticationManager.addAuthenticatorToStart(new LogonFixtureAuthenticator(configuration));

        return authenticationManager;
    }

}
