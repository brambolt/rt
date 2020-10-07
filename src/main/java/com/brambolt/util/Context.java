package com.brambolt.util;

import org.slf4j.Logger;

import java.util.Vector;

public class Context {

    public enum Type {

        DEBUG(false),

        ERROR(true),

        FATAL(true),

        INFO(false),

        WARN(true);

        private final boolean dirty;

        Type(boolean dirty) {
            this.dirty = dirty;
        }

        public boolean isDirty() {
            return dirty;
        }

        public boolean isClean() {
            return !isDirty();
        }
    }

    public static class Entry {

        public final Type type;

        public final Class<?> sourceClass;

        public final Throwable throwable;

        public final String message;

        public final Object[] params;

        public Entry(Type type, Class<?> sourceClass, String message, Object... params) {
            this(type, sourceClass, null, message, params);
        }

        public Entry(Type type, Class<?> sourceClass, Throwable throwable, String message, Object... params) {
            this.type = type;
            this.sourceClass = sourceClass;
            this.throwable = throwable;
            this.message = message;
            this.params = params;
        }
    }

    public static Context create(Class<?> c) {
        return create(c, null);
    }

    public static Context create(Class<?> c, Logger logger) {
        return new Context(logger);
    }

    private final Vector<Entry> entries = new Vector<>();

    private final Logger logger;

    private Context(Logger logger) {
        this.logger = logger;
    }

    public boolean isClean() {
        for (Entry entry: entries) {
            if (entry.type.isDirty())
                return false;
        }
        return true;
    }

    public void error(Class<?> c, String message, Object... params) {
        error(c, null, message, params);
    }

    public void error(Class<?> c, Throwable t) {
        error(c, "...", t);
    }

    public void error(Class<?> c, Throwable t, String message, Object... params) {
        store(Type.ERROR, c, t, message, params);
    }

    public void fatal(Class<?> c, String message, Object... params) {
        fatal(c, null, message, params);
    }

    public void fatal(Class<?> c, Throwable t) {
        fatal(c, t, "...");
    }

    public void fatal(Class<?> c, Throwable t, String message, Object... params) {
        store(Type.FATAL, c, t, message, params);
    }

    public void info(Class<?> c, String message, Object... params) {
        store(Type.INFO, c, null, message, params);
    }

    private void store(Type tp, Class<?> c, Throwable t, String message, Object... params) {
        entries.add(new Entry(tp, c, t, message, params));
    }

    public Vector<Entry> getEntries() {
        return entries;
    }

    public Logger getLogger() {
        return logger;
    }
}