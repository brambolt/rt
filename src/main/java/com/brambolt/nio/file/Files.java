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

package com.brambolt.nio.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Files {

    /**
     * <p>Reads an entire file into a string.</p>
     *
     * <p>With Java 11+, use <code>java.nio.file.Files.readString</code>.</p>
     *
     * @param path The path to read from
     * @param encoding The encoding to read with
     * @return The content at the parameter path as a string in the encoding
     * @throws IOException If unable to read the string content
     */
    public static String readString(String path, Charset encoding) throws IOException {
        return readString(Paths.get(path), encoding);
    }

    /**
     * <p>Reads an entire file into a string.</p>
     *
     * <p>With Java 11+, use <code>java.nio.file.Files.readString</code>.</p>
     *
     * @param path The path to read from
     * @param encoding The encoding to read with
     * @return The content at the parameter path as a string in the encoding
     * @throws IOException If unable to read the string content
     */
    public static String readString(Path path, Charset encoding) throws IOException {
        return new String(
            java.nio.file.Files.readAllBytes(path),
            encoding);
    }

    /**
     * <p>Reads an entire file into a string.</p>
     *
     * <p>With Java 11+, use <code>java.nio.file.Files.readString</code>.</p>
     *
     * @param file The file to read from
     * @param encoding The encoding to read with
     * @return The content at the parameter path as a string in the encoding
     * @throws IOException If unable to read the string content
     */
    public static String readString(File file, Charset encoding) throws IOException {
        return readString(file.toPath(), encoding);
    }

    public static Path createTempCopy(Path path) throws IOException {
        FileNames.Split fileName = FileNames.Split.apply(path);
        Path tmpPath = java.nio.file.Files.createTempFile(fileName.prefix, fileName.suffix);
        java.nio.file.Files.copy(path, tmpPath, StandardCopyOption.REPLACE_EXISTING);
        return tmpPath;
    }
}
