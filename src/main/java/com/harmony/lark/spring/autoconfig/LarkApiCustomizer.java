package com.harmony.lark.spring.autoconfig;

import com.harmony.lark.LarkApi;
import com.harmony.lark.eventhandler.RegistrableEventHandler;
import com.harmony.lark.eventhandler.TypedEventHandler;

/**
 * @author wuxin
 */
public interface LarkApiCustomizer {

    default void addEventHandlers(EventHandlerRegistry registry) {
    }

    default void addCardHandlers(CardHandlerRegistry registry) {
    }

    class CardHandlerRegistry {

        private final LarkApi larkApi;

        public CardHandlerRegistry(LarkApi larkApi) {
            this.larkApi = larkApi;
        }

        public void registryHandler(com.larksuite.oapi.core.card.IHandler handler) {
            larkApi.setCardHandler(handler);
        }

    }

    class EventHandlerRegistry {

        private final LarkApi larkApi;

        public EventHandlerRegistry(LarkApi larkApi) {
            this.larkApi = larkApi;
        }

        public void registerHandler(RegistrableEventHandler<?> handler) {
            handler.register(larkApi);
        }

        public void registerHandler(TypedEventHandler<?> handler) {
            larkApi.setEventHandler(handler.getEventType(), handler);
        }

        public void registerHandler(String eventType, com.larksuite.oapi.core.event.IHandler<?> handler) {
            larkApi.setEventHandler(eventType, handler);
        }

    }

}
