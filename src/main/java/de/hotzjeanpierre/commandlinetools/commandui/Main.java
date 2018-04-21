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

import java.util.Scanner;

/**
 * Created by Jonny on 16.04.2018.
 */
public class Main {

    public static void main(String[] args) {
        CommandLineFrame cli = new CommandLineFrame();

        Scanner inputScanner = new Scanner(System.in);

        System.out.println("Please give us your name.");
        System.out.print("Jonny@10.0.0.5>");
        String input = inputScanner.nextLine();
        System.out.println("Hello " + input + "!");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cli.dispose();
    }

}
