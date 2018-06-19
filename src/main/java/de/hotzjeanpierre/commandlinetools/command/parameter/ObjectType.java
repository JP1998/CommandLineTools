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
 * This class represents a type which allows values of a given class.
 */
public final class ObjectType implements Type {

    private Class type;

    public ObjectType(Class type) {
        this.type = type;
    }

    @Override
    public boolean isValidValue(Object o) {
        return o == null || type.isInstance(o);
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public String getSimpleName() {
        return type.getSimpleName();
    }

    @Override
    public boolean isSubType(Type t) {
        return t instanceof ObjectType &&
                ((ObjectType) t).type.isAssignableFrom(type);
    }

    public boolean equals(ObjectType obj) {
        return obj.type.equals(type);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ObjectType) {
            return equals((ObjectType) obj);
        }
        return false;
    }
}
