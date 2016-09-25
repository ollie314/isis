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

package org.apache.isis.core.metamodel.specloader.specimpl;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.annotation.When;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.FacetedMethod;
import org.apache.isis.core.metamodel.facets.all.hide.HiddenFacet;
import org.apache.isis.core.metamodel.facets.members.hidden.HiddenFacetAbstract;
import org.apache.isis.core.metamodel.interactions.UsabilityContext;
import org.apache.isis.core.metamodel.interactions.VisibilityContext;
import org.apache.isis.core.metamodel.services.ServicesInjector;
import org.apache.isis.core.metamodel.services.persistsession.PersistenceSessionServiceInternal;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectAssociationAbstractTest_alwaysHidden {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private ObjectAssociationAbstract objectAssociation;
    private FacetedMethod facetedMethod;

    @Mock
    private ObjectSpecification mockObjectSpecification;

    @Mock
    private ServicesInjector mockServicesInjector;
    @Mock
    private SpecificationLoader mockSpecificationLoader;
    @Mock
    private PersistenceSessionServiceInternal mockPersistenceSessionServiceInternal;

    public static class Customer {
        public String getFirstName() {
            return null;
        }
    }

    @Before
    public void setup() {
        facetedMethod = FacetedMethod.createForProperty(Customer.class, "firstName");

        context.checking(new Expectations() {{
            allowing(mockServicesInjector).getSpecificationLoader();
            will(returnValue(mockSpecificationLoader));

            allowing(mockServicesInjector).getPersistenceSessionServiceInternal();
            will(returnValue(mockPersistenceSessionServiceInternal));
        }});

        objectAssociation = new ObjectAssociationAbstract(
                facetedMethod, FeatureType.PROPERTY, mockObjectSpecification,
                mockServicesInjector) {

            @Override
            public ObjectAdapter get(
                    final ObjectAdapter fromObject,
                    final InteractionInitiatedBy interactionInitiatedBy) {
                return null;
            }

            @Override
            public boolean isEmpty(final ObjectAdapter adapter, final InteractionInitiatedBy interactionInitiatedBy) {
                return false;
            }

            @Override
            public ObjectAdapter[] getChoices(
                    final ObjectAdapter object,
                    final InteractionInitiatedBy interactionInitiatedBy) {
                return null;
            }

            @Override
            public ObjectAdapter getDefault(final ObjectAdapter adapter) {
                return null;
            }

            @Override
            public void toDefault(final ObjectAdapter target) {
            }

            @Override
            public UsabilityContext<?> createUsableInteractionContext(
                    final ObjectAdapter target, final InteractionInitiatedBy interactionInitiatedBy,
                    final Where where) {
                return null;
            }

            @Override
            public VisibilityContext<?> createVisibleInteractionContext(
                    final ObjectAdapter targetObjectAdapter, final InteractionInitiatedBy interactionInitiatedBy,
                    final Where where) {
                return null;
            }

            @Override
            public boolean containsDoOpFacet(final Class<? extends Facet> facetType) {
                return false;
            }

            @Override
            public boolean hasAutoComplete() {
                return false;
            }

            @Override
            public ObjectAdapter[] getAutoComplete(
                    final ObjectAdapter object,
                    final String searchArg,
                    final InteractionInitiatedBy interactionInitiatedBy) {
                return null;
            }
            @Override
            public int getAutoCompleteMinLength() {
                return 0;
            }
        };
    }


    @Test
    public void whenNone() throws Exception {

        // given (none)

        // when, then
        assertFalse(objectAssociation.isAlwaysHidden());
    }

    @Test
    public void whenNoop() throws Exception {

        // given
        addHiddenFacet(When.ALWAYS, Where.EVERYWHERE, facetedMethod, true);

        // when, then
        assertFalse(objectAssociation.isAlwaysHidden());
    }

    @Test
    public void whenNotAlwaysEverywhere() throws Exception {

        // given
        addHiddenFacet(When.ONCE_PERSISTED, Where.EVERYWHERE, facetedMethod, false);

        // when, then
        assertFalse(objectAssociation.isAlwaysHidden());
    }

    @Test
    public void whenAlwaysNotEverywhere() throws Exception {

        // given
        addHiddenFacet(When.ALWAYS, Where.OBJECT_FORMS, facetedMethod, false);

        // when, then
        assertFalse(objectAssociation.isAlwaysHidden());
    }

    @Test
    public void whenAlwaysEverywhere() throws Exception {

        // given
        addHiddenFacet(When.ALWAYS, Where.EVERYWHERE, facetedMethod, false);

        // when, then
        assertTrue(objectAssociation.isAlwaysHidden());
    }

    @Test
    public void whenAlwaysAnywhere() throws Exception {

        // given
        addHiddenFacet(When.ALWAYS, Where.ANYWHERE, facetedMethod, false);

        // when, then
        assertTrue(objectAssociation.isAlwaysHidden());
    }

    private static void addHiddenFacet(final When when, final Where where, final FacetedMethod holder, final boolean noop) {
        HiddenFacet facet = new HiddenFacetAbstract(HiddenFacet.class, when, where, holder) {
            @Override
            protected String hiddenReason(final ObjectAdapter target, final Where whereContext) {
                return null;
            }

            @Override
            public boolean isNoop() {
                return noop;
            }
        };
        FacetUtil.addFacet(facet);
    }
}
