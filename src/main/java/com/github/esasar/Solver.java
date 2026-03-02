package com.github.esasar;

import java.util.*;
import java.util.stream.Collectors;

public record Solver(Wordle wordle, Dictionary dictionary) {

    private static final int MAX_ITERATIONS = 64;
    private static final double INV_LOG2 = 1.0 / Math.log(2);

    private static final ThreadLocal<int[]> PATTERN_COUNTS = ThreadLocal.withInitial(() -> new int[1024]);
    private static final ThreadLocal<int[]> USED_PATTERNS = ThreadLocal.withInitial(() -> new int[1024]);

    public void solve() {
        var solutions = this.dictionary.dictionary();
        final var allWords = this.dictionary.dictionary();
        var attempts = 0;

        while (attempts++ < MAX_ITERATIONS) {

            final var guess = generateGuess(solutions, allWords);

            System.out.println("Guessed: " + guess);

            if (this.wordle.guess(guess)) {
                System.out.println("Solved with " + attempts + " attempts");
                break;
            }

            final var status = Wordle.generateStatus(guess, this.wordle.answer());

            final var filteredSolutions = filterSolutions(solutions, guess, status);

            // if solution set has not changed, something went wrong
            // e.g. the word is not present in the dictionary
            if (filteredSolutions.equals(solutions)) {
                System.out.println("Could not find a solution");
                break;
            }

            solutions = filteredSolutions;
        }
    }

    private String generateGuess(final Set<String> solutions,
                                 final Set<String> allWords) {
        // in the case that we only have one solution, all entropies are 0.00, so we need to short-circuit
        if (solutions.size() == 1) {
            return solutions.iterator().next();
        }
        return entropies(allWords, solutions)
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow();
    }

    private Set<String> filterSolutions(final Set<String> solutions,
                                        final String guess,
                                        final int status) {
        return solutions.parallelStream()
                .filter(s -> {
                    final var st = Wordle.generateStatus(guess, s);
                    return st == status;
                })
                .collect(Collectors.toSet());
    }

    private Map<String, Double> entropies(final Set<String> candidates,
                                          final Set<String> solutions) {
        return candidates.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        c -> c,
                        c -> entropy(c, solutions)
                ));
    }

    private double entropy(final String guess,
                           final Set<String> solutions) {
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

        final var total = solutions.size();
        var entropy = 0.0;

        for (int i = 0; i < usedCount; i++) {
            final int count = counts[usedPatterns[i]];
            final double p = (double) count / total;
            entropy -= p * Math.log(p) * INV_LOG2;
            counts[usedPatterns[i]] = 0;
        }

        return entropy;
    }
}
