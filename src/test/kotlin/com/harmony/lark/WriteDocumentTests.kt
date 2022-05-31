package com.harmony.lark

import com.google.gson.JsonObject
import com.harmony.lark.utils.BitableField
import com.harmony.lark.utils.BitableField.text
import com.harmony.lark.utils.LarkUtils
import com.larksuite.oapi.core.api.tools.OKHttps
import com.larksuite.oapi.core.utils.Jsons
import com.larksuite.oapi.okhttp3_14.Request
import com.larksuite.oapi.service.bitable.v1.model.AppTableField
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.TimeUnit

@ActiveProfiles("local")
@SpringBootTest
class WriteDocumentTests(@Autowired val larkApi: LarkApi) {

    val appToken: String = "bascnpswhT0erusXWSYde1Erok5"

    val bitableApi = larkApi.unwrap(BitableApi::class)

    @Test
    fun testCreateTable() {
        val tableId = bitableApi.createBitable(appToken, "业务编码表", listOf(text("业务名称"), text("业务编码")))
        println(tableId)
    }

    fun readApiList(): JsonObject {
        val client = OKHttps.create(30, 30, TimeUnit.SECONDS)
        val request = Request.Builder()
            .url("https://open.feishu.cn/api/tools/server-side-api/list")
            .get()
            .build()
        val response = client.newCall(request).execute()
        val responseText = response.body()!!.string()
        return Jsons.DEFAULT_GSON.fromJson(responseText, JsonObject::class.java)
    }

}
