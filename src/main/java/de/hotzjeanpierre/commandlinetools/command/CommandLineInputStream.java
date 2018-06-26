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

import java.io.InputStream;

/**
 * This class is used as an abstract wrapper for any input stream used for any
 * command line interface. It generalizes / provides some operations that
 * are usually provided by a command line interface that goes above the
 * capabilities of a normal InputStream.
 */
public abstract class CommandLineInputStream extends InputStream {

    /**
     * This property determines whether the current input is to be recorded
     * into the history of the InputStream.
     */
    private boolean recordInput;

    /**
     * Contains the maximum amount of records to keep track of in the history.
     */
    private int maxAmountOfRecords;

    /**
     * This method gives you whether this stream should currently
     * record the input into its history.
     *
     * @return whether to record the input or not
     */
    public boolean isRecordInput() {
        return recordInput;
    }

    /**
     * This method allows you to set whether you want this
     * stream to record its input or not.
     *
     * @param recordInput whether to record the input or not
     */
    public void setRecordInput(boolean recordInput) {
        this.recordInput = recordInput;
    }

    /**
     * This method gives you the current maximum amount of
     * records this stream keeps track of in its history.
     */
    public int getMaxAmountOfRecords() {
        return maxAmountOfRecords;
    }

    /**
     * This method allows you to set the maximum amount of
     * records this stream keeps track of in its history.
     * Any values below {@code 0} will be simply ignored.
     *
     * @param maxAmountOfRecords the maximum amount of
     *                          records in the history of this stream
     */
    public void setMaxAmountOfRecords(int maxAmountOfRecords) {
        if(maxAmountOfRecords >= 0) {
            this.maxAmountOfRecords = maxAmountOfRecords;
        }
    }

    /**
     * This method should clear the currently used commandline interface
     * of any text that is currently beind shown to the user.
     * This includes any text out of screen that might be available
     * through scrolling or other methods of changing text position.
     */
    public abstract void clear();

}
