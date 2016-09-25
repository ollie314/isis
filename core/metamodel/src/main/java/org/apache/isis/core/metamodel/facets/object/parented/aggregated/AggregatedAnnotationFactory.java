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

package org.apache.isis.core.metamodel.facets.object.parented.aggregated;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Aggregated;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facetapi.MetaModelValidatorRefiner;
import org.apache.isis.core.metamodel.facets.Annotations;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.core.metamodel.progmodel.DeprecatedMarker;
import org.apache.isis.core.metamodel.specloader.validator.MetaModelValidatorAbstract;
import org.apache.isis.core.metamodel.specloader.validator.MetaModelValidatorComposite;
import org.apache.isis.core.metamodel.specloader.validator.ValidationFailures;

/**
 * The {@link Aggregated @Aggregated} annotation is no longer supported; this facet factory prevents its use.
 *
 * @deprecated
 */
@Deprecated
public class AggregatedAnnotationFactory extends FacetFactoryAbstract implements
        MetaModelValidatorRefiner, DeprecatedMarker {

    public AggregatedAnnotationFactory() {
        super(FeatureType.OBJECTS_ONLY);
    }

    final List<String> classesWithAnnotation = Lists.newArrayList();

    @Override
    public void process(final ProcessClassContext processClassContext) {
        final Class<?> cls = processClassContext.getCls();
        final Aggregated annotation = Annotations.getAnnotation(cls, Aggregated.class);
        if(annotation != null) {
            classesWithAnnotation.add(cls.getName());
        }
    }

    @Override
    public void refineMetaModelValidator(
            final MetaModelValidatorComposite metaModelValidator,
            final IsisConfiguration configuration) {

        metaModelValidator.add(new MetaModelValidatorAbstract() {
            @Override
            public void validate(final ValidationFailures validationFailures) {
                for (String className : classesWithAnnotation) {
                    validationFailures.add("%s has @Aggregated annotation, which is no longer supported.",
                            className);
                }
            }
        });
    }
}
