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

package de.hotzjeanpierre.commandlinetools.command.utils.files;

import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

public class FileNamingTemplateTest {

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidFormat() {
        FileNamingTemplate.parse("{");
    }

    @Test
    public void testParseValidFormat() {
        assertThat(
                FileNamingTemplate.parse("{originallocation}{index}{{{  originalname\t}}}{{index 123}}{index : 1234}asdf{extension}.jpg"),
                is(new FileNamingTemplate(new FileNamingTemplate.Token[]{
                        new FileNamingTemplate.OriginalLocationToken(),
                        new FileNamingTemplate.IndexToken(),
                        new FileNamingTemplate.TextToken("{"),
                        new FileNamingTemplate.OriginalNameToken(),
                        new FileNamingTemplate.TextToken("}{index 123}"),
                        new FileNamingTemplate.IndexToken(1234),
                        new FileNamingTemplate.TextToken("asdf"),
                        new FileNamingTemplate.ExtensionToken(),
                        new FileNamingTemplate.TextToken(".jpg")
                }))
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseIndexInvalidLength() {
        FileNamingTemplate.parse("{index:9999999999}");
    }

    @Test
    public void testToStringFormat() {
        assertThat(
                FileNamingTemplate.parse(
                        "{ originallocation }{index:0} {{ asdf }} { index : 1234 }{ originalname }{extension}"
                ).toString(),
                is("{originallocation}{index} {{ asdf }} {index:1234}{originalname}{extension}")
        );
    }

    @Test
    public void testProduceFileName() {
        assertThat(
                FileNamingTemplate.parse("{originallocation}{index} - {originalname}{extension}")
                        .produceFileNane(new FileNamingData.Builder()
                                .setExtension(".ext")
                                .setOriginalLocation("/some/location/")
                                .setOriginalName("filename")
                                .setIndex(4321)
                                .build()
                        ),
                is("/some/location/4321 - filename.ext")
        );
    }

    @Test(expected = InvocationTargetException.class)
    public void testResolveTokenIllegalAccess() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method toInvoke = FileNamingTemplate.class.getDeclaredMethod("resolveToken", String.class);
        toInvoke.setAccessible(true);
        toInvoke.invoke(null, "someillegaltoken");
    }

    @Test
    public void testEqualsWithDifferentLengths() {
        assertThat(
                FileNamingTemplate.parse("{index}{originalname}asdf{extension}").equals(FileNamingTemplate.parse("")),
                is(false)
        );
    }

    @Test
    public void testEqualsWithNonMatchingType() {
        assertThat(
                FileNamingTemplate.parse("").equals(new Object()),
                is(false)
        );
    }

    @Test
    public void testEqualsWithNull() {
        assertThat(
                FileNamingTemplate.parse("").equals(null),
                is(false)
        );
    }

    @Test
    public void testEqualsWithSameLengthButDifferentTokens() {
        assertThat(
                FileNamingTemplate.parse("asdf{index}{originalname}{extension}")
                        .equals(FileNamingTemplate.parse("asdf{index}{{originalname}}{extension}")),
                is(false)
        );
    }
}
