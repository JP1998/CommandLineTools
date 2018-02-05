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

package test.de.hotzjeanpierre.commandlinetools.command.utils;

import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import de.hotzjeanpierre.commandlinetools.command.utils.exceptions.StringProcessingFormatException;
import org.junit.Assert;
import org.junit.Test;

public class StringProcessingTest {

    @Test
    public void testFormatValid() {
        Assert.assertEquals(
                "Something's wrong with StringProcessing#format(String, Object...).",
                "1234--{asdf}",
                StringProcessing.format(
                        "{0}--{{{1}}}",
                        1234,
                        "asdf"
                )
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

}