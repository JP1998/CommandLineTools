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
                new Converter().convert("some string", String.class),
                is("some string")
        );
    }

    @Test
    public void testFileConversionValid() {
        assertThat(
                new Converter().convert("/asdf/ghjk lmno/pqrs.txt", File.class),
                is(new File("/asdf/ghjk lmno/pqrs.txt"))
        );
    }

    @Test
    public void testFileConversionInvalid() {
        assertThat(
                new Converter().convert("/asd?/ghjk.jpg", File.class),
                nullValue()
        );
    }

    @Test
    public void testFileNamingTemplateConversionValid() {
        assertThat(
                new Converter().convert("{index:10}asdf - fdsa{extension}", FileNamingTemplate.class),
                is(FileNamingTemplate.parse("{index:10}asdf - fdsa{extension}"))
        );
    }

    @Test
    public void testFileNamingTemplateConversionInvalid() {
        assertThat(
                new Converter().convert("{indax:10}asdf - fdsa{extension}", FileNamingTemplate.class),
                nullValue()
        );
    }

    @Test
    public void testBooleanConversionValidTrue() {
        assertThat(
                new Converter().convert("true", Boolean.class),
                is(true)
        );
    }

    @Test
    public void testBooleanConversionValidFalse() {
        assertThat(
                new Converter().convert("false", Boolean.class),
                is(false)
        );
    }

    @Test
    public void testBooleanConversionInvalid() {
        assertThat(
                new Converter().convert("true af", Boolean.class),
                nullValue()
        );
    }

    @Test
    public void testDoubleConversionValid() {
        assertThat(
                new Converter().convert("1.234e+4", Double.class),
                is(1.234e+4d)
        );
    }

    @Test
    public void testDoubleConversionInvalid() {
        assertThat(
                new Converter().convert("1.2.3.4", Double.class),
                nullValue()
        );
    }

    @Test
    public void testFloatConversionValid() {
        assertThat(
                new Converter().convert("1.234e+4", Float.class),
                is(1.234e+4f)
        );
    }

    @Test
    public void testFloatConversionInvalid() {
        assertThat(
                new Converter().convert("1.2.3.4", Float.class),
                nullValue()
        );
    }

    @Test
    public void testByteConversionValid() {
        assertThat(
                new Converter().convert("125", Byte.class),
                is((byte) 125)
        );
    }

    @Test
    public void testByteConversionInvalid() {
        assertThat(
                new Converter().convert("12345", Byte.class),
                nullValue()
        );
    }

    @Test
    public void testShortConversionValid() {
        assertThat(
                new Converter().convert("23456", Short.class),
                is((short) 23456)
        );
    }

    @Test
    public void testShortConversionInvalid() {
        assertThat(
                new Converter().convert("123456", Short.class),
                nullValue()
        );
    }

    @Test
    public void testCharacterConversionValid() {
        assertThat(
                new Converter().convert("a", Character.class),
                is('a')
        );
    }

    @Test
    public void testCharacterConversionInvalid() {
        assertThat(
                new Converter().convert("ab", Character.class),
                nullValue()
        );
    }

    @Test
    public void testIntegerConversionValid() {
        assertThat(
                new Converter().convert("12345678", Integer.class),
                is(12345678)
        );
    }

    @Test
    public void testIntegerConversionInvalid() {
        assertThat(
                new Converter().convert("ab", Integer.class),
                nullValue()
        );
    }

    @Test
    public void testLongConversionValid() {
        assertThat(
                new Converter().convert("123456789101112", Long.class),
                is(123456789101112L)
        );
    }

    @Test
    public void testLongConversionInvalid() {
        assertThat(
                new Converter().convert("ab", Long.class),
                nullValue()
        );
    }

    @Test
    public void testEnumConversionValid() {
        assertThat(
                new Converter().convert("BLOCKED", Thread.State.class),
                is(Thread.State.BLOCKED)
        );
    }

    @Test
    public void testEnumConversionInvalid() {
        assertThat(
                new Converter().convert("anything but a valid string", Thread.State.class),
                nullValue()
        );
    }

    @Test
    public void testAnyInvalidConversion() {
        assertThat(
                new Converter().convert("anything", Object.class),
                nullValue()
        );
    }
}
