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

package de.hotzjeanpierre.commandlinetools;

import de.hotzjeanpierre.commandlinetools.command.*;
import de.hotzjeanpierre.commandlinetools.command.exceptions.CommandNotSupportedException;
import de.hotzjeanpierre.commandlinetools.command.parameter.CommonTypes;
import de.hotzjeanpierre.commandlinetools.command.parameter.Parameter;
import de.hotzjeanpierre.commandlinetools.command.parameter.ParameterValuesList;
import de.hotzjeanpierre.commandlinetools.commandui.FrameCommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main implements ICommandLineApplication {

    private static final Main APPLICATION = new Main();

    private static final ICommandLine DEFAULT_CLI = new ICommandLine() {
        private CommandLineInputStream usedInputStream;

        @Override
        public void setupCLI(ICommandLineApplication associatedApplication) {
            usedInputStream = new CommandLineInputStreamWrapper(System.in) {
                @Override
                public void clear() {
                    String lowerOSName = System.getProperty("os.name").toLowerCase();

                    if(lowerOSName.contains("window")) {
                        try {
                            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                    }
                }
            };
        }

        @Override
        public CommandLineInputStream getUsedInputStream() {
            return usedInputStream;
        }

        @Override
        public void disposeCLI() { }
    };

    private static final Map<String, ICommandLine> sAvailableCommandLines;

    static {
        sAvailableCommandLines = new HashMap<>();
        addCommandLine("default", DEFAULT_CLI);
        addCommandLine("ui", new FrameCommandLine());
    }

    public static void addCommandLine(String term, ICommandLine cmd) {
        NamingValidator.assureNameValidity(
                term,
                "The term '{0}' is not easy to give as parameter and is thus not allowed as name of a commandline.",
                term
        );

        if(cmd != null && !sAvailableCommandLines.containsKey(term) &&
                !sAvailableCommandLines.containsValue(cmd)) {
            sAvailableCommandLines.put(term, cmd);
        }
    }

    public static void main(String[] args) {
        APPLICATION.execute(args);
    }

    private ICommandLine cli;

    private boolean running;

    public void execute(String[] args) {
        cli = determineCLI(args);

        cli.setupCLI(this);

        Command.assureLoadingOfCommands(
                "de.hotzjeanpierre.commandlinetools.Main$ExitCommand",
                "de.hotzjeanpierre.commandlinetools.Main$ClearCommand",
                "de.hotzjeanpierre.commandlinetools.Main$InfoCommand"
        );

        running = true;

        Scanner reader = new Scanner(System.in);

        while (running) {
            System.out.print(System.getProperty("user.name") + ">");

            String input = null;
            Command.ExecutableCommand cmd = null;
            boolean error;

            try {
                input = reader.nextLine();
                cmd = Command.parseCommand(input);
                error = false;
            } catch (CommandNotSupportedException exc) {
                cli.getUsedInputStream().setRecordInput(false);
                System.out.print("Your command has not been recognized.\nShould we try to execute it as shell command? [y/n] ");
                String answer = reader.nextLine();
                System.out.println();
                if(answer.trim().toLowerCase().startsWith("y")) {
                    cmd = Command.parseShellCommand(input);
                    error = false;
                } else {
                    error = true;
                }
                cli.getUsedInputStream().setRecordInput(true);
            } catch (Exception exc) {
                System.out.println("Seems like your instruction included mistakes. Here's the error message:");
                System.out.println(exc.getMessage());
                System.out.println();
                error = true;
            }

            if (!error) {

                cli.getUsedInputStream().setRecordInput(false);

                cli.onStartExecution(input);

                if(cmd.isDeleteInput()) {
                    // Delete the input made before writing the output that results from the command
                    cli.getUsedInputStream().clear();
                }

                CommandExecutionResult result = cmd.execute();

                if (!result.isSuccess()) {
                    System.out.println("Command finished without success.");
                }

                cli.onEndExecution();

                cli.getUsedInputStream().setRecordInput(true);
            }
        }

        cli.disposeCLI();
    }

    @Override
    public void onCLITermination() {
        this.running = false;
    }

    private static ICommandLine determineCLI(String[] args) {
        ICommandLine commandLineToUse = null;

        for(int i = 0; i < args.length; i++) {
            if(args[i].trim().toLowerCase().equals("--cli") && args.length > i + 1) {
                commandLineToUse = sAvailableCommandLines.get(args[i + 1].trim().toLowerCase());
            }
        }

        if(commandLineToUse == null) {
            commandLineToUse = DEFAULT_CLI;
        }

        return commandLineToUse;
    }

    @SuppressWarnings("unused")
    public static class ExitCommand extends Command {

        static {
            Command.addSupportedCommand(new ExitCommand(
                    "exit",
                    "This command exits the current program.",
                    new Parameter[0]
            ));
        }

        private ExitCommand(String name, String descr, Parameter[] params) {
            super(name, descr, params);
        }

        @Override
        protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
            APPLICATION.running = false;
            return new CommandExecutionResult.Builder()
                    .setSuccess(true)
                    .build();
        }
    }

    @SuppressWarnings("unused")
    public static class ClearCommand extends Command {

        static {
            Command.addSupportedCommand(new ClearCommand(
                    "clear",
                    "Clears the current output from the command line.",
                    new Parameter[0]
            ));
        }

        private ClearCommand(String name, String description, Parameter[] params) {
            super(name, description, params);
        }

        @Override
        protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
            APPLICATION.cli.getUsedInputStream().clear();
            return new CommandExecutionResult.Builder()
                    .setSuccess(true)
                    .build();
        }
    }

    @SuppressWarnings("unused")
    public static class InfoCommand extends Command {

        static {
            Command.addSupportedCommand(
                    new InfoCommand(
                            "info",
                            "This command gives you some information about this application.",
                            new Parameter[] {
                                    new Parameter(
                                            "surprise",
                                            CommonTypes.Primitives.Integer,
                                            "A little surprise.",
                                            (Object) (-1)
                                    )
                            }
                    )
            );
        }

        private InfoCommand(String name, String description, Parameter[] paramList)
                throws NullPointerException, IllegalArgumentException {
            super(name, description, paramList);
        }

        private static final String[] APPLICATION_INFORMATION = {
                "",
                "          CommandLineTools v1.0.0-SNAPSHOT",
                "initial idea by Jean-Pierre Hotz (https://github.com/JP1998/)",
                "",
                "This work has been licensed under the Apache License version 2.0:",
                "",
                "    Copyright 2018 Jean-Pierre Hotz",
                "",
                "Licensed under the Apache License, Version 2.0 (the \"License\");",
                "you may not use this file except in compliance with the License.",
                "You may obtain a copy of the License at",
                "",
                "    http://www.apache.org/licenses/LICENSE-2.0",
                "",
                "Unless required by applicable law or agreed to in writing, software",
                "distributed under the License is distributed on an \"AS IS\" BASIS,",
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.",
                "See the License for the specific language governing permissions and",
                "limitations under the License.",
                "",
                "This work is open source, whereas you can view the source code of /",
                "contribute to this project at https://github.com/JP1998/CommandLineTools.",
                "",
                "Following is a list of all contributors at time of compilation:",
                "Jean-Pierre Hotz (https://github.com/JP1998)",
                "Thor77 (https://github.com/Thor77)",
                ""
        };

        @Override
        protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
            int surprisecode = (int) params.getValue("surprise");

            if(surprisecode > 0) {
                if(APPLICATION.cli instanceof FrameCommandLine) {
                    FrameCommandLine uicli = (FrameCommandLine) APPLICATION.cli;

                    if(!uicli.isValidSurprise(surprisecode)) {
                        for(String line : APPLICATION_INFORMATION) {
                            System.out.println(line);
                        }
                    } else {
                        if(uicli.isSurprise(surprisecode)) {
                            System.out.println("You've been unbamboozled!");
                        } else {
                            System.out.println("You've been bamboozled!");
                        }
                        System.out.println();
                        uicli.toggleSurprise((int) params.getValue("surprise"));
                    }
                }
            } else {
                for(String line : APPLICATION_INFORMATION) {
                    System.out.println(line);
                }
            }

            return new CommandExecutionResult.Builder()
                    .setSuccess(true)
                    .build();
        }
    }
}
