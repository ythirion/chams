package money_problem.core

import money_problem.core.Currency.*

data class Money(val amount: Double, val currency: Currency) {
    operator fun times(times: Int): Money = Money(amount * times, currency)
}

fun Double.toMoney(currency: Currency): Money = Money(this, currency)
fun Double.dollars(): Money = Money(this, USD)
fun Double.euros(): Money = Money(this, EUR)
fun Double.koreanWons(): Money = Money(this, KRW)
