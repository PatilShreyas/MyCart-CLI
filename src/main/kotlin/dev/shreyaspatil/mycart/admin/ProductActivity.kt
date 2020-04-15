package dev.shreyaspatil.mycart.admin

import dev.shreyaspatil.mycart.common.ShowCategoriesActivity
import dev.shreyaspatil.mycart.common.ShowProductActivity
import dev.shreyaspatil.mycart.model.Product
import dev.shreyaspatil.mycart.model.Response
import dev.shreyaspatil.mycart.repository.CategoryRepository
import dev.shreyaspatil.mycart.repository.ProductsRepository
import java.util.*

class ProductActivity {

    private val scanner by lazy { Scanner(System.`in`) }
    private val categoryRepository by lazy { CategoryRepository() }
    private val productRepository by lazy { ProductsRepository() }

    fun start() {
        showMainMenu()
        scanner.close()
    }

    private fun showMainMenu() {
        val mainMenuString = """
            ------------------------------------------------------------
            CATEGORIES
            ------------------------------------------------------------
            MENU
            
            1) Add Product
            2) View Products
            3) Navigate Back

            Select Action: 
        """.trimIndent()

        var input = 0

        loop@ while (input != 3) {
            println(mainMenuString)
            input = scanner.nextInt()

            when (input) {
                1 -> addProduct()
                2 -> showProducts()
                3 -> break@loop
                else -> println("INVALID CHOICE!")
            }
        }
    }

    private fun showProducts() {
        ShowProductActivity().show(productRepository.getAllProducts())
    }

    private fun addProduct() {
        val categoryList = categoryRepository.getAllCategories()

        println("Select Category:")
        ShowCategoriesActivity().show(categoryList)

        println("Enter the Category ID: ")
        val categoryId = scanner.nextInt()

        categoryList.find { it.id == categoryId }.let {
            if (it == null) {
                println("INVALID CATEGORY ID")
                return
            }
        }

        println("Enter product details: \n")
        print("Product Name: ")
        scanner.nextLine()
        val productName = scanner.nextLine()

        if (productName.isBlank()) {
            println("INVALID PRODUCT NAME!")
            return
        }

        print("Product Description: ")
        val productDescription = scanner.nextLine()

        if (productDescription.isBlank()) {
            println("INVALID PRODUCT DESCRIPTION!")
            return
        }

        print("Price: Rs.")
        val price = scanner.nextDouble()

        if (price <= 1.0) {
            println("INVALID PRICE AMOUNT!")
            return
        }

        val product = Product(
            categoryId = categoryId,
            name = productName,
            description = productDescription,
            price = price
        )

        productRepository.addProduct(product) { response ->
            when (response) {
                is Response.Success -> {
                    println(
                        """
                            ================================================
                            '$productName' PRODUCT ADDED
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