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
package org.apache.isis.viewer.restfulobjects.applib.user;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.ClientResponseType;

import org.apache.isis.viewer.restfulobjects.applib.RestfulMediaType;

@Path("/user")
public interface UserResource {

    @GET
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_USER })
    @ClientResponseType(entityType = String.class)
    public Response user();

    @DELETE
    public Response deleteUserNotAllowed();

    @PUT
    public Response putUserNotAllowed();

    @POST
    public Response postUserNotAllowed();

    /**
     * Not part of the Restful Objects spec.
     */
    @GET
    @Path("/logout")
    @Produces({ MediaType.APPLICATION_JSON, RestfulMediaType.APPLICATION_JSON_HOME_PAGE })
    @ClientResponseType(entityType = String.class)
    public Response logout();

}