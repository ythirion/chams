package money_problem.domain.functional

import money_problem.domain.Currency

data class Money(val amount: Double, val currency: Currency) {
    operator fun times(times: Int): Money = Money(amount * times, currency)
    fun divide(divisor: Int): Money = Money(amount / divisor, currency)
}