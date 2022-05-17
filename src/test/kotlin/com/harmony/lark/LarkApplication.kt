package com.harmony.lark

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LarkApplication

fun main(args: Array<String>) {
    runApplication<LarkApplication>(*args)
}
