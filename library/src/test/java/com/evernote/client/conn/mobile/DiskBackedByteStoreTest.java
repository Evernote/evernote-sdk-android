package com.evernote.client.conn.mobile;

import android.annotation.SuppressLint;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rwondratschek
 */
@FixMethodOrder(MethodSorters.JVM)
public class DiskBackedByteStoreTest {

    private static final int ONE_MB = 1024 * 1024;

    protected void testRandomLengthInMemory(DiskBackedByteStore byteStore, int length) throws IOException {
        byte[] buffer = createRandomFilledBuffer(length);

        byteStore.write(buffer);
        assertThat(byteStore.swapped()).isFalse();

        assertThat(byteStore.getData()).isNotNull().isEqualTo(buffer);
    }

    private void testRandomLengthSwap(DiskBackedByteStore byteStore, int length) throws IOException {
        byte[] buffer = createRandomFilledBuffer(length);

        byteStore.write(buffer);
        assertThat(byteStore.swapped()).isFalse();

        byte[] newBuffer = Arrays.copyOf(buffer, buffer.length + 1);

        byteStore.write(newBuffer[newBuffer.length - 1]); // another byte
        assertThat(byteStore.swapped()).isTrue();

        assertThat(byteStore.getData()).isNotNull().hasSize(newBuffer.length).isEqualTo(newBuffer);
    }

    @SuppressLint("Assert")
    private void testRandomLengthClose(DiskBackedByteStore byteStore, int length) throws IOException {
        byte[] buffer = createRandomFilledBuffer(length);

        byteStore.write(buffer);
        byteStore.getData();

        try {
            byteStore.write(1);
            assert false; // don't reach

        } catch (IOException ignored) {
        }

        byteStore.reset();
        byteStore.write(8398490);
        assertThat(byteStore.getBytesWritten()).isEqualTo(1);
    }

    @Test
    public void testOnlyInMemory() throws IOException {
        for (int i = 0; i < 10; i++) {
            int length = (int) (Math.random() * ONE_MB);
            DiskBackedByteStore byteStore = new DiskBackedByteStore(null, length); // no cache dir
            testRandomLengthInMemory(byteStore, length);
        }
    }

    @Test
    public void testSwap() throws IOException {
        int length = ONE_MB;
        DiskBackedByteStore byteStore = new DiskBackedByteStore(getCacheDir(), length);
        testRandomLengthSwap(byteStore, length);
    }

    @Test
    public void testClose() throws IOException {
        int length = ONE_MB;
        DiskBackedByteStore byteStore = new DiskBackedByteStore(getCacheDir(), length);
        testRandomLengthClose(byteStore, length);
    }

    @Test
    @Ignore
    public void testRepetition() throws IOException {
        int length = ONE_MB;
        DiskBackedByteStore byteStore = new DiskBackedByteStore(getCacheDir(), length);

        for (int i = 0; i < 20; i++) {
            if (i % 3 == 0) {
                testRandomLengthInMemory(byteStore, length);
            } else if (i % 3 == 1) {
                testRandomLengthSwap(byteStore, length);
            } else {
                testRandomLengthClose(byteStore, Math.random() > 0.5 ? ONE_MB : (ONE_MB + 1));
            }
            byteStore.reset();
        }
    }

    @Test
    @Ignore
    public void testParallelism() throws InterruptedException, ExecutionException, TimeoutException {
        int threadCount = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount * 3; i++) {
            Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        testRepetition();
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                }
            });

            futures.add(future);
        }

        for (Future<Boolean> future : futures) {
            assertThat(future.get(1, TimeUnit.MINUTES)).isTrue();
        }
    }

    private static byte[] createRandomFilledBuffer(int size) {
        Random random = new Random();
        byte[] buffer = new byte[size];

        int start = random.nextInt(size);
        int end = Math.min(start + 1 + random.nextInt(buffer.length), buffer.length);
        Arrays.fill(buffer, start, end, (byte) random.nextInt(255));

        return buffer;
    }

    private static File getCacheDir() {
        return new File(System.getProperty("java.io.tmpdir", "."));
    }
}
