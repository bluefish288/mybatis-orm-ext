package com.mybatisext.page;

import java.io.Serializable;

/**
 * 分页参数
 */
public class PageParam implements Serializable{

    /**
     * 当前第几页
     */
    private int currentPage = 1;

    private int pageSize = 10;

    private boolean all = false;

    public PageParam() {
    }

    public PageParam(int currentPage) {
        this.currentPage = currentPage;
    }

    public PageParam(int currentPage, boolean all){
        this.currentPage = currentPage;
        this.all = all;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isAll() {
        return all;
    }
}
