package com.brambolt.util;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static com.brambolt.util.Require.notNull;

public class Streams {

    public static String scan(InputStream is, String charset) {
        return scan(is, Charset.forName(charset));
    }

    public static String scan(InputStream is) {
        return scan(is, StandardCharsets.UTF_8);
    }

    public static String scan(InputStream is, Charset charset) {
        notNull(is);
        notNull(charset);
        try (Scanner scan = new Scanner(is, charset.name()).useDelimiter("\\Z")) {
            return scan.next();
        }
    }
}
