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
 * This class represents a primitive type which allows values of the primitive types wrapper class
 * or of the primitive type itself.
 */
public final class PrimitiveType implements Type {

    public static final PrimitiveType BOOLEAN   = new PrimitiveType(Boolean.class, boolean.class);
    public static final PrimitiveType DOUBLE    = new PrimitiveType(Double.class, double.class);
    public static final PrimitiveType FLOAT     = new PrimitiveType(Float.class, float.class);
    public static final PrimitiveType BYTE      = new PrimitiveType(Byte.class, byte.class);
    public static final PrimitiveType SHORT     = new PrimitiveType(Short.class, short.class);
    public static final PrimitiveType CHAR      = new PrimitiveType(Character.class, char.class);
    public static final PrimitiveType INT       = new PrimitiveType(Integer.class, int.class);
    public static final PrimitiveType LONG      = new PrimitiveType(Long.class, long.class);

    private Class wrapperClass;
    private Class primitiveClass;

    private PrimitiveType(Class wrapperClass, Class primitiveType) {
        this.wrapperClass = wrapperClass;
        this.primitiveClass = primitiveType;
    }

    @Override
    public boolean isValidValue(Object o) {
        return wrapperClass.isInstance(o) || primitiveClass.isInstance(o);
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public String getName() {
        return primitiveClass.getName();
    }

    @Override
    public String getSimpleName() {
        return primitiveClass.getSimpleName();
    }

    @Override
    public boolean isSubType(Type t) {
        return false;
    }

    public boolean equals(PrimitiveType obj) {
        return obj.wrapperClass.equals(wrapperClass) &&
                obj.primitiveClass.equals(primitiveClass);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PrimitiveType &&
                equals((PrimitiveType) obj);
    }
}
