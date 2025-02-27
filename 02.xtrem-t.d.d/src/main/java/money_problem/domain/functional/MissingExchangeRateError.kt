package money_problem.domain.functional

data class MissingExchangeRateError(val from: Currency, val to: Currency) {
    override fun toString(): String = "Missing exchange rate from $from to $to"
}