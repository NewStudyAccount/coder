package com.admin.common.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageParam implements Serializable {

    private int pageNum = 1;
    private int pageSize = 10;

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
