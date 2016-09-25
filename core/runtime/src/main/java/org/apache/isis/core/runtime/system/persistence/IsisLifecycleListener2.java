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
package org.apache.isis.core.runtime.system.persistence;

import java.util.Map;

import javax.jdo.listener.AttachLifecycleListener;
import javax.jdo.listener.ClearLifecycleListener;
import javax.jdo.listener.CreateLifecycleListener;
import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.DetachLifecycleListener;
import javax.jdo.listener.DirtyLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.LoadLifecycleListener;
import javax.jdo.listener.StoreLifecycleListener;

import com.google.common.collect.Maps;

import org.datanucleus.enhancement.Persistable;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManagerBase;

public class IsisLifecycleListener2
        implements AttachLifecycleListener, ClearLifecycleListener, CreateLifecycleListener, DeleteLifecycleListener,
        DetachLifecycleListener, DirtyLifecycleListener, LoadLifecycleListener, StoreLifecycleListener,
        SuspendableListener {

    /**
     * The internal contract between PersistenceSession and this class.
     */
    interface PersistenceSessionLifecycleManagement extends AdapterManagerBase {

        void ensureRootObject(Persistable pojo);
        void initializeMapAndCheckConcurrency(Persistable pojo);

        void enlistCreatedAndRemapIfRequiredThenInvokeIsisInvokePersistingOrUpdatedCallback(Persistable pojo);
        void invokeIsisPersistingCallback(Persistable pojo);
        void enlistUpdatingAndInvokeIsisUpdatingCallback(Persistable pojo);
        void enlistDeletingAndInvokeIsisRemovingCallbackFacet(Persistable pojo);
    }

    private final PersistenceSessionLifecycleManagement persistenceSession;

    public IsisLifecycleListener2(final PersistenceSessionLifecycleManagement persistenceSession) {
        this.persistenceSession = persistenceSession;
    }


    /////////////////////////////////////////////////////////////////////////
    // callbacks
    /////////////////////////////////////////////////////////////////////////

    @Override
    public void postCreate(final InstanceLifecycleEvent event) {
        // no-op
    }

    @Override
    public void preAttach(final InstanceLifecycleEvent event) {
        final Persistable pojo = Utils.persistenceCapableFor(event);
        persistenceSession.ensureRootObject(pojo);
    }

    @Override
    public void postAttach(final InstanceLifecycleEvent event) {
        final Persistable pojo = Utils.persistenceCapableFor(event);
        persistenceSession.ensureRootObject(pojo);
    }

    @Override
    public void postLoad(final InstanceLifecycleEvent event) {
        final Persistable pojo = Utils.persistenceCapableFor(event);
        persistenceSession.initializeMapAndCheckConcurrency(pojo);
    }

	@Override
    public void preStore(InstanceLifecycleEvent event) {
        final Persistable pojo = Utils.persistenceCapableFor(event);
        persistenceSession.invokeIsisPersistingCallback(pojo);
    }

    @Override
    public void postStore(InstanceLifecycleEvent event) {
        final Persistable pojo = Utils.persistenceCapableFor(event);
        persistenceSession.enlistCreatedAndRemapIfRequiredThenInvokeIsisInvokePersistingOrUpdatedCallback(pojo);
    }

    @Override
    public void preDirty(InstanceLifecycleEvent event) {
        final Persistable pojo = Utils.persistenceCapableFor(event);
        persistenceSession.enlistUpdatingAndInvokeIsisUpdatingCallback(pojo);
    }

    @Override
    public void postDirty(InstanceLifecycleEvent event) {

        // cannot assert on the frameworks being in agreement, due to the scenario documented
        // in the FrameworkSynchronizer#preDirtyProcessing(...)
        //
        // 1<->m bidirectional, persistence-by-reachability

        // no-op
    }

    @Override
    public void preDelete(InstanceLifecycleEvent event) {
        final Persistable pojo = Utils.persistenceCapableFor(event);
        persistenceSession.enlistDeletingAndInvokeIsisRemovingCallbackFacet(pojo);


    }

    @Override
    public void postDelete(InstanceLifecycleEvent event) {

        // previously we called the PersistenceSession to invoke the removed callback (if any).
        // however, this is almost certainly incorrect, because DN will not allow us
        // to "touch" the pojo once deleted.
        //
        // CallbackFacet.Util.callCallback(adapter, RemovedCallbackFacet.class);

    }

    /**
     * Does nothing, not important event for Isis to track.
     */
    @Override
    public void preClear(InstanceLifecycleEvent event) {
        // ignoring, not important to us
    }

    /**
     * Does nothing, not important event for Isis to track.
     */
    @Override
    public void postClear(InstanceLifecycleEvent event) {
        // ignoring, not important to us
    }

    @Override
    public void preDetach(InstanceLifecycleEvent event) {
        final Persistable pojo = Utils.persistenceCapableFor(event);
        persistenceSession.ensureRootObject(pojo);
    }

    @Override
    public void postDetach(InstanceLifecycleEvent event) {
        final Persistable pojo = Utils.persistenceCapableFor(event);
        persistenceSession.ensureRootObject(pojo);
    }

    
    // /////////////////////////////////////////////////////////
    // SuspendListener
    // /////////////////////////////////////////////////////////

    private boolean suspended;

    @Override
    public boolean isSuspended() {
        return suspended;
    }

    @Override
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    // /////////////////////////////////////////////////////////
    // Logging
    // /////////////////////////////////////////////////////////

    private enum Phase {
        PRE, POST
    }

    private static Map<Integer, LifecycleEventType> events = Maps.newHashMap();

    private enum LifecycleEventType {
        CREATE(0), LOAD(1), STORE(2), CLEAR(3), DELETE(4), DIRTY(5), DETACH(6), ATTACH(7);

        private LifecycleEventType(int code) {
            events.put(code, this);
        }

        public static LifecycleEventType lookup(int code) {
            return events.get(code);
        }
    }

    private String logString(Phase phase, LoggingLocation location, InstanceLifecycleEvent event) {
        final Persistable pojo = Utils.persistenceCapableFor(event);
        final ObjectAdapter adapter = persistenceSession.getAdapterFor(pojo);
        return phase + " " + location.prefix + " " + LifecycleEventType.lookup(event.getEventType()) + ": oid=" + (adapter !=null? adapter.getOid(): "(null)") + " ,pojo " + pojo;
    }

}
