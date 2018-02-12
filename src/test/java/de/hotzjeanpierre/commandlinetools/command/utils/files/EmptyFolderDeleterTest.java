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

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class EmptyFolderDeleterTest {

    private static File fileToUse = new File(
            System.getProperty("user.home"),
            "asdf.txt"
    );
    private static File emptyFolderToUse = new File(
            System.getProperty("user.home"),
            "empty folder"
    );
    private static File emptyFolderWithChildrenToUse = new File(
            System.getProperty("user.home"),
            "some/folder/in here"
    );
    private static File emptyFolderWithChildrenToUseAsRoot = new File(
            System.getProperty("user.home"),
            "some"
    );
    private static File nonEmptyFolderToUseAsRoot = new File(
            System.getProperty("user.home"),
            "some nonempty"
    );
    private static File nonEmptyFolderToUse = new File(
            System.getProperty("user.home"),
            "some nonempty/folder/right there"
    );
    private static File fileInNonEmptyFolderToUse = new File(
            System.getProperty("user.home"),
            "some nonempty/folder/asdf.txt"
    );

    @Test
    public void testDeleteIfEmptyWithFile() throws IOException {
        boolean success = fileToUse.createNewFile();

        EmptyFolderDeleter.deleteIfEmpty(fileToUse);

        success &= fileToUse.exists();

        assertThat(success, is(true));
    }

    @Test
    public void testDeleteIfEmptyWithEmptyDirectory() {
        boolean success = emptyFolderToUse.mkdirs();

        EmptyFolderDeleter.deleteIfEmpty(emptyFolderToUse);

        success &= !emptyFolderToUse.exists();

        assertThat(success, is(true));
    }

    @Test
    public void testDeleteIfEmptyWithEmptyDirectoryWithChildren() {
        boolean success = emptyFolderWithChildrenToUse.mkdirs();

        success &= emptyFolderWithChildrenToUse.exists() &&
                emptyFolderWithChildrenToUse.isDirectory() &&
                emptyFolderWithChildrenToUseAsRoot.listFiles().length == 1;

        EmptyFolderDeleter.deleteIfEmpty(emptyFolderWithChildrenToUseAsRoot);

        success &= !emptyFolderWithChildrenToUseAsRoot.exists();

        assertThat(success, is(true));
    }

    @Test
    public void testDeleteIfEmptyNonEmptyDirectory() throws IOException {
        boolean success = nonEmptyFolderToUse.mkdirs();

        success &= fileInNonEmptyFolderToUse.createNewFile();

        EmptyFolderDeleter.deleteIfEmpty(nonEmptyFolderToUseAsRoot);

        success &= nonEmptyFolderToUseAsRoot.exists() &&
                nonEmptyFolderToUse.exists() &&
                fileInNonEmptyFolderToUse.exists();

        assertThat(success, is(true));
    }

    @After
    public void cleanUp() {
        fileToUse.delete();
        emptyFolderToUse.delete();
        emptyFolderWithChildrenToUse.delete();
        new File(System.getProperty("user.home"), "some/folder").delete();
        new File(System.getProperty("user.home"), "some").delete();
        fileInNonEmptyFolderToUse.delete();
        nonEmptyFolderToUse.delete();
        new File(System.getProperty("user.home"), "some nonempty/folder").delete();
        new File(System.getProperty("user.home"), "some nonempty").delete();
    }
}
