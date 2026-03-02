package com.github.esasar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public record Dictionary(Set<String> dictionary) {

    public static Dictionary fromPath(final Path path) {
        return new Dictionary(parse(path));
    }

    private static HashSet<String> parse(final Path path) {
        var result = new HashSet<String>();

        try (final var lines = Files.lines(path)) {
            lines.forEach(result::add);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
