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
package org.apache.isis.core.runtime.services.eventbus;

import java.util.Collections;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.Subscribe;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class EventBusServiceDefaultTest {

    EventBusServiceDefault eventBusService;

    @Before
    public void setUp() throws Exception {
        eventBusService = new EventBusServiceDefault() {
        };
    }

    public static class Init extends EventBusServiceDefaultTest {

        @Test
        public void emptyMap() throws Exception {
            eventBusService.init(Collections.<String, String>emptyMap());

            assertThat(eventBusService.getImplementation(), is("guava"));
            assertThat(eventBusService.isAllowLateRegistration(), is(false));
        }

        @Test
        public void allowLateRegistration_setToFalse() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_ALLOW_LATE_REGISTRATION, "false"));
            assertThat(eventBusService.isAllowLateRegistration(), is(false));
        }

        @Test
        public void allowLateRegistration_setToTrue() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_ALLOW_LATE_REGISTRATION, "true"));
            assertThat(eventBusService.isAllowLateRegistration(), is(true));
        }

        @Test
        public void allowLateRegistration_setToTrueMixedCase() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_ALLOW_LATE_REGISTRATION, "TrUe"));
            assertThat(eventBusService.isAllowLateRegistration(), is(true));
        }

        @Test
        public void allowLateRegistration_setToEmptyString() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_ALLOW_LATE_REGISTRATION, ""));
            assertThat(eventBusService.isAllowLateRegistration(), is(false));
        }

        @Test
        public void allowLateRegistration_setToGarbage() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_ALLOW_LATE_REGISTRATION, "SDF$%FDVDFG"));
            assertThat(eventBusService.isAllowLateRegistration(), is(false));
        }

        @Test
        public void implementation_setToGuava() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_EVENT_BUS_IMPLEMENTATION, "guava"));
            assertThat(eventBusService.getImplementation(), is("guava"));
        }

        @Test
        public void implementation_setToGuavaMixedCaseRequiringTrimming() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_EVENT_BUS_IMPLEMENTATION, "  GuAvA "));
            assertThat(eventBusService.getImplementation(), is("guava"));
        }

        @Test
        public void implementation_setToAxon() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_EVENT_BUS_IMPLEMENTATION, "axon"));
            assertThat(eventBusService.getImplementation(), is("axon"));
        }

        @Test
        public void implementation_setToAxonMixedCaseRequiringTrimming() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_EVENT_BUS_IMPLEMENTATION, " AxOn   "));
            assertThat(eventBusService.getImplementation(), is("axon"));
        }

        @Test
        public void implementation_setToEmptyString() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_EVENT_BUS_IMPLEMENTATION, ""));
            assertThat(eventBusService.getImplementation(), is("guava"));
        }

        @Test
        public void implementation_setToAnythingElse() throws Exception {
            eventBusService.init(ImmutableMap.of(EventBusServiceDefault.KEY_EVENT_BUS_IMPLEMENTATION,
                    "com.mycompany.my.event.bus.Implementation"));
            assertThat(eventBusService.getImplementation(), is("com.mycompany.my.event.bus.Implementation"));
        }

    }

    public static class Post extends EventBusServiceDefaultTest {

        public static class Subscriber {
            Object obj;
            @Subscribe
            public void on(Object obj) {
                this.obj = obj;
            }
        }

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        Subscriber subscriber;

        @Before
        public void setUp() throws Exception {
            super.setUp();
            subscriber = new Subscriber();
        }

        @Test
        public void allow_late_registration_means_can_register_after_post() throws Exception {
            // given
            eventBusService.init(ImmutableMap.of(
                    EventBusServiceDefault.KEY_ALLOW_LATE_REGISTRATION, "true",
                    EventBusServiceDefault.KEY_EVENT_BUS_IMPLEMENTATION, "guava"));
            assertThat(eventBusService.isAllowLateRegistration(), is(true));
            assertThat(eventBusService.getImplementation(), is("guava"));

            eventBusService.post(new Object());

            // when
            eventBusService.register(subscriber);

            // then
            assertThat(subscriber.obj, is(nullValue()));
        }

        @Test
        public void disallow_late_registration_means_cannot_register_after_post() throws Exception {
            // given
            eventBusService.init(ImmutableMap.of(
                    EventBusServiceDefault.KEY_ALLOW_LATE_REGISTRATION, "false",
                    EventBusServiceDefault.KEY_EVENT_BUS_IMPLEMENTATION, "guava"));
            assertThat(eventBusService.isAllowLateRegistration(), is(false));
            assertThat(eventBusService.getImplementation(), is("guava"));

            eventBusService.post(new Object());

            // expect
            expectedException.expect(IllegalStateException.class);

            // when
            eventBusService.register(new Subscriber());
        }

        @Test
        public void disallow_late_registration_means_can_register_before_post() throws Exception {
            // given
            eventBusService.init(ImmutableMap.of(
                    EventBusServiceDefault.KEY_ALLOW_LATE_REGISTRATION, "false",
                    EventBusServiceDefault.KEY_EVENT_BUS_IMPLEMENTATION, "guava"));
            assertThat(eventBusService.isAllowLateRegistration(), is(false));
            assertThat(eventBusService.getImplementation(), is("guava"));

            eventBusService.register(subscriber);

            // when
            final Object event = new Object();
            eventBusService.post(event);

            // then
            assertThat(subscriber.obj, is(event));
        }

    }
}