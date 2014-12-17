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

import com.funkyandroid.sync.Utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

class FileSystemUtils extends Utils {

    protected final File mBaseDirectory;

    FileSystemUtils(String baseDirectory)
        throws FileNotFoundException {
        mBaseDirectory = new File(baseDirectory);
        if(!mBaseDirectory.exists() || !mBaseDirectory.isDirectory()) {
            throw new FileNotFoundException("Directory "+baseDirectory+" not found.");
        }
    }


    protected byte[] calculateMD5ForFile(File localFile)
            throws IOException {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");

            try (DigestInputStream digestingStream =
                         new DigestInputStream(new BufferedInputStream(new FileInputStream(localFile)), digester)) {

                final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
                ReadableByteChannel digestingChannel = Channels.newChannel(digestingStream);

                while (digestingChannel.read(buffer) != -1) {
                    buffer.clear();
                }
            }

            return digester.digest();
        } catch(NoSuchAlgorithmException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Unable to find digest algorithm.", e);
            return null;
        }
    }

    public String toString() {
        return "Directory "+mBaseDirectory;
    }

}
