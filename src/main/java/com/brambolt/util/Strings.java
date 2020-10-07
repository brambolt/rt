package com.brambolt.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Strings {

    public static List<String> asList(String value) {
        return asList(value, ",");
    }

    public static List<String> asList(String value, String delimiter) {
        return (null != value && !value.trim().isEmpty())
            ? Arrays.asList(value.split(delimiter))
            : new ArrayList<>(); // An empty list for an empty value
    }

    public static int maxLength(String[] strings) {
        int length = 0;
        for (String s: strings) {
            int current = s.length();
            if (current > length)
                length = current;
        }
        return length;
    }
}
