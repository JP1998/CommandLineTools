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

package de.hotzjeanpierre.commandlinetools.command.utils;

import de.hotzjeanpierre.commandlinetools.command.parameter.*;
import de.hotzjeanpierre.commandlinetools.command.utils.files.FileNamingTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class is used to convert values from their string representation
 * back into their type. To support types that are not by default supported
 * you'll have to extend this class and parse the given type yourself.
 *
 * @see Converter#convert(String, Type)
 */
public class Converter {

    private static final Pattern sFileNamePattern = Pattern.compile("^([A-Z]:)?[^<>:\"|?*]*$");

    /**
     * <p>This method converts an object from its String representation into its
     * actual type. It only supports a limited (though extensive enough
     * to be working for most basic programs) amount of types. Those types are {@code String}, {@code File}
     * {@code boolean}, {@code byte}, {@code short}, {@code char}, {@code int}, {@code long},
     * {@code float}, {@code double} and any kind of {@code enum}.</p>
     * <p>To support any kind of type that may not be mentioned above, you'll have to
     * extend this class, and override this method. This though requires you to keep
     * a few limitations.
     * <ul>
     * <li>you should never throw any kind of exception from this method</li>
     * <li>in case any error occurs you should return {@code null}</li>
     * <li>you should only parse the custom types you are trying to support,
     * and if the given type is not one of those you should call the super-method accordingly</li>
     * <li>To keep this method better organized you should make an own method for each possible
     * type to convert to. Optionally you could keep this parse-method in the data-class you're trying to support.</li>
     * </ul></p>
     * As a quick example implementation:
     * <pre><code>
     * <span style="color:#CC7832;">package</span> com.example.exampleproject.commands<span style="color:#CC7832;">;</span>
     *
     * <span style="color:#CC7832;">import</span> de.hotzjeanpierre.commandlinetools.command.utils.Converter<span style="color:#CC7832;">;</span>
     *
     * <span style="color:#CC7832;">public class</span> CustomConverter <span style="color:#CC7832;">extends</span> Converter {
     *
     *     <span style="color:#BBB529;">&#64;Override</span>
     *     <span style="color:#CC7832;">public</span> Object convert(String representation<span style="color:#CC7832;">,</span> Class toConvertTo) {
     *         <span style="color:#CC7832;">if</span>(toConvertTo.equals(CustomType1.<span style="color:#CC7832;">class</span>)) {
     *             <span style="color:#CC7832;">return</span> CustomType1.parse(representation)<span style="color:#CC7832;">;</span>
     *         } <span style="color:#CC7832;">else if</span>(toConvertTo.equals(CustomType2.<span style="color:#CC7832;">class</span>)) {
     *         <span style="color:#808080;">// etc...</span>
     *         } <span style="color:#CC7832;">else</span> {
     *             <span style="color:#CC7832;">return super</span>.convert(representation<span style="color:#CC7832;">,</span> toConvertTo)<span style="color:#CC7832;">;</span>
     *         }
     *     }
     * }
     * </code></pre>
     *
     * @param representation the value to convert
     * @param toConvertTo    the type to convert the given String into
     * @return the value represented by given string converted to the given type
     */
    @Nullable
    @SuppressWarnings({"unchecked"})
    public Object convert(String representation, Type toConvertTo) {
        if (CommonTypes.String.equals(toConvertTo)) {                     // String
            return representation;
        } else if (CommonTypes.File.equals(toConvertTo)) {                // File
            if(!sFileNamePattern.matcher(representation).matches()) {
                return null;
            } else {
                return new File(representation);
            }
        } else if (CommonTypes.FileNamingTemplate.equals(toConvertTo)) {       // FileNamingTemplate
            try {
                return FileNamingTemplate.parse(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (CommonTypes.Primitives.Boolean.equals(toConvertTo)) {             // boolean
            if(representation.trim().equalsIgnoreCase("true")) {
                return true;
            } else if(representation.trim().equalsIgnoreCase("false")) {
                return false;
            } else {
                return null;
            }
        } else if (CommonTypes.Primitives.Double.equals(toConvertTo)) {              // double
            try {
                return Double.parseDouble(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (CommonTypes.Primitives.Float.equals(toConvertTo)) {               // float
            try {
                return Float.parseFloat(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (CommonTypes.Primitives.Byte.equals(toConvertTo)) {                // byte
            try {
                return Byte.parseByte(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (CommonTypes.Primitives.Short.equals(toConvertTo)) {               // short
            try {
                return Short.parseShort(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (CommonTypes.Primitives.Character.equals(toConvertTo)) {           // char
            if (representation.length() > 1) {
                return null;
            }
            return representation.charAt(0);
        } else if (CommonTypes.Primitives.Integer.equals(toConvertTo)) {             // int
            try {
                return Integer.parseInt(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (CommonTypes.Primitives.Long.equals(toConvertTo)) {                // long
            try {
                return Long.parseLong(representation);
            } catch (Exception e) {
                return null;
            }
        } else if(toConvertTo.isArray()) {

            return parseArray(
                    ((ArrayType) toConvertTo).getContainedType(),
                    StringProcessing.tokenizeArray(representation),
                    ((ArrayType) toConvertTo).getDimensions(),
                    0
            ).value;

        } else if (toConvertTo.isEnum()) {   // any enum
            try {
                return Enum.valueOf(((EnumType) toConvertTo).getEnumType(), representation);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    /**
     * This method tries to parse an array which is assumed to be of correct syntax.
     * Nested arrays will be handled by this method aswell as any type incompatibilities
     * or dimensional issues witht the resulting array.
     *
     * @param elementType the type the elements of the resulting array should have
     * @param representation the tokenized array as specified by {@link StringProcessing#tokenizeArray(String)}
     * @param dim the number of dimensions the resulting array should have
     * @param i the index at which this method should start parsing. From outside you
     *          should always give this parameter the value 0
     * @return the result of the trial of parsing the given tokens;
     *         will contain {@code (-1, null)} if there was an error
     *         will contain the index of the last processed token and
     *         the array this parsing has resulted in otherwise
     */
    @NotNull
    private ArrayParseResult parseArray(Type elementType, @NotNull String[] representation, int dim, int i) {
        if(!representation[i].equals("{")) {
            return new ArrayParseResult(-1, null);
        }

        List<Object> elements = new ArrayList<>();

        i++;

        while(i < representation.length && !representation[i].equals("}")) {

            if(dim > 1 && representation[i].equals("{")) {
                ArrayParseResult result = this.parseArray(elementType, representation, dim - 1, i);

                if(result.value == null) {
                    return new ArrayParseResult(-1, null);
                } else {
                    elements.add(result.value);
                    i = result.i;
                }

            } else if(dim > 1) {
                return new ArrayParseResult(-1, null);
            }

            if(dim == 1) {
                Object element = this.convert(representation[i], elementType);

                if(elementType == null) {
                    return new ArrayParseResult(-1, null);
                } else {
                    elements.add(element);
                }
            }

            i++;
        }

        Object[] elementArray = new Object[elements.size()];
        elementArray = elements.toArray(elementArray);

        return new ArrayParseResult(i, new Array(elementType, dim, elementArray));
    }

    /**
     * This class represents the result of the trial of parsing an array.
     */
    private static class ArrayParseResult {
        /**
         * The index at which the parsing has stopped
         */
        public int i;
        /**
         * The object in which the parsing has resulted in
         */
        public Object value;

        ArrayParseResult(int i, Object value) {
            this.i = i;
            this.value = value;
        }
    }
}
