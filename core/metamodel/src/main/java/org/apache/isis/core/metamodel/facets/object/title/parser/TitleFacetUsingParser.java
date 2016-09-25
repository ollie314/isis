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

package org.apache.isis.core.metamodel.facets.object.title.parser;

import org.apache.isis.applib.adapters.Parser;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facetapi.FacetAbstract;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.object.title.TitleFacet;
import org.apache.isis.core.metamodel.services.ServicesInjector;

public class TitleFacetUsingParser extends FacetAbstract implements TitleFacet {

    private final Parser parser;
    private final ServicesInjector dependencyInjector;

    public TitleFacetUsingParser(final Parser parser, final FacetHolder holder, final ServicesInjector dependencyInjector) {
        super(TitleFacet.class, holder, Derivation.NOT_DERIVED);
        this.parser = parser;
        this.dependencyInjector = dependencyInjector;
    }

    @Override
    protected String toStringValues() {
        getServicesInjector().injectServicesInto(parser);
        return parser.toString();
    }

    @Override
    public String title(final ObjectAdapter adapter) {
        if (adapter == null) {
            return null;
        }
        final Object object = adapter.getObject();
        if (object == null) {
            return null;
        }
        getServicesInjector().injectServicesInto(parser);
        return parser.displayTitleOf(object);
    }

    @Override
    public String title(ObjectAdapter contextAdapter, ObjectAdapter targetAdapter) {
        return title(targetAdapter);
    }

    /**
     * not API
     */
    public String title(final ObjectAdapter adapter, final String usingMask) {
        if (adapter == null) {
            return null;
        }
        final Object object = adapter.getObject();
        if (object == null) {
            return null;
        }
        getServicesInjector().injectServicesInto(parser);
        return parser.displayTitleOf(object, usingMask);
    }


    // //////////////////////////////////////////////////////
    // Dependencies (from constructor)
    // //////////////////////////////////////////////////////

    /**
     * @return the dependencyInjector
     */
    public ServicesInjector getServicesInjector() {
        return dependencyInjector;
    }


}
