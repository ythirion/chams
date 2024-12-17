package money_problem.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Portfolio {

    private final List<Money> lines;

    public Portfolio() {
        this(Collections.emptyList());
    }

    private Portfolio(List<Money> lines) {
        this.lines = Collections.unmodifiableList(lines);
    }

    private static String formatPortfolio(Currency currency, double sum) {
        return sum + " " + currency;
    }

    private Money convertToCurrency(Currency currency, Money money, Bank bank) {
        try {
            return bank.convert(money, currency);
        } catch (MissingExchangeRateException e) {
            throw new RuntimeException(e);
        }
    }

    public String evaluate(Bank bank, Currency currency) {
        double sum = lines
                .stream()
                .map(money -> convertToCurrency(currency, money, bank).amount()
                ).reduce(0.0, Double::sum);
        return formatPortfolio(currency, sum);
    }

    public Portfolio addMoney(Money money) {
        ArrayList<Money> newLines = new ArrayList<>(lines);
        newLines.add(money);
        return new Portfolio(newLines);
    }
}
