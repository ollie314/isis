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

package org.apache.isis.viewer.wicket.ui.pages.error;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import org.apache.isis.applib.services.error.ErrorDetails;
import org.apache.isis.applib.services.error.ErrorReportingService;
import org.apache.isis.applib.services.error.Ticket;
import org.apache.isis.viewer.wicket.model.common.PageParametersUtils;
import org.apache.isis.viewer.wicket.ui.errors.ExceptionModel;
import org.apache.isis.viewer.wicket.ui.errors.ExceptionStackTracePanel;
import org.apache.isis.viewer.wicket.ui.errors.StackTraceDetail;
import org.apache.isis.viewer.wicket.ui.pages.PageAbstract;

/**
 * Web page representing the home page (showing a welcome message).
 */
@AuthorizeInstantiation("org.apache.isis.viewer.wicket.roles.USER")
public class ErrorPage extends PageAbstract {

    private static final long serialVersionUID = 1L;

    private static final String ID_EXCEPTION_STACK_TRACE = "exceptionStackTrace";


    public ErrorPage(ExceptionModel exceptionModel) {
        super(PageParametersUtils.newPageParameters(), null);

        addBookmarkedPages(themeDiv);

        final ErrorReportingService errorReportingService = getServicesInjector()
                .lookupService(ErrorReportingService.class);
        if(errorReportingService != null) {

            final String mainMessage = exceptionModel.getMainMessage();
            final boolean recognized = exceptionModel.isRecognized();
            final boolean authorizationException = exceptionModel.isAuthorizationException();

            final List<StackTraceDetail> stackTrace = exceptionModel.getStackTrace();
            final List<String> stackDetails = Lists.transform(stackTrace, new Function<StackTraceDetail, String>() {
                @Nullable @Override public String apply(final StackTraceDetail stackTraceDetail) {
                    return stackTraceDetail.getLine();
                }
            });

            final ErrorDetails errorDetails = new ErrorDetails(mainMessage, recognized, authorizationException,
                    stackDetails);

            final Ticket ticket = errorReportingService.reportError(errorDetails);

            if (ticket != null) {
                exceptionModel.setTicket(ticket);
            }

        }

        themeDiv.add(new ExceptionStackTracePanel(ID_EXCEPTION_STACK_TRACE, exceptionModel));

    }

}
