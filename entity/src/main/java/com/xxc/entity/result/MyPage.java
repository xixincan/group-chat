package com.xxc.entity.result;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 * @Author: xixincan
 * @Date: 2019/5/19
 * @Version 1.0
 */
public class MyPage<T> implements Serializable {

    // 当前页
    private int pageNum;
    // 每页的数量
    private int pageSize;
    // 当前页的数量
    private int size;
    // 总记录数
    private long total;
    // 总页数
    private int pages;
    // 第一页
    private int firstPage;
    // 前一页
    private int prePage;
    // 下一页
    private int nextPage;
    // 最后一页
    private int lastPage;
    // 是否为第一页
    private boolean isFPage = false;
    // 是否为最后一页
    private boolean isLPage = false;
    // 是否有前一页
    private boolean hasPreviousPage = false;
    // 是否有下一页
    private boolean hasNextPage = false;
    // 结果集
    private List<T> list;
    
    public MyPage(){

    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public boolean isFPage() {
        return isFPage;
    }

    public void setFPage(boolean isFPage) {
        this.isFPage = isFPage;
    }

    public boolean isLPage() {
        return isLPage;
    }

    public void setLPage(boolean isLPage) {
        this.isLPage = isLPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
