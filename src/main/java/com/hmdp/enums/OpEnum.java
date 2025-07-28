package com.hmdp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpEnum {
    INSERT("c", "新增"),
    UPDATE("u", "更新"),
    DELETE("d", "删除"),
    ;

    private final String type;
    private final String desc;

    public static boolean isInsertOrUpdate(String type) {
        return INSERT.type.equals(type) || UPDATE.type.equals(type);
    }

    public static boolean isNotInsertOrUpdate(String type) {
        return !isInsertOrUpdate(type);
    }

    public static boolean isNotUpdate(String type) {
        return !UPDATE.type.equals(type);
    }

    public static boolean isUpdate(String type) {
        return !isNotUpdate(type);
    }
}