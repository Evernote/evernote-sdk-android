package com.evernote.client.conn.mobile;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Holds the data until it's written to the server.
 *
 * @author rwondratschek
 * @see MemoryByteStore
 * @see DiskBackedByteStore
 */
public abstract class ByteStore extends OutputStream {

    /**
     * @return The number of bytes which this instance is holding at the moment.
     */
    public abstract int getBytesWritten();

    /**
     * The returned byte buffer very likely has a different size than the bytes written. If you want
     * to read the data from the buffer only use the first {@link #getBytesWritten()} bytes with no
     * offset.
     *
     * @return A byte array containing the data from the byte store.
     */
    public abstract byte[] getData() throws IOException;

    /**
     * Reset all pointers.
     */
    public abstract void reset() throws IOException;

    /**
     * A factory to create a byte store.
     */
    public interface Factory {
        /**
         * @return A new instance.
         */
        ByteStore create();
    }
}
