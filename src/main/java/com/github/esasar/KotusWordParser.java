package com.github.esasar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Nykysuomen sanalista. Kotimaisten kielten keskus. Päivitetty 30.4.2024 [viitattu 02.03.2026].
 * Saatavissa <a href="https://kaino.kotus.fi/lataa/nykysuomensanalista2024.txt">nykysuomensanalista2024.txt</a>
 */
public class KotusWordParser {
    private static final Path INPUT_PATH = Path.of("src/main/resources/nykysuomensanalista2024.txt");
    private static final Path OUTPUT_PATH = Path.of("src/main/resources/fin_dictionary.txt");

    private static final String FINNISH_ALPHABET = "[a-zäö]+";

    static void main() throws IOException {
        parse();
    }

    public static void parse() throws IOException {
        var words = new HashSet<String>();

        try (final var lines = Files.lines(INPUT_PATH)) {
            lines.forEach(line -> {
                final var parts = line.split("\t+");
                final var word = parts[0];

                if (word.length() == 5 && word.matches(FINNISH_ALPHABET)) {
                    words.add(parts[0]);
                }
            });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final var output = String.join("\n", words);

        Files.writeString(OUTPUT_PATH, output);
    }
}
