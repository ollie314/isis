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

package org.apache.isis.viewer.wicket.ui.components.entity.collection;

import java.io.Serializable;
import java.util.List;

import javax.inject.Provider;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import org.apache.isis.applib.annotation.Where;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.consent.Consent;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.facets.all.named.NamedFacet;
import org.apache.isis.core.metamodel.facets.members.cssclass.CssClassFacet;
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.isis.viewer.wicket.model.links.LinkAndLabel;
import org.apache.isis.viewer.wicket.model.models.EntityCollectionModel;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.model.util.ComponentHintKey;
import org.apache.isis.viewer.wicket.ui.ComponentFactory;
import org.apache.isis.viewer.wicket.ui.components.actionmenu.entityactions.AdditionalLinksPanel;
import org.apache.isis.viewer.wicket.ui.components.collection.CollectionPanel;
import org.apache.isis.viewer.wicket.ui.components.collection.selector.CollectionSelectorHelper;
import org.apache.isis.viewer.wicket.ui.components.collection.selector.CollectionSelectorPanel;
import org.apache.isis.viewer.wicket.ui.panels.HasDynamicallyVisibleContent;
import org.apache.isis.viewer.wicket.ui.panels.PanelAbstract;
import org.apache.isis.viewer.wicket.ui.util.Components;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;

/**
 * {@link PanelAbstract Panel} representing the properties of an entity, as per
 * the provided {@link EntityModel}.
 */
public class EntityCollectionPanel extends PanelAbstract<EntityModel> implements HasDynamicallyVisibleContent {

    private static final long serialVersionUID = 1L;

    private static final String ID_COLLECTION_GROUP = "collectionGroup";
    private static final String ID_COLLECTION_NAME = "collectionName";
    private static final String ID_COLLECTION = "collection";

    private static final String ID_ADDITIONAL_LINKS = "additionalLinks";
    private static final String ID_SELECTOR_DROPDOWN = "selectorDropdown";

    private final ComponentHintKey selectedItemHintKey;

    CollectionSelectorPanel selectorDropdownPanel;

    final WebMarkupContainer div;

    public EntityCollectionPanel(final String id, final EntityModel entityModel) {
        super(id, entityModel);

        selectedItemHintKey = ComponentHintKey.create(getSelectorDropdownPanel(), EntityCollectionModel.HINT_KEY_SELECTED_ITEM);
        div = buildGui();
    }

    Provider<Component> getSelectorDropdownPanel() {
        return new SelectorDropDownPanelProvider();
    }

    /**
     * Attach UI only after added to parent.
     */
    public void onInitialize() {
        super.onInitialize();

        final WebMarkupContainer panel = this;
        if(visible) {
            panel.add(div);
        } else {
            Components.permanentlyHide(panel, div.getId());
        }

    }

    private WebMarkupContainer buildGui() {
        final WebMarkupContainer div = new WebMarkupContainer(ID_COLLECTION_GROUP);


        final EntityCollectionModel entityCollectionModel = EntityCollectionModel.createParented(getModel());
        CssClassAppender.appendCssClassTo(div, entityCollectionModel.getCollectionMemento().getId());
        CssClassAppender.appendCssClassTo(div, entityCollectionModel.getTypeOfSpecification().getFullIdentifier().replace('.','-'));

        final OneToManyAssociation association = entityCollectionModel.getCollectionMemento().getCollection(
                entityCollectionModel.getSpecificationLoader());
        final ObjectAdapter objectAdapter = getModel().getObject();
        final Consent visibility = association.isVisible(objectAdapter, InteractionInitiatedBy.USER, Where.OBJECT_FORMS);

        if(visibility.isAllowed()) {

            visible = true;

            final CssClassFacet facet = association.getFacet(CssClassFacet.class);
            if(facet != null) {
                final String cssClass = facet.cssClass(objectAdapter);
                CssClassAppender.appendCssClassTo(div, cssClass);
            }

            final CollectionPanel collectionPanel = newCollectionModel(ID_COLLECTION, entityCollectionModel);
            div.addOrReplace(collectionPanel);


            Label labelComponent = collectionPanel.createLabel(ID_COLLECTION_NAME, association.getName());
            final NamedFacet namedFacet = association.getFacet(NamedFacet.class);
            labelComponent.setEscapeModelStrings(namedFacet == null || namedFacet.escaped());
            div.add(labelComponent);

            final String description = association.getDescription();
            if(description != null) {
                labelComponent.add(new AttributeAppender("title", Model.of(description)));
            }

            final List<LinkAndLabel> links = entityCollectionModel.getLinks();
            AdditionalLinksPanel.addAdditionalLinks (div,ID_ADDITIONAL_LINKS, links, AdditionalLinksPanel.Style.INLINE_LIST);

            final CollectionSelectorHelper selectorHelper =
                    new CollectionSelectorHelper(entityCollectionModel, getComponentFactoryRegistry(),
                            selectedItemHintKey);

            final List<ComponentFactory> componentFactories = selectorHelper.getComponentFactories();

            if (componentFactories.size() <= 1) {
                permanentlyHide(ID_SELECTOR_DROPDOWN);
            } else {
                selectorDropdownPanel = new CollectionSelectorPanel(ID_SELECTOR_DROPDOWN,
                        entityCollectionModel, selectedItemHintKey);

                final Model<ComponentFactory> componentFactoryModel = new Model<>();

                final String selected = selectorHelper.honourViewHintElseDefault(selectorDropdownPanel);

                ComponentFactory selectedComponentFactory = selectorHelper.find(selected);
                componentFactoryModel.setObject(selectedComponentFactory);

                this.setOutputMarkupId(true);
                div.addOrReplace(selectorDropdownPanel);

                collectionPanel.setSelectorDropdownPanel(selectorDropdownPanel);
            }
        }
        return div;
    }

    protected CollectionPanel newCollectionModel(String id, EntityCollectionModel entityCollectionModel) {
        return new CollectionPanel(id, entityCollectionModel);
    }


    private boolean visible = false;
    @Override
    public boolean isVisible() {
        return visible;
    }

    private class SelectorDropDownPanelProvider implements Provider<Component>, Serializable {
        @Override
        public Component get() {
            return selectorDropdownPanel;
        }
    }
}
