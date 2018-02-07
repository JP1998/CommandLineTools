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

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AssuranceTest {

    private static boolean classLoaded;

    @Test
    public void testTryLoadingClassValid() {
        classLoaded = false;

        Assurance.tryLoadingClass(
                "de.hotzjeanpierre.commandlinetools.command.utils.AssuranceTest$ClassToLoad"
        );

        assertThat(
                classLoaded,
                is(true)
        );
    }

    @Test
    public void testTryLoadingClassInvalid() {
        classLoaded = false;

        Assurance.tryLoadingClass(
                "ClassToLoad"
        );

        assertThat(
                classLoaded,
                is(false)
        );
    }

    private static class ClassToLoad {

        static {
            AssuranceTest.classLoaded = true;
        }

    }

}
