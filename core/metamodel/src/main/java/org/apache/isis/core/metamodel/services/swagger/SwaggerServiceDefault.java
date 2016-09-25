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
package org.apache.isis.core.metamodel.services.swagger;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.swagger.SwaggerService;
import org.apache.isis.core.metamodel.services.swagger.internal.SwaggerSpecGenerator;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class SwaggerServiceDefault implements SwaggerService {

    @SuppressWarnings("unused")
    private final static Logger LOG = LoggerFactory.getLogger(SwaggerServiceDefault.class);

    public static final String KEY_RESTFUL_BASE_PATH = "isis.services.swagger.restfulBasePath";
    public static final String KEY_RESTFUL_BASE_PATH_DEFAULT = "/restful";

    private String basePath;

    @PostConstruct
    public void init(final Map<String,String> properties) {
        this.basePath = getPropertyElse(properties, KEY_RESTFUL_BASE_PATH, KEY_RESTFUL_BASE_PATH_DEFAULT);
    }

    static String getPropertyElse(final Map<String, String> properties, final String key, final String dflt) {
        String basePath = properties.get(key);
        if(basePath == null) {
            basePath = dflt;
        }
        return basePath;
    }

    @Programmatic
    @Override
    public String generateSwaggerSpec(
            final Visibility visibility,
            final Format format) {

        final SwaggerSpecGenerator swaggerSpecGenerator = new SwaggerSpecGenerator(specificationLoader);
        final String swaggerSpec = swaggerSpecGenerator.generate(basePath, visibility, format);
        return swaggerSpec;
    }


    @javax.inject.Inject
    SpecificationLoader specificationLoader;

}
