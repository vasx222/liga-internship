package ru.liga;

public class Parser {
    public static final Integer ANALYZE_CREATE_FILE = 0;
    public static final Integer ANALYZE = 1;
    public static final Integer CHANGE = 2;

    static Integer parseArgs(String[] args) {
        if (args.length == 3 && args[1].equals("analyze") && args[2].equals("-f")) {
            return ANALYZE_CREATE_FILE;
        }
        if (args.length == 2 && args[1].equals("analyze")) {
            return ANALYZE;
        }
        if (args.length == 6 && args[1].equals("change") &&
                args[2].equals("-trans") && args[4].equals("-tempo") &&
                isInteger(args[3]) && isInteger(args[5])) {
            return CHANGE;
        }
        return null;
    }
    private static boolean isInteger(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!('0' <= c && c <= '9')) {
                return false;
            }
        }
        return true;
    }
    static String getName(String fullName) {
        int index = fullName.indexOf(".");
        if (index == -1) {
            return fullName;
        }
        return fullName.substring(0, index);
    }
}
