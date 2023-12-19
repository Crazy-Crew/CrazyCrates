package com.badbones69.crazycrates.common.utils;

import java.util.List;

public class StringUtils {

    /**
     * Loops through a string-list and parses the colors then returns a string builder
     *
     * @param list to convert
     * @return the string-builder
     */
    public static String convertList(List<String> list) {
        StringBuilder message = new StringBuilder();

        for (String line : list) {
            message.append(line).append("\n");
        }

        return message.toString();
    }
}