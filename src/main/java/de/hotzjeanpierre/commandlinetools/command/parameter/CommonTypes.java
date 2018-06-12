/*
 *     Copyright 2018 Jean-Pierre Hotz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hotzjeanpierre.commandlinetools.command.parameter;

public final class CommonTypes {

    private CommonTypes() {}

    public static final class Primitives {

        private Primitives() {}

        public static final PrimitiveType Boolean = PrimitiveType.BOOLEAN;
        public static final PrimitiveType Double = PrimitiveType.DOUBLE;
        public static final PrimitiveType Float = PrimitiveType.FLOAT;
        public static final PrimitiveType Byte = PrimitiveType.BYTE;
        public static final PrimitiveType Short = PrimitiveType.SHORT;
        public static final PrimitiveType Character = PrimitiveType.CHAR;
        public static final PrimitiveType Integer = PrimitiveType.INT;
        public static final PrimitiveType Long = PrimitiveType.LONG;

    }

    public static final ObjectType String = new ObjectType(java.lang.String.class);
    public static final ObjectType File = new ObjectType(java.io.File.class);
    public static final ObjectType FileNamingTemplate = new ObjectType(de.hotzjeanpierre.commandlinetools.command.utils.files.FileNamingTemplate.class);

    public static final EnumType FilterMode = new EnumType(de.hotzjeanpierre.commandlinetools.command.utils.files.FilterMode.class);

}
