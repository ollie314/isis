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
package org.apache.isis.core.metamodel.services.publishing;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.iactn.Interaction;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facetapi.IdentifiedHolder;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;

public interface PublishingServiceInternal {

    @Programmatic
    void publishObjects();

    @Programmatic
    void publishAction(
            final Interaction.Execution execution,
            final ObjectAction objectAction, final IdentifiedHolder identifiedHolder,
            final ObjectAdapter targetAdapter,
            final List<ObjectAdapter> parameterAdapters,
            final ObjectAdapter resultAdapter);

    @Programmatic
    void publishProperty(final Interaction.Execution execution);


    interface Block<T> {
        T exec();
    }

    /**
     * Slightly hokey wormhole (anti)pattern to disable publishing for mixin associations.
     */
    @Programmatic
    <T> T withPublishingSuppressed(final Block<T> block);
}
