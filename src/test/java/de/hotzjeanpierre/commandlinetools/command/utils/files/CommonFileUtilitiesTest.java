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
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class CommonFileUtilitiesTest {

    @Test
    public void testExtractFileExtensionFileOnDirectory() {
        assertThat(
                CommonFileUtilities.extractFileExtension(
                        new File(System.getProperty("user.home"))
                ),
                nullValue()
        );
    }

    @Test
    public void testExtractFileExtensionFileOnFileWithExtension() {
        assertThat(
                CommonFileUtilities.extractFileExtension(
                        new File(
                                System.getProperty("user.home"),
                                "somefile.someextension"
                        )
                ),
                is(".someextension")
        );
    }

    @Test
    public void testExtractFileExtensionFileOnFileWithoutExtension() {
        assertThat(
                CommonFileUtilities.extractFileExtension(
                        new File(
                                System.getProperty("user.home"),
                                "somefilewithoutextension"
                        )
                ),
                is("")
        );
    }

    @Test
    public void testExtractFileExtensionFileOnFileWithExtensionInGitlikeFolder() {
        assertThat(
                CommonFileUtilities.extractFileExtension(
                        new File(
                                System.getProperty("user.home"),
                                ".someweirdfolder/somefile.someextension"
                        )
                ),
                is(".someextension")
        );
    }

    @Test
    public void testExtractFileExtensionFileOnFileWithoutExtensionInGitlikeFolder() {
        assertThat(
                CommonFileUtilities.extractFileExtension(
                        new File(
                                System.getProperty("user.home"),
                                ".someweirdfolder/somefilewithoutextension"
                        )
                ),
                is("")
        );
    }

    @Test
    public void testExtractFileExtensionStringOnDirectory() {
        assertThat(
                CommonFileUtilities.extractFileExtension(
                        System.getProperty("user.home")
                ),
                is("")
        );
    }

    @Test
    public void testExtractFileExtensionStringOnFileWithExtension() {
        assertThat(
                CommonFileUtilities.extractFileExtension(
                        System.getProperty("user.home") + File.separator + "somefile.someextension"
                ),
                is(".someextension")
        );
    }

    @Test
    public void testExtractFileExtensionStringOnFileWithoutExtension() {
        assertThat(
                CommonFileUtilities.extractFileExtension(
                        System.getProperty("user.home") + File.separator + "somefilewithoutextension"
                ),
                is("")
        );
    }

    @Test
    public void testExtractFileExtensionStringOnFileWithExtensionInGitlikeFolder() {
        assertThat(
                CommonFileUtilities.extractFileExtension(
                        System.getProperty("user.home") + File.separator + ".someweirdfolder" + File.separator + "somefile.someextension"
                ),
                is(".someextension")
        );
    }

    @Test
    public void testExtractFileExtensionStringOnFileWithoutExtensionInGitlikeFolder() {
        assertThat(
                CommonFileUtilities.extractFileExtension(
                        System.getProperty("user.home") + File.separator + ".someweirdfolder" + File.separator + "somefilewithoutextension"
                ),
                is("")
        );
    }

    @Test
    public void testExtractFileNameFileOnFileWithExtension() {
        assertThat(
                CommonFileUtilities.extractFileName(
                        new File(
                                System.getProperty("user.home"),
                                "somefile.someextension"
                        )
                ),
                is("somefile")
        );
    }

    @Test
    public void testExtractFileNameFileOnFileWithoutExtension() {
        assertThat(
                CommonFileUtilities.extractFileName(
                        new File(
                                System.getProperty("user.home"),
                                "somefile"
                        )
                ),
                is("somefile")
        );
    }

    @Test
    public void testExtractFileNameStringOnFileWithExtension() {
        assertThat(
                CommonFileUtilities.extractFileName(
                        System.getProperty("user.home") + File.separator + "somefile.someextension"
                ),
                is("somefile")
        );
    }

    @Test
    public void testExtractFileNameStringOnFileWithoutExtension() {
        assertThat(
                CommonFileUtilities.extractFileName(
                        System.getProperty("user.home") + File.separator + "somefile"
                ),
                is("somefile")
        );
    }

    @Test
    public void testExtractFolderPathFileOnDirectory() {
        assertThat(
                CommonFileUtilities.extractFolderPath(new File(System.getProperty("user.home"))),
                is(System.getProperty("user.home"))
        );
    }

    @Test
    public void testExtractFolderPathFileOnFile() {
        assertThat(
                new File(CommonFileUtilities.extractFolderPath(new File(System.getProperty("user.home"), "somefile.someextension"))),
                is(new File(System.getProperty("user.home")))
        );
    }


    @Test
    public void testExtractFolderPathStringOnDirectory() {
        assertThat(
                CommonFileUtilities.extractFolderPath(new File(System.getProperty("user.home"))),
                is(System.getProperty("user.home"))
        );
    }
}
