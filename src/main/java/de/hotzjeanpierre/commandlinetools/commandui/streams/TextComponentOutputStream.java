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

import org.jetbrains.annotations.NotNull;

import javax.swing.text.JTextComponent;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class may be used to create an OutputStream using a {@link JTextComponent}.<br>
 * It provides enough functionality to be efficiently used as stdout (by calling {@link System#setOut(PrintStream)} with
 * a PrintStream, created with the TextComponentInputStream, as parameter.<br>
 * The TextComponents content and attributes may be altered while used by this Stream, and thus
 * is compatible with the {@link TextComponentInputStream} to be used on the same TextComponent at the same time.<br>
 * When output is written the text is always appended to the end of the text of the component.
 */
public class TextComponentOutputStream extends OutputStream {

    /**
     * The JTextComponent to use for the output
     */
    private JTextComponent console;

    /**
     * This constructor creates an OutputStream using the given JTextComponent
     * @param console the JTextComponent to use for this OutputStream
     */
    public TextComponentOutputStream(JTextComponent console) {
        this.console = console;
    }

    @Override
    public void write(int b) {
        if(b != 13) {
            console.setText(console.getText() + ((char) b));
        }

        console.setCaretPosition(console.getDocument().getLength());
    }

    @Override
    public void write(@NotNull byte[] bytes, int off, int len) {
        if((off < 0) || (off > bytes.length) || (len < 0) || ((off + len) > bytes.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if(len == 0){
            return;
        }

        String text = new String(bytes, off, len);

        console.setText(console.getText() + text);
        console.setCaretPosition(console.getDocument().getLength());
    }
}
