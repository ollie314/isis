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

import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;

import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionFacet;
import org.apache.isis.core.metamodel.facets.actcoll.typeof.TypeOfFacet;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;

public class SpecificationLoaderTest_collection extends SpecificationLoaderTestAbstract {

    @Override
    protected ObjectSpecification loadSpecification(final SpecificationLoader reflector) {
        return reflector.loadSpecification(Vector.class);
    }

    @Test
    public void testType() throws Exception {
        Assert.assertTrue(specification.isParentedOrFreeCollection());
    }

    @Test
    public void testName() throws Exception {
        Assert.assertEquals(Vector.class.getName(), specification.getFullIdentifier());
    }

    @Test
    @Override
    public void testCollectionFacet() throws Exception {
        final Facet facet = specification.getFacet(CollectionFacet.class);
        Assert.assertNotNull(facet);
    }

    @Test
    @Override
    public void testTypeOfFacet() throws Exception {
        final TypeOfFacet facet = specification.getFacet(TypeOfFacet.class);
        Assert.assertNotNull(facet);
        Assert.assertEquals(Object.class, facet.value());
    }

}
