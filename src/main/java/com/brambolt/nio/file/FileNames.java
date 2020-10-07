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

import java.nio.file.Path;

public class FileNames {

    public static class Split { // Poor man's case class

        public static Split apply(Path path) {
            return apply(path.getFileName().toString());
        }

        public static Split apply(String fileName) {
            int offset = fileName.lastIndexOf('.');
            return ((-1 < offset)
                ? new Split(fileName.substring(0, offset), fileName.substring(offset))
                : new Split(fileName, ""));
        }

        public final String prefix;

        public final String suffix;

        public Split(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }
}
