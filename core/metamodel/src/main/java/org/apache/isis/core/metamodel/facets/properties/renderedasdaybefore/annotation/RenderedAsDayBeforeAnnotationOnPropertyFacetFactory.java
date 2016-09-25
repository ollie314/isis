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

package org.apache.isis.core.metamodel.facets.properties.renderedasdaybefore.annotation;

import org.apache.isis.applib.annotation.RenderedAsDayBefore;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facetapi.MetaModelValidatorRefiner;
import org.apache.isis.core.metamodel.facets.Annotations;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.core.metamodel.facets.objectvalue.renderedadjusted.RenderedAdjustedFacet;
import org.apache.isis.core.metamodel.progmodel.DeprecatedMarker;
import org.apache.isis.core.metamodel.services.ServicesInjector;
import org.apache.isis.core.metamodel.specloader.validator.MetaModelValidatorComposite;
import org.apache.isis.core.metamodel.specloader.validator.MetaModelValidatorForDeprecatedAnnotation;

/**
 * @deprecated
 */
@Deprecated
public class RenderedAsDayBeforeAnnotationOnPropertyFacetFactory extends FacetFactoryAbstract implements MetaModelValidatorRefiner, DeprecatedMarker {

    private final MetaModelValidatorForDeprecatedAnnotation validator = new MetaModelValidatorForDeprecatedAnnotation(RenderedAsDayBefore.class);


    public RenderedAsDayBeforeAnnotationOnPropertyFacetFactory() {
        super(FeatureType.PROPERTIES_ONLY);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        final RenderedAsDayBefore annotation = Annotations.getAnnotation(processMethodContext.getMethod(), RenderedAsDayBefore.class);
        final RenderedAdjustedFacet facet = create(annotation, processMethodContext.getFacetHolder());

        FacetUtil.addFacet(validator.flagIfPresent(facet, processMethodContext));
    }

    private RenderedAdjustedFacet create(final RenderedAsDayBefore annotation, final FacetHolder holder) {
        return annotation != null ? new RenderedAsDayBeforeFacetAnnotationOnProperty(holder) : null;
    }

    @Override
    public void refineMetaModelValidator(final MetaModelValidatorComposite metaModelValidator, final IsisConfiguration configuration) {
        metaModelValidator.add(validator);
    }

    @Override
    public void setServicesInjector(final ServicesInjector servicesInjector) {
        super.setServicesInjector(servicesInjector);
        IsisConfiguration configuration = servicesInjector.getConfigurationServiceInternal();
        validator.setConfiguration(configuration);
    }


}
