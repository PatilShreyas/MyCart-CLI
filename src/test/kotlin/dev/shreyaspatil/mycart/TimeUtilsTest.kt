package dev.shreyaspatil.mycart

import dev.shreyaspatil.mycart.utils.hoursDifference
import dev.shreyaspatil.mycart.utils.minus
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.sql.Date
import java.sql.Timestamp

@RunWith(JUnit4::class)
class TimeUtilsTest {

    @Test
    fun test_hours_difference() {
        val t1 = Timestamp.valueOf("2020-04-20 12:00:00")
        val t2 = Timestamp.valueOf("2020-04-20 14:00:00")

        assertThat(t1.hoursDifference(t2), equalTo(2.0))
    }

    @Test
    fun test_date_difference() {
        val t1 = Date.valueOf("2020-04-20")
        val t2 = Date.valueOf("2020-04-21")

        val expected = t1.time - t2.time

        assertThat(t1 - t2, equalTo(expected))
    }
}