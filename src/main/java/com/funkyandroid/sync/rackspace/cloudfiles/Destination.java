/*
 * Copyright (c) 2014 Funky Android Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.funkyandroid.sync.rackspace.cloudfiles;

import com.funkyandroid.sync.StorageObject;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

class Destination extends Location implements com.funkyandroid.sync.Destination {

    public Destination(CloudFilesApi cloudFilesApi, String region, String container) {
        super(cloudFilesApi, region, container);
    }

    @Override
    public boolean needsUpdating(StorageObject sourceStorageObject) {
        SwiftObject object = mObjectApi.getWithoutBody(sourceStorageObject.getName());
        if(object == null) {
            return true;
        }

        String eTag = object.getETag(); // For cloud files the eTag is the MD5 hash of the contents.
        byte[] md5 = convertETagToByteArray(eTag);
        return !Arrays.equals(md5, sourceStorageObject.getHash());
    }

    @Override
    public void storeCopyOf(StorageObject sourceObject) throws IOException {
        try (InputStream stream = sourceObject.openInputStream()) {
            Payload payload = Payloads.newInputStreamPayload(stream);
            mObjectApi.put(sourceObject.getName(), payload);
        }
    }

    @Override
    public void close() {
        try {
            mCloudFilesApi.close();
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.INFO, "Problem closing cloudfile connection.", e);
        }
    }
}
