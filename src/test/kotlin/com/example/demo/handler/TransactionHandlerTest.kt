package com.example.demo.handler

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class TransactionHandlerTest {
    private val transactionController = TransactionController()

    @Test
    fun `getTransactions should return HTTP 200 OK`() {
        val response = transactionController.getTransactions()
        assertEquals(200, response.statusCode.value())
    }

    @Test
    fun `getTransactions should return dummy transaction list`() {
        val response = transactionController.getTransactions()
        val transactions = response.body
        assertNotNull(transactions)
        assertEquals(4, transactions!!.size)
        assertEquals("AAPL", transactions[0].symbol)
        assertEquals("BUY", transactions[0].op.value)
    }
}
