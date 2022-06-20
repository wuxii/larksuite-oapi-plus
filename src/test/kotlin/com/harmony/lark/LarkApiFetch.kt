package com.harmony.lark

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.harmony.lark.com.harmony.lark.WikiApi
import com.larksuite.oapi.core.api.tools.OKHttps
import com.larksuite.oapi.core.utils.Jsons
import com.larksuite.oapi.okhttp3_14.HttpUrl
import com.larksuite.oapi.okhttp3_14.Request
import com.larksuite.oapi.service.bitable.v1.model.AppTableRecord
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.TimeUnit

@ActiveProfiles("local")
@SpringBootTest
class LarkApiFetch(@Autowired val larkApi: LarkApi) {

    private val log = LoggerFactory.getLogger(LarkApiFetch::class.java)

    val wikiApi = larkApi.unwrap(WikiApi::class)
    val bitableApi = larkApi.unwrap(BitableApi::class.java)

    val appToken = "bascnpswhT0erusXWSYde1Erok5"
    val apiTableId = "tblDf13p6rTXo371"
    val paramTableId = "tblBIr0rZYGGxtXM"
    val batchNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))

    @Test
    fun writeApisToBitable() {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val records = readApis().map { api ->
            recordOf(
                mapOf(
                    "ID" to api["id"].asLong,
                    "名称" to api["name"].asString,
                    "接口说明" to api["detail"].asString,
                    "URL" to api["url"].asString.split(":")[1],
                    "HttpMethod" to api["url"].asString.split(":")[0],
                    "Tags" to api["tags"].asJsonArray.toList(),
                    "业务编码" to api["bizTag"].asString,
                    "支持APP类型" to api["supportAppTypes"].asJsonArray.toList(),
                    "排序值" to api["orderMark"].asInt,
                    "文档地址" to api["fullPath"].asString,
                    "更新时间" to sdf.format(Date(api["updateTime"].asLong * 1000)),
                    "Meta.Name" to api["meta.Name"].asString,
                    "Meta.Project" to api["meta.Project"].asString,
                    "Meta.Resource" to api["meta.Resource"].asString,
                    "Meta.Type" to api["meta.Type"].asString,
                    "Meta.Version" to api["meta.Version"].asString,
                    "批次号" to batchNo
                )
            )
        }
        writeRecords(apiTableId, records)

        records.forEach {
            try {
                writeApiMethodToBitable(it.fields)
            } catch (e: ApiNotFoundException) {
                log.warn("api not found, {}", it.fields)
            } catch (e: java.lang.Exception) {
                log.error("write fail", e)
            }
        }

    }

    fun writeApiMethodToBitable(apiRecord: Map<String, Any>) {
        val apiId = apiRecord["ID"]
        val data = readApiMethodData(
            project = apiRecord["Meta.Project"] as String,
            version = apiRecord["Meta.Version"] as String,
            resource = apiRecord["Meta.Resource"] as String,
            method = apiRecord["Meta.Name"] as String
        )["data"]
        val request = data["request"].asJsonObject

        val bodyParams = params("body", request)
        val queryParams = params("query", request)
        val pathParams = params("path", request)

        val params = bodyParams + queryParams + pathParams
        val records = params.map { p ->
            recordOf(
                mapOf(
                    "字段名称" to p["name"].asString,
                    "字段描述" to p["description"].asString,
                    "字段位置" to p["_location"].asString,
                    "字段类型" to p["type"].asString,
                    "必填" to if (p["mandatory"].asBoolean) "必填" else "否",
                    "示例值" to p["example"].asString,
                    "参数正则" to p["regexp"].asString,
                    "接口ID" to apiId,
                    "批次号" to batchNo
                )
            )
        }

        if (records.isEmpty()) {
            log.warn("api not need params: {}", apiRecord)
            return
        }

        writeRecords(paramTableId, records)
    }

    @Test
    fun testWriteApiMethodToBitable() {
        writeApiMethodToBitable(
            mapOf(
                "ID" to "6907569744329932801",
                "Meta.Name" to "v1/index",
                "Meta.Resource" to "default",
                "Meta.Project" to "oauth",
                "Meta.Version" to "old",
                "Meta.Type" to "1"
            )
        )
    }

    fun readApis() = readApiData()["data"]["apis"].asJsonArray

    fun readApiData(): JsonObject {
        val client = OKHttps.create(30, 30, TimeUnit.SECONDS)
        val request = Request.Builder()
            .url("https://open.feishu.cn/api/tools/server-side-api/list")
            .get()
            .build()
        val response = client.newCall(request).execute()
        val responseText = response.body()!!.string()
        return Jsons.DEFAULT_GSON.fromJson(responseText, JsonObject::class.java)
    }

    fun readApiMethodData(project: String, version: String, resource: String, method: String): JsonObject {
        val url = HttpUrl.get("https://open.feishu.cn/api/tools/api_explore/api_method_meta")
            .newBuilder()
            .addQueryParameter("project", project)
            .addQueryParameter("version", version)
            .addQueryParameter("resource", resource)
            .addQueryParameter("method", method)
            .build()
        val client = OKHttps.create(30, 30, TimeUnit.SECONDS)
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response = client.newCall(request).execute()
        val responseText = response.body()!!.string()
        val respObj = Jsons.DEFAULT_GSON.fromJson(responseText, JsonObject::class.java)
        if (respObj["code"].asInt != 0) {
            throw ApiNotFoundException()
        }
        return respObj
    }

    private fun params(location: String, request: JsonObject): JsonArray {
        val p = request[location]["properties"]
        if (p is JsonNull) {
            return JsonArray()
        }
        p.asJsonArray.map { it.asJsonObject.addProperty("_location", location) }
        return p.asJsonArray
    }

    private fun recordOf(valuePair: Map<String, Any?>): AppTableRecord {
        val record = AppTableRecord()
        record.fields = valuePair
        return record
    }

    operator fun JsonElement.get(name: String): JsonElement {
        var o: JsonElement? = this
        for (s in name.split(".")) {
            if (o == null) {
                return JsonNull.INSTANCE
            }
            o = o.asJsonObject[s]
        }
        return o ?: JsonNull.INSTANCE
    }

    private fun writeRecords(tableId: String, records: List<AppTableRecord>) {
        val total = records.size
        val size = 100
        var start = 0
        while (start <= total) {
            val end = (start + size).coerceAtMost(total)
            val list = records.subList(start, end)
            bitableApi.addBitableRecords(appToken, tableId, list)
            start = end + 1
        }
    }

    class ApiNotFoundException : Exception() {

    }

}
