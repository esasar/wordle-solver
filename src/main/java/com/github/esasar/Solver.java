package com.github.esasar;

import java.util.*;

public record Solver(Wordle wordle, Dictionary dictionary) {

    private static final int MAX_ITERATIONS = 64;
    private static final double INV_LOG2 = 1.0 / Math.log(2);

    private static final ThreadLocal<int[]> PATTERN_COUNTS = ThreadLocal.withInitial(() -> new int[1024]);
    private static final ThreadLocal<int[]> USED_PATTERNS = ThreadLocal.withInitial(() -> new int[1024]);

    public void solve() {
        final var allWords = toByteArrays(this.dictionary.dictionary());
        var solutions = allWords;
        var attempts = 0;

        while (attempts++ < MAX_ITERATIONS) {

            final var guess = generateGuess(solutions, allWords);
            final var guessString = new String(guess);
            System.out.println("Guessed: " + guessString);

            if (this.wordle.guess(guessString)) {
                System.out.println("Solved with " + attempts + " attempts");
                break;
            }

            final var status = Wordle.generateStatus(guess, this.wordle.answer().getBytes());

            final var filteredSolutions = filterSolutions(solutions, guess, status);

            // if solution set has not changed, something went wrong
            // e.g. the word is not present in the dictionary
            if (Arrays.deepEquals(filteredSolutions, solutions)) {
                System.out.println("Could not find a solution");
                break;
            }

            solutions = filteredSolutions;
        }
    }

    private static byte[][] toByteArrays(final Set<String> words) {
        final var result = new byte[words.size()][5];
        int i = 0;
        for (final var word : words) {
            result[i++] = word.getBytes();
        }
        return result;
    }

    private byte[] generateGuess(final byte[][] solutions,
                                 final byte[][] allWords) {
        // in the case that we only have one solution, all entropies are 0.00, so we need to short-circuit
        if (solutions.length == 1) {
            return solutions[0];
        }

        return Arrays.stream(allWords)
                .parallel()
                .map(w -> new Candidate(w, entropy(w, solutions)))
                .reduce((a, b) -> a.score() >= b.score() ? a : b)
                .map(Candidate::word)
                .orElseThrow();
    }

    private byte[][] filterSolutions(final byte[][] solutions,
                                     final byte[] guess,
                                     final int status) {
        return Arrays.stream(solutions)
                .parallel()
                .filter(s -> Wordle.generateStatus(guess, s) == status)
                .toArray(byte[][]::new);
    }

    private double entropy(final byte[] guess,
                           final byte[][] solutions) {
        // array of pattern counts
        // e.g. pattern counts[0b10] represents the count for pattern [PRESENT, ABSENT, ABSENT, ABSENT, ABSENT]
        final var counts = PATTERN_COUNTS.get();
        // tracks used patterns, so we need not iterate entire counts
        final var usedPatterns = USED_PATTERNS.get();
        var usedCount = 0;

        for (final var solution : solutions) {
            final var status = Wordle.generateStatus(guess, solution);
            if (counts[status] == 0) {
                usedPatterns[usedCount++] = status;
            }
            counts[status]++;
        }

        final var total = solutions.length;
        var entropy = 0.0;

        for (int i = 0; i < usedCount; i++) {
            final int count = counts[usedPatterns[i]];
            final double p = (double) count / total;
            entropy -= p * Math.log(p) * INV_LOG2;
            counts[usedPatterns[i]] = 0;
        }

        return entropy;
    }

    private record Candidate(byte[] word, double score) {}
}
