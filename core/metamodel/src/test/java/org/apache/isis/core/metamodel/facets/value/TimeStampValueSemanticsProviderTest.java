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

package org.apache.isis.core.metamodel.facets.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.value.TimeStamp;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FacetHolderImpl;
import org.apache.isis.core.metamodel.facets.value.timestamp.TimeStampValueSemanticsProvider;

public class TimeStampValueSemanticsProviderTest extends ValueSemanticsProviderAbstractTestCase {

    private TimeStampValueSemanticsProvider adapter;
    private Object timestamp;
    private FacetHolder holder;

    @Before
    public void setUpObjects() throws Exception {
        context.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("isis.value.format.timestamp");
                will(returnValue(null));
            }
        });

        TestClock.initialize();
        timestamp = new TimeStamp(0);
        holder = new FacetHolderImpl();
        setValue(adapter = new TimeStampValueSemanticsProvider(holder, mockServicesInjector));
    }

    @Override
    public void testParseEmptyString() {
        final Object parsed = adapter.parseTextEntry(null, "");
        assertNull(parsed);
    }

    @Test
    public void testTitle() {
        assertEquals("01/01/70 00:00:00 UTC", adapter.titleString(timestamp));
    }

    @Test
    public void testEncodesTimeStamp() {
        final String encodedString = adapter.toEncodedString(timestamp);
        assertEquals("19700101T000000000", encodedString);
    }

    @Test
    public void testDecodesTimeStamp() {
        final String encodedString = "19700101T000000000";
        final Object restored = adapter.fromEncodedString(encodedString);
        assertEquals(((TimeStamp) timestamp).longValue(), ((TimeStamp) restored).longValue());
    }
}
