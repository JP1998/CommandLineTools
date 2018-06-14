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
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class Array implements Iterable {

    private Object[] elements;
    private Type elementType;
    private int dimensions;

    public Array(Type t, int dimensions, Object... elements) {
        this.elementType = t;
        this.dimensions = dimensions;

            this.elements = new Object[elements.length];

        if(dimensions < 1) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "You cannot create an array with {0} dimensions.",
                    dimensions
            ));
        } else if(dimensions == 1) {
            for(int i = 0; i < elements.length; i++) {
                if(elementType.isValidValue(elements[i])) {
                    this.elements[i] = elements[i];
                } else if(elements[i] != null) {
                    throw new IllegalArgumentException(StringProcessing.format(
                            "Cannot add value of type {0} into an array of type {2}.",
                            elements[i].getClass().getSimpleName(),
                            elementType.getSimpleName()
                    ));
                } else {
                    throw new IllegalArgumentException(StringProcessing.format(
                            "Cannot add null-value into an array of type {2}.",
                            elementType.getSimpleName()
                    ));
                }
            }
        } else {
            for(int i = 0; i < elements.length; i++) {
                if(elements[i] instanceof Array) {
                    Array toInsert = (Array) elements[i];
                    if (!elementType.isSubType(toInsert.elementType)) {
                        throw new IllegalArgumentException(StringProcessing.format(
                                "Cannot insert array of type {0} into an array of type {b}.",
                                toInsert.elementType.getSimpleName(),
                                elementType.getSimpleName()
                        ));
                    } else if (this.dimensions != toInsert.dimensions + 1) {
                        throw new IllegalArgumentException(StringProcessing.format(
                                "Cannot insert array with {0} dimensions into an array with {1} dimensions.",
                                toInsert.dimensions,
                                this.dimensions
                        ));
                    } else {
                        this.elements[i] = elements[i];
                    }
                } else if(elements[i] == null) {
                    this.elements[i] = null;
                } else {
                    throw new IllegalArgumentException(StringProcessing.format(
                            "Cannot insert element into array an {0}-dimensional array.",
                            this.dimensions
                    ));
                }
            }
        }
    }

    public int length() {
        return elements.length;
    }

    public void setElement(Object element, int... indices) {
        if(indices.length != dimensions) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "You may not give {0} indices to an {1}-dimensional array to retrieve an element.",
                    indices.length,
                    dimensions
            ));
        } else if(!elementType.isValidValue(element)) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "The value '{0}' is not of type {1} and can thus not be assigned to the index {2} of an array of said type.",
                    element,
                    elementType.getSimpleName(),
                    indices[indices.length - 1]
            ));
        }

        Array arr = getArray(stripLast(indices));

        int lastIndex = indices[indices.length - 1];

        if(lastIndex < 0 || lastIndex >= arr.length()) {
            throw new IndexOutOfBoundsException(StringProcessing.format(
                    "You cannot set element at index {0} from array with length {1}.",
                    lastIndex,
                    arr.elements.length
            ));
        }

        arr.elements[lastIndex] = element;
    }

    public Object getElement(int... indices) {
        if(indices.length != dimensions) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "You may not give {0} indices to an {1}-dimensional array to retrieve an element.",
                    indices.length,
                    dimensions
            ));
        }

        Array arr = getArray(stripLast(indices));

        int lastIndex = indices[indices.length - 1];

        if(lastIndex < 0 || lastIndex >= arr.length()) {
            throw new IndexOutOfBoundsException(StringProcessing.format(
                    "You cannot get element at index {0} from array with length {1}.",
                    lastIndex,
                    arr.elements.length
            ));
        }

        return arr.elements[lastIndex];
    }

    public void setArray(Array toAdd, int... indices) {
        if(indices.length >= dimensions - 1) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "You may not give {0} indices to an {1}-dimensional array to set an array.",
                    indices.length,
                    dimensions
            ));
        } else if(toAdd.dimensions != dimensions - indices.length) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "You may not insert an array with {0} dimensions into the {1}th dimension of an {2}-dimensional array.",
                    toAdd.dimensions,
                    indices.length,
                    dimensions
            ));
        } else if(!toAdd.elementType.isSubType(elementType)) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "You may not insert an array with elements of "
            ));
        }

        Array arr = getArray(stripLast(indices));

        int lastIndex = indices[indices.length - 1];

        if(lastIndex < 0 || lastIndex >= arr.length()) {
            throw new IndexOutOfBoundsException(StringProcessing.format(
                    "You cannot set element at index {0} from array with length {1}.",
                    lastIndex,
                    arr.elements.length
            ));
        }

        arr.elements[lastIndex] = toAdd;
    }

    public Array getArray(int... indices) {
        if(indices.length >= dimensions) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "You may not give {0} indices to an {1}-dimensional array to retrieve a contained array.",
                    indices.length,
                    dimensions
            ));
        }

        Array currentArray = this;
        for(int i : indices) {
            if(i < 0 || i >= currentArray.elements.length) {
                throw new IndexOutOfBoundsException(StringProcessing.format(
                        "You cannot retrieve element at index {0} from array with length {1}.",
                        i,
                        currentArray.elements.length
                ));
            }
            currentArray = (Array) currentArray.elements[i];
        }

        return currentArray;
    }

    public boolean conformsToType(Type t) {
        if(t instanceof ArrayType) {
            ArrayType arrT = (ArrayType) t;
            return arrT.getDimensions() == dimensions &&
                    elementType.equals(arrT.getContainedType());
        }
        return false;
    }

    private static int[] stripLast(@NotNull int[] arr) {
        int[] result = new int[(arr.length > 0)? arr.length - 1 : 0];
        System.arraycopy(arr, 0, result, 0, result.length);
        return result;
    }

    @Override
    public String toString() {
        return StringProcessing.format(
                "{0}{1} {2}",
                elementType.getSimpleName(),
                StringProcessing.multiply("[]", this.dimensions),
                createValueString()
        );
    }

    private String createValueString() {
        StringBuilder result = new StringBuilder("{ ");

        for(int i = 0; i < this.elements.length; i++) {
            if(i > 0) {
                result.append(", ");
            }
            if(this.elements[i] != null) {
                if (this.dimensions > 1) {
                    result.append(((Array) this.elements[i]).createValueString());
                } else {
                    result.append(this.elements[i].toString());
                }
            } else {
                result.append("null");
            }
        }

        return result.append(" }").toString();
    }

    private class ArrayIterator implements Iterator {

        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < elements.length;
        }

        @Override
        public Object next() {
            return elements[current++];
        }
    }

    @NotNull
    @Override
    public Iterator iterator() {
        return new ArrayIterator();
    }
}
