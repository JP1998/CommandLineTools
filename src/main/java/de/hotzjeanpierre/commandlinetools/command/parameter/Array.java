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
import de.hotzjeanpierre.commandlinetools.command.utils.arrays.ArrayHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * This class represents an array with an given number of dimensions and
 * an given type of elements.
 * An n-dimensional array (n > 1) will only be able to contain
 * arrays with the same type of elements and with the dimension of n - 1.
 * A 1-dimensional array will only be able to contain values of the given type.
 */
public class Array implements Iterable {

    /**
     * The container of all the elements / arrays contained within this array.
     */
    private Object[] elements;
    /**
     * The type of the elements that may be contained within this array
     */
    private Type elementType;
    /**
     * The number of dimensions of this array.
     */
    private int dimensions;

    /**
     * This constructor creates an array with the given type of elements,
     * the given dimension and the given objects as contained elements.
     *
     * @param t             the type of elemnts contained
     * @param dimensions    the dimension of this array
     * @param elements      the elements contained in this array
     */
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
            // check for the elements to be of valid type and eventually copy them into the array
            for(int i = 0; i < elements.length; i++) {
                if(elementType.isValidValue(elements[i])) {
                    // if the given element at index i is a valid value we'll copy it
                    this.elements[i] = elements[i];
                } else if(elements[i] != null) {
                    // the given element is not of valid type
                    throw new IllegalArgumentException(StringProcessing.format(
                            "Cannot add value of type {0} into an array of type {2}.",
                            elements[i].getClass().getSimpleName(),
                            elementType.getSimpleName()
                    ));
                } else {
                    // the element type does not allow null values
                    throw new IllegalArgumentException(StringProcessing.format(
                            "Cannot add null-value into an array of type {2}.",
                            elementType.getSimpleName()
                    ));
                }
            }
        } else {
            // check for validity of the contained arrays
            for(int i = 0; i < elements.length; i++) {
                if(elements[i] instanceof Array) {
                    // if it actually is an array we cast it to one and keep on checking it
                    Array toInsert = (Array) elements[i];
                    if (!elementType.isSubType(toInsert.elementType)) {
                        // if the array is not containing valid values for this array we'll throw an exception
                        throw new IllegalArgumentException(StringProcessing.format(
                                "Cannot insert array of type {0} into an array of type {b}.",
                                toInsert.elementType.getSimpleName(),
                                elementType.getSimpleName()
                        ));
                    } else if (this.dimensions != toInsert.dimensions + 1) {
                        // if the dimensions do not match up we'll also throw an exception
                        throw new IllegalArgumentException(StringProcessing.format(
                                "Cannot insert array with {0} dimensions into an array with {1} dimensions.",
                                toInsert.dimensions,
                                this.dimensions
                        ));
                    } else {
                        // if the previous checks didn't catch an faulty value we'll just assign it
                        this.elements[i] = elements[i];
                    }
                } else if(elements[i] == null) {
                    // an array consisting of arrays will allow null-values
                    this.elements[i] = null;
                } else {
                    // if it is not an array and also not null we know that it is an element
                    // (of which type doesn't matter) which may not be inserted
                    throw new IllegalArgumentException(StringProcessing.format(
                            "Cannot insert element into array an {0}-dimensional array.",
                            this.dimensions
                    ));
                }
            }
        }
    }

    /**
     * This method gives you the amount of elements saved in this array.
     *
     * @return the length of this array
     */
    public int length() {
        return elements.length;
    }

    /**
     * This method will set the <b>element</b> at the given index. This means
     * you'll have to provide as many indices as there are dimensions in this
     * array, whereas each of the indices will belong to the dimension in their order.
     * Thus the call {@code someArray.setElement(someElement, a, b, c);} would be
     * the equivalent to the native java code {@code someArray[a][b][c] = someElement;}.
     *
     * @param element the element that is to be assigned to the given position in the array
     * @param indices the indices of the position to assign the given element to
     * @throws IllegalArgumentException in case the number of indices is incorrect
     *                                  or the value is of a wrong type
     * @throws IndexOutOfBoundsException in case any of the given indices is out
     *                                   of bounds of the array
     */
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

        Array arr = getArray(ArrayHelper.stripLast(indices));

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

    /**
     * This method will get the <b>element</b> at the given index. This means
     * you'll have to provide as many indices as there are dimensions in this
     * array, whereas each of the indices will belong to the dimension in their order.
     * Thus the call {@code Object obj = someArray.getElement(a, b, c);} would be
     * the equivalent to the native java code {@code Object obj = someArray[a][b][c];}.
     *
     * @param indices the indices of the position to read the element from
     * @return the element at the given indices
     * @throws IllegalArgumentException in case the number of indices is incorrect
     * @throws IndexOutOfBoundsException in case any of the given indices is out
     *                                   of bounds of the array
     */
    public Object getElement(int... indices) {
        if(indices.length != dimensions) {
            throw new IllegalArgumentException(StringProcessing.format(
                    "You may not give {0} indices to an {1}-dimensional array to retrieve an element.",
                    indices.length,
                    dimensions
            ));
        }

        Array arr = getArray(ArrayHelper.stripLast(indices));

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

    /**
     * This method will set the <b>array</b> at the given index. This means
     * you'll have to provide minimally one and maximally n - 1 indices,
     * where n is the amount of dimensions in this array, whereas each of
     * the indices will belong to the dimension in their order.
     * Thus the call {@code someArray.setArray(someOtherArray, a, b, c);} would be
     * the equivalent to the native java code {@code someArray[a][b][c] = someOtherArray;}.
     *
     * @param toAdd the array that is to be added att the given position
     * @param indices the indices of the position to assign the array to
     * @throws IllegalArgumentException in case the number of indices is incorrect,
     *                                  the dimensions of the arrays don' match up or
     *                                  the types of the arrays don't match
     * @throws IndexOutOfBoundsException in case any of the given indices is out
     *                                   of bounds of the array
     */
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
                    "You may not insert an array with elements of type {0} into an array with elments of type {2}.",
                    toAdd.elementType.getSimpleName(),
                    this.elementType.getSimpleName()
            ));
        }

        Array arr = getArray(ArrayHelper.stripLast(indices));

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

    /**
     * This method will get the <b>array</b> at the given position. This means
     * you'll have to provide minimally one and maximally n - 1 indices,
     * where n is the amount of dimensions in this array. Each of
     * the indices will belong to the dimension in their order.
     * Thus the call {@code Array arr = (Array) someArray.getArray(a, b, c);} would be
     * the equivalent to the native java code {@code Object[] arr = someArray[a][b][c];}.
     *
     * @param indices the indices of the position to read the array from
     * @throws IllegalArgumentException in case the number of indices is incorrect
     * @throws IndexOutOfBoundsException in case any of the given indices is out
     *                                   of bounds of the array
     */
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

    /**
     * This method determines whether the array this method is called upon
     * conforms to the given type. Therefore the type of the contained
     * elements is checked, and the number of dimensions.
     *
     * @param t the type to check for compatibility with this array
     * @return whether this array is valid for the given type
     */
    public boolean conformsToType(Type t) {
        if(t instanceof ArrayType) {
            ArrayType arrT = (ArrayType) t;
            return arrT.getDimensions() == dimensions &&
                    arrT.getContainedType().isSubType(elementType);
        }
        return false;
    }

    /**
     * This method will return this array represented as an string. It will use
     * the {@link #toString()}-representation for its elements if they are not
     * arrays themselves.
     *
     * @return the array represented as a string
     */
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

    @Override
    public String toString() {
        return StringProcessing.format(
                "{0}{1} {2}",
                elementType.getSimpleName(),
                StringProcessing.multiply("[]", this.dimensions),
                createValueString()
        );
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
