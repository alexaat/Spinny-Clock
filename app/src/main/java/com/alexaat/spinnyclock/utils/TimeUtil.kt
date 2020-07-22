package com.alexaat.spinnyclock.utils

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.hours


fun addMinute(hours:Int,minutes:Int):Calendar{

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY,hours)
    calendar.set(Calendar.MINUTE,minutes)
    calendar.add(Calendar.MINUTE,1)
    return calendar

}

fun subtractMinute(hours:Int,minutes:Int):Calendar{
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY,hours)
    calendar.set(Calendar.MINUTE,minutes)
    calendar.add(Calendar.MINUTE,-1)
    return calendar

}