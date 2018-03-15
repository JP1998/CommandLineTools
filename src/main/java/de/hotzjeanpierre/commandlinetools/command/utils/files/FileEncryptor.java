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
import de.hotzjeanpierre.commandlinetools.command.impl.exceptions.FileCouldNotBeEncryptedException;
import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * This class can be used to en- or decrypt single files and save the
 * encrypted data to a specified file. The name of the original file
 * is also saved within the encrypted data, whereas the name is restored
 * as soon as its decrypted again.
 */
public class FileEncryptor {

    /**
     * This method reads all the byte-data from the given file.
     *
     * @param file the file to read from.
     * @return the data contained in the given file.
     * @throws IOException in case an error occurs during reading (e.g. the file doesn't exist)
     */
    @NotNull
    public static byte[] readFile(@NotNull File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * This method writes the given data to the given file.
     * Any missing parent folders of the file will not be created, whereas this method
     * will throw an exception in case there are any folders missing.
     *
     * @param file the path to the file to which the data is to be saved
     * @param data the data that is to be saved to the given file
     * @throws IOException in case an error occurs during writing (e.g. the folder this file lies in does not exist)
     */
    public static void writeFile(@NotNull File file, @NotNull byte[] data) throws IOException {
        Files.write(file.toPath(), data);
    }

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
            return new HashingResult(e);
        }
    }

    /**
     * This method encrypts the given byte data with the given secret key.
     *
     * @param data the data to encrypt
     * @param pw   the secret key to use for encryption
     * @return the encrypted data
     * @throws NoSuchPaddingException in case there is an error while encrypting
     */
    @NotNull
    /* package-protected */ static byte[] encrypt(byte[] data, SecretKeySpec pw)
            throws GeneralSecurityException {

        Cipher aes = Cipher.getInstance("AES");
        aes.init(Cipher.ENCRYPT_MODE, pw);

        return aes.doFinal(data);
    }

    /**
     * This method decrypts the given data with the given secret key.
     *
     * @param data the data to decrypt
     * @param pw   the secret key to use for decryption
     * @return the decrypted data
     * @throws GeneralSecurityException in case there is an error while decrypting; most likely due to a wrong password
     */
    @NotNull
    /* package-protected */ static byte[] decrypt(byte[] data, SecretKeySpec pw)
            throws GeneralSecurityException {

        Cipher aes = Cipher.getInstance("AES");
        aes.init(Cipher.DECRYPT_MODE, pw);

        return aes.doFinal(data);
    }

    /**
     * This method encrypts the data from the given file with the given secret key.
     * The returned {@link EncryptionResult} contains whether the encryption process was successful,
     * if so the encrypted data, otherwise an error message (and for debugging purposes the stacktrace).
     * You can then (in case the encryption was successful) save the data to another file
     * by calling {@link FileEncryptor#writeFile(File, byte[])}.
     *
     * @param pw the secret key used for encryption
     * @param in the file that is to be encrypted
     */
    @NotNull
    public static EncryptionResult encryptFile(SecretKeySpec pw, File in, File relativeTo) {
        if (in == null || !in.isFile() || !in.exists()) {
            return new EncryptionResult(
                    new FileCouldNotBeEncryptedException(StringProcessing.format(
                            "The file '{0}' does not exist and can thus not be encrypted.",
                            (in != null) ? in.getAbsolutePath() : "null"
                    ))
            );
        }

        try {
            byte[] data = readFile(in);

            String filename = in.getAbsolutePath().substring(
                    relativeTo.getAbsolutePath().length(),
                    in.getAbsolutePath().length()
            );

            System.out.println(filename);

            byte[] filenameBytes = filename.getBytes();

            ByteBuffer buffer = ByteBuffer.allocate(4 + filenameBytes.length + data.length);

            buffer.putInt(filenameBytes.length);
            buffer.put(filenameBytes);
            buffer.put(data);

            return new EncryptionResult(filename, encrypt(buffer.array(), pw));
        } catch (Exception e) {
            return new EncryptionResult(e);
        }
    }

    /**
     * This method decrypts the data from the given file with the given secret key.
     * The returned {@link EncryptionResult} contains whether the decryption process was successful,
     * if so the decrypted data, otherwise an error message (and for debugging purposes the stacktrace).
     * You can then (in case the encryption was successful) save the data to another file
     * by calling {@link FileEncryptor#writeFile(File, byte[])}.
     *
     * @param pw the secret key used for decryption
     * @param in the file that is to be decrypted
     */
    @NotNull
    public static EncryptionResult decryptFile(SecretKeySpec pw, File in) {
        if (in == null || !in.isFile() || !in.exists()) {
            return new EncryptionResult(
                    new FileCouldNotBeEncryptedException(StringProcessing.format(
                            "The file '{0}' does not exist and can thus not be decrypted.",
                            (in != null) ? in.getAbsolutePath() : "null"
                    ))
            );
        }

        try {
            byte[] data = readFile(in);

            data = decrypt(data, pw);

            ByteBuffer buffer = ByteBuffer.allocate(data.length);
            buffer.put(data);

            buffer.rewind();

            int nameLength = buffer.getInt();
            byte[] nameBytes = new byte[nameLength];
            buffer.get(nameBytes);

            String fileName = new String(nameBytes);

            byte[] encodedData = new byte[buffer.remaining()];
            buffer.get(encodedData);

            return new EncryptionResult(fileName, encodedData);
        } catch (Exception e) {
            return new EncryptionResult(e);
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
     * The encryption result is the result of trying to en- or decrypt a file.
     */
    public static class EncryptionResult {

        /**
         * The name of the original file which is used for the FileNamingData
         * that can be built from an EncryptionResult-object
         */
        private String originalName;
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
         * Creates an EncryptionResult which was unsuccessful due to the gien Exception.
         *
         * @param error the Exception which aborted the de- or encryption
         */
        /* package-protected */ EncryptionResult(Exception error) {
            this.originalName = null;
            this.data = null;
            this.success = false;
            this.error = error;
        }

        /**
         * Creates an EncryptionResult which represents the successful en or decryption
         * of the given data, which was originally stored in the file with given name.
         *
         * @param originalName the name of the original file
         * @param data         the data that was originally stored in the file with given name
         */
        /* package-protected */ EncryptionResult(String originalName, byte[] data) {
            this.originalName = originalName;
            this.data = data;
            this.success = true;
            this.error = null;
        }

        /**
         * @return The name of the original file which
         */
        public String getOriginalName() {
            return originalName;
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
}
