package com.hmdp.util;

import com.alibaba.fastjson.JSON;
import com.hmdp.domain.dto.BinLog;

public class BinlogUtils {

    public static BinLog parseJsonString2Binlog(String jsonStr) {
        return JSON.parseObject(jsonStr, BinLog.class);
    }
}
