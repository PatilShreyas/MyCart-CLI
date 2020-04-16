package dev.shreyaspatil.mycart.user

import dev.shreyaspatil.mycart.model.Item
import dev.shreyaspatil.mycart.model.Product
import dev.shreyaspatil.mycart.model.Response
import dev.shreyaspatil.mycart.repository.CartRepository
import dev.shreyaspatil.mycart.session.SessionManager
import dev.shreyaspatil.mycart.utils.DatabaseUtils
import java.util.*

class ProductDetailsActivity(private val product: Product) {

    private val cartRepository: CartRepository by lazy {
        CartRepository(
            DatabaseUtils.getConnection()!!,
            SessionManager.currentUser!!
        )
    }
    private val scanner = Scanner(System.`in`)

    fun start() {
        println(
            """
            =========================================
            ${product.name}
            =========================================
            Price: Rs.      ${product.price}
            Category:       ${product.categoryName}
            Description:    ${product.description}
            =========================================
        """.trimIndent()
        )
        cartRepository.getCart().let { cart ->
            val isExistsInCart = if (cart != null) {
                val item = cart.items.find { it.product.id == product.id }
                item != null
            } else {
                false
            }

            if (!isExistsInCart) {
                showMainMenu()
            } else {
                println("++ ITEM EXISTS IN YOUR CART ++")
            }
        }
    }

    private fun showMainMenu() {

        val mainMenuString = """
            ---------
            MENU
            ---------
            1) Add to Cart
            2) Navigate Back
            
            Select Option: 
        """.trimIndent()

        var input = 0

        while (input != 2) {
            print(mainMenuString)
            input = scanner.nextInt()

            if (input == 1) {
                print("\nQuantity: ")
                val quantity = scanner.nextInt()

                if (quantity !in 1..10) {
                    println("QUANTITY SHOULD BE MIN 1 AND MAX 10 ")
                    return
                }

                addToCart(Item(product, quantity))

                break
            }
        }
    }

    private fun addToCart(item: Item) {
        cartRepository.addItemToCart(item) { response ->
            when (response) {
                is Response.Success -> {
                    println(
                        """
                        ================================================
                        ++ ITEM ADDED TO THE CART ++
                        ================================================
                    """.trimIndent()
                    )
                }

                is Response.Error -> {
                    println(
                        """
                        ================================================
                        ERROR OCCURRED: ${response.message}
                        ================================================
                    """.trimIndent()
                    )
                }
            }
        }
    }
}