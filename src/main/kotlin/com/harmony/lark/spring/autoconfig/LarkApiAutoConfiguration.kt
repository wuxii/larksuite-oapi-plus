package com.harmony.lark.spring.autoconfig

import com.harmony.lark.LarkApi
import com.harmony.lark.LarkApiBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author wuxin
 */
@ConditionalOnProperty(value = ["lark.app-id", "lark.app-secret"])
@Configuration
@EnableConfigurationProperties(LarkApiProperties::class)
open class LarkApiAutoConfiguration(private val larkProperties: LarkApiProperties) {

    @Bean
    open fun larkApi(): LarkApi {
        return LarkApiBuilder()
            .apply {
                appId = larkProperties.appId
                appSecret = larkProperties.appSecret
                encryptKey = larkProperties.encryptKey
                verificationToken = larkProperties.verificationToken
                pageSize = larkProperties.pageSize
            }
            .build()
    }

}
