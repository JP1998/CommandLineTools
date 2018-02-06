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

import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import de.hotzjeanpierre.commandlinetools.command.utils.exceptions.StringProcessingFormatException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class StringProcessingTest {

    @Test
    public void testFormatValid() {
        assertThat(
                "1234--{asdf}",
                is(StringProcessing.format(
                        "{0}--{{{1}}}",
                        1234,
                        "asdf"
                ))
        );
    }

    @Test(expected = StringProcessingFormatException.class)
    public void testFormatInvalid() {
        StringProcessing.format("Hello World {");
    }

    @Test(expected = StringProcessingFormatException.class)
    public void testFormatNoWildcardIndex() {
        StringProcessing.format("Hello World {asd}", "asdfg");
    }

    @Test
    public void testTokenizing() {
        String[] tokens = StringProcessing.tokenizeCommand("asdf ghjk lmno pqrs \"\\\"Hello World!\\\"\"");

        assertThat(
                tokens,
                is(new String[] { "asdf", "ghjk", "lmno", "pqrs", "\"Hello World!\"" })
        );
    }

}