package com.github.esasar;

import com.github.esasar.Status;
import com.github.esasar.Wordle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WordleTest {

    @Test
    public void generateResult_withAllCorrect() {
        final var res = Wordle.generateStatus("aaaaa",
                                              "aaaaa");

        Assertions.assertArrayEquals(new Status[] {
                Status.CORRECT,
                Status.CORRECT,
                Status.CORRECT,
                Status.CORRECT,
                Status.CORRECT
        }, res);
    }

    @Test
    public void generateResult_withAllAbsent() {
        final var res = Wordle.generateStatus("bbbbb",
                                              "aaaaa");

        Assertions.assertArrayEquals(new Status[] {
                Status.ABSENT,
                Status.ABSENT,
                Status.ABSENT,
                Status.ABSENT,
                Status.ABSENT
        }, res);
    }

    @Test
    public void generateResult_withAllPresent() {
        final var res = Wordle.generateStatus("bbaaa",
                                              "aabbb");

        Assertions.assertArrayEquals(new Status[] {
                Status.PRESENT,
                Status.PRESENT,
                Status.PRESENT,
                Status.PRESENT,
                Status.PRESENT
        }, res);
    }

    @Test
    public void generateResult_withSomePresent() {
        final var res = Wordle.generateStatus("caacc",
                                              "aabbb");

        Assertions.assertArrayEquals(new Status[] {
                Status.ABSENT,
                Status.CORRECT,
                Status.PRESENT,
                Status.ABSENT,
                Status.ABSENT
        }, res);
    }
}
