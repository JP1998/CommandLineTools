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

package de.hotzjeanpierre.commandlinetools.command.utils.files;

import de.hotzjeanpierre.commandlinetools.command.development.DebuggingPurpose;
import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import de.hotzjeanpierre.commandlinetools.command.utils.files.exceptions.EncryptionAbortedException;
import de.hotzjeanpierre.commandlinetools.command.utils.files.exceptions.HashingAbortedException;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * This class can be used to en- or decrypt single files and save the
 * encrypted data to a specified file. The name of the original file
 * is also saved within the encrypted data, whereas the name is restored
 * as soon as its decrypted again.
 */
public class EncryptionService {

    /**
     * This method creates a private key from the given password. The returned {@link HashingResult}
     * will contain whether the Hashing-process was successful, if so it will also contain a
     * {@link SecretKeySpec}, otherwise an error message (and for debugging purposes the stacktrace)
     *
     * @param pw the password to create a private key from
     * @return the result of the hashing denoting whether it was successful,
     * the errors message (if applicable), and the SecretKeySpec (also if applicable)
     */
    @NotNull
    public static HashingResult createPrivateKey(String pw) {
        try {
            byte[] key = pw.getBytes(Charset.forName("UTF-8"));
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            key = sha256.digest(key);

            key = Arrays.copyOf(key, 16);

            return new HashingResult(new SecretKeySpec(key, "AES"));
        } catch (Exception e) {
            return new HashingResult(new HashingAbortedException("Hashing has been aborted.", e));
        }
    }

    /**
     * This method encrypts the given byte data with the given secret key.
     *
     * @param data the data to encrypt
     * @param pw   the secret key to use for encryption
     * @return the result of the encryption
     */
    @NotNull
    public static EncryptionResult encrypt(byte[] data, SecretKeySpec pw) {
        try {
            Cipher aes = Cipher.getInstance("AES");
            aes.init(Cipher.ENCRYPT_MODE, pw);
            return new EncryptionResult(aes.doFinal(data));
        } catch (Exception exc) {
            return new EncryptionResult(exc);
        }
    }

    /**
     * This method decrypts the given data with the given secret key.
     *
     * @param data the data to decrypt
     * @param pw   the secret key to use for decryption
     * @return the decrypted data
     */
    @NotNull
    public static EncryptionResult decrypt(byte[] data, SecretKeySpec pw) {
        try {
            Cipher aes = Cipher.getInstance("AES");
            aes.init(Cipher.DECRYPT_MODE, pw);
            return new EncryptionResult(aes.doFinal(data));
        } catch (Exception exc) {
            return new EncryptionResult(exc);
        }
    }

    /**
     * This method encrypts the data from the given file with the given secret key.
     * The returned {@link FileEncryptionResult} contains whether the encryption process was successful,
     * if so the encrypted data, otherwise an error message (and for debugging purposes the stacktrace).
     * You can then (in case the encryption was successful) save the data to another file.
     *
     * @param pw the secret key used for encryption
     * @param in the file that is to be encrypted
     */
    @NotNull
    public static FileEncryptionResult encryptFile(SecretKeySpec pw, File in, File relativeTo) {
        if (in == null || !in.isFile() || !in.exists()) {
            return new FileEncryptionResult(
                    new EncryptionAbortedException(StringProcessing.format(
                            "The file '{0}' does not exist and can thus not be encrypted.",
                            (in != null) ? in.getAbsolutePath() : "null"
                    ), null)
            );
        }

        try {
            byte[] data = CommonFileUtilities.readFile(in);

            String filename = in.getAbsolutePath().substring(
                    relativeTo.getAbsolutePath().length(),
                    in.getAbsolutePath().length()
            );

            byte[] filenameBytes = filename.getBytes();

            ByteBuffer buffer = ByteBuffer.allocate(4 + filenameBytes.length + data.length);

            buffer.putInt(filenameBytes.length);
            buffer.put(filenameBytes);
            buffer.put(data);

            EncryptionResult encryptionResult = encrypt(buffer.array(), pw);

            if(!encryptionResult.isSuccess()) {
                return new FileEncryptionResult(encryptionResult);
            }

            return new FileEncryptionResult(filename, encryptionResult.getData());
        } catch (Exception e) {
            return new FileEncryptionResult(new EncryptionAbortedException("Ecryption has been aborted.", e));
        }
    }

    /**
     * This method decrypts the data from the given file with the given secret key.
     * The returned {@link FileEncryptionResult} contains whether the decryption process was successful,
     * if so the decrypted data, otherwise an error message (and for debugging purposes the stacktrace).
     * You can then (in case the encryption was successful) save the data to another file.
     *
     * @param pw the secret key used for decryption
     * @param in the file that is to be decrypted
     */
    @NotNull
    public static FileEncryptionResult decryptFile(SecretKeySpec pw, File in) {
        if (in == null || !in.isFile() || !in.exists()) {
            return new FileEncryptionResult(
                    new EncryptionAbortedException(StringProcessing.format(
                            "The file '{0}' does not exist and can thus not be decrypted.",
                            (in != null) ? in.getAbsolutePath() : "null"
                    ), null)
            );
        }

        try {
            byte[] data = CommonFileUtilities.readFile(in);

            EncryptionResult decryptionResult = decrypt(data, pw);

            if(!decryptionResult.isSuccess()) {
                return new FileEncryptionResult(decryptionResult);
            }

            data = decryptionResult.getData();

            ByteBuffer buffer = ByteBuffer.allocate(data.length);
            buffer.put(data);

            buffer.rewind();

            int nameLength = buffer.getInt();
            byte[] nameBytes = new byte[nameLength];
            buffer.get(nameBytes);

            String fileName = new String(nameBytes);

            byte[] encodedData = new byte[buffer.remaining()];
            buffer.get(encodedData);

            return new FileEncryptionResult(fileName, encodedData);
        } catch (Exception e) {
            return new FileEncryptionResult(new EncryptionAbortedException("Decryption has been aborted.", e));
        }
    }

    /**
     * The hashing result is the result of trying to create a secret key by hashing a password.
     */
    public static class HashingResult {

        /**
         * Whether the hashing process was successfl.
         */
        private boolean success;
        /**
         * The error that first occured during hashing (if there was any)
         */
        private Exception error;
        /**
         * The secret key that resulted from the hashing.
         * {@code null} if the hashing was not successful.
         */
        private SecretKeySpec password;

        /**
         * Creates a HashingResult with the given Exception as a cause to
         * abort the hashing process.
         *
         * @param error the error that aborted the execution of the hashing
         */
        private HashingResult(Exception error) {
            this.success = false;
            this.error = error;
            this.password = null;
        }

        /**
         * Creates a HashingResult that has successfully created the given secret key.
         *
         * @param pw the secret key that resulted from the hashing
         */
        private HashingResult(SecretKeySpec pw) {
            this.success = true;
            this.error = null;
            this.password = pw;
        }

        /**
         * @return Whether the hashing process terminated successfully.
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * @return THe message of the error in case there was one.
         */
        public String getErrorMessage() {
            if (error == null) {
                return null;
            }
            return error.getMessage();
        }

        /**
         * @return Gives you the exception that occurred during the hashing process.
         */
        @DebuggingPurpose
        @SuppressWarnings("unused")
        public Exception getError() {
            return error;
        }

        /**
         * @return gives you the secret key that resulted from the hashing
         */
        public SecretKeySpec getSecretKey() {
            return password;
        }
    }

    /**
     * EncryptionResult is a general result of trying to en- or decrypt data.
     */
    public static class EncryptionResult {

        /**
         * The byte data that resulted from the en- or decryption
         */
        private byte[] data;
        /**
         * Whether the en- or decryption was successful
         */
        private boolean success;
        /**
         * The error that occurred during the en- or decryption
         */
        private Exception error;

        /**
         * Creates an EncryptionResult which was unsuccessful due to the given Exception.
         *
         * @param error the Exception which aborted the de- or encryption-process
         */
        /* package-protected */ EncryptionResult(Exception error) {
            this.data = null;
            this.success = false;
            this.error = error;
        }

        /**
         * Creates an FileEncryptionResult which represents the successful en or decryption
         * of the given data, which was originally stored in the file with given name.
         *
         * @param data         the data that was originally stored in the file with given name
         */
        /* package-protected */ EncryptionResult(byte[] data) {
            this.data = data;
            this.success = true;
            this.error = null;
        }

        /**
         * @return The byte data that resulted from the en- or decryption
         */
        public byte[] getData() {
            return data;
        }

        /**
         * @return Whether the en- or decryption was successful
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * @return The message of the Exception which made the command abort
         */
        public String getErrorMessage() {
            if (error == null) {
                return null;
            }
            return error.getMessage();
        }

        /**
         * @return The Exception which made the command abort
         */
        @DebuggingPurpose
        @SuppressWarnings("unused")
        public Exception getError() {
            return error;
        }
    }

    /**
     * FileEncryptionResult is the result of trying to en- or decrypt a file.
     */
    public static class FileEncryptionResult extends EncryptionResult {

        /**
         * The name of the original file which is used for the FileNamingData
         * that can be built from an FileEncryptionResult-object
         */
        private String originalName;

        /* package-protected */ FileEncryptionResult(EncryptionResult result) {
            this(result.error);
        }

        /**
         * Creates an FileEncryptionResult which was unsuccessful due to the given Exception.
         *
         * @param error the Exception which aborted the de- or encryption-process
         */
        /* package-protected */ FileEncryptionResult(Exception error) {
            super(error);
            this.originalName = null;
        }

        /**
         * Creates an FileEncryptionResult which represents the successful en or decryption
         * of the given data, which was originally stored in the file with given name.
         *
         * @param originalName the name of the original file
         * @param data         the data that was originally stored in the file with given name
         */
        /* package-protected */ FileEncryptionResult(String originalName, byte[] data) {
            super(data);
            this.originalName = originalName;
        }

        /**
         * @return The name of the original file which
         */
        public String getOriginalName() {
            return originalName;
        }
    }
}
