package com.bjpowernode.crm.vo;


import java.util.List;

/*
   PagenationVo<T> 中的T是为了增加这个Vo的通用性，以后这个T中传的是哪个对象，这个就是哪个对象的Vo
   属性List集合中也就是该对象的List集合
 */
public class PagenationVo<T>{
    private  int total;
    private List<T> dataList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
