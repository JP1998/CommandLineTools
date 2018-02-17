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

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

public class ArraysHelperTest {

    @Test
    public void testContainsWithNullArray() {
        assertThat(
                ArrayHelper.containsAny(null, 7, 6, 4, 3),
                is(false)
        );
    }

    @Test
    public void testContainsWithoutElement() {
        assertThat(
                ArrayHelper.containsAny(new Integer[] { 1, 3, 5, 7, 9 }),
                is(false)
        );
    }

    @Test
    public void testContainsWithSingleElement() {
        assertThat(
                ArrayHelper.containsAny(new Integer[] { 1, 3, 5, 7, 9 }, 7),
                is(true)
        );
    }

    @Test
    public void testContainsWithSeveralElements() {
        assertThat(
                ArrayHelper.containsAny(new Integer[] { 1, 3, 5, 7, 9 }, 2, 4, 6, 7),
                is(true)
        );
    }

    @Test
    public void testContainsWithNullElement() {
        assertThat(
                ArrayHelper.containsAny(new Integer[] { 1, 3, 5, null, 9 }, 2, 4, 6, 8, null),
                is(true)
        );
    }

    @Test
    public void testArrayContentEqualsDualNullArrays() {
        assertThat(
                ArrayHelper.arrayContentEquals(null, null),
                is(true)
        );
    }

    @Test
    public void testArrayContentEqualsSingleNullArrays() {
        assertThat(
                ArrayHelper.arrayContentEquals(new Object[0], null),
                is(false)
        );
    }

    @Test
    public void testArrayContentEqualsArraysWithDifferentLengths() {
        assertThat(
                ArrayHelper.arrayContentEquals(new Object[12], new Object[11]),
                is(false)
        );
    }

    @Test
    public void testArrayContentEqualsArraysWithDifferentContent() {
        assertThat(
                ArrayHelper.arrayContentEquals(
                        new Integer[] { 1, 2, 3, 4, 5 },
                        new Integer[] { 6, 7, 8, 9, 0 }
                ),
                is(false)
        );
    }

    @Test
    public void testArrayContentEqualsArraysWithSameContent() {
        assertThat(
                ArrayHelper.arrayContentEquals(
                        new Integer[] { 1, 2, 3, 4, 5 },
                        new Integer[] { 4, 2, 5, 3, 1 }
                ),
                is(true)
        );
    }

    @Test
    public void testCast() {
        assertArrayEquals(
                new Character[] { 'a', 's', 'd', 'f' },
                ArrayHelper.cast(new char[] { 'a', 's', 'd', 'f' })
        );
    }
}
