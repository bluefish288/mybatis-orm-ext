package com.mybatisext.mybatis;


import com.mybatisext.thread.CustomThreadLocal;

import java.util.List;

public class ArgHolder {

    public final static CustomThreadLocal<Integer> PAGE_COUNT_HOLDER = new CustomThreadLocal<>();

    public final static CustomThreadLocal<List<Object>> INSERT_UPDATE_ARG_HOLDER = new CustomThreadLocal<>();

}