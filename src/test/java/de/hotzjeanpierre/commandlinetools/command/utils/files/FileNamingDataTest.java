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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FileNamingDataTest {

    @Test
    public void testCreationOfFileNamingData() {
        assertThat(
                new FileNamingData.Builder()
                    .setExtension("ext")
                    .setIndex(0)
                    .setOriginalLocation("/")
                    .setOriginalName("asdf")
                    .build(),
                is(new FileNamingData(
                        "asdf",
                        0,
                        "ext",
                        "/"
                ))
        );
    }

    @Test(expected = IllegalStateException.class)
    public void testCreationOfFileNamingDataWithoutName() {
        new FileNamingData.Builder()
                .setIndex(0)
                .setOriginalLocation("/")
                .setExtension(".ext")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreationOfFileNamingDataWithoutIndex() {
        new FileNamingData.Builder()
                .setOriginalLocation("/")
                .setExtension(".ext")
                .setOriginalName("asdf")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreationOfFileNamingDataWithoutExtension() {
        new FileNamingData.Builder()
                .setIndex(0)
                .setOriginalLocation("/")
                .setOriginalName("asdf")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreationOfFileNamingDataWithoutOriginalLocation() {
        new FileNamingData.Builder()
                .setIndex(0)
                .setExtension(".ext")
                .setOriginalName("asdf")
                .build();
    }

    @Test
    public void testCreationOfFileNamingDataOriginalNameIgnoringNull() {
        assertThat(
                new FileNamingData.Builder()
                        .setOriginalName("somename")
                        .setOriginalName(null)
                        .setIndex(0)
                        .setExtension(".ext")
                        .setOriginalLocation("/")
                        .build(),
                is(new FileNamingData(
                        "somename",
                        0,
                        ".ext",
                        "/"
                ))
        );
    }

    @Test
    public void testCreationOfFileNamingDataExtensionIgnoringNull() {
        assertThat(
                new FileNamingData.Builder()
                        .setOriginalName("somename")
                        .setIndex(0)
                        .setExtension(".ext")
                        .setExtension(null)
                        .setOriginalLocation("/")
                        .build(),
                is(new FileNamingData(
                        "somename",
                        0,
                        ".ext",
                        "/"
                ))
        );
    }

    @Test
    public void testCreationOfFileNamingDataOriginalLocationIgnoringNull() {
        assertThat(
                new FileNamingData.Builder()
                        .setOriginalName("somename")
                        .setIndex(0)
                        .setExtension(".ext")
                        .setExtension(null)
                        .setOriginalLocation("/")
                        .setOriginalLocation(null)
                        .build(),
                is(new FileNamingData(
                        "somename",
                        0,
                        ".ext",
                        "/"
                ))
        );
    }

    @Test
    public void testCreationOfFileNamingDataFromEncryptionResult() {
        assertThat(
                FileNamingData.Builder.build(
                        new FileEncryptor.EncryptionResult(
                                File.separator + "some" + File.separator + "goddamn folder" + File.separator + "somefile.someextension",
                                new byte[0]
                        ),
                        0
                ),
                is(new FileNamingData(
                        "somefile",
                        0,
                        ".someextension",
                        File.separator + "some" + File.separator + "goddamn folder" + File.separator
                ))
        );
    }

    @Test
    public void testFileNamingDataToStringFormat() {
        assertThat(
                new FileNamingData(
                        "name",
                        123,
                        ".ext",
                        "/the/location/"
                ).toString(),
                is("FileNamingDate[originalName='name', index=123, extension='.ext', originalLocation='/the/location/']")
        );
    }

    @Test
    public void testGetOriginalName() {
        assertThat(
                new FileNamingData(
                        "name",
                        123,
                        ".ext",
                        "/the/location"
                ).getOriginalName(),
                is("name")
        );
    }

    @Test
    public void testGetIndex() {
        assertThat(
                new FileNamingData(
                        "name",
                        123,
                        ".ext",
                        "/the/location"
                ).getIndex(),
                is(123)
        );
    }

    @Test
    public void testGetExtension() {
        assertThat(
                new FileNamingData(
                        "name",
                        123,
                        ".ext",
                        "/the/location"
                ).getExtension(),
                is(".ext")
        );
    }

    @Test
    public void testGetOriginalLocation() {
        assertThat(
                new FileNamingData(
                        "name",
                        123,
                        ".ext",
                        "/the/location"
                ).getOriginalLocation(),
                is("/the/location")
        );
    }

    @Test
    public void testEqualsWithIncompatibleTypes() {
        assertThat(
                new FileNamingData(
                        "name",
                        123,
                        ".ext",
                        "/location/"
                ).equals(new Object()),
                is(false)
        );
    }
}