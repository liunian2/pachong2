package com.ouyang.service.impl;

import com.ouyang.dao.CrawlerMapper;
import com.ouyang.dto.CrawlerContentDto;
import com.ouyang.dto.PagenationDto;
import com.ouyang.model.CrawlerContent;
import com.ouyang.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CrawlerServiceImpl implements CrawlerService {

    @Autowired
    private CrawlerMapper crawlerMapper;

    public void insert(CrawlerContent crawlerContent){
        crawlerMapper.create(crawlerContent);
    }

    @Override
    public void update(CrawlerContent crawlerContent) {
        crawlerMapper.updateByUrl(crawlerContent);
    }

    @Override
    public PagenationDto searchInfoPageByParam(CrawlerContentDto crawlerContentDto, int page, int size) {
        PagenationDto pagenationDto = new PagenationDto();
        int total = crawlerMapper.count(crawlerContentDto);
        if(total != 0){
            int totalPage;
            if(total % size == 0){
                totalPage = total/size;
            }else{
                totalPage = total/size + 1;
            }
            if(page < 1){
                page = 1;
            }
            if(page > totalPage){
                page = totalPage;
            }
            pagenationDto.setPagenation(totalPage, page);
            int offset = size * (page - 1);
            crawlerContentDto.setOffset(offset);
            crawlerContentDto.setSize(size);
            List<CrawlerContent>  crawlerContentList = crawlerMapper.searchInfoPageByParam(crawlerContentDto);
            pagenationDto.setCrawlerContentList(crawlerContentList);
        }else{
            pagenationDto = null;
        }
        return pagenationDto;
    }

    @Override
    public List<CrawlerContent> searchInfoByParam(CrawlerContentDto crawlerContentDto) {
        return crawlerMapper.searchInfoByParam(crawlerContentDto);
    }

    @Override
    public List<CrawlerContent> getCrawlerInfoById(Map<String, Object> param) {
        return crawlerMapper.getCrawlerInfoById(param);
    }
}
