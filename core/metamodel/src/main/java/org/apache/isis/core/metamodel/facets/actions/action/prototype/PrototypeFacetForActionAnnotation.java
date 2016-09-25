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

package org.apache.isis.core.metamodel.facets.actions.action.prototype;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.core.metamodel.deployment.DeploymentCategory;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.actions.prototype.PrototypeFacet;
import org.apache.isis.core.metamodel.facets.actions.prototype.PrototypeFacetAbstract;

public class PrototypeFacetForActionAnnotation extends PrototypeFacetAbstract {

    public static PrototypeFacet create(
            final Action action,
            final FacetHolder holder,
            final DeploymentCategory deploymentCategory) {

        return action == null || action.restrictTo() == RestrictTo.NO_RESTRICTIONS
                ? null
                : new PrototypeFacetForActionAnnotation(holder, deploymentCategory);

    }

    private PrototypeFacetForActionAnnotation(FacetHolder holder, final DeploymentCategory deploymentCategory) {
        super(holder, deploymentCategory);
    }

}
