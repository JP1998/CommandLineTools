/*
 *     Copyright 2017 Jean-Pierre Hotz
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

import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;

import java.util.Map;

/**
 * This class represents a list of parameter values that are given to
 * a command to execute it. The value of a parameter with a certain name
 * can be easily retrieved by calling {@link ParameterValuesList#getValue(String)}.
 */
public class ParameterValuesList {

    /**
     * The map that maps the name of a parameter to the value that was given for it.
     */
    private Map<String, Parameter.Value> values;

    /**
     * This constructor creates a ParameterValuesList with the given mappings
     *
     * @param values the map of the parameter names to the values
     */
    public ParameterValuesList(Map<String, Parameter.Value> values) {
        if(values == null) {
            throw new NullPointerException(StringProcessing.format(
                    "You cannot have a 'null' list of parameter values. If you want to create an empty list, give an empty map."
            ));
        }
        this.values = values;
    }

    /**
     * This method gives you the value for the parameter with specified name.
     *
     * @param paramName The name of the parameter of which you want to fetch the value
     * @return the value of the parameter; {@code null} if the parameter does not exists
     */
    public Object getValue(String paramName) {
        return values.get(paramName).getValue();
    }
}
