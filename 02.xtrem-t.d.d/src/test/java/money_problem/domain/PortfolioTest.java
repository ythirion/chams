package money_problem.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class PortfolioTest {
    @Test
    void testPortfolioCanEvaluateWithinTheSameCurrency() {
        Portfolio portfolio = new Portfolio();
        portfolio.addMoney(5.0, Currency.USD);
        portfolio.addMoney(10.0, Currency.USD);

        Assertions.assertThat(portfolio.evaluate(Currency.USD)).isEqualTo("15.0 USD");
    }
}
