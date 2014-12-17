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

package com.funkyandroid.sync;

import com.funkyandroid.sync.localfilesystem.LocalFilesystemSourceFactory;
import com.funkyandroid.sync.rackspace.cloudfiles.CloudFilesSourceFactory;

import java.io.IOException;

public class SourceFactory {

    private static final String CLOUDFILES_PREFIX = "cloudfiles:";

    private static final CloudFilesSourceFactory CLOUDFILES_SOURCE_FACTORY
                                = new CloudFilesSourceFactory();

    private static final LocalFilesystemSourceFactory LOCAL_SOURCE_FACTORY
            = new LocalFilesystemSourceFactory();


    public Source getSourceFor(final String string)
        throws IOException {
        if(string.startsWith(CLOUDFILES_PREFIX)) {
            return CLOUDFILES_SOURCE_FACTORY.getForURL(string);
        }

        return LOCAL_SOURCE_FACTORY.getForDirectory(string);
    }

}
