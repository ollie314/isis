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
package org.apache.isis.applib.services.eventbus;

import java.util.EventObject;

import org.apache.isis.applib.services.i18n.TranslatableString;

/**
 * Emitted for subscribers to obtain a cssClass hint (equivalent to the <tt>title()</tt> supporting method).
 */
public abstract class TitleUiEvent<S> extends AbstractUiEvent<S> {

    private static final long serialVersionUID = 1L;

    //region > Default class
    /**
     * This class is the default for the
     * {@link org.apache.isis.applib.annotation.DomainObjectLayout#titleUiEvent()} annotation attribute.  Whether this
     * raises an event or not depends upon the "isis.reflector.facet.domainObjectLayoutAnnotation.titleUiEvent.postForDefault"
     * configuration property.
     */
    public static class Default extends TitleUiEvent<Object> {
        private static final long serialVersionUID = 1L;
    }
    //endregion

    //region > Noop class

    /**
     * Convenience class to use indicating that an event should <i>not</i> be posted (irrespective of the configuration
     * property seting for the {@link Default} event.
     */
    public static class Noop extends TitleUiEvent<Object> {
        private static final long serialVersionUID = 1L;
    }
    //endregion

    //region > Doop class

    /**
     * Convenience class meaning that an event <i>should</i> be posted (irrespective of the configuration
     * property setting for the {@link Default} event..
     */
    public static class Doop extends TitleUiEvent<Object> {
        private static final long serialVersionUID = 1L;
    }
    //endregion

    //region > constructors
    /**
     * If used then the framework will set state via (non-API) setters.
     *
     * <p>
     *     Because the {@link EventObject} superclass prohibits a null source, a dummy value is temporarily used.
     * </p>
     */
    public TitleUiEvent() {
        this(null);
    }

    public TitleUiEvent(final S source) {
        super(source);
    }

    //endregion

    //region > title
    private String title;

    /**
     * The title as provided by a subscriber using {@link #setTitle(String)}.
     *
     * <p>
     *     Note that a {@link #getTranslatableTitle()} will be used in preference, if available.
     * </p>
     */
    public String getTitle() {
        return title;
    }

    /**
     * For subscribers to call to provide a (non-translated) title for this object.
     */
    public void setTitle(final String title) {
        this.title = title;
    }
    //endregion

    //region > translatableTitle
    private TranslatableString translatableTitle;

    /**
     * The translatable (i18n) title as provided by a subscriber using {@link #setTranslatableTitle(TranslatableString)}.
     *
     * <p>
     *     If a translatable title has been provided then this will be used in preference to any
     *     {@link #getTitle() non-translatable title}.
     * </p>
     */
    public TranslatableString getTranslatableTitle() {
        return translatableTitle;
    }

    /**
     * For subscribers to call to provide a translatable (i18n) title for this object.
     */
    public void setTranslatableTitle(final TranslatableString translatableTitle) {
        this.translatableTitle = translatableTitle;
    }
    //endregion

}