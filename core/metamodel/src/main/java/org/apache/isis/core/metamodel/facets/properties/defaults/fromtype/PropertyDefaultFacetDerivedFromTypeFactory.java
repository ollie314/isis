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

package org.apache.isis.core.metamodel.facets.properties.defaults.fromtype;

import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.core.metamodel.facets.object.defaults.DefaultedFacet;
import org.apache.isis.core.metamodel.facets.properties.defaults.PropertyDefaultFacet;
import org.apache.isis.core.metamodel.services.ServicesInjector;
import org.apache.isis.core.metamodel.services.persistsession.PersistenceSessionServiceInternal;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;

public class PropertyDefaultFacetDerivedFromTypeFactory extends FacetFactoryAbstract {


    public PropertyDefaultFacetDerivedFromTypeFactory() {
        super(FeatureType.PROPERTIES_ONLY);
    }

    /**
     * If there is a {@link DefaultedFacet} on the properties return type, then
     * installs a {@link PropertyDefaultFacet} for the property with the same
     * default.
     */
    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        // don't overwrite any defaults that might already picked up
        final PropertyDefaultFacet existingDefaultFacet = processMethodContext.getFacetHolder().getFacet(PropertyDefaultFacet.class);
        if (existingDefaultFacet != null && !existingDefaultFacet.isNoop()) {
            return;
        }

        // try to infer defaults from the underlying return type
        final Class<?> returnType = processMethodContext.getMethod().getReturnType();
        final DefaultedFacet returnTypeDefaultedFacet = getDefaultedFacet(returnType);
        if (returnTypeDefaultedFacet != null) {
            final PropertyDefaultFacetDerivedFromDefaultedFacet propertyFacet = new PropertyDefaultFacetDerivedFromDefaultedFacet(returnTypeDefaultedFacet, processMethodContext.getFacetHolder(), adapterManager);
            FacetUtil.addFacet(propertyFacet);
        }
    }

    private DefaultedFacet getDefaultedFacet(final Class<?> paramType) {
        final ObjectSpecification paramTypeSpec = getSpecificationLoader().loadSpecification(paramType);
        return paramTypeSpec.getFacet(DefaultedFacet.class);
    }

    // ////////////////////////////////////////////////////////////////////
    // Injected
    // ////////////////////////////////////////////////////////////////////


    @Override
    public void setServicesInjector(final ServicesInjector servicesInjector) {
        super.setServicesInjector(servicesInjector);
        adapterManager = servicesInjector.getPersistenceSessionServiceInternal();
    }

    PersistenceSessionServiceInternal adapterManager;

}
