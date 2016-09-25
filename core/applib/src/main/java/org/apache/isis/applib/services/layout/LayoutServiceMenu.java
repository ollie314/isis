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
package org.apache.isis.applib.services.layout;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.inject.Inject;

import org.apache.isis.applib.IsisApplibModule;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "500.400"
)
public class LayoutServiceMenu {

    public static abstract class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<LayoutServiceMenu> {
    }

    private final MimeType mimeTypeApplicationZip;

    public LayoutServiceMenu() {
        try {
            mimeTypeApplicationZip = new MimeType("application", "zip");
        } catch (final MimeTypeParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static class DownloadLayoutsDomainEvent extends ActionDomainEvent {
    }

    @Action(
            domainEvent = DownloadLayoutsDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-download",
            named = "Download Layouts (XML)"
    )
    @MemberOrder(sequence="500.400.1")
    public Blob downloadLayouts(final LayoutService.Style style) {

        final String fileName = "layouts." + style.name().toLowerCase() + ".zip";

        final byte[] zipBytes = layoutService.toZip(style);
        return new Blob(fileName, mimeTypeApplicationZip, zipBytes);
    }

    public LayoutService.Style default0DownloadLayouts() {
        return LayoutService.Style.NORMALIZED;
    }


    // //////////////////////////////////////


    @javax.inject.Inject
    LayoutService layoutService;



}