package money_problem.domain

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import money_problem.domain.functional.Currency.*
import money_problem.domain.functional.FunctionalBank
import money_problem.domain.functional.MissingExchangeRateError
import money_problem.domain.functional.Money

class FunctionalBankKTTest : StringSpec({
    val bank = FunctionalBank.withExchangeRate(EUR, USD, 1.2)

    "10 EUR -> USD = 12 USD" {
        bank.convert(Money(10.0, EUR), USD) shouldBeRight Money(12.0, USD)
    }

    "10 EUR -> EUR = 10 EUR" {
        bank.convert(Money(10.0, EUR), EUR) shouldBeRight Money(10.0, EUR)
    }

    "Return an error on missing exchange rate" {
        bank.convert(Money(10.0, EUR), KRW) shouldBeLeft MissingExchangeRateError(EUR, KRW)
    }

    "Conversion with different exchange rates EUR to USD" {
        bank.convert(Money(10.0, EUR), USD) shouldBeRight Money(12.0, USD)

        bank.addExchangeRate(EUR, USD, 1.3)
            .convert(Money(10.0, EUR), USD)
            .shouldBeRight(Money(13.0, USD))
    }
})
