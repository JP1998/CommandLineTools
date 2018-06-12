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

import de.hotzjeanpierre.commandlinetools.command.parameter.CommonTypes;
import de.hotzjeanpierre.commandlinetools.command.parameter.EnumType;
import de.hotzjeanpierre.commandlinetools.command.parameter.ObjectType;
import de.hotzjeanpierre.commandlinetools.command.utils.files.FileNamingTemplate;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ConverterTest {

    @Test
    public void testStringConversion() {
        assertThat(
                new Converter().convert("some string", CommonTypes.String),
                is("some string")
        );
    }

    @Test
    public void testFileConversionValid() {
        assertThat(
                new Converter().convert("/asdf/ghjk lmno/pqrs.txt", CommonTypes.File),
                is(new File("/asdf/ghjk lmno/pqrs.txt"))
        );
    }

    @Test
    public void testFileConversionInvalid() {
        assertThat(
                new Converter().convert("/asd?/ghjk.jpg", CommonTypes.File),
                nullValue()
        );
    }

    @Test
    public void testFileNamingTemplateConversionValid() {
        assertThat(
                new Converter().convert("{index:10}asdf - fdsa{extension}", CommonTypes.FileNamingTemplate),
                is(FileNamingTemplate.parse("{index:10}asdf - fdsa{extension}"))
        );
    }

    @Test
    public void testFileNamingTemplateConversionInvalid() {
        assertThat(
                new Converter().convert("{indax:10}asdf - fdsa{extension}", CommonTypes.FileNamingTemplate),
                nullValue()
        );
    }

    @Test
    public void testBooleanConversionValidTrue() {
        assertThat(
                new Converter().convert("true", CommonTypes.Primitives.Boolean),
                is(true)
        );
    }

    @Test
    public void testBooleanConversionValidFalse() {
        assertThat(
                new Converter().convert("false", CommonTypes.Primitives.Boolean),
                is(false)
        );
    }

    @Test
    public void testBooleanConversionInvalid() {
        assertThat(
                new Converter().convert("true af", CommonTypes.Primitives.Boolean),
                nullValue()
        );
    }

    @Test
    public void testDoubleConversionValid() {
        assertThat(
                new Converter().convert("1.234e+4", CommonTypes.Primitives.Double),
                is(1.234e+4d)
        );
    }

    @Test
    public void testDoubleConversionInvalid() {
        assertThat(
                new Converter().convert("1.2.3.4", CommonTypes.Primitives.Double),
                nullValue()
        );
    }

    @Test
    public void testFloatConversionValid() {
        assertThat(
                new Converter().convert("1.234e+4", CommonTypes.Primitives.Float),
                is(1.234e+4f)
        );
    }

    @Test
    public void testFloatConversionInvalid() {
        assertThat(
                new Converter().convert("1.2.3.4", CommonTypes.Primitives.Float),
                nullValue()
        );
    }

    @Test
    public void testByteConversionValid() {
        assertThat(
                new Converter().convert("125", CommonTypes.Primitives.Byte),
                is((byte) 125)
        );
    }

    @Test
    public void testByteConversionInvalid() {
        assertThat(
                new Converter().convert("12345", CommonTypes.Primitives.Byte),
                nullValue()
        );
    }

    @Test
    public void testShortConversionValid() {
        assertThat(
                new Converter().convert("23456", CommonTypes.Primitives.Short),
                is((short) 23456)
        );
    }

    @Test
    public void testShortConversionInvalid() {
        assertThat(
                new Converter().convert("123456", CommonTypes.Primitives.Short),
                nullValue()
        );
    }

    @Test
    public void testCharacterConversionValid() {
        assertThat(
                new Converter().convert("a", CommonTypes.Primitives.Character),
                is('a')
        );
    }

    @Test
    public void testCharacterConversionInvalid() {
        assertThat(
                new Converter().convert("ab", CommonTypes.Primitives.Character),
                nullValue()
        );
    }

    @Test
    public void testIntegerConversionValid() {
        assertThat(
                new Converter().convert("12345678", CommonTypes.Primitives.Integer),
                is(12345678)
        );
    }

    @Test
    public void testIntegerConversionInvalid() {
        assertThat(
                new Converter().convert("ab", CommonTypes.Primitives.Integer),
                nullValue()
        );
    }

    @Test
    public void testLongConversionValid() {
        assertThat(
                new Converter().convert("123456789101112", CommonTypes.Primitives.Long),
                is(123456789101112L)
        );
    }

    @Test
    public void testLongConversionInvalid() {
        assertThat(
                new Converter().convert("ab", CommonTypes.Primitives.Long),
                nullValue()
        );
    }

    @Test
    public void testEnumConversionValid() {
        assertThat(
                new Converter().convert("BLOCKED", new EnumType(Thread.State.class)),
                is(Thread.State.BLOCKED)
        );
    }

    @Test
    public void testEnumConversionInvalid() {
        assertThat(
                new Converter().convert("anything but a valid string", new EnumType(Thread.State.class)),
                nullValue()
        );
    }

    @Test
    public void testAnyInvalidConversion() {
        assertThat(
                new Converter().convert("anything", new ObjectType(Object.class)),
                nullValue()
        );
    }
}
