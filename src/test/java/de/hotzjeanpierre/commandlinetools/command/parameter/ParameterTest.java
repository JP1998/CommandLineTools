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

package de.hotzjeanpierre.commandlinetools.command.parameter;

import de.hotzjeanpierre.commandlinetools.command.exceptions.ParameterTypeMismatchException;
import de.hotzjeanpierre.commandlinetools.command.testutilities.SomeClass;
import de.hotzjeanpierre.commandlinetools.command.testutilities.SomeSubClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class ParameterTest {

    @Test(expected = NullPointerException.class)
    public void testNullNameFailing() {
        new Parameter(
                null,
                new ObjectType(ParameterTest.class),
                "some description",
                null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyNameFailing() {
        new Parameter(
                "   \t\n",
                new ObjectType(ParameterTest.class),
                "some description",
                null
        );
    }

    @Test(expected = NullPointerException.class)
    public void testNullTypeFailing() {
        new Parameter(
                "somename",
                null,
                "some description",
                null
        );
    }

    @Test(expected = NullPointerException.class)
    public void testNullDescriptionFailing() {
        new Parameter(
                "somename",
                new ObjectType(ParameterTest.class),
                null,
                null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyDescriptionFailing() {
        new Parameter(
                "somename",
                new ObjectType(ParameterTest.class),
                "    \t \n",
                null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypeMismatchForDefaultValue() {
        new Parameter(
                "somename",
                new ObjectType(SomeSubClass.class),
                "some description",
                new SomeClass(1, 2.3)
        );
    }

    @Test(expected = ParameterTypeMismatchException.class)
    public void testTypeMismatchForCreatingValue() {
        new Parameter(
                "somename",
                new ObjectType(SomeClass.class),
                "some description",
                null
        ).createValue(new Object());
    }

    @Test
    public void testParameterNameForValueCorrect() {
        Parameter testedParameter = new Parameter("somename", new ObjectType(SomeClass.class), "some description", 0);

        assertThat(
                testedParameter.createValue(new SomeClass(1, 2.3)).getParameterName(),
                is(testedParameter.getName())
        );
    }

    @Test
    public void testParameterCreatesValueWithGivenValue() {
        Parameter testedParameter = new Parameter("somename", new ObjectType(SomeClass.class), "some description", 0);

        assertThat(
                testedParameter.createValue(new SomeClass(1, 2.3)).getValue(),
                is(new SomeClass(1, 2.3))
        );
    }

    @Test
    public void testParameterTypeValid() {
        Type type = new ObjectType(SomeClass.class);

        assertThat(
                new Parameter(
                        "someparameter",
                        type,
                        "some description",
                        0
                ).getType(),
                is((Object) type)
        );
    }

    @Test
    public void testParameterNullDefaultValueValid() {
        assertThat(
                new Parameter(
                        "someparameter",
                        new ObjectType(SomeClass.class),
                        "some description",
                        null
                ).getDefaultValue(),
                nullValue()
        );
    }

    @Test
    public void testParameterNotNullDefaultValueValid() {
        assertThat(
                new Parameter(
                        "someparameter",
                        new ObjectType(SomeClass.class),
                        "some description",
                        new SomeSubClass(14, 15.16, false)
                ).getDefaultValue(),
                is(new SomeSubClass(14, 15.16, false))
        );
    }

    @Test
    public void testParameterDescriptionValid() {
        assertThat(
                new Parameter(
                        "someparameter",
                        new ObjectType(SomeClass.class),
                        "This is some extended description which we want to test.",
                        0
                ).getDescription(),
                is("This is some extended description which we want to test.")
        );
    }
}