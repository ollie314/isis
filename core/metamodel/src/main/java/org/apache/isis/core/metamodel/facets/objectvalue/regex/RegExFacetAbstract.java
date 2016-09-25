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

package org.apache.isis.core.metamodel.facets.objectvalue.regex;

import org.apache.isis.applib.events.ValidityEvent;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.MultipleValueFacetAbstract;
import org.apache.isis.core.metamodel.interactions.ProposedHolder;
import org.apache.isis.core.metamodel.interactions.ValidityContext;

public abstract class RegExFacetAbstract extends MultipleValueFacetAbstract implements RegExFacet {

    public static Class<? extends Facet> type() {
        return RegExFacet.class;
    }

    private final String validation;
    private final String format;
    private final boolean caseSensitive;
    private final String replacement;

    public RegExFacetAbstract(final String validation, final String format, final boolean caseSensitive, final FacetHolder holder, final String replacement) {
        super(type(), holder);
        this.validation = validation;
        this.format = format;
        this.caseSensitive = caseSensitive;
        this.replacement = replacement;
    }

    @Override
    public String validation() {
        return validation;
    }

    @Override
    public String format() {
        return format;
    }

    @Override
    public boolean caseSensitive() {
        return caseSensitive;
    }

    @Override
    public String replacement() {
        return replacement;
    }

    // //////////////////////////////////////////////////////////

    @Override
    public String invalidates(final ValidityContext<? extends ValidityEvent> context) {
        if (!(context instanceof ProposedHolder)) {
            return null;
        }
        final ProposedHolder proposedHolder = (ProposedHolder) context;
        final ObjectAdapter proposedArgument = proposedHolder.getProposed();
        if (proposedArgument == null) {
            return null;
        }
        final String titleString = proposedArgument.titleString();
        if (!doesNotMatch(titleString)) {
            return null;
        }

        return replacement();
    }

}
