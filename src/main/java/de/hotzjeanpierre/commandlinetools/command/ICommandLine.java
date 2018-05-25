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

/**
 * Created by Jonny on 21.04.2018.
 */
public interface ICommandLine {

    /**
     * This method is supposed to setup the command line.
     */
    void setupCLI();

    /**
     * This method is called whenever the execution of a command starts.
     *
     * @param cmd the input that lead to the execution of the command
     */
    default void onStartExecution(String cmd) {}

    /**
     * This method is called whenever the execution of a command has ended.
     */
    default void onEndExecution() {}

    /**
     * This method is supposed to clear the command line.
     */
    void clearCLI();

    /**
     * This method is supposed to dispose the command line
     */
    void disposeCLI();
}
