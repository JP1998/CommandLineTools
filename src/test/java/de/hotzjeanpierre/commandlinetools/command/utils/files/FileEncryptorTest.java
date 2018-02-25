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

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

public class FileEncryptorTest {

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

        assertArrayEquals(
                FileEncryptor.readFile(toRead),
                TESTREADFILE_EXPECTED
        );

        toRead.delete();
    }

    private static final String TESTWRITEFILE_TOWRITE = "This is some different but also weird kind of text.\nIt is used to test whether the class FileEncryptor actually correctly writes data to files.";
    private static final byte[] TESTWRITEFILE_EXPECTED = TESTWRITEFILE_TOWRITE.getBytes();

    @Test
    public void testWriteFile() throws IOException {
        File toWrite = new File(System.getProperty("user.home"), "sometestfile.txt");

        FileEncryptor.writeFile(toWrite, TESTWRITEFILE_EXPECTED);

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
        
        assertArrayEquals(
                readData,
                TESTWRITEFILE_EXPECTED
        );

        toWrite.delete();
    }
}
