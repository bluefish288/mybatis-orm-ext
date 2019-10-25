package com.mybatisext.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Page<S> implements Serializable{

    private int pageSize;

    private int totalPage;
    private int currentPage;
    private int totalRecord;
    private List<S> dataList = new ArrayList<>();

    private Page(){}

    public final static Page EMPTY_PAGE = new Page();

    public Page(PageParam pageParam, int totalRecord){
        this.totalRecord = totalRecord;
        this.currentPage = pageParam.getCurrentPage();
        this.pageSize = pageParam.getPageSize();
        totalPage = (totalRecord % pageSize == 0) ? (totalRecord / pageSize) : ((totalRecord / pageSize)+1);
        if(currentPage < 1){
            currentPage = 1;
        }
        if(currentPage > totalPage){
            currentPage = totalPage;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public int getPrePage(){
        return currentPage > 1 ? currentPage - 1 : 1;
    }

    public int getNextPage(){
        return currentPage == totalPage ? currentPage : currentPage + 1;
    }

    public List<S> getDataList() {
        return dataList;
    }



    public interface DataTransfer<SRC,TAR>{
        TAR transfer(SRC src);
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageSize=" + pageSize +
                ", totalPage=" + totalPage +
                ", currentPage=" + currentPage +
                ", totalRecord=" + totalRecord +
                ", dataList=" + dataList +
                '}';
    }
}
