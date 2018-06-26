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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides an easy wrapper for existing InputStreams to be used as
 * CommandLineInputStream. It does not provide a history by default.
 */
public abstract class CommandLineInputStreamWrapper extends CommandLineInputStream {

    private InputStream wrappedInputStream;

    public CommandLineInputStreamWrapper(InputStream wrappedInputStream) {
        this.wrappedInputStream = wrappedInputStream;
    }

    @Override
    public int read() throws IOException {
        return wrappedInputStream.read();
    }

    @Override
    public int read(@NotNull byte[] b) throws IOException {
        return wrappedInputStream.read(b);
    }

    @Override
    public int read(@NotNull byte[] b, int off, int len) throws IOException {
        return wrappedInputStream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return wrappedInputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return wrappedInputStream.available();
    }

    @Override
    public void close() throws IOException {
        wrappedInputStream.close();
        super.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        wrappedInputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        wrappedInputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return wrappedInputStream.markSupported();
    }

}
