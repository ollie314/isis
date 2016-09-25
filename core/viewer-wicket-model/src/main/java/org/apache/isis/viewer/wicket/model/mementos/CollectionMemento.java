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
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.runtime.system.session.IsisSessionFactory;

/**
 * {@link Serializable} representation of a {@link OneToManyAssociation} (a
 * parented collection of entities).
 */
public class CollectionMemento implements Serializable {

    private static final long serialVersionUID = 1L;

    private static ObjectSpecification owningSpecFor(
            final OneToManyAssociation association,
            final IsisSessionFactory isisSessionFactory) {
        final SpecificationLoader specificationLoader = isisSessionFactory.getSpecificationLoader();
        return specificationLoader.loadSpecification(association.getIdentifier().toClassIdentityString());
    }

    private final ObjectSpecId owningType;
    private final String id;

    private transient OneToManyAssociation collection;

    public CollectionMemento(
            final ObjectSpecId owningType,
            final String id, final SpecificationLoader specificationLoader) {
        this(owningType, id, collectionFor(owningType, id, specificationLoader));
    }

    public CollectionMemento(final OneToManyAssociation collection, final IsisSessionFactory isisSessionFactory) {
        this(owningSpecFor(collection, isisSessionFactory).getSpecId(), collection.getIdentifier().toNameIdentityString(), collection);
    }

    private CollectionMemento(final ObjectSpecId owningType, final String id, final OneToManyAssociation collection) {
        this.owningType = owningType;
        this.id = id;
        this.collection = collection;
    }

    public ObjectSpecId getOwningType() {
        return owningType;
    }

    /**
     * Only applies to parented collections, being the id of the collection in
     * the parent (eg <tt>lineItems</tt>).
     * 
     * <p>
     * Will return <tt>null</tt> otherwise.
     */
    public String getId() {
        return id;
    }

    public String getName(final SpecificationLoader specificationLoader) {
        return getCollection(specificationLoader).getName();
    }

    public OneToManyAssociation getCollection(final SpecificationLoader specificationLoader) {
        if (collection == null) {
            collection = collectionFor(owningType, id, specificationLoader);
        }
        return collection;
    }

    private static OneToManyAssociation collectionFor(
            ObjectSpecId owningType,
            String id,
            final SpecificationLoader specificationLoader) {
        return (OneToManyAssociation) SpecUtils.getSpecificationFor(owningType, specificationLoader).getAssociation(id);
    }

}
