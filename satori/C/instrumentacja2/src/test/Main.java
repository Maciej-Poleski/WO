package test;

import java.util.regex.Pattern;

public class Main {

    private static Exception createEx() {
        return new Exception("Hello, World!");
    }

    private static void throwEx() throws Exception {
        Exception ex = createEx();
        throw ex;
    }

    public static void main(String[] args) throws Exception {
        try {
            throwEx();
        } catch (Exception ex) {
            throw ex;
        }
    }

}
