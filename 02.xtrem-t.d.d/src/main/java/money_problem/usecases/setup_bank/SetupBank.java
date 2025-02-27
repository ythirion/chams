package money_problem.usecases.setup_bank;

import money_problem.core.Currency;
import money_problem.usecases.Request;

public record SetupBank(Currency from, Currency to, double rate) implements Request {
}