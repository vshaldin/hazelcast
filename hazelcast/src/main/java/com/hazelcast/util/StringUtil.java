/*
 * Copyright (c) 2008-2017, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.util;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.isLetter;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.toLowerCase;

/**
 * Utility class for Strings.
 */
public final class StringUtil {

    /**
     * UTF-8 Charset
     */
    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    /**
     * Points to the System property 'line.separator'.
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * LOCALE_INTERNAL is the default locale for string operations and number formatting. Initialized to
     * {@code java.util.Locale.US} (US English).
     */
    //TODO Use java.util.Locale#ROOT value (language neutral) in Hazelcast 4
    public static final Locale LOCALE_INTERNAL = Locale.US;

    /**
     * Pattern used to tokenize version strings.
     */
    private static final Pattern VERSION_PATTERN
            = Pattern.compile("^([\\d]+)\\.([\\d]+)(\\.([\\d]+))?(-[\\w]+)?(-SNAPSHOT)?$");

    private static final String GETTER_PREFIX = "get";

    private StringUtil() {
    }

    /**
     * Creates a UTF8_CHARSET string from a byte array.
     *
     * @param bytes  the byte array.
     * @param offset the index of the first byte to decode
     * @param length the number of bytes to decode
     * @return the string created from the byte array.
     */
    public static String bytesToString(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, UTF8_CHARSET);
    }

    /**
     * Creates a UTF8_CHARSET string from a byte array.
     *
     * @param bytes the byte array.
     * @return the string created from the byte array.
     */
    public static String bytesToString(byte[] bytes) {

        return new String(bytes, UTF8_CHARSET);
    }

    /**
     * Creates a byte array from a string.
     *
     * @param s the string.
     * @return the byte array created from the string.
     */
    public static byte[] stringToBytes(String s) {
        return s.getBytes(UTF8_CHARSET);
    }

    /**
     * Checks if a string is empty or not.
     *
     * @param s the string to check.
     * @return true if the string is null or empty, false otherwise
     */

    public static boolean isNullOrEmpty(String s) {
        if (s == null) {
            return true;
        }
        return s.isEmpty();
    }

    /**
     * Checks if a string is empty or not after trim operation
     *
     * @param s the string to check.
     * @return true if the string is null or empty, false otherwise
     */

    public static boolean isNullOrEmptyAfterTrim(String s) {
        if (s == null) {
            return true;
        }
        return s.trim().isEmpty();
    }

    /**
     * HC specific settings, operands etc. use this method.
     * Creates an uppercase string from the given string.
     *
     * @param s the given string
     * @return an uppercase string, or null/empty if the string is null/empty
     */
    public static String upperCaseInternal(String s) {
        if (isNullOrEmpty(s)) {
            return s;
        }
        return s.toUpperCase(LOCALE_INTERNAL);
    }

    /**
     * HC specific settings, operands etc. use this method.
     * Creates a lowercase string from the given string.
     *
     * @param s the given string
     * @return a lowercase string, or null/empty if the string is null/empty
     */
    public static String lowerCaseInternal(String s) {
        if (isNullOrEmpty(s)) {
            return s;
        }
        return s.toLowerCase(LOCALE_INTERNAL);
    }

    /**
     * Returns a String representation of the time.
     * <p>
     * This method is not particularly efficient since it generates a ton of litter.
     *
     * @param timeMillis time in millis
     * @return the String
     */
    public static String timeToString(long timeMillis) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(new Date(timeMillis));
    }

    /**
     * Returns a String representation of the time. If time is 0, then 'never' is returned.
     * <p>
     * This method is not particularly efficient since it generates a ton of litter.
     *
     * @param timeMillis time in millis
     * @return the String
     */
    public static String timeToStringFriendly(long timeMillis) {
        return timeMillis == 0 ? "never" : timeToString(timeMillis);
    }

    /**
     * Like a String.indexOf but without MIN_SUPPLEMENTARY_CODE_POINT handling
     *
     * @param input  to check the indexOf on
     * @param ch     character to find the index of
     * @param offset offset to start the reading from
     * @return index of the character, or -1 if not found
     */
    public static int indexOf(String input, char ch, int offset) {
        for (int i = offset; i < input.length(); i++) {
            if (input.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Like a String.indexOf but without MIN_SUPPLEMENTARY_CODE_POINT handling
     *
     * @param input to check the indexOf on
     * @param ch    character to find the index of
     * @return index of the character, or -1 if not found
     */
    public static int indexOf(String input, char ch) {
        return indexOf(input, ch, 0);
    }

    /**
     * Like a String.lastIndexOf but without MIN_SUPPLEMENTARY_CODE_POINT handling
     *
     * @param input  to check the indexOf on
     * @param ch     character to find the index of
     * @param offset offset to start the reading from the end
     * @return index of the character, or -1 if not found
     */
    public static int lastIndexOf(String input, char ch, int offset) {
        for (int i = input.length() - 1 - offset; i >= 0; i--) {
            if (input.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Like a String.lastIndexOf but without MIN_SUPPLEMENTARY_CODE_POINT handling
     *
     * @param input to check the indexOf on
     * @param ch    character to find the index of
     * @return index of the character, or -1 if not found
     */
    public static int lastIndexOf(String input, char ch) {
        return lastIndexOf(input, ch, 0);
    }

    /**
     * Tokenizes a version string and returns the tokens with the following grouping:
     * (1) major version, eg "3"
     * (2) minor version, eg "8"
     * (3) patch version prefixed with ".", if exists, otherwise null (eg ".0")
     * (4) patch version, eg "0"
     * (5) 1st -qualifier, if exists
     * (6) -SNAPSHOT qualifier, if exists
     * @param version
     * @return
     */
    public static String[] tokenizeVersionString(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            String[] tokens = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                tokens[i] = matcher.group(i + 1);
            }
            return tokens;
        } else {
            return null;
        }
    }

    /**
     * Convert getter into a property name
     * Example: 'getFoo' is converted into 'foo'
     *
     * It's written defensively, when output is not a getter then it
     * return the original name.
     *
     * It converters name starting with the get- prefix only. When a getter
     * starts with is- prefix (=boolean) then it does not convert it.
     *
     * @param getterName
     * @return property matching the given getter
     */
    public static String getterIntoProperty(String getterName) {
        if (getterName == null) {
            return getterName;
        }
        int length = getterName.length();
        if (!getterName.startsWith(GETTER_PREFIX) || length <= GETTER_PREFIX.length()) {
            return getterName;
        }

        String propertyName = getterName.substring(GETTER_PREFIX.length(), length);
        char firstChar = propertyName.charAt(0);
        if (isLetter(firstChar)) {
            if (isLowerCase(firstChar)) {
                //ok, apparently this is not a JavaBean getter, better leave it untouched
                return getterName;
            }
            propertyName = toLowerCase(firstChar) + propertyName.substring(1, propertyName.length());
        }
        return propertyName;
    }
}
