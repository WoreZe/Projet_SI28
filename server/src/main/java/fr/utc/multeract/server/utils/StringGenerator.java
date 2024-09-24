package fr.utc.multeract.server.utils;

import java.util.Arrays;
import java.util.Random;

public class StringGenerator {
    public static String generateCode() {
        String s = random(6).toUpperCase();
        return s.substring(0, 2) + "-" + s.substring(2, 4) + "-" + s.substring(4, 6);
    }

    public static String random(int length) {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
