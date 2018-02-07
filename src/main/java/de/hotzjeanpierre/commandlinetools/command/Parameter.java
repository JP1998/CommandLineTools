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

import de.hotzjeanpierre.commandlinetools.command.exceptions.ParameterTypeMismatchException;
import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;

/**
 * This class represents a parameter that can be taken by a command.
 * It consists of a name (which is used to identify a parameter),
 * a description (which will be displayed to the user in the help-command,
 * or in general in the documentation of a command) and a type of the parameter.
 * A parameter will only allow values being produced with a value that
 * is of the type the parameter requires.
 * Also a parameter may have a default value, which will be assigned to
 * a parameter as soon as there was no value given by the user for the parameter.
 * If a parameter should not have a default value you'll have to give the default
 * value of {@code null}. This value thus also cannot be assigned
 * to a parameter.
 */
public class Parameter implements NamingValidator {

    /**
     * The name of the parameter; used for identification in parsing a command
     */
    private String name;
    /**
     * The type of the parameter; determines which values are allowed for the parameter
     */
    private Class type;

    /**
     * The description of the parameter. Should actually contain well documented information
     * since it may be displayed to the user.
     */
    private String description;

    /**
     * The default value which will be assigned when a command is parsed
     * from a string in which there is no value given for the parameter.
     */
    private Object defaultValue;

    /**
     * This constructor creates a parameter with given name, description and type.
     * The parameter will have no default value.
     *
     * @param name        The name the parameter should have
     * @param type        The type the parameters values should have
     * @param description The description of the parameter
     * @throws NullPointerException     in case the name, type or description is {@code null}
     * @throws IllegalArgumentException in case the name or description is empty
     */
    public Parameter(String name, Class type, String description)
            throws NullPointerException, IllegalArgumentException {
        this(name, type, description, null);
    }

    /**
     * This constructor creates a parameter with given name, description, type
     * and default value.
     *
     * @param name         The name the parameter should have
     * @param type         The type the parameters values should have
     * @param description  The description of the parameter
     * @param defaultValue The default value of the parameter
     * @throws NullPointerException     in case the name, type or description is {@code null}
     * @throws IllegalArgumentException in case the name or description is empty
     */
    public Parameter(String name, Class<?> type, String description, Object defaultValue) {
        if (name == null) {
            throw new NullPointerException("Name of a parameter may not be null.");
        } else if (name.trim().length() == 0) {
            throw new IllegalArgumentException("Name of a parameter may not be empty.");
        }
        if (type == null) {
            throw new NullPointerException("Type of a parameter may not be null.");
        }
        if (description == null) {
            throw new NullPointerException("Description of a parameter may not be null.");
        } else if (description.trim().length() == 0) {
            throw new IllegalArgumentException("Description of a parameter may not be empty.");
        }

        assureNameValidity(
                name,
                "The parameter name '{0}' you were trying to assign is not valid.",
                name
        );

        if(defaultValue != null && !type.isInstance(defaultValue)) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "The value '{0}' cannot be used as default value for type '{1}'.",
                    defaultValue,
                    type.getName()
            ));
        }

        this.name = name;
        this.type = type;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    /**
     * This method gives the name of the parameter, which identifies it.
     *
     * @return the name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * This method gives the type of the parameter.
     *
     * @return the type of the parameter
     */
    public Class getType() {
        return type;
    }

    /**
     * This method gives the description of the parameter, which may be shown to the user.
     *
     * @return the description of the parameter
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method default value of the parameter.
     *
     * @return the default value of the parameter
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * This method creates a {@link Parameter.Value}-object with the given value,
     * while also making sure that the type of the parameter is correct.
     *
     * @param val the value the {@link Parameter.Value}-object should have
     * @return the {@link Parameter.Value}-object with given value
     * @throws ParameterTypeMismatchException in case the type of the given value
     *                                        does not match with the parameters type.
     */
    public Value createValue(Object val)
            throws ParameterTypeMismatchException {
        if (type.isInstance(val)) {
            return new Value(val);
        }
        throw new ParameterTypeMismatchException(StringProcessing.format(
                "Couldn't convert type '{0}' to parameter type '{1}'.\n" +
                        "This likely occurred because the command you were\n" +
                        "trying to call has two or more parameters with the same name.",
                val.getClass().getName(),
                type.getName()
        ));
    }

    /**
     * This class represents a value destined for a certain parameter.
     */
    public class Value {

        /**
         * The value the value itself has
         */
        private Object value;

        /**
         * This constructor creates a Value-object with the given value
         *
         * @param obj the value the Value-object should have
         */
        private Value(Object obj) {
            this.value = obj;
        }

        /**
         * This methode gives you the value of the Value-object
         *
         * @return the value of the Value-object
         */
        public Object getValue() {
            return value;
        }

        /**
         * This method gives you the name of the parameter that created this value.
         *
         * @return The name of the source parameter
         */
        public String getParameterName() {
            return getName();
        }
    }
}
