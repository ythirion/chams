package eid;

import io.vavr.control.Either;

import java.util.Arrays;

public enum Sex {
    Sloubi('1'),
    Gagna('2'),
    Catact('3');

    public final char value;

    Sex(char value) {
        this.value = value;
    }

    public static Either<ParsingError, Sex> parse(char potentialSex) {
        if (Arrays.stream(Sex.values()).anyMatch(sex -> sex.value == potentialSex)) {
            return Either.right(Arrays.stream(Sex.values()).filter(sex -> sex.value == potentialSex).findFirst().get());
        }
        return Either.left(new ParsingError());
    }
}
