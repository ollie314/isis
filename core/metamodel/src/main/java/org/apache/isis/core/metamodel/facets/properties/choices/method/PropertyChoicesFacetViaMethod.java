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

package org.apache.isis.core.metamodel.facets.properties.choices.method;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.apache.isis.core.commons.lang.ObjectExtensions;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.ImperativeFacet;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.metamodel.facets.CollectionUtils;
import org.apache.isis.core.metamodel.facets.properties.choices.PropertyChoicesFacetAbstract;

public class PropertyChoicesFacetViaMethod extends PropertyChoicesFacetAbstract implements ImperativeFacet {

    private final Method method;
    private final Class<?> choicesClass;

    private final AdapterManager adapterManager;

    public PropertyChoicesFacetViaMethod(final Method method, final Class<?> choicesClass, final FacetHolder holder, final SpecificationLoader specificationLookup, final AdapterManager adapterManager) {
        super(holder, specificationLookup);
        this.method = method;
        this.choicesClass = choicesClass;
        this.adapterManager = adapterManager;
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
        return Intent.CHOICES_OR_AUTOCOMPLETE;
    }

    @Override
    public Object[] getChoices(
            final ObjectAdapter owningAdapter,
            final SpecificationLoader specificationLookup,
            final InteractionInitiatedBy interactionInitiatedBy) {
        final Object options = ObjectAdapter.InvokeUtils.invoke(method, owningAdapter);
        if (options == null) {
            return null;
        }
        if (options.getClass().isArray()) {
            return ObjectExtensions.asArray(options);
        }
        final ObjectSpecification specification = specificationLookup.loadSpecification(choicesClass);
        return CollectionUtils.getCollectionAsObjectArray(options, specification, getAdapterManager());
    }

    @Override
    protected String toStringValues() {
        return "method=" + method + ",class=" + choicesClass;
    }

    // ////////////////////////////////////////////
    // Dependencies
    // ////////////////////////////////////////////

    protected AdapterManager getAdapterManager() {
        return adapterManager;
    }

}
