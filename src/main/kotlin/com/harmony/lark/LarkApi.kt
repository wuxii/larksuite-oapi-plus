package com.harmony.lark

import com.harmony.lark.model.ContinuouslyResult
import com.harmony.lark.model.ListResult
import com.larksuite.oapi.core.Config
import com.larksuite.oapi.core.Context
import com.larksuite.oapi.core.card.Card
import com.larksuite.oapi.core.card.mode.HTTPCard
import com.larksuite.oapi.core.event.Event
import com.larksuite.oapi.core.event.model.HTTPEvent
import com.larksuite.oapi.core.model.OapiRequest
import com.larksuite.oapi.core.model.OapiResponse
import com.larksuite.oapi.core.utils.Jsons
import kotlin.reflect.KClass

open class LarkApi(
    val config: Config,
    var pageSize: Int = 20,
    private val eventHandler: EventHandler = EventHandler.DEFAULT,
    private val cardHandler: CardHandler = CardHandler.DEFAULT,
) {

    companion object {
        const val CTX_LARK_API = "-----ctxLarkApi"
    }

    val appId: String = config.appSettings.appID

    fun setEventHandler(eventType: String, handler: IEventHandler<*>) {
        Event.setTypeHandler(config, eventType, handler)
    }

    fun removeEventHandler(eventType: String) {
        Event.setTypeHandler(config, eventType, null as IEventHandler<*>?);
    }

    fun setCardHandler(handler: ICardHandler) {
        Card.setHandler(config, handler)
    }

    fun removeCardHandler() {
        Card.setHandler(config, null)
    }

    fun dispatchCard(request: OapiRequest): OapiResponse {
        val card = HTTPCard(request, OapiResponse())
        cardHandler.handle(buildContext(), card)
        return card.response
    }

    fun dispatchEvent(request: OapiRequest): OapiResponse {
        val event = HTTPEvent(request, OapiResponse())
        eventHandler.handle(buildContext(), event)
        return event.response
    }

    internal fun <T> first(pageSize: Int = this.pageSize, fn: (String?, Int) -> Any): ContinuouslyResult<T> {
        val loader = { pageToken: String? -> toListResult<T>(fn.invoke(pageToken, pageSize)) }
        return ContinuouslyResult(loader.invoke(null), loader)
    }

    private fun <T> toListResult(obj: Any): ListResult<T> {
        obj.javaClass.getDeclaredField("items")
        obj.javaClass.getDeclaredField("pageToken")
        obj.javaClass.getDeclaredField("hasMore")
        obj.javaClass.getDeclaredField("total")
        return null!!
    }

    fun GET() = httpRequest("GET")

    fun POST() = httpRequest("POST")

    fun DELETE() = httpRequest("DELETE")

    fun PUT() = httpRequest("PUT")

    fun PATCH() = httpRequest("PATCH")

    private fun httpRequest(httpMethod: String) = LarkRequest(httpMethod, config)

    fun <T : Any> unwrap(serviceType: KClass<T>): T {
        return unwrap(serviceType.java)
    }

    fun <T> unwrap(serviceType: Class<T>): T {
        try {
            serviceType.getConstructor(LarkApi::class.java).newInstance(this)
        } catch (e: Exception) {
            // ignore
        }
        try {
            return serviceType.getConstructor(Config::class.java).newInstance(this)
        } catch (e: Exception) {
            throw LarkException("$serviceType not lark service", e)
        }
    }

    private fun buildContext(): Context {
        val context = Context()
        config.withContext(context)
        context.set(CTX_LARK_API, this)
        return context
    }

    internal fun firstNotNullIdType(first: String?, remain: List<String>): String? {
        val id = (listOf(first) + remain).first { it != null }
        return if (id == null) null else LarkIdType.ofType(id)
    }

    class ListResultAdapter<T> : ListResult<T> {

    }

}
