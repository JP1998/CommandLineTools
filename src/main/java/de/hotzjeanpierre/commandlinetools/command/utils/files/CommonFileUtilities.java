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
import java.io.IOException;
import java.nio.file.Files;

/**
 * This class contains several methods that can help you on
 * processing files and / or abstract path names used for files.
 */
public class CommonFileUtilities {

    /**
     * This method reads all the byte-data from the given file.
     *
     * @param file the file to read from.
     * @return the data contained in the given file.
     * @throws IOException in case an error occurs during reading (e.g. the file doesn't exist)
     */
    @NotNull
    public static byte[] readFile(@NotNull File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * This method writes the given data to the given file.
     * Any missing parent folders of the file will not be created, whereas this method
     * will throw an exception in case there are any folders missing.
     *
     * @param file the path to the file to which the data is to be saved
     * @param data the data that is to be saved to the given file
     * @throws IOException in case an error occurs during writing (e.g. the folder this file lies in does not exist)
     */
    public static void writeFile(@NotNull File file, @NotNull byte[] data) throws IOException {
        Files.write(file.toPath(), data);
    }

    /**
     * This method extracts the file extension of the given file while the point
     * of the extension will be included in the returned value.
     * In case the given file is a directory this method will return {@code null}.
     *
     * @param f the file to extract the file extension of
     * @return the files extension
     */
    @Nullable
    public static String extractFileExtensionContainingPoint(@NotNull File f) {
        if(f.isDirectory()) {
            return null;
        }
        return extractFileExtensionContainingPoint(f.getAbsolutePath());
    }

    /**
     * This method extracts the file extension of the given path while the point
     * of the extension will be included in the returned value.
     * Since the given path cannot be checked on whether it is a directory,
     * or not this method will return an empty string regardless of whether it
     * is a file without extension or a directory.
     *
     * @param f the path to extract the file extension of
     * @return the paths extension
     */
    @NotNull
    public static String extractFileExtensionContainingPoint(@NotNull String f) {
        int index = getExtensionPointIndex(f);
        return index != -1 ? f.substring(index, f.length()) : "";
    }

    /**
     * This method extracts the file extension of the given file while the point
     * of the extension will not be included in the returned value.
     * In case the given file is a directory this method will return {@code null}.
     *
     * @param f the file to extract the file extension of
     * @return the files extension
     */
    @Nullable
    public static String extractFileExtension(File f) {
        if(f.isDirectory()) {
            return null;
        }
        String absPath = f.getAbsolutePath();
        int index = getExtensionPointIndex(absPath);

        return index != -1 ? absPath.substring(index + 1, absPath.length()) : "";
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

    /**
     * This method determines whether a specific extension is to be filtered.
     *
     * @param extension         the extension to check
     * @param extensionsToCheck the list of extensions to filter
     * @return whether or not the given extension is within the list
     */
    public static boolean fileExtensionContained(String extension, @NotNull String extensionsToCheck) {
        String[] extensions = extensionsToCheck.split(";");

        for (String ext : extensions) {
            if (extension.trim().equalsIgnoreCase(ext.trim())) {
                return true;
            }
        }

        return false;
    }
}
