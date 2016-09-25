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

@XmlType(
        namespace = "http://isis.apache.org/applib/layout/component"
)
public enum BookmarkPolicy {
    /**
     * Can be bookmarked, and is a top-level 'root' (or parent) bookmark.
     */
    AS_ROOT,
    /**
     * Can be bookmarked, but only as a child or some other parent/root bookmark
     */
    AS_CHILD,
    /**
     * An unimportant entity that should never be bookmarked.
     */
    NEVER
}