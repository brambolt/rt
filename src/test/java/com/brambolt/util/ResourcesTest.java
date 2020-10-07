package com.brambolt.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.brambolt.nio.file.Files;
import org.junit.jupiter.api.Test;

import static com.brambolt.util.Resources.createFileFromResource;
import static com.brambolt.util.Resources.createTemporaryFileFromResource;
import static com.brambolt.util.Resources.getPackagePath;
import static com.brambolt.util.Resources.stream;
import static com.brambolt.util.Resources.scan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourcesTest {

    public static final String TEXT_RESOURCE_PATH = "com/brambolt/util/text-resource.txt";

    public static final String EXPECTED_SNIPPET = "Some text to scan...";

    @Test
    public void testGetPackagePath() {
      assertEquals("com/brambolt/embedded/util", getPackagePath(ResourcesTest.class));
    }

    @Test
    public void testStreamNotNull() {
      assertNotNull(stream(TEXT_RESOURCE_PATH));
    }

    @Test
    public void testStreamNotFound() {
      // There is a leading slash, and the file won't be found:
      assertNull(stream("/com/brambolt/embedded/util/text-resource.txt"));
    }

    @Test
    public void testScan() {
        assertTrue(scan(TEXT_RESOURCE_PATH).contains(EXPECTED_SNIPPET));
    }

    @Test
    public void testScanWithBadPath() {
        assertNull(scan("bad path"));
    }

    @Test
    public void testScanWithStringCharset() {
        assertTrue(scan(TEXT_RESOURCE_PATH, "UTF-8").contains(EXPECTED_SNIPPET));
    }

    @Test
    void testCreateFileFromResource() throws IOException {
        File tmpFile = File.createTempFile("test", ".txt");
        tmpFile.deleteOnExit();
        check(createFileFromResource(TEXT_RESOURCE_PATH, tmpFile, null));
    }

    @Test
    void testCreateTemporaryFileFromResource() throws IOException {
        check(createTemporaryFileFromResource(TEXT_RESOURCE_PATH, "test", ".txt"));
    }

    void check(File created) throws IOException {
        assertNotNull(created);
        assertTrue(created.exists());
        assertTrue(created.isFile());
        assertTrue(Files
            .readString(created, StandardCharsets.UTF_8)
            .contains("... more text to scan..."));
    }
}
