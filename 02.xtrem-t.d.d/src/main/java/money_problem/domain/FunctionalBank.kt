package money_problem.domain

import io.vavr.control.Either

class FunctionalBank private constructor(private val exchangeRates: Map<String, Double>) {
    private fun convertSafely(money: Money, to: Currency): Money =
        if (money.currency == to) money
        else Money(money.amount * exchangeRates[keyFor(money.currency, to)]!!, to)

    private fun Currency.canConvert(to: Currency): Boolean =
        this == to || exchangeRates.containsKey(keyFor(this, to))


    fun addExchangeRate(from: Currency, to: Currency, rate: Double): FunctionalBank =
        HashMap(exchangeRates).let { novel ->
                novel[keyFor(from, to)] = rate
                FunctionalBank(java.util.Map.copyOf(novel))
            }

    fun convert(money: Money, currency: Currency): Either<MissingExchangeRateError, Money> {
        if (!money.currency.canConvert(currency)) {
            return Either.left(MissingExchangeRateError(money.currency, currency))
        }
        return Either.right(convertSafely(money, currency))
    }

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