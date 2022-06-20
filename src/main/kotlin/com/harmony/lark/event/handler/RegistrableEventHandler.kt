package com.harmony.lark.event.handler

import com.harmony.lark.LarkApi
import com.larksuite.oapi.core.Context
import com.larksuite.oapi.core.event.IHandler

/**
 * 飞书事件处理
 * @author wuxin
 */
interface RegistrableEventHandler<T> : IHandler<T> {

    override fun Handle(context: Context, event: T) {
        handle(context, event)
    }

    fun handle(context: Context, event: T)

    /**
     * 将本 handler 注册到 api 中
     *
     * @param larkApi lark api
     */
    fun register(larkApi: LarkApi)

    /**
     * 将 handler 解除注册
     *
     * @param larkApi lark api
     */
    fun unregister(larkApi: LarkApi) {
        val eventHandlers = IHandler.Hub.appID2EventType2Handler[larkApi.appId]
        eventHandlers?.remove(eventHandlers.entries.first { it.value == this }.key)
    }

}
