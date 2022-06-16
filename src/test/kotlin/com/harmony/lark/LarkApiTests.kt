package com.harmony.lark

import com.harmony.lark.model.wiki.NodeList
import com.larksuite.oapi.core.utils.Jsons
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("local")
@SpringBootTest(properties = ["debug=true"])
class LarkApiTests {

    @Autowired
    lateinit var larkApi: LarkApi

    @Test
    fun test() {
        val data = larkApi.GET()
            .setPath("https://open.feishu.cn/open-apis/wiki/v2/spaces/:space_id/nodes")
            .setPathVariable("space_id", "7008734914229338115")
            .setQueryParams("parent_node_token", "wikcnXdNHMcfzgDNVc0gDJ5689Y")
            .setPageParams(null, 10)
            .execute(NodeList::class.java)
            .ensureData()
        println(Jsons.DEFAULT_GSON.toJson(data))
    }

}
