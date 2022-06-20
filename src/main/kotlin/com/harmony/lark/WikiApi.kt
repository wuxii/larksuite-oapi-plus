package com.harmony.lark.com.harmony.lark

import com.harmony.lark.LarkApi
import com.harmony.lark.Node
import com.harmony.lark.ensureData

class WikiApi(private val larkApi: LarkApi) {

    fun getNode(token: String): Node {
        return larkApi.GET()
            .setPath("https://open.feishu.cn/open-apis/wiki/v2/spaces/get_node")
            .setQueryParams("token", token)
            .execute(Node::class.java)
            .ensureData()
    }

}
