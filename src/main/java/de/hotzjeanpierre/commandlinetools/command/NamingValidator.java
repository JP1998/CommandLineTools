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

package de.hotzjeanpierre.commandlinetools.command;

import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;

import java.util.regex.Pattern;

/**
 * This interface provides an easily usable validator for names that are supposed
 * to start with an alphabetical character or an underscore and is to go on
 * with alphabetical characters, digits or an underscore.
 * It can therefor be used to validate for example variable or parameter names.
 */
public interface NamingValidator {

    /**
     * The pattern to validate names with.
     */
    Pattern sNameValidatorPattern =
            Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9]*$");

    /**
     * Validates a name and throws an {@link InvalidNameException} if it is not valid.
     *
     * @param name the name to validate
     */
    default void assureNameValidity(String name) {
        assureNameValidity(name, "The name '{0}' is not valid.", name);
    }

    /**
     * Validates a name and throws an {@link InvalidNameException} if it is not valid.
     *
     * @param name        the name to validate
     * @param failMessage the message to give the exception in case it is thrown
     */
    default void assureNameValidity(String name, String failMessage) {
        assureNameValidity(name, failMessage, new Object[0]);
    }

    /**
     * Validates a name and throws an {@link InvalidNameException} if it is not valid.
     *
     * @param name              the name to validate
     * @param failMessageFormat the format for the message of the exception as specified in {@link StringProcessing#format(String, Object...)}
     * @param replacements      the Objects to write into the wildcards of the messages format
     */
    default void assureNameValidity(String name, String failMessageFormat, Object... replacements) {
        if (!sNameValidatorPattern.matcher(name).matches()) {
            throw new InvalidNameException(
                    (replacements.length == 0)? failMessageFormat : StringProcessing.format(failMessageFormat, replacements)
            );
        }
    }

    /**
     * The exception that is thrown as soon as an invalid name is tried to
     * be validated.
     */
    class InvalidNameException extends RuntimeException {
        InvalidNameException(String message) {
            super(message);
        }
    }
}
