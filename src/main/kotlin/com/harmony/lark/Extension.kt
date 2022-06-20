package com.harmony.lark

import com.larksuite.oapi.core.Config
import com.larksuite.oapi.core.Context
import com.larksuite.oapi.core.api.AccessTokenType
import com.larksuite.oapi.core.api.ReqCaller
import com.larksuite.oapi.core.api.request.Request
import com.larksuite.oapi.core.api.request.RequestOptFn
import com.larksuite.oapi.core.api.response.Response
import com.larksuite.oapi.core.model.OapiRequest
import com.larksuite.oapi.core.utils.Servlets
import com.larksuite.oapi.service.im.v1.ImService
import com.larksuite.oapi.service.im.v1.model.MessageReceiveEventData
import lombok.SneakyThrows
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

typealias EventHandler = com.larksuite.oapi.core.event.handler.Handler

typealias CardHandler = com.larksuite.oapi.core.card.handler.Handler

typealias IEventHandler<T> = com.larksuite.oapi.core.event.IHandler<T>

typealias ICardHandler = com.larksuite.oapi.core.card.IHandler

fun <IN, OUT> ReqCaller<IN, OUT>.getQueryParams(): MutableMap<String, Any> =
    this.javaClass.getDeclaredField("queryParams")
        .let {
            it.isAccessible = true
            return@let it.get(this) as MutableMap<String, Any>
        }

fun ImService.ChatCreateReqCall.addQueryParam(pair: Pair<String, Any>): ImService.ChatCreateReqCall {
    this.getQueryParams()[pair.first] = pair.second
    return this
}

fun <DATA> Response<DATA>.isHttpOk(): Boolean = (this.httpStatusCode == 200)

fun <DATA> Response<DATA>.isOk(): Boolean = this.isHttpOk() && this.code == 0

private val DEFAULT_ERROR: (resp: Response<*>) -> Throwable = { LarkException("feishu error", it) }

/**
 * @param throwable 当验证响应不正常后可通过预设的异常进行抛出信息
 */
fun <DATA> Response<DATA>.ensureOk(throwable: (resp: Response<DATA>) -> Throwable = DEFAULT_ERROR) {
    if (!this.isOk()) {
        throw throwable.invoke(this)
    }
}

/**
 * 确保响应正常并获取响应结果
 * @see Response.ensureOk
 */
fun <DATA> Response<DATA>.ensureData(throwable: (resp: Response<DATA>) -> Throwable = DEFAULT_ERROR): DATA {
    ensureOk(throwable)
    return this.data
}

fun <IN, OUT> newRequest(
    httpPath: String,
    httpMethod: String,
    accessTokenType: List<AccessTokenType> = listOf(AccessTokenType.Tenant),
    body: IN,
    result: OUT,
    vararg requestOptFn: RequestOptFn,
): Request<IN, OUT> =
    Request.newRequest(httpPath, httpMethod, accessTokenType.toTypedArray(), body, result, *requestOptFn)

fun Context.getConfig(): Config? {
    return Config.ByCtx(this)
}

fun Context.getLarkApi(): LarkApi? {
    return this.get(LarkApi.CTX_LARK_API) as LarkApi?
}

fun <T> Context.get(requireType: Class<T>): T? {
    return this.get(requireType.name) as T?
}

fun MessageReceiveEventData.isTextMessage(): Boolean {
    return this.message.messageType == "text"
}

fun MessageReceiveEventData.isGroupMessage(): Boolean {
    return this.message.chatType == "group"
}

fun MessageReceiveEventData.isPrivateMessage(): Boolean {
    return this.message.messageType == "p2p"
}

fun MessageReceiveEventData.getTextMessageContent(): String {
    if (!this.isTextMessage()) {
        throw LarkException("message type `!=` text")
    }
    return this.message.content
}

fun MessageReceiveEventData.getTextMessageDisplayContent(): String {
    var content = getTextMessageContent()
    val mentions = this.message.mentions
    if (mentions?.isNotEmpty() == true) {
        for (mention in mentions) {
            content = content.replace(mention.key, "@${mention.name}")
        }
    }
    return content
}

fun MessageReceiveEventData.getTextMessageTrimContent(): String {
    var content = getTextMessageContent()
    val mentions = this.message.mentions
    if (mentions?.isNotEmpty() == true) {
        for (mention in mentions) {
            content = content.replace(mention.key, "")
        }
    }
    return content
}

fun MessageReceiveEventData.isTextMessageContentStartsWith(prefix: String): Boolean {
    if (!this.isTextMessage()) {
        return false
    }
    return getTextMessageTrimContent().startsWith(prefix)
}

fun MessageReceiveEventData.getMessageId(): String {
    return this.message.messageId
}

fun MessageReceiveEventData.getChatId(): String {
    return this.message.chatId
}

@SneakyThrows(IOException::class)
fun LarkApi.dispatchEvent(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse) {
    val oapiRequest = Servlets.toRequest(httpRequest)
    val oapiResponse = this.dispatchEvent(oapiRequest)
    Servlets.writeResponse(oapiResponse, httpResponse)
    this.log.info("Dispatch lark event $oapiRequest and reply $oapiResponse")
}
