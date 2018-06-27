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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamRedirect extends Thread {

    private InputStream src;
    private File dest;

    private volatile boolean redirecting;

    public InputStreamRedirect(InputStream src, File dest) throws IOException {
        this.src = src;
        this.dest = dest;
        this.redirecting = true;

        if(!dest.exists() && !dest.createNewFile()) {
            throw new IOException(StringProcessing.format(
                    "Couldn't create pipe file with path '{0}'",
                    dest
            ));
        }
    }

    public void requestStop() {
        this.redirecting = false;
    }

    @Override
    public void run() {
        try (FileWriter writer = new FileWriter(dest)) {
            while(redirecting) {
                if(src.available() > 0) {
                    writer.write(src.read());
                }
            }
        } catch (IOException ignored) {}

        // noinspection ResultOfMethodCallIgnored
        dest.delete();
    }
}
