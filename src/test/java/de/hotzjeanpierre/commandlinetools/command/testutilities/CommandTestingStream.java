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

package de.hotzjeanpierre.commandlinetools.command.testutilities;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

public class CommandTestingStream extends OutputStream {

    private StringBuilder output;
    private boolean evaluated;

    public CommandTestingStream() {
        output = new StringBuilder();
        evaluated = false;
    }

    @Override
    public void write(@NotNull byte[] b) {
//        super.write(b);
        output.append(new String(b));
    }

    @Override
    public void write(@NotNull byte[] b, int off, int len) {
//        super.write(b, off, len);
        output.append(new String(b, off, len));
    }

    @Override
    public void write(int b) {}

    public String evaluate() {
        if(evaluated) {
            return null;
        }
        evaluated = true;
        return output.toString();
    }
}
