package org.passwordmaker.android;

import java.io.Closeable;
import java.io.IOException;

public class StreamUtils {
    public static void closeNoThrow(Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (IOException e) {}
    }
}
