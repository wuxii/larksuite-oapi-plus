package com.harmony.lark.event.msghandler

import com.harmony.lark.LarkApi
import com.harmony.lark.event.EventContext
import com.harmony.lark.event.handler.RegistrableEventHandler
import com.larksuite.oapi.core.Context
import com.larksuite.oapi.service.im.v1.ImService
import com.larksuite.oapi.service.im.v1.model.MessageReceiveEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MessageReceiveEventHandler(private val messageHandlers: List<MessageHandler>) :
    RegistrableEventHandler<MessageReceiveEvent>,
    ImService.MessageReceiveEventHandler() {

    private val log: Logger = LoggerFactory.getLogger(MessageReceiveEventHandler::class.java)

    override fun handle(context: Context, event: MessageReceiveEvent) {
        val eventContext = EventContext(context, event.event)
        val handler = messageHandlers.firstOrNull { it.canHandle(eventContext) }
        log.info("handle message event by {}", handler)
        handler?.handle(eventContext)
    }

    override fun register(larkApi: LarkApi) {
        larkApi.unwrap(ImService::class).setMessageReceiveEventHandler(this)
    }

}
