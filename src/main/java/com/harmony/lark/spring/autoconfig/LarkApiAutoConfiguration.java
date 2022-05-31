package com.harmony.lark.spring.autoconfig;

import com.harmony.lark.LarkApi;
import com.harmony.lark.LarkApiBuilder;
import com.larksuite.oapi.core.DefaultStore;
import com.larksuite.oapi.core.IStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

/**
 * @author wuxin
 */
@Configuration
@ConditionalOnProperty({"lark.app-id", "lark.app-secret"})
@EnableConfigurationProperties(LarkApiProperties.class)
public class LarkApiAutoConfiguration {

    private final LarkApiProperties larkApiProperties;

    public LarkApiAutoConfiguration(LarkApiProperties larkApiProperties) {
        this.larkApiProperties = larkApiProperties;
    }

    @Bean
    public LarkApi larkApi(
        @Autowired(required = false) IStore store,
        @Autowired(required = false) com.larksuite.oapi.core.event.handler.Handler eventHandler,
        @Autowired(required = false) com.larksuite.oapi.core.card.handler.Handler cardHandler,
        ObjectProvider<LarkApiCustomizer> customizers
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
        return buildCustomizers(customizers).customize(builder.build());
    }

    private LarkApiCustomizers buildCustomizers(ObjectProvider<LarkApiCustomizer> customizers) {
        return new LarkApiCustomizers(customizers.orderedStream().collect(Collectors.toList()));
    }
}
