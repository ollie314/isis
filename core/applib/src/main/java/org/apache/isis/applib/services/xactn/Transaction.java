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

package org.apache.isis.applib.services.xactn;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.HasTransactionId;

import java.util.UUID;

/**
 * Representation of the current transaction, which conceptually wraps the underlying objectstore transaction.
 */
public interface Transaction extends HasTransactionId {


    /**
     * The {@link HasTransactionId#getTransactionId()} is (as of 1.13.0) actually an identifier for the request/
     * interaction, and there can actually be multiple transactions within such a request/interaction.  The sequence
     * (0-based) is used to distinguish such.
     */
    @Programmatic
    int getSequence();

    /**
     * Flush all changes to the object store.
     *
     * <p>
     * Occasionally useful to ensure that newly persisted domain objects
     * are flushed to the database prior to a subsequent repository query.
     * </p>
     *
     * <p>
     *     Equivalent to {@link TransactionService#flushTransaction()}.
     * </p>
     */
    @Programmatic
    void flush();

    /**
     * If the cause has been rendered higher up in the stack, then clear the cause so that
     * it won't be picked up and rendered elsewhere.
     */
    @Programmatic
    void clearAbortCause();

}
