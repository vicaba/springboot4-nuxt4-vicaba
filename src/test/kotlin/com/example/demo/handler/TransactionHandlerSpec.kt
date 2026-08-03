package com.example.demo.handler

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TransactionHandlerSpec : FunSpec() {
    private val transactionController = TransactionController()

    init {
        test("getTransactions should return HTTP 200 OK") {
            val response = transactionController.getTransactions()
            response.statusCode.value() shouldBe 200
        }

        test("getTransactions should return dummy transaction list") {
            val response = transactionController.getTransactions()
            val transactions = response.body
            transactions shouldNotBe null
            transactions!! shouldHaveSize 4
            transactions[0].symbol shouldBe "AAPL"
            transactions[0].op.value shouldBe "BUY"
        }
    }
}
