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

package org.apache.isis.core.metamodel.adapter.oid;

import org.junit.Test;

import org.apache.isis.core.metamodel.spec.ObjectSpecId;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OidMarshallerTest_roundtripping {

    private OidMarshaller oidMarshaller = OidMarshaller.INSTANCE;
    
    @Test
    public void rootOid_withNoVersion() {
        RootOid oid = RootOid.create(ObjectSpecId.of("CUS"), "123");
        
        final String enString = oid.enString();
        final RootOid deString = RootOid.deString(enString);
        assertThat(deString, is(oid));
    }

    @Test
    public void rootOid_withVersion() {
        RootOid oid = RootOid.create(ObjectSpecId.of("CUS"), "123", 90807L);
        
        final String enString = oid.enString();
        final RootOid deString = RootOid.deString(enString);
        assertThat(deString, is(oid));
        assertThat(deString.getVersion(), is(oid.getVersion())); // assert separately because not part of equality check
    }

    

    @Test
    public void collectionOid_withNoVersion() {
        RootOid parentOid = RootOid.create(ObjectSpecId.of("CUS"), "123");
        ParentedCollectionOid oid = new ParentedCollectionOid(parentOid, "items");
        
        final String enString = oid.enString();
        final ParentedCollectionOid deString = ParentedCollectionOid.deString(enString);
        assertThat(deString, is(oid));
    }

    @Test
    public void collectionOid_withVersion() {
        RootOid parentOid = RootOid.create(ObjectSpecId.of("CUS"), "123", 90807L);
        ParentedCollectionOid oid = new ParentedCollectionOid(parentOid, "items");
        
        final String enString = oid.enString();
        final ParentedCollectionOid deString = ParentedCollectionOid.deString(enString);
        assertThat(deString, is(oid));
        assertThat(deString.getRootOid().getVersion(), is(parentOid.getVersion())); // assert separately because not part of equality check
    }

}
