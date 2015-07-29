/*
 * Copyright 2012 Evernote Corporation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.evernote.client.conn.mobile;


import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * Holds all the data in memory.
 *
 * @author rwondratschek
 */
public class MemoryByteStore extends ByteStore {

    protected final LazyByteArrayOutputStream mByteArrayOutputStream;

    protected int mBytesWritten;
    protected boolean mClosed;

    protected byte[] mData;

    protected MemoryByteStore() {
        mByteArrayOutputStream = new LazyByteArrayOutputStream();
    }

    @Override
    public void write(@NonNull byte[] buffer, int offset, int count) throws IOException {
        checkNotClosed();

        mByteArrayOutputStream.write(buffer, offset, count);
        mBytesWritten += count;
    }

    @Override
    public void write(int oneByte) throws IOException {
        checkNotClosed();

        mByteArrayOutputStream.write(oneByte);
        mBytesWritten++;
    }

    private void checkNotClosed() throws IOException {
        if (mClosed) {
            throw new IOException("Already closed");
        }
    }

    @Override
    public void close() throws IOException {
        if (!mClosed) {
            mByteArrayOutputStream.reset();
            mClosed = true;
        }
    }

    @Override
    public int getBytesWritten() {
        return mBytesWritten;
    }

    @Override
    public byte[] getData() throws IOException {
        if (mData != null) {
            return mData;
        }

        close();

        mData = mByteArrayOutputStream.toByteArray();
        return mData;
    }

    @Override
    public void reset() throws IOException {
        try {
            close();

        } finally {
            mData = null;
            mBytesWritten = 0;
            mClosed = false;
        }
    }

    @SuppressWarnings("unused")
    public static class Factory implements ByteStore.Factory {

        @Override
        public MemoryByteStore create() {
            return new MemoryByteStore();
        }
    }
}
