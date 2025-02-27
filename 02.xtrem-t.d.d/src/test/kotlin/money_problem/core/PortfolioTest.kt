package money_problem.core

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import money_problem.core.Currency.*

class PortfolioTest : StringSpec({
    val bank = Bank.withExchangeRate(USD, USD, 1.0)
        .addExchangeRate(USD, KRW, 1100.0)
        .addExchangeRate(EUR, USD, 1.2)

    "5 USD + 10 USD = 15 USD" {
        portfolioWith(
            5.0.dollars(),
            10.0.dollars()
        ).evaluate(bank, USD) shouldBeRight 15.0.dollars()
    }

    "5 USD + 10 EUR = 17 USD" {
        portfolioWith(
            5.0.dollars(),
            10.0.euros()
        ).evaluate(bank, USD) shouldBeRight 17.0.dollars()
    }

    "1 USD + 1100 KRW = 2220 KRW" {
        portfolioWith(
            1.0.dollars(),
            1100.0.koreanWons()
        ).evaluate(bank, KRW) shouldBeRight 2200.0.koreanWons()
    }

    "5 USD + 10 EUR + 4 EUR = 21.8 USD" {
        portfolioWith(
            5.0.dollars(),
            10.0.euros(),
            4.0.euros()
        ).evaluate(bank, USD) shouldBeRight 21.8.dollars()
    }

    "Return a failure result in case of missing exchange rates" {
        val emptyBank = Bank.withExchangeRate(EUR, USD, 1.2)

        portfolioWith(
            1.0.dollars(),
            1.0.euros(),
            1.0.koreanWons()
        ).evaluate(emptyBank, KRW) shouldBeLeft MissingExchangeRatesError(
            listOf(
                MissingExchangeRateError(USD, KRW),
                MissingExchangeRateError(EUR, KRW)
            )
        )
    }

})

fun portfolioWith(vararg moneys: Money): Portfolio = moneys.fold(Portfolio(), Portfolio::addMoney)
