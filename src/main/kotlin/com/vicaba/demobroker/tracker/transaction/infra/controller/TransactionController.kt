package com.vicaba.demobroker.tracker.transaction.infra.controller

import com.vicaba.demobroker.tracker.application.infra.logger.logger
import com.vicaba.demobroker.tracker.contract.api.TransactionsApi
import com.vicaba.demobroker.tracker.contract.model.ConvertedCurrencyDto
import com.vicaba.demobroker.tracker.contract.model.ConvertedMoneyDto
import com.vicaba.demobroker.tracker.contract.model.MoneyDto
import com.vicaba.demobroker.tracker.contract.model.TransactionResponse
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
    override fun getTransactions(): ResponseEntity<List<TransactionResponse>> {
        logger.info("Fetching dummy transactions")
        val dummyDomainTransactions = createDummyTransactions()
        val response = dummyDomainTransactions.map { it.toResponse() }
        return ResponseEntity.ok(response)
    }

    private fun createDummyTransactions(): List<Transaction<Op>> {
        val usd = Currency.getInstance("USD")
        val eur = Currency.getInstance("EUR")
        val currencyUsdEur = ConvertedCurrency(value = usd, original = eur)

        val tx1 =
            Transaction(
                id = TransactionId("tx-1001"),
                account = "Brokerage Alpha",
                symbol = "AAPL",
                date = LocalDateTime.of(2026, 7, 15, 10, 30),
                currency = currencyUsdEur,
                op = Op.BuyImpl,
                quantity = 50,
                price =
                    ConvertedMoney(
                        value = Money.of(185.50, usd),
                        original = Money.of(170.20, eur),
                    ),
                fee =
                    ConvertedMoney(
                        value = Money.of(1.50, usd),
                        original = Money.of(1.38, eur),
                    ),
                code = listOf(TransactionCode.OPEN),
            )

        val tx2 =
            Transaction(
                id = TransactionId("tx-1002"),
                account = "Brokerage Alpha",
                symbol = "MSFT",
                date = LocalDateTime.of(2026, 7, 20, 14, 15),
                currency = currencyUsdEur,
                op = Op.BuyImpl,
                quantity = 25,
                price =
                    ConvertedMoney(
                        value = Money.of(420.00, usd),
                        original = Money.of(385.30, eur),
                    ),
                fee =
                    ConvertedMoney(
                        value = Money.of(2.00, usd),
                        original = Money.of(1.84, eur),
                    ),
                code = listOf(TransactionCode.OPEN),
            )

        val tx3 =
            Transaction(
                id = TransactionId("tx-1003"),
                account = "Growth Portfolio",
                symbol = "NVDA",
                date = LocalDateTime.of(2026, 7, 28, 11, 45),
                currency = currencyUsdEur,
                op = Op.SellImpl,
                quantity = 10,
                price =
                    ConvertedMoney(
                        value = Money.of(125.75, usd),
                        original = Money.of(115.40, eur),
                    ),
                fee =
                    ConvertedMoney(
                        value = Money.of(1.00, usd),
                        original = Money.of(0.92, eur),
                    ),
                code = listOf(TransactionCode.CLOSE),
            )

        val tx4 =
            Transaction(
                id = TransactionId("tx-1004"),
                account = "Growth Portfolio",
                symbol = "GOOGL",
                date = LocalDateTime.of(2026, 8, 1, 16, 0),
                currency = currencyUsdEur,
                op = Op.BuyImpl,
                quantity = 40,
                price =
                    ConvertedMoney(
                        value = Money.of(175.25, usd),
                        original = Money.of(160.80, eur),
                    ),
                fee =
                    ConvertedMoney(
                        value = Money.of(1.25, usd),
                        original = Money.of(1.15, eur),
                    ),
                code = listOf(TransactionCode.OPEN),
            )

        return listOf(tx1, tx2, tx3, tx4)
    }

    private fun Transaction<Op>.toResponse(): TransactionResponse {
        val opEnum =
            when (op) {
                is Op.Buy -> TransactionResponse.Op.BUY
                is Op.Sell -> TransactionResponse.Op.SELL
            }

        val codeList =
            code.map { c ->
                when (c) {
                    TransactionCode.OPEN -> TransactionResponse.Code.OPEN
                    TransactionCode.CLOSE -> TransactionResponse.Code.CLOSE
                    TransactionCode.UNKNOWN -> TransactionResponse.Code.UNKNOWN
                }
            }

        return TransactionResponse(
            id = id.value,
            account = account,
            symbol = symbol,
            date = date.atOffset(ZoneOffset.UTC),
            currency =
                ConvertedCurrencyDto(
                    value = currency.value.currencyCode,
                    original = currency.original.currencyCode,
                ),
            op = opEnum,
            quantity = quantity,
            price =
                ConvertedMoneyDto(
                    value = MoneyDto(value = price.value.value.toDouble(), currency = price.value.currency.currencyCode),
                    original = MoneyDto(value = price.original.value.toDouble(), currency = price.original.currency.currencyCode),
                ),
            fee =
                ConvertedMoneyDto(
                    value = MoneyDto(value = fee.value.value.toDouble(), currency = fee.value.currency.currencyCode),
                    original = MoneyDto(value = fee.original.value.toDouble(), currency = fee.original.currency.currencyCode),
                ),
            code = codeList,
        )
    }
}
