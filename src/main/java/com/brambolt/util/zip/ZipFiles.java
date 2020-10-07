package com.brambolt.util.zip;

import com.brambolt.util.Streams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.brambolt.util.Strings.maxLength;

public class ZipFiles {

    public static byte[] bytes(String content, String path, Charset charset) throws IOException {
        return zip(zipper(content, path, charset), content.length());
    }

    public static byte[] bytes(String[] content, String[] paths, Charset charset) throws IOException {
       return zip(zipper(content, paths, charset), maxLength(content));
    }

    public static File file(String[] content, String[] paths, Charset charset) throws IOException {
        return file(content, paths, charset, true);
    }

    public static File file(String[] content, String[] paths, Charset charset, boolean deleteOnExit) throws IOException {
        File file = File.createTempFile("bra", ".zip");
        if (deleteOnExit)
            file.deleteOnExit();
        zip(zipper(content, paths, charset), file);
        return file;
    }

    public static String[] scan(byte[] bytes, Charset charset) throws IOException {
        return scan(new ByteArrayInputStream(bytes), charset);
    }

    public static String[] scan(InputStream is, Charset charset) throws IOException {
        List<String> scanned = new ArrayList<>();
        scan((ZipInputStream zis, FilterInputStream fis) -> scanned.add(Streams.scan(is, charset)), is);
        return scanned.toArray(new String[0]);
    }

    private static void scan(BiFunction<ZipInputStream, FilterInputStream, Boolean> scanner, InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        ZipInputStream zis = new ZipInputStream(bis);
        try (FilterInputStream fis = new FilterInputStream(zis) {
            @Override
            public void close() throws IOException {
                zis.closeEntry();
            }
        }) {
            while (null != zis.getNextEntry())
                scanner.apply(zis, fis);
        }
    }
    private static Function<ZipOutputStream, Void> zipper(String content, String path, Charset charset) {
        return (ZipOutputStream zos) -> zipEntry(zos, content, path, charset);
    }

    private static Function<ZipOutputStream, Void> zipper(String[] content, String[] paths, Charset charset ) {
        return (ZipOutputStream zos) -> {
            for (int i = 0; i < content.length; ++i) {
                zipEntry(zos, content[i], paths[i], charset);
            }
            return null;
        };
    }
    private static byte[] zip(Function<ZipOutputStream, Void> zipEntries, int bufferSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bufferSize);
        zip(zipEntries, baos);
        return baos.toByteArray();
    }

    private static void zip(Function<ZipOutputStream, Void> zipEntries, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        zip(zipEntries, fos);
    }

    /**
     * Creates a zip output stream, writes the entry lambda to the output stream,
     * and closes the output stream. The caller opens the stream, and the callee
     * closes it.
     *
     * @param zipEntries The zip entry writer lambda
     * @param os An open output stream, which will be written to, and closed
     * @throws IOException If unable to write the zip entries
     */
    private static void zip(Function<ZipOutputStream, Void> zipEntries, OutputStream os) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(os))) {
            zipEntries.apply(zos);
        }
    }

    private static Void zipEntry(ZipOutputStream zos, String content, String path, Charset charset) {
        try {
            zos.putNextEntry(new ZipEntry(path));
            zos.write(content.getBytes(charset));
            zos.closeEntry();
        } catch (IOException x) {
            throw new RuntimeException(x);
        }
        return null;
    }
}
