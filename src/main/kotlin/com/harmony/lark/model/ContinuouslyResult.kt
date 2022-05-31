package com.harmony.lark.model

class ContinuouslyResult<T>(
    result: ListResult<T>,
    private val loader: (token: String?) -> ListResult<T>,
) : ListResult<T>(
    result.hasMore,
    result.pageToken,
    result.total,
    result.items
) {

    fun next(): ContinuouslyResult<T> {
        if (!hasNext()) {
            throw IllegalStateException("no more result")
        }
        val nextResult = loader.invoke(pageToken)
        return ContinuouslyResult(nextResult, loader)
    }

    fun hasNext(): Boolean {
        return hasMore
    }

}
