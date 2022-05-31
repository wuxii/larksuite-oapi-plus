package com.harmony.lark

import com.harmony.lark.model.ContinuouslyResult
import com.harmony.lark.model.LarkRequest
import com.harmony.lark.model.ListResult
import com.larksuite.oapi.core.Config
import com.larksuite.oapi.core.Context
import com.larksuite.oapi.core.card.Card
import com.larksuite.oapi.core.card.mode.HTTPCard
import com.larksuite.oapi.core.event.Event
import com.larksuite.oapi.core.event.model.HTTPEvent
import com.larksuite.oapi.core.model.OapiRequest
import com.larksuite.oapi.core.model.OapiResponse
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

open class LarkApi(
    val config: Config,
    var pageSize: Int = 20,
    private val eventHandler: EventHandler = EventHandler.DEFAULT,
    private val cardHandler: CardHandler = CardHandler.DEFAULT
) {

    companion object {
        internal const val CTX_LARK_API = "-----ctxLarkApi"
    }

    val appId: String = config.appSettings.appID

    private val serviceCache: MutableMap<Class<*>, Any> = ConcurrentHashMap()

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

    internal inline fun <reified T> first(
        pageSize: Int = this.pageSize,
        crossinline fn: (String?, Int) -> Any,
    ): ContinuouslyResult<T> {
        val loader = { pageToken: String? ->
            toListResult<T>(fn.invoke(pageToken, pageSize))
        }
        val first = loader.invoke(null)
        return ContinuouslyResult(first, loader)
    }

    fun GET() = httpRequest("GET")

    fun GET(path: String) = GET().setPath(path)

    fun POST() = httpRequest("POST")

    fun POST(path: String) = POST().setPath(path)

    fun DELETE() = httpRequest("DELETE")

    fun DELETE(path: String) = DELETE().setPath(path)

    fun PUT() = httpRequest("PUT")

    fun PUT(path: String) = PUT().setPath(path)

    fun PATCH() = httpRequest("PATCH")

    fun PATCH(path: String) = PATCH().setPath(path)

    private fun httpRequest(httpMethod: String) = LarkRequest(httpMethod, config)

    fun <T : Any> unwrap(serviceType: KClass<T>): T {
        return unwrap(serviceType.java)
    }

    fun <T> unwrap(serviceType: Class<T>): T {
        return serviceCache.computeIfAbsent(serviceType) {
            try {
                return@computeIfAbsent it.getConstructor(LarkApi::class.java).newInstance(this)
            } catch (e: NoSuchMethodException) {
                // is not larkApi service
            }
            try {
                return@computeIfAbsent it.getConstructor(Config::class.java).newInstance(config)
            } catch (e: NoSuchMethodException) {
                throw LarkException("$serviceType not lark service", e)
            }
        } as T
    }

    internal fun buildContext(): Context {
        val context = Context()
        config.withContext(context)
        context.set(CTX_LARK_API, this)
        return context
    }

    internal fun firstNotNullIdType(first: String?, remain: List<String>): String? {
        val id = (listOf(first) + remain).first { it != null }
        return if (id == null) null else LarkIdType.ofType(id)
    }

    private inline fun <reified T> toListResult(result: Any): ListResult<T> {
        if (result is ListResult<*>) {
            return result as ListResult<T>
        }
        if (result is Map<*, *>) {
            return toListResult(result)
        }
        return ListResult(
            hasMore = getFieldValue("hasMore", result),
            pageToken = getFieldValue("pageToken", result),
            total = getFieldValue("total", result),
            items = getFieldValue("items", result)
        )
    }

    private inline fun <reified T> toListResult(map: Map<*, *>): ListResult<T> {
        val items = map["items"] as List<T>?
        return ListResult(
            hasMore = map["has_more"] as Boolean,
            pageToken = map["page_token"] as String,
            total = (map["total"] as Int?) ?: -1,
            items = items?.toTypedArray() ?: arrayOf()
        )
    }

    private fun <T> getFieldValue(name: String, obj: Any): T {
        return obj.javaClass.getDeclaredField(name).let {
            it.isAccessible = true
            return@let it.get(obj)
        } as T
    }

}
