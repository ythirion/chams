package money_problem.domain.functional

data class Money(val amount: Double, val currency: Currency) {
    operator fun times(times: Int): Money = Money(amount * times, currency)
}