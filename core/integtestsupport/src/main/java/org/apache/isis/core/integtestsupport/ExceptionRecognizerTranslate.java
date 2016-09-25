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
package org.apache.isis.core.integtestsupport;

import java.util.List;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.apache.isis.applib.services.exceprecog.ExceptionRecognizer;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.session.IsisSessionFactory;

public class ExceptionRecognizerTranslate implements MethodRule {

    public static ExceptionRecognizerTranslate create() {
        return new ExceptionRecognizerTranslate();
    }
    private ExceptionRecognizerTranslate(){}


    @Override
    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
        return new TranslationStatement(statement);
    }

    private class TranslationStatement extends Statement {
        private final Statement next;

        public TranslationStatement(final Statement base) {
            this.next = base;
        }

        public void evaluate() throws Throwable {
            try {
                this.next.evaluate();
            } catch (final Throwable ex) {
                recognize(ex);
                throw ex;
            }
        }
    }

    /**
     * Simply invokes {@link org.apache.isis.applib.services.exceprecog.ExceptionRecognizer#recognize(Throwable)} for all registered {@link org.apache.isis.applib.services.exceprecog.ExceptionRecognizer}s for the provided exception, so that the message will be translated.
     */
    private void recognize(final Throwable ex) {
        final List<ExceptionRecognizer> exceptionRecognizers = getIsisSessionFactory().getServicesInjector().lookupServices(ExceptionRecognizer.class);
        for (final ExceptionRecognizer exceptionRecognizer : exceptionRecognizers) {
            final String unused = exceptionRecognizer.recognize(ex);
        }
    }

    IsisSessionFactory getIsisSessionFactory() {
        return IsisContext.getSessionFactory();
    }

}
