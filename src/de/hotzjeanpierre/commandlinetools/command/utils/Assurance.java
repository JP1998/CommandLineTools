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

package de.hotzjeanpierre.commandlinetools.command.utils;

/**
 * This class is a little helper class that contains methods
 * that make it easy to assure certain conditions.
 */
public class Assurance {

    /**
     * This method will try to load the class with its fully qualified name given.
     * Any exceptions (like {@link ClassNotFoundException}) are ignored.
     *
     * @param fullyQualifiedClassName the fully qualified name of the class to load
     */
    public static void tryLoadingClass(String fullyQualifiedClassName) {
        try {
            Class.forName(fullyQualifiedClassName);
        } catch (Exception ignored) {
        }
    }

    private Assurance() {
    }
}
