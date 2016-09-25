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

package org.apache.isis.core.metamodel.facets.object.choices.enums;

import org.apache.isis.core.commons.lang.ObjectExtensions;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facets.object.value.vsp.ValueFacetUsingSemanticsProviderFactory;

public class EnumFacetUsingValueFacetUsingSemanticsProviderFactory<T extends Enum<T>> extends ValueFacetUsingSemanticsProviderFactory<T> {

    public EnumFacetUsingValueFacetUsingSemanticsProviderFactory() {
        super();
    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        final Class<?> cls = processClassContext.getCls();
        final FacetHolder holder = processClassContext.getFacetHolder();

        if (!cls.isEnum()) {
            return;
        }

        addFacets(new EnumValueSemanticsProvider<T>(holder, asT(cls), getContext()));
        FacetUtil.addFacet(new ChoicesFacetEnum(holder, cls.getEnumConstants()));
    }

    protected Class<T> asT(final Class<?> cls) {
        return ObjectExtensions.asT(cls);
    }

}
