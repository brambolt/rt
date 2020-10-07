package com.brambolt.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import static com.brambolt.text.Templates.bind;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Convenience utilities for working with class path resources.
 */
public class Resources {

    /**
     * Converts the package name of the parameter class to a resource path.
     * @param cls The class to produce the package path for
     * @return The resource path
     */
    public static String getPackagePath(Class<?> cls) {
        return getPackagePath(cls.getPackage());
    }

    /**
     * Produces the resource path for the parameter package.
     * @param pkg The package to produce the resource path for
     * @return The resource path for the package
     */
    public static String getPackagePath(Package pkg) {
        return getPackagePath(pkg.getName());
    }

    public static String getPackagePath(String packageName) {
        return getResourcePathForPackage(packageName);
    }

    public static String getResourcePathForPackage(String packageName) {
        // There is a leading slash:
        return packageName.replaceAll("\\.", "/");
    }

    /**
     * Opens an input stream for the parameter resource path, if the resource exists.
     *
     * @param path An absolute resource path
     * @return An input stream for the parameter path, if the resource exists, else null
     */
     public static InputStream stream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    public static String scan(String path, String charset) {
        return scan(path, Charset.forName(charset));
    }

    public static String scan(String path) {
         return scan(path, StandardCharsets.UTF_8);
    }

    public static String scan(String path, Charset charset) {
        try (InputStream is = stream(path)) {
            return Streams.scan(is, charset);
        } catch (Throwable t) {
            return null;
        }
    }

    public static File createFileFromResource(String resourcePath, String filePath, Map<?, ?> bindings, String charset) throws IOException {
        return createFileFromResource(resourcePath, new File(filePath), bindings, charset);
    }

    public static File createTemporaryFileFromResource(String resourcePath, String prefix, String suffix) throws IOException {
         return createTemporaryFileFromResource(resourcePath, prefix, suffix, null, UTF_8, true);
    }

    public static File createTemporaryFileFromResource(String resourcePath, String prefix, String suffix, Map<String, String> bindings, Charset charset, boolean deleteOnExit) throws IOException {
        File tmpFile = File.createTempFile(prefix, suffix);
        if (deleteOnExit)
            tmpFile.deleteOnExit();
        return createFileFromResource(resourcePath, tmpFile, bindings, charset);
    }

    public static File createFileFromResource(String resourcePath, File targetFile, Map<?, ?> bindings, String charset) throws IOException {
        return createFileFromResource(resourcePath, targetFile, bindings, Charset.forName(charset));
    }

    public static File createFileFromResource(String resourcePath, File targetFile, Map<?, ?> bindings, Charset charset) throws IOException {
        String content = scan(resourcePath, charset);
        if (null == content)
            throw new IllegalArgumentException("No resource found: " + resourcePath);
        String bound = null != bindings && !bindings.isEmpty() ? bind(content, bindings) : content;
        Files.write(targetFile.toPath(), bound.getBytes()); // CREATE, TRUNC, WRITE options
        return targetFile;
    }

    public static File createFileFromResource(String resourcePath, String filePath, Map<?, ?> bindings) throws IOException {
        return createFileFromResource(resourcePath, new File(filePath), bindings, UTF_8.name());
    }

    public static File createFileFromResource(String resourcePath, File targetFile, Map<?, ?> bindings) throws IOException {
        return createFileFromResource(resourcePath, targetFile, bindings, UTF_8.name());
    }

    public static File createFileFromResource(String resourcePath, File targetFile) throws IOException {
        return createFileFromResource(resourcePath, targetFile, null, UTF_8.name());
    }
}
