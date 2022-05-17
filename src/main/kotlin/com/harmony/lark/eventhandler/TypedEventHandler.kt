package com.harmony.lark.eventhandler

import com.larksuite.oapi.core.event.IHandler

/**
 * @author wuxin
 */
interface TypedEventHandler<T> : IHandler<T> {

    fun getEventType(): String

}
