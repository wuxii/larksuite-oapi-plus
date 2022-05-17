package com.harmony.lark

import com.harmony.lark.model.ContinuouslyListResult
import com.larksuite.oapi.core.utils.Jsons
import com.larksuite.oapi.service.bitable.v1.BitableService
import com.larksuite.oapi.service.bitable.v1.model.AppTableField
import com.larksuite.oapi.service.bitable.v1.model.AppTableRecord
import com.larksuite.oapi.service.bitable.v1.model.AppTableRecordBatchCreateReqBody

class BitableApi(val larkApi: LarkApi) {

    private val bitableService: BitableService = larkApi.unwrap(BitableService::class.java)

    fun getBitableFields(bitableAddress: BitableAddress): ContinuouslyListResult<AppTableField> {
        return getBitableFields(bitableAddress.appToken, bitableAddress.table)
    }

    fun getBitableFields(appToken: String, table: String): ContinuouslyListResult<AppTableField> {
        return larkApi.first { pageToken, pageSize ->
            bitableService.appTableFields.list()
                .setAppToken(appToken)
                .setTableId(table)
                .setPageSize(pageSize)
                .setPageToken(pageToken)
                .execute()
                .ensureData { LarkException("获取多维表格表字段失败", it) }
        }
    }

    fun getBitableRecords(bitableAddress: BitableAddress): ContinuouslyListResult<AppTableRecord> {
        return larkApi.first { pageToken, pageSize ->
            bitableService.appTableRecords.list()
                .setAppToken(bitableAddress.appToken)
                .setTableId(bitableAddress.table)
                .setUserIdType("union_id")
                .setViewId(bitableAddress.view)
                .setPageSize(pageSize)
                .setPageToken(pageToken)
                .execute()
                .ensureData { LarkException("获取多维表格数据失败", it) }
        }
    }

    fun getBitableRecords(
        bitableAddress: BitableAddress,
        filter: BitableRecordFilter = BitableRecordFilter(),
        userIdType: String? = null
    ): ContinuouslyListResult<AppTableRecord> {
        return larkApi.first { pageToken, pageSize ->
            bitableService.appTableRecords.list()
                .setAppToken(bitableAddress.appToken)
                .setTableId(bitableAddress.table)
                .setUserIdType(userIdType)
                .setViewId(filter.viewId ?: bitableAddress.view)
                .setFilter(filter.filter)
                .setSort(filter.sort)
                .setFieldNames(Jsons.DEFAULT_GSON.toJson(filter.fieldNames))
                .setTextFieldAsArray(filter.textFieldAsArray)
                .setPageSize(pageSize)
                .setPageToken(pageToken)
                .execute()
                .ensureData { LarkException("获取多维表格数据失败", it) }
        }
    }

    fun addBitableRecords(bitableAddress: BitableAddress, vararg records: Map<String, Any>) {
        val tableRecords = records.map {
            val record = AppTableRecord()
            record.fields = it
            record
        }.toTypedArray()
        addBitableRecords(bitableAddress, *tableRecords)
    }

    fun addBitableRecords(bitableAddress: BitableAddress, vararg records: AppTableRecord) {
        val body = AppTableRecordBatchCreateReqBody()
        body.records = records
        bitableService.appTableRecords
            .batchCreate(body)
            .setAppToken(bitableAddress.appToken)
            .setTableId(bitableAddress.table)
            .execute()
            .ensureData { LarkException("添加多为表格数据失败", it) }
    }
}
