package util;

import java.util.Random;

public class Randomizer {
    private static final Random random = new Random();

    @SafeVarargs
    public static <T> T pick(T... list) {
        return list[random.nextInt(list.length)];
    }
}
