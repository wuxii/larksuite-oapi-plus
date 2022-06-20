package com.harmony.lark

import com.harmony.lark.com.harmony.lark.WikiApi
import com.harmony.lark.model.wiki.NodeList
import com.larksuite.oapi.core.utils.Jsons
import com.larksuite.oapi.service.bitable.v1.model.AppTableField
import com.larksuite.oapi.service.bitable.v1.model.AppTableFieldProperty
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("local")
@SpringBootTest
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

    @Test
    fun testGetMessages() {
        val service = larkApi.unwrap(MessageApi::class.java)
        val startTime = LocalDateTime.of(2022, 6, 17, 21, 2)
        val messages = service.getMessages("oc_f2f20b6df527e3ff9e5f07ba2c37a28f", startTime, LocalDateTime.now())
    }

    @Test
    fun testGetBitableFields() {
        val service = larkApi.unwrap(BitableApi::class.java)
        val fields = service.getBitableFields("bascnpswhT0erusXWSYde1Erok5", "tblDf13p6rTXo371")
        for (field in fields) {
            println(Jsons.DEFAULT_GSON.toJson(field))
        }
    }

    @Test
    fun testUpdateBitableField() {
        val service = larkApi.unwrap(BitableApi::class.java)
        val appToken = "bascnpswhT0erusXWSYde1Erok5"
        val tableId = "tblDf13p6rTXo371"
        val fieldId = "fldCo82ew2"
        val field = AppTableField()
        field.fieldName = "更新时间"
        field.type = 5
        field.property = AppTableFieldProperty().apply {
            this.dateFormat = "yyyy/MM/dd"
            this.timeFormat = "HH:mm"
            this.autoFill = true
        }
        service.updateBitableField(appToken, tableId, fieldId, field)

    }

    @Test
    fun testGetWikiNode() {
        val service = larkApi.unwrap(WikiApi::class.java)
        val node = service.getNode("wikcnXsbGoY5KyN6pPqT34RwcTW")
        println(Jsons.DEFAULT_GSON.toJson(node))
    }

}
