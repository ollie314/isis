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

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.spec.feature.ObjectMember;
import org.apache.isis.viewer.restfulobjects.applib.Rel;
import org.apache.isis.viewer.restfulobjects.applib.RepresentationType;
import org.apache.isis.viewer.restfulobjects.rendering.LinkBuilder;
import org.apache.isis.viewer.restfulobjects.rendering.RendererContext;
import org.apache.isis.viewer.restfulobjects.rendering.util.OidUtils;

public class DomainObjectLinkTo implements ObjectAdapterLinkTo {

    protected RendererContext rendererContext;
    protected ObjectAdapter objectAdapter;

    @Override
    public final DomainObjectLinkTo usingUrlBase(final RendererContext resourceContext) {
        this.rendererContext = resourceContext;
        return this;
    }

    @Override
    public ObjectAdapterLinkTo with(final ObjectAdapter objectAdapter) {
        this.objectAdapter = objectAdapter;
        return this;
    }

    @Override
    public LinkBuilder builder() {
        return builder(null);
    }

    @Override
    public LinkBuilder builder(final Rel rel) {
        final LinkBuilder linkBuilder = LinkBuilder.newBuilder(rendererContext, relElseDefault(rel).getName(), RepresentationType.DOMAIN_OBJECT, linkRef(new StringBuilder()).toString());
        linkBuilder.withTitle(objectAdapter.titleString());
        return linkBuilder;
    }

    /**
     * hook method
     */
    protected StringBuilder linkRef(StringBuilder buf) {
        String domainType = OidUtils.getDomainType(objectAdapter);
        String instanceId = OidUtils.getInstanceId(objectAdapter);
        return buf.append("objects/").append(domainType).append("/").append(instanceId);
    }

    protected Rel relElseDefault(final Rel rel) {
        return rel != null ? rel : defaultRel();
    }

    /**
     * hook method; used by {@link #builder(Rel)}.
     */
    protected Rel defaultRel() {
        return Rel.VALUE;
    }

    @Override
    public final LinkBuilder memberBuilder(final Rel rel, final MemberType memberType, final ObjectMember objectMember, final String... parts) {
        return memberBuilder(rel, memberType, objectMember, memberType.getRepresentationType(), parts);
    }

    @Override
    public final LinkBuilder memberBuilder(final Rel rel, final MemberType memberType, final ObjectMember objectMember, final RepresentationType representationType, final String... parts) {
        final StringBuilder buf = linkRef(new StringBuilder());
        buf.append("/").append(memberType.getUrlPart()).append(objectMember.getId());
        for (final String part : parts) {
            if (part == null) {
                continue;
            }
            buf.append("/").append(part);
        }
        final String url = buf.toString();
        return LinkBuilder.newBuilder(rendererContext, rel.andParam(memberType.getName(), objectMember.getId()), representationType, url);
    }


    

}