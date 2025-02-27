package money_problem.domain.functional

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right

typealias ExchangeRate = Double

class FunctionalBank private constructor(private val exchangeRates: Map<String, ExchangeRate>) {
    private fun convertSafely(money: Money, to: Currency): Money =
        if (money.currency == to) money
        else Money(money.amount * exchangeRates.getOrDefault(keyFor(money.currency, to), 0.0), to)

    private fun Currency.canConvert(to: Currency): Boolean =
        this == to || exchangeRates.containsKey(keyFor(this, to))

    fun addExchangeRate(from: Currency, to: Currency, rate: Double): FunctionalBank =
        FunctionalBank(
            exchangeRates.filterKeys { it != keyFor(from, to) } + (keyFor(from, to) to rate)
        )

    fun convert(money: Money, currency: Currency): Either<MissingExchangeRateError, Money> = when {
        money.currency.canConvert(currency) -> convertSafely(money, currency).right()
        else -> MissingExchangeRateError(money.currency, currency).left()
    }

    suspend fun convertWithRoutine(money: Money, currency: Currency): Either<MissingExchangeRateError, Money> =
        either {
            ensure(money.currency.canConvert(currency)) { MissingExchangeRateError(money.currency, currency) }
            convertSafely(money, currency)
        }

    companion object {
        @JvmStatic
        fun withExchangeRate(from: Currency, to: Currency, rate: Double): FunctionalBank =
            FunctionalBank(HashMap())
                .addExchangeRate(from, to, rate)

        private fun keyFor(from: Currency, to: Currency): String = "$from->$to"
    }
}