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

package org.apache.isis.core.metamodel.facets.collections.accessor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.core.commons.authentication.AuthenticationSessionProvider;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.deployment.DeploymentCategory;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.CollectionUtils;
import org.apache.isis.core.metamodel.facets.ImperativeFacet;
import org.apache.isis.core.metamodel.facets.propcoll.accessor.PropertyOrCollectionAccessorFacetAbstract;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;

public class CollectionAccessorFacetViaAccessor
        extends PropertyOrCollectionAccessorFacetAbstract
        implements ImperativeFacet {

    private final Method method;

    public CollectionAccessorFacetViaAccessor(
            final Method method,
            final FacetHolder holder,
            final DeploymentCategory deploymentCategory,
            final IsisConfiguration isisConfiguration,
            final SpecificationLoader specificationLoader,
            final AuthenticationSessionProvider authenticationSessionProvider,
            final AdapterManager adapterManager) {
        super(holder, deploymentCategory, isisConfiguration, specificationLoader, authenticationSessionProvider,
                adapterManager
        );
        this.method = method;
    }

    /**
     * Returns a singleton list of the {@link Method} provided in the
     * constructor.
     */
    @Override
    public List<Method> getMethods() {
        return Collections.singletonList(method);
    }

    @Override
    public Intent getIntent(final Method method) {
        return Intent.ACCESSOR;
    }

    @Override
    public Object getProperty(
            final ObjectAdapter owningAdapter,
            final InteractionInitiatedBy interactionInitiatedBy) {
        final Object collectionOrArray = ObjectAdapter.InvokeUtils.invoke(method, owningAdapter);

        final ObjectAdapter collectionAdapter = getAdapterManager().adapterFor(collectionOrArray);

        boolean filterForVisibility = getConfiguration().getBoolean("isis.reflector.facet.filterVisibility", true);
        if(filterForVisibility) {
            final List<ObjectAdapter> visibleAdapters =
                    ObjectAdapter.Util.visibleAdapters(
                            collectionAdapter,
                            interactionInitiatedBy);
            final Object visibleObjects =
                    CollectionUtils.copyOf(
                            Lists.transform(visibleAdapters, ObjectAdapter.Functions.getObject()),
                            method.getReturnType());
            if (visibleObjects != null) {
                return visibleObjects;
            }
            // would be null if unable to take a copy (unrecognized return type)
            // fallback to returning the original adapter, without filtering for visibility
        }

        // either no filtering, or was unable to filter (unable to take copy due to unrecognized type)
        return collectionOrArray;
    }

    @Override
    protected String toStringValues() {
        return "method=" + method;
    }


}
