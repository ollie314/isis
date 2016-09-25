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
package org.apache.isis.viewer.wicket.ui.components.actionmenu.serviceactions;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;

import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.button.DropdownAutoOpenJavaScriptReference;

/**
 * A panel responsible to render the application actions as menu in a navigation bar.
 *
 * <p>
 *     The multi-level sub menu support is borrowed from
 *     <a href="http://bootsnipp.com/snippets/featured/multi-level-dropdown-menu-bs3">Bootsnip</a>
 * </p>
 */
public class ServiceActionsPanel extends Panel {

    public ServiceActionsPanel(String id, List<CssMenuItem> menuItems) {
        super(id);
        ListView<CssMenuItem> menuItemsView = new ListView<CssMenuItem>("menuItems", menuItems) {
            @Override
            protected void populateItem(ListItem<CssMenuItem> listItem) {
                CssMenuItem menuItem = listItem.getModelObject();
                listItem.add(new Label("name", menuItem.getName()));
                MarkupContainer topMenu = new WebMarkupContainer("topMenu");
                topMenu.add(new CssClassAppender("top-menu-" + CssClassAppender.asCssStyle(menuItem.getName())));
                listItem.add(topMenu);
                List<CssMenuItem> subMenuItems = ServiceActionUtil.withSeparators(menuItem);

// fake data to test multi-level menus
//                if (menuItem.getName().equals("ToDos")) {
//                    CssMenuItem fakeItem = menuItem.newSubMenuItem("Fake item").build();
//
//                    fakeItem.newSubMenuItem("Fake item 1").link(new ExternalLink("menuLink", "http://abv.bg")).build();
//                    CssMenuItem fakeMenu12 = fakeItem.newSubMenuItem("Fake item 2").link(new ExternalLink("menuLink", "http://google.com")).build();
//
//                    fakeMenu12.newSubMenuItem("Fake item 2.1").link(new ExternalLink("menuLink", "http://web.de")).build();
//                }

                ListView<CssMenuItem> subMenuItemsView = new ListView<CssMenuItem>("subMenuItems", subMenuItems) {
                    @Override
                    protected void populateItem(ListItem<CssMenuItem> listItem) {
                        CssMenuItem subMenuItem = listItem.getModelObject();

                        if (subMenuItem.hasSubMenuItems()) {
                            addFolderItem(subMenuItem, listItem);
                        } else {

                            final MarkupContainer parent = ServiceActionsPanel.this;
                            ServiceActionUtil.addLeafItem(subMenuItem, listItem, parent);

                        }
                    }
                };
                topMenu.add(subMenuItemsView);
            }
        };
        add(menuItemsView);
    }

    private void addFolderItem(CssMenuItem subMenuItem, ListItem<CssMenuItem> listItem) {
        final MarkupContainer parent = ServiceActionsPanel.this;
        ServiceActionUtil.addFolderItem(subMenuItem, listItem, parent, ServiceActionUtil.SeparatorStrategy.WITH_SEPARATORS);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(ServiceActionsPanel.class, "ServiceActionsPanel.css")));
        response.render(JavaScriptHeaderItem.forReference(DropdownAutoOpenJavaScriptReference.instance()));
        response.render(OnDomReadyHeaderItem.forScript("$('.dropdown-toggle').dropdownHover();"));
    }

}
