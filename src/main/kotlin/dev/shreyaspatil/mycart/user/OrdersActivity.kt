package dev.shreyaspatil.mycart.user

import dev.shreyaspatil.mycart.model.OrderDetails

class OrdersActivity(private val orders: List<OrderDetails>) {
    fun start() {
        showOrders()
    }

    private fun showOrders() {
        if (orders.isEmpty()) {
            println("NO ORDERS!")
            return
        }
        orders.forEach { orderDetails ->
            println(
                """
            ===========================================
            Order ID: ${orderDetails.orderId}
            Invoice ID: ${orderDetails.invoice.invoiceId}
            Date & Time: ${orderDetails.timestamp}
        """.trimIndent()
            )

            System.out.format(
                "\n%-20s%-10s%-10s",
                "PRODUCT",
                "QUANTITY",
                "PRICE"
            )
            print("\n------------------------------------")
            orderDetails.orderItems.forEach { item ->
                System.out.format(
                    "\n%-20s%-10dRs.%-10s",
                    item.product.name,
                    item.quantity,
                    "${item.product.price * item.quantity}"
                )
            }
            print("\n===========================================")
            System.out.format("\n%-30s%-10s", "Total =", "Rs.${orderDetails.invoice.baseAmount}")
            System.out.format("\n%-30s%-10s", "Discount =", "-Rs.${orderDetails.invoice.discountAmount}")
            System.out.format("\n%-30s%-10s", "Paid Amount =", "Rs.${orderDetails.invoice.totalAmount}")
            print("\n===========================================\n")
        }
    }
}