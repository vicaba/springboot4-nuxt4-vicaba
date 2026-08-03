package com.vicaba.demobroker.tracker.currency.domain

import java.util.Currency

data class ConvertedCurrency(
    val value: Currency,
    val original: Currency,
)

data class ConvertedMoney(
    val value: Money,
    val original: Money,
) {
    companion object {
        fun additiveIdentity(currency: ConvertedCurrency): ConvertedMoney =
            ConvertedMoney(
                Money.additiveIdentity(currency.value),
                Money.additiveIdentity(currency.original),
            )
    }

    operator fun unaryMinus(): ConvertedMoney = ConvertedMoney(-value, -original)

    operator fun plus(that: ConvertedMoney): ConvertedMoney = compute(that) { a, b -> a + b }

    operator fun minus(that: ConvertedMoney): ConvertedMoney = compute(that) { a, b -> a - b }

    operator fun times(that: ConvertedMoney): ConvertedMoney = compute(that) { a, b -> a * b }

    operator fun div(that: ConvertedMoney): ConvertedMoney = compute(that) { a, b -> a / b }

    operator fun plus(that: Long): ConvertedMoney = compute(that) { m, l -> m + l }

    operator fun minus(that: Long): ConvertedMoney = compute(that) { m, l -> m - l }

    operator fun times(that: Long): ConvertedMoney = compute(that) { m, l -> m * l }

    operator fun div(that: Long): ConvertedMoney = compute(that) { m, l -> m / l }

    operator fun compareTo(that: ConvertedMoney): Int {
        val valueCompare = value.compareTo(that.value)
        return if (valueCompare != 0) valueCompare else original.compareTo(that.original)
    }

    operator fun compareTo(that: Long): Int = value.compareTo(that)

    fun scaled(scale: Scale = 8): ConvertedMoney = ConvertedMoney(value.scaled(scale), original.scaled(scale))

    private inline fun compute(
        that: Long,
        f: (Money, Long) -> Money,
    ): ConvertedMoney = ConvertedMoney(f(value, that), f(original, that))

    private inline fun compute(
        that: ConvertedMoney,
        f: (Money, Money) -> Money,
    ): ConvertedMoney = ConvertedMoney(f(value, that.value), f(original, that.original))
}
