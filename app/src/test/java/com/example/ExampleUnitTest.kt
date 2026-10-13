package com.example

import com.example.data.local.SampleData
import com.example.data.local.entity.OrderEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun sampleProducts_areNotEmpty() {
        val products = SampleData.initialProducts
        assertTrue(products.isNotEmpty())
        assertTrue(products.size >= 8)
    }

    @Test
    fun categories_containExpectedDepartments() {
        val categories = SampleData.categories
        assertTrue(categories.contains("Electronics"))
        assertTrue(categories.contains("Fashion"))
        assertTrue(categories.contains("Groceries & Spices"))
    }

    @Test
    fun orderEntity_buildsCorrectly() {
        val order = OrderEntity(
            orderNumber = "POS-1234",
            customerName = "Pradeesh",
            customerPhone = "0759786878",
            deliveryAddress = "Colombo",
            city = "Colombo",
            paymentMethod = "Cash on Delivery",
            orderStatus = "Pending",
            totalPrice = 8500.0,
            itemsSummary = "Headphones x1"
        )
        assertEquals("POS-1234", order.orderNumber)
        assertEquals(8500.0, order.totalPrice, 0.01)
        assertFalse(order.itemsSummary.isEmpty())
    }
}
