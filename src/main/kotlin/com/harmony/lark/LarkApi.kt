package com.harmony.lark

import com.harmony.lark.model.ContinuouslyListResult
import com.harmony.lark.model.LarkRequest
import com.harmony.lark.model.ListResult
import com.larksuite.oapi.core.Config
import com.larksuite.oapi.core.Context
import com.larksuite.oapi.core.event.Event
import com.larksuite.oapi.core.event.model.HTTPEvent
import com.larksuite.oapi.core.model.OapiRequest
import com.larksuite.oapi.core.model.OapiResponse
import com.larksuite.oapi.service.contact.v3.ContactService
import com.larksuite.oapi.service.im.v1.ImService
import com.larksuite.oapi.service.im.v1.model.*

open class LarkApi(
    val config: Config,
    var pageSize: Int = 20,
    private val eventHandler: EventHandler = EventHandler.DEFAULT
) {

    companion object {
        internal const val CTX_LARK_API = "-----ctxLarkApi"
    }

    val appId: String = config.appSettings.appID

    private val serviceCache: MutableMap<Class<*>, Any> = mutableMapOf()

    fun setEventHandler(eventType: String, handler: IEventHandler<*>) {
        Event.setTypeHandler(config, eventType, handler)
    }

    fun removeEventHandler(eventType: String) {
        Event.setTypeHandler(config, eventType, null as IEventHandler<*>?);
    }

    fun dispatchEvent(request: OapiRequest): OapiResponse {
        val event = HTTPEvent(request, OapiResponse())
        eventHandler.handle(buildContext(), event)
        return event.response
    }

    internal fun <T> first(fn: (String?, Int) -> Any): ContinuouslyListResult<T> {
        val size = this.pageSize
        val loader = { pageToken: String? ->
            toListResult<T>(fn.invoke(pageToken, size))
        }
        val first = loader.invoke(null)
        return ContinuouslyListResult(first, loader)
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

    fun <T> unwrap(serviceType: Class<T>): T {
        return serviceCache.computeIfAbsent(serviceType) {
            try {
                return@computeIfAbsent it.getConstructor(LarkApi::class.java).newInstance(this)
            } catch (e: java.util.NoSuchElementException) {
                // is not larkApi service
            }

            try {
                return@computeIfAbsent it.getConstructor(Config::class.java).newInstance(config)
            } catch (e: java.util.NoSuchElementException) {
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

    internal fun <T> toListResult(result: Any): ListResult<T> {
        if (result is ListResult<*>) {
            return result as ListResult<T>
        }
        return ListResult(
            hasMore = getFieldValue("hasMore", result),
            pageToken = getFieldValue("pageToken", result),
            total = getFieldValue("total", result),
            items = getFieldValue("items", result)
        )
    }

    private fun <T> getFieldValue(name: String, obj: Any): T {
        return obj.javaClass.getDeclaredField(name).let {
            it.isAccessible = true
            return@let it.get(obj)
        } as T
    }

}
