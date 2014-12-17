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

import com.funkyandroid.sync.StorageObject;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.openstack.swift.v1.features.ObjectApi;
import org.jclouds.rackspace.cloudfiles.v1.CloudFilesApi;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

class Destination extends FileSystemUtils implements com.funkyandroid.sync.Destination {

    public Destination(final String baseDirectory)
        throws FileNotFoundException {
        super(baseDirectory);
    }

    @Override
    public boolean needsUpdating(StorageObject sourceStorageObject) {
        File localFile = getFileForSourceObject(sourceStorageObject);
        if(!localFile.exists()) {
            return true;
        }

        try {
            byte[] md5 = calculateMD5ForFile(localFile);
            return !Arrays.equals(md5, sourceStorageObject.getHash());
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Unable to get local files MD5", e);
            return false;
        }
    }

    private File getFileForSourceObject(final StorageObject storageObject) {
        return new File(mBaseDirectory, storageObject.getName());
    }

    public void storeCopyOf(final StorageObject sourceObject) throws IOException {
        File file = getFileForSourceObject(sourceObject);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            FileChannel destinationChannel = fos.getChannel();

            try(InputStream inputStream = sourceObject.openInputStream()) {
                ReadableByteChannel sourceChannel  = Channels.newChannel(inputStream);

                copyBetweenChannels(sourceChannel, destinationChannel);
            } catch(IOException e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Unable to make copy of "+sourceObject.getName(), e);
            }
        } catch(IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Unable to make copy of "+sourceObject.getName(), e);
        }
    }

    @Override
    public void close() {
        // Nothing to do on close
    }
}
