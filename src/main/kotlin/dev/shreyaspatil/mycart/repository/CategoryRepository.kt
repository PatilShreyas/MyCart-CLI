package dev.shreyaspatil.mycart.repository

import dev.shreyaspatil.mycart.model.Category
import dev.shreyaspatil.mycart.model.Response

interface AbstractCategoryRepository {
    fun addCategory(category: Category, callback: ((Response<*>) -> Unit)?)
    fun getAllCategories(): List<Category>
}

/**
 * Repository of Different categories of products of MyCart
 */
class CategoryRepository : BaseRepository(), AbstractCategoryRepository {

    /**
     * Adds given [category] in the inventory of MyCart.
     * Gives back result using [callback].
     */
    override fun addCategory(category: Category, callback: ((Response<*>) -> Unit)?) {
        val query = """
            INSERT INTO ${Category.TABLE_NAME}(
                ${Category.COLUMN_NAME}
            ) VALUES(?)            
        """.trimIndent()

        val pStatement = connection.prepareStatement(query)?.apply {
            setString(1, category.name)
        }

        pStatement?.let { statement ->
            try {
                val rows = statement.executeUpdate()
                if (rows >= 1) {
                    callback?.invoke(Response.Success(rows))
                } else {
                    callback?.invoke(Response.Error<String>("Failed to add category!"))
                }
            } catch (e: Exception) {
                callback?.invoke(Response.Error<String>(e.message.toString()))
            }

            statement.close()
        }
    }

    /**
     * Returns all the categories from inventory of MyCart.
     */
    override fun getAllCategories(): List<Category> {
        val categoryList = mutableListOf<Category>()

        val query = "SELECT * FROM ${Category.TABLE_NAME}"

        connection.createStatement()?.let { statement ->
            val resultSetCategory = statement.executeQuery(query)

            while (resultSetCategory.next()) {
                val id = resultSetCategory.getInt(Category.COLUMN_ID)
                val name = resultSetCategory.getString(Category.COLUMN_NAME)

                categoryList.add(Category(id, name))
            }

            resultSetCategory.close()
            statement.close()
        }

        return categoryList
    }

}