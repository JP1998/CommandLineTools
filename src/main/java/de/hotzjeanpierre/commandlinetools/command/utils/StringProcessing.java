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
import de.hotzjeanpierre.commandlinetools.command.utils.exceptions.StringProcessingFormatException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a Utility class for processing strings. It contains a method to easily format
 * strings and one that tokenizes a String into the tokens needed for a command.
 */
public class StringProcessing {

    /**
     * The pattern to validate the syntax of a formatting template with.
     */
    private static final Pattern sFormatValidatorPattern =
            Pattern.compile("^([^{}]|(\\{\\{)|(}}))*(\\{\\s*\\d+\\s*}([^{}]|(\\{\\{)|(}}))*)*$");

    /**
     * The pattern to extract wildcards from a formatting template with.
     */
    private static final Pattern sWildcardExtractionPattern =
            Pattern.compile("(\\{\\{)|(}})|(\\{\\s*\\d+\\s*})");

    /**
     * This method makes it easy for you to insert values of variables into a string,
     * by using a format. The formatter for now only supports the inserting of the toString-value
     * of an object given. This can be accomplished by using the wildcard-syntax '{i}' to insert
     * the toString-value of the object given at index i of the replacements parameters.
     * Since curly braces are used for wildcards you'll have to escape them by typing them double.
     * Thus the method-call {@code StrinProcessing.format("{{ - {0} - }}", "Hello");} will produce
     * the result {@code "{ - Hello - }"}.
     * <table>
     *     <tr>
     *         <th style="padding:5px; border: 1px solid black;">Format example</th>
     *         <th style="padding:5px; border: 1px solid black;">Focus on</th>
     *         <th style="padding:5px; border: 1px solid black;">Description</th>
     *     </tr>
     *     <tr>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>some string</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>N/A</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;">Any normal string (except some special cases as explained below) will simply be taken into the result at the specified position.</td>
     *     </tr>
     *     <tr>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>some {{ - string - }}</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>"{{" and "}}"</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;">Since curly braces are used to qualify a wildcard you need to escape curly braces you want printed in the result.</td>
     *     </tr>
     *     <tr>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>I want some {0}.</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;"><pre><code>"{0}"</code></pre></td>
     *         <td style="padding:5px; border: 1px solid black;">A simple wildcard. The wildcard will be replaced by the toString-value of the object given at the index written within the curly braces.</td>
     *     </tr>
     * </table>
     *
     * @param format       the format with the syntax as specified above.
     * @param replacements the objects whose values are to be inserted into the wildcards.
     * @return the formatted string.
     * @throws StringProcessingFormatException in case the syntax is not valid.
     */
    @NotNull
    public static String format(String format, Object... replacements)
            throws StringProcessingFormatException {
        if (sFormatValidatorPattern.matcher(format).matches()) {
            StringBuilder resultBuilder = new StringBuilder();

            Matcher wildcards = sWildcardExtractionPattern.matcher(format);

            boolean found = wildcards.find();

            resultBuilder.append(format.substring(0,
                    (found) ? wildcards.start() : format.length()));

            while (found) {
                // replace while saving end
                String replacement = parseWildcard(wildcards.group(), replacements);
                resultBuilder.append(replacement);
                int start = wildcards.end();
                // find next
                found = wildcards.find();
                // add plain text in between
                resultBuilder.append(format.substring(start,
                        (found) ? wildcards.start() : format.length()));
            }

            return resultBuilder.toString();
        } else {
            throw new StringProcessingFormatException(
                    "The given format is not valid. Please refer to the documentation for further information.");
        }
    }

    /**
     * This method parses a wildcard and returns the value that is to be written into the wildcard.
     * It also handles special formatting (which is soon to be implemented) like 0- or whitespace-padding,
     * binary, octal or hexadecimal formatting of numbers and such.
     *
     * @param wildcard     the wildcard that is to be parsed
     * @param replacements the replacements that were given into the format-method
     * @return the text that is to be put into the wildcards position
     */
    private static String parseWildcard(@NotNull String wildcard, Object[] replacements) {
        switch (wildcard) {
            case "{{": return "{";
            case "}}": return "}";
            default:
                int replacementIndex = Integer.parseInt(wildcard.substring(1, wildcard.length() - 1).trim());

                if (replacementIndex >= replacements.length) {
                    throw new StringProcessingFormatException(StringProcessing.format(
                            "Format requires more replacements than you gave us. Expected (at least):{0}; Given:{1}",
                            replacementIndex,
                            replacements.length
                    ));
                }

                Object replacement = replacements[replacementIndex];
                return (replacement != null)? replacement.toString() : "null";
        }
    }

    /**
     * This method tokenizes a string into single tokens according to the syntax of a command.
     *
     * @param command the string representing a command that is to be tokenized
     * @return the single tokens in the string
     */
    @NotNull
    public static String[] tokenizeCommand(String command) {
        if (!validateCommand(command)) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "Command '{0}' does not conform to the syntax and can thus not be parsed.", command
            ));
        }

        List<String> tokens = new ArrayList<>();

        int start = 0;
        int end = 0;

        boolean inString;
        boolean commandNameChecked = false;

        int i = 0;

        while (i < command.length()) {
            while (i < command.length() && Character.isWhitespace(command.charAt(i))){
                i++;
            }
            // from here on the pointer (i) will be at the first non-whitespace character
            if(i == command.length()) {
                break;
            }
            start = i;

            if(!commandNameChecked) {
                // we're now at the beginning of the commands name
                i++;
                while (i < command.length() && !Character.isWhitespace(command.charAt(i))) {
                    i++;
                }
                commandNameChecked = true;
                end = i;
                tokens.add(command.substring(start, end));
            } else if(command.charAt(i) == '"') {
                // validate string (with escape sequences)
                inString = true;
                i++;
                while (i < command.length() && inString) {
                    if(command.charAt(i) == '\\') {
                        i += 2;
                    } else if(command.charAt(i) == '"') {
                        inString = false;
                        i++;
                    } else {
                        i++;
                    }
                }

                end = i;
                tokens.add(descape(command.substring(start + 1, end - 1)));
            } else {
                // "validate" any characters in a simple string
                while (i < command.length() && !Character.isWhitespace(command.charAt(i))) {
                    i++;
                }
                end = i;
                tokens.add(command.substring(start, end));
            }
        }

        String[] tokensArray = new String[tokens.size()];
        return tokens.toArray(tokensArray);
    }

    private static boolean validateCommand(String command) {
        if(command.trim().isEmpty()) {
            return false;
        }

        boolean inString;
        boolean commandNameChecked = false;

        int i = 0;

        while (i < command.length()) {
            while (i < command.length() && Character.isWhitespace(command.charAt(i))){
                i++;
            }
            // from here on the pointer (i) will be at the first non-whitespace character
            if(i == command.length()) {
                return true;
            }

            if(!commandNameChecked) {
                // validate the name of the command which will always be the first word in the command
                if(!Pattern.compile("[_a-zA-Z]").matcher("" + command.charAt(i)).matches()){
                    return false;
                }
                i++;
                while (i < command.length() && !Character.isWhitespace(command.charAt(i))) {
                    if(!Pattern.compile("[_a-zA-Z0-9]").matcher("" + command.charAt(i)).matches()) {
                        return false;
                    }
                    i++;
                }
                commandNameChecked = true;
            } else if(command.charAt(i) == '"') {
                // validate string (with escape sequences)
                inString = true;
                i++;
                while (i < command.length() && inString) {
                    if(command.charAt(i) == '\\') {
                        if(i < command.length() - 1 && ArrayHelper.containsAny(
                                new Character[] {'t', 'b', 'n', 'r', 'f', '\'', '\"', '\\'},
                                command.charAt(i + 1)
                        )) {
                            i += 2;
                        } else {
                            return false;
                        }
                    } else if(command.charAt(i) == '"') {
                        inString = false;
                        i++;
                    } else {
                        i++;
                    }
                }

                if(i == command.length() && inString) {
                    return false;
                }
            } else {
                // "validate" any characters in a simple string
                while (i < command.length() && !Character.isWhitespace(command.charAt(i))) {
                    i++;
                }
            }
        }

        return true;
    }

    /**
     * Descapes any exscape-sequence inside a string. Only supports standard escape sequences like '\n' or '\t'.
     *
     * @param str the string containing escape sequences
     * @return the string with descaped escape-sequences
     */
    @NotNull
    private static String descape(@NotNull String str) {
        return str.replace("\\t", "\t").replace("\\b", "\b").replace("\\n", "\n")
                .replace("\\r", "\r").replace("\\f", "\f").replace("\\'", "\'")
                .replace("\\\"", "\"").replace("\\\\", "\\");
    }

    /**
     * This method brings a given numbers string representation to a certain length by zero padding it.
     * This also takes into account any negative numbers. If you want the number not to be zero padded,
     * you could either not call this method, or simply give 0 or 1 as the numbers length.
     *
     * @param number the number to pad to the given length
     * @param length the length the given number should be padded to
     * @return the padded representation of the number
     */
    @NotNull
    public static String zeroPadding(int number, int length) {
        boolean negative = number < 0;

        if (negative) {
            length--;
        }

        StringBuilder result = new StringBuilder(
                Integer.toString(Math.abs(number))
        );

        while (result.length() < length) {
            result.insert(0, "0");
        }

        if (negative) {
            result.insert(0, "-");
        }

        return result.toString();
    }

    /**
     * This method stretches the given string to the given length while using
     * the given character as a filler. This character is appended or prefixed
     * depending on the value of {@code insertBefore}.
     *
     * @param toStretch the String to stretch
     * @param filler the character to use as filler
     * @param length the length the string is supposed to have afterwards
     * @param insertBefore whether to insert the filler character before
     *                    or after the string
     * @return the string brought to given length with given filler character
     */
    @NotNull
    public static String stretch(String toStretch, char filler, int length, boolean insertBefore) {
        StringBuilder result = new StringBuilder(toStretch);

        while (result.length() < length) {
            if(insertBefore) {
                result.insert(0, filler);
            } else {
                result.append(filler);
            }
        }

        return result.toString();
    }

    /**
     * The private default-constructor to keep anyone from instantiating this class,
     * since it is to be used solely in a static context.
     */
    private StringProcessing() {
    }
}
