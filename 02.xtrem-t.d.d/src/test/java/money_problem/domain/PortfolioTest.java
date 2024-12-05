package money_problem.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PortfolioTest {
    @Test
    void testPortfolioCanEvaluateWithinTheSameCurrency() {
        Bank bank = Bank.withExchangeRate(Currency.USD, Currency.USD, 1.0);
        Portfolio portfolio = new Portfolio();
        portfolio.addMoney(5.0, Currency.USD);
        portfolio.addMoney(10.0, Currency.USD);

        Assertions.assertThat(portfolio.evaluate(bank, Currency.USD)).isEqualTo("15.0 USD");
    }

    @Test
    @DisplayName("5 USD + 10 EUR = 17 USD")
    void testEvaluateMultiCurrencies() {
        Bank bank = Bank.withExchangeRate(Currency.USD, Currency.USD, 1.0);
        bank.addExchangeRate(Currency.EUR, Currency.USD, 1.2);

        Portfolio portfolio = new Portfolio();
        portfolio.addMoney(5.0, Currency.USD);
        portfolio.addMoney(10.0, Currency.EUR);

        Assertions.assertThat(portfolio.evaluate(bank, Currency.USD)).isEqualTo("17.0 USD");
    }
}
