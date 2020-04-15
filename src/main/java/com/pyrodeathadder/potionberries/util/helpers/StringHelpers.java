package com.pyrodeathadder.potionberries.util.helpers;

import java.util.stream.Stream;

public class StringHelpers {
    public static final String titleCaseConversion(String inputString, String separator)
    {
        if (inputString == "") {
            return "";
        }

        if (inputString.length() == 1) {
            return inputString.toUpperCase();
        }

        StringBuffer resultPlaceHolder = new StringBuffer(inputString.length());

        Stream.of(inputString.split(separator)).forEach(stringPart -> {
            char[] charArray = stringPart.toLowerCase().toCharArray();
            charArray[0] = Character.toUpperCase(charArray[0]);
            resultPlaceHolder.append(new String(charArray)).append(" ");
        });

        return resultPlaceHolder.toString();
    }

    public static final String timeChanger(int timeIn) {
        final int ticks;
        int seconds = timeIn / 20;
        int minutes = (int)Math.floor(seconds / 60);
        seconds = seconds - (minutes * 60);
        return (minutes + ":" + padLeftZeros(Integer.toString(seconds),2));
    }

    public static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }

}
