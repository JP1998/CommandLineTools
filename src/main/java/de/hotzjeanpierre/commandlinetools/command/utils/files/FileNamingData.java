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

/**
 * This class represents the data used by a FileNamingTemplate.<br>
 * Thus it holds values for the name of the original file, the index
 * of the processed file, the extension and the sub folder structure
 * of the original file.
 */
public class FileNamingData {

    /**
     * The name of the original file
     */
    private final String originalName;
    /**
     * The index of this file
     */
    private final int index;
    /**
     * The extension of the original file
     */
    private final String extension;
    /**
     * The sub folder structure of the original file
     */
    private final String originalLocation;

    /**
     * Creates a FileNamingData-object with the given values.
     * This constructor may only be called by the {@link Builder}-class
     * within this class, and is thus set to private.
     *
     * @param originalName     The name of the original file
     * @param index            The index of this file
     * @param extension        The extension of the original file
     * @param originalLocation The location (folder) of the original file
     */
    /* package-protected */ FileNamingData(
            String originalName,
            int index,
            String extension,
            String originalLocation
    ) {
        this.originalName = originalName;
        this.index = index;
        this.extension = extension;
        this.originalLocation = originalLocation;
    }

    /**
     * @return The name of the original file
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * @return The index of this file
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return The extension of the original file
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @return The sub folder structure of the original file
     */
    public String getOriginalLocation() {
        return originalLocation;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FileNamingData) {
            FileNamingData data = (FileNamingData) obj;

            return data.index == index &&
                    data.extension.equals(extension) &&
                    data.originalLocation.equals(originalLocation) &&
                    data.originalName.equals(originalName);
        }
        return false;
    }

    @Override
    public String toString() {
        return StringProcessing.format(
                "FileNamingDate[originalName='{0}', index={1}, extension='{2}', originalLocation='{3}']",
                originalName,
                index,
                extension,
                originalLocation
        );
    }

    /**
     * The builder class for {@link FileNamingData}.
     */
    public static class Builder {

        /**
         * The name of the original file
         */
        private String originalName;
        /**
         * The index of this file
         */
        private int index;
        /**
         * Whether the index has been initialized with a custom value
         */
        private boolean indexInitialised;
        /**
         * The extension of the original file
         */
        private String extension;
        /**
         * The sub folder structure of the original file
         */
        private String originalLocation;

        /**
         * Creates a new Builder for FileNamingData-objects with uninitialized values.
         */
        public Builder() {
            this.originalName = null;
            this.index = -1;
            this.indexInitialised = false;
            this.extension = null;
            this.originalLocation = null;
        }

        /**
         * This method sets the name of the original file to use for the data you want to produce.
         * The value {@code null} will be ignored.
         *
         * @param name the name that is to be set
         * @return the Builder for method chaining
         */
        public Builder setOriginalName(String name) {
            if (name != null) {
                this.originalName = name;
            }
            return this;
        }

        /**
         * This method sets the index of the file to use for the data you want to produce.
         *
         * @param index the index that is to be set
         * @return the Builder for method chaining
         */
        public Builder setIndex(int index) {
            this.index = index;
            this.indexInitialised = true;
            return this;
        }

        /**
         * This method sets the extension of the file to use for the data you want to produce.
         *
         * @param ext the extension that is to be set
         * @return the Builder for method chaining
         */
        public Builder setExtension(String ext) {
            if (ext != null) {
                this.extension = ext;
            }
            return this;
        }

        /**
         * This method sets the sub folder structure of the file to use for the data you want to produce.
         *
         * @param originalLocation the location (folder) that is to be set
         * @return the Builder for method chaining
         */
        public Builder setOriginalLocation(String originalLocation) {
            if (originalLocation != null) {
                this.originalLocation = originalLocation;
            }
            return this;
        }

        /**
         * This method builds a FileNamingData-object with the current state of the Builder.
         * In case any value has not been initialized yet this method will throw a {@link IllegalStateException}.
         *
         * @return the FileNamingData-object with the values of the current state of the Builder
         * @throws IllegalStateException in case any value has not been initialized yet.
         */
        public FileNamingData build() {
            if (originalName == null) {
                throw new IllegalStateException(
                        "The original file name may not be null."
                );
            }
            if (!indexInitialised) {
                throw new IllegalStateException(
                        "The index may not be uninitialized."
                );
            }
            if (extension == null) {
                throw new IllegalStateException(
                        "The extension may not be null."
                );
            }
            if (originalLocation == null) {
                throw new IllegalStateException(
                        "The subfolders may not be null."
                );
            }

            return new FileNamingData(
                    this.originalName,
                    this.index,
                    this.extension,
                    this.originalLocation
            );
        }

        /**
         * This method creates an instance of FileNamingData with regard of the given {@link FileEncryptor.EncryptionResult},
         * and the given index.
         *
         * @param result the result to evaluate
         * @param index  the index to give the FileNamingData
         * @return the FileNamingData with the extracted information
         */
        public static FileNamingData build(@NotNull FileEncryptor.EncryptionResult result, int index) {
            return new Builder()
                    .setOriginalName(CommonFileUtilities.extractFileName(result.getOriginalName()))
                    .setExtension(CommonFileUtilities.extractFileExtension(result.getOriginalName()))
                    .setIndex(index)
                    .setOriginalLocation(CommonFileUtilities.extractFolderPath(result.getOriginalName()))
                    .build();
        }
    }
}
