package ru.liga.tools;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;

public class Parser {
    static final Integer ANALYZE_CREATE_FILE = 0;
    static final Integer ANALYZE = 1;
    static final Integer CHANGE = 2;

    private Integer operationType = null;
    private String fileFullName = null;
    private Integer coefTrans = null;
    private Integer coefTempo = null;

    public Parser(String[] args) throws Exception {
        if (args.length > 0) {
            fileFullName = args[0];
        } else {
            throw new Exception("Incorrect input!");
        }

        if (args.length == 3 && args[1].equals("analyze") && args[2].equals("-f")) {
            operationType = ANALYZE_CREATE_FILE;
            return;
        }
        if (args.length == 2 && args[1].equals("analyze")) {
            operationType = ANALYZE;
            return;
        }
        if (args.length == 6 && args[1].equals("change") &&
                args[2].equals("-trans") && args[4].equals("-tempo") &&
                isInteger(args[3]) && isInteger(args[5])) {
            operationType = CHANGE;
            coefTrans = Integer.parseInt(args[3]);
            coefTempo = Integer.parseInt(args[5]);
            return;
        }
        throw new Exception("Incorrect input!");
    }

    Integer getOperationType() {
        return operationType;
    }
    public String getFileFullName() {
        return fileFullName;
    }
    String getFileName() {
        int index = fileFullName.indexOf(".");
        if (index == -1) {
            return fileFullName;
        }
        return fileFullName.substring(0, index);
    }
    Integer getCoefTrans() {
        return coefTrans;
    }
    Integer getCoefTempo() {
        return coefTempo;
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
}
