package za.ac.cput.util;

import java.util.UUID;

public class Helper {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
