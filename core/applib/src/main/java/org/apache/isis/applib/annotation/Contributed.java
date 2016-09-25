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

package org.apache.isis.applib.annotation;

import javax.xml.bind.annotation.XmlType;

/**
 * The means by which a domain service action will be contributed to a domain object.
 */
@XmlType(
        namespace = "http://isis.apache.org/applib/layout/component"
)
public enum Contributed {
    /**
     * Default: contributed as both an action and also (if takes a single argument and has safe semantics) as an association
     * (contributed property if returns a single value, contributed collection if returns a collection).
     */
    AS_BOTH,
    /**
     * Contributed as an action but <i>not</i> as an association.
     */
    AS_ACTION,
    /**
     * (If takes a single argument and has safe semantics) then is contributed as an association
     * (contributed property if returns a single value, contributed collection if returns a collection) but <i>not</i>
     * as an action.
     */
    AS_ASSOCIATION,
    /**
     * The action is not contributed as either an action or as an association.
     */
    AS_NEITHER;

}