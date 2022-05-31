package com.harmony.lark.spring.autoconfig;

import com.harmony.lark.LarkApi;
import com.harmony.lark.spring.autoconfig.LarkApiCustomizer.CardHandlerRegistry;
import com.harmony.lark.spring.autoconfig.LarkApiCustomizer.EventHandlerRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxin
 */
class LarkApiCustomizers {

    private final List<LarkApiCustomizer> customizers;

    public LarkApiCustomizers(List<LarkApiCustomizer> customizers) {
        this.customizers = new ArrayList<>(customizers);
    }

    public LarkApi customize(LarkApi larkApi) {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry(larkApi);
        CardHandlerRegistry cardHandlerRegistry = new CardHandlerRegistry(larkApi);
        for (LarkApiCustomizer customizer : customizers) {
            customizer.addEventHandlers(eventHandlerRegistry);
            customizer.addCardHandlers(cardHandlerRegistry);
        }
        return larkApi;
    }

}
