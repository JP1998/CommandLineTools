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

import de.hotzjeanpierre.commandlinetools.command.exceptions.*;
import de.hotzjeanpierre.commandlinetools.command.testutilities.CommandTestingStream;
import de.hotzjeanpierre.commandlinetools.command.testutilities.SomeClass;
import de.hotzjeanpierre.commandlinetools.command.testutilities.SomeSubClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

    @Test(expected = CommandNotSupportedException.class)
    public void testUnsupportedCommand() {
        Command.parseCommand("someunsupportedcommand someignoredparameter someignoredparametervalue");
    }

    @Test(expected = ParameterNotFoundException.class)
    public void testUnknownParameter() {
        Command.addSupportedCommand(
                new SomeCustomCommand(
                        "command_testunknownparameter",
                        "some description",
                        new Parameter[] {
                                new Parameter("supportedparam1", SomeClass.class, "some description", new SomeClass(1, 2.3)),
                                new Parameter("supportedparam2", SomeClass.class, "some description", new SomeClass(1, 2.3)),
                                new Parameter("supportedparam2", SomeClass.class, "some description", new SomeClass(1, 2.3))
                        }
                )
        );

        Command.parseCommand("command_testunknownparameter unsupportedparam true");
    }

    @Test(expected = ParameterTypeMismatchException.class)
    public void testCommandParameterTypeMismatch() {
        Command.addSupportedCommand(
                new SomeCustomCommand(
                        "command_testparametertypemismatch",
                        "some description",
                        new Parameter[] {
                                new Parameter("supportedparam1", SomeClass.class, "some description", new SomeClass(1, 2.3)),
                                new Parameter("supportedparam2", SomeClass.class, "some description", new SomeClass(1, 2.3)),
                                new Parameter("supportedparam2", SomeClass.class, "some description", new SomeClass(1, 2.3))
                        }
                )
        );

        Command.parseCommand("command_testparametertypemismatch supportedparam1 asdf");
    }

    @Test
    public void testCommandParameterTypeValid() {
        Command.addSupportedCommand(
                new SomeCustomCommand(
                        "command_testparametertypevalid",
                        "some description",
                        new Parameter[] {
                                new Parameter("supportedparam1", String.class, "some description")
                        }
                )
        );

        Command.parseCommand("command_testparametertypevalid supportedparam1 \"This is \\\"some\\\" text\"");
    }

    @Test(expected = DuplicateParameterException.class)
    public void testCommandDuplicateParameterFailing() {
        Command.addSupportedCommand(
                new SomeCustomCommand(
                        "command_testduplicateparameterfailing",
                        "some description",
                        new Parameter[] {
                                new Parameter("supportedparam1", String.class, "some description")
                        }
                )
        );

        Command.parseCommand("command_testduplicateparameterfailing supportedparam1 asdf supportedparam1 ghjk");
    }

    @Test
    public void testCommandDefaultValueTaken() {
        Command.addSupportedCommand(
                new DefaultValueEnforcingCommand(
                        "command_testdefaultvaluetaken",
                        "some description",
                        new Parameter[]{
                                new Parameter("supportedparam1", String.class, "some description", "default")
                        }
                )
        );

        Command.parseCommand("command_testdefaultvaluetaken").execute();
    }

    @Test(expected = MissingParameterException.class)
    public void testCommandMissingParameterWithoutDefaultValueFailing() {
        Command.addSupportedCommand(
                new SomeCustomCommand(
                        "command_testmissingparameterwithoutdefaultvaluefailing",
                        "some description",
                        new Parameter[]{
                                new Parameter("supportedparam1", SomeClass.class, "description", null)
                        }
                )
        );

        Command.parseCommand("command_testmissingparameterwithoutdefaultvaluefailing");
    }

    @Test
    public void testGetDocumentationFormatting() {
        Command toTestFor = new SomeCustomCommand(
                "command_testgetdocumentationformatting",
                "this is some kind of description of the command.\nIt is split across several lines and should be\ndisplayed very neatly.",
                new Parameter[]{
                        new Parameter(
                                "supportedparam1",
                                String.class,
                                "This is the description of the parameter.\nIts default value will do this and that.",
                                "Hello World"
                        ),
                        new Parameter(
                                "supportedparam2",
                                String.class,
                                "This is the description of another parameter.\nIts has no default value."
                        ),
                }
        );

        assertThat(
                toTestFor.getDocumentation(),
                is(
                        "command_testgetdocumentationformatting: \n" +
                        "    this is some kind of description of the command.\n" +
                        "    It is split across several lines and should be\n" +
                        "    displayed very neatly.\n" +
                        "  Parameters: \n" +
                        "    - supportedparam1 (String|Hello World): This is the description of the parameter.\n" +
                        "              Its default value will do this and that.\n" +
                        "    - supportedparam2 (String): This is the description of another parameter.\n" +
                        "              Its has no default value.\n"
                )
        );
    }

    @Test
    public void testExecutableCommandIsDeleteInput() {
        Command.addSupportedCommand(
                new SomeCustomCommand(
                        "command_testexecutablecommandisdeleteinput",
                        "some description",
                        new Parameter[0],
                        true
                )
        );

        assertThat(
                Command.parseCommand("command_testexecutablecommandisdeleteinput").isDeleteInput(),
                is(true)
        );
    }

    @Test
    public void testHelpAll() {
        assertThat(
                Command.parseCommand("help").execute().isSuccess(),
                is(true)
        );
    }

    @Test
    public void testHelpOutput() {
        CommandTestingStream stream = new CommandTestingStream();
        Command.parseCommand("help command help").execute(new PrintStream(stream));

        assertThat(
                stream.evaluate(),
                is(
                        "Printing help for command 'help': \r\n" +
                                "help: \n" +
                                "    Prints the help you are currently reading.\n" +
                                "  Parameters: \n" +
                                "    - command (String|): The command to print the documentation for.\n\r\n"
                )
        );
    }

    @Test
    public void testHelpSpecificAvailableCommand() {
        Command.addSupportedCommand(
                new SomeCustomCommand(
                        "command_testhelpspecificavailablecommand",
                        "some description",
                        new Parameter[0]
                )
        );

        assertThat(
                Command
                        .parseCommand(
                                "help command command_testhelpspecificavailablecommand"
                        )
                        .execute()
                        .isSuccess(),
                is(true)
        );
    }

    @Test
    public void testHelpSpecificUnavailableCommand() {
        assertThat(
                Command.parseCommand("help command someunknowncommand").execute().isSuccess(),
                is(false)
        );
    }

    @Test
    public void testHelpSpecificUnavailableCommandOutput() {
        CommandTestingStream stream = new CommandTestingStream();
        Command.parseCommand("help command someunknowncommand").execute(new PrintStream(stream));

        assertThat(
                stream.evaluate(),
                is("The command 'someunknowncommand' was not recognized.\r\n")
        );
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

    static class DefaultValueEnforcingCommand extends Command {

        private Parameter[] params;

        public DefaultValueEnforcingCommand(String name, String description, Parameter[] params) {
            super(name, description, params);
            this.params = params;
        }

        @Override
        protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
            for (Parameter param : this.params) {
                if (!params.getValue(param.getName()).equals(param.getDefaultValue())) {
                    throw new IllegalArgumentException("Parameter '" + param.getName() + "' is not set to its default value.");
                }
            }

            return null;
        }
    }
}
