package money_problem.core

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll

private const val Unused_Value = 1000.0

val amounts = Arb.double(0.0..1_000_000_000.0)
val currencies = Arb.enum<Currency>()

fun createBankWithMissingExchangeRateFor(from: Currency, to: Currency): Bank {
    val unusedCurrency = Currency.values().first { it != from && it != to }
    return Bank.withExchangeRate(unusedCurrency, unusedCurrency, 1.0)
}

class BankProperties : StringSpec({
    "identity: converting a currency to itself should return the same amount" {
        checkAll(amounts, currencies) { amount, currency ->
            val money = Money(amount, currency)

            Bank.withExchangeRate(currency, currency, Unused_Value)
                .convert(money, currency) shouldBeRight money
        }
    }

    "missing exchange rate: converting with a missing exchange rate should return an error" {
        checkAll(
            amounts,
            currencies,
            currencies
        ) { amount, fromCurrency, toCurrency ->
            if (fromCurrency == toCurrency) return@checkAll

            createBankWithMissingExchangeRateFor(fromCurrency, toCurrency)
                .convert(
                    Money(amount, fromCurrency),
                    toCurrency
                ) shouldBeLeft MissingExchangeRateError(fromCurrency, toCurrency)
        }
    }
})