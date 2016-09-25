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
import java.io.OutputStream;
import java.io.Serializable;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import com.google.common.io.ByteSource;

public final class Blob implements NamedWithMimeType, Serializable {

    /**
     * Computed for state:
     * <pre>
     *     private final MimeType mimeType;
     *     private final byte[] bytes;
     *     private final String name;
     * </pre>
     */
    private static final long serialVersionUID = 5659679806709601263L;
    
    private final MimeType mimeType;
    private final byte[] bytes;
    private final String name;
    
    public Blob(String name, String primaryType, String subtype, byte[] bytes) {
        this(name, newMimeType(primaryType, subtype), bytes);
    }

    public Blob(String name, String mimeTypeBase, byte[] bytes) {
        this(name, newMimeType(mimeTypeBase), bytes);
    }

    public Blob(String name, MimeType mimeType, byte[] bytes) {
        if(name.contains(":")) {
            throw new IllegalArgumentException("Name cannot contain ':'");
        }
        this.name = name;
        this.mimeType = mimeType;
        this.bytes = bytes;
    }

    private static MimeType newMimeType(String primaryType, String subtype) {
        try {
            return new MimeType(primaryType, subtype);
        } catch (MimeTypeParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static MimeType newMimeType(String baseType) {
        try {
            return new MimeType(baseType);
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
    
    public byte[] getBytes() {
        return bytes;
    }
    
    public void writeBytesTo(final OutputStream os) throws IOException {
        ByteSource.wrap(bytes).copyTo(os);
    }

    @Override
    public String toString() {
        return getName() + " [" + getMimeType().getBaseType() + "]: " + getBytes().length + " bytes";
    }
    
}
