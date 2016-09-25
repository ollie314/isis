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

package org.apache.isis.viewer.wicket.model.mementos;

import java.io.Serializable;

import org.apache.isis.core.metamodel.spec.ObjectSpecId;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.OneToOneAssociation;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.runtime.system.session.IsisSessionFactory;
import org.apache.isis.viewer.wicket.model.models.EntityModel;

public class PropertyMemento implements Serializable {

    private static final long serialVersionUID = 1L;

    private static ObjectSpecification owningSpecFor(
            final OneToOneAssociation property,
            final IsisSessionFactory isisSessionFactory) {
        final SpecificationLoader specificationLoader = isisSessionFactory.getSpecificationLoader();
        return specificationLoader.loadSpecification(property.getIdentifier().toClassIdentityString());
    }

    private final ObjectSpecId owningSpecId;
    private final String identifier;
    private final ObjectSpecId specId;

    public PropertyMemento(
            final OneToOneAssociation property, final IsisSessionFactory isisSessionFactory) {
        this(
                owningSpecFor(property, isisSessionFactory).getSpecId(),
                property.getIdentifier().toNameIdentityString(),
                property.getSpecification().getSpecId()
        );
    }
    
    private PropertyMemento(
            final ObjectSpecId owningSpecId,
            final String name,
            final ObjectSpecId specId) {
        this.owningSpecId = owningSpecId;
        this.identifier = name;
        this.specId = specId;
    }

    public ObjectSpecId getOwningType() {
        return owningSpecId;
    }

    public ObjectSpecId getType() {
        return specId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public OneToOneAssociation getProperty(final SpecificationLoader specificationLoader) {
        return propertyFor(owningSpecId, identifier, specificationLoader);
    }

    private static OneToOneAssociation propertyFor(
            ObjectSpecId owningType,
            String identifier,
            final SpecificationLoader specificationLoader) {
        return (OneToOneAssociation) SpecUtils.getSpecificationFor(owningType, specificationLoader).getAssociation(identifier);
    }

    /**
     * Value semantics so can use as a key in {@link EntityModel} hash.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        return result;
    }

    /**
     * Value semantics so can use as a key in {@link EntityModel} hash.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PropertyMemento other = (PropertyMemento) obj;
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        return true;
    }

}
