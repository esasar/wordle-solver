package com.github.esasar;

import java.util.*;

public record Wordle(String answer) {

    public static final int CORRECT = 0b10;
    public static final int PRESENT = 0b01;

    public static final int ALL_CORRECT = 0b10_10_10_10_10;

    private static final ThreadLocal<int[]> AVAILABLE = ThreadLocal.withInitial(() -> new int[26]);

    public Wordle(final String answer) {
        this.answer = Objects.requireNonNull(answer);
    }

    public boolean guess(final String guess) {
        final var status = generateStatus(guess.getBytes(),
                                          this.answer.getBytes());

        return status == ALL_CORRECT;
    }

    public static int generateStatus(final byte[] guess,
                                     final byte[] answer) {
        if (guess.length != 5 || answer.length != 5) {
            throw new IllegalArgumentException("Guess and answer must have length 5");
        }

        var status = 0;
        // array of available letters
        // e.g. available[0] = 1 means one 'a' is available.
        final var available = AVAILABLE.get();
        Arrays.fill(available, 0);

        // mark correct
        for (int i = 0; i < 5; i++) {
            final var g = guess[i];
            final var a = answer[i];
            if (a == g) {
                status |= (CORRECT << (i * 2));
            } else {
                available[a - 'a']++;
            }
        }

        // mark correct, but incorrect place
        for (int i = 0; i < 5; i++) {
            final var g = guess[i];
            if ((status >> (i * 2) & 0b11) == 0 && available[g - 'a'] > 0) {
                status |= (PRESENT << (i * 2));
                available[g - 'a']--;
            }
        }

        return status;
    }
}
