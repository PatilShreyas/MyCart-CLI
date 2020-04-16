package dev.shreyaspatil.mycart.repository

import dev.shreyaspatil.mycart.model.*
import java.sql.Connection
import java.sql.Timestamp

interface AbstractOrderRepository {
    fun addOrder(order: OrderDetails, callback: ((Response<*>) -> Unit)?)
    fun usedCouponTimestamp(couponCode: String): List<Timestamp>
    fun getOrderDetails(): List<OrderDetails>
}

/**
 * Repository of order details of MyCart
 */
class OrderRepository(
    private val connection: Connection,
    private val user: User
) : AbstractOrderRepository {

    /**
     * Adds [order] into the MyCart
     * Gives back the result using [callback].
     */
    override fun addOrder(order: OrderDetails, callback: ((Response<*>) -> Unit)?) {

        val query = """
            INSERT INTO ${OrderDetails.TABLE_NAME}(
                ${OrderDetails.COLUMN_USER_ID},
                ${OrderDetails.COLUMN_TIMESTAMP}
            ) VALUES(?,?)            
        """.trimIndent()

        val pStatement = connection.prepareStatement(query)?.apply {
            setString(1, user.uid)
            setTimestamp(2, Timestamp(System.currentTimeMillis()))
        }

        pStatement?.let { statement ->
            try {
                val rows = statement.executeUpdate()
                if (rows >= 1) {
                    val orderId = getRecentOrderId()
                    val orderTransaction = addOrderDetails(orderId, order.orderItems)
                    val invoiceTransaction = addInvoice(orderId, order.invoice)

                    if (orderTransaction && invoiceTransaction) {
                        callback?.invoke(Response.Success(orderId))
                    } else {
                        callback?.invoke(Response.Error<String>("Failed to place order!"))
                    }
                } else {
                    callback?.invoke(Response.Error<String>("Failed to place order!"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.invoke(Response.Error<String>(e.message.toString()))
            }
        }
    }

    /**
     * Returns the list of timestamp of used coupons of currently signed in user.
     */
    override fun usedCouponTimestamp(couponCode: String): List<Timestamp> {

        val timestamps = mutableListOf<Timestamp>()

        val query = """
            SELECT * FROM coupons_used where APPLIED_COUPON = '$couponCode' AND USER_ID = '${user.uid}'
            ORDER BY ${OrderDetails.COLUMN_TIMESTAMP} DESC
        """.trimIndent()

        connection.createStatement()?.let { statement ->
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                timestamps.add(resultSet.getTimestamp(OrderDetails.COLUMN_TIMESTAMP))
            }

            resultSet.close()
            statement.close()
        }

        return timestamps
    }

    /**
     * Returns all order details of currently signed in user.
     */
    override fun getOrderDetails(): List<OrderDetails> {

        val orders = mutableListOf<OrderDetails>()

        val query = """
            SELECT * FROM ${OrderDetails.TABLE_ORDER_ITEMS} OI, ${OrderDetails.TABLE_NAME} O, ${Invoice.TABLE_NAME} I
            WHERE O.ORDER_ID = OI.ORDER_ID AND O.ORDER_ID = I.ORDER_ID AND USER_ID = '${user.uid}'
        """.trimIndent()

        connection.createStatement()?.let { statement ->
            val resultSet = statement.executeQuery(query)

            var orderId = 0

            while (resultSet.next()) {
                val newOrderId = resultSet.getInt(OrderDetails.COLUMN_ORDER_ID)

                if (newOrderId != orderId) {
                    orderId = newOrderId
                    val timestamp = resultSet.getTimestamp(OrderDetails.COLUMN_TIMESTAMP)
                    val invoice = Invoice(
                        invoiceId = resultSet.getInt(Invoice.COLUMN_INVOICE_ID),
                        appliedCoupon = resultSet.getString(Invoice.COLUMN_APPLIED_COUPON),
                        baseAmount = resultSet.getDouble(Invoice.COLUMN_BASE_AMOUNT),
                        discountAmount = resultSet.getDouble(Invoice.COLUMN_DISCOUNT_AMOUNT),
                        totalAmount = resultSet.getDouble(Invoice.COLUMN_TOTAL_AMOUNT)
                    )
                    val items = getOrderItems(orderId)

                    orders.add(
                        OrderDetails(
                            orderId = orderId,
                            timestamp = timestamp,
                            orderItems = items,
                            invoice = invoice
                        )
                    )
                }

            }
            resultSet.close()
            statement.close()
        }

        return orders
    }

    /**
     * Returns ordered items of order using [orderId]
     * @see getOrderDetails for usage.
     */
    private fun getOrderItems(orderId: Int): MutableList<Item> {
        val items = mutableListOf<Item>()

        val productsRepository = ProductsRepository(connection)

        val query = "SELECT * FROM ${OrderDetails.TABLE_ORDER_ITEMS} WHERE ${OrderDetails.COLUMN_ORDER_ID} = $orderId"

        connection.createStatement()?.let { statement ->
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                val productId = resultSet.getInt(Item.COLUMN_PRODUCT_ID)
                val quantity = resultSet.getInt(Item.COLUMN_QUANTITY)

                val product = productsRepository.getProductById(productId)

                items.add(Item(product!!, quantity))
            }

            resultSet.close()
            statement.close()
        }

        return items
    }

    /**
     * Returns recent order's order id
     * @see getOrderDetails for usage.
     */
    private fun getRecentOrderId(): Int {

        var orderId: Int = -1

        val query = """
            SELECT * FROM ${OrderDetails.TABLE_NAME}             
            WHERE ${OrderDetails.COLUMN_USER_ID} = '${user.uid}' 
            ORDER BY ${OrderDetails.COLUMN_ORDER_ID} DESC LIMIT 1
        """.trimIndent()

        connection.createStatement()?.let { statement ->
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                orderId = resultSet.getInt(OrderDetails.COLUMN_ORDER_ID)
            }

            resultSet.close()
            statement.close()
        }

        return orderId
    }

    /**
     * Adds order into the database
     */
    private fun addOrderDetails(orderId: Int, items: List<Item>): Boolean {
        var isSuccess = false

        if (orderId != -1) {
            items.forEach { item ->
                val query = """
                    INSERT INTO ${OrderDetails.TABLE_ORDER_ITEMS}(
                        ${OrderDetails.COLUMN_ORDER_ID},
                        ${Item.COLUMN_PRODUCT_ID},
                        ${Item.COLUMN_QUANTITY}
                    ) VALUES(?,?,?)            
                """.trimIndent()

                val pStatement = connection.prepareStatement(query)?.apply {
                    setInt(1, orderId)
                    setInt(2, item.product.id)
                    setInt(3, item.quantity)
                }

                pStatement?.let { statement ->
                    try {
                        val rows = statement.executeUpdate()
                        if (rows >= 1) {
                            isSuccess = true
                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            }
        }
        return isSuccess
    }

    /**
     * Adds invoice in the database.
     */
    private fun addInvoice(orderId: Int, invoice: Invoice): Boolean {
        var isSuccess = false
        val query = """
            INSERT INTO ${Invoice.TABLE_NAME}(
                ${Invoice.COLUMN_ORDER_ID},
                ${Invoice.COLUMN_BASE_AMOUNT},
                ${Invoice.COLUMN_DISCOUNT_AMOUNT},
                ${Invoice.COLUMN_TOTAL_AMOUNT},
                ${Invoice.COLUMN_APPLIED_COUPON}
            ) VALUES(?,?,?,?,?)            
        """.trimIndent()

        connection.prepareStatement(query)?.apply {
            setInt(1, orderId)
            setDouble(2, invoice.baseAmount)
            setDouble(3, invoice.discountAmount)
            setDouble(4, invoice.totalAmount)
            setString(5, invoice.appliedCoupon)
        }?.let { statement ->
            try {
                val rows = statement.executeUpdate()
                if (rows >= 1) {
                    isSuccess = true
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }

        return isSuccess
    }
}
