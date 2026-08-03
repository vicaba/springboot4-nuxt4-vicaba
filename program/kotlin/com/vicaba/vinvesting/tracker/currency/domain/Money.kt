package com.vicaba.vinvesting.tracker.currency.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

typealias Scale = Int

data class Money(
    val value: BigDecimal,
    val currency: Currency,
) : Comparable<Money> {
    companion object {
        fun additiveIdentity(currency: Currency): Money = Money(BigDecimal.ZERO, currency)

        fun of(
            value: Long,
            currency: Currency,
        ): Money = Money(BigDecimal.valueOf(value), currency)

        fun of(
            value: String,
            currency: Currency,
        ): Money = Money(BigDecimal(value), currency)

        fun of(
            value: Double,
            currency: Currency,
        ): Money = Money(BigDecimal.valueOf(value), currency)
    }

    operator fun unaryMinus(): Money = Money(value.negate(), currency)

    operator fun plus(that: Money): Money = compute(that) { a, b -> a.add(b) }

    operator fun minus(that: Money): Money = compute(that) { a, b -> a.subtract(b) }

    operator fun times(that: Money): Money = compute(that) { a, b -> a.multiply(b) }

    operator fun div(that: Money): Money = compute(that) { a, b -> a.divide(b, RoundingMode.HALF_UP) }

    operator fun plus(that: Long): Money = Money(value.add(BigDecimal.valueOf(that)), currency)

    operator fun minus(that: Long): Money = Money(value.subtract(BigDecimal.valueOf(that)), currency)

    operator fun times(that: Long): Money = Money(value.multiply(BigDecimal.valueOf(that)), currency)

    operator fun div(that: Long): Money = Money(value.divide(BigDecimal.valueOf(that), RoundingMode.HALF_UP), currency)

    override fun compareTo(other: Money): Int {
        require(isCurrencyEqual(other)) { "Cannot compare Money with different currencies. this: $this; that: $other" }
        return value.compareTo(other.value)
    }

    operator fun compareTo(that: Long): Int = value.compareTo(BigDecimal.valueOf(that))

    fun scaled(scale: Scale = 8): Money = Money(value.setScale(scale, RoundingMode.HALF_UP), currency)

    private inline fun compute(
        that: Money,
        f: (BigDecimal, BigDecimal) -> BigDecimal,
    ): Money {
        require(isCurrencyEqual(that)) { "Cannot compute Money, currencies must be equal. this: $this; that: $that" }
        return Money(f(value, that.value), currency)
    }

    private fun isCurrencyEqual(that: Money): Boolean = currency.currencyCode == that.currency.currencyCode
}
