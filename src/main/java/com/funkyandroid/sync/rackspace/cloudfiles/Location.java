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

import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;

class Location extends CloudFilesUtils {

    protected CloudFilesApi mCloudFilesApi;
    protected ObjectApi mObjectApi;

    private final String mStringRepresentation;

    public Location(final CloudFilesApi cloudFilesApi, final String region, final String container) {
        mCloudFilesApi = cloudFilesApi;
        mObjectApi = cloudFilesApi.getObjectApi(region, container);

        mStringRepresentation = "Container "+container+" in "+region;
    }

    @Override
    public String toString() {
        return mStringRepresentation;
    }
}
