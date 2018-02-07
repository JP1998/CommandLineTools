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

import org.junit.Test;

public class NamingValidatorTest implements NamingValidator {

    @Test
    public void testValidName_withoutmessage() {
        assureNameValidity("_asd_123ASDF");
    }

    @Test(expected = NamingValidator.InvalidNameException.class)
    public void testNameBeginningWithDigit_withoutmessage() {
        assureNameValidity("123asdf");
    }

    @Test(expected = NamingValidator.InvalidNameException.class)
    public void testNameContainingWhitespace_withoutmessage() {
        assureNameValidity("_asdf asdf");
    }

    @Test
    public void testValidName_withmessage() {
        assureNameValidity("_asd_123ASDF", "some error message.");
    }

    @Test(expected = NamingValidator.InvalidNameException.class)
    public void testNameBeginningWithDigit_withmessage() {
        assureNameValidity("123asdf", "some error message.");
    }

    @Test(expected = NamingValidator.InvalidNameException.class)
    public void testNameContainingWhitespace_withmessage() {
        assureNameValidity("_asdf asdf", "some error { message");
    }

}
