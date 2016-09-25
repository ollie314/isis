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
package org.apache.isis.viewer.wicket.ui.components.widgets.favicon;

import com.google.inject.name.Named;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.util.string.Strings;

/**
 * A component for application favorite icon
 */
public class Favicon extends WebComponent {

    @com.google.inject.Inject(optional = true)
    @Named("faviconUrl")
    private String url;

    @com.google.inject.Inject(optional = true)
    @Named("faviconContentType")
    private String contentType;

    public Favicon(String id) {
        super(id);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        setVisible(!Strings.isEmpty(url));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        tag.put("href", url);

        if (!Strings.isEmpty(contentType)) {
            tag.put("type", contentType);
        }
    }
}
