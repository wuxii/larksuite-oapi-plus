package com.harmony.lark.spring.autoconfig;

import com.harmony.lark.LarkApi;
import com.harmony.lark.LarkApiBuilder;
import com.harmony.lark.event.handler.RegistrableEventHandler;
import com.harmony.lark.event.msghandler.MessageHandler;
import com.harmony.lark.event.msghandler.MessageReceiveEventHandler;
import com.larksuite.oapi.core.DefaultStore;
import com.larksuite.oapi.core.IStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuxin
 */
@Configuration
@ConditionalOnProperty({"lark.app-id", "lark.app-secret"})
@EnableConfigurationProperties(LarkApiProperties.class)
public class LarkApiAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LarkApiAutoConfiguration.class);
    private final LarkApiProperties larkApiProperties;

    public LarkApiAutoConfiguration(LarkApiProperties larkApiProperties) {
        this.larkApiProperties = larkApiProperties;
    }

    @Bean
    public LarkApi larkApi(
        @Autowired(required = false) IStore store,
        @Autowired(required = false) com.larksuite.oapi.core.event.handler.Handler eventHandler,
        @Autowired(required = false) com.larksuite.oapi.core.card.handler.Handler cardHandler
    ) {
        LarkApiBuilder builder = new LarkApiBuilder();
        builder.setAppId(larkApiProperties.getAppId());
        builder.setAppSecret(larkApiProperties.getAppSecret());
        builder.setAppType(larkApiProperties.getAppType());
        builder.setDomain(larkApiProperties.getDomain());
        builder.setEncryptKey(larkApiProperties.getEncryptKey());
        builder.setVerificationToken(larkApiProperties.getVerificationToken());
        builder.setPageSize(larkApiProperties.getPageSize());
        builder.setStore(store == null ? new DefaultStore() : store);
        if (cardHandler != null) {
            builder.setCardHandler(cardHandler);
        }
        if (eventHandler != null) {
            builder.setEventHandler(eventHandler);
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(LarkApiConfigSupport.class)
    public LarkApiConfigSupport larkApiConfigSupport(LarkApi larkApi, ObjectProvider<RegistrableEventHandler<?>> eventHandlers) {
        return new LarkApiConfigSupport(larkApi, eventHandlers);
    }

    @Bean
    @ConditionalOnBean(MessageHandler.class)
    @ConditionalOnMissingBean(MessageReceiveEventHandler.class)
    public MessageReceiveEventHandler messageReceiveEventHandler(ObjectProvider<MessageHandler> messageHandlers) {
        List<MessageHandler> handlers = messageHandlers.orderedStream().collect(Collectors.toList());
        log.info("Create default lark message receive event handler with {}", handlers);
        return new MessageReceiveEventHandler(handlers);
    }

}
