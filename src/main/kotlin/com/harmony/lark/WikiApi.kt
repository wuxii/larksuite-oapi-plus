package com.harmony.lark

import com.harmony.lark.model.ContinuouslyResult
import com.harmony.lark.model.ListResult

class WikiApi(private val larkApi: LarkApi) {

    fun getSpaces(): ContinuouslyResult<Map<String, Any>> {
        return larkApi.first { pageToken, pageSize ->
            larkApi.GET("https://open.feishu.cn/open-apis/wiki/v2/spaces")
                .setPageParams(pageToken, pageSize)
                .execute()
                .ensureData { LarkException("获取知识空间列表失败", it) }
        }
    }

    fun getSpace(spaceId: String): Any {
        return larkApi.GET("https://open.feishu.cn/open-apis/wiki/v2/spaces/:space_id")
            .setPathVariable("space_id", spaceId)
            .execute()
            .ensureData { LarkException("获取知识空间信息失败", it) }
    }

    fun getNode(token: String): Node {
        return larkApi.GET("https://open.feishu.cn/open-apis/wiki/v2/spaces/get_node")
            .setQueryParams("token", token)
            .execute(Node::class.java)
            .ensureData { LarkException("获取节点信息失败", it) }
    }

    fun getChildNodes(spaceId: String, token: String? = null): ContinuouslyResult<Node> {
        TODO("execute type and result type convert")
        return larkApi.first { pageToken, pageSize ->
            larkApi.GET("https://open.feishu.cn/open-apis/wiki/v2/spaces/:space_id/nodes")
                .setPathVariable("space_id", spaceId)
                .setQueryParams("parent_node_token", token)
                .setPageParams(pageToken, pageSize)
                .execute(ListResult::class.java)
                .ensureData { LarkException("获取子节点信息失败", it) }
        }
    }

    fun moveNode(spaceId: String, token: String, targetParentToken: String): Map<String, Any> {
        return larkApi.POST("https://open.feishu.cn/open-apis/wiki/v2/spaces/:space_id/nodes/:node_token/move")
            .setPathVariable("space_id", spaceId)
            .setPathVariable("node_token", token)
            .setBody(mapOf("target_parent_token" to targetParentToken))
            .execute()
            .ensureData { LarkException("移动节点失败", it) }
    }

    fun getWikiContent(token: String) {

    }

}
