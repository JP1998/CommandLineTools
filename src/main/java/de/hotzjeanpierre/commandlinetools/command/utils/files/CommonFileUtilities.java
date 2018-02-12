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
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * This class contains several methods that can help you on
 * processing files and / or abstract path names used for files.
 */
public class CommonFileUtilities {

    /**
     * This method extracts the file extension of the given file.
     * In case the given file is a directory this method will return {@code null}.
     *
     * @param f the file to extract the file extension of
     * @return the files extension
     */
    @Nullable
    public static String extractFileExtension(@NotNull File f) {
        if(f.isDirectory()) {
            return null;
        }
        return extractFileExtension(f.getAbsolutePath());
    }

    /**
     * This method extracts the file extension of the given path.
     * Since the given path cannot be checked on whether it is a directory,
     * or not this method will return an empty string regardless of whether it
     * is a file without extension or a directory.
     *
     * @param f the path to extract the file extension of
     * @return the paths extension
     */
    @NotNull
    public static String extractFileExtension(@NotNull String f) {
        int index = getExtensionPointIndex(f);
        return index != -1 ? f.substring(index, f.length()) : "";
    }

    /**
     * This method extracts the file name of the given file.
     *
     * @param f the file to extract the file name of
     * @return the name without the extension of the given file
     */
    @NotNull
    public static String extractFileName(@NotNull File f)    {
        return extractFileName(f.getAbsolutePath());
    }

    /**
     * This method extracts the file name of the given abstract path.
     *
     * @param f the path to extract the file name of
     * @return the name without the extension of the given file
     */
    @NotNull
    public static String extractFileName(@NotNull String f) {
        String name = getNameWithExtension(f);
        int index = name.lastIndexOf('.');

        return index != -1 ? name.substring(0, index) : name;
    }

    /**
     * This method extracts the path to the given file.
     *
     * @param f the file to extract the path to
     * @return the path to the given file
     */
    @NotNull
    public static String extractFolderPath(@NotNull File f) {
        if(f.isDirectory()) {
            return f.getAbsolutePath();
        }
        return extractFolderPath(f.getAbsolutePath());
    }

    /**
     * This method extracts the path to the given abstract path.
     *
     * @param f the abstract path to a file to extract its folder path from
     * @return the path to the given path of a file
     */
    @NotNull
    public static String extractFolderPath(@NotNull String f) {
        int index = f.lastIndexOf(File.separatorChar);
        return f.substring(0, index + 1);
    }

    /**
     * This method extracts the file name with its extension from the given file.
     *
     * @param f the abstract path to a file whose name is to be extracted
     * @return the given files name with its extension
     */
    @NotNull
    private static String getNameWithExtension(@NotNull String f) {
        int index = f.lastIndexOf(File.separatorChar);
        return f.substring(index + 1, f.length());
    }

    /**
     * This method gives you the index of the point for the file extension of the given file.
     *
     * @param f the abstract path to a file whose extension point is to be found
     * @return the index of the point for the extension; {@code -1} if the file doesn't have an extension
     */
    private static int getExtensionPointIndex(String f) {
        String name = getNameWithExtension(f);
        if(name.lastIndexOf('.') != -1) {
            return f.lastIndexOf('.');
        } else {
            return -1;
        }
    }
}
