package dev.shreyaspatil.mycart.model

data class Invoice(
    val invoiceId: Int? = -1,
    val appliedCoupon: String? = null,
    val baseAmount: Double,
    val discountAmount: Double = 0.0,
    val totalAmount: Double
) {
    companion object {
        const val TABLE_NAME = "invoice"

        const val COLUMN_INVOICE_ID = "INVOICE_ID"
        const val COLUMN_ORDER_ID = "ORDER_ID"
        const val COLUMN_APPLIED_COUPON = "APPLIED_COUPON"
        const val COLUMN_BASE_AMOUNT = "BASE_AMOUNT"
        const val COLUMN_DISCOUNT_AMOUNT = "DISCOUNT_AMOUNT"
        const val COLUMN_TOTAL_AMOUNT = "TOTAL_AMOUNT"
    }
}