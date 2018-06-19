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

package de.hotzjeanpierre.commandlinetools.command.parameter;

/**
 * This interface gives a contract that every kind of type of parameter
 * should extend. It will define all the methods that are needed for
 * usage as a type for a parameter.
 * Since there are definitions already given in {@link de.hotzjeanpierre.commandlinetools.command.parameter}
 * you shouldn't implement this interface and rather use those Type-classes.
 *
 * @see CommonTypes
 * @see PrimitiveType
 * @see ObjectType
 * @see ArrayType
 * @see EnumType
 */
public interface Type {

    /**
     * This method determines whether the given Object is valid for the
     * given type, which means that it should be type-castable to the
     * represented type.
     *
     * @param o the object to check for validity of its type
     * @return whether the given value is valid for the represented type
     */
    boolean isValidValue(Object o);

    /**
     * This method should give the full (class-) name this type represents.
     * So if you have for example a type-object that represents Strings this
     * method should return {@code java.lang.String}.
     *
     * @return the full name of the type represented by this object
     */
    String getName();

    /**
     * This method should give the simple (class-) name this type represents.
     * So if you have for example a type-object that represents Strings this
     * method should return {@code String}.
     *
     * @return the simple name of the type represented by this object
     */
    String getSimpleName();

    /**
     * This method is supposed to determine whether the given type-object
     * represents either the same type as {@code this}, or a sub-type of {@code this}.
     * Thus you can derive that if (and only if) the call {@code t1.isSubType(t2)}
     * returns {@code true} you'll know that you can assign any value of type
     * {@code t2} to a variable of type {@code t1}.
     *
     * @param t the type to check for whether it is a sub-type of {@code this}
     * @return whether the given type is a sub-type of {@code this}
     */
    boolean isSubType(Type t);

    /**
     * This method should determine whether the represented type is an array.
     *
     * @return whether {@code this} represents an array
     */
    boolean isArray();

    /**
     * This method should determine whether the represented type is an enum.
     *
     * @return whether {@code this} represents an enumerated type
     */
    boolean isEnum();

}
