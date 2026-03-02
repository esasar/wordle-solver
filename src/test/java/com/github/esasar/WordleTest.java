package com.github.esasar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WordleTest {

    @Test
    public void generateResult_withAllCorrect() {
        final var res = Wordle.generateStatus("aaaaa",
                                              "aaaaa");

        Assertions.assertEquals(0b10_10_10_10_10, res);
    }

    @Test
    public void generateResult_withAllAbsent() {
        final var res = Wordle.generateStatus("bbbbb",
                                              "aaaaa");

        Assertions.assertEquals(0b00_00_00_00_00, res);
    }

    @Test
    public void generateResult_withAllPresent() {
        final var res = Wordle.generateStatus("abcde",
                                              "eabcd");

        Assertions.assertEquals(0b01_01_01_01_01, res);
    }

    @Test
    public void generateResult_withSomePresent() {
        final var res = Wordle.generateStatus("caacc",
                                              "aabbb");

        Assertions.assertEquals(0b00_00_01_10_00, res);
    }
}
