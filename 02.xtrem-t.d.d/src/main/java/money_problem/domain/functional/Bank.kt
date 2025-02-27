package money_problem.domain.functional

import arrow.core.Either
import arrow.core.left
import arrow.core.right

typealias ExchangeRate = Double

class Bank private constructor(private val exchangeRates: Map<String, ExchangeRate>) {
    private fun convertSafely(money: Money, to: Currency): Money =
        if (money.currency == to) money
        else Money(money.amount * exchangeRates.getOrDefault(keyFor(money.currency, to), 0.0), to)

    private fun Currency.canConvert(to: Currency): Boolean =
        this == to || exchangeRates.containsKey(keyFor(this, to))

    fun addExchangeRate(from: Currency, to: Currency, rate: Double): Bank =
        Bank(
            exchangeRates.filterKeys { it != keyFor(from, to) } + (keyFor(from, to) to rate)
        )

    // Alternatives here: https://arrow-kt.io/learn/typed-errors/either-and-ior/
    fun convert(money: Money, currency: Currency): Either<MissingExchangeRateError, Money> = when {
        money.currency.canConvert(currency) -> convertSafely(money, currency).right()
        else -> MissingExchangeRateError(money.currency, currency).left()
    }

    companion object {
        fun withExchangeRate(from: Currency, to: Currency, rate: Double): Bank =
            Bank(HashMap()).addExchangeRate(from, to, rate)

        private fun keyFor(from: Currency, to: Currency): String = "$from->$to"
    }
}