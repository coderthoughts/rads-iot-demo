package org.coderthoughts.radsiotdemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Streams {
    private Streams() {}

    public static void pump(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[8192];

        int length = 0;
        int offset = 0;

        while ((length = is.read(bytes, offset, bytes.length - offset)) != -1) {
            offset += length;

            if (offset == bytes.length) {
                os.write(bytes, 0, bytes.length);
                offset = 0;
            }
        }
        if (offset != 0) {
            os.write(bytes, 0, offset);
        }
    }

    public static byte [] suck(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            pump(is, baos);
            return baos.toByteArray();
        } finally {
            is.close();
        }
    }
}