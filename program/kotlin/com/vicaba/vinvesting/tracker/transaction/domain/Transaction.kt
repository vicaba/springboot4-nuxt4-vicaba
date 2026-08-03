package com.vicaba.vinvesting.tracker.transaction.domain

import com.vicaba.vinvesting.tracker.currency.domain.ConvertedCurrency
import com.vicaba.vinvesting.tracker.currency.domain.ConvertedMoney
import com.vicaba.vinvesting.tracker.currency.domain.Scale
import java.time.LocalDateTime
import java.util.UUID

@JvmInline
value class TransactionId(
    val value: String,
) {
    companion object {
        fun next(): TransactionId = TransactionId(UUID.randomUUID().toString())
    }
}

typealias StockSymbol = String

sealed interface Op {
    sealed interface Buy : Op {
        companion object : Buy
    }

    sealed interface Sell : Op {
        companion object : Sell
    }

    object BuyImpl : Buy

    object SellImpl : Sell
}

enum class TransactionCode {
    OPEN,
    CLOSE,
    UNKNOWN,
}

interface HasTransactionOperation<out O : Op> {
    val op: O
}

data class Share<out O : Op>(
    val origin: Transaction<O>,
) : HasTransactionOperation<O> {
    override val op: O
        get() = origin.op

    fun basis(scale: Scale = 8): ConvertedMoney = origin.basisPerShare(scale)
}

data class Transaction<out O : Op>(
    val id: TransactionId = TransactionId.next(),
    val account: String,
    val symbol: StockSymbol,
    val date: LocalDateTime,
    val currency: ConvertedCurrency,
    override val op: O,
    val quantity: Long,
    val price: ConvertedMoney,
    val fee: ConvertedMoney,
    val code: List<TransactionCode> = listOf(TransactionCode.UNKNOWN),
) : HasTransactionOperation<O> {
    init {
        require(quantity > 0) { "Quantity ($quantity) must be positive or zero" }
        require(price > 0) { "Target price ($price) must be positive or zero" }
        require(fee >= 0) { "Fee ($fee) must be positive or zero" }
    }

    fun proceeds(scale: Scale = 8): ConvertedMoney = (price.scaled(scale) * quantity).scaled(scale)

    fun basis(scale: Scale = 8): ConvertedMoney {
        val adjustedFee =
            when (op) {
                is Op.Buy -> fee
                is Op.Sell -> -fee
            }
        return (proceeds(scale) + adjustedFee.scaled(scale)).scaled(scale)
    }

    fun basisPerShare(scale: Scale = 8): ConvertedMoney = (basis(scale) / quantity).scaled(scale)

    val shares: List<Share<O>>
        get() = List(quantity.toInt()) { Share(this) }
}
