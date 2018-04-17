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

import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class may be used to create an InputStream using a {@link JTextComponent}.<br>
 * It provides enough functionality to be efficiently used as stdin (by calling {@link System#setIn(InputStream)} with
 * the TextComponentInputStream as parameter, or with an {@link java.util.Scanner}-object.<br>
 * The TextComponents content and attributes may be altered while used by this Stream, and thus
 * is compatible with the {@link TextComponentOutputStream} to be used on the same TextComponent at the same time.<br>
 * When typed by the user the text is always appended to the end of the text of the component, and there is no possibility to delete any typed text.
 */
public class TextComponentInputStream extends InputStream {

    /**
     * This Queue contains all the characters typed by the user into the text component
     */
    private LinkedBlockingQueue<Character> blockingQueue;

    /**
     * Whether this stream has been closed or not
     */
    private boolean closed;

    /**
     * This constructor creates an InputStream using the given JTextComponent
     * @param console the JTextComponent to use for this InputStream
     */
    public TextComponentInputStream(JTextComponent console) {
        this.closed = false;
        console.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(!e.isControlDown() && !e.isAltDown() && !e.isAltGraphDown()) {
                    blockingQueue.offer(e.getKeyChar());
                    console.setCaretPosition(console.getDocument().getLength());
                }
            }

            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE){
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e){}
        });

        blockingQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public int read() throws IOException {
        int c = -1;

        try {
            c = blockingQueue.take();
        }catch (InterruptedException exc){
            exc.printStackTrace();
        }

        return c;
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        if(bytes == null){
            throw new NullPointerException();
        }
        if(off < 0 || len < 0){
            throw new IndexOutOfBoundsException();
        }
        if(len == 0){
            return 0;
        }

        int i = 0;
        byte currentItem;

        do{
            currentItem = (byte) read();

            if(currentItem == -1){
                break;
            }

            bytes[off + i++] = currentItem;
        } while(i < len && currentItem != '\n' && !closed);

        return i;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        super.close();
    }
}
