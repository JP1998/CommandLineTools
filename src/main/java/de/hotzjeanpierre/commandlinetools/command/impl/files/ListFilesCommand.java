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

package de.hotzjeanpierre.commandlinetools.command.impl.files;

import de.hotzjeanpierre.commandlinetools.command.Command;
import de.hotzjeanpierre.commandlinetools.command.CommandExecutionResult;
import de.hotzjeanpierre.commandlinetools.command.Parameter;
import de.hotzjeanpierre.commandlinetools.command.ParameterValuesList;
import de.hotzjeanpierre.commandlinetools.command.utils.arrays.ArrayHelper;
import de.hotzjeanpierre.commandlinetools.command.utils.files.FileLister;
import de.hotzjeanpierre.commandlinetools.command.utils.files.FilterMode;
import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintStream;

@SuppressWarnings("unused")
public class ListFilesCommand extends Command {

    private static final String COMMAND_NAME = "list";
    private static final String COMMAND_DESCRIPTION = "This command lets you look at the files contained within a folder.\nIt supports listing them as a tree, and also not as a tree (although the tree view is highly recommended).\nAlso it supports filtering files, and a format for how to print the file name.";


    private static final String PARAMETER_NAME_SOURCE = "folder";
    private static final String PARAMETER_DESCRIPTION_SOURCE = "The folder of the files to list.";

    private static final String PARAMETER_NAME_TREE = "tree";
    private static final String PARAMETER_DESCRIPTION_TREE = "Whether to format the output as a tree.";

    private static final String PARAMETER_NAME_FILTER = "filter";
    private static final String PARAMETER_DESCRIPTION_FILTER = "The filter to apply to the search of files.";

    private static final String PARAMETER_NAME_FILTERMODE = "filtermode";
    private static final String PARAMETER_DESCRIPTION_FILTERMODE = "[None, Filter, AllowOnly]; the filter mode to apply.";

    private static final String PARAMETER_NAME_LISTFOLDERS = "listfolders";
    private static final String PARAMETER_DESCRIPTION_LISTFOLDERS = "Whether to list folders or not";

    private static final String PARAMETER_NAME_SUBDIRECTORIES = "subdir";
    private static final String PARAMETER_DESCRIPTION_SUBDIRECTORIES = "Whether to also search within sub directories for files to encrypt.";

    private static final String PARAMETER_NAME_FORMAT = "format";
    private static final String PARAMETER_DESCRIPTION_FORMAT = "The template to use for the output of the files.";

    static {
        Command.addSupportedCommand(
                new ListFilesCommand()
        );
    }

    private ListFilesCommand() {
        super(
                COMMAND_NAME,
                COMMAND_DESCRIPTION,
                new Parameter[] {
                        new Parameter(
                                PARAMETER_NAME_SOURCE,
                                File.class,
                                PARAMETER_DESCRIPTION_SOURCE
                        ),
                        new Parameter(
                                PARAMETER_NAME_TREE,
                                Boolean.class,
                                PARAMETER_DESCRIPTION_TREE,
                                true
                        ),
                        new Parameter(
                                PARAMETER_NAME_FILTER,
                                String.class,
                                PARAMETER_DESCRIPTION_FILTER,
                                ""
                        ),
                        new Parameter(
                                PARAMETER_NAME_FILTERMODE,
                                FilterMode.class,
                                PARAMETER_DESCRIPTION_FILTERMODE,
                                FilterMode.None
                        ),
                        new Parameter(
                                PARAMETER_NAME_LISTFOLDERS,
                                Boolean.class,
                                PARAMETER_DESCRIPTION_LISTFOLDERS,
                                true
                        ),
                        new Parameter(
                                PARAMETER_NAME_SUBDIRECTORIES,
                                Boolean.class,
                                PARAMETER_DESCRIPTION_SUBDIRECTORIES,
                                false
                        ),
                        new Parameter(
                                PARAMETER_NAME_FORMAT,
                                String.class,
                                PARAMETER_DESCRIPTION_FORMAT,
                                "- ${name}"
                        )
                }
        );
    }

    @Override
    protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
        File folder = (File) params.getValue(PARAMETER_NAME_SOURCE);
        boolean tree = (boolean) params.getValue(PARAMETER_NAME_TREE);
        String filter = (String) params.getValue(PARAMETER_NAME_FILTER);
        FilterMode filtermode = (FilterMode) params.getValue(PARAMETER_NAME_FILTERMODE);
        boolean listfolders = (boolean) params.getValue(PARAMETER_NAME_LISTFOLDERS);
        boolean subdir = (boolean) params.getValue(PARAMETER_NAME_SUBDIRECTORIES);
        String format = (String) params.getValue(PARAMETER_NAME_FORMAT);

        if(!folder.exists() || !folder.isDirectory()) {
            outputStream.println("The given file is not a directory we could list file from.");
            return new CommandExecutionResult.Builder().setSuccess(false).build();
        }

        File[] toList = FileLister.list(folder, subdir, filtermode, filter, listfolders);

        list(
                outputStream,
                folder,
                toList,
                0,
                format,
                tree,
                true
        );

        return new CommandExecutionResult.Builder()
                .setSuccess(true)
                .build();
    }

    /**
     * This method recursively lists every fil within the given File (Parameter: {@code a}) to the given
     * PrintStream, while only listing files that are listed within the given File-array. The current
     * file ({@code a}) will be listed at the level of {@code depth}, in case that the files are being
     * listed as a tree.
     * The format allows you to format of how to write the file name. You can use the placeholder {@code "${name}"}
     * for the file name.
     * For the root file of a listing the value of {@code absolute} should be {@code true}, and the
     * value of {@code depth} should ne {@code 0}.
     *
     * @param out The PrintStream to write the output to.
     * @param a The file to list files from.
     * @param toList The files that are actually supposed to be listed.
     * @param depth The depth of the current file.
     * @param format The format for the file name.
     * @param tree Whether to list the files as a tree.
     * @param absolute Whether to list the current file with its absolute name.
     */
    private static void list(PrintStream out, File a, File[] toList, int depth, String format, boolean tree, boolean absolute) {
        // in case the current file is absolute (and thus the root-folder)
        // or it is contained in the files that are to be listed
        if(absolute || ArrayHelper.containsAny(toList, a)) {

            // we'll print the file itself
            out.println(
                    makeTree(tree, depth) + makeFileName(absolute, format, a)
            );

            // and if it is an directory (which it most likey is)
            if(a.isDirectory()) {
                // we'll try to list all its children
                File[] children = a.listFiles();

                if(children != null) {
                    for(File f : children) {
                        list(
                                out,
                                f,
                                toList,
                                depth + 1,
                                format,
                                tree,
                                false
                        );
                    }
                }
            }
        }
    }

    /**
     * This method generates the file name from a format, the file itself and the information
     * about whether we are supposed to take the absolute file path.
     *
     * @param abs whether we are supposed to take the absolute file name.
     * @param format the format to print the file name in.
     * @param f the file whose name we are supposed to use.
     * @return the file name formatted with the given format.
     */
    @NotNull
    @Contract(pure = true)
    private static String makeFileName(boolean abs, @NotNull String format, @NotNull File f) {
        return StringProcessing.format(format.replace("${name}", "{0}"), abs? f.getAbsolutePath() : f.getName());
    }

    /**
     * This method generates a tree based on the information whether we're supposed to
     * create a tree and the depth that is supposed to be generated.
     *
     * @param tree whether a tree is supposed to be generated.
     * @param depth the depth to generate
     * @return the generated tree
     */
    @NotNull
    private static String makeTree(boolean tree, int depth) {
        if(!tree || depth <= 0) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();

            for(int i = 0; i < depth; i++) {
                result.append("   |");
            }

            return result.toString();
        }
    }
}
