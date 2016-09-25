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
package org.apache.isis.viewer.wicket.ui.components.layout.bs3.clearfix;

import org.apache.wicket.markup.html.WebMarkupContainer;

import org.apache.isis.applib.layout.grid.bootstrap3.BS3ClearFix;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.ui.components.layout.bs3.Util;
import org.apache.isis.viewer.wicket.ui.panels.PanelAbstract;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;

public class ClearFix extends PanelAbstract<EntityModel> {

    private static final long serialVersionUID = 1L;

    private static final String ID_COL = "clearfix";

    private final BS3ClearFix bs3ClearFix;

    public ClearFix(
            final String id,
            final EntityModel entityModel) {

        super(id, entityModel);

        bs3ClearFix = (BS3ClearFix) entityModel.getLayoutMetadata();

        buildGui();
    }

    private void buildGui() {

        setRenderBodyOnly(true);
        Util.appendCssClassIfRequired(this, bs3ClearFix);

        final WebMarkupContainer div = new WebMarkupContainer(ID_COL);
        CssClassAppender.appendCssClassTo(div, bs3ClearFix.toCssClass());

        this.addOrReplace(div);
    }


}
