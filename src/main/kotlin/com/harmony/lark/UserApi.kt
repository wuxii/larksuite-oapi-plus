package com.harmony.lark

import com.larksuite.oapi.service.contact.v3.ContactService
import com.larksuite.oapi.service.contact.v3.model.User

class UserApi(val larkApi: LarkApi) {

    private val contactService: ContactService = larkApi.unwrap(ContactService::class.java)

    /**
     * 获取人员信息
     */
    fun getUser(userId: String, userIdType: String = LarkIdType.ofType(userId)): User {
        return contactService.users.get()
            .setUserIdType(userIdType)
            .setUserId(userId)
            .execute()
            .ensureData { LarkException("获取人员信息失败", it) }
            .user
    }

}
