package com.xxc.common.util;

import com.github.pagehelper.PageInfo;
import com.xxc.entity.result.MyPage;

import java.util.List;

/**
 * Created by xi yang.
 * 2018/12/19
 */
public class MyPageUtil {

    /**
     * 转为MyPage
     *
     * @param page 使用分页插件查出的集合
     * @param data 目标集合
     * @param <T>  目标集合类型
     * @return MyPage
     */
    public static <T> MyPage<T> genPage(List<?> page, List<T> data) {
        MyPage<T> myPage = new MyPage<>();
        PageInfo<?> pageInfo = new PageInfo<>(page);

        myPage.setPageNum(pageInfo.getPageNum());
        myPage.setPageSize(pageInfo.getPageSize());
        myPage.setPages(pageInfo.getPages());
        myPage.setSize(pageInfo.getSize());
        myPage.setTotal(pageInfo.getTotal());
        myPage.setFPage(pageInfo.isIsFirstPage());
        myPage.setFirstPage(pageInfo.getNavigateFirstPage());
        myPage.setHasPreviousPage(pageInfo.isHasPreviousPage());
        myPage.setPrePage(pageInfo.getPrePage());
        myPage.setHasNextPage(pageInfo.isHasNextPage());
        myPage.setNextPage(pageInfo.getNextPage());
        myPage.setLPage(pageInfo.isIsLastPage());
        myPage.setLastPage(pageInfo.getNavigateLastPage());
        myPage.setList(data);
        return myPage;
    }

}
