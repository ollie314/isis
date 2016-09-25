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
package org.apache.isis.core.runtime.services.i18n.po;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PoWriterTest_escape {

    @Test
    public void no_quotes() throws Exception {
        String escape = PoWriter.escape("abc");
        assertThat(escape, is(equalTo("abc")));
    }

    @Test
    public void with_quotes() throws Exception {
        String escape = PoWriter.escape(str('a', '"', 'b', '"', 'c'));
        assertThat(escape, is(equalTo(str('a', '\\', '"', 'b', '\\', '"', 'c'))));
    }

    private static String str(char... params) {
        return new String(params);
    }
}