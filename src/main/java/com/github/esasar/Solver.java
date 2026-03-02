package com.github.esasar;

import java.util.*;
import java.util.stream.Collectors;

public record Solver(Wordle wordle, Dictionary dictionary) {

    private static final int MAX_ITERATIONS = 64;

    public void solve() {
        var solutions = this.dictionary.dictionary();
        final var allWords = this.dictionary.dictionary();
        var attempts = 0;

        while (attempts++ < MAX_ITERATIONS) {

            final var guess = generateGuess(solutions, allWords);

            System.out.println("Guessed: " + guess);

            if (this.wordle.guess(guess)) {
                break;
            }

            final var status = Wordle.generateStatus(guess,
                                                     this.wordle.answer());

            solutions = filterSolutions(solutions,
                                        guess,
                                        status);
        }

        System.out.println("Solved with " + attempts + " attempts");
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
                                        final Status[] status) {
        return solutions.parallelStream()
                .filter(s -> {
                    final var st = Wordle.generateStatus(guess, s);
                    return Arrays.equals(st, status);
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
        final var patternCounts = new HashMap<String, Integer>();

        for (final var solution : solutions) {
            final var status = Wordle.generateStatus(guess,
                                                     solution);

            final var key = createStatusKey(status);
            patternCounts.merge(key, 1, Integer::sum);
        }

        final var total = solutions.size();

        var entropy = 0.0;
        for (final int count : patternCounts.values()) {
            final double p = (double) count / total;
            entropy -= p * (Math.log(p) / Math.log(2));
        }

        return entropy;
    }

    private String createStatusKey(final Status[] status) {
        final var sb = new StringBuilder(5);
        for (final var s : status) {
            sb.append(s.key);
        }
        return sb.toString();
    }
}
