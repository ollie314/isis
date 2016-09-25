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

package org.apache.isis.core.metamodel.facets.actions.exploration;

import java.lang.reflect.Method;

import org.apache.isis.applib.annotation.Exploration;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facets.AbstractFacetFactoryTest;
import org.apache.isis.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import org.apache.isis.core.metamodel.facets.actions.exploration.annotation.PrototypeFacetForExplorationAnnotationFactory;
import org.apache.isis.core.metamodel.facets.actions.prototype.PrototypeFacet;
import org.apache.isis.core.metamodel.facets.actions.prototype.PrototypeFacetAbstract;

public class PrototypeFacetForPrototypeFacetForExplorationAnnotationFactoryTest extends AbstractFacetFactoryTest {

    private PrototypeFacetForExplorationAnnotationFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new PrototypeFacetForExplorationAnnotationFactory();
        facetFactory.setServicesInjector(stubServicesInjector);
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testExplorationAnnotationPickedUp() {
        class Customer {
            @SuppressWarnings("unused")
            @Exploration
            public void someAction() {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, null, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PrototypeFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PrototypeFacetAbstract);

        assertNoMethodsRemoved();
    }

}
