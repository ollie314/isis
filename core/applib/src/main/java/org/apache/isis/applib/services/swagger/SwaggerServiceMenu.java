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
package org.apache.isis.applib.services.swagger;

import javax.inject.Inject;

import org.apache.isis.applib.IsisApplibModule;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Clob;


@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "500.600"
)
public class SwaggerServiceMenu {

    public static abstract class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<SwaggerServiceMenu> {
    }

    public static class DownloadSwaggerSpecDomainEvent extends ActionDomainEvent {}

    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = DownloadSwaggerSpecDomainEvent.class,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-download"
    )
    @MemberOrder(sequence="500.600.1")
    public Clob downloadSwaggerSpec(
            @ParameterLayout(named = "Filename")
            final String fileNamePrefix,
            final SwaggerService.Visibility visibility,
            final SwaggerService.Format format) {
        final String fileName = Util.buildFileName(fileNamePrefix, visibility, format);
        final String spec = swaggerService.generateSwaggerSpec(visibility, format);
        return new Clob(fileName, format.mediaType(), spec);
    }

    public String default0DownloadSwaggerSpec() {
        return "swagger";
    }
    public SwaggerService.Visibility default1DownloadSwaggerSpec() {
        return SwaggerService.Visibility.PRIVATE;
    }
    public SwaggerService.Format default2DownloadSwaggerSpec() {
        return SwaggerService.Format.YAML;
    }

    @javax.inject.Inject
    SwaggerService swaggerService;
}    
