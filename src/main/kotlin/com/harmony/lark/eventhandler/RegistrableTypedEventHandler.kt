package com.harmony.lark.eventhandler

import com.harmony.lark.LarkApi

/**
 * @author wuxin
 */
interface RegistrableTypedEventHandler<T> : RegistrableEventHandler<T>, TypedEventHandler<T> {

    override fun register(larkApi: LarkApi) {
        larkApi.setEventHandler(getEventType(), this)
    }

    override fun unregister(larkApi: LarkApi) {
        larkApi.removeEventHandler(getEventType())
    }

}
