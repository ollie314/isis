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

package org.apache.isis.viewer.wicket.ui.components.collection.selector;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.layout.component.CollectionLayoutData;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.core.metamodel.facets.collections.collection.defaultview.DefaultViewFacet;
import org.apache.isis.core.metamodel.facets.members.render.RenderFacet;
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.isis.viewer.wicket.model.hints.UiHintContainer;
import org.apache.isis.viewer.wicket.model.models.EntityCollectionModel;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.model.util.ComponentHintKey;
import org.apache.isis.viewer.wicket.ui.ComponentFactory;
import org.apache.isis.viewer.wicket.ui.ComponentType;
import org.apache.isis.viewer.wicket.ui.app.registry.ComponentFactoryRegistry;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.CollectionContentsAsAjaxTablePanelFactory;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.multiple.CollectionContentsMultipleViewsPanelFactory;
import org.apache.isis.viewer.wicket.ui.components.collectioncontents.unresolved.CollectionContentsHiddenPanelFactory;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.List;

public class CollectionSelectorHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    static final String UIHINT_EVENT_VIEW_KEY = EntityCollectionModel.HINT_KEY_SELECTED_ITEM;

    private final EntityCollectionModel model;

    private final List<ComponentFactory> componentFactories;
    private final ComponentHintKey componentHintKey;

    public CollectionSelectorHelper(
            final EntityCollectionModel model,
            final ComponentFactoryRegistry componentFactoryRegistry) {
        this(model, componentFactoryRegistry, ComponentHintKey.<String>noop());
    }

    public CollectionSelectorHelper(
            final EntityCollectionModel model,
            final ComponentFactoryRegistry componentFactoryRegistry,
            final ComponentHintKey componentHintKey) {
        this.model = model;
        this.componentFactories = locateComponentFactories(componentFactoryRegistry);
        this.componentHintKey = componentHintKey != null ? componentHintKey : ComponentHintKey.<String>noop();
    }

    private List<ComponentFactory> locateComponentFactories(ComponentFactoryRegistry componentFactoryRegistry) {
        final List<ComponentFactory> componentFactories = componentFactoryRegistry.findComponentFactories(ComponentType.COLLECTION_CONTENTS, model);
        List<ComponentFactory> otherFactories = Lists.newArrayList(Collections2.filter(componentFactories, new Predicate<ComponentFactory>() {
            @Override
            public boolean apply(final ComponentFactory input) {
                return input.getClass() != CollectionContentsMultipleViewsPanelFactory.class;
            }
        }));
        return ordered(otherFactories);
    }

    public List<ComponentFactory> getComponentFactories() {
        return componentFactories;
    }

    public String honourViewHintElseDefault(final Component component) {
        // honour hints ...
        final UiHintContainer hintContainer = getUiHintContainer(component);
        if (hintContainer != null) {
            String viewStr = hintContainer.getHint(component, UIHINT_EVENT_VIEW_KEY);
            if (viewStr != null) {
                return viewStr;
            }
        }

        // ... else default
        String initialFactory = determineInitialFactory();
        if (hintContainer != null) {
            hintContainer.setHint(component, UIHINT_EVENT_VIEW_KEY, initialFactory);
            // don't broadcast (no AjaxRequestTarget, still configuring initial setup)
        }
        return initialFactory;
    }

    //region > helpers

    /**
     * return the index of {@link CollectionContentsHiddenPanelFactory unresolved panel} if present and not eager loading;
     * else the index of {@link org.apache.isis.viewer.wicket.ui.components.collectioncontents.ajaxtable.CollectionContentsAsAjaxTablePanelFactory ajax table} if present,
     * otherwise first factory.
     */
    private String determineInitialFactory() {

        // try to load from session, if can
        final Bookmark bookmark = domainObjectBookmarkIfAny();
        final String sessionAttribute = componentHintKey.get(bookmark);
        if(sessionAttribute != null) {
            return sessionAttribute;
        }

        // else grid layout hint
        final CollectionLayoutData layoutData = this.model.getLayoutData();
        if(layoutData != null) {
            final String defaultView = layoutData.getDefaultView();
            if(defaultView != null) {
                return defaultView;
            }
        }

        // else @CollectionLayout#defaultView attribute
        if (hasDefaultViewFacet(model)) {
            DefaultViewFacet defaultViewFacet = model.getCollectionMemento().getCollection(model.getSpecificationLoader()).getFacet(DefaultViewFacet.class);
            for (ComponentFactory componentFactory : componentFactories) {
                final String componentName = componentFactory.getName();
                final String viewName = defaultViewFacet.value();
                if (componentName.equalsIgnoreCase(viewName)) {
                    return componentName;
                }
            }
        }

        // else honour @CollectionLayout#renderEagerly
        return hasRenderEagerlyFacet(model) || model.isStandalone()
                ? CollectionContentsAsAjaxTablePanelFactory.NAME
                : CollectionContentsHiddenPanelFactory.NAME;

    }

    private Bookmark domainObjectBookmarkIfAny() {
        final EntityModel entityModel = this.model.getEntityModel();
        return entityModel != null
                ? entityModel.getObjectAdapterMemento().asBookmark()
                : null;
    }

    private static List<ComponentFactory> ordered(List<ComponentFactory> componentFactories) {
        return orderAjaxTableToEnd(componentFactories);
    }

    static List<ComponentFactory> orderAjaxTableToEnd(List<ComponentFactory> componentFactories) {
        int ajaxTableIdx = findAjaxTable(componentFactories);
        if (ajaxTableIdx >= 0) {
            List<ComponentFactory> orderedFactories = Lists.newArrayList(componentFactories);
            ComponentFactory ajaxTableFactory = orderedFactories.remove(ajaxTableIdx);
            orderedFactories.add(ajaxTableFactory);
            return orderedFactories;
        } else {
            return componentFactories;
        }
    }

    private static int findAjaxTable(List<ComponentFactory> componentFactories) {
        for (int i = 0; i < componentFactories.size(); i++) {
            if (componentFactories.get(i) instanceof CollectionContentsAsAjaxTablePanelFactory) {
                return i;
            }
        }
        return -1;
    }

    private static UiHintContainer getUiHintContainer(final Component component) {
        return UiHintContainer.Util.hintContainerOf(component, EntityCollectionModel.class);
    }

    private static boolean hasRenderEagerlyFacet(IModel<?> model) {
        final EntityCollectionModel entityCollectionModel = toEntityCollectionModel(model);
        if (entityCollectionModel == null) {
            return false;
        }

        final OneToManyAssociation collection =
                entityCollectionModel.getCollectionMemento().getCollection(entityCollectionModel.getSpecificationLoader());
        RenderFacet renderFacet = collection.getFacet(RenderFacet.class);
        return renderFacet != null && renderFacet.value() == Render.Type.EAGERLY;
    }

    private static boolean hasDefaultViewFacet(IModel<?> model) {
        final EntityCollectionModel entityCollectionModel = toEntityCollectionModel(model);
        if (entityCollectionModel == null) {
            return false;
        }

        final OneToManyAssociation collection =
                entityCollectionModel.getCollectionMemento().getCollection(entityCollectionModel.getSpecificationLoader());
        DefaultViewFacet defaultViewFacet = collection.getFacet(DefaultViewFacet.class);
        return defaultViewFacet != null;
    }

    private static EntityCollectionModel toEntityCollectionModel(IModel<?> model) {
        if (!(model instanceof EntityCollectionModel)) {
            return null;
        }

        final EntityCollectionModel entityCollectionModel = (EntityCollectionModel) model;
        if (!entityCollectionModel.isParented()) {
            return null;
        }

        return entityCollectionModel;
    }

    public ComponentFactory find(final String selected) {
        ComponentFactory componentFactory = doFind(selected);
        if (componentFactory != null) {
            return componentFactory;
        }

        final EntityCollectionModel entityCollectionModel = model;
            final String fallback;
        fallback = entityCollectionModel.isParented()
                ? CollectionContentsHiddenPanelFactory.NAME
                : CollectionContentsAsAjaxTablePanelFactory.NAME;
        componentFactory = doFind(fallback);
        if(componentFactory == null) {
            throw new IllegalStateException(String.format(
                    "Could not locate '%s' (as the fallback collection panel)",
                    fallback));
        }
        return componentFactory;
    }

    private ComponentFactory doFind(final String selected) {
        for (ComponentFactory componentFactory : componentFactories) {
            if(selected.equals(componentFactory.getName())) {
                return componentFactory;
            }
        }
        return null;
    }

    public int lookup(final String view) {
        int i=0;
        for (ComponentFactory componentFactory : componentFactories) {
            if(view.equals(componentFactory.getName())) {
                return i;
            }
            i++;
        }
        return 0;
    }

    //endregion

}
