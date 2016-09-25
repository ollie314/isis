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
package org.apache.isis.core.runtime.services.command;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.command.Command;
import org.apache.isis.applib.services.command.CommandDefault;
import org.apache.isis.applib.services.command.spi.CommandService;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class CommandServiceDefault implements CommandService {

    @Programmatic
    @Override
    public Command create() {
        return new CommandDefault();
    }

    @Deprecated
    @Programmatic
    @Override
    public void startTransaction(final Command command, final UUID transactionId) {
        // nothing to do.
    }

    @Programmatic
    @Override
    public void complete(final Command command) {
        // nothing to do
    }


    @Programmatic
    @Override
    public boolean persistIfPossible(final Command command) {
        return false;
    }

    @javax.inject.Inject
    ClockService clockService;

}
