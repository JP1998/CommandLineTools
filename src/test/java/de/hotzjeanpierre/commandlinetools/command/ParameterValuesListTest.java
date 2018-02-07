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

package de.hotzjeanpierre.commandlinetools.command;

import de.hotzjeanpierre.commandlinetools.command.testutilities.SomeClass;
import de.hotzjeanpierre.commandlinetools.command.testutilities.SomeSubClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ParameterValuesListTest {

    @Test(expected = NullPointerException.class)
    public void testNullValuesList() {
        new ParameterValuesList(null);
    }

    @Test
    public void testValuesListMap() {
        String valuename = "somevalue";
        Parameter parameter = new Parameter(
                "someparameter",
                SomeClass.class,
                "this is some parameter"
        );

        SomeClass value = new SomeSubClass(1, 2.3, true);

        Map<String, Parameter.Value> list = new HashMap<>();

        list.put("somevalue1", parameter.createValue(new SomeClass(2, 3.4)));
        list.put("somevalue2", parameter.createValue(new SomeClass(3, 4.5)));
        list.put("somevalue3", parameter.createValue(new SomeClass(4, 5.6)));
        list.put("somevalue4", parameter.createValue(new SomeClass(5, 6.7)));
        list.put(valuename, parameter.createValue(value));

        ParameterValuesList paramList = new ParameterValuesList(list);

        assertThat(
                paramList.getValue(valuename),
                is(value)
        );
    }

}
