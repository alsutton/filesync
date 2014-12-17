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

import com.funkyandroid.sync.Synchronizer;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.openstack.swift.v1.options.ListContainerOptions;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;

import java.util.logging.Level;
import java.util.logging.Logger;

class Source extends Location implements com.funkyandroid.sync.Source {

    public Source(CloudFilesApi cloudFilesApi, String region, String container) {
        super(cloudFilesApi, region, container);
    }

    @Override
    public void close() {
        try {
            mCloudFilesApi.close();
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.INFO, "Problem closing cloudfile connection.", e);
        }
    }

    @Override
    public void synchronizeUsing(Synchronizer synchronizer, boolean deleteAfterSync) {
        for(SwiftObject object : mObjectApi.list(ListContainerOptions.NONE)) {
            StorageObject storageObject = createStorageObjectFromSwiftObject( object );
            synchronizer.synchronize(storageObject);
            if(deleteAfterSync) {
                mObjectApi.delete(object.getName());
            }
        }
    }

    private StorageObject createStorageObjectFromSwiftObject(final SwiftObject object) {
        String eTag = object.getETag();
        byte[] md5Hash = convertETagToByteArray(eTag); // Cloudfiles uses the object MD5 as the eTag
        return new StorageObject(this, object.getName(), md5Hash);
    }

    ObjectApi getObjectApi() {
        return mObjectApi;
    }
}
