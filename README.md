# CommandLineTools [![Build Status](https://travis-ci.org/JP1998/CommandLineTools.svg?branch=master)](https://travis-ci.org/JP1998/CommandLineTools)

This is a small project that is trying to aim at programmers and computer-affine people. It creates a command line tool,
which is implementing more complex tasks like bulk renaming of files by a certain pattern, or en- / decrypting files with a password.

## Table of contents

1. [Usage of the custom and pre-defined commands](#usage-of-the-custom-and-pre-defined-commands)
2. [List of pre-defined commands](#list-of-pre-defined-commands)
3. [Creation of a new command](#creation-of-a-new-command)
4. [Using custom typed parameters](#using-custom-typed-parameters)
5. [What features are to come in the future](#what-features-are-to-come-in-the-future)

## Usage of the custom and pre-defined commands

To be able to use custom commands you'll need to assure that your commands are loaded into the pool of supported commands, which can be achieved by
calling `Command.assureLoadingOfCommands(String...)` with the fully qualified class name of your command class (more about creating custom
commands [here](#creation-of-a-new-command)). After you made sure that this has happened you can obtain an `ExecutableCommand` by
calling `Command.parse(String)`, which will give your command the right parameters as soon as you do `ExecutableCommand.execute()`.

Considering a command named 'samplecommand' with two parameters: 'param1' having the type of String and no default value
and 'param2' having the type of Integer and the default value of 123 ([Code of the command creation can be seen here](#creation-of-a-new-command)),
the following code will execute said command first with the values of `param1="this is some string"` 
and `param2=321` and afterwards with the values `param1="some other parameter"` and the default value for `param2`:

```Java
package com.yourname.yourproject;

import de.commandlinetools.command.Command;

public class Main {
    
    public static void main(String[] args) {
        Command.assureLoadingOfCommands(
            "com.yourname.yourproject.commands.SampleCommand"
            // Your other command classes can be listed here too, with commas separated
        );
        
        Command.parse(
            "samplecommand param1 \"this is some string\" param2 321"
        ).execute();
        
        Command.parse(
            "samplecommand param1 \"some other parameter\" "
        ).execute();
    }
}
```

### List of pre-defined commands

| Name | Description | Parameters |
|------|-------------|------------|
| help | Prints the descriptions for all available commands | **command** (optional; default: "") - The command for which the documentation is to be printed. |
| encrypt | Encrypts a bulk of files with a given password and saves it | **src** - the source to take files from<br/> **out** - Where to save the encrypted files <br/> **password** - The password to use for encrypting the files <br/> **delsrc** (optional; default: true) - whether to delete the source files after they are processed <br/> **filter** (optional; default: "") - The filter to apply to the files that are to be encrypted; currently only filtering through file extensions is supported <br/> **filtermode** (optional; default: None) - The mode the filter is supposed to act in; AllowOnly will allow only files matching the filter, Filter will allow only files not matching the filter, and None will allow any file <br/> **subdir** (optional; default: true) - Whether to also encrypt files from sub directories <br/> **format** (optional; default: "{index:10}.encr") - The template to use for naming the encrypted files |
| decrypt | Decrypts a bulk of files with a given password and saves it; only works for files encrypted with the encrypt-command | **src** - the source to take files from <br/> **out** - Where to save the decrypted files <br/> **password** - The password to use for decrypting the files <br/> **delsrc** (optional; default: true) - whether to delete the source files after they are processed <br/> **filter** (optional; default: "encr") - The filter to apply to the files that are to be decrypted; currently only filtering through file extensions is supported <br/> **filtermode** (optional; default: AllowOnly) - The mode the filter is supposed to act in; AllowOnly will allow only files matching the filter, Filter will allow only files not matching the filter, and None will allow any file <br/> **subdir** (optional; default: true) - Whether to also decrypt files from sub directories <br/> **format** (optional; default: "{originallocation}{originalname}{extension}") - The template to use for naming the encrypted files |
| list | Lists files contained within a folder | **folder** - The root folder to list files from <br/> **tree** - (optional; default: true) Whether to list the files in a tree structure (highly recommended when also listing files from sub directories) or not <br/> **filter** (optional; default: "") - The filter to apply to the files that are to be listed; currently only filtering through file extensions is supported <br/> **filtermode** (optional; default: None) - The mode the filter is supposed to act in; AllowOnly will allow only files matching the filter, Filter will allow only files not matching the filter, and None will allow any file <br/> **listfolders** (optional; default: true) - Whether to also contain folders in the list of files (we recommend only giving false if also subdir is set to false) <br/> **subdir** (optional; default: false) - Whether to also list files from sub directories or not <br/> **format** (optional; default: "- ${name}") - The template to use for listing files; "${name}" can be used as a placeholder for the file name |



## Creation of a new command

You may easily create your own command by extending the `de.hotzjeanpierre.commandlinetools.command.Command`-class. You will want to make
sure that your custom command cannot be instantiated by anyone other that your class (thus a private constructor is desirable).

To put an instance (which by the way should be but doesn't have to be the only instance of your class) into the pool of supported commands
(which is also handled by the `Command`-class) you may call the `Command.addSupportedCommand(Command)`-function with the instance you want
to add to the pool. But since you should use a private constructor you'll need to do said adding of an instance in a static initializer-block
and assure the loading of your command class. This can be done by calling `Command.assureLoadingOfCommands(String...)`. The string you give
as parameters to this method have to be the fully qualified class names of your custom command classes.

Last but not least you'll have to give the command functionality. This is done by implementing the method `Command.execute(ParameterValuesList, PrintStream)`, whereas
you can get values from the given parameter list by calling `ParameterValuesList.getValue(String)`-method and simply casting the return
value to the desired type. This can be done since the type is enforced while the command is being parsed. Any output that your command wants to
print to the user will instead of to `System.out` will have to be printed to the given PrintStream. This is done for a more dynamic solution.

This means that a command class will possibly look like follows:

```Java
package com.yourname.yourproject.commands;

import de.commandlinetools.command.Command;
import de.commandlinetools.command.CommandExecutionResult;
import de.commandlinetools.command.Parameter;
import de.commandlinetools.command.ParameterValuesList;

public class SampleCommand extends Command {
    
    static {
        Command.addSupportedCommand(new SampleCommand());
    }
    
    private SampleCommand() {
        super(
            // the name of your command; see the doc of the command-constructor
            "samplecommand",
            // some description; may be displayed to the user
            "This command does this and that with the given string and the int you gave.",
            // the list of parameters valid for the command
            new Parameter[] {
                new Parameter(
                    "param1",
                    String.class,
                    "This parameter is to do something like this."
                ),
                new Parameter(
                    "param2",
                    Integer.class,
                    "Some integer parameter with default value 123",
                    123
                )
            }
        );
    }
    
    public CommandExecutionResult execute(ParameterValuesList params, PrintStream output) {
        String param1 = (String) params.getValue("param1");
        int param2 = (int) params.getValue("param2");
        
        // TODO: Do something with the parameter you read from the parameter list.
        
        return new CommandExecutionResult.Builder()
                .setSuccess(true)
                .build();
    }
}
```

## Using custom typed parameters

You can basically provide any type you'd like for a parameter. But unless you either provide a default value for the parameter
or a custom Converter the command with a parameter with said custom type will not be executable. This is because we need to parse
the value we give for a parameter from a string to the desired type. But since the vanilla Converter only supports String, 
primitive types and any enumerated types (so you won't have to worry if you use a custom enum type). Anything other than those
types will have to be either taken as string parameter and parsed by your command, or you'll have to extend Converter and give
your custom Converter to Command using `Command.setUsedConverter(Converter)`.

Since you don't need to implement every single type that might to be parsed, you'll always want to make sure that 
(in the `Converter.convert(String, Class)`-method) in case the given type is not a custom type you'll want to call the method
of the super-class. This makes sure that you can also implement several custom types (maybe someone else uses code from your project
and thus inherits your custom converter-class but doesn't want to copy or modify the code) through inheritance.

A little example converter class could be as follows:
```Java
package com.yourname.yourproject.commands.utils;

import de.hotzjeanpierre.commandlinetools.command.utils.Converter;

public class CustomConverter extends Converter { 
    
    @Override
    public void convert(String representation, Class type) {
        if(CustomType1.class.equals(type)) {
            return CustomType1.parse(representation);
        } else if(CustomType2.class.equals(type)) {
            // as the documentation of Converter#convert(String, Class) declares
            // you should not throw any exceptions from the convert-method
            // but instead return the value null as soon as you recognize an invalid format
            if(CustomType2.isValidRepresentation(representation)) {
                return CustomType2.parse(representation);
            } else {
                return null;
            }
        } else {
            return super.convert(representation, type);
        }
    }
}
```
## What features are to come in the future

- (current top priority:) adding tests for the whole project
- some default commands such as:
  - bulk renaming of files within a directory
    - having an option for the folder to rename the contained files
    - having an option to filter files
    - having an option to rename everything but matches to a pattern
    - having an option to recursively rename within subdirectories
    - having an option to format the result name
  - a password generator / manager
  - (low priority) interpreter for the esoteric programming language Brainfuck
  - (low priority) interpreter for the esoteric programming language False
- a list of parameters given by order (at least for the parameters without default value)
- the possibility of giving boolean parameters without "false" or "true", but with "-&lt;name&gt;" (instead of "&lt;name&gt; false") and "--&lt;name&gt;" (instead of "&lt;name&gt; true")
- some lightweight pattern matching as a better solution for filtering files
- a GUI that is more comfortable to use than the command prompt of Win 7
  - also text based but created by a textbox or similar
  - using streams to read or write (so System.out.println() and Scanner can be used)





