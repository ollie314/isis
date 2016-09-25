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

package org.apache.isis.core.metamodel.facets.object.defaults;

import org.apache.isis.applib.adapters.DefaultsProvider;
import org.apache.isis.applib.adapters.EncoderDecoder;
import org.apache.isis.core.commons.lang.ClassExtensions;
import org.apache.isis.core.metamodel.facetapi.FacetAbstract;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.services.ServicesInjector;

public abstract class DefaultedFacetAbstract extends FacetAbstract implements DefaultedFacet {

    private final Class<?> defaultsProviderClass;

    // to delegate to
    private final DefaultedFacetUsingDefaultsProvider defaultedFacetUsingDefaultsProvider;

    private final ServicesInjector dependencyInjector;

    public DefaultedFacetAbstract(final String candidateEncoderDecoderName, final Class<?> candidateEncoderDecoderClass, final FacetHolder holder, final ServicesInjector dependencyInjector) {
        super(DefaultedFacet.class, holder, Derivation.NOT_DERIVED);

        this.defaultsProviderClass = DefaultsProviderUtil.defaultsProviderOrNull(candidateEncoderDecoderClass, candidateEncoderDecoderName);
        this.dependencyInjector = dependencyInjector;
        if (isValid()) {
            final DefaultsProvider<?> defaultsProvider = (DefaultsProvider<?>) ClassExtensions.newInstance(defaultsProviderClass, FacetHolder.class, holder);
            this.defaultedFacetUsingDefaultsProvider = new DefaultedFacetUsingDefaultsProvider(defaultsProvider, holder, getDependencyInjector());
        } else {
            this.defaultedFacetUsingDefaultsProvider = null;
        }
    }

    /**
     * Discover whether either of the candidate defaults provider name or class
     * is valid.
     */
    public boolean isValid() {
        return defaultsProviderClass != null;
    }

    /**
     * Guaranteed to implement the {@link EncoderDecoder} class, thanks to
     * generics in the applib.
     */
    public Class<?> getDefaultsProviderClass() {
        return defaultsProviderClass;
    }

    @Override
    public Object getDefault() {
        return defaultedFacetUsingDefaultsProvider.getDefault();
    }

    @Override
    protected String toStringValues() {
        return defaultsProviderClass.getName();
    }

    // //////////////////////////////////////////////////////
    // Dependencies (from constructor)
    // //////////////////////////////////////////////////////

    private ServicesInjector getDependencyInjector() {
        return dependencyInjector;
    }

}
