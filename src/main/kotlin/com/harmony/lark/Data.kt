package com.harmony.lark

import com.google.gson.annotations.SerializedName
import com.harmony.lark.model.ListResult
import com.larksuite.oapi.core.utils.Jsons
import com.larksuite.oapi.service.im.v1.model.ChatCreateReqBody

data class BitableRecordFilter(
    val viewId: String? = null,
    val filter: String? = null,
    val sort: String? = null,
    val fieldNames: List<String> = listOf(),
    val textFieldAsArray: Boolean = true,
)

data class MessageContent(
    @SerializedName("content") val content: String,
    @SerializedName("msg_type") val msgType: String,
) {
    companion object {

        fun ofText(text: String) = MessageContent(ofContent(mapOf("text" to text)), "text")

        private fun ofContent(content: Any): String {
            return Jsons.DEFAULT_GSON.toJson(content)
        }

    }
}

data class ChatCreateBody(
    @SerializedName("user_id_list") var userIds: List<String> = listOf(),
    @SerializedName("bot_id_list") var botIds: List<String> = listOf(),
) : ChatCreateReqBody()

/**
 * 飞书审批实例
 */
data class ApprovalInstance(
    @SerializedName("approval_code") var approvalCode: String? = null,
    @SerializedName("approval_name") var approvalName: String? = null,
    @SerializedName("comment_list") var comments: List<Comment>? = listOf(),
    @SerializedName("department_id") var departmentId: String? = null,
    @SerializedName("end_time") var endTime: String? = null,
    @SerializedName("form") var form: String? = null,
    @SerializedName("open_id") var openId: String? = null,
    @SerializedName("serial_number") var serialNumber: String? = null,
    @SerializedName("start_time") var startTime: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("task_list") var tasks: List<Task>? = listOf(),
    @SerializedName("timeline") var timeline: List<Timeline>? = listOf(),
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("uuid") var uuid: String? = null,
)

data class Task(
    @SerializedName("id") var id: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("open_id") var openId: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("node_id") var nodeId: String? = null,
    @SerializedName("node_name") var nodeName: String? = null,
    @SerializedName("custom_node_id") var customNodeId: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("start_time") var startTime: String? = null,
    @SerializedName("end_time") var endTime: String? = null,
)

data class Timeline(
    @SerializedName("create_time") var createTime: String? = null,
    @SerializedName("ext") var ext: String? = null,
    @SerializedName("open_id") var openId: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("node_key") var nodeKey: String? = null,
    @SerializedName("cc_user_list") var ccUsers: List<Any>? = null,
    @SerializedName("open_id_list") var openIds: List<Any>? = null,
    @SerializedName("user_id_list") var userIds: List<Any>? = null,
)

data class Comment(
    @SerializedName("comment") var comment: String? = null,
    @SerializedName("create_time") var createTime: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("open_id") var openId: String? = null,
    @SerializedName("user_id") var userId: String? = null,
)

data class LarkApprovalFormItem(
    @SerializedName("ext") var ext: Any? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("value") var value: Any? = null,
    @SerializedName("open_ids") var openIds: List<String>? = null,
    @SerializedName("option") var option: Option? = null,
)

data class Option(
    @SerializedName("key") var key: String? = null,
    @SerializedName("text") var text: String? = null,
)

/**
 * 审批任务同意
 */
data class ApprovalInstanceApproveBody(
    @SerializedName("approval_code") var approvalCode: String? = null,
    @SerializedName("instance_code") var instanceCode: String? = null,
    @SerializedName("open_id") var openId: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("task_id") var taskId: String? = null,
    @SerializedName("comment") var comment: String? = null,
)

/**
 * 退回审批
 */
data class ApprovalInstanceRollbackBody(
    @SerializedName("task_id") var taskId: String,
    @SerializedName("user_id") var userId: String,
    @SerializedName("reason") var reason: String = "",
    @SerializedName("task_def_key_list") var taskDefKeys: List<String> = listOf(),
)

data class BitableAddress(
    val address: String,
    val appToken: String,
    val table: String,
    val view: String,
)

data class Space(
    var name: String?,
    var description: String?,
    var spaceId: String?,
    var spaceType: String?,
    var visibility: String?,
)

data class Node(
    var title: String? = null,
    var nodeToken: String? = null,
    var nodeType: String? = null,
    var hasChild: Boolean? = null,
    var spaceId: String? = null,
    var objToken: String? = null,
    var objType: String? = null,
    var objCreateTime: String? = null,
    var objEditTime: String? = null,
    var originNodeToken: String? = null,
    var originSpaceId: String? = null,
    var parentNodeToken: String? = null,
    var nodeCreateTime: String? = null,
)

// data class BitableTable()
