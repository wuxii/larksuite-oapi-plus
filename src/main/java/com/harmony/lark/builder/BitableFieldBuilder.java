package com.harmony.lark.builder;

import com.larksuite.oapi.service.bitable.v1.model.AppTableFieldPropertyOption;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BitableFieldBuilder {

    private String fieldName;

    private String fieldType;

    private List<AppTableFieldPropertyOption> options = new ArrayList<>();

    private String formatter;

    private String dateFormat;

    private String timeFormat;

    private Boolean autoFill;

    private Boolean multiple;

    private String tableId;

    private String viewId;

    private List<String> fields = new ArrayList<>();

}
