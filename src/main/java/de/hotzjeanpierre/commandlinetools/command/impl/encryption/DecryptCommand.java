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

package de.hotzjeanpierre.commandlinetools.command.impl.encryption;

import de.hotzjeanpierre.commandlinetools.command.Command;
import de.hotzjeanpierre.commandlinetools.command.CommandExecutionResult;
import de.hotzjeanpierre.commandlinetools.command.Parameter;
import de.hotzjeanpierre.commandlinetools.command.ParameterValuesList;
import de.hotzjeanpierre.commandlinetools.command.utils.files.*;
import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

@SuppressWarnings("unused")
public class DecryptCommand extends Command {

    private static final String COMMAND_NAME = "decrypt";
    private static final String COMMAND_DESCRIPTION = "This command allows you to decrypt files encrypted with the 'encrypt'-command.\nIt supports all the parameters that are also supported by the 'encrypt'-command.";


    private static final String PARAMETER_NAME_SOURCE = "src";
    private static final String PARAMETER_DESCRIPTION_SOURCE = "The folder of the files to decrypt.";

    private static final String PARAMETER_NAME_DELETESOURCE = "delsrc";
    private static final String PARAMETER_DESCRIPTION_DELETESOURCE = "Whether to delete the source files, or not.";

    private static final String PARAMETER_NAME_FILTER = "filter";
    private static final String PARAMETER_DESCRIPTION_FILTER = "The filter to apply to the search of files.";

    private static final String PARAMETER_NAME_FILTERMODE = "filtermode";
    private static final String PARAMETER_DESCRIPTION_FILTERMODE = "[None, Filter, AllowOnly]; the filter mode to apply.";

    private static final String PARAMETER_NAME_SUBDIRECTORIES = "subdir";
    private static final String PARAMETER_DESCRIPTION_SUBDIRECTORIES = "Whether to also search within sub directories for files to decrypt.";

    private static final String PARAMETER_NAME_OUTPUTPATH = "out";
    private static final String PARAMETER_DESCRIPTION_OUTPUTPATH = "Where to save the decrypted files.";

    private static final String PARAMETER_NAME_FORMAT = "format";
    private static final String PARAMETER_DESCRIPTION_FORMAT = "The template to use for the decrypted files.";

    private static final String PARAMETER_NAME_PASSWORD = "password";
    private static final String PARAMETER_DESCRIPTION_PASSWORD = "The password to use for decrypting the files.";

    static {
        Command.addSupportedCommand(
                new DecryptCommand()
        );
    }

    private DecryptCommand() {
        super(
                COMMAND_NAME,
                COMMAND_DESCRIPTION,
                new Parameter[]{
                        new Parameter(
                                PARAMETER_NAME_SOURCE,
                                File.class,
                                PARAMETER_DESCRIPTION_SOURCE
                        ),
                        new Parameter(
                                PARAMETER_NAME_DELETESOURCE,
                                Boolean.class,
                                PARAMETER_DESCRIPTION_DELETESOURCE,
                                true
                        ),
                        new Parameter(
                                PARAMETER_NAME_FILTER,
                                String.class,
                                PARAMETER_DESCRIPTION_FILTER,
                                "encr"
                        ),
                        new Parameter(
                                PARAMETER_NAME_FILTERMODE,
                                FilterMode.class,
                                PARAMETER_DESCRIPTION_FILTERMODE,
                                FilterMode.AllowOnly
                        ),
                        new Parameter(
                                PARAMETER_NAME_SUBDIRECTORIES,
                                Boolean.class,
                                PARAMETER_DESCRIPTION_SUBDIRECTORIES,
                                true
                        ),
                        new Parameter(
                                PARAMETER_NAME_OUTPUTPATH,
                                File.class,
                                PARAMETER_DESCRIPTION_OUTPUTPATH
                        ),
                        new Parameter(
                                PARAMETER_NAME_FORMAT,
                                FileNamingTemplate.class,
                                PARAMETER_DESCRIPTION_FORMAT,
                                FileNamingTemplate.parse("{originallocation}{originalname}{extension}")
                        ),
                        new Parameter(
                                PARAMETER_NAME_PASSWORD,
                                String.class,
                                PARAMETER_DESCRIPTION_PASSWORD
                        )
                },
                true
        );
    }

    @Override
    protected CommandExecutionResult execute(ParameterValuesList params, PrintStream outputStream) {
        File src = (File) params.getValue(PARAMETER_NAME_SOURCE);
        boolean delsrc = (boolean) params.getValue(PARAMETER_NAME_DELETESOURCE);
        String filter = (String) params.getValue(PARAMETER_NAME_FILTER);
        FilterMode filtermode = (FilterMode) params.getValue(PARAMETER_NAME_FILTERMODE);
        boolean subdirectories = (boolean) params.getValue(PARAMETER_NAME_SUBDIRECTORIES);
        File output = (File) params.getValue(PARAMETER_NAME_OUTPUTPATH);
        FileNamingTemplate format = (FileNamingTemplate) params.getValue(PARAMETER_NAME_FORMAT);
        String password = (String) params.getValue(PARAMETER_NAME_PASSWORD);

        CommandExecutionResult.Builder syso = new CommandExecutionResult.Builder();

        // create a secret key spec from the given password to use for decryption
        FileEncryptor.HashingResult secretkeyresult = FileEncryptor.createPrivateKey(password);

        if (!secretkeyresult.isSuccess()) {
            outputStream.println(secretkeyresult.getErrorMessage());
            return syso.setSuccess(false)
                    .build();
        }

        File[] toDecrypt;

        try {
            // determine all the files to decrypt
            toDecrypt = FileLister.list(
                    src,
                    subdirectories,
                    filtermode,
                    filter,
                    false
            );
        } catch (IllegalArgumentException exc) {
            outputStream.println(exc.getMessage());
            return syso.setSuccess(false)
                    .build();
        }

        int index = 0;

        for (File f : toDecrypt) {
            // read from the file and decrypt it
            FileEncryptor.EncryptionResult result = FileEncryptor.decryptFile(
                    secretkeyresult.getSecretKey(), f
            );

            if (result.isSuccess()) {
                // build the new files name
                FileNamingData data = FileNamingData.Builder.build(result, index++);
                File outFile = new File(output, format.produceFileNane(data));

                // create the files parent directories in case they don't already exist
                if (!outFile.getParentFile().exists()) {
                    if (!outFile.getParentFile().mkdirs()) {
                        outputStream.println(StringProcessing.format(
                                "Couldn't create folder '{0}'. Will abort execution of command.\nThere might already be files processed by this command.",
                                outFile.getParentFile()
                        ));
                        return syso.setSuccess(false)
                                .build();
                    }
                }

                // try to write the (decrypted) data to the file
                try {
                    FileEncryptor.writeFile(outFile, result.getData());
                } catch (IOException e) {
                    outputStream.println(StringProcessing.format(
                            "Couldn't write decrypted data to file '{0}'. Will abort execution of command.\nThere might already be files processed by the command.",
                            outFile
                    ));
                    return syso.setSuccess(false)
                            .build();
                }

                // Try deleting the source file in case we are supposed to do so
                if (delsrc && !f.delete()) {
                    outputStream.println(StringProcessing.format(
                            "Couldn't delete file '{0}'. Please try deleting it manually.",
                            f
                    ));
                }
            } else {
                // if there was an error we'll show the message and abort the command
                outputStream.println(StringProcessing.format(
                        "File '{0}' could not be decrypted.\nFollowing error was produced in an attempt to encrypt said file:\n{1}\nDid you give the correct password?",
                        f.getAbsolutePath(),
                        result.getErrorMessage()
                ));
                return syso.setSuccess(false)
                        .build();
            }
        }

        if(delsrc) {
            EmptyFolderDeleter.deleteIfEmpty(src);
        }

        return syso.setSuccess(true)
                .build();
    }
}
