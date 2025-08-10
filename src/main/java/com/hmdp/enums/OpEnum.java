package com.hmdp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpEnum {
    READ("r", "读取"),
    CREATE("c", "创建"),
    UPDATE("u", "更新"),
    DELETE("d", "删除"),
    ;

    private final String type;
    private final String desc;

    public static boolean isCreateOrUpdate(String type) {
        return CREATE.type.equals(type) || UPDATE.type.equals(type);
    }

    public static boolean isNotCreateOrUpdate(String type) {
        return !isCreateOrUpdate(type);
    }

    public static boolean isNotUpdate(String type) {
        return !UPDATE.type.equals(type);
    }

    public static boolean isUpdate(String type) {
        return !isNotUpdate(type);
    }

    public static boolean isNotDelete(String type) {
        return !isDelete(type);
    }

    public static boolean isDelete(String type) {
        return DELETE.type.equals(type);
    }

    public static boolean isReadOrUpdate(String type) {
        return READ.type.equals(type) || UPDATE.type.equals(type);
    }
}