package dev.shreyaspatil.mycart.repository

import dev.shreyaspatil.mycart.model.Product
import dev.shreyaspatil.mycart.model.Response
import java.sql.ResultSet

interface AbstractProductRepository {
    fun addProduct(product: Product, callback: ((Response<*>) -> Unit)?)
    fun getProductById(productId: Int): Product?
    fun getProductsByCategory(categoryId: Int): List<Product>
    fun getAllProducts(): List<Product>
}

/**
 * Repository of products of MyCart
 */
class ProductsRepository : BaseRepository(), AbstractProductRepository {

    /**
     * List of Categories
     */
    private val categories by lazy { CategoryRepository().getAllCategories() }

    /**
     * Adds a [product] into the inventory.
     * Gives back result using [callback]
     */
    override fun addProduct(product: Product, callback: ((Response<*>) -> Unit)?) {
        val query = """
            INSERT INTO ${Product.TABLE_NAME}(
                ${Product.COLUMN_CATEGORY_ID}, 
                ${Product.COLUMN_NAME}, 
                ${Product.COLUMN_DESC},
                ${Product.COLUMN_PRICE}
            ) VALUES(?,?,?,?)            
        """.trimIndent()

        val pStatement = connection.prepareStatement(query)?.apply {
            setInt(1, product.categoryId)
            setString(2, product.name)
            setString(3, product.description)
            setDouble(4, product.price)
        }

        pStatement?.let { statement ->
            try {
                val rows = statement.executeUpdate()
                if (rows >= 1) {
                    callback?.invoke(Response.Success(rows))
                } else {
                    callback?.invoke(Response.Error<String>("Failed to add product!"))
                }
            } catch (e: Exception) {
                callback?.invoke(Response.Error<String>(e.message.toString()))
            }

            statement.close()
        }
    }

    /**
     * Returns product specified by [productId]
     */
    override fun getProductById(productId: Int): Product? {
        var product: Product? = null

        val query = "SELECT * FROM ${Product.TABLE_NAME} WHERE ${Product.COLUMN_ID} = $productId"

        connection.createStatement()?.let { statement ->
            val resultSetProduct = statement.executeQuery(query)

            while (resultSetProduct.next()) {
                product = getProductFromResultSet(resultSetProduct)
            }

            resultSetProduct.close()
            statement.close()
        }

        return product
    }

    /**
     * Returns a list of product by using specified [categoryId]
     */
    override fun getProductsByCategory(categoryId: Int): List<Product> {
        val productList = mutableListOf<Product>()

        val query = "SELECT * FROM ${Product.TABLE_NAME} WHERE ${Product.COLUMN_CATEGORY_ID} = $categoryId"

        connection.createStatement()?.let { statement ->
            val resultSetProducts = statement.executeQuery(query)

            while (resultSetProducts.next()) {
                productList.add(getProductFromResultSet(resultSetProducts))
            }

            resultSetProducts.close()
            statement.close()
        }

        return productList
    }

    /**
     * Returns all products from the inventory.
     */
    override fun getAllProducts(): List<Product> {
        val productList = mutableListOf<Product>()

        val query = "SELECT * FROM ${Product.TABLE_NAME}"

        connection.createStatement()?.let { statement ->
            val resultSetProducts = statement.executeQuery(query)

            while (resultSetProducts.next()) {
                productList.add(getProductFromResultSet(resultSetProducts))
            }

            resultSetProducts.close()
            statement.close()
        }

        return productList
    }

    /**
     * Parses the [Product] from [ResultSet]
     */
    private fun getProductFromResultSet(resultSet: ResultSet): Product {
        val id = resultSet.getInt(Product.COLUMN_ID)
        val categoryId = resultSet.getInt(Product.COLUMN_CATEGORY_ID)
        val name = resultSet.getString(Product.COLUMN_NAME)
        val description = resultSet.getString(Product.COLUMN_DESC)
        val price = resultSet.getDouble(Product.COLUMN_PRICE)
        val categoryName = categories.find { it.id == categoryId }!!.name

        return Product(id, categoryId, categoryName, name, description, price)
    }

}