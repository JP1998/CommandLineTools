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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class can be used to easily list files within a directory.
 * It supports search in sub directories, and also filtering
 * by file extension.
 */
public class FileLister {

    /**
     * This method lists all the files within the given folder. if {@code searchSubDir} is
     * set to {@code true} the sub directories (thus in any sub directory of the given file)
     * will also be searched within. The given filter is to (duh?!) filter the files. The folders
     * can be filtered with the parameter {@code listFolders}.
     *
     * @param folder       the file to list files from
     * @param searchSubDir whether to search within sub directories or not
     * @param mode         the mode the filter is supposed to perform in
     * @param filter       the extensions to apply the filter to, separated by semicolons
     * @param listFolders  whether to list folders or not
     * @return the list of files within the given folder with given parameters applied
     */
    @NotNull
    public static File[] list(@NotNull File folder, boolean searchSubDir, FilterMode mode, String filter, boolean listFolders) {
        // check whether the given folder actually exists and whether it is a folder
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "The file '{0}' is not a directory we could possibly list files from.",
                    folder.getAbsolutePath()
            ));
        }

        // create a list for the listed files, and a Stack for the folders that are to be processed
        List<File> files = new ArrayList<>();
        Stack<File> directories = new Stack<>();

        // push the original folder onto the stack we are supposed to processed
        directories.push(folder);

        // we'll go through every directory that is to be processed, while the number of
        // folders might be incremented during this loop, whereas a for-loop is not practical
        while (!directories.empty()) {
            File directory = directories.pop();

            // we'll list *all* files within the currently processed folder
            File[] toProcess = directory.listFiles();

            // if the processed folder is not empty
            if (toProcess != null) {
                // we'll iterate over all files within the folder
                for (File f : toProcess) {
                    // if a file is to list we'll add it
                    if (fileToList(f, mode, filter, listFolders)) {
                        files.add(f);
                    }

                    // and if it is a directory and we're supposed to also
                    // search within sub directories we'll push it onto the Stack
                    if (f.isDirectory() && searchSubDir) {
                        directories.push(f);
                    }
                }
            }
        }

        // finally convert the list into an array and return it
        File[] result = new File[files.size()];
        return files.toArray(result);
    }

    /**
     * This method is used to determine whether a given file is to be
     * listed after applying the given filter parameters.
     *
     * @param f           the file to check
     * @param mode        the mode the filter is performing in
     * @param filter      the extensions to filter
     * @param listFolders whether to list folders
     * @return whether or not the given file is supposed to be listed
     */
    private static boolean fileToList(@NotNull File f, FilterMode mode, String filter, boolean listFolders) {
        if (f.isDirectory()) {
            return listFolders;
        }

        if (mode == FilterMode.Filter) {
            return !fileExtensionContained(findExtension(f), filter);
        } else if (mode == FilterMode.AllowOnly) {
            return fileExtensionContained(findExtension(f), filter);
        } else {
            return true /* !f.isDirectory() || listFolders */;
        }
    }

    /**
     * This method gives you the extension of the given file.
     *
     * @param f the file to determine the extension of
     * @return the extension of the given file
     */
    @NotNull
    private static String findExtension(@NotNull File f) {
        int dot = f.getAbsolutePath().lastIndexOf('.');
        return dot == -1 ? "" : f.getAbsolutePath().substring(dot + 1);
    }

    /**
     * This method determines whether a specific extension is to be filtered.
     *
     * @param extension         the extension to check
     * @param extensionsToCheck the list of extensions to filter
     * @return whether or not the given extension is within the list
     */
    private static boolean fileExtensionContained(String extension, @NotNull String extensionsToCheck) {
        String[] extensions = extensionsToCheck.split(";");

        for (String ext : extensions) {
            if (extension.trim().equalsIgnoreCase(ext.trim())) {
                return true;
            }
        }

        return false;
    }
}
