package dev.shreyaspatil.mycart.model

import java.sql.Timestamp

data class OrderDetails(
    val orderId: Int? = null,
    val orderItems: List<Item>,
    val timestamp: Timestamp? = null,
    val invoice: Invoice
) {
    companion object {
        const val TABLE_NAME = "orders"
        const val TABLE_ORDER_ITEMS = "order_items"

        const val COLUMN_TIMESTAMP = "TIMESTAMP"
        const val COLUMN_ORDER_ID = "ORDER_ID"
        const val COLUMN_USER_ID = "USER_ID"

    }
}