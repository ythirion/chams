package money_problem.usecases;

import arrow.core.Option;
import money_problem.core.Bank;
import money_problem.usecases.ports.BankRepository;

public class BankRepositoryFake implements BankRepository {
    private boolean setup;
    private Bank savedBank;

    @Override
    public boolean exists() {
        return setup;
    }

    @Override
    public Option<Bank> getBank() {
        return Option.fromNullable(savedBank);
    }

    @Override
    public void save(Bank bank) {
        savedBank = bank;
    }

    public void hasAlreadyBeenSetup() {
        this.setup = true;
    }

    public boolean hasBeenSaved() {
        return savedBank != null;
    }
}
