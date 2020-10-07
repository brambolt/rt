package com.brambolt.util;

import java.util.Collection;

public class Require {

    public static void notEmpty(Collection<?> c) {
        assert(null != c && !c.isEmpty());
    }

    public static void notEmpty(String s) {
        assert(null != s && !s.isEmpty());
    }

    public static void notNull(Object o) {
        assert(null != o);
    }

    public static void positive(int i) {
        assert(0 < i);
    }

    public static void nonNegative(int i) {
        assert(-1 < i);
    }

    public static void negative(int i) {
        assert(i < 0);
    }

    public static void nonPositive(int i) {
        assert(i < 1);
    }
}
