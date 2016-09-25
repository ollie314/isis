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
package org.apache.isis.core.runtime.sessiontemplate;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.adapter.oid.RootOid;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.core.runtime.system.session.IsisSession;
import org.apache.isis.core.runtime.system.session.IsisSessionFactory;
import org.apache.isis.core.runtime.system.transaction.IsisTransactionManager;
import org.apache.isis.core.runtime.system.transaction.TransactionalClosure;

public abstract class AbstractIsisSessionTemplate {

    /**
     * Sets up an {@link IsisSession} then passes along any calling framework's context. 
     */
    public void execute(final AuthenticationSession authSession, final Object context) {
        try {
            getIsisSessionFactory().openSession(authSession);
            PersistenceSession persistenceSession = getPersistenceSession();
            persistenceSession.getServicesInjector().injectServicesInto(this);
            doExecute(context);
        } finally {
            getIsisSessionFactory().closeSession();
        }
    }

    // //////////////////////////////////////
    
    /**
     * Either override {@link #doExecute(Object)} (this method) or alternatively override
     * {@link #doExecuteWithTransaction(Object)}.
     *
     * <p>
     * This method is called within a current {@link org.apache.isis.core.runtime.system.session.IsisSession session},
     * but with no current transaction.  The default implementation sets up a
     * {@link org.apache.isis.core.runtime.system.transaction.IsisTransaction transaction}
     * and then calls {@link #doExecuteWithTransaction(Object)}.  Override if you require more sophisticated
     * transaction handling.
     */
    protected void doExecute(final Object context) {
        final PersistenceSession persistenceSession = getPersistenceSession();
        final IsisTransactionManager transactionManager = getTransactionManager(persistenceSession);
        transactionManager.executeWithinTransaction(new TransactionalClosure() {
            @Override
            public void execute() {
                doExecuteWithTransaction(context);
            }
        });
    }

    /**
     * Either override {@link #doExecuteWithTransaction(Object)} (this method) or alternatively override
     * {@link #doExecuteWithTransaction(Object)}.
     *
     * <p>
     * This method is called within a current
     * {@link org.apache.isis.core.runtime.system.transaction.IsisTransaction transaction}, by the default
     * implementation of {@link #doExecute(Object)}.
     */
    protected void doExecuteWithTransaction(final Object context) {}

    // //////////////////////////////////////

    protected ObjectAdapter adapterFor(final Object targetObject) {
        return getPersistenceSession().adapterFor(targetObject);
    }
    protected ObjectAdapter adapterFor(final RootOid rootOid) {
        return getPersistenceSession().adapterFor(rootOid);
    }
    
    // //////////////////////////////////////


    protected IsisSessionFactory getIsisSessionFactory() {
        return IsisContext.getSessionFactory();
    }

    protected PersistenceSession getPersistenceSession() {
        return getIsisSessionFactory().getCurrentSession().getPersistenceSession();
    }

    protected IsisTransactionManager getTransactionManager(PersistenceSession persistenceSession) {
        return persistenceSession.getTransactionManager();
    }

    protected SpecificationLoader getSpecificationLoader() {
        return getIsisSessionFactory().getSpecificationLoader();
    }


}
