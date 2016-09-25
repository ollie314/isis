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

package org.apache.isis.core.commons.lang;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import org.apache.isis.core.commons.exceptions.IsisException;

public final class ClassExtensions {


    // //////////////////////////////////////


    private ClassExtensions() {
    }

    public static Object newInstance(final Class<?> extendee, final Class<?> constructorParamType, final Object constructorArg) {
        return ClassExtensions.newInstance(extendee, new Class[] { constructorParamType }, new Object[] { constructorArg });
    }

    /**
     * Tries to instantiate using a constructor accepting the supplied
     * arguments; if no such constructor then falls back to trying the no-arg
     * constructor.
     */
    public static Object newInstance(final Class<?> extendee, final Class<?>[] constructorParamTypes, final Object[] constructorArgs) {
        try {
            Constructor<?> constructor;
            try {
                constructor = extendee.getConstructor(constructorParamTypes);
                return constructor.newInstance(constructorArgs);
            } catch (final NoSuchMethodException ex) {
                try {
                    constructor = extendee.getConstructor();
                    return constructor.newInstance();
                } catch (final NoSuchMethodException e) {
                    throw new IsisException(e);
                }
            }
        } catch (final SecurityException ex) {
            throw new IsisException(ex);
        } catch (final IllegalArgumentException e) {
            throw new IsisException(e);
        } catch (final InstantiationException e) {
            throw new IsisException(e);
        } catch (final IllegalAccessException e) {
            throw new IsisException(e);
        } catch (final InvocationTargetException e) {
            throw new IsisException(e);
        }
    }

    public static String getSuperclass(final Class<?> extendee) {
        final Class<?> superType = extendee.getSuperclass();
    
        if (superType == null) {
            return null;
        }
        return superType.getName();
    }

    public static boolean isAbstract(final Class<?> extendee) {
        return Modifier.isAbstract(extendee.getModifiers());
    }

    public static boolean isFinal(final Class<?> extendee) {
        return Modifier.isFinal(extendee.getModifiers());
    }

    public static boolean isPublic(final Class<?> extendee) {
        return Modifier.isPublic(extendee.getModifiers());
    }

    public static boolean isJavaClass(final Class<?> extendee) {
        final String className = extendee.getName();
        return className.startsWith(ClassUtil.JAVA_CLASS_PREFIX) || 
               extendee.getName().startsWith("sun.");
    }

    static Class<?> implementingClassOrNull(final Class<?> extendee, final Class<?> requiredClass, final Class<?> constructorParamType) {
        if (extendee == null) {
            return null;
        }
        if (!requiredClass.isAssignableFrom(extendee)) {
            return null;
        }
        try {
            extendee.getConstructor(new Class[] { constructorParamType });
        } catch (final NoSuchMethodException ex) {
            try {
                extendee.getConstructor(new Class[] {});
            } catch (final NoSuchMethodException e) {
                return null;
            }
        } catch (final SecurityException e) {
            return null;
        }
        final int modifiers = extendee.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            return null;
        }
        return extendee;
    }

    public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterClass) throws NoSuchMethodException {
        return clazz.getMethod(methodName, parameterClass);
    }

    public static Method getMethodElseNull(final Class<?> clazz, final String methodName, final Class<?>... parameterClass) {
        try {
            return clazz.getMethod(methodName, parameterClass);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    public static Method findMethodElseNull(final Class<?> clazz, final String[] candidateMethodNames, final Class<?>... parameterClass) {
        for (final String candidateMethodName : candidateMethodNames) {
            final Method method = getMethodElseNull(clazz, candidateMethodName, parameterClass);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    public static Properties resourceProperties(final Class<?> extendee, final String suffix) {
        try {
            final URL url = Resources.getResource(extendee, extendee.getSimpleName()+suffix);
            final ByteSource byteSource = Resources.asByteSource(url);
            final Properties properties = new Properties();
            properties.load(byteSource.openStream());
            return properties;
        } catch (Exception e) {
            return null;
        }
    }

    public static String resourceContent(final Class<?> cls, final String suffix) throws IOException {
        final String resourceName = cls.getSimpleName() + suffix;
        return resourceContentOf(cls, resourceName);
    }

    public static String resourceContentOf(final Class<?> cls, final String resourceName) throws IOException {
        final URL url = Resources.getResource(cls, resourceName);
        return Resources.toString(url, Charset.defaultCharset());
    }

    public static boolean exists(final Class<?> cls, final String resourceName) {
        final URL url = Resources.getResource(cls, resourceName);
        return url != null;
    }

    public static Class<?> asWrapped(final Class<?> primitiveClassExtendee) {
        return ClassUtil.wrapperClasses.get(primitiveClassExtendee);
    }

    public static Class<? extends Object> asWrappedIfNecessary(final Class<?> cls) {
        return cls.isPrimitive() ? asWrapped(cls) : cls;
    }

    public static Object toDefault(final Class<?> extendee) {
        if(!extendee.isPrimitive()) {
            return null;
        }
        return ClassUtil.defaultByPrimitiveClass.get(extendee);
    }

    /**
     * Returns the corresponding 'null' value for the primitives, or just
     * <tt>null</tt> if the class represents a non-primitive type.
     */
    public static Object getNullOrDefault(final Class<?> type) {
        return ClassUtil.defaultByPrimitiveType.get(type);
    }

    public static boolean isCompatibleAsReturnType(final Class<?> returnTypeExtendee, final boolean canBeVoid, final Class<?> type) {
        if (returnTypeExtendee == null) {
            return true;
        }
        if (canBeVoid && (type == void.class)) {
            return true;
        }
    
        if (type.isPrimitive()) {
            return returnTypeExtendee.isAssignableFrom(ClassUtil.wrapperClasses.get(type));
        }
    
        return (returnTypeExtendee.isAssignableFrom(type));
    }

}
