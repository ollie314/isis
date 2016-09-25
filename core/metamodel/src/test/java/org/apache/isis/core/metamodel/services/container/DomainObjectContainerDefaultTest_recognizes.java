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

package org.apache.isis.core.metamodel.services.container;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.metamodel.adapter.oid.RootOid;
import org.apache.isis.core.metamodel.adapter.version.ConcurrencyException;
import org.apache.isis.core.metamodel.spec.ObjectSpecId;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class DomainObjectContainerDefaultTest_recognizes {

    static class SomeRandomException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private Exception ex;
    
    private DomainObjectContainerDefault container;
    
    @Before
    public void setUp() throws Exception {
        container = new DomainObjectContainerDefault();
    }
    
    @Test
    public void whenConcurrencyException_is_recognized() throws Exception {
        ex = new ConcurrencyException("foo", RootOid.create(ObjectSpecId.of("CUS"), "123"));
        assertThat(container.recognize(ex), is(not(nullValue())));
    }

    @Test
    public void whenSomeRandomException_is_not_recognized() throws Exception {
        ex = new SomeRandomException();
        assertThat(container.recognize(ex), is(nullValue()));
    }
    
}
