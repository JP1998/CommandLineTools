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

package de.hotzjeanpierre.commandlinetools.command.utils.stream;

import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;

import java.io.*;
import java.nio.charset.Charset;

public class PrintStreamStreamRedirect extends Thread {

    private File src;
    private PrintStream dest;

    private volatile boolean redirecting;

    public PrintStreamStreamRedirect(File src, PrintStream dest) throws IOException {
        this.src = src;
        this.dest = dest;
        this.redirecting = true;

        if(!src.exists() && !src.createNewFile()) {
            throw new IOException(StringProcessing.format(
                    "Couldn't create pipe file with path '{0}'",
                    src
            ));
        }
    }

    public void requestStop() {
        this.redirecting = false;

        try {
            synchronized (this) {
                this.wait();
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void run() {
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(src))) {
            byte[] buffer = new byte[1024];
            int readLength = 0;

            while(redirecting || readLength > 0) {
                if(reader.available() > 0) {
                    readLength = reader.read(buffer, 0, buffer.length);
                    dest.print(new String(buffer, 0, readLength));
                } else {
                    readLength = 0;
                }
            }
        } catch (IOException ignored) { }

        try {
            synchronized (this) {
                this.notify();
            }
        } catch (Exception ignored) {}

        //noinspection ResultOfMethodCallIgnored
        src.delete();
    }
}
