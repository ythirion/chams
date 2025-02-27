package money_problem.usecases.ports;

import arrow.core.Option;
import money_problem.core.Bank;

public interface BankRepository {
    boolean exists();

    Option<Bank> getBank();

    void save(Bank bank);
}
