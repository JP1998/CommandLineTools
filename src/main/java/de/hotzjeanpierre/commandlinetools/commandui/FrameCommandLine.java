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

package de.hotzjeanpierre.commandlinetools.commandui;

import de.hotzjeanpierre.commandlinetools.command.CommandLineInputStream;
import de.hotzjeanpierre.commandlinetools.command.ICommandLine;
import de.hotzjeanpierre.commandlinetools.command.ICommandLineApplication;
import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import de.hotzjeanpierre.commandlinetools.command.utils.arrays.ArrayHelper;

public class FrameCommandLine implements ICommandLine {

    private static final Integer[] VALIDSURPRISECODES = { 1 };
    private static final String DEFAULT_TITLE = "CommandLineTools v1.0.0-SNAPSHOT";

    private ConsoleFrame shownConsole;

    @Override
    public void setupCLI(ICommandLineApplication associatedApplication) {
        this.shownConsole = new ConsoleFrame(associatedApplication, DEFAULT_TITLE);
    }

    @Override
    public CommandLineInputStream getUsedInputStream() {
        return this.shownConsole.getUsedInputStream();
    }

    @Override
    public void onStartExecution(String cmd) {
        this.shownConsole.setTitle(StringProcessing.format("Executing \"{0}\"", cmd));
    }

    @Override
    public void onEndExecution() {
        this.shownConsole.setTitle(DEFAULT_TITLE);
    }

    @Override
    public void disposeCLI() {
        this.shownConsole.dispose();
        this.shownConsole = null;
    }

    public void toggleSurprise(int i) {
        switch (i) {
            case 1: shownConsole.setSurprise1(!isSurprise(i)); break;
        }
    }

    public boolean isSurprise(int i) {
        switch (i) {
            case 1: return shownConsole.isSurprise1();
            default: return false;
        }
    }

    public boolean isValidSurprise(int i) {
        return ArrayHelper.containsAny(VALIDSURPRISECODES, i);
    }
}
