import com.practice.FizzBuzz;
import net.bytebuddy.asm.MemberSubstitution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FizzBuzzTest {
    public static Stream<Arguments> fizzFeeder() {
        return Stream.of(
                Arguments.of(3),
                Arguments.of(6),
                Arguments.of(9)
        );
    }
    // si 3, 6, 9 ... -> Fizz
    // si mul 5 -> Buzz
    // si mul 3 & 5 -> com.practice.FizzBuzz
    // sinon chiffre
    // entre 1 et 100

    @ParameterizedTest
    @MethodSource("fizzFeeder")
    void testConvert_whenGivenAMultipleOf3_shouldReturnFizz(int number) {
        assertThat(FizzBuzz.convert(number)).isEqualTo("Fizz");
    }

    @Test
    void testConvert_whenGiven5_shouldReturnFizz() {
        assertThat(FizzBuzz.convert(5)).isEqualTo("Buzz");
    }


}