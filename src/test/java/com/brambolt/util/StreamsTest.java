package com.brambolt.util;


import org.junit.jupiter.api.Test;

import static com.brambolt.util.Resources.stream;
import static com.brambolt.util.ResourcesTest.TEXT_RESOURCE_PATH;
import static com.brambolt.util.Streams.scan;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamsTest {

    @Test
    public void testScan() {
        String content = scan(stream(TEXT_RESOURCE_PATH));
        assertTrue(content.contains("Some text to scan..."));
    }

    @Test
    public void testScanUtf8String() {
        String content = scan(stream(TEXT_RESOURCE_PATH), "UTF-8");
        assertTrue(content.contains("Some text to scan..."));
    }
}
