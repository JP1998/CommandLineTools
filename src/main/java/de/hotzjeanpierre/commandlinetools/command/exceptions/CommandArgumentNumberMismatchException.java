/*
 *     Copyright 2017 Jean-Pierre Hotz
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

package de.hotzjeanpierre.commandlinetools.command.exceptions;

/**
 * This exception may be thrown whenever there is an uneven number of arguments given for a
 * command. This is done because parameters (at least in this environment) are always given as
 * key-value-pairs, whereas a command always has the format {@code "<command-name> (<parameter-name> <parameter-value>)*"}.
 */
public class CommandArgumentNumberMismatchException extends RuntimeException {

    public CommandArgumentNumberMismatchException(String message) {
        super(message);
    }
}
