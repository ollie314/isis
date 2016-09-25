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

package org.apache.isis.core.webapp.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.runtime.authentication.AuthenticationManager;
import org.apache.isis.core.webapp.IsisSessionFilter;

/**
 * Decouples the {@link IsisSessionFilter} from the mechanism of obtaining the
 * {@link AuthenticationSession}.
 */
public interface AuthenticationSessionStrategy {

    /**
     * Returns a
     * {@link AuthenticationManager#isSessionValid(AuthenticationSession)
     * still-valid} {@link AuthenticationSession}.
     */
    AuthenticationSession lookupValid(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse);

    void bind(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final AuthenticationSession authSession);

    void invalidate(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse);
}
