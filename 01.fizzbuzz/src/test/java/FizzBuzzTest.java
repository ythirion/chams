import com.practice.FizzBuzz;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FizzBuzzTest {
    // si 3, 6, 9 ... -> Fizz
    // si mul 5 -> Buzz
    // si mul 3 & 5 -> com.practice.FizzBuzz
    // sinon chiffre
    // entre 1 et 100

    @ParameterizedTest
    @MethodSource("fizzBuzzFeeder")
    void testConvert_MultipleOf3_shouldReturnFizzOrBuzz(int number, String result) {
        assertThat(FizzBuzz.convert(number)).isEqualTo(result);
    }

    @ParameterizedTest
    @MethodSource("canonicalNumbersFeeder")
    void testConvert_NormalNumber_shouldBeReturnedAsIs() {
        assertThat(FizzBuzz.convert(2)).isEqualTo("2");
    }

    @Test
    void testConvert_WhenOutOfRange_throwException() {
        assertThatThrownBy(() -> FizzBuzz.convert(-1)).isInstanceOf(IllegalArgumentException.class);
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

}