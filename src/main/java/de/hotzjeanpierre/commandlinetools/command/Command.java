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
import de.hotzjeanpierre.commandlinetools.command.utils.Assurance;
import de.hotzjeanpierre.commandlinetools.command.utils.Converter;
import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * (inheriting Converter) while overriding the {@link Converter#convert(String, Class)}-method
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
public abstract class Command implements NamingValidator {

    /*                                                                 *
     * =============================================================== *
     *                                                                 *
     *                         Static members                          *
     *                                                                 *
     * =============================================================== *
     *                                                                 */

    /**
     * This Map contains all the currently supported commands. Commands may
     * be publicly added by using the method {@link Command#addSupportedCommand(Command)}.
     * Here it is crucial to note that a command with an already existing name
     * would actually delete the old command, whereas we may not allow duplicate adding of names.
     */
    private static Map<String, Command> sSupportedCommands;
    /**
     * The converter to use for conversion of the given parameters representation
     * as String, to the desired type. In case you use a custom type in your code
     * you'll have to give a custom converter using
     * {@link Command#setUsedConverter(Converter)}.
     */
    private static Converter sUsedConverter;

    static {
        sSupportedCommands = new HashMap<>();
        setUsedConverter(new Converter());

        assureLoadingOfCommands(
                "de.hotzjeanpierre.commandlinetools.command.Command$HelpCommand",
                "de.hotzjeanpierre.commandlinetools.command.impl.encryption.EncryptCommand",
                "de.hotzjeanpierre.commandlinetools.command.impl.encryption.DecryptCommand",
                "de.hotzjeanpierre.commandlinetools.command.impl.files.ListFilesCommand"
        );
    }

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

    protected Command(String name, String description, Parameter[] paramList)
            throws NullPointerException, IllegalArgumentException {
        this(name, description, paramList, false);
    }

    /**
     * This constructor creates a Command with given name, description and parameters.
     * The name is used to identify a call of a command, and thus may neitber be null nor empty.
     * Same goes for the description except that this is because we want to give the user a
     * pleasant expierence. Thus the default command "help" prints the description of a
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

        assureNameValidity(
                name,
                "The command name '{0}' you were trying to assign is not valid.",
                name
        );

        this.name = name;
        this.description = description;

        this.parameterList = new HashMap<>();
        if (paramList != null) {
            for (Parameter param : paramList) {
                this.parameterList.put(param.getName(), param);
            }
        }

        this.deleteInput = deleteInput;
    }

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
     * or if the name of the given command already exists. This prevents anyone from overriding
     * already existing (maybe even crucial default-) commands.
     *
     * @param cmd the command to add to the supported command pool
     */
    public static void addSupportedCommand(Command cmd) {
        if (cmd != null && !sSupportedCommands.keySet().contains(cmd.getName().trim())) {
            sSupportedCommands.put(cmd.getName(), cmd);
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


    /*                                                                 *
     * =============================================================== *
     *                                                                 *
     *                         Object members                          *
     *                                                                 *
     * =============================================================== *
     *                                                                 */

    /**
     * This method parses the an ExecutableCommand from the given String. The format hereby always
     * has to be "&lt;command name&gt; [&lt;parameter name&gt; &lt;parameter value&gt;]+".
     * Any errors that might occur during parsing are ignored.
     *
     * @param toParse the string to parse an ExecutableCommand from
     * @return the ExecutableCommand that has been represented by the given String
     * @throws CommandArgumentNumberMismatchException if the one of the parameters is not a key-value pair
     * @throws CommandNotSupportedException           if the command is not found in the supported command pool
     * @throws ParameterNotFoundException             if a given parameter-key is not found for the given command
     * @throws ParameterTypeMismatchException         if one of the parameter-values cannot be converted to the desired type
     * @throws DuplicateParameterException            if one or more of the parameters have been given multiple times
     * @throws MissingParameterException              if one of the parameters without default-value has not been given
     */
    @NotNull
    public static ExecutableCommand parseCommand(String toParse)
            throws CommandArgumentNumberMismatchException, CommandNotSupportedException,
            ParameterNotFoundException, ParameterTypeMismatchException,
            DuplicateParameterException, MissingParameterException {

        // tokenize the String and make sure the number of key-value pairs for the parameters is valid.
        String[] tokens = StringProcessing.tokenizeCommand(toParse);
        if (tokens.length % 2 != 1) {
            throw new CommandArgumentNumberMismatchException(StringProcessing.format(
                    "Command may not have odd number of parameters; Given:{0};\nPlease use the format '<commandname> [<parameterkey> <parametervalue>]+'",
                    tokens.length - 1
            ));
        }

        // retrieve the command template and assure that it is supported
        Command template = Command.findTemplateFromName(tokens[0].trim());
        if (template == null) {
            throw new CommandNotSupportedException(StringProcessing.format(
                    "Command '{0}' is not supported.", tokens[0]
            ));
        }

        // parse the single *given* key-value pairs into parameter-values
        List<Parameter.Value> valueList = new ArrayList<>();
        for (int i = 1; i < tokens.length; i += 2) {
            // retrieve the parameter and assure it exists for the given command
            Parameter param = findParameterByName(template, tokens[i]);
            if (param == null) {
                throw new ParameterNotFoundException(StringProcessing.format(
                        "Parameter '{0}' has not been found for command '{1}'.",
                        tokens[i], template.name
                ));
            }

            // parse the value from the string and the type of the parameter
            // assure that the value has been correctly parsed into the given type6
            Object value = sUsedConverter.convert(tokens[i + 1], param.getType());

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
        }

        // now we filter the list, in case it has duplicate values of a parameter key,
        // supplement any not given parameters with default values (also filter those without default value
        // that were not given) and convert the list into a map
        Map<String, Parameter.Value> arguments = filterSupplementAndConvertList(template, valueList);

        // finally create the actually executable command
        return template.new ExecutableCommand(arguments);
    }

    /**
     * This method finds a command template from its name. If the command with
     * given name is not supported this method will return {@code null}.
     *
     * @param cmdName the name of the command to retrieve
     * @return the template of the command with given name
     */
    @Nullable
    /* package-protected */ static Command findTemplateFromName(String cmdName) {
        return sSupportedCommands.get(cmdName);
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
     */ // TODO: Test explicitly
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
        result.append(StringProcessing.format("{0}: \n", name));

        String[] descriptionLines = description.split("\\r?\\n");
        for (String line : descriptionLines) {
            result.append(StringProcessing.format("    {0}\n", line));
        }

        if (parameterList.size() != 0) {
            // Further documentation: => parameter names and descriptions
            // Format:
            //     - <param_name> (<param_typ>[ |<param_default>]): <line 1 of the description>
            //               <line 2 of the description>
            //               <line 3 of the description>
            //               ...
            //               <line n of the description>
            result.append("  Parameters: \n");
            for (String paramKey : parameterList.keySet()) {
                Parameter param = parameterList.get(paramKey);

                String[] paramDescrLines = param.getDescription().split("\\r?\\n");

                if (param.getDefaultValue() == null) {
                    result.append(StringProcessing.format(
                            "    - {0} ({1}): {2}\n",
                            param.getName(),
                            param.getType().getSimpleName(),
                            paramDescrLines[0]
                    ));
                } else {
                    result.append(StringProcessing.format(
                            "    - {0} ({1}|{2}): {3}\n",
                            param.getName(),
                            param.getType().getSimpleName(),
                            param.getDefaultValue(),
                            paramDescrLines[0]
                    ));
                }

                for (int i = 1; i < paramDescrLines.length; i++) {
                    result.append(StringProcessing.format(
                            "              {0}\n",
                            paramDescrLines[i]
                    ));
                }
            }
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

    /**
     * This class is simply a wrapper class,
     * that makes it even easier to execute a parsed command.
     */
    public class ExecutableCommand {

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
        private ExecutableCommand(Map<String, Parameter.Value> args) {
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
                                    String.class,
                                    "The command to print the documentation for.",
                                    ""
                            )
                    }
            );
        }

        @Override
        protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
            String command = (String) params.getValue("command");

            if(command.isEmpty()) {
                outputStream.println("Documentation of all recognized commands: ");
                outputStream.println();

                for (String key : sSupportedCommands.keySet()) {
                    outputStream.println(sSupportedCommands.get(key).getDocumentation());
                }

                return new CommandExecutionResult.Builder()
                        .setSuccess(true)
                        .build();
            } else {
                if(!sSupportedCommands.containsKey(command)) {
                    outputStream.println(StringProcessing.format(
                            "The command '{0}' was not recognized.",
                            command
                    ));
                    return new CommandExecutionResult.Builder()
                            .setSuccess(false)
                            .build();
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
