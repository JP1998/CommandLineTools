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

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import de.hotzjeanpierre.commandlinetools.command.exceptions.CommandArgumentNumberMismatchException;
import de.hotzjeanpierre.commandlinetools.command.testutilities.SomeClass;
import de.hotzjeanpierre.commandlinetools.command.testutilities.SomeSubClass;
import org.junit.Test;

import java.io.PrintStream;

public class CommandTest {

    @Test(expected = NullPointerException.class)
    public void testCommandNullName() {
        new Command(null, "some description", new Parameter[0], false) {
            @Override
            protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
                return null;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommandEmptyName() {
        new Command("", "some description", new Parameter[0], false) {
            @Override
            protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
                return null;
            }
        };
    }

    @Test(expected = NullPointerException.class)
    public void testCommandNullDescription() {
        new Command("somevalidcommandname", null, new Parameter[0], false) {
            @Override
            protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
                return null;
            }
        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommandEmptyDescription() {
        new Command("somevalidcommandname", "", new Parameter[0], false) {
            @Override
            protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
                return null;
            }
        };
    }

    @Test(expected = NamingValidator.InvalidNameException.class)
    public void testCommandInvalidName() {
        new Command("this is some invalid name", "and this some description", new Parameter[0], false) {
            @Override
            protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
                return null;
            }
        };
    }

    @Test
    public void testCommandAcceptingName() {
        assertThat(
                new Command("somename", "some description", new Parameter[0], false) {
                    @Override
                    protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
                        return null;
                    }
                }.getName(),
                is("somename")
        );
    }

    @Test
    public void testCommandAcceptingDescription() {
        assertThat(
                new Command("somename", "some description", new Parameter[0], false) {
                    @Override
                    protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
                        return null;
                    }
                }.getDescription(),
                is("some description")
        );
    }

    @Test
    public void testCommandAcceptingParameterList() {
        Parameter toRetrieve = new Parameter(
                "someparameter2",
                SomeClass.class,
                "some description"
        );

        assertThat(
                new Command(
                        "somecommand",
                        "some description",
                        new Parameter[]{
                                new Parameter("irrelevant", SomeSubClass.class, "some irrelevant description"),
                                toRetrieve
                        },
                        false) {
                    @Override
                    protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
                        return null;
                    }
                }.getParameterByName(toRetrieve.getName()),
                is(toRetrieve)
        );
    }

    @Test
    public void testCommandWithDefaultDeleteInput() {
        assertThat(
                new SomeCustomCommand("n", "d", null),
                is(new SomeCustomCommand("n", "d", null, false))
        );
    }

    @Test
    public void testCommandLoading() {
        Command.assureLoadingOfCommands("de.hotzjeanpierre.commandlinetools.command.CommandTest$SomeUnloadedCommand");
        assertNotNull(Command.parseCommand("someunloadedcommand"));
    }

    @Test
    public void testDuplicateCommandsIgnored() {
        Command.addSupportedCommand(
                new SomeCustomCommand("somename", "some description", new Parameter[0])
        );
        Command.addSupportedCommand(
                new SomeCustomCommand("somename", "some different description", new Parameter[0])
        );

        assertThat(
                Command.findTemplateFromName("somename").getDescription(),
                is("some description")
        );
    }

    @Test(expected = CommandArgumentNumberMismatchException.class)
    public void testParsingInvalidNumberOfParameters() {
        Command.parseCommand("somecommand somparameter");
    }

    @SuppressWarnings("unused")
    static class SomeUnloadedCommand extends Command {

        static {
            Command.addSupportedCommand(new SomeUnloadedCommand());
        }

        private SomeUnloadedCommand() {
            super("someunloadedcommand", "description", new Parameter[0]);
        }

        @Override
        protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
            return null;
        }
    }

    static class SomeCustomCommand extends Command {

        public SomeCustomCommand(String name, String descr, Parameter[] params) {
            super(name, descr, params);
        }

        public SomeCustomCommand(String name, String descr, Parameter[] params, boolean deloutput) {
            super(name, descr, params, deloutput);
        }

        @Override
        protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SomeCustomCommand &&
                    getName().equals(((SomeCustomCommand) obj).getName()) &&
                    getDescription().equals(((SomeCustomCommand) obj).getDescription()) &&
                    isDeleteInput() == ((SomeCustomCommand) obj).isDeleteInput();
        }
    }

}
