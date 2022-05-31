package com.harmony.lark

import com.larksuite.oapi.core.utils.Jsons
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LarkApiTests(@Autowired val larkApi: LarkApi) {

    val wikiApi = larkApi.unwrap(WikiApi::class)

    @Test
    fun test() {
        val nodes = wikiApi.getChildNodes("7008734914229338115")
        println(Jsons.DEFAULT_GSON.toJson(nodes))
    }

    @Test
    fun testGetWikiContent() {
        wikiApi.getWikiContent("wikcn2ExVnA9YMTd2KMiGldsqJB")
    }

}
