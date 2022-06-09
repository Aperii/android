package com.aperii.utilities.time

import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    private val prettyTime = PrettyTime()

    fun getShortDateString(dateTimeSeconds: Long): String = SimpleDateFormat("MMMM yyyy", Locale.US).format(Date(dateTimeSeconds))

    fun getShortTimeString(dateTimeSeconds: Long): String {
        if(Calendar.getInstance().timeInMillis - dateTimeSeconds > 1000 * 60 * 60 * 24 * 2) {
            return SimpleDateFormat("MMM dd", Locale.US).format(Date(dateTimeSeconds))
        }
        return prettyTime.formatDuration(Date(dateTimeSeconds))
    }

    fun getLongTimeString(dateTimeSeconds: Long): String = SimpleDateFormat("MM/dd/yyyy h:mma", Locale.US).format(Date(dateTimeSeconds))

}