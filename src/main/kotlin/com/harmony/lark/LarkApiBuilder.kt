package com.harmony.lark

import com.larksuite.oapi.core.*

/**
 * @author wuxin
 */
class LarkApiBuilder {

    var domain: Domain = Domain.FeiShu

    var appType: AppType = AppType.Internal
    lateinit var appId: String
    lateinit var appSecret: String

    var encryptKey: String? = null
    lateinit var verificationToken: String

    var store: IStore = DefaultStore()
    var pageSize: Int = 20

    var eventHandler = EventHandler.DEFAULT
    var cardHandler = CardHandler.DEFAULT

    fun build(): LarkApi {
        val appSettings = AppSettings(appType, appId, appSecret, verificationToken, encryptKey)
        return LarkApi(Config(domain, appSettings, store), pageSize)
    }

}
