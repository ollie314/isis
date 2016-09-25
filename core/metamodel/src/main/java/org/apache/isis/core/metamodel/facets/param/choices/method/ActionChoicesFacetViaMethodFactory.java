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

package org.apache.isis.core.metamodel.facets.param.choices.method;

import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.isis.core.commons.lang.StringExtensions;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.MethodFinderUtils;
import org.apache.isis.core.metamodel.facets.MethodPrefixBasedFacetFactoryAbstract;
import org.apache.isis.core.metamodel.facets.MethodPrefixConstants;
import org.apache.isis.core.metamodel.methodutils.MethodScope;
import org.apache.isis.core.metamodel.services.ServicesInjector;
import org.apache.isis.core.metamodel.services.persistsession.PersistenceSessionServiceInternal;

public class ActionChoicesFacetViaMethodFactory extends MethodPrefixBasedFacetFactoryAbstract {

    private static final String[] PREFIXES = { MethodPrefixConstants.CHOICES_PREFIX };


    /**
     * Note that the {@link Facet}s registered are the generic ones from
     * noa-architecture (where they exist)
     */
    public ActionChoicesFacetViaMethodFactory() {
        super(FeatureType.ACTIONS_ONLY, OrphanValidation.VALIDATE, PREFIXES);
    }

    // ///////////////////////////////////////////////////////
    // Actions
    // ///////////////////////////////////////////////////////

    @Override
    public void process(final ProcessMethodContext processMethodContext) {
        attachActionChoicesFacetIfParameterChoicesMethodIsFound(processMethodContext);
    }

    private void attachActionChoicesFacetIfParameterChoicesMethodIsFound(final ProcessMethodContext processMethodContext) {

        final Method actionMethod = processMethodContext.getMethod();
        final Class<?>[] actionParamTypes = actionMethod.getParameterTypes();

        if (actionParamTypes.length <= 0) {
            return;
        }

        Method choicesMethod = null;
        if (choicesMethod == null) {
            choicesMethod = findChoicesMethodReturning(processMethodContext, Object[][].class);
        }
        if (choicesMethod == null) {
            choicesMethod = findChoicesMethodReturning(processMethodContext, Object[].class);
        }
        if (choicesMethod == null) {
            choicesMethod = findChoicesMethodReturning(processMethodContext, Collection.class);
        }
        if (choicesMethod == null) {
            return;
        }
        processMethodContext.removeMethod(choicesMethod);

        final Class<?> returnType = actionMethod.getReturnType();
        final FacetHolder action = processMethodContext.getFacetHolder();
        final ActionChoicesFacetViaMethod facet = new ActionChoicesFacetViaMethod(choicesMethod, returnType, action,
                getDeploymentCategory(), getSpecificationLoader(), getAuthenticationSessionProvider(), adapterManager);
        FacetUtil.addFacet(facet);
    }

    protected Method findChoicesMethodReturning(final ProcessMethodContext processMethodContext, final Class<?> returnType2) {
        Method choicesMethod;
        final Class<?> cls = processMethodContext.getCls();

        final Method actionMethod = processMethodContext.getMethod();
        final MethodScope methodScope = MethodScope.scopeFor(actionMethod);
        final String capitalizedName = StringExtensions.asCapitalizedName(actionMethod.getName());

        final String name = MethodPrefixConstants.CHOICES_PREFIX + capitalizedName;
        choicesMethod = MethodFinderUtils.findMethod(cls, methodScope, name, returnType2, new Class[0]);
        return choicesMethod;
    }

    // ///////////////////////////////////////////////////////////////
    // Dependencies
    // ///////////////////////////////////////////////////////////////


    @Override
    public void setServicesInjector(final ServicesInjector servicesInjector) {
        super.setServicesInjector(servicesInjector);
        adapterManager = servicesInjector.getPersistenceSessionServiceInternal();
    }

    PersistenceSessionServiceInternal adapterManager;

}
