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

import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import de.hotzjeanpierre.commandlinetools.command.utils.arrays.ArrayHelper;
import org.junit.*;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

public class FileListerTest {

    @BeforeClass
    public static void setupFiles() throws IOException {
        for(int i = 0; i < filesToTestOn.length; i++) {
            if(i < 8) {
                if(!filesToTestOn[i].mkdir()) {
                    throw new IOException("Couldn't create folder required for testing.");
                }
            } else {
                if(!filesToTestOn[i].createNewFile()) {
                    throw new IOException("Couldn't create file required for testing.");
                }
            }
        }

        System.out.println("Setup files for testing.");
    }

    @AfterClass
    public static void deleteFiles() {
        for(int i = filesToTestOn.length - 1; i >= 0; i--) {
            if(!filesToTestOn[i].delete()) {
                System.out.println(StringProcessing.format(
                        "Couldn't delete file / folder: '{0}'",
                        filesToTestOn[i]
                ));
            }
        }

        System.out.println("Cleaned up files for testing.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testListFilesWithNonExistingFolder() {
        FileLister.list(
                new File(System.getProperty("user.home"), "/somenonexistingfolder/"),
                true,
                FilterMode.None,
                "",
                true
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testListFilesWithFile() {
        FileLister.list(
                new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0008.jpq"),
                true,
                FilterMode.None,
                "",
                true
        );
    }

    @Test
    public void testListFilesAllFilesWithFolders() {
        assertThat(
                ArrayHelper.arrayContentEquals(
                        FileLister.list(rootFile, true, FilterMode.None, "", true),
                        expectedin_testListFilesAllFilesWithFolders
                ),
                is(true)
        );
    }

    @Test
    public void testListFilesWithoutSubdirectories() {
        assertThat(
                ArrayHelper.arrayContentEquals(
                        FileLister.list(rootFile, false, FilterMode.None, "", true),
                        expectedin_testListFilesWithoutSubdirectories
                ),
                is(true)
        );
    }

    @Test
    public void testListFilesWithFilterAllowOnly() {
        assertThat(
                ArrayHelper.arrayContentEquals(
                        FileLister.list(rootFile, true, FilterMode.AllowOnly, "txt", true),
                        expectedin_testListFilesWithFilterAllowOnly
                ),
                is(true)
        );
    }

    @Test
    public void testListFilesWithFilterFilter() {
        assertThat(
                ArrayHelper.arrayContentEquals(
                        FileLister.list(rootFile, true, FilterMode.Filter, "txt", true),
                        expectedin_testListFilesWithFilterFilter
                ),
                is(true)
        );
    }

    ///
    /// Expected results of the tests and the files used for testing in general.
    /// Not really very nice to look at and also was not nive to create, so please don't judge <3
    ///

    private static final File[] expectedin_testListFilesAllFilesWithFolders = {
            new File(System.getProperty("user.home"), "/somefolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/def/"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0010.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0010.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0010.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0010.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0010.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0010.jpq"),
    };

    private static final File[] expectedin_testListFilesWithoutSubdirectories = {
            new File(System.getProperty("user.home"), "/somefolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0010.jpq")
    };

    private static final File[] expectedin_testListFilesWithFilterAllowOnly = {
            new File(System.getProperty("user.home"), "/somefolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/def/"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0010.txt")
    };

    private static final File[] expectedin_testListFilesWithFilterFilter = {
            new File(System.getProperty("user.home"), "/somefolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/def/"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0010.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0010.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0010.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0010.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0010.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0010.jpq")
    };




    private static final File[] filesToTestOn = {
            new File(System.getProperty("user.home"), "/somefolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/def/"),


            new File(System.getProperty("user.home"), "/somefolder/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/someimage0010.jpq"),

            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/someimage0010.jpq"),

            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/txt/sometext0010.txt"),

            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/b/someimage0010.jpq"),

            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some subfolder/c/someimage0010.jpq"),

            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/someimage0010.jpq"),

            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0001.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0002.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0003.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0004.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0005.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0006.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0007.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0008.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0009.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/sometext0010.txt"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0001.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0002.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0003.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0004.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0005.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0006.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0007.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0008.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0009.jpq"),
            new File(System.getProperty("user.home"), "/somefolder/some other subfolder/abc/someimage0010.jpq"),
    };

    private static final File rootFile = filesToTestOn[0];
}
