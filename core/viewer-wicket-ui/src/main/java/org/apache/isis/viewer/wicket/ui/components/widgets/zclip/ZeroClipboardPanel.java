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
package org.apache.isis.viewer.wicket.ui.components.widgets.zclip;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.ui.pages.PageClassRegistry;
import org.apache.isis.viewer.wicket.ui.pages.PageClassRegistryAccessor;
import org.apache.isis.viewer.wicket.ui.panels.PanelAbstract;

import de.agilecoders.wicket.jquery.util.Strings2;

public class ZeroClipboardPanel extends PanelAbstract<EntityModel> {

    private static final long serialVersionUID = 1L;
    
    private static final String ID_SUBSCRIBING_LINK = "subscribingLink";
    private static final String ID_COPY_LINK = "copyLink";
    private static final String ID_SIMPLE_CLIPBOARD_MODAL_WINDOW = "simpleClipboardModalWindow";

    private AjaxLink<ObjectAdapter> copyLink;
    private SimpleClipboardModalWindow simpleClipboardModalWindow;

    public ZeroClipboardPanel(String id, EntityModel entityModel) {
        super(id, entityModel);
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();

        if(copyLink == null) {
            copyLink = createLink(ID_COPY_LINK);
            addOrReplace(copyLink);
        }
        EntityModel model = getModel();
        addSimpleClipboardModalWindow();

        EntityModel.RenderingHint renderingHint = model.getRenderingHint();
        EntityModel.Mode mode = model.getMode();
        setVisible(renderingHint == EntityModel.RenderingHint.REGULAR && mode == EntityModel.Mode.VIEW);
    }

    private AjaxLink<ObjectAdapter> createLink(String linkId) {
        return newSimpleClipboardLink(linkId);
    }

    private AjaxLink<ObjectAdapter> newSimpleClipboardLink(String linkId) {
        return new AjaxLink<ObjectAdapter>(linkId) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void onClick(AjaxRequestTarget target) {
                
                String contentId = simpleClipboardModalWindow.getContentId();
                SimpleClipboardModalWindowPanel panel = new SimpleClipboardModalWindowPanel(contentId);
                SimpleClipboardModalWindowForm form = new SimpleClipboardModalWindowForm("form");

                final TextField<String> textField = new TextField<String>("textField", new LoadableDetachableModel<String>() {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings({ "rawtypes", "unchecked" })
                    @Override
                    protected String load() {

                        final Class pageClass = ZeroClipboardPanel.this.getPage().getPageClass();

                        final EntityModel entityModel = ZeroClipboardPanel.this.getModel();
                        final PageParameters pageParameters = entityModel.getPageParameters();

                        final CharSequence urlFor = getRequestCycle().urlFor(pageClass, pageParameters);
                        return getRequestCycle().getUrlRenderer().renderFullUrl(Url.parse(urlFor));
                    }
                });
                panel.add(form);
                form.add(textField);
                
                textField.setOutputMarkupId(true);

                CharSequence modalId = Strings2.escapeMarkupId(simpleClipboardModalWindow.getMarkupId());
                CharSequence textFieldId = Strings2.escapeMarkupId(textField.getMarkupId());
                target.appendJavaScript(String.format("$('#%s').one('shown.bs.modal', function(){Wicket.$('%s').select();});", modalId, textFieldId));

                simpleClipboardModalWindow.setPanel(panel, target);
                simpleClipboardModalWindow.showPrompt(target);
                
                target.focusComponent(textField);
            }
        };
    }

    

    private void addSimpleClipboardModalWindow() {
        simpleClipboardModalWindow = SimpleClipboardModalWindow.newModalWindow(ID_SIMPLE_CLIPBOARD_MODAL_WINDOW);
        addOrReplace(simpleClipboardModalWindow);
    }


    // //////////////////////////////////////

    protected PageClassRegistry getPageClassRegistry() {
        final PageClassRegistryAccessor pcra = (PageClassRegistryAccessor) getApplication();
        return pcra.getPageClassRegistry();
    }


}
