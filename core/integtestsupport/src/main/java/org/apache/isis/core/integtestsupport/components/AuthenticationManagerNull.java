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

package org.apache.isis.core.integtestsupport.components;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.components.Noop;
import org.apache.isis.core.metamodel.deployment.DeploymentCategory;
import org.apache.isis.core.runtime.authentication.AuthenticationManager;
import org.apache.isis.core.runtime.authentication.AuthenticationRequest;
import org.apache.isis.core.runtime.authentication.RegistrationDetails;

public class AuthenticationManagerNull implements AuthenticationManager, Noop {

    @Override
    public void init(final DeploymentCategory deploymentCategory) {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public AuthenticationSession authenticate(final AuthenticationRequest request) {
        return null;
    }

    @Override
    public void closeSession(final AuthenticationSession session) {
    }

    @Override
    public boolean isSessionValid(final AuthenticationSession session) {
        return false;
    }

    public void testSetSession(final AuthenticationSession authenticationSession) {
    }

    @Override
    public boolean register(final RegistrationDetails registrationDetails) {
        return false;
    }

    @Override
    public boolean supportsRegistration(final Class<? extends RegistrationDetails> registrationDetailsClass) {
        return false;
    }

}
