package edu.duke.ece651.risk.client;

import java.util.regex.Pattern;

public class Format {
    /**
     * regular expression to check is number or not¢
     */
    private static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
}

