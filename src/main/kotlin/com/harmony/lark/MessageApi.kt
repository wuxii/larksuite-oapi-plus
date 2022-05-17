package com.harmony.lark

import com.larksuite.oapi.service.im.v1.ImService
import com.larksuite.oapi.service.im.v1.model.Message
import com.larksuite.oapi.service.im.v1.model.MessageCreateReqBody
import com.larksuite.oapi.service.im.v1.model.MessageReplyReqBody

class MessageApi(val larkApi: LarkApi) {

    private val imService: ImService = larkApi.unwrap(ImService::class.java)

    /**
     * 发送文本消息
     */
    fun sendText(
        receiveId: String,
        content: String,
        receiveIdType: String = LarkIdType.ofType(receiveId)
    ): Message = sendMessage(receiveId, MessageContent.ofText(content), receiveIdType)

    /**
     * 发送消息
     */
    fun sendMessage(
        receiveId: String,
        content: MessageContent,
        receiveIdType: String = LarkIdType.ofType(receiveId)
    ): Message {
        val body = MessageCreateReqBody()
        body.receiveId = receiveId
        body.content = content.content
        body.msgType = content.msgType
        return imService.messages.create(body)
            .setReceiveIdType(receiveIdType)
            .execute()
            .ensureData { LarkException("发送飞书消息失败", it) }
    }

    /**
     * 回复文本消息
     */
    fun replyText(messageId: String, content: String): Message {
        return replyMessage(messageId, MessageContent.ofText(content))
    }

    /**
     * 回复消息
     */
    fun replyMessage(messageId: String, body: MessageContent): Message {
        val content = MessageReplyReqBody()
        content.msgType = body.msgType
        content.content = body.content
        return imService.messages.reply(content)
            .setMessageId(messageId)
            .execute()
            .ensureData { LarkException("回复消息失败", it) }
    }

}
