package dev.shreyaspatil.mycart.model

data class Item(
    val product: Product,
    val quantity: Int
) {
    companion object {
        const val COLUMN_PRODUCT_ID = "PRODUCT_ID"
        const val COLUMN_QUANTITY = "QUANTITY"
    }
}