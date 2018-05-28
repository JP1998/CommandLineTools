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
import de.hotzjeanpierre.commandlinetools.command.utils.arrays.ArrayHelper;
import de.hotzjeanpierre.commandlinetools.commandui.CommandLineFrame;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Main implements ICommandLineApplication {

    private static final Main APPLICATION = new Main();

    private static final ICommandLine DEFAULT_CLI = new ICommandLine() {
        @Override
        public void setupCLI(ICommandLineApplication associatedApplication) { }

        @Override
        public void clearCLI() {
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

        @Override
        public void disposeCLI() { }
    };

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
                "de.hotzjeanpierre.commandlinetools.Main$ClearCommand"
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
            } catch (Exception exc) {
                System.out.println("Seems like your instruction included mistakes. Here's the error message:");
                System.out.println(exc.getMessage());
                error = true;
            }

            if (!error) {

                cli.onStartExecution(input);

                if(cmd.isDeleteInput()) {
                    // Delete the input made before writing the output that results from the command
                    cli.clearCLI();
                }

                CommandExecutionResult result = cmd.execute();

                if (!result.isSuccess()) {
                    System.out.println("Command finished without success.");
                }

                cli.onEndExecution();
            }
        }

        cli.disposeCLI();
    }

    @Override
    public void onCLITermination() {
        this.running = false;
    }

    private static ICommandLine determineCLI(String[] args) {
        if(ArrayHelper.containsAny(args, "ui", "-ui", "/ui")) {
            return new CommandLineFrame();
        } else {
            return DEFAULT_CLI;
        }
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
            APPLICATION.cli.clearCLI();
            return new CommandExecutionResult.Builder()
                    .setSuccess(true)
                    .build();
        }
    }
}
