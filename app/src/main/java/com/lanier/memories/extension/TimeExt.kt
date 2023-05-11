package com.lanier.memories

import java.util.Calendar

/**
 * Author: Turtledove
 * Date  : on 2023/5/9
 * Desc  :
 */
fun Long.toTime(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val h = calendar.get(Calendar.HOUR_OF_DAY)
    val m = calendar.get(Calendar.MINUTE)
    return "$year/$month/$day $h:$m"
}
