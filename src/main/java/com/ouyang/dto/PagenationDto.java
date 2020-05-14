package com.ouyang.dto;

import com.ouyang.model.CrawlerContent;

import java.util.ArrayList;
import java.util.List;

public class PagenationDto {
    private List<CrawlerContent> crawlerContentList;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer currentPage;
    private List<Integer> pageList = new ArrayList<>();
    private Integer totalPage;

    public List<CrawlerContent> getCrawlerContentList() {
        return crawlerContentList;
    }

    public void setCrawlerContentList(List<CrawlerContent> crawlerContentList) {
        this.crawlerContentList = crawlerContentList;
    }

    public boolean isShowPrevious() {
        return showPrevious;
    }

    public void setShowPrevious(boolean showPrevious) {
        this.showPrevious = showPrevious;
    }

    public boolean isShowFirstPage() {
        return showFirstPage;
    }

    public void setShowFirstPage(boolean showFirstPage) {
        this.showFirstPage = showFirstPage;
    }

    public boolean isShowNext() {
        return showNext;
    }

    public void setShowNext(boolean showNext) {
        this.showNext = showNext;
    }

    public boolean isShowEndPage() {
        return showEndPage;
    }

    public void setShowEndPage(boolean showEndPage) {
        this.showEndPage = showEndPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public List<Integer> getPageList() {
        return pageList;
    }

    public void setPageList(List<Integer> pageList) {
        this.pageList = pageList;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public void setPagenation(int totalPage, Integer currentPage) {
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        pageList.add(currentPage);
        for(int i=1; i<=3; i++){
            if(currentPage - i > 0){
                pageList.add(0, currentPage - i);
            }
            if(currentPage + i <= totalPage){
                pageList.add(currentPage + i);
            }
        }

        //是否展示上一页
        if(currentPage == 1){
            showPrevious = false;
        }else{
            showPrevious = true;
        }
        //是否展示下一页
        if(currentPage == totalPage){
            showNext = false;
        }else {
            showNext = true;
        }
        //是否展示第一页
        if(pageList.contains(1)){
            showFirstPage = false;
        }else{
            showFirstPage = true;
        }
        //是否展示最后一页
        if(pageList.contains(totalPage)){
            showEndPage = false;
        }else{
            showEndPage = true;
        }
    }
}
