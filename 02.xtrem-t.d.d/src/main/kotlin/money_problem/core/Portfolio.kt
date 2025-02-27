package money_problem.core

import arrow.core.Either
import arrow.core.left
import arrow.core.right

typealias ConversionResults = List<Either<MissingExchangeRateError, Money>>

class Portfolio private constructor(private val lines: List<Money>) {
    constructor() : this(emptyList())

    fun addMoney(money: Money): Portfolio = Portfolio(lines + money)

    private fun convertAllMoneys(bank: Bank, toCurrency: Currency): List<Either<MissingExchangeRateError, Money>> =
        lines.map { money: Money -> bank.convert(money, toCurrency) }

    fun evaluate(bank: Bank, to: Currency): Either<MissingExchangeRatesError, Money> =
        convertAllMoneys(bank, to)
            .let {
                when {
                    it.containsFailure() -> toFailure(it).left()
                    else -> sumConvertedMoney(it, to).right()
                }
            }

    private fun ConversionResults.containsFailure(): Boolean = any { result -> result.isLeft() }

    private fun toFailure(convertedMoneys: ConversionResults): MissingExchangeRatesError =
        MissingExchangeRatesError(
            convertedMoneys
                .filter { result -> result.isLeft() }
                .mapNotNull { result -> result.swap().orNull() }
        )

    private fun sumConvertedMoney(convertedMoneys: ConversionResults, toCurrency: Currency): Money =
        Money(
            convertedMoneys
                .mapNotNull { it.orNull() }
                .sumOf { it.amount }, toCurrency
        )
}