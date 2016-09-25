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

package org.apache.isis.core.commons.authentication;

import java.io.Serializable;
import java.util.List;

import org.apache.isis.applib.security.UserMemento;
import org.apache.isis.core.commons.encoding.Encodable;

/**
 * The representation within the system of an authenticated user.
 */
public interface AuthenticationSession extends Encodable, Serializable {

    /**
     * The name of the authenticated user; for display purposes only.
     */
    String getUserName();

    boolean hasUserNameOf(String userName);

    /**
     * The roles this user belongs to
     */
    List<String> getRoles();

    /**
     * A unique code given to this session during authentication.
     * 
     * <p>
     * This can be used to confirm that this session has been properly created
     * and the user has been authenticated. It should return an empty string (
     * <tt>""</tt>) if this is unauthenticated user (i.e., as created within an
     * exploration system).
     */
    String getValidationCode();

    /**
     * For viewers (in particular) to store additional attributes, analogous to
     * an <tt>HttpSession</tt>.
     */
    Object getAttribute(String attributeName);

    /**
     * @see #getAttribute(String)
     */
    void setAttribute(String attributeName, Object attribute);

    /**
     * The {@link MessageBroker} that holds messages for this user.
     */
    MessageBroker getMessageBroker();

    UserMemento createUserMemento();
}
