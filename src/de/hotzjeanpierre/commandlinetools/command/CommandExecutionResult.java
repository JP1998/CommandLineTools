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
 * This class specifies a result of the execution of a command. It may indicate whether there was an
 * error during the execution of the command, whether the input of the command is supposed to be deleted from
 * the CLI, and also the output from the command.
 */
public class CommandExecutionResult {

    /**
     * Whether the command has successfully executed.
     */
    private boolean success;

    /**
     * Private, so the Builder has to be used, which is way more comfortable anyways.
     *
     * @param success     whether the command executed succeddfully
     */
    private CommandExecutionResult(
            boolean success
    ) {
        this.success = success;
    }

    /**
     * @return Whether the command has successfully executed.
     */
    public boolean isSuccess() {
        return success;
    }

    public static class Builder {

        /**
         * Whether the input of the command is supposed to be deleted.
         */
        private boolean success;
        /**
         * Whether the {@link #build()}-method has been called on this Builder-object.
         */
        private boolean built;

        /**
         * Creates a Builder with uninitialized values.
         */
        public Builder() {
            success = false;
            built = false;
        }

        /**
         * Sets whether the command terminated with success.
         *
         * @param success whether the command terminated with success
         * @return the Builder for method chaining
         */
        public Builder setSuccess(boolean success) {
            checkState();

            this.success = success;
            return this;
        }

        /**
         * This method checks the state of this Builder, and if it has already been built
         * will throw an exception
         *
         * @throws IllegalStateException in case the Builder-object has already been built
         */
        private void checkState() {
            if (built) {
                throw new IllegalStateException("The Builder you are trying to modify has already been built.");
            }
        }

        /**
         * Builds the CommandExecutionResult-object. The builder-object will get useless afterwards, and any
         * method call on it will result in a {@link IllegalStateException}.
         *
         * @return The CommandExecutionResult created from this Builder
         */
        public CommandExecutionResult build() {
            checkState();

            this.built = true;

            return new CommandExecutionResult(
                    this.success
            );
        }
    }
}
