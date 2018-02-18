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

import de.hotzjeanpierre.commandlinetools.command.utils.StringProcessing;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class may be used to easily name a file. It has useful utilities like
 * wildcards for an index padded to at least a specific length (useful for bulk-naming files),
 * wildcards for the extension or the name of the original file (when renaming files),
 * or even the sub folder structure of the original file.
 * The following syntax is expected for file templates that can be created through the method
 * {@link FileNamingTemplate#parse(String)}:
 * <ul>
 *     <li>
 *         any valid character may be used for the files structure, whereas the characters ':', '*',
 *         '?', '"', '&lt;', '&gt;' and '|' are forbidden.
 *         While '\' and '/' are also forbidden for file <b>names</b> they are still allowed to allow
 *         creation of a folder structure within the template.
 *     </li>
 *     <li>
 *         the characters '{' and '}' must be escaped with a second one of the character, since
 *         they are used to delimit wildcards in the template
 *     </li>
 *     <li>
 *         wildcards are delimited by '{' and  '}'; they are not case-sensitive and a parameter
 *         (of which a wildcard may only have one) will be separated from the wildcards name with a colon.
 *         In case a wildcard has a parameter, it will also provide a default value for said parameter.
 *     </li>
 * </ul>
 * With the given syntax and the following wildcards you should be able to produce any template you
 * would like to make.
 * <table>
 *     <thead>
 *         <tr>
 *             <th>Name</th>
 *             <th>Parameter Type</th>
 *             <th>Parameter default value</th>
 *             <th>Description</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>OriginalName</td>
 *             <td>None</td>
 *             <td>None</td>
 *             <td>Represents the file name of the original file without any folders or extension.</td>
 *         </tr>
 *         <tr>
 *             <td>Index</td>
 *             <td>Int</td>
 *             <td>0</td>
 *             <td>Represents an index of the processed file. May be used in bulk renaming of files. The given parameter therefor represents the minimum length of the index; will be zero-padded</td>
 *         </tr>
 *         <tr>
 *             <td>Extension</td>
 *             <td>None</td>
 *             <td>None</td>
 *             <td>Represents the extension of the original file.</td>
 *         </tr>
 *         <tr>
 *             <td>OriginalLocation</td>
 *             <td>None</td>
 *             <td>None</td>
 *             <td>Represents the subfolder structure of the original file.</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class FileNamingTemplate {

    /**
     * The tokens that represent the FileNamingTemplate.
     */
    private Token[] template;

    /**
     * This constructor creates an immutable FileNamingTemplate with the given Tokens.
     *
     * @param templateTokens the Tokens that represent the template
     */
    /* package-protected */ FileNamingTemplate(Token[] templateTokens) {
        this.template = templateTokens;
    }

    /**
     * This method produces a file name according to the file naming template and the given data.
     *
     * @param data the data to insert into the wildcards
     * @return the file name that has been produced
     */
    public String produceFileNane(FileNamingData data) {
        StringBuilder result = new StringBuilder();

        for (Token currrentToken : template) {
            result.append(currrentToken.getRepresentation(data));
        }

        return result.toString();
    }

    /**
     * The Pattern that checks a string whether it is a valid template or not.
     * <p>
     * <pre><code>
     * ^
     * (
     *     [^{}/\\:\*\?\"<>\|]*
     *     (
     *         \{\{                             |
     *         }}                               |
     *         {\s*originalname\s*}             |
     *         {\s*index\s*(:\s*[0-9]+\s*)?}    |
     *         {\s*extension\s*}                |
     *         {\s*originallocation\s*}
     *     )
     * )*
     * [^{}/\\:\*\?\"<>\|]*
     * $
     * </code></pre>
     */
    private static final Pattern TEMPLATE_VALIDITY_PATTERN = Pattern.compile(
            "^([^{}:*?\"<>|]*(\\{\\{|}}|\\{\\s*originalname\\s*}|\\{\\s*index\\s*(:\\s*[0-9]+\\s*)?}|\\{\\s*extension\\s*}|\\{\\s*originallocation\\s*}))*[^{}:*?\"<>|]*$"
    );
    /**
     * The pattern that is used to extract the single groups from the template string.
     * You'll have to watch for fragmented text groups, which may occur.
     * <p>
     * <pre><code>
     * (
     *     [^{}/\\:*?"<>|]+              |
     *     \{\{                          |
     *     }}                            |
     *     \{\s*originalname\s*}         |
     *     \{\s*index\s*:\s*[0-9]+\s*}   |
     *     \{\s*extension\s*}            |
     *     \{\s*originallocation\s*}
     * )
     * </code></pre>
     */
    private static final Pattern TEMPLATE_GROUPTEXTRACTOR_PATTERN = Pattern.compile(
            "([^{}:*?\"<>|]+|\\{\\{|}}|\\{\\s*originalname\\s*}|\\{\\s*index\\s*(:\\s*[0-9]+\\s*)?}|\\{\\s*extension\\s*}|\\{\\s*originallocation\\s*})"
    );

    /**
     * This method parses a given String into a FileNamingTemplate.<br>
     * A FileNamingTemplate may consist of any character except ':', '*',
     * '?', '"', '&lt;', '&gt;' and '|'. The characters '/' and '\' represent
     * a folder sequence respectively. Thus the character sequence '//' or '/\' might not
     * be invalidated by this method (i.e. it is valid syntax for the FileNamingTemplate),
     * but it will eventually cause you problems when trying to work with a file name
     * produced by the template with said character sequence.<br>
     * Also to note is that if you want to use the characters '{' or '}' in
     * your template you'll have to escape said character with a second one of it.<br>
     * Thus in case you want to write "{asdf}" in your template you will have to write
     * "{{asdf}}" into the template itself.<br>
     * This comes from the denomination of wildcards in the template. The wildcards are always
     * separated from normal strings by '{' and '}'. The wildcards names are always case-insensitive,
     * and Parameters are given after the wildcards name separarted with a colon.<br>
     * For a list of all the allowed wildcards you can look at the table shown in the
     * description of the class {@link FileNamingTemplate}.
     *
     * @param template the String that contains the template in valid syntax
     * @return the FileNamingTemplate parsed from given string
     */
    @NotNull
    public static FileNamingTemplate parse(String template) {
        // check for valid syntax with the regular expression
        if (!TEMPLATE_VALIDITY_PATTERN.matcher(template).matches()) {
            throw new IllegalArgumentException(
                    StringProcessing.format(
                            "The file naming template '{0}' is either invalid or does not produce valid file names.",
                            template
                    )
            );
        }

        // extract the single groups
        Matcher templategroups = TEMPLATE_GROUPTEXTRACTOR_PATTERN.matcher(template);
        List<Token> resultTokens = new ArrayList<>();

        StringBuilder currentTextToken = new StringBuilder();

        while (templategroups.find()) {
            String currentGroup = templategroups.group();

            // parse the single tokens and (if needed) add a text token
            if (isToken(currentGroup)) {
                if (currentTextToken.length() != 0) {
                    resultTokens.add(new TextToken(currentTextToken.toString()));
                    currentTextToken = new StringBuilder();
                }

                resultTokens.add(resolveToken(currentGroup));
            } else {
                // append the text if there is no token in the next group
                currentTextToken.append(resolveText(currentGroup));
            }
        }

        // in case the last bit of text has not been added yet, we'll simply
        // add it too as a text token
        if (currentTextToken.length() != 0) {
            resultTokens.add(new TextToken(currentTextToken.toString()));
        }

        // create an array of the needed length and copy the list into said array
        Token[] resultTokenArray = new Token[resultTokens.size()];
        // finally create and return the FileNamingTEmplate with the parsed tokens
        return new FileNamingTemplate(resultTokens.toArray(resultTokenArray));
    }

    /**
     * This method determines whetheer a given group is a token or not
     *
     * @param toTest the group to test
     * @return whether given group is a token or not
     */
    private static boolean isToken(@NotNull String toTest) {
        return (toTest.startsWith("{") || toTest.startsWith("}")) && !(toTest.startsWith("{{") || toTest.startsWith("}}"));
    }

    /**
     * This method parses a given group into a token. It does not parse any text,
     * but onky valid groups that represent a token after {@link FileNamingTemplate#TEMPLATE_GROUPTEXTRACTOR_PATTERN}
     * and {@link FileNamingTemplate#isToken(String)}.
     *
     * @param representation the group (which represents a token) that is to be parsed
     * @return the token represented by the given group
     */
    @NotNull
    private static Token resolveToken(@NotNull String representation) {
        int end = representation.lastIndexOf(':');
        boolean parameterfound = end != -1;
        String parameter;

        if (!parameterfound) {
            parameter = "";
            end = representation.length() - 1;
        } else {
            parameter = representation.substring(end + 1, representation.length() - 1).trim();
        }

        String wildcardName = representation.substring(1, end).trim();

        switch (wildcardName.toLowerCase()) {
            case "originalname":
                return new OriginalNameToken();
            case "index":
                if (!parameterfound) {
                    return new IndexToken();
                } else {
                    try {
                        return new IndexToken(Integer.parseInt(parameter));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                StringProcessing.format(
                                        "Problem with token '{0}' in token '{1}'. Parameter of index has to be an integer.",
                                        parameter, representation
                                ), e
                        );
                    }
                }
            case "extension":
                return new ExtensionToken();
            case "originallocation":
                return new OriginalLocationToken();
            default:
                throw new IllegalAccessError(
                        "You may have accessed the method FileNamingTemplate#resolveToken(String) without having access granted to said method.\nAre you using reflection to access private methods?"
                );
        }
    }

    /**
     * This method resolves the text that is represented by the given group.
     * It is assumed that any string starting with '{' or '}' equal to "{{" or "}}"
     * respectively.
     *
     * @param toResolve the text that is to eb resolved
     * @return the resolved text
     */
    private static String resolveText(@NotNull String toResolve) {
        if (toResolve.startsWith("{") || toResolve.startsWith("}")) {
            return toResolve.substring(0, 1);
        } else {
            return toResolve;
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Token t : template) {
            result.append(t.toString());
        }

        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null ||
                !(obj instanceof FileNamingTemplate) ||
                template.length != ((FileNamingTemplate) obj).template.length) {
            return false;
        }

        for (int i = 0; i < template.length; i++) {
            if(!template[i].equals(((FileNamingTemplate) obj).template[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * A base class for any kind of text that is to be taken into account within
     * a FileNamingTemplate.<br>
     * Thus any wildcard or plain text will resolve to some kind of token, which
     * is to be determined by the implementing classes of this interface.
     */
    /* package-protected */ interface Token {

        /**
         * This method is supposed to return the value this token resolves to
         * with the given data.
         *
         * @param data the data to use for creation of the representation
         * @return the representation of the token
         */
        String getRepresentation(FileNamingData data);

    }

    /**
     * This token represents the name of the original file.<br>
     * Thus any wildcard with name "originalname" will resolve
     * to an instance of this class.
     */
    /* package-protected */ static class OriginalNameToken implements Token {

        /* package-protected */ OriginalNameToken() {
        }

        @Override
        public String getRepresentation(FileNamingData data) {
            return data.getOriginalName();
        }

        @Override
        public String toString() {
            return "{originalname}";
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null &&
                    obj instanceof OriginalNameToken;
        }
    }

    /**
     * This class represents a token that resolves to the given index zero-padded
     * to a certain length provided by its parameter.<br>
     * Thus any wildcard with name "index" will resolve
     * to an instance of this class.
     */
    /* package-protected */ static class IndexToken implements Token {

        /**
         * The length the index will be padded to
         */
        private int length;

        /* package-protected */ IndexToken() {
            this(0);
        }

        /* package-protected */ IndexToken(int length) {
            this.length = length;
        }

        @Override
        public String getRepresentation(FileNamingData data) {
            return StringProcessing.zeroPadding(data.getIndex(), length);
        }

        @Override
        public String toString() {
            if (length <= 1) {
                return "{index}";
            } else {
                return StringProcessing.format("{{index:{0}}}", length);
            }
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null &&
                    obj instanceof IndexToken &&
                    length == ((IndexToken) obj).length;
        }
    }

    /**
     * This class represents a token that will resolve to the extension
     * of the original file.<br>
     * Thus any wildcard with name "extension" will resolve
     * to an instance of this class.
     */
    /* package-protected */ static class ExtensionToken implements Token {

        /* package-protected */ ExtensionToken() {
        }

        @Override
        public String getRepresentation(FileNamingData data) {
            return data.getExtension();
        }

        @Override
        public String toString() {
            return "{extension}";
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null &&
                    obj instanceof ExtensionToken;
        }
    }

    /**
     * This class represents a token that will resolve to the sub folder structure
     * the original file was in.<br>
     * Thus any wildcard with name "originallocation" will resolve
     * to an instance of this class.
     */
    /* package-protected */ static class OriginalLocationToken implements Token {

        /* package-protected */ OriginalLocationToken() {
        }

        @Override
        public String getRepresentation(FileNamingData data) {
            return data.getOriginalLocation();
        }

        @Override
        public String toString() {
            return "{originallocation}";
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null &&
                    obj instanceof OriginalLocationToken;
        }
    }

    /**
     * This class represents any plain text within a FileNamingTemplate, and
     * will resolve to the same text without regard of the given data.
     */
    /* package-protected */ static class TextToken implements Token {

        /**
         * The text the token represents
         */
        private String text;

        /* package-protected */ TextToken(String text) {
            this.text = text;
        }

        @Override
        public String getRepresentation(FileNamingData data) {
            return text;
        }

        @Override
        public String toString() {
            return text.replace("{", "{{").replace("}", "}}");
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null &&
                    obj instanceof TextToken &&
                    text.equals(((TextToken) obj).text);
        }
    }
}
