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
package org.apache.isis.viewer.wicket.ui.components.layout.bs3.tabs;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.services.i18n.TranslationService;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.session.IsisSessionFactory;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import org.apache.isis.applib.layout.grid.bootstrap3.BS3Tab;
import org.apache.isis.applib.layout.grid.bootstrap3.BS3TabGroup;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.model.util.ComponentHintKey;
import org.apache.isis.viewer.wicket.ui.components.layout.bs3.col.RepeatingViewWithDynamicallyVisibleContent;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;

// hmmm... not sure how to make this implement HasDynamicallyVisibleContent
public class TabGroupPanel extends AjaxBootstrapTabbedPanel  {

    public static final String SESSION_ATTR_SELECTED_TAB = "selectedTab";

    // the view metadata
    private final ComponentHintKey selectedTabHintKey;
    private final EntityModel entityModel;

    private static List<ITab> tabsFor(final EntityModel entityModel) {
        final List<ITab> tabs = Lists.newArrayList();

        final BS3TabGroup tabGroup = (BS3TabGroup) entityModel.getLayoutMetadata();
        final List<BS3Tab> tablist = FluentIterable
                .from(tabGroup.getTabs())
                .filter(BS3Tab.Predicates.notEmpty())
                .toList();

        for (final BS3Tab bs3Tab : tablist) {
            final RepeatingViewWithDynamicallyVisibleContent rv = TabPanel.newRows(entityModel, bs3Tab);
            String translateContext = entityModel.getTypeOfSpecification().getFullIdentifier();
            String bs3TabName = bs3Tab.getName();
            String tabName = getTranslationService().translate(translateContext, bs3TabName);
            tabs.add(new AbstractTab(Model.of(tabName)) {
                private static final long serialVersionUID = 1L;

                @Override
                public Panel getPanel(String panelId) {
                    return new TabPanel(panelId, entityModel, bs3Tab, rv);
                }

                @Override
                public boolean isVisible() {
                    return rv.isVisible();
                }
            });
        }
        return tabs;
    }

    static TranslationService getTranslationService() {
        return IsisContext.getSessionFactory().getServicesInjector().lookupService(TranslationService.class);
    }

    public TabGroupPanel(String id, final EntityModel entityModel) {
        super(id, tabsFor(entityModel));
        this.entityModel = entityModel;

        this.selectedTabHintKey = ComponentHintKey.create(this, SESSION_ATTR_SELECTED_TAB);
    }

    @Override
    protected void onInitialize() {
        setSelectedTabFromSessionIfAny(this);
        super.onInitialize();
    }

    @Override
    public TabbedPanel setSelectedTab(final int index) {
        selectedTabHintKey.set(entityModel.getObjectAdapterMemento().asBookmark(), ""+index);
        return super.setSelectedTab(index);
    }

    private void setSelectedTabFromSessionIfAny(
            final AjaxBootstrapTabbedPanel ajaxBootstrapTabbedPanel) {
        final String selectedTabStr = selectedTabHintKey.get(entityModel.getObjectAdapterMemento().asBookmark());
        final Integer tabIndex = parse(selectedTabStr);
        if (tabIndex != null) {
            final int numTabs = ajaxBootstrapTabbedPanel.getTabs().size();
            if (tabIndex < numTabs) {
                // to support dynamic reloading; the data in the session might not be compatible with current layout.
                ajaxBootstrapTabbedPanel.setSelectedTab(tabIndex);
            }
        }
    }

    private Integer parse(final String selectedTabStr) {
        try {
            return Integer.parseInt(selectedTabStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean isVisible() {
        return FluentIterable.<AbstractTab>from(getTabs()).anyMatch(new Predicate<AbstractTab>() {
            @Override
            public boolean apply(final AbstractTab tab) {
                return tab.isVisible();
            }
        });
    }




}
