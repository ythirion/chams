package money_problem.domain;

import io.vavr.control.Either;
import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static money_problem.domain.Currency.*;

class FunctionalBankTest {
    private final FunctionalBank bank = FunctionalBank.withExchangeRate(EUR, USD, 1.2);

    @Test
    @DisplayName("10 EUR -> USD = 12 USD")
    void shouldConvertEuroToUsd() {
        var result = bank.convert(new Money(10, EUR), USD);
        VavrAssertions.assertThat(result)
                .containsOnRight(new Money(12, USD));
    }

    @Test
    @DisplayName("10 EUR -> EUR = 10 EUR")
    void shouldConvertInSameCurrency() {
        var result = bank.convert(new Money(10, EUR), EUR);
        VavrAssertions.assertThat(result).containsOnRight(new Money(10, EUR));
    }

    @Test
    @DisplayName("Throws a MissingExchangeRateException in case of missing exchange rates")
    void shouldReturnALeftOnMissingExchangeRate() {
        var result = bank.convert(new Money(10, EUR), KRW);
        VavrAssertions.assertThat(result).containsOnLeft(new MissingExchangeRateError(EUR, KRW));
    }

    @Test
    @DisplayName("Conversion with different exchange rates EUR to USD")
    void shouldConvertWithDifferentExchangeRates() {
        var result = bank.convert(new Money(10, EUR), USD);
        VavrAssertions.assertThat(result).containsOnRight(new Money(12, USD));

        FunctionalBank updatedBank = bank.addExchangeRate(EUR, USD, 1.3);

        var updatedResult = updatedBank.convert(new Money(10, EUR), USD);
        VavrAssertions.assertThat(updatedResult).containsOnRight(new Money(13, USD));
    }

    @Test
    void shouldReturnAnEitherWhenConvertCalled() {
        Either<MissingExchangeRateError, Money> result = bank.convert(new Money(10, EUR), KRW);
        VavrAssertions.assertThat(result).containsOnLeft(new MissingExchangeRateError(EUR, KRW));
    }

    @Test
    void shouldConvertCurrencyWithoutErrorWithResult() {
        Either<MissingExchangeRateError, Money> result = bank.convert(new Money(10, EUR), USD);
        VavrAssertions.assertThat(result).containsOnRight(new Money(12, USD));
    }

}
