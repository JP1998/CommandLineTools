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

public class EnumType implements Type {

    private Class enumType;

    public EnumType(Class type) {
        this.enumType = type;
    }

    public Class getEnumType() {
        return enumType;
    }

    @Override
    public boolean isValidValue(Object o) {
        return enumType.isInstance(o);
    }

    @Override
    public String getName() {
        return enumType.getName();
    }

    @Override
    public String getSimpleName() {
        return enumType.getSimpleName();
    }

    @Override
    public boolean isSubType(Type t) {
        return false;
    }

    @Override
    public boolean isEnum() {
        return true;
    }

    public boolean equals(EnumType obj) {
        return obj.enumType.equals(enumType);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof EnumType) {
            return equals((EnumType) obj);
        }
        return false;
    }
}
