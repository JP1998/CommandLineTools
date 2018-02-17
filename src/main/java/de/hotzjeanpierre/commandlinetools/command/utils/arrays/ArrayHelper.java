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
import org.jetbrains.annotations.NotNull;

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
    public static <T> boolean containsAny(T[] arr, T... elements) {
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

    /**
     * This method checks whether the given arrays contents are equal. The general
     * contract on equality in content of {@code null}-arrays is not well defined.
     * Thus this method enforces the contract which defines that a {@code null}-array
     * can only have the same content as a {@code null}-array.
     * Otherwise any two arrays of different lengths can also not contain the same elements.
     * Any element in the first array will have to have the same amount of occurrences
     * in the second array, to be equal in content.
     * For testing of equality of two elements the {@link Object#equals(Object)}-method
     * is used and will thus have to be overwritten in the types class.
     *
     * @param arr1 the first array to check for equality in content.
     * @param arr2 the second array to check for equality in content.
     * @param <T> An arbitrary type that the array is declared with.
     * @return Whether or not the content of the two given arrays are equal.
     */
    @Contract("null, null -> true; null, !null -> false; !null, null -> false")
    public static <T> boolean arrayContentEquals(T[] arr1, T[] arr2) {
        if(arr1 == null && arr2 == null) {
            return true;
        } else if(arr1 == null || arr2 == null) {
            return false;
        } else if(arr1.length != arr2.length) {
            return false;
        }

        boolean[] usedElements = new boolean[arr2.length];

        for(int i = 0; i < arr1.length; i++) {
            boolean found = false;

            for(int j = 0; j < arr2.length && !found; j++) {

                if(!usedElements[j] && elementsEqual(arr1[i], arr2[j])) {
                    found = true;
                    usedElements[j] = true;
                }

            }

            if(!found) {
                return false;
            }
        }

        return true;
    }

    /**
     * This method checks whether two objects are equal.
     * In this term equality means either the elements are both {@code null},
     * or they are equal according to their {@link Object#equals(Object)}-method,
     * meaning they are both not {@code null} and equal.
     *
     * @param el1 the first element to test fot equality.
     * @param el2 the second element to test fot equality.
     * @param <T> An arbitrary type that the elements are declared with.
     * @return Whether the elements are equal.
     */
    @Contract("null, null -> true; null, !null -> false; !null, null -> false")
    private static <T> boolean elementsEqual(T el1, T el2) {
        return (el1 == null && el2 == null) || (el1 != null && el1.equals(el2));
    }

    /**
     * This method casts a
     * @param characters
     * @return
     */
    @Contract(pure = true)
    public static Character[] cast(@NotNull char[] characters) {
        Character[] result = new Character[characters.length];

        for(int i = 0; i < result.length; i++) {
            result[i] = characters[i];
        }

        return result;
    }
}
