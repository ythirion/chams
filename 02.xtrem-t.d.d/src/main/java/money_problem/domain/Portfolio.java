package money_problem.domain;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {

    private final Map<Currency, Double> lines = new HashMap<>();

    private static String formatPortfolio(Currency currency, double sum) {
        return sum + " " + currency;
    }

    private double convertToCurrency(Currency currency, Map.Entry<Currency, Double> entry, Bank bank) {
        try {
            return bank.convert(entry.getValue(), entry.getKey(), currency);
        } catch (MissingExchangeRateException e) {
            throw new RuntimeException(e);
        }
    }

    public String evaluate(Bank bank, Currency currency) {
        double sum = lines.entrySet()
                .stream()
                .map(entry -> convertToCurrency(currency, entry, bank)
                ).reduce(0.0, Double::sum);
        return formatPortfolio(currency, sum);
    }

    public void addMoney(Money money) {
        double amount = money.amount();
        if (lines.containsKey(money.currency())) {
            amount += lines.get(money.currency());
        }
        lines.put(money.currency(), amount);
    }
}
