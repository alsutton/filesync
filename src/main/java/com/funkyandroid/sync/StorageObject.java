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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public abstract class StorageObject implements Comparable<StorageObject> {

    protected final String mName;
    protected final byte[] mHash;

    protected Source mSource;

    public StorageObject(final Source source, final String name, final byte[] hash) {
        mSource = source;
        mName = name;
        mHash = hash;
    }

    public String getName() {
        return mName;
    }

    public byte[] getHash() {
        return mHash;
    }

    @Override
    public int compareTo(StorageObject o) {
        if(!Arrays.equals(mHash, o.mHash)) {
            return -1;
        }

        return mName.compareTo(o.mName);
    }

    public abstract InputStream openInputStream() throws IOException;
}
