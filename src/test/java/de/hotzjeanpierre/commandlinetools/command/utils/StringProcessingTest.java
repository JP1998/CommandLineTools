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
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StringProcessingTest {

    @Test
    public void testFormatValid() {
        assertThat(
                StringProcessing.format(
                        "{0}--{{{1}}}",
                        1234,
                        "asdf"
                ),
                is("1234--{asdf}")
        );
    }

    @Test(expected = StringProcessingFormatException.class)
    public void testFormatInvalidUnescapedCurlyBrackets() {
        StringProcessing.format("Hello World {");
    }

    @Test(expected = StringProcessingFormatException.class)
    public void testFormatInvalidToHighIndex() {
        StringProcessing.format("Hello World {123}");
    }

    @Test(expected = StringProcessingFormatException.class)
    public void testFormatNoWildcardIndex() {
        StringProcessing.format("Hello World {asd}", "asdfg");
    }

    @Test
    public void testTokenizingValid() {
        assertThat(
                StringProcessing.tokenizeCommand(
                        "asdf ghjk lmno pqrs \"\\\"Hello World!\\\"\""
                ),
                is(new String[] {
                        "asdf",
                        "ghjk",
                        "lmno",
                        "pqrs",
                        "\"Hello World!\""
                })
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTokenizingInvalidFormat() {
        StringProcessing.tokenizeCommand("99asdf");
    }

    // Tests exception that does not apply anymore!
//    @Test(expected = IllegalArgumentException.class)
//    public void testTokenizingInvalidArgumentNumber() {
//        StringProcessing.tokenizeCommand(
//                "asd fgh"
//        );
//    }

    @Test
    public void testZeroPaddingNegativeNumbers() {
        assertThat(
                StringProcessing.zeroPadding(-123, 5),
                is("-0123")
        );
    }

    @Test
    public void testZeroPaddingLessDigitsThanNeeded() {
        assertThat(
                StringProcessing.zeroPadding(123456, 2),
                is("123456")
        );
    }

    @Test
    public void testZeroPaddingMoreDigitsThanNeeded() {
        assertThat(
                StringProcessing.zeroPadding(1234, 8),
                is("00001234")
        );
    }

}