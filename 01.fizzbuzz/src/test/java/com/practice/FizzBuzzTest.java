package com.practice;

import io.vavr.test.Arbitrary;
import io.vavr.test.Property;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FizzBuzzTest {

    @ParameterizedTest
    @MethodSource("fizzBuzzFeeder")
    void testConvert_MultipleOf3_shouldReturnFizzOrBuzz(int number, String result) {
        assertThat(FizzBuzz.convert(number)).isEqualTo(result);
    }

    @ParameterizedTest
    @MethodSource("canonicalNumbersFeeder")
    void testConvert_NormalNumber_shouldBeReturnedAsIs(int number, String result) {
        assertThat(FizzBuzz.convert(number)).isEqualTo(result);
    }

    public static Stream<Arguments> fizzBuzzFeeder() {
        return Stream.of(
                Arguments.of(3, "Fizz"),
                Arguments.of(6, "Fizz"),
                Arguments.of(9, "Fizz"),
                Arguments.of(5, "Buzz"),
                Arguments.of(10, "Buzz"),
                Arguments.of(15, "FizzBuzz"),
                Arguments.of(20, "Buzz"),
                Arguments.of(30, "FizzBuzz"),
                Arguments.of(45, "FizzBuzz")
        );
    }

    public static Stream<Arguments> canonicalNumbersFeeder() {
        return Stream.of(
                Arguments.of(1, "1"),
                Arguments.of(2, "2"),
                Arguments.of(4, "4"),
                Arguments.of(7, "7"),
                Arguments.of(8, "8"),
                Arguments.of(11, "11"),
                Arguments.of(13, "13")
        );
    }

    @Test
    void should_throw_exception_when_out_of_range() {
        var outOfRangeNumbers = Arbitrary
                .integer()
                .filter(i -> i < FizzBuzz.MINIMUM || i > FizzBuzz.MAXIMUM);

        Property.def("FizBuzz should fail for numbers out of range")
                .forAll(outOfRangeNumbers)
                .suchThat(this::throwsIllegalArgumentException)
                .check()
                .assertIsSatisfied();
    }

    private Boolean throwsIllegalArgumentException(Integer input) {
        try {
            FizzBuzz.convert(input);
            return false;
        }
        catch (IllegalArgumentException e) {
            return true;
        }
    }
}