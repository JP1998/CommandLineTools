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
     * The pattern to validate the syntax of commands with.
     */
    private static final Pattern sCommandValidator =
            Pattern.compile("^\\s*([_a-zA-Z][_a-zA-Z0-9]*)(\\s+((\"(?:[^\"\\\\]|\\\\[tbnrf'\"\\\\])*\")|([^\\s]+)))*\\s*$");
    // ^\s*([_a-zA-Z][_a-zA-Z0-9]*)(\s+([_a-zA-Z][_a-zA-Z0-9]*)\s+(("(?:[^"\\]|\\[tbnrf'"\\])*")|([^\s]+)))*\s*$
    /**
     * The pattern to extract the name of a command with.
     */
    private static final Pattern sCommandNameExtractor =
            Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
    /**
     * The Pattern that is used to extract a part of a parameter of a command.
     */
    private static final Pattern sCommandParameterPartExtractor =
            Pattern.compile("((\"(?:[^\"\\\\]|\\\\[tbnrf'\"\\\\])*\")|([^\\s]+))");
    /**
     * The pattern that is used to determine whether the parameter is a string or not.
     */
    private static final Pattern sCommandParameterStringValidator =
            Pattern.compile("\"(?:[^\"\\\\]|\\\\[tbnrf'\"\\\\])*\"");

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
            case "{{":
                return "{";
            case "}}":
                return "}";
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
     * @param toTokenize the string representing a command that is to be tokenized
     * @return the single tokens in the string
     */
    @NotNull
    public static String[] tokenizeCommand(String toTokenize) {
        if (!sCommandValidator.matcher(toTokenize).matches()) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "Command '{0}' does not conform to the syntax and can thus not be parsed.", toTokenize
            ));
        }

        List<String> tokens = new ArrayList<>();

        // apply the group extracting pattern onto the given String we're trying to tokenize
        Matcher commandNameMatcher = sCommandNameExtractor.matcher(toTokenize);

        if (!commandNameMatcher.find()) {
            // usually unreachable statement, since the given String has been verified to
            // at least have one match with the pattern for group extraction
            // still checked for information in case something goes wrong during development.
            throw new IllegalArgumentException(StringProcessing.format(
                    "Command '{0}' cannot be parsed due to an error in the Regex of the tokenizer.", toTokenize
            ));
        }

        // extract the commands name
        tokens.add(commandNameMatcher.group());

        // strip away the command name since we already processed it
        toTokenize = toTokenize.substring(commandNameMatcher.end());

        // extract the single pieces of the parameters
        Matcher commandParameterMatcher = sCommandParameterPartExtractor.matcher(toTokenize);

        while (commandParameterMatcher.find()) {
            String token = commandParameterMatcher.group();

            // descape and remove double quotes from quoted parameters strings
            if (sCommandParameterStringValidator.matcher(token).matches()) {
                token = descape(token.substring(1, token.length() - 1));
            }

            tokens.add(token);
        }

        // copy the list into an array
        String[] tokensArray = new String[tokens.size()];
        return tokens.toArray(tokensArray);
    }

    /**
     * Descapes any exscape-sequence inside a string. Only supports standard escape sequences like '\n' or '\t'.
     *
     * @param str the string containing escape sequences
     * @return the string with descaped escape-sequences
     */
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
        return zeroPadding(number, length, 10);
    }

    public static String zeroPadding(int number, int length, int radix) {
        boolean negative = number < 0;

        if (negative) {
            length--;
        }

        StringBuilder result = new StringBuilder(
                Integer.toString(Math.abs(number), radix)
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
     * The private default-constructor to keep anyone from instantiating this class,
     * since it is to be used solely in a static context.
     */
    private StringProcessing() {
    }
}
