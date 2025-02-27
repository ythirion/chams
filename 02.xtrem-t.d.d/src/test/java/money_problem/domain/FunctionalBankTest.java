package money_problem.domain;

import money_problem.domain.functional.FunctionalBank;
import money_problem.domain.functional.MissingExchangeRateError;
import money_problem.domain.functional.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static money_problem.domain.functional.Currency.*;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class FunctionalBankTest {
    private final FunctionalBank bank = FunctionalBank.withExchangeRate(EUR, USD, 1.2);

    @Test
    @DisplayName("10 EUR -> USD = 12 USD")
    void shouldConvertEuroToUsd() {
        assertThat(bank.convert(new Money(10, EUR), USD))
                .containsOnRight(new Money(12, USD));
    }

    @Test
    @DisplayName("10 EUR -> EUR = 10 EUR")
    void shouldConvertInSameCurrency() {
        assertThat(bank.convert(new Money(10, EUR), EUR))
                .containsOnRight(new Money(10, EUR));
    }

    @Test
    void shouldReturnALeftOnMissingExchangeRate() {
        assertThat(bank.convert(new Money(10, EUR), KRW))
                .containsOnLeft(new MissingExchangeRateError(EUR, KRW));
    }

    @Test
    @DisplayName("Conversion with different exchange rates EUR to USD")
    void shouldConvertWithDifferentExchangeRates() {
        assertThat(bank.convert(new Money(10, EUR), USD))
                .containsOnRight(new Money(12, USD));

        assertThat(
                bank.addExchangeRate(EUR, USD, 1.3)
                        .convert(new Money(10, EUR), USD)
        ).containsOnRight(new Money(13, USD));
    }

    @Test
    void shouldReturnAnEitherWhenConvertCalled() {
        assertThat(bank.convert(new Money(10, EUR), KRW))
                .containsOnLeft(new MissingExchangeRateError(EUR, KRW));
    }

    @Test
    void shouldConvertCurrencyWithoutErrorWithResult() {
        assertThat(bank.convert(new Money(10, EUR), USD))
                .containsOnRight(new Money(12, USD));
    }
}