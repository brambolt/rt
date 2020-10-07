/*
 * Copyright 2017-2020 Brambolt ehf.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brambolt.util.jar;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Manifest-related utilities.
 */
public interface Manifests {

    String MANIFEST_VERSION = "Manifest-Version";

    String SIGNATURE_VERSION = "Signature-Version";

    static Path getPath(FileSystem fs) {
        return fs.getPath("META-INF/MANIFEST.MF");
    }

    static String readString(FileSystem fs) throws IOException {
        return readString(fs, StandardCharsets.UTF_8);
    }

    static String readString(FileSystem fs, Charset encoding) throws IOException {
        return com.brambolt.nio.file.Files.readString(getPath(fs), encoding);
    }

    static List<String> readAllLines(FileSystem fs) throws IOException {
        return readAllLines(fs, StandardCharsets.UTF_8);
    }

    static List<String> readAllLines(FileSystem fs, Charset encoding) throws IOException {
        return java.nio.file.Files.readAllLines(getPath(fs), encoding);
    }

    default void apply(FileSystem fs) throws IOException {
        apply(getPath(fs));
    }

    default void apply(Path manifestPath) throws IOException {
        Files.write(
            manifestPath,
            (String.join("\n",
                apply(Files.readAllLines(manifestPath))) + "\n")
                .getBytes(StandardCharsets.UTF_8),
            StandardOpenOption.TRUNCATE_EXISTING);
    }

    default String apply(String content) {
        return rejoin(apply(split(content)));
    }

    default List<String> split(String content) {
        return asList(content.split("\n"));
    }

    default String rejoin(List<String> lines) {
        return String.join("\n", lines) + "\n"; // Newline before EOF
    }

    default List<String> apply(List<String> lines) {
        return lines;
    }

    static boolean isLineAttribute(String line, String attributeName) {
        return line.trim().toLowerCase().startsWith(attributeName.toLowerCase());
    }

    static boolean hasAttribute(List<String> lines, String attributeName) {
        return lines.stream().anyMatch(line -> isLineAttribute(line, attributeName));
    }

    static boolean hasAttribute(FileSystem fs, String attributeName) throws IOException {
        return hasAttribute(readAllLines(fs), attributeName);
    }

    static boolean isManifestVersion(String line) {
        return isLineAttribute(line, MANIFEST_VERSION);
    }

    static boolean isSignatureVersion(String line) {
        return isLineAttribute(line, SIGNATURE_VERSION);
    }
}
