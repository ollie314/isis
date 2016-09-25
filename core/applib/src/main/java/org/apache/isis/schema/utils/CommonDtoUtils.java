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
package org.apache.isis.schema.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.schema.cmd.v1.ParamDto;
import org.apache.isis.schema.common.v1.EnumDto;
import org.apache.isis.schema.common.v1.OidDto;
import org.apache.isis.schema.common.v1.ValueDto;
import org.apache.isis.schema.common.v1.ValueType;
import org.apache.isis.schema.common.v1.ValueWithTypeDto;
import org.apache.isis.schema.utils.jaxbadapters.JavaSqlTimestampXmlGregorianCalendarAdapter;
import org.apache.isis.schema.utils.jaxbadapters.JodaDateTimeXMLGregorianCalendarAdapter;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateTimeXMLGregorianCalendarAdapter;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateXMLGregorianCalendarAdapter;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalTimeXMLGregorianCalendarAdapter;

public final class CommonDtoUtils {

    //region > PARAM_DTO_TO_NAME, PARAM_DTO_TO_TYPE

    public static final Function<ParamDto, String> PARAM_DTO_TO_NAME = new Function<ParamDto, String>() {
        @Override public String apply(final ParamDto paramDto) {
            return paramDto.getName();
        }
    };
    public static final Function<ParamDto, ValueType> PARAM_DTO_TO_TYPE = new Function<ParamDto, ValueType>() {
        @Override public ValueType apply(final ParamDto paramDto) {
            return paramDto.getType();
        }
    };
    //endregion

    //region > asValueType
    private final static ImmutableMap<Class<?>, ValueType> valueTypeByClass =
            new ImmutableMap.Builder<Class<?>, ValueType>()
                    .put(String.class, ValueType.STRING)
                    .put(byte.class, ValueType.BYTE)
                    .put(Byte.class, ValueType.BYTE)
                    .put(short.class, ValueType.SHORT)
                    .put(Short.class, ValueType.SHORT)
                    .put(int.class, ValueType.INT)
                    .put(Integer.class, ValueType.INT)
                    .put(long.class, ValueType.LONG)
                    .put(Long.class, ValueType.LONG)
                    .put(char.class, ValueType.CHAR)
                    .put(Character.class, ValueType.CHAR)
                    .put(boolean.class, ValueType.BOOLEAN)
                    .put(Boolean.class, ValueType.BOOLEAN)
                    .put(float.class, ValueType.FLOAT)
                    .put(Float.class, ValueType.FLOAT)
                    .put(double.class, ValueType.DOUBLE)
                    .put(Double.class, ValueType.DOUBLE)
                    .put(BigInteger.class, ValueType.BIG_INTEGER)
                    .put(BigDecimal.class, ValueType.BIG_DECIMAL)
                    .put(DateTime.class, ValueType.JODA_DATE_TIME)
                    .put(LocalDateTime.class, ValueType.JODA_LOCAL_DATE_TIME)
                    .put(LocalDate.class, ValueType.JODA_LOCAL_DATE)
                    .put(LocalTime.class, ValueType.JODA_LOCAL_TIME)
                    .put(java.sql.Timestamp.class, ValueType.JAVA_SQL_TIMESTAMP)
                    .build();

    public static ValueType asValueType(final Class<?> type) {
        final ValueType valueType = valueTypeByClass.get(type);
        if (valueType != null) {
            return valueType;
        }
        if (type.isEnum()) {
            return ValueType.ENUM;
        }
        // assume reference otherwise
        return ValueType.REFERENCE;
    }
    //endregion

    //region > newValueDto, setValueOn

    public static ValueDto newValueDto(
            final ValueType valueType,
            final Object val,
            final BookmarkService bookmarkService) {

        if(val == null) {
            return null;
        }

        final ValueDto valueDto = new ValueDto();
        return setValueOn(valueDto, valueType, val, bookmarkService);
    }

    public static <T extends ValueWithTypeDto> T setValueOn(
            final T valueWithTypeDto,
            final ValueType valueType,
            final Object val,
            final BookmarkService bookmarkService) {
        setValueOn((ValueDto)valueWithTypeDto, valueType, val, bookmarkService);
        valueWithTypeDto.setNull(val == null);
        return valueWithTypeDto;
    }

    public static <T extends ValueDto> T setValueOn(
            final T valueDto,
            final ValueType valueType,
            final Object val,
            final BookmarkService bookmarkService) {
        switch (valueType) {
        case STRING: {
            final String argValue = (String) val;
            valueDto.setString(argValue);
            return valueDto;
        }
        case BYTE: {
            final Byte argValue = (Byte) val;
            valueDto.setByte(argValue);
            return valueDto;
        }
        case SHORT: {
            final Short argValue = (Short) val;
            valueDto.setShort(argValue);
            return valueDto;
        }
        case INT: {
            final Integer argValue = (Integer) val;
            valueDto.setInt(argValue);
            return valueDto;
        }
        case LONG: {
            final Long argValue = (Long) val;
            valueDto.setLong(argValue);
            return valueDto;
        }
        case CHAR: {
            final Character argValue = (Character) val;
            valueDto.setChar("" + argValue);
            return valueDto;
        }
        case BOOLEAN: {
            final Boolean argValue = (Boolean) val;
            valueDto.setBoolean(argValue);
            return valueDto;
        }
        case FLOAT: {
            final Float argValue = (Float) val;
            valueDto.setFloat(argValue);
            return valueDto;
        }
        case DOUBLE: {
            final Double argValue = (Double) val;
            valueDto.setDouble(argValue);
            return valueDto;
        }
        case BIG_INTEGER: {
            final BigInteger argValue = (BigInteger) val;
            valueDto.setBigInteger(argValue);
            return valueDto;
        }
        case BIG_DECIMAL: {
            final BigDecimal argValue = (BigDecimal) val;
            valueDto.setBigDecimal(argValue);
            return valueDto;
        }
        case JODA_DATE_TIME: {
            final DateTime argValue = (DateTime) val;
            valueDto.setDateTime(JodaDateTimeXMLGregorianCalendarAdapter.print(argValue));
            return valueDto;
        }
        case JODA_LOCAL_DATE_TIME: {
            final LocalDateTime argValue = (LocalDateTime) val;
            valueDto.setLocalDateTime(JodaLocalDateTimeXMLGregorianCalendarAdapter.print(argValue));
            return valueDto;
        }
        case JODA_LOCAL_DATE: {
            final LocalDate argValue = (LocalDate) val;
            valueDto.setLocalDate(JodaLocalDateXMLGregorianCalendarAdapter.print(argValue));
            return valueDto;
        }
        case JODA_LOCAL_TIME: {
            final LocalTime argValue = (LocalTime) val;
            valueDto.setLocalTime(JodaLocalTimeXMLGregorianCalendarAdapter.print(argValue));
            return valueDto;
        }
        case JAVA_SQL_TIMESTAMP: {
            final java.sql.Timestamp argValue = (java.sql.Timestamp) val;
            valueDto.setTimestamp(JavaSqlTimestampXmlGregorianCalendarAdapter.print(argValue));
            return valueDto;
        }
        case ENUM: {
            final Enum argValue = (Enum) val;
            if(argValue == null) {
                return null;
            }
            final EnumDto enumDto = new EnumDto();
            valueDto.setEnum(enumDto);
            enumDto.setEnumType(argValue.getClass().getName());
            enumDto.setEnumName(argValue.name());
            return valueDto;
        }
        case REFERENCE: {
            final Bookmark bookmark = val instanceof Bookmark
                    ? (Bookmark) val
                    : bookmarkService.bookmarkFor(val);

            if (bookmark != null) {
                OidDto argValue = bookmark != null ? bookmark.toOidDto() : null;
                valueDto.setReference(argValue);
            }
            return valueDto;
        }
        case VOID:
            return null;
        default:
            // should never happen; all cases are listed above
            throw new IllegalArgumentException(String.format(
                    "newValueDto(): do not recognize valueType %s (likely a framework error)",
                    valueType));
        }
    }
    //endregion

    //region > getValue (from valueDto)

    public static <T> T getValue(
            final ValueDto valueDto,
            final ValueType valueType) {
        switch(valueType) {
        case STRING:
            return (T) valueDto.getString();
        case BYTE:
            return (T) valueDto.getByte();
        case SHORT:
            return (T) valueDto.getShort();
        case INT:
            return (T) valueDto.getInt();
        case LONG:
            return (T) valueDto.getLong();
        case FLOAT:
            return (T) valueDto.getFloat();
        case DOUBLE:
            return (T) valueDto.getDouble();
        case BOOLEAN:
            return (T) valueDto.isBoolean();
        case CHAR:
            final String aChar = valueDto.getChar();
            if(Strings.isNullOrEmpty(aChar)) { return null; }
            return (T) (Object)aChar.charAt(0);
        case BIG_DECIMAL:
            return (T) valueDto.getBigDecimal();
        case BIG_INTEGER:
            return (T) valueDto.getBigInteger();
        case JAVA_SQL_TIMESTAMP:
            return (T) JavaSqlTimestampXmlGregorianCalendarAdapter.parse(valueDto.getDateTime());
        case JODA_DATE_TIME:
            return (T) JodaDateTimeXMLGregorianCalendarAdapter.parse(valueDto.getDateTime());
        case JODA_LOCAL_DATE:
            return (T) JodaLocalDateXMLGregorianCalendarAdapter.parse(valueDto.getLocalDate());
        case JODA_LOCAL_DATE_TIME:
            return (T) JodaLocalDateTimeXMLGregorianCalendarAdapter.parse(valueDto.getLocalDateTime());
        case JODA_LOCAL_TIME:
            return (T) JodaLocalTimeXMLGregorianCalendarAdapter.parse(valueDto.getLocalTime());
        case ENUM:
            final EnumDto enumDto = valueDto.getEnum();
            final String enumType = enumDto.getEnumType();
            final Class<? extends Enum> enumClass = loadClassElseThrow(enumType);
            return (T) Enum.valueOf(enumClass, enumDto.getEnumName());
        case REFERENCE:
            return (T) valueDto.getReference();
        case VOID:
            return null;
        default:
            // should never happen; all cases are listed above
            throw new IllegalArgumentException(String.format(
                    "getValueDto(...): do not recognize valueType %s (likely a framework error)",
                    valueType));
        }
    }

    private static <T> Class<T> loadClassElseThrow(final String enumType) {
        try {
            return (Class<T>) loadClass(enumType);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> loadClass(String className) throws ClassNotFoundException {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        if(ccl == null) {
            return loadClass(className, (ClassLoader)null);
        } else {
            try {
                return loadClass(className, ccl);
            } catch (ClassNotFoundException var3) {
                return loadClass(className, (ClassLoader)null);
            }
        }
    }

    private static Class<?> loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        return classLoader == null?Class.forName(className):Class.forName(className, true, classLoader);
    }
    //endregion

    //region > newValueWithTypeDto


    public static ValueWithTypeDto newValueWithTypeDto(
            final Class<?> type,
            final Object val,
            final BookmarkService bookmarkService) {

        final ValueWithTypeDto valueWithTypeDto = new ValueWithTypeDto();

        final ValueType valueType = asValueType(type);
        valueWithTypeDto.setType(valueType);

        setValueOn(valueWithTypeDto, valueType, val, bookmarkService);

        return valueWithTypeDto;
    }

    //endregion

    //region > getValue (from ValueWithTypeDto)

    public static <T> T getValue(final ValueWithTypeDto valueWithTypeDto) {
        if(valueWithTypeDto.isNull()) {
            return null;
        }
        final ValueType type = valueWithTypeDto.getType();
        return CommonDtoUtils.getValue(valueWithTypeDto, type);
    }


    //endregion


    //region > newParamDto

    public static ParamDto newParamDto(
            final String parameterName,
            final Class<?> parameterType,
            final Object arg,
            final BookmarkService bookmarkService) {

        final ParamDto paramDto = new ParamDto();

        paramDto.setName(parameterName);

        final ValueType valueType = CommonDtoUtils.asValueType(parameterType);
        paramDto.setType(valueType);

        CommonDtoUtils.setValueOn(paramDto, valueType, arg, bookmarkService);

        return paramDto;
    }
    //endregion

    //region > getValue (from ParamDto)

    public static <T> T getValue(final ParamDto paramDto) {
        if(paramDto.isNull()) {
            return null;
        }
        final ValueType parameterType = paramDto.getType();
        return CommonDtoUtils.getValue(paramDto, parameterType);
    }

    //endregion




}
