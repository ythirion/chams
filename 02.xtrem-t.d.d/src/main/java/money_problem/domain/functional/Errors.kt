package money_problem.domain.functional

data class MissingExchangeRatesError(val errors: List<MissingExchangeRateError>) {
    override fun toString(): String = errors.joinToString(", ", "Missing exchange rates: ")
}

data class MissingExchangeRateError(val from: Currency, val to: Currency) {
    override fun toString(): String = "$from to $to"
}