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

package org.apache.isis.core.metamodel.facets.members.order.annotprop;

import java.util.Properties;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.services.i18n.TranslationService;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.Annotations;
import org.apache.isis.core.metamodel.facets.ContributeeMemberFacetFactory;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.core.metamodel.facets.members.order.MemberOrderFacet;

public class MemberOrderFacetFactory extends FacetFactoryAbstract implements ContributeeMemberFacetFactory {

    public MemberOrderFacetFactory() {
        super(FeatureType.MEMBERS);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        
        MemberOrderFacet memberOrderFacet = createFromMetadataPropertiesIfPossible(processMethodContext);
        if(memberOrderFacet == null) {
            memberOrderFacet = createFromAnnotationIfPossible(processMethodContext);
        }

        // no-op if facet is null
        FacetUtil.addFacet(memberOrderFacet);
    }

    @Override
    public void process(final ProcessContributeeMemberContext processMemberContext) {
        final MemberOrderFacet memberOrderFacet = createFromMetadataPropertiesIfPossible(processMemberContext);

        // no-op if facet is null
        FacetUtil.addFacet(memberOrderFacet);
    }

    private MemberOrderFacet createFromMetadataPropertiesIfPossible(
            final ProcessContextWithMetadataProperties<? extends FacetHolder> pcwmp) {
        
        final FacetHolder holder = pcwmp.getFacetHolder();
        
        final MemberOrderFacet memberOrderFacet;
        final Properties properties = pcwmp.metadataProperties("memberOrder");
        if(properties != null) {
            memberOrderFacet = new MemberOrderFacetProperties(
                    properties,
                    servicesInjector.lookupService(TranslationService.class),
                    holder);
        } else {
            memberOrderFacet = null;
        }
        return memberOrderFacet;
    }

    private MemberOrderFacet createFromAnnotationIfPossible(final ProcessMethodContext processMethodContext) {
        final MemberOrder annotation = Annotations.getAnnotation(processMethodContext.getMethod(), MemberOrder.class);
        if (annotation != null) {
            return new MemberOrderFacetAnnotation(
                    annotation.name(),
                    annotation.sequence(),
                    servicesInjector.lookupService(TranslationService.class),
                    processMethodContext.getFacetHolder());
        }
        else {
            return null;
        }
    }

}
