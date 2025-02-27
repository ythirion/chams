package money_problem.domain.functional

import io.vavr.control.Either
import io.vavr.control.Either.left
import io.vavr.control.Either.right
import money_problem.domain.Currency
import money_problem.domain.MissingExchangeRateError

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

    fun convert(money: Money, currency: Currency): Either<MissingExchangeRateError, Money> =
        if (!money.currency.canConvert(currency)) left(MissingExchangeRateError(money.currency, currency))
        else right(convertSafely(money, currency))

    companion object {
        @JvmStatic
        fun withExchangeRate(from: Currency, to: Currency, rate: Double): FunctionalBank {
            val bank = FunctionalBank(HashMap())
            return bank.addExchangeRate(from, to, rate)
        }

        private fun keyFor(from: Currency, to: Currency): String {
            return "$from->$to"
        }
    }
}