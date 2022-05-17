package com.harmony.lark.spring.autoconfig

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * @author wuxin
 */
@ConstructorBinding
@ConfigurationProperties("lark")
data class LarkApiProperties(
    val appId: String,
    val appSecret: String,
    val encryptKey: String?,
    val verificationToken: String = "",
    val pageSize: Int = 20
)
