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

package org.apache.isis.core.runtime.authentication.standard;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.metamodel.deployment.DeploymentCategory;
import org.apache.isis.core.runtime.authentication.AuthenticationRequest;

public abstract class AuthenticatorAbstract implements Authenticator {

    //region > constructor, fields
    private final IsisConfiguration configuration;
    private DeploymentCategory deploymentCategory;

    public AuthenticatorAbstract(final IsisConfiguration configuration) {
        this.configuration = configuration;
    }

    //endregion

    //region > init, shutdown

    @Override
    public void init(final DeploymentCategory deploymentCategory) {
        // does nothing.
        this.deploymentCategory = deploymentCategory;
    }

    @Override
    public void shutdown() {
        // does nothing.
    }

    @Override
    public DeploymentCategory getDeploymentCategory() {
        return deploymentCategory;
    }

    //endregion

    //region > API

    /**
     * Default implementation returns a {@link SimpleSession}; can be overridden
     * if required.
     */
    @Override
    public AuthenticationSession authenticate(final AuthenticationRequest request, final String code) {
        if (!isValid(request)) {
            return null;
        }
        return new SimpleSession(request.getName(), request.getRoles(), code);
    }


    /**
     * Whether this {@link Authenticator} is valid in the running context (and
     * optionally with respect to the provided {@link AuthenticationRequest}).
     *
     * <p>
     * For example, the <tt>ExplorationAuthenticator</tt> (in the default
     * runtime) is only available for authentication if running in
     * <i>exploration mode</i>.
     *
     * <p>
     * TODO: [ISIS-292] should change visibility to <tt>protected</tt> when remove from the API.
     */
    protected abstract boolean isValid(AuthenticationRequest request);

    @Override
    public void logout(final AuthenticationSession session) {
        // no-op
    }


    //endregion

    //region > Injected (via constructor)

    public IsisConfiguration getConfiguration() {
        return configuration;
    }

    //endregion

}
