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

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * This class can be used to safely delete folders in case they are empty.
 */
public class EmptyFolderDeleter {

    /**
     * This method first checks whether the given folder is empty (i.e. the
     * folder and any sub directories do not contain files), and deletes it and
     * its sub directories in case it is empty.
     *
     * @param folder the folder to delete
     */
    public static void deleteIfEmpty(@NotNull File folder) {
        if(isFolderNotEmpty(folder)) {
            return;
        }

        deleteWithChildren(folder);
    }

    /**
     * This method deletes a folder and all its sub directories.
     * The given folder is assumed not to contain any files other than
     * directories, which in turn also applies to those sub directories.
     *
     * @param folder the folder to delete
     */
    private static void deleteWithChildren(@NotNull File folder) {
        File[] children = folder.listFiles();

        if(children != null) {
            // in case there are children (which can only be folders)
            for (File fileToDelete : children) {
                // we'll delete each and every one of them
                deleteWithChildren(fileToDelete);
            }
        }

        // noinspection ResultOfMethodCallIgnored
        folder.delete();
    }

    /**
     * This method is used to check whether a given folder is empty.
     * An empty folder is defined to be a directory (according to {@link File#isDirectory()})
     * either without any files contained, or only empty folders contained as files.
     *
     * @param f the file to check on whether it is an empty folder
     * @return whether the given file is an empty folder
     */
    private static boolean isFolderNotEmpty(@NotNull File f) {
        // an empty folder must be a directory
        if(!f.isDirectory()) {
            return true;
        }

        File[] children = f.listFiles();

        if(children == null || children.length == 0) {
            // a directory without children is defined as an empty folder
            return false;
        } else {
            // if the directory is not empty we'll check ever child whether it is an empty folder,
            // and if one of them is not, we'll know that the folder is not empty
            for(File fileToCheck : children) {
                if(isFolderNotEmpty(fileToCheck)) {
                    return true;
                }
            }

            // if there was no non-empty folder the requirements for an empty folder are met
            return false;
        }
    }
}
