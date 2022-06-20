package com.harmony.lark.event.msghandler

import com.harmony.lark.event.EventContext
import com.larksuite.oapi.service.im.v1.model.MessageReceiveEventData
import org.springframework.core.Ordered

interface MessageHandler : Ordered {

    fun canHandle(context: EventContext<MessageReceiveEventData>): Boolean

    fun handle(context: EventContext<MessageReceiveEventData>)

    override fun getOrder(): Int = 0

}
