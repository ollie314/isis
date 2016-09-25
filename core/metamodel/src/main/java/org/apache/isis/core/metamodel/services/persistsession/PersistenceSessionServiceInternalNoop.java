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
package org.apache.isis.core.metamodel.services.persistsession;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.apache.isis.applib.services.xactn.Transaction;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.oid.Oid;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosure;

@DomainService(nature = NatureOfService.DOMAIN)
public class PersistenceSessionServiceInternalNoop implements PersistenceSessionServiceInternal {


    @Override
    public ObjectAdapter getAdapterFor(final Object pojo) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public ObjectAdapter adapterFor(
            final Object pojo,
            final ObjectAdapter ownerAdapter,
            final OneToManyAssociation collection) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public ObjectAdapter mapRecreatedPojo(Oid oid, Object recreatedPojo) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public void removeAdapter(ObjectAdapter adapter) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public ObjectAdapter adapterFor(final Object domainObject) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public ObjectAdapter getAdapterFor(Oid oid) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public ObjectAdapter createTransientInstance(final ObjectSpecification spec) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public ObjectAdapter createViewModelInstance(ObjectSpecification spec, String memento) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public Object lookup(
            final Bookmark bookmark,
            final BookmarkService2.FieldResetPolicy fieldResetPolicy) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public Bookmark bookmarkFor(Object domainObject) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public Bookmark bookmarkFor(Class<?> cls, String identifier) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public void resolve(final Object parent, final Object field) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public void resolve(final Object parent) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public void beginTran() {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public boolean flush() {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public void executeWithinTransaction(TransactionalClosure transactionalClosure) {
        transactionalClosure.execute();
    }

    @Override
    public Transaction currentTransaction() {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public void remove(final ObjectAdapter adapter) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public void makePersistent(final ObjectAdapter adapter) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public <T> ObjectAdapter firstMatchingQuery(final Query<T> query) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

    @Override
    public <T> List<ObjectAdapter> allMatchingQuery(final Query<T> query) {
        throw new UnsupportedOperationException("Not supported by this implementation of PersistenceSessionServiceInternal");
    }

}
