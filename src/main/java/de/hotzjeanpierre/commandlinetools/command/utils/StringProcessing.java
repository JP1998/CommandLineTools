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
    public static String[] tokenizeCommand(@NotNull String command) {
        // first off validate the given command so we don't have to do that *while* parsing / tokenizing
        if (!validateCommand(command)) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "Command '{0}' does not conform to the syntax and can thus not be parsed.", command
            ));
        }

        // the list that contains all the tokens
        List<String> tokens = new ArrayList<>();

        // the start / end index of the next token in the given string
        int start = 0;
        int end = 0;

        // some flags
        boolean inString;
        boolean commandNameChecked = false;

        // the index of the character we're currently investigating
        int i = 0;

        // while there are still characters to process
        while (i < command.length()) {
            // we'll skip all the whitespace
            while (i < command.length() && Character.isWhitespace(command.charAt(i))){
                i++;
            }
            // and end the loop if there was only whitespace left
            if(i == command.length()) {
                break;
            }
            // set the start to be the first non-whitespace character
            start = i;

            if(!commandNameChecked) {
                // we'll have to parse the commands name at the beginning of the command
                // the first character is definitely no whitespace and after that we'll skip
                // all the non-whitespace characters
                i++;
                while (i < command.length() && !Character.isWhitespace(command.charAt(i))) {
                    i++;
                }

                // then we'll extract the command name and set the flag 'commandNameChecked'
                commandNameChecked = true;
                end = i;
                tokens.add(command.substring(start, end));
            } else if(command.charAt(i) == '"') {
                // we'll have to parse a string that has been given, thus we'll show that we're
                // now processing a string and advance the pointer, as the character at the current
                // position is definitely a double-quote, whereas without advancing we'd immediately
                // terminate the string again.
                inString = true;
                i++;
                // we'll evaluate as long as we're in a string
                while (i < command.length() && inString) {
                    if(command.charAt(i) == '\\') {
                        // if there is a escape sequence we'll completely skip it
                        i += 2;
                    } else if(command.charAt(i) == '"') {
                        // if the string is terminated we'll delete the flag and advance the pointer
                        inString = false;
                        i++;
                    } else {
                        // any other character will simply be skipped
                        i++;
                    }
                }

                // then we'll extract the actual string and descape its contents
                end = i;
                tokens.add(descape(command.substring(start + 1, end - 1)));
            } else {
                // parse any sequence of plain non-whitespace characters
                while (i < command.length() && !Character.isWhitespace(command.charAt(i))) {
                    i++;
                }
                end = i;
                tokens.add(command.substring(start, end));
            }
        }

        // convert the list of tokens into an array and return said array
        String[] tokensArray = new String[tokens.size()];
        return tokens.toArray(tokensArray);
    }

    /**
     * This method is supposed to validate a command that has been given.
     * It will return the value {@code true} in case the value of the given string
     * *could* be tokenized, and {@code false} otherwise.
     * This does not mean though, that the command that has been given is definitely
     * valid, since this method only checks syntax and not semantics.
     *
     * @param command the command that is to be validated
     * @return whether the given command is valid or not
     */
    private static boolean validateCommand(@NotNull String command) {
        // we need at least a command name whereas an empty string is not valid
        if(command.trim().isEmpty()) {
            return false;
        }

        // some flags we need to properly validate the command
        boolean commandNameChecked = false;

        // the index of the character we currently process
        int i = 0;

        // we have to process every character in the command
        while (i < command.length()) {
            // we'll skip all the whitespace at the current selection
            while (i < command.length() && Character.isWhitespace(command.charAt(i))){
                i++;
            }

            // since we know that the command is not empty and we reached the end by
            // skipping whitespace while there has not been any invalidation before
            // we know that the given command is valid
            if(i == command.length()) {
                return true;
            }

            if(!commandNameChecked) {
                // validate the name of the command which will always be the first word in the command
                i = validateCommandName(command, i);
                if(i == -1) {
                    return false;
                }
                commandNameChecked = true;
            } else if(command.charAt(i) == '"') {
                // validate string (with escape sequences)
                i = validateString(command, i);
                if(i == -1) {
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
     * This method will validate a command name which starts at the given index i.
     * If the command name is valid this method will return the index of the first
     * character that does not belong to the command name. In case it is invalid this
     * method will return {code -1}.
     *
     * @param command the command that is to be validated
     * @param i the index of the first character of the command name
     * @return the new index i if the command name is valid; -1 if it is invalid
     */
    private static int validateCommandName(@NotNull String command, int i) {
        final Pattern commandNameStartPattern = Pattern.compile("[_a-zA-Z]");
        final Pattern commandNamePattern = Pattern.compile("[_a-zA-Z0-9]");

        //  check the first character of the command name
        if(!commandNameStartPattern.matcher(Character.toString(command.charAt(i))).matches()){
            return -1;
        }
        // check for all the subsequent characters until we see whitespace
        i++;
        while (i < command.length() && !Character.isWhitespace(command.charAt(i))) {
            if(!commandNamePattern.matcher(Character.toString(command.charAt(i))).matches()) {
                return -1;
            }
            i++;
        }

        return i;
    }

    /**
     * This method will validate a string which starts at the given index i.
     * If the string is valid this method will return the index of the first
     * character that does not belong to the string. In case it is invalid this
     * method will return {code -1}.
     *
     * @param command the command that is to be validated
     * @param i the index of the first character of the string
     * @return the new index i if the string is valid; -1 if it is invalid
     */
    private static int validateString(@NotNull String command, int i) {
        // all the characters that form a valid escape sequence
        final Character[] validEscapeSequences = new Character[] {
                't', 'b', 'n', 'r', 'f', '\'', '\"', '\\' };

        // a flag to indicate whether we're still in the string
        boolean inString = true;

        // the first character is a double quote and needs to be skipped
        i++;
        // we need to process characters until we're not in the string anymore
        // while we also have to make sure that there are characters left to process
        while (i < command.length() && inString) {
            if(command.charAt(i) == '\\') {
                // in case there is a backslash we'll validate an escape sequence
                if(i < command.length() - 1 &&
                        ArrayHelper.containsAny(validEscapeSequences, command.charAt(i + 1))) {
                    i += 2;
                } else {
                    return -1;
                }
            } else if(command.charAt(i) == '"') {
                // in case there is a double quote we'll terminate the string
                inString = false;
                i++;
            } else {
                // any other character will be skipped
                i++;
            }
        }

        // if we reach the end of the command while still being inside a string
        // we'll know that the string has not been terminated
        if(i == command.length() && inString) {
            return -1;
        }

        return i;
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
