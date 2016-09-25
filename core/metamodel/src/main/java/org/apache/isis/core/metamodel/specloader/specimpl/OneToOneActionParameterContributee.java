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
package org.apache.isis.core.metamodel.specloader.specimpl;

import java.util.List;

import org.apache.isis.core.commons.lang.ListExtensions;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.spec.feature.ObjectActionParameter;

public class OneToOneActionParameterContributee extends OneToOneActionParameterDefault implements ObjectActionParameterContributee{

    private final ObjectAdapter serviceAdapter;
    private final ObjectActionParameter serviceActionParameter;
    private final ObjectActionContributee contributeeAction;

    public OneToOneActionParameterContributee(
            final ObjectAdapter serviceAdapter,
            final ObjectActionParameterAbstract serviceActionParameter,
            final int contributeeParamNumber,
            final ObjectActionContributee contributeeAction) {
        super(contributeeParamNumber, contributeeAction, serviceActionParameter.getPeer());
        this.serviceAdapter = serviceAdapter;
        this.serviceActionParameter = serviceActionParameter;
        this.contributeeAction = contributeeAction;
    }

    @Override
    public ObjectAdapter[] getAutoComplete(
            final ObjectAdapter adapter,
            final String searchArg,
            final InteractionInitiatedBy interactionInitiatedBy) {
        return serviceActionParameter.getAutoComplete(serviceAdapter, searchArg,
                interactionInitiatedBy);
    }

    protected ObjectAdapter targetForDefaultOrChoices(final ObjectAdapter adapter) {
        return serviceAdapter;
    }

    protected List<ObjectAdapter> argsForDefaultOrChoices(
            final ObjectAdapter contributee,
            final List<ObjectAdapter> argumentsIfAvailable) {

        final List<ObjectAdapter> suppliedArgs = ListExtensions.mutableCopy(argumentsIfAvailable);
        
        final int contributeeParam = contributeeAction.getContributeeParam();
        ListExtensions.insert(suppliedArgs, contributeeParam, contributee);
        
        return suppliedArgs;
    }
    
}
