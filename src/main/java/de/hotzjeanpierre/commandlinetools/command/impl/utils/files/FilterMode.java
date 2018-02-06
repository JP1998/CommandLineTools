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

package de.hotzjeanpierre.commandlinetools.command.impl.utils.files;

/**
 * This enum defines the single modes in which a filter can perform.
 */
public enum FilterMode {

    /**
     * Does not apply any kind of filtering,
     * and instead simply allows every kind of file
     */
    None,

    /**
     * Filters all the files with an extension that is given.
     * Thus any file with an extension that is contained in the list of
     * extensions to filter will not be contained
     */
    Filter,

    /**
     * Filters all the files with an extension that is not given.
     * Thus any file with an extension that is contained in the list of
     * extensions to filter will be contained.
     */
    AllowOnly
}
