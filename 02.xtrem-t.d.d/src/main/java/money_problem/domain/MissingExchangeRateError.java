package money_problem.domain;

public record MissingExchangeRateError(Currency from, Currency to) {
    public String toString() {
        return "Unable to convert money from " + from + " to " + to;
    }
}
