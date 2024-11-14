import com.practice.FizzBuzz;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FizzBuzzTest {
    public static Stream<Arguments> fizzBuzzFeeder() {
        return Stream.of(
                Arguments.of(3, "Fizz"),
                Arguments.of(6, "Fizz"),
                Arguments.of(9, "Fizz"),
                Arguments.of(5, "Buzz"),
                Arguments.of(10, "Buzz"),
                Arguments.of(20, "Buzz")
        );
    }
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

    @Test
    void testConvert_WhenOutOfRange_throwException() {
        assertThatThrownBy(() -> FizzBuzz.convert(-1)).isInstanceOf(IllegalArgumentException.class);
    }

}