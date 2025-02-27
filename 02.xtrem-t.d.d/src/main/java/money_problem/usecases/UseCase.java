package money_problem.usecases;

import arrow.core.Either;

public interface UseCase<T extends Request, S> {
    Either<UseCaseError, S> invoke(T command);
}