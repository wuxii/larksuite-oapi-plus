package com.harmony.lark.spring.autoconfig;

import com.harmony.lark.LarkApi;
import com.harmony.lark.event.handler.RegistrableEventHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;

/**
 * @author wuxin
 */
@RequiredArgsConstructor
public class LarkApiConfigSupport implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(LarkApiConfigSupport.class);

    private final LarkApi larkApi;

    private final ObjectProvider<RegistrableEventHandler<?>> registrableEventHandlers;

    @Override
    public void afterPropertiesSet() throws Exception {
        registrableEventHandlers
            .orderedStream()
            .forEach(handler -> {
                log.info("Registry lark event handler {}", handler);
                handler.register(larkApi);
            });
    }

}
