package dev.shreyaspatil.mycart.repository

import dev.shreyaspatil.mycart.model.Cart
import dev.shreyaspatil.mycart.model.Item
import dev.shreyaspatil.mycart.model.Response
import dev.shreyaspatil.mycart.model.User
import dev.shreyaspatil.mycart.session.SessionManager

interface AbstractCartRepository {
    fun addItemToCart(item: Item, callback: ((Response<*>) -> Unit)?)
    fun getCart(): Cart?
    fun removeAllItems(callback: ((Response<*>) -> Unit)?)
}

/**
 * Repository to keep track of cart of currently signed in user.
 */
class CartRepository : BaseRepository(), AbstractCartRepository {

    private lateinit var user: User

    init {
        try {
            user = SessionManager.currentUser!!
        } catch (e: Exception) {
        }
    }

    /**
     * Adds [item] in the cart of currently signed in user.
     * Gives back result using [callback]
     */
    override fun addItemToCart(item: Item, callback: ((Response<*>) -> Unit)?) {

        if (!this::user.isInitialized) {
            callback?.invoke(Response.Error<String>("ACCESS DENIED! Login First!"))
            return
        }

        val query = """
            INSERT INTO ${Cart.TABLE_NAME}(
                ${Cart.COLUMN_USER_ID}, 
                ${Item.COLUMN_PRODUCT_ID}, 
                ${Item.COLUMN_QUANTITY}
            ) VALUES(?,?,?)            
        """.trimIndent()

        val pStatement = connection.prepareStatement(query)?.apply {
            setString(1, user.uid)
            setInt(2, item.product.id)
            setInt(3, item.quantity)
        }

        pStatement?.let { statement ->
            try {
                val rows = statement.executeUpdate()
                if (rows >= 1) {
                    callback?.invoke(Response.Success(rows))
                } else {
                    callback?.invoke(Response.Error<String>("Failed to add item to the cart!"))
                }
            } catch (e: Exception) {
                callback?.invoke(Response.Error<String>(e.message.toString()))
            }

            statement.close()
        }
    }

    /**
     * Returns current cart details of currently signed in user.
     */
    override fun getCart(): Cart? {
        var cart: Cart? = null

        val productsRepository = ProductsRepository()

        val query = "SELECT * FROM ${Cart.TABLE_NAME} WHERE ${Cart.COLUMN_USER_ID} = '${user.uid}'"

        connection.createStatement()?.let { statement ->
            val resultSet = statement.executeQuery(query)

            val cartItems = mutableListOf<Item>()

            while (resultSet.next()) {
                val productId = resultSet.getInt(Item.COLUMN_PRODUCT_ID)
                val quantity = resultSet.getInt(Item.COLUMN_QUANTITY)

                val product = productsRepository.getProductById(productId)

                cartItems.add(Item(product!!, quantity))
            }

            cart = Cart(cartItems)

            resultSet.close()
            statement.close()
        }

        return cart
    }

    /**
     * Cleares the cart of currently signed in user.
     */
    override fun removeAllItems(callback: ((Response<*>) -> Unit)?) {
        val query = "DELETE FROM ${Cart.TABLE_NAME} WHERE ${Cart.COLUMN_USER_ID} = '${user.uid}'"

        connection.createStatement()?.let { statement ->
            try {
                val rows = statement.executeUpdate(query)
                if (rows >= 1) {
                    callback?.invoke(Response.Success(rows))
                } else {
                    callback?.invoke(Response.Error<String>("Failed to remove items from the cart!"))
                }
            } catch (e: Exception) {
                callback?.invoke(Response.Error<String>(e.message.toString()))
            }
            statement.close()
        }
    }
}