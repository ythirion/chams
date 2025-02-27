package money_problem.usecases.setup_bank;

import arrow.core.Either;
import money_problem.core.Bank;
import money_problem.usecases.Unit;
import money_problem.usecases.UseCase;
import money_problem.usecases.UseCaseError;
import money_problem.usecases.ports.BankRepository;

import static money_problem.usecases.Unit.unit;

public class SetupBankUseCase implements UseCase<SetupBank, Unit> {
    private final BankRepository bankRepository;

    public SetupBankUseCase(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    private static boolean isValid(SetupBank command) {
        return command != null && command.from() != null && command.to() != null && command.rate() > 0;
    }

    @Override
    public Either<UseCaseError, Unit> invoke(SetupBank command) {
        if (!isValid(command))
            return new Either.Left(new UseCaseError("The command is invalid"));

        return bankRepository.exists()
                ? new Either.Left(new UseCaseError("Bank is already setup"))
                : new Either.Right(setupBank(command));
    }

    private Unit setupBank(SetupBank setupBank) {
        bankRepository.save(
                Bank.withExchangeRate(
                        setupBank.from(),
                        setupBank.to(),
                        setupBank.rate()
                )
        );
        return unit();
    }
}