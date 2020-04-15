package dev.shreyaspatil.mycart.utils

import java.sql.Date
import java.sql.Timestamp

/**
 * Returns the days difference between two dates.
 */
fun Date.daysDifference(other: Date): Long = ((other.time - time) / (24 * 60 * 60 * 1000))

/**
 * Returns hours difference between two timestamps.
 */
fun Timestamp.hoursDifference(other: Timestamp): Double = ((other.time - time) / (60 * 60 * 1000.0))

/**
 * Returns milliseconds difference
 */
operator fun Date.minus(other: Date): Long = (time - other.time)