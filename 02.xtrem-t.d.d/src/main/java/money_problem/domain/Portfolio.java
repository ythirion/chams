package money_problem.domain;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {

    private final Map<Currency, Double> lines = new HashMap<>();

    public void addMoney(double amount, Currency currency) {
        if (lines.containsKey(currency)) {
            amount += lines.get(currency);
        }
        lines.put(currency, amount);
    }

    public String evaluate(Currency currency) {
        double sum = lines.entrySet()
                .stream()
                .map(entry -> {
                    Bank bank = Bank.withExchangeRate(entry.getKey(), currency, 1.0);
                    try {
                        return bank.convert(entry.getValue(), entry.getKey(), currency);
                    } catch (MissingExchangeRateException e) {
                        throw new RuntimeException(e);
                    }
                }).reduce(0.0, Double::sum);
        return sum + " " + currency;
    }
}
