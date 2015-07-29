package com.evernote.client.conn.mobile;

import java.io.ByteArrayOutputStream;

/**
 * @author rwondratschek
 */
/*package*/ class LazyByteArrayOutputStream extends ByteArrayOutputStream {

    @Override
    public synchronized byte[] toByteArray() {
        return buf;
    }
}
