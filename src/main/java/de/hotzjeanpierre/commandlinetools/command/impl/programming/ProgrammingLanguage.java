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

import de.hotzjeanpierre.commandlinetools.command.impl.programming.lang.brainfuck.BrainfuckMemoryModel;
import de.hotzjeanpierre.commandlinetools.command.impl.programming.lang.brainfuck.BrainfuckParser;

public enum ProgrammingLanguage {

    Brainfuck(new BrainfuckParser(), new BrainfuckMemoryModel.Template());

    private Parser parser;
    private MemoryModel.Template memoryModelTemplate;

    ProgrammingLanguage(Parser parser, MemoryModel.Template memoryModelTemplate) {
        this.parser = parser;
        this.memoryModelTemplate = memoryModelTemplate;
    }

    public Program compile(String str) {
        if(!parser.isValidCode(str)) {
            throw new IllegalArgumentException("The given code contains errors.");
        }

        return new Program(
                this,
                parser.parse(str),
                memoryModelTemplate.createEmptyModel()
        );
    }

}
