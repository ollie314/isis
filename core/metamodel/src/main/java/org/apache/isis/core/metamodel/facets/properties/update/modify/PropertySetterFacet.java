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

package org.apache.isis.core.metamodel.facets.properties.update.modify;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facets.propcoll.accessor.PropertyOrCollectionAccessorFacet;
import org.apache.isis.core.metamodel.spec.feature.OneToOneAssociation;

/**
 * The mechanism by which the value of the property can be set.
 * 
 * <p>
 * In the standard Apache Isis Programming Model, corresponds to invoking the
 * mutator method for a property.
 * 
 * @see PropertyOrCollectionAccessorFacet
 * @see org.apache.isis.core.metamodel.facets.properties.update.clear.PropertyClearFacet
 * @see org.apache.isis.core.metamodel.facets.properties.update.init.PropertyInitializationFacet
 */
public interface PropertySetterFacet extends Facet {

    /**
     * Sets the value of this property.
     */
    void setProperty(
            final OneToOneAssociation owningAssociation,
            final ObjectAdapter inObject,
            final ObjectAdapter value,
            final InteractionInitiatedBy interactionInitiatedBy);
}
