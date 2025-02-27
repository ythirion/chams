package money_problem.usecases;

import arrow.core.Either;
import money_problem.usecases.setup_bank.SetupBank;
import money_problem.usecases.setup_bank.SetupBankUseCase;
import org.junit.jupiter.api.Test;

import static money_problem.core.Currency.EUR;
import static money_problem.core.Currency.USD;
import static money_problem.usecases.Unit.unit;
import static org.assertj.core.api.Assertions.assertThat;

class SetupBankUseCaseTest {
    public static final SetupBank INVALID_COMMAND = new SetupBank(null, null, 0.0);
    public static final SetupBank VALID_COMMAND = new SetupBank(EUR, USD, 0.987);

    private final BankRepositoryFake bankRepositoryFake = new BankRepositoryFake();
    private final SetupBankUseCase setupBankUseCase = new SetupBankUseCase(bankRepositoryFake);

    @Test
    void return_an_error_when_bank_already_setup() {
        bankAlreadySetup();

        assertThat(setupBankUseCase.invoke(VALID_COMMAND))
                .isEqualTo(new Either.Left(new UseCaseError("Bank is already setup")));
    }

    @Test
    void return_an_error_when_invalid_command() {
        assertThat(setupBankUseCase.invoke(INVALID_COMMAND))
                .isEqualTo(new Either.Left(new UseCaseError("The command is invalid")));
    }

    @Test
    void return_a_success_when_bank_not_already_setup() {
        assertThat(setupBankUseCase.invoke(VALID_COMMAND))
                .isEqualTo(new Either.Right(unit()));

        hasBeenSaved();
    }

    private void bankAlreadySetup() {
        bankRepositoryFake.hasAlreadyBeenSetup();
    }

    private void hasBeenSaved() {
        assertThat(bankRepositoryFake.hasBeenSaved())
                .isTrue();
    }
}