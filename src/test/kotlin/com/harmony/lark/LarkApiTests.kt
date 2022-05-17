package com.harmony.lark

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LarkApiTests {

    @Autowired
    private lateinit var larkApi: LarkApi

    @Test
    fun test() {
        val service = larkApi.unwrap(WikiApi::class.java)
        val spaces = service.getSpaces()
        println(spaces)
    }

}
