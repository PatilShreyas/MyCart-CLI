package dev.shreyaspatil.mycart.repository

import dev.shreyaspatil.mycart.model.Coupon
import dev.shreyaspatil.mycart.model.Response
import dev.shreyaspatil.mycart.model.UsageType
import java.sql.SQLIntegrityConstraintViolationException

interface AbstractCouponRepository {
    fun addCoupon(coupon: Coupon, callback: ((Response<*>) -> Unit)?)
    fun getCouponByCode(couponCode: String): Coupon?
    fun getAllCoupons(): List<Coupon>
}

/**
 * Repository for Coupons or Offers of MyCart
 */
class CouponRepository : BaseRepository(), AbstractCouponRepository {

    /**
     * Adds [coupon] into the inventory of MyCart
     * Gives back result using [callback]
     */
    override fun addCoupon(coupon: Coupon, callback: ((Response<*>) -> Unit)?) {
        val query = """
            INSERT INTO ${Coupon.TABLE_NAME}(
                ${Coupon.COLUMN_COUPON_CODE}, 
                ${Coupon.COLUMN_USAGE_TYPE}, 
                ${Coupon.COLUMN_USAGE_LIMIT},
                ${Coupon.COLUMN_START_DATE},
                ${Coupon.COLUMN_END_DATE},
                ${Coupon.COLUMN_DISCOUNT}
            ) VALUES(?,?,?,?,?,?)            
        """.trimIndent()

        val usageType: Int
        val usageLimit: Int

        when (coupon.usageType) {
            is UsageType.OneTime -> {
                usageType = Coupon.VALUE_ONE_TIME_USAGE
                usageLimit = 1
            }
            is UsageType.MultipleTime -> {
                usageType = Coupon.VALUE_MULTIPLE_TIME_USAGE
                usageLimit = coupon.usageType.limit
            }
            is UsageType.UnlimitedTime -> {
                usageType = Coupon.VALUE_UNLIMITED_TIME_USAGE
                usageLimit = 0
            }
        }

        val pStatement = connection.prepareStatement(query)?.apply {
            setString(1, coupon.couponCode)
            setInt(2, usageType)
            setInt(3, usageLimit)
            setDate(4, coupon.startDate)
            setDate(5, coupon.endDate)
            setDouble(6, coupon.discountPercentage)
        }

        pStatement?.let { statement ->
            try {
                val rows = statement.executeUpdate()
                if (rows >= 1) {
                    callback?.invoke(Response.Success(rows))
                } else {
                    callback?.invoke(Response.Error<String>("Failed to add coupon!"))
                }
            } catch (sqlException: SQLIntegrityConstraintViolationException) {
                callback?.invoke(Response.Error<String>("Coupon Already Exists:"))
            } catch (e: Exception) {
                callback?.invoke(Response.Error<String>(e.message.toString()))
            }
        }
    }

    /**
     * Retrieves Coupon using coupon code
     * @param couponCode Unique coupon code
     * @return coupon object retrieved using [couponCode]
     */
    override fun getCouponByCode(couponCode: String): Coupon? {

        var coupon: Coupon? = null

        val query = """
            SELECT * FROM ${Coupon.TABLE_NAME}
            WHERE ${Coupon.COLUMN_COUPON_CODE} = '$couponCode'
        """.trimIndent()

        try {
            connection.createStatement()?.executeQuery(query)?.let { resultSet ->
                if (resultSet.first()) {
                    val couponCode = resultSet.getString(Coupon.COLUMN_COUPON_CODE)
                    val couponUsageType = resultSet.getInt(Coupon.COLUMN_USAGE_TYPE)
                    val couponUsageLimit = resultSet.getInt(Coupon.COLUMN_USAGE_LIMIT)

                    val startDate = resultSet.getDate(Coupon.COLUMN_START_DATE)
                    val endDate = resultSet.getDate(Coupon.COLUMN_END_DATE)
                    val discount = resultSet.getDouble(Coupon.COLUMN_DISCOUNT)

                    coupon = Coupon.from(
                        couponCode = couponCode,
                        usageType = couponUsageType,
                        usageLimit = couponUsageLimit,
                        startDate = startDate,
                        endDate = endDate,
                        discountPercentage = discount
                    )
                }

                resultSet.close()
            }
        } catch (e: Exception) {
            println("Error Occurred: ${e.message}")
        }
        return coupon
    }

    /**
     * Returns all coupons
     */
    override fun getAllCoupons(): List<Coupon> {
        val couponList = mutableListOf<Coupon>()

        val query = "SELECT * FROM ${Coupon.TABLE_NAME}"

        connection.createStatement()?.let { statement ->
            val resultSetCoupon = statement.executeQuery(query)

            while (resultSetCoupon.next()) {
                val couponCode = resultSetCoupon.getString(Coupon.COLUMN_COUPON_CODE)
                val usageType = resultSetCoupon.getInt(Coupon.COLUMN_USAGE_TYPE)
                val usageLimit = resultSetCoupon.getInt(Coupon.COLUMN_USAGE_LIMIT)
                val startDate = resultSetCoupon.getDate(Coupon.COLUMN_START_DATE)
                val endDate = resultSetCoupon.getDate(Coupon.COLUMN_END_DATE)
                val discountPercentage = resultSetCoupon.getDouble(Coupon.COLUMN_DISCOUNT)

                couponList.add(
                    Coupon.from(
                        couponCode = couponCode,
                        usageType = usageType,
                        usageLimit = usageLimit,
                        startDate = startDate,
                        endDate = endDate,
                        discountPercentage = discountPercentage
                    )
                )
            }

            resultSetCoupon.close()
            statement.close()
        }

        return couponList
    }

}