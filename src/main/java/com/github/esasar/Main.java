package com.github.esasar;

import java.nio.file.Path;
import java.util.function.BiFunction;

public class Main {
    private static final Path DICTIONARY_PATH = Path.of("src/main/resources/dictionary.txt");
    private static final Path FIN_DICTIONARY_PATH = Path.of("src/main/resources/fin_dictionary.txt");

    static void main() {
        benchmark(Main::solve);
    }

    /**
     * Benchmark:
     * - Naive: 24155,615ms
     * -- {@link Wordle#generateStatus(String, String)} takes ~48.7% of computation time
     * -- {@link java.util.HashMap#merge(Object, Object, BiFunction)} takes ~22.9% of computation time
     * - ParallelStream: 3382,412 ms
     */
    private static void solve() {
        final var dictionary = Dictionary.fromPath(FIN_DICTIONARY_PATH);
        // examples
        //  ["tares", "cloam", "spunk", "whyda", "abyss"]
        //  ["tares", "broil", "micra", "cigar"]
        final var wordle = new Wordle("silmu");
        final var solver = new Solver(wordle, dictionary);
        solver.solve();
    }

    private static void benchmark(final Runnable runnable) {
        final var start = System.nanoTime();
        runnable.run();
        final var elapsed = System.nanoTime() - start;
        System.out.printf("Took %.3f ms%n", elapsed / 1_000_000.0);
    }
}
