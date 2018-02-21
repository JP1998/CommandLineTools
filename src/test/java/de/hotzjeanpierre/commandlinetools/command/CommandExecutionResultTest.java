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

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CommandExecutionResultTest {

    @Test(expected = IllegalStateException.class)
    public void testSecondBuildFailing() {
        CommandExecutionResult.Builder builder = new CommandExecutionResult.Builder();

        builder.build();
        builder.build();
    }

    @Test
    public void testFirstBuildSuccess() {
        CommandExecutionResult.Builder builder = new CommandExecutionResult.Builder();

        CommandExecutionResult result = builder.build();

        assertNotNull(result);
    }

    @Test
    public void testSuccessAcceptingValue() {
        CommandExecutionResult result = new CommandExecutionResult.Builder()
                .setSuccess(true)
                .build();

        assertThat(
                result.isSuccess(),
                is(true)
        );
    }
}
