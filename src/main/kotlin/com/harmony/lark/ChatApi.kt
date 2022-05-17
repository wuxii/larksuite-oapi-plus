package com.harmony.lark

import com.harmony.lark.model.ContinuouslyListResult
import com.larksuite.oapi.service.im.v1.ImService
import com.larksuite.oapi.service.im.v1.model.ChatCreateResult
import com.larksuite.oapi.service.im.v1.model.ListChat

class ChatApi(val larkApi: LarkApi) {

    private val imService = larkApi.unwrap(ImService::class.java)

    fun createChat(
        name: String,
        description: String? = null,
        userIds: List<String> = listOf(),
        userIdType: String? = larkApi.firstNotNullIdType(null, userIds),
        setBotManager: Boolean = false
    ): ChatCreateResult {
        val body = ChatCreateBody()
        body.name = name
        body.description = description
        body.userIds = userIds
        return createChat(body, userIdType, setBotManager)
    }

    /**
     * 创建会话
     */
    fun createChat(
        body: ChatCreateBody,
        userIdType: String? = larkApi.firstNotNullIdType(body.ownerId, body.userIds),
        setBotManager: Boolean = false
    ): ChatCreateResult {
        return imService.chats.create(body)
            .setUserIdType(userIdType)
            .addQueryParam("set_bot_manager" to setBotManager)
            .execute()
            .ensureData { LarkException("创建会话失败", it) }
    }

    /**
     * 解散会话
     */
    fun deleteChat(chatId: String) {
        imService.chats.delete()
            .setChatId(chatId)
            .execute()
            .ensureData { LarkException("解散会话失败", it) }
    }

    /**
     * 机器人所在的群
     */
    fun listChats(userIdType: String? = LarkIdType.UNION_ID.type): ContinuouslyListResult<ListChat> {
        return larkApi.first { pageToken, pageSize ->
            imService.chats.list()
                .setUserIdType(userIdType)
                .setPageToken(pageToken)
                .setPageSize(pageSize)
                .execute()
                .ensureData { LarkException("获取群组列表失败", it) }
        }
    }

}
