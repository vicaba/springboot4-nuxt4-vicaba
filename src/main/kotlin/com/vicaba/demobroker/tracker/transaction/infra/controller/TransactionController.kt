package com.vicaba.demobroker.tracker.transaction.infra.controller

import com.vicaba.demobroker.tracker.application.infra.logger.logger
import com.vicaba.demobroker.tracker.contract.api.TransactionsApi
import com.vicaba.demobroker.tracker.contract.model.ConvertedCurrencyVM
import com.vicaba.demobroker.tracker.contract.model.ConvertedMoneyVM
import com.vicaba.demobroker.tracker.contract.model.MoneyVM
import com.vicaba.demobroker.tracker.contract.model.TransactionResponseVM
import com.vicaba.demobroker.tracker.currency.domain.ConvertedCurrency
import com.vicaba.demobroker.tracker.currency.domain.ConvertedMoney
import com.vicaba.demobroker.tracker.currency.domain.Money
import com.vicaba.demobroker.tracker.transaction.domain.Op
import com.vicaba.demobroker.tracker.transaction.domain.Transaction
import com.vicaba.demobroker.tracker.transaction.domain.TransactionCode
import com.vicaba.demobroker.tracker.transaction.domain.TransactionId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Currency

@RestController
class TransactionController : TransactionsApi {
    override fun getTransactions(): ResponseEntity<List<TransactionResponseVM>> {
        logger.info("Fetching transactions from TransactionController")
        val transactions = createDummyTransactions()
        val response = transactions.map { it.mapToResponse() }
        return ResponseEntity.ok(response)
    }

    private fun createDummyTransactions(): List<Transaction<Op>> {
        val usd = Currency.getInstance("USD")
        val eur = Currency.getInstance("EUR")

        return listOf(
            Transaction(
                id = TransactionId("tx-1"),
                account = "Account-A",
                symbol = "AAPL",
                date = LocalDateTime.of(2026, 1, 15, 10, 30),
                currency = ConvertedCurrency(value = usd, original = eur),
                op = Op.Buy,
                quantity = 100,
                price =
                    ConvertedMoney(
                        value = Money(150.0.toBigDecimal(), usd),
                        original = Money(140.0.toBigDecimal(), eur),
                    ),
                fee =
                    ConvertedMoney(
                        value = Money(5.0.toBigDecimal(), usd),
                        original = Money(4.6.toBigDecimal(), eur),
                    ),
                code = listOf(TransactionCode.OPEN),
            ),
            Transaction(
                id = TransactionId("tx-2"),
                account = "Account-A",
                symbol = "GOOGL",
                date = LocalDateTime.of(2026, 2, 1, 14, 15),
                currency = ConvertedCurrency(value = usd, original = usd),
                op = Op.Buy,
                quantity = 50,
                price =
                    ConvertedMoney(
                        value = Money(2800.0.toBigDecimal(), usd),
                        original = Money(2800.0.toBigDecimal(), usd),
                    ),
                fee =
                    ConvertedMoney(
                        value = Money(10.0.toBigDecimal(), usd),
                        original = Money(10.0.toBigDecimal(), usd),
                    ),
                code = listOf(TransactionCode.OPEN),
            ),
            Transaction(
                id = TransactionId("tx-3"),
                account = "Account-B",
                symbol = "MSFT",
                date = LocalDateTime.of(2026, 2, 10, 11, 0),
                currency = ConvertedCurrency(value = usd, original = eur),
                op = Op.Sell,
                quantity = 25,
                price =
                    ConvertedMoney(
                        value = Money(310.0.toBigDecimal(), usd),
                        original = Money(290.0.toBigDecimal(), eur),
                    ),
                fee =
                    ConvertedMoney(
                        value = Money(3.5.toBigDecimal(), usd),
                        original = Money(3.2.toBigDecimal(), eur),
                    ),
                code = listOf(TransactionCode.CLOSE),
            ),
            Transaction(
                id = TransactionId("tx-4"),
                account = "Account-B",
                symbol = "AMZN",
                date = LocalDateTime.of(2026, 3, 5, 16, 45),
                currency = ConvertedCurrency(value = usd, original = usd),
                op = Op.Buy,
                quantity = 10,
                price =
                    ConvertedMoney(
                        value = Money(3300.0.toBigDecimal(), usd),
                        original = Money(3300.0.toBigDecimal(), usd),
                    ),
                fee =
                    ConvertedMoney(
                        value = Money(12.0.toBigDecimal(), usd),
                        original = Money(12.0.toBigDecimal(), usd),
                    ),
                code = listOf(TransactionCode.OPEN),
            ),
            Transaction(
                id = TransactionId("tx-5"),
                account = "Account-A",
                symbol = "TSLA",
                date = LocalDateTime.of(2026, 3, 20, 9, 30),
                currency = ConvertedCurrency(value = usd, original = eur),
                op = Op.Sell,
                quantity = 30,
                price =
                    ConvertedMoney(
                        value = Money(750.0.toBigDecimal(), usd),
                        original = Money(700.0.toBigDecimal(), eur),
                    ),
                fee =
                    ConvertedMoney(
                        value = Money(8.0.toBigDecimal(), usd),
                        original = Money(7.5.toBigDecimal(), eur),
                    ),
                code = listOf(TransactionCode.CLOSE),
            ),
        )
    }

    private fun Transaction<*>.mapToResponse(): TransactionResponseVM {
        val opEnum =
            when (op) {
                is Op.Buy -> TransactionResponseVM.Op.BUY
                is Op.Sell -> TransactionResponseVM.Op.SELL
            }

        val codeList =
            code.map { c ->
                when (c) {
                    TransactionCode.OPEN -> TransactionResponseVM.Code.OPEN
                    TransactionCode.CLOSE -> TransactionResponseVM.Code.CLOSE
                    TransactionCode.UNKNOWN -> TransactionResponseVM.Code.UNKNOWN
                }
            }

        return TransactionResponseVM(
            id = id.value,
            account = account,
            symbol = symbol,
            date = date.atOffset(ZoneOffset.UTC),
            currency =
                ConvertedCurrencyVM(
                    value = currency.value.currencyCode,
                    original = currency.original.currencyCode,
                ),
            op = opEnum,
            quantity = quantity,
            price =
                ConvertedMoneyVM(
                    value = MoneyVM(value = price.value.value.toDouble(), currency = price.value.currency.currencyCode),
                    original = MoneyVM(value = price.original.value.toDouble(), currency = price.original.currency.currencyCode),
                ),
            fee =
                ConvertedMoneyVM(
                    value = MoneyVM(value = fee.value.value.toDouble(), currency = fee.value.currency.currencyCode),
                    original = MoneyVM(value = fee.original.value.toDouble(), currency = fee.original.currency.currencyCode),
                ),
            code = codeList,
        )
    }
}
