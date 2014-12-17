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

import org.jclouds.ContextBuilder;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

class CloudFilesUtils {
    private static final Map<String,String> REGION_PROVIDER_MAP = new HashMap<>();
    static {
        REGION_PROVIDER_MAP.put("LON", "rackspace-cloudfiles-uk");
    }

    private static final Map<String,String> REGION_ALIASES = new HashMap<>();
    static {
        REGION_ALIASES.put("london", "LON");
    }

    private static final URLStreamHandler DUMMY_URL_HANDLER = new URLStreamHandler() {

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            throw new IOException("Not implemented");
        }
    };


    CloudFilesConnectionDetails getConnectionObjectFromUrl(final String containerUrl)
        throws MalformedURLException {
        URL connectionUrl = new URL(null, containerUrl, DUMMY_URL_HANDLER);

        String username = getUsername(connectionUrl);
        String apiKey = getApiKey(connectionUrl);
        String region = getRegion(connectionUrl);
        String provider = REGION_PROVIDER_MAP.get(region);
        String container = getContainer(connectionUrl);
        if(container == null) {
            throw new MalformedURLException("No container found in "+containerUrl);
        }

        CloudFilesApi cloudFilesApi = ContextBuilder.newBuilder(provider)
                .credentials(username, apiKey)
                .buildApi(CloudFilesApi.class);

        return new CloudFilesConnectionDetails( cloudFilesApi, region, container );
    }

    private String getRegion(URL url) {
        String region = url.getHost();
        String alias = REGION_ALIASES.get(region);
        if(alias != null) {
            return alias;
        }
        return region;
    }

    private String getUsername(URL url) {
        String userInfo = url.getUserInfo();
        if(userInfo == null) {
            return System.getProperty("CLOUDFILES_USERNAME");
        }

        int colonSeparator = userInfo.indexOf(':');
        if(colonSeparator == -1) {
            return userInfo;
        }
        return userInfo.substring(0, colonSeparator);
    }

    private String getApiKey(URL url)
            throws MalformedURLException {
        String userInfo = url.getUserInfo();
        if(userInfo == null) {
            return System.getProperty("CLOUDFILES_APIKEY");
        }

        int colonSeparator = userInfo.indexOf(':');
        if(colonSeparator == -1) {
            throw new MalformedURLException("Unable to find API Key in "+userInfo);
        }
        return userInfo.substring(colonSeparator+1);
    }

    private String getContainer(URL url) {
        String path = url.getPath();

        if(path.startsWith("/")) {
            path = path.substring(1);
        }

        int pathSeparatorIdx = path.indexOf('/');
        if(pathSeparatorIdx == -1) {
            return path;
        }

        return path.substring(0, pathSeparatorIdx);
    }

    protected byte[] convertETagToByteArray(final String eTag) {
        int eTagLength = eTag.length();
        byte[] values = new byte[eTagLength/2];

        int index = 0;
        for(int i = 0 ; i < eTag.length() ; i+=2, index++) {
            String thisPair = eTag.substring(i, i+2);
            int value = Integer.parseInt(thisPair, 16);
            values[index] = (byte)(value & 0xff);
        }

        return values;
    }

    static class CloudFilesConnectionDetails {
        final CloudFilesApi cloudFilesApi;
        final String region;
        final String container;

        CloudFilesConnectionDetails(final CloudFilesApi cloudFilesApi, final String region, final String container) {
            this.cloudFilesApi = cloudFilesApi;
            this.region = region;
            this.container = container;
        }
    }
}
