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

package de.hotzjeanpierre.commandlinetools.command.impl.programming;

import de.hotzjeanpierre.commandlinetools.command.Command;
import de.hotzjeanpierre.commandlinetools.command.CommandExecutionResult;
import de.hotzjeanpierre.commandlinetools.command.parameter.*;

import java.io.PrintStream;

public class InterpretCommand extends Command {

    private static final String COMMAND_NAME = "interpret";
    private static final String COMMAND_DESCRIPTION = "This command lets you interpret certain programs in various (mostly esoteric) programming languages.\nFor now it only supports the following programming languagen:\n - Brainfuck";

    private static final String PARAMETER_NAME_LANG = "lang";
    private static final String PARAMETER_DESCRIPTION_LANG = "The language in which the given source code is to be interpreted / compiled.";

    private static final String PARAMETER_NAME_SOURCE = "src";
    private static final String PARAMETER_DESCRIPTION_SOURCE = "The source code to interpret or compile and execute.";

    static {
        Command.addSupportedCommand(new InterpretCommand());
    }

    private InterpretCommand() {
        super(
                COMMAND_NAME,
                COMMAND_DESCRIPTION,
                new Parameter[] {
                        new Parameter(
                                PARAMETER_NAME_LANG,
                                new EnumType(ProgrammingLanguage.class),
                                PARAMETER_DESCRIPTION_LANG,
                                0
                        ),
                        new Parameter(
                                PARAMETER_NAME_SOURCE,
                                CommonTypes.String,
                                PARAMETER_DESCRIPTION_SOURCE,
                                1
                        )
                }
        );
    }

    @Override
    protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
        return null;
    }
}
