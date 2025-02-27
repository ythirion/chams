package money_problem.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.vavr.control.Either.left
import io.vavr.control.Either.right
import money_problem.domain.functional.Currency.*
import money_problem.domain.functional.FunctionalBank
import money_problem.domain.functional.MissingExchangeRateError
import money_problem.domain.functional.Money

class FunctionalBankKTTest : StringSpec({

    val bank = FunctionalBank.withExchangeRate(EUR, USD, 1.2)

    "10 EUR -> USD = 12 USD" {
        bank.convert(Money(10.0, EUR), USD) shouldBe right(Money(12.0, USD))
    }

    "10 EUR -> EUR = 10 EUR" {
        bank.convert(Money(10.0, EUR), EUR) shouldBe right(Money(10.0, EUR))
    }

    "Should return a Left on missing exchange rate" {
        bank.convert(Money(10.0, EUR), KRW) shouldBe left(MissingExchangeRateError(EUR, KRW))
    }

    "Conversion with different exchange rates EUR to USD" {
        bank.convert(Money(10.0, EUR), USD) shouldBe right(Money(12.0, USD))
        
        bank.addExchangeRate(EUR, USD, 1.3)
            .convert(Money(10.0, EUR), USD)
            .shouldBe(right(Money(13.0, USD)))
    }

    "Should return an Either when convert is called" {
        bank.convert(Money(10.0, EUR), KRW) shouldBe left(MissingExchangeRateError(EUR, KRW))
    }

    "Should convert currency without error with result" {
        bank.convert(Money(10.0, EUR), USD) shouldBe right(Money(12.0, USD))
    }
})
