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

package de.hotzjeanpierre.commandlinetools.command.utils.arrays;

import org.jetbrains.annotations.Contract;

/**
 * This class provides some help with arrays.
 */
public class ArrayHelper {

    /**
     * This method checks whether the given element is contained in the given array.
     * In case the array is empty or {@code null}, {@code false} will be returned.
     * In case the given element is {@code null}, we'll search for a {@code null}-element
     * within the array, and if one is contained we'll return {@code true}; {@code false} otherwise.
     * In any other case we'll search for an {@code item} within the array, that
     * returns {@code true} at the check of {@code item.equals(element)}.
     *
     * @param arr The array to check on whether the given element is contained in it.
     * @param elements The elements to check whether it is in the given array.
     * @param <T> An arbitrary type that the array is declared with.
     * @return Whether any of the given elements are contained within the array.
     */
    @Contract("null, _ -> false")
    public static <T> boolean contains(T[] arr, T... elements) {
        if(arr == null || arr.length == 0) {
            return false;
        }

        for(T elementToTest : elements) {
            for (T item : arr) {
                if (item == null && elementToTest == null) {
                    return true;
                } else if (item != null) {
                    if (item.equals(elementToTest)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static Character[] cast(char[] characters) {
        Character[] result = new Character[characters.length];

        for(int i = 0; i < result.length; i++) {
            result[i] = characters[i];
        }

        return result;
    }

}
