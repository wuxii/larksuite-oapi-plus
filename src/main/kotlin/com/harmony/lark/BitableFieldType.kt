package com.harmony.lark

enum class BitableFieldType(val value: Int) {

    TEXT(1),
    NUMBER(2),
    SINGLE_SELECT(3),
    MULTI_SELECT(4),
    DATE_TIME(5),
    CHECKBOX(7),
    USER(11),
    LINK(15),
    ATTACHMENT(17),
    ASSOCIATION(18),
    FORMULA(20),
    CREATED_AT(1001),
    UPDATED_AT(1002),
    CREATED_BY(1003),
    UPDATED_BY(1004)

}
