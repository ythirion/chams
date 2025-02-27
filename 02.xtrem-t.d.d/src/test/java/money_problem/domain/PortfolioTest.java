package money_problem.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PortfolioTest {
    private Bank bank;

    @BeforeEach
    void setUp() {
        bank = Bank.withExchangeRate(Currency.USD, Currency.USD, 1.0)
                .addExchangeRate(Currency.USD, Currency.KRW, 1100)
                .addExchangeRate(Currency.EUR, Currency.USD, 1.2);
    }

    @Test
    void testPortfolioCanEvaluateWithinTheSameCurrency() {
        Portfolio portfolio = new Portfolio()
                .addMoney(new Money(5.0, Currency.USD))
                .addMoney(new Money(10.0, Currency.USD));

        Assertions.assertThat(portfolio.evaluate(bank, Currency.USD)).isEqualTo("15.0 USD");
    }

    @Test
    @DisplayName("5 USD + 10 EUR = 17 USD")
    void testEvaluateMultiCurrencies() {
        Portfolio portfolio = new Portfolio()
                .addMoney(new Money(5.0, Currency.USD))
                .addMoney(new Money(10.0, Currency.EUR));

        Assertions.assertThat(portfolio.evaluate(bank, Currency.USD)).isEqualTo("17.0 USD");
    }

    @Test
    @DisplayName("1 USD + 1100 KRW = 2200 KRW")
    void testEvaluateMultiCurrenciesWithKoreanWon() {
        Portfolio portfolio = new Portfolio()
                .addMoney(new Money(1.0, Currency.USD))
                .addMoney(new Money(1100.0, Currency.KRW));

        Assertions.assertThat(portfolio.evaluate(bank, Currency.KRW)).isEqualTo("2200.0 KRW");
    }
}
