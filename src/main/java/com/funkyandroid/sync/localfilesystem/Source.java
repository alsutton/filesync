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

package com.funkyandroid.sync.localfilesystem;

import com.funkyandroid.sync.Synchronizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

class Source extends FileSystemUtils  implements com.funkyandroid.sync.Source {
    private final URI mBaseDirectoryUri;

    public Source(final String baseDirectory) throws FileNotFoundException {
        super(baseDirectory);
        mBaseDirectoryUri = mBaseDirectory.toURI();
    }

    @Override
    public void close() {
        // Nothing to do on close
    }

    @Override
    public void synchronizeUsing(Synchronizer synchronizer, boolean deleteAfterSync) throws IOException {
        synchronize(synchronizer, deleteAfterSync, mBaseDirectory);
    }

    private void synchronize(Synchronizer synchronizer, boolean deleteAfterSync, File file) throws IOException {
        if(file.isDirectory()) {
            processDirectory(synchronizer, deleteAfterSync, file);
            return;
        }

        StorageObject storageObject = createStorageObjectFromFile(file);
        synchronizer.synchronize(storageObject);
        if(deleteAfterSync) {
            file.delete();
        }
    }

    private void processDirectory(Synchronizer synchronizer, boolean deleteAfterSync, File directory) throws IOException {
        File[] files = directory.listFiles();
        if(files == null) {
            throw new IOException("Unable to sync from "+directory.getAbsolutePath());
        }

        for(File file : files) {
            synchronize(synchronizer, deleteAfterSync, file);
        }
    }

    private StorageObject createStorageObjectFromFile(final File file) throws IOException {
        byte[] md5Hash = calculateMD5ForFile(file); // Cloudfiles uses the object MD5 as the eTag
        String relativePath = mBaseDirectoryUri.relativize(file.toURI()).getPath();
        return new StorageObject(this, relativePath, md5Hash, file);
    }
}
