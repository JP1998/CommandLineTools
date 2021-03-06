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
 *
 */
public interface FilterModeApplication {

    /**
     * This method is used to determine whether a given file is to be
     * listed after applying the given filter parameters.
     *
     * @param f         the file to check
     * @param filter    the extensions to filter
     * @param lf        whether to list folders
     * @return whether or not the given file is supposed to be listed
     */
    boolean allow(@NotNull File f, String filter, boolean lf);
}
