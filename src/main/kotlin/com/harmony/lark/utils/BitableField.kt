package com.harmony.lark.utils

import com.harmony.lark.BitableFieldType
import com.larksuite.oapi.service.bitable.v1.model.AppTableField
import com.larksuite.oapi.service.bitable.v1.model.AppTableFieldProperty
import com.larksuite.oapi.service.bitable.v1.model.AppTableFieldPropertyOption

object BitableField {

    // 文本
    fun text(name: String): AppTableField {
        return newBuilder(BitableFieldType.TEXT)
            .apply {
                this.fieldName = name
            }
            .build()
    }

    // 数值
    fun number(name: String, formatter: String = "0"): AppTableField {
        return newBuilder(BitableFieldType.NUMBER)
            .apply {
                this.fieldName = name
                this.formatter = formatter
            }
            .build()
    }

    // 选择
    fun select(
        name: String,
        multiple: Boolean = false,
        vararg options: AppTableFieldPropertyOption = arrayOf(),
    ): AppTableField {
        return newBuilder(if (multiple) BitableFieldType.MULTI_SELECT else BitableFieldType.SINGLE_SELECT)
            .apply {
                this.fieldName = name
                this.options.addAll(options)
            }
            .build()
    }

    // 时间
    fun dateTime(name: String): AppTableField {
        return newBuilder(BitableFieldType.DATE_TIME)
            .apply {
                this.fieldName = name
            }
            .build()
    }

    // 复选框
    fun checkbox() {}

    // 人员
    fun user() {}

    // 链接
    fun link() {}

    // 附件
    fun attachment() {}

    // 公式
    fun formula() {}

    // 关联
    fun association() {}

    fun createdAt(name: String, autoFill: Boolean = true) {}

    fun createdBy(name: String, autoFill: Boolean = true) {}

    fun updatedAt(name: String, autoFill: Boolean = true) {}

    fun updatedBy(name: String, autoFill: Boolean = true) {}

    fun newBuilder(filedType: Int): BitableFieldBuilder {
        return BitableFieldBuilder(filedType)
    }

    fun newBuilder(filedType: BitableFieldType): BitableFieldBuilder {
        return BitableFieldBuilder(filedType.value)
    }

    class BitableFieldBuilder(private val fieldType: Int) {

        var fieldName: String? = null

        var formatter: String? = null
        var dateFormat: String? = null
        var timeFormat: String? = null
        var autoFill: Boolean? = null
        var multiple: Boolean? = null
        var tableId: String? = null
        var viewId: String? = null

        val options: MutableList<AppTableFieldPropertyOption> = mutableListOf()
        val fields: MutableList<String> = mutableListOf()

        fun build(): AppTableField {
            val o = this
            return AppTableField().apply {
                this.fieldName = o.fieldName
                this.type = o.fieldType

                if (o.formatter != null
                    && o.dateFormat != null
                    && o.timeFormat != null
                    && o.tableId != null
                    && o.viewId != null
                    && o.autoFill != null
                    && o.multiple != null
                ) {
                    this.property = AppTableFieldProperty()
                    this.property.formatter = o.formatter
                    this.property.dateFormat = o.dateFormat
                    this.property.timeFormat = o.timeFormat
                    this.property.tableId = o.tableId
                    this.property.viewId = o.viewId
                    this.property.autoFill = o.autoFill
                    this.property.multiple = o.multiple
                    if (options.isNotEmpty()) {
                        this.property.options = o.options.toTypedArray()
                    }
                    if (fields.isNotEmpty()) {
                        this.property.fields = o.fields.toTypedArray()
                    }
                }
            }
        }

    }

}
