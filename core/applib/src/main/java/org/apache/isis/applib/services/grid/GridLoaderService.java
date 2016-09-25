/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.isis.applib.services.grid;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.layout.component.Grid;

public interface GridLoaderService {

    /**
     * Whether dynamic reloading of layouts is enabled.
     */
    @Programmatic
    boolean supportsReloading();

    /**
     * To support metamodel invalidation/rebuilding of spec.
     */
    @Programmatic void remove(Class<?> domainClass);

    /**
     * Whether any persisted layout metadata (eg a <code>.layout.xml</code> file) exists for this domain class.
     */
    @Programmatic
    boolean existsFor(Class<?> domainClass);

    /**
     * Returns a new instance of a {@link Grid} for the specified domain class, eg from a
     * <code>layout.xml</code> file, else <code>null</code>.
     */
    @Programmatic
    Grid load(final Class<?> domainClass);

}
