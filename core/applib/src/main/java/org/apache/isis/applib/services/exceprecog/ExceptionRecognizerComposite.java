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
package org.apache.isis.applib.services.exceprecog;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import com.google.common.collect.Lists;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslationService;

/**
 * Convenience implementation of {@link ExceptionRecognizer} that loops through a list of
 * {@link #add(ExceptionRecognizer) registered} services.
 * 
 * <p>
 * Note that the framework <i>does</i> allow multiple {@link ExceptionRecognizer service}s
 * to be registered in <tt>isis.properties</tt>, and each will be consulted in turn.  Therefore
 * it is not necessary to use this class to register more than one such service.  However,
 * it may be useful to use to treat a group of similar exceptions as a single unit.  For example,
 * the <i>JDO object store</i> (in its applib) provides a set of {@link ExceptionRecognizer} to
 * recognize various types of constraint violations.  These are grouped together as a single
 * set through the use of this class.
 */
public class ExceptionRecognizerComposite implements ExceptionRecognizer2 {

    private final List<ExceptionRecognizer> exceptionRecognizers = Lists.newArrayList();

    public ExceptionRecognizerComposite(final ExceptionRecognizer... exceptionRecognizers) {
        this(Arrays.asList(exceptionRecognizers));
    }
    
    public ExceptionRecognizerComposite(final List<ExceptionRecognizer> exceptionRecognizers) {
        for (final ExceptionRecognizer er : exceptionRecognizers) {
            add(er);
        }
    }

    /**
     * Register an {@link ExceptionRecognizer} to be consulted when
     * {@link #recognize(Throwable)} is called.
     * 
     * <p>
     * The most specific {@link ExceptionRecognizer recognizer}s should be registered
     * before the more general ones.  See the <i>JDO object store</i> applib for
     * an example.
     */
    @Programmatic
    public final void add(final ExceptionRecognizer ers) {
        exceptionRecognizers.add(ers);
    }
    
    /**
     * Returns the non-<tt>null</tt> message of the first {@link #add(ExceptionRecognizer) add}ed 
     * {@link ExceptionRecognizer service} that recognizes the exception. 
     */
    @Programmatic
    public final String recognize(final Throwable ex) {
        for (final ExceptionRecognizer ers : exceptionRecognizers) {
            final String message = ers.recognize(ex);
            if(message != null) {
                return message;
            }
        }
        return null;
    }


    /**
     * Returns the non-<tt>null</tt> recognition of the first {@link #add(ExceptionRecognizer) add}ed
     * (that is also an {@link org.apache.isis.applib.services.exceprecog.ExceptionRecognizer2}).
     *
     * <p>
     *     If none (as {@link org.apache.isis.applib.services.exceprecog.ExceptionRecognizer2}) recognize
     *     the exception, then falls back to using {@link #recognize(Throwable)}, returning a
     *     {@link org.apache.isis.applib.services.exceprecog.ExceptionRecognizer2.Recognition} with a
     *     category of {@link org.apache.isis.applib.services.exceprecog.ExceptionRecognizer2.Category#CLIENT_ERROR}.
     * </p>
     */
    @Programmatic
    public final Recognition recognize2(final Throwable ex) {
        for (final ExceptionRecognizer ers : exceptionRecognizers) {
            if(ers instanceof ExceptionRecognizer2) {
                final ExceptionRecognizer2 recognizer2 = (ExceptionRecognizer2) ers;
                final Recognition recognition = recognizer2.recognize2(ex);
                if(recognition != null) {
                    return recognition;
                }
            }
        }
        // backward compatible so far as possible.
        return Recognition.of(Category.OTHER, recognize(ex));
    }

    // //////////////////////////////////////

    /**
     * For recognizers already {@link #add(ExceptionRecognizer) add}ed, simply {@link #injectServices()} and {@link #initRecognizers(Map) initializes}.
     *
     * <p>
     *     Typical usage:
     * </p>
     * <pre>
     *    public void init(Map&lt;String,String> properties) {
     *        add(new ExceptionRecognizerForThisException());
     *        add(new ExceptionRecognizerForThatException());
     *        add(new ExceptionRecognizerForTheOtherException());
     *        super.init(properties);
     *    }
     * </pre>
     *
     */
    @PostConstruct
    @Override
    @Programmatic
    public void init(final Map<String, String> properties) {
        injectServices();
        initRecognizers(properties);
    }

    protected void injectServices() {
        if(container != null) {
            for (final ExceptionRecognizer ers : exceptionRecognizers) {
                container.injectServicesInto(ers);
            }
        }
    }

    protected void initRecognizers(final Map<String, String> properties) {
        for (final ExceptionRecognizer ers : exceptionRecognizers) {
            ers.init(properties);
        }
    }



    @PreDestroy
    @Override
    @Programmatic
    public void shutdown() {
        for (final ExceptionRecognizer ers : exceptionRecognizers) {
            ers.shutdown();
        }
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    DomainObjectContainer container;
    @javax.inject.Inject
    TranslationService translationService;

}
