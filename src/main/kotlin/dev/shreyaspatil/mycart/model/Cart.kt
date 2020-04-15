package dev.shreyaspatil.mycart.model

data class Cart(
    val items: List<Item>
) {
    companion object {
        const val TABLE_NAME = "cart"

        const val COLUMN_USER_ID = "USER_ID"
    }
}