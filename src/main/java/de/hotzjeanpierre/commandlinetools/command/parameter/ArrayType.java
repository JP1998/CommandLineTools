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

import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;

/**
 * This type represents an array of arbitrary dimension with
 * any type of elements (except arrays) contained.
 */
public final class ArrayType implements Type {

    private Type containedType;
    private int dimensions;

    public ArrayType(Type t, int dimensions) {
        this.containedType = t;
        this.dimensions = dimensions;
    }

    @Override
    public boolean isValidValue(Object o) {
        return o == null || (o instanceof Array &&
                ((Array) o).conformsToType(this));
    }

    public Type getContainedType() {
        return containedType;
    }

    public int getDimensions() {
        return dimensions;
    }

    @Override
    public String getName() {
        return StringProcessing.format(
                "{0}{1}",
                containedType.getName(),
                StringProcessing.multiply("[]", dimensions)
        );
    }

    @Override
    public String getSimpleName() {
        return StringProcessing.format(
                "{0}{1}",
                containedType.getSimpleName(),
                StringProcessing.multiply("[]", dimensions)
        );
    }

    @Override
    public boolean isSubType(Type t) {
        return false;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    public boolean equals(ArrayType at) {
        return this.containedType.equals(at.containedType) &&
                this.dimensions == at.dimensions;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ArrayType &&
                this.equals((ArrayType) obj);
    }
}
