package com.github.esasar;

import java.util.*;

public record Wordle(String answer) {

    public Wordle(final String answer) {
        this.answer = Objects.requireNonNull(answer);
    }

    public boolean guess(final String guess) {
        final var status = generateStatus(guess,
                                          this.answer);

        return Arrays.stream(status).allMatch(Status.CORRECT::equals);
    }

    public static Status[] generateStatus(final String guess,
                                          final String answer) {
        if (guess.length() != 5 || answer.length() != 5) {
            throw new IllegalArgumentException("Guess and answer must have length 5");
        }

        var status = new Status[5];
        Arrays.fill(status, Status.ABSENT);
        var used = new boolean[5];

        // mark correct
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                status[i] = Status.CORRECT;
                used[i] = true;
            }
        }

        // mark correct, but incorrect place
        for (int i = 0; i < guess.length(); i++) {
            if (Status.CORRECT.equals(status[i])) {
                continue;
            }

            for (int j = 0; j < answer.length(); j++) {
                if (answer.charAt(j) == guess.charAt(i) && !used[j]) {
                    used[j] = true;
                    status[i] = Status.PRESENT;
                    break;
                }
            }
        }

        return status;
    }
}
