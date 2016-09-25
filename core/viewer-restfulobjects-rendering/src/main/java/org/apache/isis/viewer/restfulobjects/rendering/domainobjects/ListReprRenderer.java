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
package org.apache.isis.viewer.restfulobjects.rendering.domainobjects;

import java.util.Collection;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.viewer.restfulobjects.applib.JsonRepresentation;
import org.apache.isis.viewer.restfulobjects.applib.Rel;
import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;
import org.apache.isis.viewer.restfulobjects.rendering.LinkFollowSpecs;
import org.apache.isis.viewer.restfulobjects.rendering.RendererContext;
import org.apache.isis.viewer.restfulobjects.rendering.ReprRendererAbstract;

public class ListReprRenderer extends ReprRendererAbstract<ListReprRenderer, Collection<ObjectAdapter>> {

    private ObjectAdapterLinkTo linkTo;
    private Collection<ObjectAdapter> objectAdapters;
    private ObjectSpecification elementType;
    private ObjectSpecification returnType;
    private Rel elementRel;

    public ListReprRenderer(final RendererContext resourceContext, final LinkFollowSpecs linkFollower, final JsonRepresentation representation) {
        super(resourceContext, linkFollower, RepresentationType.LIST, representation);
        usingLinkToBuilder(new DomainObjectLinkTo());
    }

    public ListReprRenderer usingLinkToBuilder(final ObjectAdapterLinkTo objectAdapterLinkToBuilder) {
        this.linkTo = objectAdapterLinkToBuilder.usingUrlBase(rendererContext);
        return this;
    }

    @Override
    public ListReprRenderer with(final Collection<ObjectAdapter> objectAdapters) {
        this.objectAdapters = objectAdapters;
        return this;
    }

    public ListReprRenderer withElementRel(Rel elementRel) {
        this.elementRel = elementRel;
        return this;
    }

    public ListReprRenderer withReturnType(final ObjectSpecification returnType) {
        this.returnType = returnType;
        return this;
    }

    public ListReprRenderer withElementType(final ObjectSpecification elementType) {
        this.elementType = elementType;
        return this;
    }

    @Override
    public JsonRepresentation render() {

        if(representation == null) {
            return null;
        }

        addValue();

        addLinkToReturnType();
        addLinkToElementType();

        getExtensions();

        return representation;
    }

    private void addValue() {
        if (objectAdapters == null) {
            return;
        }

        final JsonRepresentation values = JsonRepresentation.newArray();

        for (final ObjectAdapter adapter : objectAdapters) {
            final ObjectSpecification specification = adapter.getSpecification();
            if (specification.isHidden()) {
                continue;
            }
            final JsonRepresentation linkToObject = linkTo.with(adapter).builder(elementRel).build();
            values.arrayAdd(linkToObject);

            final LinkFollowSpecs linkFollower = getLinkFollowSpecs().follow("value");
            if (linkFollower.matches(linkToObject)) {
                final DomainObjectReprRenderer renderer = new DomainObjectReprRenderer(getRendererContext(), linkFollower, JsonRepresentation.newMap()
                );
                final JsonRepresentation domainObject = renderer.with(adapter).render();
                linkToObject.mapPut("value", domainObject);
            }
        }
        representation.mapPut("value", values);
    }


    protected void addLinkToReturnType() {
        addLink(Rel.RETURN_TYPE, returnType);
    }

    protected void addLinkToElementType() {
        addLink(Rel.ELEMENT_TYPE, elementType);
    }

}