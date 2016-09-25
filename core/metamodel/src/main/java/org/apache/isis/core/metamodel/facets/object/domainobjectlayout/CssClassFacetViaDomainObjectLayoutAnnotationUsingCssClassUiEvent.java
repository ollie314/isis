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

package org.apache.isis.core.metamodel.facets.object.domainobjectlayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.NonRecoverableException;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.services.eventbus.CssClassUiEvent;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetAbstract;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.members.cssclass.CssClassFacet;
import org.apache.isis.core.metamodel.facets.members.cssclass.CssClassFacetAbstract;
import org.apache.isis.core.metamodel.services.ServicesInjector;
import org.apache.isis.core.metamodel.util.EventUtil;

public class CssClassFacetViaDomainObjectLayoutAnnotationUsingCssClassUiEvent extends FacetAbstract implements
        CssClassFacet {

    private static final Logger LOG = LoggerFactory.getLogger(CssClassFacetViaDomainObjectLayoutAnnotationUsingCssClassUiEvent.class);

    public static Facet create(
            final DomainObjectLayout domainObjectLayout,
            final ServicesInjector servicesInjector,
            final IsisConfiguration configuration, final FacetHolder facetHolder) {
        if(domainObjectLayout == null) {
            return null;
        }
        final Class<? extends CssClassUiEvent<?>> cssClassUiEventClass = domainObjectLayout.cssClassUiEvent();

        if(!EventUtil.eventTypeIsPostable(
                cssClassUiEventClass,
                CssClassUiEvent.Noop.class,
                CssClassUiEvent.Default.class,
                "isis.reflector.facet.domainObjectLayoutAnnotation.cssClassUiEvent.postForDefault",
                configuration)) {
            return null;
        }

        final EventBusService eventBusService = servicesInjector.lookupService(EventBusService.class);

        return new CssClassFacetViaDomainObjectLayoutAnnotationUsingCssClassUiEvent(
                cssClassUiEventClass, eventBusService, facetHolder);
    }

    private final Class<? extends CssClassUiEvent<?>> cssClassUiEventClass;
    private final EventBusService eventBusService;

    public CssClassFacetViaDomainObjectLayoutAnnotationUsingCssClassUiEvent(
            final Class<? extends CssClassUiEvent<?>> cssClassUiEventClass,
            final EventBusService eventBusService,
            final FacetHolder holder) {
        super(CssClassFacetAbstract.type(), holder, Derivation.NOT_DERIVED);
        this.cssClassUiEventClass = cssClassUiEventClass;
        this.eventBusService = eventBusService;
    }

    @Override
    public String cssClass(final ObjectAdapter objectAdapter) {

        final CssClassUiEvent<Object> cssClassUiEvent = newCssClassUiEvent(objectAdapter);

        eventBusService.post(cssClassUiEvent);

        final String cssClass = cssClassUiEvent.getCssClass();
        return cssClass; // could be null
    }

    private CssClassUiEvent<Object> newCssClassUiEvent(final ObjectAdapter owningAdapter) {
        final Object domainObject = owningAdapter.getObject();
        return newCssClassUiEvent(domainObject);
    }

    private CssClassUiEvent<Object> newCssClassUiEvent(final Object domainObject) {
        try {
            final CssClassUiEvent<Object> cssClassUiEvent = (CssClassUiEvent<Object>) cssClassUiEventClass.newInstance();
            cssClassUiEvent.setSource(domainObject);
            return cssClassUiEvent;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new NonRecoverableException(ex);
        }
    }

}
