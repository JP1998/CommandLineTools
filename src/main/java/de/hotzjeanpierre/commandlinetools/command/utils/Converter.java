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

import de.hotzjeanpierre.commandlinetools.command.utils.arrays.ArrayHelper;
import de.hotzjeanpierre.commandlinetools.command.utils.files.FileNamingTemplate;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * This class is used to convert values from their string representation
 * back into their type. To support types that are not by default supported
 * you'll have to extend this class and parse the given type yourself.
 *
 * @see Converter#convert(String, Class)
 */
public class Converter {

    /**
     * <p>This method converts an object from its String representation into its
     * actual type. Though it only supports a limited (though extensive enough
     * to be working for mostbasic programs) amount of types. Those types are {@code String}, {@code File}
     * {@code boolean}, {@code byte}, {@code short}, {@code char}, {@code int}, {@code long},
     * {@code float}, {@code double} and any kind of {@code enum}.</p>
     * <p>To support any kind of type that may not be mentioned above, you'll have to
     * extend this class, and override this method. This though requires you to keep
     * a few limitations.
     * <ul>
     * <li>you should never throw any kind of exception from this method</li>
     * <li>in case any error occurs you should retunrn {@code null}</li>
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
    public Object convert(String representation, Class toConvertTo) {
        if (String.class.equals(toConvertTo)) {                     // String
            return representation;
        } else if (File.class.equals(toConvertTo)) {                // File
            if(ArrayHelper.containsAny(
                    ArrayHelper.cast(representation.toCharArray()),
                    '<', '>', ':', '"', '|', '?', '*'
            )) {
                return null;
            } else {
                return new File(representation);
            }
        } else if (toConvertTo == FileNamingTemplate.class) {       // FileNamingTemplate
            try {
                return FileNamingTemplate.parse(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (Boolean.class.equals(toConvertTo)) {             // boolean
            if(representation.trim().equalsIgnoreCase("true")) {
                return true;
            } else if(representation.trim().equalsIgnoreCase("false")) {
                return false;
            } else {
                return null;
            }
        } else if (Double.class.equals(toConvertTo)) {              // double
            try {
                return Double.parseDouble(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (Float.class.equals(toConvertTo)) {               // float
            try {
                return Float.parseFloat(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (Byte.class.equals(toConvertTo)) {                // byte
            try {
                return Byte.parseByte(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (Short.class.equals(toConvertTo)) {               // short
            try {
                return Short.parseShort(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (Character.class.equals(toConvertTo)) {           // char
            if (representation.length() > 1) {
                return null;
            }
            return representation.charAt(0);
        } else if (Integer.class.equals(toConvertTo)) {             // int
            try {
                return Integer.parseInt(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (Long.class.equals(toConvertTo)) {                // long
            try {
                return Long.parseLong(representation);
            } catch (Exception e) {
                return null;
            }
        } else if (toConvertTo != null && toConvertTo.isEnum()) {   // any enum
            try {
                return Enum.valueOf(toConvertTo, representation);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

}
