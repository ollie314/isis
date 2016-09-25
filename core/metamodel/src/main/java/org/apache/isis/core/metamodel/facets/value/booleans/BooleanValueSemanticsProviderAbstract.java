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

package org.apache.isis.core.metamodel.facets.value.booleans;

import org.apache.isis.core.commons.exceptions.IsisException;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.object.parseable.TextEntryParseException;
import org.apache.isis.core.metamodel.facets.object.value.vsp.ValueSemanticsProviderAndFacetAbstract;
import org.apache.isis.core.metamodel.services.ServicesInjector;


public abstract class BooleanValueSemanticsProviderAbstract extends ValueSemanticsProviderAndFacetAbstract<Boolean> implements BooleanValueFacet {

    private static Class<? extends Facet> type() {
        return BooleanValueFacet.class;
    }

    private static final int MAX_LENGTH = 5;
    private static final int TYPICAL_LENGTH = MAX_LENGTH;

    public BooleanValueSemanticsProviderAbstract(final FacetHolder holder, final Class<Boolean> adaptedClass, final Boolean defaultValue, final ServicesInjector context) {
        super(type(), holder, adaptedClass, TYPICAL_LENGTH, MAX_LENGTH, Immutability.IMMUTABLE, EqualByContent.HONOURED, defaultValue, context);
    }

    // //////////////////////////////////////////////////////////////////
    // Parsing
    // //////////////////////////////////////////////////////////////////

    @Override
    protected Boolean doParse(final Object context, final String entry) {
        final String compareTo = entry.trim().toLowerCase();
        if ("true".equals(compareTo)) {
            return Boolean.TRUE;
        } else if ("false".startsWith(compareTo)) {
            return Boolean.FALSE;
        } else {
            throw new TextEntryParseException(String.format("'%s' cannot be parsed as a boolean", entry));
        }
    }

    @Override
    public String titleString(final Object value) {
        return value == null ? "" : isSet(value) ? "True" : "False";
    }

    @Override
    public String titleStringWithMask(final Object value, final String usingMask) {
        return titleString(value);
    }

    // //////////////////////////////////////////////////////////////////
    // Encode, Decode
    // //////////////////////////////////////////////////////////////////

    @Override
    protected String doEncode(final Object object) {
        return isSet(object) ? "T" : "F";
    }

    @Override
    protected Boolean doRestore(final String data) {
        final int dataLength = data.length();
        if (dataLength == 1) {
            switch (data.charAt(0)) {
            case 'T':
                return Boolean.TRUE;
            case 'F':
                return Boolean.FALSE;
            default:
                throw new IsisException("Invalid data for logical, expected 'T', 'F' or 'N, but got " + data.charAt(0));
            }
        } else if (dataLength == 4 || dataLength == 5) {
            switch (data.charAt(0)) {
            case 't':
                return Boolean.TRUE;
            case 'f':
                return Boolean.FALSE;
            default:
                throw new IsisException("Invalid data for logical, expected 't' or 'f', but got " + data.charAt(0));
            }
        }
        throw new IsisException("Invalid data for logical, expected 1, 4 or 5 bytes, got " + dataLength + ": " + data);
    }

    private boolean isSet(final Object value) {
        return ((Boolean) value).booleanValue();
    }

    // //////////////////////////////////////////////////////////////////
    // BooleanValueFacet
    // //////////////////////////////////////////////////////////////////

    @Override
    public boolean isSet(final ObjectAdapter adapter) {
        if (!ObjectAdapter.Util.exists(adapter)) {
            return false;
        }
        final Object object = adapter.getObject();
        final Boolean objectAsBoolean = (Boolean) object;
        return objectAsBoolean.booleanValue();
    }

    // /////// toString ///////

    @Override
    public String toString() {
        return "BooleanValueSemanticsProvider";
    }

}
