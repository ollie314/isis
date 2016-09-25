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
package org.apache.isis.core.metamodel.specloader;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.isis.core.metamodel.spec.ObjectSpecId;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.metamodel.specloader.SpecificationCacheDefault;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class SpecificationCacheDefaultTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);

    @Mock
    private ObjectSpecification customerSpec;
    @Mock
    private ObjectSpecification orderSpec;
    
    private SpecificationCacheDefault specificationCache;
    
    @Before
    public void setUp() throws Exception {
        specificationCache = new SpecificationCacheDefault();

        context.checking(new Expectations() {{
            allowing(customerSpec).getCorrespondingClass();
            will(returnValue(Customer.class));

            allowing(orderSpec).getCorrespondingClass();
            will(returnValue(Order.class));
        }});
    }

    @After
    public void tearDown() throws Exception {
        specificationCache = null;
    }

    static class Customer {}
    static class Order {}
    
    @Test
    public void get_whenNotCached() {
        assertNull(specificationCache.get(Customer.class.getName()));
    }

    @Test
    public void get_whenCached() {
        final String customerClassName = Customer.class.getName();
        specificationCache.cache(customerClassName, customerSpec);
        
        final ObjectSpecification objectSpecification = specificationCache.get(customerClassName);
        
        assertSame(objectSpecification, customerSpec);
    }


    @Test
    public void allSpecs_whenCached() {
        specificationCache.cache(Customer.class.getName(), customerSpec);
        specificationCache.cache(Order.class.getName(), orderSpec);

        final Collection<ObjectSpecification> allSpecs = specificationCache.allSpecifications();
        
        assertThat(allSpecs.size(), is(2));
    }

    @Test(expected=IllegalStateException.class)
    public void getByObjectType_whenNotSet() {
        specificationCache.getByObjectType(ObjectSpecId.of("CUS"));
    }

    @Test
    public void getByObjectType_whenSet() {
        Map<ObjectSpecId, ObjectSpecification> specByObjectType = Maps.newHashMap();
        specByObjectType.put(ObjectSpecId.of("CUS"), customerSpec);
        
        specificationCache.setCacheBySpecId(specByObjectType);
        final ObjectSpecification objectSpec = specificationCache.getByObjectType(ObjectSpecId.of("CUS"));
        
        assertSame(objectSpec, customerSpec);
    }

}
