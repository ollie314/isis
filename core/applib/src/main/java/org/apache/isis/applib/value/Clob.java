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

package org.apache.isis.applib.value;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import com.google.common.io.CharSource;

public final class Clob implements NamedWithMimeType, Serializable {

    private final String name;
    private final MimeType mimeType;
    private final CharSequence chars;
    
    public Clob(String name, String primaryType, String subType, char[] chars) {
        this(name, primaryType, subType, new String(chars));
    }

    public Clob(String name, String mimeTypeBase, char[] chars) {
        this(name, mimeTypeBase, new String(chars));
    }

    public Clob(String name, MimeType mimeType, char[] chars) {
        this(name, mimeType, new String(chars));
    }

    public Clob(String name, String primaryType, String subType, CharSequence chars) {
        this(name, newMimeType(primaryType, subType), chars);
    }

    public Clob(String name, String mimeTypeBase, CharSequence chars) {
        this(name, newMimeType(mimeTypeBase), chars);
    }

    public Clob(String name, MimeType mimeType, CharSequence chars) {
        if(name.contains(":")) {
            throw new IllegalArgumentException("Name cannot contain ':'");
        }
        this.name = name;
        this.mimeType = mimeType;
        this.chars = chars;
    }

    private static MimeType newMimeType(String baseType) {
        try {
            return new MimeType(baseType);
        } catch (MimeTypeParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static MimeType newMimeType(String primaryType, String subType) {
        try {
            return new MimeType(primaryType, subType);
        } catch (MimeTypeParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getName() {
        return name;
    }
    
    public MimeType getMimeType() {
        return mimeType;
    }

    public CharSequence getChars() {
        return chars;
    }
    
    public void writeCharsTo(final Writer wr) throws IOException {
        CharSource.wrap(chars).copyTo(wr);
    }

    @Override
    public String toString() {
        return getName() + " [" + getMimeType().getBaseType() + "]: " + getChars().length() + " chars";
    }

}
