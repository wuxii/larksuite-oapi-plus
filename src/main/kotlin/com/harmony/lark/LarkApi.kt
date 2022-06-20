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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
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

    val log: Logger = LoggerFactory.getLogger(LarkApi::class.java)

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
        if (obj is ListResult<*>) {
            return obj as ListResult<T>
        }
        val hasMore = getValue(obj, "hasMore")!!
        val pageToken = getValue(obj, "pageToken")!!
        val total = getValue(obj, "total")
        val items = getValue(obj, "items")!!
        return ListResult.simple(
            hasMore = hasMore as Boolean,
            pageToken = pageToken as String,
            total = if (total == null) -1 else total as Int,
            items = (items as Array<T>).toList()
        )
    }

    private fun getValue(obj: Any, name: String): Any? {
        try {
            val field = obj.javaClass.getDeclaredField(name)
            field.isAccessible = true
            return field.get(obj)
        } catch (e: java.lang.Exception) {
            return null
        }
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
            return serviceType.getConstructor(LarkApi::class.java).newInstance(this)
        } catch (e: Exception) {
            // ignore
        }
        try {
            return serviceType.getConstructor(Config::class.java).newInstance(config)
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

}
