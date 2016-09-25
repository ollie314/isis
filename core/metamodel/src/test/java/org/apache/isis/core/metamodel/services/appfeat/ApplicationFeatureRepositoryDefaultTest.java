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
package org.apache.isis.core.metamodel.services.appfeat;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.When;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facets.all.hide.HiddenFacet;
import org.apache.isis.core.metamodel.facets.members.hidden.HiddenFacetAbstract;
import org.apache.isis.core.metamodel.facets.objectvalue.maxlen.MaxLengthFacet;
import org.apache.isis.core.metamodel.facets.objectvalue.maxlen.MaxLengthFacetAbstract;
import org.apache.isis.core.metamodel.facets.objectvalue.typicallen.TypicalLengthFacet;
import org.apache.isis.core.metamodel.facets.properties.typicallen.annotation.TypicalLengthFacetOnPropertyAnnotation;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.Contributed;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.isis.core.metamodel.spec.feature.OneToOneAssociation;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ApplicationFeatureRepositoryDefaultTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    ObjectSpecification mockSpec;
    @Mock
    OneToOneAssociation mockProp;
    @Mock
    OneToManyAssociation mockColl;
    @Mock
    ObjectAction mockAct;
    ObjectAction mockActThatIsHidden;

    @Mock
    DomainObjectContainer mockContainer;

    @Mock
    FactoryService mockFactoryService;

    @Mock
    ServiceRegistry2 mockServiceRegistry;

    @Mock
    SpecificationLoader mockSpecificationLoader;

    ApplicationFeatureRepositoryDefault applicationFeatureRepository;

    @Before
    public void setUp() throws Exception {
        applicationFeatureRepository = new ApplicationFeatureRepositoryDefault();
        applicationFeatureRepository.container = mockContainer;
        applicationFeatureRepository.serviceRegistry = mockServiceRegistry;
        applicationFeatureRepository.specificationLoader = mockSpecificationLoader;

        final ApplicationFeatureFactory applicationFeatureFactory = new ApplicationFeatureFactory();
        applicationFeatureRepository.applicationFeatureFactory = applicationFeatureFactory;
        applicationFeatureFactory.factoryService = mockFactoryService;


        mockActThatIsHidden = context.mock(ObjectAction.class, "mockActThatIsHidden");
    }

    public static class Load extends ApplicationFeatureRepositoryDefaultTest {

        public static class Bar {}

        @Ignore // considering deleting this test, it's too long and too fragile.  integ tests ought to suffice.
        @Test
        public void happyCase() throws Exception {

            final List<ObjectAssociation> properties = Lists.<ObjectAssociation>newArrayList(mockProp);
            final List<ObjectAssociation> collections = Lists.<ObjectAssociation>newArrayList(mockColl);
            final List<ObjectAction> actions = Lists.newArrayList(mockAct, mockActThatIsHidden);

            context.checking(new Expectations() {{
                allowing(mockSpec).isAbstract();
                will(returnValue(false));

                allowing(mockSpec).getFullIdentifier();
                will(returnValue(Bar.class.getName()));

                allowing(mockSpec).getAssociations(with(Contributed.INCLUDED), with(ObjectAssociation.Filters.PROPERTIES));
                will(returnValue(properties));

                allowing(mockSpec).getAssociations(with(Contributed.INCLUDED), with(ObjectAssociation.Filters.COLLECTIONS));
                will(returnValue(collections));

                allowing(mockSpec).getFacet(HiddenFacet.class);
                will(returnValue(new HiddenFacetAbstract(When.ALWAYS, Where.EVERYWHERE, mockSpec) {
                    @Override
                    protected String hiddenReason(final ObjectAdapter target, final Where whereContext) {
                        return null;
                    }
                }));

                allowing(mockSpec).getCorrespondingClass();
                will(returnValue(Bar.class));

                allowing(mockSpec).getObjectActions(with(Contributed.INCLUDED));
                will(returnValue(actions));

                allowing(mockProp).getId();
                will(returnValue("someProperty"));

                allowing(mockProp).getFacet(MaxLengthFacet.class);
                will(returnValue(new MaxLengthFacetAbstract(30, mockProp){}));

                allowing(mockProp).getFacet(TypicalLengthFacet.class);
                will(returnValue(new TypicalLengthFacetOnPropertyAnnotation(15, mockProp)));

                allowing(mockProp).isAlwaysHidden();
                will(returnValue(false));

                allowing(mockColl).getId();
                will(returnValue("someCollection"));

                allowing(mockColl).isAlwaysHidden();
                will(returnValue(false));

                allowing(mockAct).getId();
                will(returnValue("someAction"));

                allowing(mockAct).isAlwaysHidden();
                will(returnValue(false));

                allowing(mockAct).getSemantics();
                will(returnValue(ActionSemantics.Of.SAFE));

                allowing(mockActThatIsHidden).getId();
                will(returnValue("someActionThatIsHidden"));

                allowing(mockActThatIsHidden).isAlwaysHidden();
                will(returnValue(true));

                allowing(mockActThatIsHidden).getSemantics();
                will(returnValue(ActionSemantics.Of.SAFE));

                allowing(mockServiceRegistry).getRegisteredServices();
                will(returnValue(Lists.newArrayList()));
            }});

            // then
            final Sequence sequence = context.sequence("loadSequence");
            context.checking(new Expectations() {{
                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newClass(Bar.class.getName()))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newMember(Bar.class.getName(), "someProperty"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newMember(Bar.class.getName(), "someCollection"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newMember(Bar.class.getName(), "someAction"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom.feature"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org.isisaddons.module.security"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org.isisaddons.module"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org.isisaddons"))));

                oneOf(mockContainer).newTransientInstance(ApplicationFeature.class);
                inSequence(sequence);
                will(returnValue(new ApplicationFeature(ApplicationFeatureId.newPackage("org"))));
            }});

            // when
            applicationFeatureRepository.createApplicationFeaturesFor(mockSpec);

            // then
            final ApplicationFeature orgPkg = applicationFeatureRepository.findPackage(ApplicationFeatureId.newPackage("org"));
            assertThat(orgPkg, is(notNullValue()));
            final ApplicationFeature orgIsisaddonsPkg = applicationFeatureRepository.findPackage(ApplicationFeatureId.newPackage("org.isisaddons"));
            assertThat(orgPkg, is(notNullValue()));
            final ApplicationFeature featurePkg = applicationFeatureRepository.findPackage(ApplicationFeatureId.newPackage("org.isisaddons.module.security.dom.feature"));
            assertThat(orgPkg, is(notNullValue()));
            assertThat(orgPkg.getContents(), contains(orgIsisaddonsPkg.getFeatureId()));
            assertThat(featurePkg.getContents(), contains(ApplicationFeatureId.newClass(Bar.class.getName())));

            // then
            final ApplicationFeature barClass = applicationFeatureRepository.findClass(ApplicationFeatureId.newClass(Bar.class.getName()));
            assertThat(barClass, is(Matchers.notNullValue()));

            // then the mockActThatIsHidden is not listed.
            assertThat(barClass.getProperties().size(), is(1));
            assertThat(barClass.getCollections().size(), is(1));
            assertThat(barClass.getActions().size(), is(1));
            assertThat(barClass.getProperties(),
                    containsInAnyOrder(
                            ApplicationFeatureId.newMember(Bar.class.getName(), "someProperty")
                    ));
            assertThat(barClass.getCollections(),
                    containsInAnyOrder(
                            ApplicationFeatureId.newMember(Bar.class.getName(), "someCollection")
                    ));
            assertThat(barClass.getActions(),
                    containsInAnyOrder(
                            ApplicationFeatureId.newMember(Bar.class.getName(), "someAction")
                    ));
        }

    }

    public static class AddClassParent extends ApplicationFeatureRepositoryDefaultTest {

        @Before
        public void setUp() throws Exception {
            super.setUp();

            context.checking(new Expectations() {{
                allowing(mockServiceRegistry).getRegisteredServices();
                will(returnValue(Lists.newArrayList()));

                allowing(mockSpecificationLoader).allSpecifications();
                will(returnValue(Lists.newArrayList()));
            }});

        }

        @Test
        public void parentNotYetEncountered() throws Exception {

            // given
            final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");

            // expecting
            final ApplicationFeature newlyCreatedParent = new ApplicationFeature();

            context.checking(new Expectations() {{
                oneOf(mockFactoryService).instantiate(ApplicationFeature.class);
                will(returnValue(newlyCreatedParent));
            }});

            // when
            final ApplicationFeatureId classParentId = applicationFeatureRepository.addClassParent(classFeatureId);

            // then
            Assert.assertThat(classParentId, is(equalTo(classFeatureId.getParentPackageId())));
            final ApplicationFeature classPackage = applicationFeatureRepository.findPackage(classParentId);
            assertThat(classPackage, is(newlyCreatedParent));
        }

        @Test
        public void parentAlreadyEncountered() throws Exception {

            // given
            final ApplicationFeatureId packageId = ApplicationFeatureId.newPackage("com.mycompany");
            final ApplicationFeature pkg = new ApplicationFeature();
            pkg.setFeatureId(packageId);
            applicationFeatureRepository.packageFeatures.put(packageId, pkg);

            final ApplicationFeatureId classFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");



            // when
            final ApplicationFeatureId applicationFeatureId = applicationFeatureRepository.addClassParent(classFeatureId);

            // then
            Assert.assertThat(applicationFeatureId, is(equalTo(packageId)));
        }

    }

}