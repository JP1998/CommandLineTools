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

            resultBuilder.append(format, 0, (found) ? wildcards.start() : format.length());

            while (found) {
                // replace while saving end
                String replacement = parseWildcard(wildcards.group(), replacements);
                resultBuilder.append(replacement);
                int start = wildcards.end();
                // find next
                found = wildcards.find();
                // add plain text in between
                resultBuilder.append(format, start, (found) ? wildcards.start() : format.length());
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
        int start;
        int end;

        // some flags
        boolean commandNameChecked = false;

        // the index of the character we're currently investigating
        int i = 0;

        // while there are still characters to process
        while (i < command.length()) {
            // we'll skip all the whitespace
            i = skipWhiteSpace(command, i);
            // and end the loop if there was only whitespace left
            if(i == command.length()) {
                break;
            }
            // set the start to be the first non-whitespace character
            start = i;

            if(!commandNameChecked) {
                i = findEndOfCommandName(command, i);
                commandNameChecked = true;
                end = i;
                tokens.add(command.substring(start, end));
            } else if(command.charAt(i) == '"') {
                i = findEndOfString(command, i);
                end = i;
                tokens.add(descape(command.substring(start + 1, end - 1)));
            } else if(command.charAt(i) == '{') {
                i = findEndOfArray(command, i);
                end = i;
                tokens.add(command.substring(start, end));
            } else {
                i = findEndOfSimpleParameter(command, i);
                end = i;
                tokens.add(command.substring(start, end));
            }
        }

        // convert the list of tokens into an array and return said array
        String[] tokensArray = new String[tokens.size()];
        return tokens.toArray(tokensArray);
    }

    /**
     * This method returns the first index c at which the character in the given
     * command is no whitespace, while it begins to search at the given index i.
     * Thus you can consider the relation {@code i <= c <= command.length()}, whereas
     * command.length() is returned when there is no whitespace contained in the
     * string when beginning to search at the given index i.
     *
     * @param command The string to skip whitespace in
     * @param i the index at which to search for whitespace
     * @return the index of the first character (after i) that is no whitespace
     */
    private static int skipWhiteSpace(@NotNull String command, int i) {
        while (i < command.length() && Character.isWhitespace(command.charAt(i))){
            i++;
        }
        return i;
    }

    /**
     * This method returns the index c of the first character at which the array
     * starting at the given index i will have ended. This index is always the index
     * of the character after the final closing bracket of the array (nested arrays
     * will be handled by this method). In case you have validated the command beforehand
     * you can consider the relation {@code i + 1 < c <= command.length()}, while the
     * lower bound is exclusive since the opening bracket cannot be the closing bracket.
     *
     * @param command the command to find the end of the array in
     * @param i the index at which the array starts (with an opening curly bracket)
     * @return the index c of the first character after the final closing bracket
     */
    private static int findEndOfArray(@NotNull String command, int i) {
        i++;

        int depth = 1;

        while (i < command.length() && depth > 0) {
            i = skipWhiteSpace(command, i);

            if (command.charAt(i) == '{') {
                depth++;
            } else if(command.charAt(i) == '}') {
                depth--;
            } else if(command.charAt(i) == '"') {
                // the minus one here is to be done since the findEndOfString-method
                // will let i point to the first character outside of the string,
                // which will make String#substring(int, int) include only the last double quote.
                i = findEndOfString(command, i) - 1;
            } else if(command.charAt(i) != ',') {
                // minus one again because of aforementioned reason
                i = findEndOfArrayElement(command, i) - 1;
            }
            i++;
        }

        return i;
    }

    /**
     * This method returns the index c of the first character at which the command name
     * starting at the given index i will have ended. Since you are supposed to have
     * the command validated, this is done by searching for the first whitespace character.
     *
     * @param command the command to find the end of the command name in
     * @param i the index at which the command name starts
     * @return the index c of the first character after the command name
     */
    private static int findEndOfCommandName(@NotNull String command, int i) {
        // we'll have to parse the commands name at the beginning of the command
        // the first character is definitely no whitespace and after that we'll skip
        // all the non-whitespace characters
        i++;
        while (i < command.length() && !Character.isWhitespace(command.charAt(i))) {
            i++;
        }
        return i;
    }

    /**
     * This method returns the index c of the first character that is not included
     * in the string starting at the given index i. The ending double quotes will
     * be considered to be part of the string, whereas you can consider the relation
     * {@code i + 1 < c <= command.length()}. The lower bound can be explained by
     * {@code c} always being the index after the closing double quotes, whereas
     * these have to be at least at the index {@code i + 1}.
     *
     * @param command the command to find the end of the string in
     * @param i the index at which the string starts
     * @return the index c of the first character after the string
     */
    private static int findEndOfString(@NotNull String command, int i) {
        // we'll have to parse a string that has been given, thus we'll show that we're
        // now processing a string and advance the pointer, as the character at the current
        // position is definitely a double-quote, whereas without advancing we'd immediately
        // terminate the string again.
        boolean inString = true;
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
        return i;
    }

    /**
     * This method returns the index c of the first character that is not included
     * in the simple parameter starting at the given index i. Since the syntax of
     * a simple parameter is defined as a string of characters not interrupted by whitespace
     * this method will search for the first whitespace and return its index.
     *
     * @param command the command to find the end of the simple parameter in
     * @param i the index at which the simple parameter starts
     * @return the index c of the first character after the simple parameter
     */
    private static int findEndOfSimpleParameter(@NotNull String command, int i) {
        while (i < command.length() && !Character.isWhitespace(command.charAt(i))) {
            i++;
        }
        return i;
    }

    /**
     * This method finds the index of the first character not belonging to the
     * array element that starts at the given index i.
     *
     * @param command the command that contains the array element to find
     * @param i the index at which the array element starts
     * @return the index of the first character that is not in the element
     */
    private static int findEndOfArrayElement(@NotNull String command, int i) {
        while (i < command.length() && !Character.isWhitespace(command.charAt(i)) &&
                command.charAt(i) != '{' && command.charAt(i) != '}' &&
                command.charAt(i) != ',') {
            i++;
        }
        return i;
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
            i = skipWhiteSpace(command, i);

            // since we know that the command is not empty and we reached the end by
            // skipping whitespace while there has not been any invalidation before
            // we know that the given command is valid
            if (i == command.length()) {
                return true;
            }

            if (!commandNameChecked) {
                // validate the name of the command which will always be the first word in the command
                i = validateCommandName(command, i);
                if (i == -1) {
                    return false;
                }
                commandNameChecked = true;
            } else if (command.charAt(i) == '"') {
                // validate string (with escape sequences)
                i = validateString(command, i);
                if (i == -1) {
                    return false;
                }
            } else if (command.charAt(i) == '{') {
                // validate an array
                i = validateArray(command, i);
                if(i == -1) {
                    return false;
                }
            } else {
                // "validate" any characters in a simple string
                i = validateSimpleParameter(command, i);
                if(i == -1) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * This method is supposed to validate a simple parameter in the given command.
     * It will return the value {@code -1} in case the simple parameter is not
     * valid (which will never be the case since the simple parameter has no restrictions),
     * and the first index after the simple command in case it is valid.
     *
     * @param command the command that is to be validated
     * @param i the index at which the simple parameter starts
     * @return -1 if the parameter is not valid;
     *         the index of the first character after the parameter otherwise
     */
    private static int validateSimpleParameter(@NotNull String command, int i) {
        while (i < command.length() && !Character.isWhitespace(command.charAt(i))) {
            i++;
        }
        return i;
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
                't', 'b', 'n', 'r', 'f', '\'', '\"', '\\', 'u' };

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
     * This method will validate an array which starts at the given index i.
     * If the array is valid this method will return the index of the first
     * character that does not belong to the array. In case it is invalid this
     * method will return {code -1}.
     * Any nested arrays (i.e. arrays that contain arrays; may be with
     * arbitrary depth) will also be handled by this method.
     *
     * @param command the command that is to be validated
     * @param i the index of the first character of the array
     * @return the new index i if the array is valid; -1 if it is invalid
     */
    private static int validateArray(@NotNull String command, int i) {
        i++;

        boolean firstElement = true;
        boolean inArray = true;

        while (i < command.length() && inArray) {
            i = skipWhiteSpace(command, i);

            if(!firstElement) {
                if(command.charAt(i) != ',' && command.charAt(i) != '}') {
                    return -1;
                } else if(command.charAt(i) == '}') {
                    inArray = false;
                } else {
                    i++;
                }
            }

            i = skipWhiteSpace(command, i);

            if (command.charAt(i) == '{') {
                i = validateArray(command, i);
                if(i == -1) {
                    return -1;
                }
            } else if(command.charAt(i) == '"') {
                i = validateString(command, i);
                if(i == -1) {
                    return -1;
                }
            } else {
                i = validateArrayElement(command, i);
                if(i == -1) {
                    return -1;
                }
            }

            firstElement = false;
        }

        if(i == command.length() && inArray) {
            return -1;
        }

        return i + 1;
    }

    /**
     * This method is supposed to validate an element of an array in the given command.
     * It will return the value {@code -1} in case the element is not
     * valid (which will never be the case since the array elements have no restrictions),
     * and the first index after the element in case it is valid.
     * This method is similar to {@link #validateSimpleParameter(String, int)}, while
     * this method reacts to any closing curly brackets and commas, aswell as
     * any whitespace.
     *
     * @param command the command that is to be validated
     * @param i the index at which the array element starts
     * @return -1 if the element is not valid;
     *         the index of the first character after the element otherwise
     */
    private static int validateArrayElement(@NotNull String command, int i) {
        while (i < command.length() && !Character.isWhitespace(command.charAt(i)) &&
                command.charAt(i) != '}' && command.charAt(i) != ',') {
            i++;
        }
        return i;
    }

    /**
     * This method tokenizes a string that represents an array.
     * For this purpose there are only three types of tokens used:
     * the opening bracket ("{"}, the closing bracket ("}") and any
     * kind of value contained within the array.
     *
     * @param representation the tokenized array as described above
     * @return the array containing all the single tokens
     */
    @NotNull
    /* package-protected */ static String[] tokenizeArray(@NotNull String representation) {
        List<String> tokens = new ArrayList<>();
        int i = 0;

        while (i < representation.length()) {
            i = skipWhiteSpace(representation, i);

            if(representation.charAt(i) == '{') {
                tokens.add("{");
            } else if (representation.charAt(i) == '}') {
                tokens.add("}");
            } else if(representation.charAt(i) == '"') {
                int start = i;
                i = findEndOfString(representation, i);
                tokens.add(descape(representation.substring(start + 1, i - 1)));
            } else if(representation.charAt(i) != ',') {
                int start = i;
                i = findEndOfArrayElement(representation, i);
                tokens.add(representation.substring(start, i));
            }
            i++;
        }

        String[] result = new String[tokens.size()];
        return tokens.toArray(result);
    }

    private static final Pattern UNICODE_CODEPOINT_PATTERN = Pattern.compile("^[a-fA-F0-9]{4}$");

    /**
     * Descapes any exscape-sequence inside a string. This method supports standard escape sequences like '\n' or '\t'
     * as well as unicode escape sequences like '\u0054' for an uppercase t ('T').
     *
     * @param str the string containing escape sequences
     * @return the string with descaped escape-sequences
     */
    @NotNull
    private static String descape(@NotNull String str) {
        int i = 0;
        int occurrence = str.indexOf('\\', i);

        while (occurrence >= 0) {
            String replacement;
            int skippedCharacters;
            switch (str.charAt(occurrence + 1)) {
                case 't':
                    replacement = "\t";
                    skippedCharacters = 2;
                    break;
                case 'b':
                    replacement = "\b";
                    skippedCharacters = 2;
                    break;
                case 'n':
                    replacement = "\n";
                    skippedCharacters = 2;
                    break;
                case 'r':
                    replacement = "\r";
                    skippedCharacters = 2;
                    break;
                case 'f':
                    replacement = "\f";
                    skippedCharacters = 2;
                    break;
                case '\'':
                    replacement = "\'";
                    skippedCharacters = 2;
                    break;
                case '\"':
                    replacement = "\"";
                    skippedCharacters = 2;
                    break;
                case '\\':
                    replacement = "\\";
                    skippedCharacters = 2;
                    break;
                case 'u':
                    skippedCharacters = 6;

                    String unicodeCodePoint = str.substring(occurrence + 2, occurrence + 6);
                    if(UNICODE_CODEPOINT_PATTERN.matcher(unicodeCodePoint).matches()) {
                        int literalCodePoint = Integer.parseInt(unicodeCodePoint, 16);
                        replacement = new String(Character.toChars(literalCodePoint));
                    } else {
                        throw new IllegalArgumentException(StringProcessing.format(
                                "Malformed unicode escape sequence '{0}' recognized.",
                                str.substring(occurrence, occurrence + 6)
                        ));
                    }
                    break;
                default: throw new IllegalArgumentException(StringProcessing.format(
                        "Illegal escape sequence '{0}' recognized.",
                        str.substring(occurrence, occurrence + 2),
                        str
                ));
            }

            str = str.substring(0, occurrence) + replacement + str.substring(occurrence + skippedCharacters);

            i = occurrence;
            occurrence = str.indexOf('\\', i);
        }

        return str;
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

        String toInsert = multiply(Character.toString(filler), length - toStretch.length());

        if(insertBefore) {
            result.insert(0, toInsert);
        } else {
            result.append(toInsert);
        }

        return result.toString();
    }

    /**
     * This method concatenates the string with itself n times and returns the result.
     *
     * @param str the string to concatenate with itself
     * @param n how often the string is supposed to be concatenated with itself
     * @return the given string concatenated n times with itself
     */
    @NotNull
    public static String multiply(String str, int n) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < n; i++) {
            result.append(str);
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
