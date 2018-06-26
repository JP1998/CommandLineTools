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

import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;

/**
 * This class may be used to create an InputStream using a {@link JTextComponent}.<br>
 * It provides enough functionality to be efficiently used as stdin (by calling {@link System#setIn(InputStream)} with
 * the TextComponentInputStream as parameter, or with an {@link java.util.Scanner}-object.<br>
 * The TextComponents content and attributes may be altered while used by this Stream, and thus
 * is compatible with the {@link TextComponentOutputStream} to be used on the same TextComponent.<br>
 * When typed by the user the text is always assumed to be at the end of the text of the component,
 * whereas there might be some problems occurring when using this stream with concurrency.
 */
public class TextComponentInputStream extends InputStream {

    /**
     * This Queue contains all the characters typed by the user into the text component
     */
    private LinkedBlockingQueue<Character> blockingQueue;

    /**
     * Contains the JTextComponent that is used as the output for this stream
     */
    private JTextComponent console;

    /**
     * Whether this stream has been closed or not
     */
    private volatile boolean closed;

    /**
     * Contains the index (in {@link TextComponentInputStream#records_workingcopy}) of the
     * record that is currently selected.
     */
    private int currentlySelectedRecord;

    /**
     * Contains the maximum amount of records to keep track of in the history.
     */
    private int maxAmountOfRecords;

    /**
     * This list contains the records of the history of all the submitted inputs.
     * The newest entry will be at index 0.
     */
    private List<HistoryRecord> records;

    /**
     * This list will contain a working copy of the history of all submitted inputs,
     * where the latest (at index 0; will be selected by default) will initially be empty.
     */
    private List<HistoryRecord> records_workingcopy;

    /**
     * The length of the text displayed in the console before a prompt occurs.
     * This is used to determine the text to keep in display when switching between
     * records.
     */
    private int textLengthBeforePrompt;

    /**
     * This constructor creates an InputStream using the given JTextComponent
     * @param console the JTextComponent to use for this InputStream
     */
    public TextComponentInputStream(JTextComponent console) {
        this.closed = false;
        this.console = console;
        this.console.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.isControlDown() || e.isAltDown() || e.isAltGraphDown()) {
                    return;
                }

                if(console.getText().length() == console.getCaretPosition()) {
                    blockingQueue.offer(e.getKeyChar());
                    console.setCaretPosition(console.getDocument().getLength());
                } else {
                    e.consume();
                    console.setCaretPosition(console.getText().length());
                }
            }

            @Override
            public void keyPressed(KeyEvent e){
                if(e.isControlDown() || e.isAltDown() || e.isAltGraphDown()) {
                    if(e.getKeyCode() == VK_V || e.getKeyCode() == VK_X) {
                        e.consume();
                    }
                    return;
                }

                if(console.getCaretPosition() != console.getText().length()) {
                    e.consume();
                }

                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    previousRecord();
                    e.consume();
                } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    nextRecord();
                    e.consume();
                }

                if(e.getKeyCode() == KeyEvent.VK_DELETE){
                    e.consume();
                } else if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE && records_workingcopy.get(currentlySelectedRecord).getAmountCapturedCharacters() == 0) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e){}
        });

        blockingQueue = new LinkedBlockingQueue<>();

        this.maxAmountOfRecords = 20;
        this.records = new ArrayList<>();
        this.records_workingcopy = new ArrayList<>();
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

    @Override
    public int read() {
        int c = -1;

        try {
            Character c_temp = blockingQueue.poll(100, TimeUnit.MILLISECONDS);

            if(c_temp != null) {
                c = c_temp;
            }
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }

        return c;
    }

    /**
     * This method can be used to select and display the next record,
     * while the next record will always be more recent than the currently
     * selected, except the latest record is already selected.
     */
    private void nextRecord() {
        if(currentlySelectedRecord > 0) {
            currentlySelectedRecord--;
            displayCurrentRecord();
        }
    }

    /**
     * This method can be used to select and display the previous record,
     * while the previous record will always be less recent than the currently
     * selected, except the last available record is already selected.
     */
    private void previousRecord() {
        if(currentlySelectedRecord + 1 < records_workingcopy.size()) {
            currentlySelectedRecord++;
            displayCurrentRecord();
        }
    }

    /**
     * This method copies the record history into the working
     * copy of the records history. The copied records will be safe
     * to be modified as neither they nor their components are
     * copied by reference.
     */
    private void copyRecords() {
        records_workingcopy.clear();

        for (HistoryRecord record : records) {
            records_workingcopy.add(record.copy());
        }
    }

    /**
     * This method displays the currently selected record in the console JTextComponent.
     */
    private void displayCurrentRecord() {
        HistoryRecord recordToAppend = records_workingcopy.get(currentlySelectedRecord);
        String textToSet = StringProcessing.format(
                "{0}{1}",
                this.console.getText().substring(0, textLengthBeforePrompt),
                new String(recordToAppend.getData(), recordToAppend.getOffset(), recordToAppend.getAmountCapturedCharacters())
        );

        this.console.setText(textToSet);
        this.console.setCaretPosition(textToSet.length());
    }

    @Override
    public int read(@NotNull byte[] bytes, int off, int len) {
        if(off < 0 || len < 0){
            throw new IndexOutOfBoundsException();
        }
        if(len == 0){
            return 0;
        }

        copyRecords();
        textLengthBeforePrompt = this.console.getText().length();
        records_workingcopy.add(0, new HistoryRecord(bytes.length, off));
        currentlySelectedRecord = 0;

        byte currentItem;
        HistoryRecord recordToUse;

        do{
            currentItem = (byte) read();

            recordToUse = records_workingcopy.get(currentlySelectedRecord);

            if(currentItem == 8) {
                if(recordToUse.getAmountCapturedCharacters() > 0) {
                    recordToUse.delete();
                }
            } else if(currentItem != -1) {
                recordToUse.add(currentItem);
            }

        } while(!closed && recordToUse.getAmountCapturedCharacters() < len && currentItem != '\n');

        HistoryRecord submittingRecord = records_workingcopy.get(currentlySelectedRecord);
        records.add(0, submittingRecord);

        if(records.size() > maxAmountOfRecords) {
            records.remove(records.size() - 1);
        }

        System.arraycopy(
                submittingRecord.getData(),
                submittingRecord.getOffset(),
                bytes,
                off,
                len
        );

        return submittingRecord.getAmountCapturedCharacters();
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        super.close();
    }
}
