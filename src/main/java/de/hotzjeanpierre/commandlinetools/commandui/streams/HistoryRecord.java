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

package de.hotzjeanpierre.commandlinetools.commandui.streams;

/**
 * This class represents a history record to be used by the {@link TextComponentInputStream}.
 * It contains the actually captured data, the index at which the actually captured data starts
 * and the amount of bytes that have been captured.
 */
public class HistoryRecord {

    /**
     * The data that has been captured.
     */
    private byte[] data;
    /**
     * The index at which the actual data starts.
     */
    private int offset;
    /**
     * The amount of characters that have been captured.
     */
    private int amountCapturedCharacters;

    /**
     * This constructor creates an empty HistoryRecord with the given array length
     * while the index at which the data actually starts is given as offset.
     *
     * @param length the length of the byte array used as the data storage
     * @param offset the index at which the data actually starts
     */
    /* package-protected */ HistoryRecord(int length, int offset) {
        this.data = new byte[length];
        this.offset = offset;
        this.amountCapturedCharacters = 0;
    }

    /**
     * This constructor is only used for the {@link #copy()}-method.
     * It copies all the given values into its own attributes.
     *
     * @param data the array that contains the relevant data;
     *             only the relevant data is copied
     * @param off the offset at which the relevant data starts
     * @param capturedChars the amount of relevant bytes
     */
    private HistoryRecord(byte[] data, int off, int capturedChars) {
        this.data = new byte[data.length];
        this.offset = off;

        int backtrack = 0;
        if(data[off + capturedChars - 1] == '\n') {
            backtrack++;
        }
        if(data[off + capturedChars - 2] == '\r') {
            backtrack++;
        }

        System.arraycopy(data, off, this.data, off, capturedChars - backtrack);
        this.amountCapturedCharacters = capturedChars - backtrack;
    }

    /**
     * This method will delete the latest character.
     * It does not handle the input being empty! This will
     * have to be checked through comparing {@link #getAmountCapturedCharacters()}
     * with {@code 0}.
     */
    public void delete() {
        data[offset + --amountCapturedCharacters] = 0;
    }

    /**
     * This method adds the given date to the record this method has been called on.
     * The length of this records backing array is thereby assumed to never be reached.
     *
     * @param date the date to add to this record
     */
    public void add(byte date) {
        data[offset + amountCapturedCharacters++] = date;
    }

    /**
     * This method gives you te amount of characters that have yet
     * been captured by this record.
     *
     * @return the amount of captured characters
     */
    public int getAmountCapturedCharacters() {
        return amountCapturedCharacters;
    }

    /**
     * This method gives you the backing array of this record.
     * To extract any data you'll probably have to keep in mind that
     * the actually relevant data starts at the index given by
     * {@link #getOffset()} and that there are only {@link #getAmountCapturedCharacters()}
     * bytes that are relevant to you.
     *
     * @return the backing array to the record containing its data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * This method gives you the index of the start of the relevant data
     * inside the backing array of the record.
     *
     * @return the offset for the relevant data in the backing array
     */
    public int getOffset() {
        return offset;
    }

    /**
     * This method copies the record this method has been called on.
     * This means that the returned HistoryRecord will still contain
     * all of the relevant data of the original record, although any
     * changes to the returned record will not affect the original record.
     * Also the records may differ in some details, like values in the
     * backing array outside of the range for the relevant data.
     * Also (to prevent any issues with Scanner etc.) any line separators
     * at the end of this data are being deleted.
     *
     * @return a copy of the record this was called on
     */
    public HistoryRecord copy() {
        return new HistoryRecord(this.data, this.offset, this.amountCapturedCharacters);
    }
}
