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

import java.io.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class CommonFileUtilitiesTest {

    private static final String TESTREADFILE_TOWRITE = "This is some weird kind of text.\nIt is used to test whether the class FileEncryptor actually correctly reads data from files.";
    private static final byte[] TESTREADFILE_EXPECTED = TESTREADFILE_TOWRITE.getBytes();

    @Test
    public void testReadFile() throws IOException {
        File toRead = new File(System.getProperty("user.home"), "sometestfile.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(toRead))) {
            writer.write(TESTREADFILE_TOWRITE);
        } catch (IOException e) {
            throw new AssertionError(
                    "Couldn't test since there was an error setting up the test environment.", e
            );
        }

        assertThat(
                CommonFileUtilities.readFile(toRead),
                is(TESTREADFILE_EXPECTED)
        );

        toRead.delete();
    }

    private static final String TESTWRITEFILE_TOWRITE = "This is some different but also weird kind of text.\nIt is used to test whether the class FileEncryptor actually correctly writes data to files.";
    private static final byte[] TESTWRITEFILE_EXPECTED = TESTWRITEFILE_TOWRITE.getBytes();

    @Test
    public void testWriteFile() throws IOException {
        File toWrite = new File(System.getProperty("user.home"), "sometestfile.txt");

        CommonFileUtilities.writeFile(toWrite, TESTWRITEFILE_EXPECTED);

        byte[] readData = new byte[(int) toWrite.length()];

        try (FileInputStream stream = new FileInputStream(toWrite)) {
            if(stream.read(readData) != readData.length) {
                throw new IOException("Length of file does not match the detected data.");
            }
        } catch (IOException e) {
            throw new AssertionError(
                    "Couldn't test since there was an error setting up the test environment.", e
            );
        }

        assertThat(
                readData,
                is(TESTWRITEFILE_EXPECTED)
        );

        toWrite.delete();
    }

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
