package com.harmony.lark

import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.concurrent.TimeUnit

fun main() {
    val date = Date()
    val instant = date.toInstant()
    println(instant.nano)
    println(instant.epochSecond)

    instant.toEpochMilli()
    instant.epochSecond

    val unit = TimeUnit.SECONDS

    unit.toMillis(instant.toEpochMilli())
}
