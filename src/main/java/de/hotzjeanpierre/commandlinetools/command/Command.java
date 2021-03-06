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

import de.hotzjeanpierre.commandlinetools.command.exceptions.*;
import de.hotzjeanpierre.commandlinetools.command.parameter.*;
import de.hotzjeanpierre.commandlinetools.command.utils.Assurance;
import de.hotzjeanpierre.commandlinetools.command.utils.Converter;
import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import de.hotzjeanpierre.commandlinetools.command.utils.files.CommonFileUtilities;
import de.hotzjeanpierre.commandlinetools.command.utils.stream.InputStreamRedirect;
import de.hotzjeanpierre.commandlinetools.command.utils.stream.PrintStreamStreamRedirect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * <p>This class is used to easily implement functionality with commands read from console.
 * Therefore you can simply call the static method {@link Command#parseCommand(String)},
 * which gives you an {@link ExecutableCommand}. It may easily be executed by calling {@link ExecutableCommand#execute()}.
 * It will (internally) handle the callback to your command and the passing of all the parameters,
 * which is also a reason why {@link ExecutableCommand} should not be inherited.</p>
 * <p>
 * <p>To make this possible though you'll have to ensure several things:</p>
 * <ul>
 * <li style="margin-bottom:10px;"><b>You'll have to make sure you're using the right {@link Converter}:</b> In case you are using
 * custom / complex types as parameter types (i.e. objects) you'll have to give a custom converter
 * (inheriting Converter) while overriding the {@link Converter#convert(String, Type)}-method
 * to support the type you need. Once created you'll have to give your custom Converter
 * using the {@link Command#setUsedConverter(Converter)}-method ideally before trying to parse
 * a command having a parameter with custom / complex type</li>
 * <li style="margin-bottom:10px;"><b>You'll have to add your custom commands to the pool of supported commands:</b> This can be
 * done in two ways. The first (and probably the one that is more obwious) is to simply call
 * {@link Command#addSupportedCommand(Command)} with a new instance of your custom command.
 * The huge down-side to this though is, that every command is supposed to be a singleton and
 * should directly inherit from {@link Command}, which cannot be ensured with a public constructor.
 * Thus you're supposed to do this in a static initializer-block of your custom command class.
 * This though only ensures the creation and adding of your commands as soon as your
 * custom command classes are loaded, which in turn brings us to the next point.</li>
 * <li><b>Make sure your command classes are loaded (and thus added to the pool of supported commands):</b>
 * This can be done by calling {@link Command#assureLoadingOfCommands(String...)} with the fully qualified
 * class name of your command class. This should be done before the first call to {@link Command#parseCommand(String)}.</li>
 * </ul>
 * <p>
 * <p>To explain these specifications even further here is an outline of a program using this class:</p>
 * <p>
 * <div style="border:1px black solid; background-color:#2B2B2B; color:#D8D8D8;"><pre><code> File: %project%/src/com/example/someapplication/Main.java</code></pre></div>
 * <div style="border:1px black solid; background-color:#2B2B2B; color:#D8D8D8;">
 * <pre><code> <span style="color:#CC7832;">package</span> com.example.someapplication<span style="color:#CC7832;">;</span>
 *
 * <span style="color:#CC7832;">import</span> de.hotzjeanpierre.commandlinetools.command.*<span style="color:#CC7832;">;</span>
 *
 * <span style="color:#CC7832;">public class</span> Main {
 *
 *     <span style="color:#CC7832;">public static void</span> <span style="color:#FFC66D;">main</span>(String[] args) {
 *         Command.setUsedConverter(<span style="color:#CC7832;">new</span> CustomConverter())<span style="color:#CC7832;">;</span>
 *     }
 * }</code></pre>
 * </div>
 * <p>
 * <div style="border:1px black solid; background-color:#2B2B2B; color:#D8D8D8;"><pre><code> File: %project%/src/com/example/someapplication/CustomConverter.java</code></pre></div>
 * <div style="border:1px black solid; background-color:#2B2B2B; color:#D8D8D8;">
 * <pre><code> <span style="color:#CC7832;">package</span> com.example.someapplication<span style="color:#CC7832;">;</span>
 *
 * <span style="color:#CC7832;">import</span> de.hotzjeanpierre.commandlinetools.command.utils.Converter<span style="color:#CC7832;">;</span>
 *
 * <span style="color:#CC7832;">public class</span> CustomConverter <span style="color:#CC7832;">extends</span> Converter {
 *
 *     <span style="color:#BBB529;">&#64;Override</span>
 *     <span style="color:#CC7832;">public</span> Object <span style="color:#FFC66D;">convert</span>(String representation<span style="color:#CC7832;">,</span> Class toConvertTo) {
 *         <span style="color:#CC7832;">if</span>(toConvertTo.equals(CustomType.<span style="color:#CC7832;">class</span>)) {
 *             <span style="color:#CC7832;">return</span> CustomType.parse(representation)<span style="color:#CC7832;">;</span>
 *         } <span style="color:#CC7832;">else</span> {
 *             <span style="color:#CC7832;">return super</span>.convert(representation, toConvertTo)<span style="color:#CC7832;">;</span>
 *         }
 *     }
 * }</code></pre>
 * </div>
 * <p>
 * <div style="border:1px black solid; background-color:#2B2B2B; color:#D8D8D8;"><pre><code> File: %project%/src/com/example/someapplication/CustomConverter.java</code></pre></div>
 * <div style="border:1px black solid; background-color:#2B2B2B; color:#D8D8D8;">
 * <pre><code> <span style="color:#CC7832;">package</span> com.example.someapplication<span style="color:#CC7832;">;</span>
 *
 * <span style="color:#CC7832;">public class</span> CustomType {
 *     <span style="color:#808080;">// This is a custom type of yours</span>
 *
 *     <span style="color:#CC7832;">public static</span> CustomType <span style="color:#FFC66D;">parse</span>(String toParseFrom) {
 *         <span style="color:#808080;">// This method is supposed to parse an object of CustomType from a</span>
 *         <span style="color:#808080;">// string. This may also be done in CustomConverter#convert(String, Class),</span>
 *         <span style="color:#808080;">// although this could make the CustomConverter#convert(String, Class)-method a little messy.</span>
 *     }
 * }</code></pre>
 * </div>
 * <p>
 * <div style="border:1px black solid; background-color:#2B2B2B; color:#D8D8D8;"><pre><code> File: %project%/src/com/example/someapplication/commands/SomeCommand.java</code></pre></div>
 * <div style="border:1px black solid; background-color:#2B2B2B; color:#D8D8D8;">
 * <pre><code> <span style="color:#CC7832;">package</span> com.example.someapplication.commands<span style="color:#CC7832;">;</span>
 *
 * <span style="color:#CC7832;">import</span> de.hotzjeanpierre.commandlinetools.command.*<span style="color:#CC7832;">;</span>
 *
 * <span style="color:#CC7832;">public class</span> SomeCommand <span style="color:#CC7832;">extends</span> Command {
 *
 *     <span style="color:#CC7832;">static</span> {
 *         Command.addSupportedCommand(<span style="color:#CC7832;">new</span> SomeCommand())<span style="color:#CC7832;">;</span>
 *     }
 *
 *     <span style="color:#CC7832;">private</span> <span style="color:#FFC66D;">SomeCommand</span>() {
 *         <span style="color:#CC7832;">super</span>(
 *             <span style="color:#808080;">// The command name</span>
 *             <span style="color:#6A8759;">"somecommand"</span><span style="color:#CC7832;">,</span>
 *             <span style="color:#808080;">// a little description of the command</span>
 *             <span style="color:#6A8759;">"This command does x and y while maintaining condition z."</span><span style="color:#CC7832;">,</span>
 *             <span style="color:#808080;">// The list of parameters for the command</span>
 *             <span style="color:#CC7832;">new</span> Parameter[] {
 *                 <span style="color:#808080;">// a parameter with name "customtypeparameter" of type CustomType,</span>
 *                 <span style="color:#808080;">// the given description and no default value (the null may be omitted)</span>
 *                 <span style="color:#808080;">// Here is to note that parameters with the same name are allowed, but will</span>
 *                 <span style="color:#808080;">// ultimately lead to undefined behaviour</span>
 *                 <span style="color:#CC7832;">new</span> Parameter(
 *                     <span style="color:#6A8759;">"customtypeparameter"</span><span style="color:#CC7832;">,</span>
 *                     CustomType.<span style="color:#CC7832;">class,</span>
 *                     <span style="color:#6A8759;">"This parameter does something with my custom type (also explain what syntax the type uses when being parsed)"</span><span style="color:#CC7832;">,</span>
 *                     <span style="color:#CC7832;">null</span>
 *                 )<span style="color:#CC7832;">,</span>
 *                 <span style="color:#808080;">// a parameter with name "someintparameter" of type Integer (which can be auto-boxed and auto-unboxed to int),</span>
 *                 <span style="color:#808080;">// the given description and the default value of 12</span>
 *                 <span style="color:#CC7832;">new</span> Parameter(
 *                     <span style="color:#6A8759;">"someintparameter"</span><span style="color:#CC7832;">,</span>
 *                     Integer.<span style="color:#CC7832;">class,</span>
 *                     <span style="color:#6A8759;">"This parameter does something to the execution of the command [...]"</span><span style="color:#CC7832;">,</span>
 *                     <span style="color:#6897BB;">12</span>
 *                 )
 *             }
 *         )<span style="color:#CC7832;">;</span>
 *     }
 *
 *     <span style="color:#BBB529;">&#64;Override</span>
 *     <span style="color:#CC7832;">protected void</span> <span style="color:#FFC66D;">execute</span>(ParameterList params) {
 *         <span style="color:#808080;">// You can easily and safely type-cast the parameters since any type-incompatibilities are</span>
 *         <span style="color:#808080;">// caught in the process of creating the actual ExecutableCommand</span>
 *         CustomType customtypeparameter = (CustomType) params.getValue(<span style="color:#6A8759;">"customtypeparameter"</span>)<span style="color:#CC7832;">;</span>
 *         <span style="color:#808080;">// You won't have to handle a non-existing parameter, since the default-value will be</span>
 *         <span style="color:#808080;">// automatically assigned for you (also in the process of creating the actual ExecutableCommand)</span>
 *         <span style="color:#CC7832;">int</span> someintparameter = (<span style="color:#CC7832;">int</span>) params.getValue(<span style="color:#6A8759;">"someintparameter"</span>)<span style="color:#CC7832;">;</span>
 *
 *         <span style="color:#808080;">// here you can do whatever you want the command to do with your parameters</span>
 *     }
 * }</code></pre>
 * </div>
 *
 * @see Converter
 */ // TODO: Update description
public abstract class Command /* implements NamingValidator */ {

    /*                                                                 *
     * =============================================================== *
     *                                                                 *
     *                         Static members                          *
     *                                                                 *
     * =============================================================== *
     *                                                                 */

    /**
     * This Map contains all the currently supported custom commands. Commands may
     * be publicly added by using the method {@link Command#addSupportedCommand(Command)}.
     * Here it is crucial to note that a command with an already existing name
     * would actually delete the old command, whereas we may not allow duplicate adding of names.
     */
    private static Map<String, Command> sSupportedCommands;
    /**
     * This Map contains all the default commands. Their support may be switched off or on
     * by using {@link Command#setDefaultCommandsEnabled(boolean)}.
     */
    private static Map<String, Command> sDefaultCommands;
    /**
     * This variable determines whether the default commands are enabled or not.
     */
    private static boolean sDefaultCommandsEnabled;
    /**
     * The converter to use for conversion of the given parameters representation
     * as String, to the desired type. In case you use a custom type in your code
     * you'll have to give a custom converter using
     * {@link Command#setUsedConverter(Converter)}.
     */
    private static Converter sUsedConverter;

    /**
     * This variable determines whether internally
     * the default commands are currently being loaded.
     */
    private static boolean sDefaultCommandsAreLoading;

    static {

        sSupportedCommands = new HashMap<>();
        sDefaultCommands = new HashMap<>();
        sDefaultCommandsEnabled = true;
        setUsedConverter(new Converter());

        // add all the "normal" default commands to the command list while sDefaultCommandsAreLoading is set to true
        sDefaultCommandsAreLoading = true;
        assureLoadingOfCommands(
                "de.hotzjeanpierre.commandlinetools.command.impl.encryption.EncryptCommand",
                "de.hotzjeanpierre.commandlinetools.command.impl.encryption.DecryptCommand",
                "de.hotzjeanpierre.commandlinetools.command.impl.files.ListFilesCommand",
                "de.hotzjeanpierre.commandlinetools.command.impl.programming.InterpretCommand"
        );

        // add the help command to the command list after sDefaultCommandsAreLoading is set back to false
        // since it is not supposed to be treated as a usual default command but should be available
        // regardless of sDefaultCommandsEnabled
        sDefaultCommandsAreLoading = false;
        assureLoadingOfCommands(
                "de.hotzjeanpierre.commandlinetools.command.Command$HelpCommand"
        );
    }

    public static boolean commandNameExists(String name) {
        name = name.toLowerCase();
        return sDefaultCommands.keySet().contains(name) || sSupportedCommands.keySet().contains(name);
    }

    /*                                                                 *
     * =============================================================== *
     *                                                                 *
     *                         Object members                          *
     *                                                                 *
     * =============================================================== *
     *                                                                 */

    /**
     * This is the name of the command. It determines how the command is to be called, whereas it
     * should be one single word. Otherwise it would have to be written in double quotes, which
     * is extremely inconvenient.
     */
    private String name;
    /**
     * This is the description of the command. It will be written to the standard output stream
     * as soon as the command 'help' (which is implemented by default) is called.
     */
    private String description;
    /**
     * The map of the names of a parameter to the parameter itself. This (datastructure)
     * is used to improve efficiency while parsing a command.
     */
    private Map<String, Parameter> parameterList;
    /**
     * Whether you are supposed to delete the input of the command before executing it, or not.
     */
    private boolean deleteInput;
    /**
     * This list contains the ordered version of all the parameters
     * given a significant ordinal, in the order determined by the
     * assigned ordinal.
     */
    private List<Parameter> orderedParameterList;

    /**
     * This constructor creates a Command with given name, description, parameters.
     * It will not clear the console after execution.
     * The name is used to identify a call of a command, and thus may neither be null nor empty.
     * Same goes for the description except that this is because we want to give the user a
     * pleasant experience. Thus the default command "help" prints the description of a
     * command along with further information about the command.
     * The list of parameters may be null, but may not contain any null items.
     *
     * @param name        the name of the command to construct
     * @param description the description of what the constructed command does
     * @param paramList   the list of parameters wo use for the command (may be null)
     * @throws NullPointerException     if the name, description or one of the items of the given parameter list is null
     * @throws IllegalArgumentException if the name or the description is empty
     */
    protected Command(String name, String description, Parameter[] paramList)
            throws NullPointerException, IllegalArgumentException {
        this(name, description, paramList, false);
    }

    /**
     * This constructor creates a Command with given name, description, parameters and
     * the value that determines whether the console is to be cleared after
     * execution of the command.
     * The name is used to identify a call of a command, and thus may neither be null nor empty.
     * Same goes for the description except that this is because we want to give the user a
     * pleasant experience. Thus the default command "help" prints the description of a
     * command along with further information about the command.
     * The list of parameters may be null, but may not contain any null items.
     *
     * @param name        the name of the command to construct
     * @param description the description of what the constructed command does
     * @param paramList   the list of parameters wo use for the command (may be null)
     * @param deleteInput whether to delete the input for the command before executing it, or not
     * @throws NullPointerException     if the name, description or one of the items of the given parameter list is null
     * @throws IllegalArgumentException if the name or the description is empty
     */
    protected Command(String name, String description, Parameter[] paramList, boolean deleteInput)
            throws NullPointerException, IllegalArgumentException {
        // check name and description for validity
        if (name == null) {
            throw new NullPointerException("Name of a command may not be null.");
        } else if (name.trim().equals("")) {
            throw new IllegalArgumentException("Name of the command may not be empty.");
        }
        if (description == null) {
            throw new NullPointerException("Description of a command may not be null.");
        } else if (description.trim().equals("")) {
            throw new IllegalArgumentException("Description of the command may not be empty.");
        }

        NamingValidator.assureNameValidity(
                name,
                "The command name '{0}' you were trying to assign is not valid.",
                name
        );

        this.name = name;
        this.description = description;

        // transform the parameter list into a hashmap
        this.parameterList = new HashMap<>();
        orderedParameterList = new ArrayList<>();
        if (paramList != null) {
            for (Parameter param : paramList) {
                this.parameterList.put(param.getName(), param);
                if(param.getOrdinal() >= 0) {
                    orderedParameterList.add(param);
                }
            }
        }

        this.deleteInput = deleteInput;

        orderedParameterList.sort(Comparator.comparing(Parameter::getOrdinal));
        for(int i = 0; i < orderedParameterList.size(); i++) {
            orderedParameterList.get(i).setOrdinal(i + 1);
        }
    }


    /*                                                                 *
     * =============================================================== *
     *                                                                 *
     *                         Static methods                          *
     *                                                                 *
     * =============================================================== *
     *                                                                 */

    /**
     * This method makes sure that the classes whose <b>fully qualified</b> names
     * have been given are loaded. Any errors are ignored. This ensures that, in
     * case the class name was correct, your command is added (which your command class
     * is supposed to do in a static initializer block).
     *
     * @param fullyQualifiedClassNames The fully qualified name of the classes to load.
     */
    public static void assureLoadingOfCommands(@NotNull String... fullyQualifiedClassNames) {
        for (String fqcn : fullyQualifiedClassNames) {
            Assurance.tryLoadingClass(fqcn);
        }
    }

    /**
     * This method adds your given command object to the pool of supported commands.
     * A call to this method will be ignored if the given command is {@code null},
     * or if the name of the given command already exists, which also applies to
     * the names of default commands that have been disabled. This prevents anyone
     * from overriding already existing (maybe even crucial default-) commands.
     *
     * @param cmd the command to add to the supported command pool
     */
    public static void addSupportedCommand(Command cmd) {
        if(cmd == null) return;

        if(!commandNameExists(cmd.getName())) {
            if(sDefaultCommandsAreLoading) {
                sDefaultCommands.put(cmd.getName().toLowerCase(), cmd);
            } else {
                sSupportedCommands.put(cmd.getName().toLowerCase(), cmd);
            }
        }
    }

    /**
     * This method sets the converter to use to parse the user input into
     * the values for the parameters of the commands.
     *
     * @param converter the Converter supporting the needed types
     */
    public static void setUsedConverter(Converter converter) {
        if (converter != null) {
            sUsedConverter = converter;
        }
    }

    /**
     * This method lets you en- or disable the default commands.
     * You'll have to note that the names of the default commands are still reserved,
     * whereas any call to {@link Command#addSupportedCommand(Command)} with a command
     * with the name of a default command will be simply ignored.
     * Also default commands will not be shown or recognized by the help command.
     *
     * @param enabled whether default commands should be enabled or not
     */
    public static void setDefaultCommandsEnabled(boolean enabled) {
        sDefaultCommandsEnabled = enabled;
    }

    /**
     * This method returns whether default commands are enabled or not.
     *
     * @return whether default commands are enabled or not
     */
    public static boolean areDefaultCommandsEnabled() {
        return sDefaultCommandsEnabled;
    }

    /**
     * This method parses the an ExecutableCommand from the given String. The format hereby always
     * has to be "&lt;command name&gt; [&lt;parameter name&gt; &lt;parameter value&gt;]+".
     * Any errors that might occur during parsing are ignored.
     *
     * @param toParse the string to parse an ExecutableCommand from
     * @return the ExecutableCommand that has been represented by the given String
     * @throws CommandNotSupportedException           if the command is not found in the supported command pool
     * @throws ParameterNotFoundException             if a given parameter-key is not found for the given command
     * @throws ParameterTypeMismatchException         if one of the parameter-values cannot be converted to the desired type
     * @throws DuplicateParameterException            if one or more of the parameters have been given multiple times
     * @throws MissingParameterException              if one of the parameters without default-value has not been given
     */
    @NotNull
    public static ExecutableCommand parseCommand(String toParse)
            throws CommandNotSupportedException,
            ParameterNotFoundException, ParameterTypeMismatchException,
            DuplicateParameterException, MissingParameterException {

        // tokenize the String so the single tokens can be parsed
        String[] tokens = StringProcessing.tokenizeCommand(toParse);

        // retrieve the command template and assure that it is supported
        Command template = Command.findTemplateFromName(tokens[0].trim().toLowerCase());
        if (template == null) {
            throw new CommandNotSupportedException(StringProcessing.format(
                    "Command '{0}' is not supported.", tokens[0]
            ));
        }

        boolean[] usedListParameters = new boolean[template.orderedParameterList.size()];
        int currentListParameter = 0;
        for(int i = 0; i < usedListParameters.length; i++) {
            usedListParameters[i] = false;
        }

        // parse the single *given* key-value pairs into parameter-values
        List<Parameter.Value> valueList = new ArrayList<>();
        for (int i = 1; i < tokens.length; ) {
            // variables for the amount of tokens used for the parameter,
            // the parameter that get a value assigned and the value that will be assigned
            int jump;
            Parameter param;
            Object value;

            if(tokens[i].startsWith("--")) {
                // handle a boolean parameter with implicit value
                // first determine the length of the prefix and the value to assign
                int stripLength;
                if(tokens[i].startsWith("--not-")) {
                    stripLength = 6;
                    value = false;
                } else {
                    stripLength = 2;
                    value = true;
                }

                // determine the parameter to assign the value to and check it for being null
                param = findParameterByName(template, tokens[i].substring(stripLength).toLowerCase());

                if (param == null) {
                    throw new ParameterNotFoundException(StringProcessing.format(
                            "Parameter '{0}' has not been found for command '{1}'.",
                            tokens[i].substring(stripLength), template.name
                    ));
                }
                jump = 1;
            } else {

                param = findParameterByName(template, tokens[i].toLowerCase());
                if(param == null) {
                    // try to parse the given token as value for the next list parameter

                    // search for the next parameter that has not yet been used
                    while (currentListParameter < template.orderedParameterList.size()
                            && usedListParameters[currentListParameter]) {
                        currentListParameter++;
                    }

                    // if the end of the parameter list is reached we know that the command we need to parse is faulty
                    if(currentListParameter == template.orderedParameterList.size()) {
                        throw new ParameterNotFoundException(StringProcessing.format(
                                "\"{0}\" was not found as parameter name and could also not be implicitly assigned to a parameter.",
                                tokens[i]
                        ));
                    }

                    // get the parameter from the list, flag it as used and parse the value for the parameter
                    param = template.orderedParameterList.get(currentListParameter);
                    usedListParameters[currentListParameter++] = true;
                    value = sUsedConverter.convert(tokens[i], param.getType());

                    if (value == null) {
                        throw new ParameterTypeMismatchException(StringProcessing.format(
                                "Tried to implicitly assign the value '{0}' to the parameter '{1}' of command '{2}'.{3}This parameter though, needs a value of type {4}, which '{0}' could not be parsed to.",
                                tokens[i],
                                param.getName(),
                                template.getName(),
                                System.lineSeparator(),
                                param.getType().getSimpleName()
                        ));
                    }

                    jump = 1;

                } else {
                    // handle any parameter with explicit value given
                    int index = template.orderedParameterList.indexOf(param);
                    if(index != -1) {
                        usedListParameters[index] = true;
                    }

                    value = sUsedConverter.convert(tokens[i + 1], param.getType());
                    jump = 2;
                }
            }

            if (value == null) {
                throw new ParameterTypeMismatchException(StringProcessing.format(
                        "Parameter '{0}' of command '{1}' needs a value of type {2}, which '{3}' could not be parsed to.",
                        param.getName(),
                        template.getName(),
                        param.getType().getSimpleName(),
                        tokens[i + 1]
                ));
            }

            // add the parsed value to the list of values
            valueList.add(param.createValue(value));
            i += jump;
        }

        // now we filter the list, in case it has duplicate values of a parameter key,
        // supplement any not given parameters with default values (also filter those without default value
        // that were not given) and convert the list into a map
        Map<String, Parameter.Value> arguments = filterSupplementAndConvertList(template, valueList);

        // finally create the actually executable command
        return template.new CustomExecutableCommand(arguments);
    }

    public static ShellCommand parseShellCommand(String toParse) {
        return new ShellCommand(StringProcessing.tokenizeCommand(toParse));
    }

    /**
     * This method finds a command template from its name. If the command with
     * given name is not supported this method will return {@code null}.
     * This method will also return {@code null} in case that default commands
     * are disabled and the given command name is not a custom command.
     *
     * @param cmdName the name of the command to retrieve
     * @return the template of the command with given name
     */
    @Nullable
    /* package-protected */ static Command findTemplateFromName(String cmdName) {
        if(sDefaultCommandsEnabled) {
            if(!commandNameExists(cmdName)) {
                return null;
            } else if(sDefaultCommands.containsKey(cmdName)) {
                return sDefaultCommands.get(cmdName);
            } else {
                return sSupportedCommands.get(cmdName);
            }
        } else {
            return sSupportedCommands.get(cmdName);
        }
    }

    /**
     * This method finds you a parameter of the given command template with the
     * given name. If the parameter with given name does not exist in given command
     * this method will return {@code null}.
     *
     * @param template the command template to search for the parameter in
     * @param arg      the name of the parameter to search for
     * @return the parameter if it exists, or {@code null}
     */
    @Nullable
    private static Parameter findParameterByName(@NotNull Command template, String arg) {
        return template.parameterList.get(arg);
    }

    /**
     * This method checks the given parameter for a command to conform to the actual
     * parameter list of the given command template. It thus throws an exception if
     * a parameter has values assigned twice or more, of if the parameter has no value assigned
     * and it has no default value. Otherwise the value will be assigned its default value,
     * and the list of values is converted to a map.
     *
     * @param template the command to take as template for the parameter list
     * @param values   the list of parameter values that were given for the command
     * @return the parameter names mapped to their respective value
     * @throws DuplicateParameterException if a parameter has a value assigned twice or more
     * @throws MissingParameterException   if a parameter without default value has no value assigned
     */
    private static Map<String, Parameter.Value> filterSupplementAndConvertList(@NotNull Command template, List<Parameter.Value> values)
            throws DuplicateParameterException, MissingParameterException {

        Map<String, Parameter.Value> result = new HashMap<>();

        // we iterate through every parameter the command has
        for (String key : template.parameterList.keySet()) {
            Parameter param = template.parameterList.get(key);
            boolean found = false;

            // and go through every given value
            for (Parameter.Value value : values) {
                // if the name of the parameter and the name of the values parameter match
                if (param.getName().equals(value.getParameterName())) {
                    if (!found) {
                        // and we haven't yet found a matching value
                        // we'll simply put the value into the map
                        found = true;
                        result.put(key, value);
                    } else {
                        // otherwise we'll throw an exception
                        throw new DuplicateParameterException(StringProcessing.format(
                                "Parameter '{0}' for command '{1}' has been given multiple times.",
                                param.getName(),
                                template.getName()
                        ));
                    }
                }
            }

            // if there was no value for the parameter
            if (!found) {
                // we'll try to assign the default value for the parameter
                if (template.parameterList.get(key).getDefaultValue() != null) {
                    result.put(key, param.createValue(param.getDefaultValue()));
                } else {
                    // if there is no default value we'll throw an exception
                    throw new MissingParameterException(StringProcessing.format(
                            "Parameter '{0}' has to be given for the command '{1}', since it has no default value.",
                            param.getName(),
                            template.getName()
                    ));
                }
            }
        }

        // as soon as we successfully iterated through the whole parameter list
        // we'll return the result.
        return result;
    }

    /*                                                                 *
     * =============================================================== *
     *                                                                 *
     *                         Object methods                          *
     *                                                                 *
     * =============================================================== *
     *                                                                 */

    /**
     * This method returns the name of the command.
     *
     * @return the name with which the command may be called.
     */
    @Contract(pure = true)
    public final String getName() {
        return name;
    }

    /**
     * This method returns the description of the command.
     *
     * @return the description of a command, which is supposed
     * to give users a hint at what and how it does something.
     */
    @Contract(pure = true)
    public String getDescription() {
        return description;
    }

    /**
     * Gets the parameter with given name of a command.
     * If there is no parameter with given name this method will return {@code null}.
     *
     * @param paramName The name of the parameter that you are looking for
     * @return the parameter with the given name; {@code null} if it does not exist
     */
    @Nullable
    public final Parameter getParameterByName(String paramName) {
        return parameterList.get(paramName);
    }

    @NotNull
    public final String getDocumentation() {
        StringBuilder result = new StringBuilder();

        // Head of the documentation: => name and description in multiple lines
        // Format:
        // <name>:
        //     <line 1 of the description>
        //     <line 2 of the description>
        //     ...
        //     <line n of the description>
        result.append(StringProcessing.format("{0}: {1}", name, System.lineSeparator()));

        String[] descriptionLines = description.split("\\r?\\n");
        for (String line : descriptionLines) {
            result.append(StringProcessing.format("    {0}{1}", line, System.lineSeparator()));
        }

        if (parameterList.size() != 0) {
            // Further documentation: => parameter names and descriptions
            // Format:
            //     - <param_name> (<param_typ>[ |<param_default>]): <line 1 of the description>
            //               <line 2 of the description>
            //               <line 3 of the description>
            //               ...
            //               <line n of the description>
            result.append("  Parameters: ");
            result.append(System.lineSeparator());
            for (String paramKey : parameterList.keySet()) {
                Parameter param = parameterList.get(paramKey);

                String[] paramDescrLines = param.getDescription().split("\\r?\\n");

                result.append(
                        StringProcessing.format(
                                buildParameterLine(param),
                                param.getName(),
                                param.getType().getSimpleName(),
                                param.getDefaultValue(),
                                (param.getType().isEnum())? buildEnumListing((EnumType) param.getType()) : null,
                                paramDescrLines[0],
                                System.lineSeparator(),
                                generateOrdinalString(param)
                        )
                );

                for (int i = 1; i < paramDescrLines.length; i++) {
                    result.append(StringProcessing.format(
                            "              {0}{1}",
                            paramDescrLines[i],
                            System.lineSeparator()
                    ));
                }
            }
        }

        return result.toString();
    }

    /**
     * This method builds the format for the first line of the
     * description of a parameter in the documentation of a command.
     *
     * @param param the parameter for which the line is to be built
     * @return the format to use for the first line of the description of the parameter
     */
    @NotNull
    private static String buildParameterLine(@NotNull Parameter param) {
        StringBuilder result = new StringBuilder("    {6} {0} ({1}");

        if(param.getDefaultValue() != null) {
            result.append("|{2}");
        }

        result.append("): ");

        if(param.getType().isEnum()) {
            result.append("[{3}]; ");
        }

        result.append("{4}{5}");

        return result.toString();
    }

    /**
     * This method generates the string that is supposed
     * to display the ordinal of a parameter.
     *
     * @param param the parameter whose ordinal is to be displayed
     * @return the string showing the ordinal of the parameter
     */
    private String generateOrdinalString(@NotNull Parameter param) {
        if(param.getOrdinal() < 0) {
            return StringProcessing.stretch(
                    "-",
                    ' ',
                    Integer.toString(orderedParameterList.size()).length() + 1,
                    false
            );
        } else {
            return StringProcessing.format(
                    "{0}.",
                    StringProcessing.stretch(
                            param.getOrdinal() + "",
                            ' ',
                            Integer.toString(orderedParameterList.size()).length(),
                            true
                    )
            );
        }
    }

    /**
     * This method builds the listing of the valid values of the
     * given enumeration-type.
     *
     * @param type the type whose enum-values are to be listed
     * @return the listing of the valid values for the given enum-type
     */
    @NotNull
    private static String buildEnumListing(@NotNull EnumType type) {
        StringBuilder result = new StringBuilder();

        Object[] values = type.getEnumType().getEnumConstants();
        for(int i = 0; i < values.length; i++) {
            if(i != 0) {
                result.append(", ");
            }
            result.append(values[i].toString());
        }
        return result.toString();
    }

    /**
     * @return Whether to delete the input before executing the command.
     */
    public boolean isDeleteInput() {
        return deleteInput;
    }

    /**
     * This method is the entry point of the program for your command. The given ParameterList
     * contains a value for every parameter given in the command. These values may either be
     * default values (as given in a parameter) or simply the value the user has given.
     *
     * @param params the list of parameters as defined above
     */
    protected abstract CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream);

    public interface ExecutableCommand {

        boolean isDeleteInput();

        CommandExecutionResult execute();

        CommandExecutionResult execute(PrintStream out);
    }

    public static class ShellCommand implements ExecutableCommand {

        private String[] arguments;

        private ShellCommand(String[] args) {
            this.arguments = args;
        }

        @Override
        public boolean isDeleteInput() {
            return false;
        }

        @Override
        public CommandExecutionResult execute() {
            String lowerOSName = System.getProperty("os.name").toLowerCase();

            int exit;

            try {
                File inputRedirectFile = new File(CommonFileUtilities.getWorkingDirectory(), "CommandLineTools_InputRedirection");
                File outputRedirectFile = new File(CommonFileUtilities.getWorkingDirectory(), "CommandLineTools_OutputRedirection");
                File errorRedirectFile = new File(CommonFileUtilities.getWorkingDirectory(), "CommandLineTools_ErrorRedirection");

                InputStreamRedirect inputRedirect = new InputStreamRedirect(System.in, inputRedirectFile);
                PrintStreamStreamRedirect outputRedirect = new PrintStreamStreamRedirect(outputRedirectFile, System.out);
                PrintStreamStreamRedirect errorRedirect = new PrintStreamStreamRedirect(errorRedirectFile, System.out);

                Process p = new ProcessBuilder(buildArguments(lowerOSName, arguments))
                        .redirectInput(inputRedirectFile)
                        .redirectOutput(outputRedirectFile)
                        .redirectError(errorRedirectFile)
                        .start();


                inputRedirect.start();
                outputRedirect.start();
                errorRedirect.start();

                exit = p.waitFor();

                inputRedirect.requestStop();
                outputRedirect.requestStop();
                errorRedirect.requestStop();

            } catch (IOException | InterruptedException e) {
                exit = -1;
            }

            return new CommandExecutionResult.Builder()
                    .setSuccess(exit == 0)
                    .build();
        }

        private static String[] buildArguments(String lowerOSName, String[] args) {
            boolean windows = lowerOSName.contains("window");
            int offset = (windows)? 2 : 0;

            String[] result = new String[args.length + offset];

            if(windows) {
                result[0] = "cmd";
                result[1] = "/c";
            }

            System.arraycopy(args, 0, result, offset, args.length);

            return result;
        }

        @Override
        public CommandExecutionResult execute(PrintStream out) {
            return execute();
        }
    }

    /**
     * This class is simply a wrapper class,
     * that makes it even easier to execute a parsed command.
     */
    public class CustomExecutableCommand implements ExecutableCommand {

        /**
         * The parameterlist that was given for the execution of the ExecutableCommand
         */
        private ParameterValuesList args;

        /**
         * Creates an ExecutableCommand with the given command as its template and
         * the given map as its parameterlist (which will be converted into a parameterlist).
         *
         * @param args     The parameterlist that is given for the execution of the ExecutableCommand
         */
        private CustomExecutableCommand(Map<String, Parameter.Value> args) {
            this.args = new ParameterValuesList(args);
        }

        /**
         * @return Whether to delete the input before executing this command.
         */
        public boolean isDeleteInput() {
            return Command.this.isDeleteInput();
        }

        /**
         * This method executes the executable command.
         */
        public CommandExecutionResult execute() {
            return execute(System.out);
        }

        public CommandExecutionResult execute(PrintStream outputStream) {
            return Command.this.execute(args, outputStream);
        }
    }

    /**
     * This method is a default implementation of the 'help' command.
     * Since it has to read from the pool of supported commands we need to keep
     * it in close relation to the Command-class itself. This is established
     * through it being an inner class of Command.
     */
    @SuppressWarnings("unused")
    public static class HelpCommand extends Command {

        static {
            Command.addSupportedCommand(new HelpCommand());
        }

        private HelpCommand() {
            super(
                    "help",
                    "Prints the help you are currently reading.",
                    new Parameter[] {
                            new Parameter(
                                    "command",
                                    CommonTypes.String,
                                    "The command to print the documentation for.",
                                    "",
                                    0
                            )
                    }
            );
        }

        @Override
        protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
            String command = ((String) params.getValue("command")).toLowerCase();

            if(command.isEmpty()) {
                outputStream.println("Documentation of all recognized commands: ");
                outputStream.println();

                if(sDefaultCommandsEnabled) {
                    for (String key : sDefaultCommands.keySet()) {
                        outputStream.println(sDefaultCommands.get(key).getDocumentation());
                    }
                }

                for (String key : sSupportedCommands.keySet()) {
                    outputStream.println(sSupportedCommands.get(key).getDocumentation());
                }

                return new CommandExecutionResult.Builder()
                        .setSuccess(true)
                        .build();
            } else {
                if(!sSupportedCommands.containsKey(command)) {
                    if(!sDefaultCommandsEnabled || !sDefaultCommands.containsKey(command)) {
                        outputStream.println(StringProcessing.format(
                                "The command '{0}' was not recognized.",
                                command
                        ));
                        return new CommandExecutionResult.Builder()
                                .setSuccess(false)
                                .build();
                    } else {
                        outputStream.println(StringProcessing.format("Printing help for command '{0}': ", command));
                        outputStream.println(sDefaultCommands.get(command).getDocumentation());

                        return new CommandExecutionResult.Builder()
                                .setSuccess(true)
                                .build();
                    }
                } else {
                    outputStream.println(StringProcessing.format("Printing help for command '{0}': ", command));
                    outputStream.println(sSupportedCommands.get(command).getDocumentation());

                    return new CommandExecutionResult.Builder()
                            .setSuccess(true)
                            .build();
                }
            }
        }
    }
}
