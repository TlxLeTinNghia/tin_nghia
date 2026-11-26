package com.example.tin_nghia.application.dto;

import com.example.tin_nghia.domain.Product;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PageResult<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElement;
    public PageResult(List<T> content , int page , int size , long totalElement){
        this.content  = content;
        this.page = page;
        this.size = size;
        this.totalElement = totalElement;
    }
    public List<T> getContent(){
        return this.content;
    }
    public int getPage(){
        return  page;
    }
    public int getSize(){
        return size;
    }
    public long getTotalElement(){
        return totalElement;
    }
    public long getTotalPages(){
        return totalElement/size + (totalElement%size==0?0:1);
    }
}
