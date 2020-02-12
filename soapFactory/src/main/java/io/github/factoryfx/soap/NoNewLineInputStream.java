package io.github.factoryfx.soap;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class NoNewLineInputStream extends FilterInputStream {

    /**
     * Creates a <code>FilterInputStream</code> by assigning the  argument <code>in</code> to the field <code>this.in</code> so as to remember it for later use.
     *
     * @param in
     *     the underlying input stream, or <code>null</code> if this instance is to be created without an underlying stream.
     */
    protected NoNewLineInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int c;
        do {
            c = in.read();
        } while ((c == '\n' || c == '\r' || c == '\t'));
        return c;
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);
        if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;

        int i = 1;
        try {
            for (; i < len ; i++) {
                c = read();
                if (c == -1) {
                    break;
                }
                b[off + i] = (byte)c;
            }
        } catch (IOException ignore) {}
        return i;
    }

}
