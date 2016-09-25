/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.isis.objectstore.jdo.datanucleus.service.support;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jdo.listener.InstanceLifecycleEvent;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.timestamp.HoldsUpdatedAt;
import org.apache.isis.applib.services.timestamp.HoldsUpdatedBy;
import org.apache.isis.applib.services.user.UserService;

@RequestScoped
@DomainService(
        nature = NatureOfService.DOMAIN
)
public class TimestampService implements
        javax.jdo.listener.StoreLifecycleListener {

    @PostConstruct
    public void open() {
        isisJdoSupport.getJdoPersistenceManager().addInstanceLifecycleListener(this, null);
    }

    @PreDestroy
    public void close() {
        isisJdoSupport.getJdoPersistenceManager().removeInstanceLifecycleListener(this);
    }

    @Programmatic
    public void preStore (InstanceLifecycleEvent event) {

        final Object pi = event.getPersistentInstance();

        if(pi instanceof org.datanucleus.enhancement.Persistable) {

            if(pi instanceof HoldsUpdatedBy) {
                ((HoldsUpdatedBy)pi).setUpdatedBy(userService.getUser().getName());
            }
            if(pi instanceof HoldsUpdatedAt) {
                ((HoldsUpdatedAt)pi).setUpdatedAt(clockService.nowAsJavaSqlTimestamp());
            }
        }
    }

    @Programmatic
    public void postStore (InstanceLifecycleEvent event) {
        // no-op
    }

    @javax.inject.Inject
    UserService userService;

    @javax.inject.Inject
    ClockService clockService;

    @javax.inject.Inject
    IsisJdoSupport isisJdoSupport;
}