package gui;

import java.util.Random;

public class Randomizer {
    private Random random;

    public Randomizer() {
        random = new Random();
    }

    @SafeVarargs
    public final <T> T pick(T... list) {
        return list[random.nextInt(list.length)];
    }
}
