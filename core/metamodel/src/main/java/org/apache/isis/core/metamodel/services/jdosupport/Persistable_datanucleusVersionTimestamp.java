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
package org.apache.isis.core.metamodel.services.jdosupport;

import javax.jdo.JDOHelper;

import org.datanucleus.enhancement.Persistable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

@Mixin
public class Persistable_datanucleusVersionTimestamp {

    private final Persistable persistable;

    public Persistable_datanucleusVersionTimestamp(final Persistable persistable) {
        this.persistable = persistable;
    }

    public static class ActionDomainEvent extends org.apache.isis.applib.IsisApplibModule.ActionDomainEvent<Persistable_datanucleusVersionTimestamp> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @PropertyLayout(
            named = "Version",
            hidden = Where.ALL_TABLES
    )
    @MemberOrder(name = "Metadata", sequence = "800.2")
    public java.sql.Timestamp $$() {
        final Object version = JDOHelper.getVersion(persistable);
        return version != null && version instanceof java.sql.Timestamp ? (java.sql.Timestamp) version : null;
    }

    public boolean hide$$() {
        return $$() == null;
    }

}
