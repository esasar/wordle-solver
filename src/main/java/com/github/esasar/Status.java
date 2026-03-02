package com.github.esasar;

public enum Status {
    CORRECT('C'),
    PRESENT('P'),
    ABSENT('A');

    public final char key;

    Status(final char key) {
        this.key = key;
    }

    public static Status fromChar(final char c) {
        return switch (c) {
            case 'C' -> CORRECT;
            case 'P' -> PRESENT;
            case 'A' -> ABSENT;
            default -> throw new IllegalArgumentException("Invalid char " + c);
        };
    }
}
