# CommandLineTools [![Build Status](https://travis-ci.org/JP1998/CommandLineTools.svg?branch=master)](https://travis-ci.org/JP1998/CommandLineTools)

This is a small project that is trying to aim at programmers and computer-affine people. It creates a command line,
which is implementing more complex tasks like bulk renaming of files by a certain pattern, or en- / decrypting files
with a password. Furthermore it makes it way easier to simply implement own commands that can then be used by you or
other people you deliver your solution to.

## Table of contents

1. [Usage of the custom and pre-defined commands](#usage-of-the-custom-and-pre-defined-commands)
2. [List of pre-defined commands](#list-of-pre-defined-commands)
3. [Creation of a new command](#creation-of-a-new-command)
4. [Using custom typed parameters](#using-custom-typed-parameters)
5. [What features are to come in the future](#what-features-are-to-come-in-the-future)

## Usage of the custom and pre-defined commands

To be able to use custom commands you'll need to assure that your commands are loaded into the pool of supported commands, which can be achieved by
calling `Command.assureLoadingOfCommands(String...)` with the fully qualified class name of your command class (more about creating custom
commands [here](#creation-of-a-new-command)). This is automatically done for the default commands such as `encrypt`. After you made sure that
this has happened you can obtain an `ExecutableCommand` by calling `Command.parse(String)`, which will give your command the right parameters
as soon as you call `ExecutableCommand.execute()`.

The method `Command.parse(String)` takes in a string in a format specified as follows:
The first word (sequence of characters not interrupted by whitespace) has to be a command name, where a command name is defined to
begin with either a standard latin character (a-z; upper or lower case) or an underscore, and may be followed by aforementioned
characters or a digit (0-9).
After the commands name you can give any parameter a value explicitly by first writing the parameter name and afterwards the value, which will
be attempted to be parsed and to an object of the desired type. The value (or rather anything after the command name) can be either given
without double quotes, which means the word will be used as is, or you can include it within double quotes, which means you can include
whitespace, but you'll have to escape special characters like '\n' for a line break. You will have to note though that only the standard
escape sequences `\\`, `\"`, `\'`, `\t`, `\n`, `\b`, `\r`and `\f` are supported.
Thus a parameter of type integer can be given as '`"-123"`' and a parameter of type String can be given as '`asdf`'.
The value of parameters can also be given implicitly by writing the values in the order of the
ordinals of the parameters, while parameters that were explicitly given before the implicit assignment will be skipped. Also
parameters of type 'Boolean' can be given explicitly while still only using a single word, by adding the prefix '`--`' (for '`true`')
or '`--not-`' (for '`false`') to the parameter name.

The name, type and ordinal of the parameters of a command can be read by calling the `help`-command. For example the command `help encrypt` will
print any available information about the `list`-command. The output will look as follows:
```
John Doe>help encrypt
Printing help for command 'encrypt': 
encrypt: 
    This command allows you to encrypt multiple files into several encrypted files.
    It supports filtering of files within a folder, formatting of the filename of the resulting files
    and recursive searching for files that are to be encrypted.
  Parameters: 
    -  filter (String|): The filter to apply to the search of files.
    -  subdir (boolean|true): Whether to also search within sub directories for files to encrypt.
    -  filtermode (FilterMode|None): [None, Filter, AllowOnly]; the filter mode to apply.
    3. password (String): The password to use for encrypting the files.
    1. src (File): The folder of the files to encrypt.
    -  format (FileNamingTemplate|{index:10}.encr): The template to use for the name of the encrypted files.
    -  delsrc (boolean|true): Whether to delete the source files, or not.
    2. out (File): Where to save the encrypted files.

```

As you can see there is a brief description of the command after the actual name of the command. Such a description is also provided
for any parameter that can be applied to the command. In case the parameter has an ordinal it will be printed before the parameters
name. In case it has no ordinal there will be a dash instead. The type of the parameter is given after the parameters name in brackets.
The default value of the parameter is given after its type (in the same brackets) while delimited by a vertical dash.
In case the parameter is of enumerated type the valid values for this type will be given as first in the description of the parameter
collected in rectangular brackets.

Considering the previously shown `encrypt`-command you can call said command with the parameters `password="asdf1234"`,
`src="/somedirectory/toEncrypt/"` and `out="/somedirectory/encrypted/"` as shown in the command sample below.

You can also create a command named 'samplecommand' with two parameters: 'param1' having the type of String and no default value
and 'param2' having the type of Integer and the default value of 123, whereas 'param1' has the ordinal 1 and 'param2' the ordinal 2
([Code of the command creation can be seen here](#creation-of-a-new-command)), in the following code sample this command will
execute said command first with the values of `param1="this is some string"` and `param2=321`, then with the values
`param1="some other parameter"` and the default value for `param2`, and finally with the values `param1="asdf1234"` and `param2=456`:

```Java
package com.yourname.yourproject;

import de.commandlinetools.command.Command;

public class Main {
    
    public static void main(String[] args) {
        // examples of calling the encrypt command.
        // As this is a default command you don't have to assure the loading of it
        Command.parse(
            // here the parameters are only given through implicit assignments
            "encrypt /somedirectory/toEncrypt/ /somedirectory/encrypted/ asdf1234"
        ).execute();

        Command.parse(
            // here only the parameter out is given explicitly
            // since its value is given before it is tried to be assigned implicitly
            // it is simply skipped in the list of implicit parameters
            "encrypt out /somedirectory/encrypted/ /somedirectory/toEncrypt/ asdf1234"
        ).execute();

        Command.parse(
            // here all of the parameters are given explicitly whereas the order does not matter whatsoever
            "encrypt password asdf1234 out /somedirectory/encrypted/  src /somedirectory/toEncrypt/"
        ).execute();

        // now we have to load the mentioned custom command
        Command.assureLoadingOfCommands(
            "com.yourname.yourproject.commands.SampleCommand"
            // Your other command classes can be listed here too, with commas separated
        );
        
        // now we can use it as already explained before with explicit or implicit value assignments
        Command.parse(
            "samplecommand param1 \"this is some string\" param2 321"
        ).execute();
        
        Command.parse(
            "samplecommand param1 \"some other parameter\" "
        ).execute();
        
        Command.parse(
            "samplecommand asdf1234 456"
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
sure that your custom command cannot be instantiated by anyone other that your class (thus a private constructor is desirable), since
the command is supposed to be loaded through (light) reflection, which makes this framework very versatile while still being easy to use.

To put an instance (which by the way should be but doesn't have to be the only instance of your class) into the pool of supported commands
(which is also handled by the `Command`-class) you may call the `Command.addSupportedCommand(Command)`-function with the instance you want
to add to the pool. But since you should use a private constructor you'll need to do said adding of an instance in a static initializer-block
and assure the loading of your command class by calling `Command.assureLoadingOfCommands(String...)` while passing the fully qualified class
names of you command-classes to said method.

Last but not least you'll have to give the command functionality. This is done by implementing the method `Command.execute(ParameterValuesList, PrintStream)`, whereas
you can get values from the given parameter list by calling `ParameterValuesList.getValue(String)`-method and simply casting the return
value to the desired type. This can be done since the type of any parameter is enforced while the command is being parsed. Any output that your command wants to
display to the user will have to be printed to the given PrintStream instead of to `System.out`. This is done for a more dynamic solution allowing
custom command lines that do not use the `System.out`-stream.

At the end of the execution you'll have to return a `CommandExecutionResult` by creating one through its inner `Builder`-class, and telling it
whether the execution of your command has ended in a success. This is done to further 

This means that a command class will possibly look like follows:

```Java
package com.yourname.yourproject.commands;

import de.commandlinetools.command.Command;
import de.commandlinetools.command.CommandExecutionResult;
import de.commandlinetools.command.parameter.CommonTypes;
import de.commandlinetools.command.parameter.Parameter;
import de.commandlinetools.command.parameter.ParameterValuesList;

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
                    CommonTypes.String,
                    "This parameter is to do something like this.",
                    1
                ),
                new Parameter(
                    "param2",
                    CommonTypes.Primitives.Integer,
                    "Some integer parameter with default value 123",
                    (Integer) 123
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
types will have to be either taken as string parameter and parsed by your command (which would mean that the execution of your
command could also fail due to a failure in parsing said parameters), or you'll have to extend Converter and give
your custom Converter to Command using `Command.setUsedConverter(Converter)`.

Since you don't need to implement every single type that might have to be parsed, you'll always want to make sure that 
(in the `Converter.convert(String, Class)`-method) in case the given type is not a custom type you'll want to call the method
of the super-class. This makes sure that you can also implement several custom types (maybe someone else uses code from your project
and thus inherits your custom converter-class but doesn't want to copy or modify the code) through inheritance.

A little example converter class could be as follows:
```Java
package com.yourname.yourproject.commands.utils;

import de.hotzjeanpierre.commandlinetools.command.utils.Converter;
import de.hotzjeanpierre.commandlinetools.command.parameter.Type;

public class CustomConverter extends Converter { 
    
    private static final Type CustomType1_Type = new ObjectType(CustomType1.class);
    private static final Type CustomType2_Type = new ObjectType(CustomType2.class);

    @Override
    public void convert(String representation, Type type) {
        if(CustomType1_Type.equals(type)) {
            return CustomType1.parse(representation);
        } else if(CustomType2_Type.equals(type)) {
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
- some lightweight pattern matching as a better solution for filtering files
- making the execution of shell / console commands possible
- creating the possibility for smart auto-complete in the ui-client
- adding the possibility to add a custom ICommandLine-type to the Main-class,
    which should be able to be used with certain command-line arguments
- allow nested command calls (like "list (find asdf C:/Users/)", which should list all the files in any directory containing the phrase "asdf" in "C:/Users/")?
