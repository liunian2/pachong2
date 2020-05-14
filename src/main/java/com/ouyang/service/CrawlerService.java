package com.ouyang.service;

import com.ouyang.dto.CrawlerContentDto;
import com.ouyang.dto.PagenationDto;
import com.ouyang.model.CrawlerContent;
import java.util.List;
import java.util.Map;

public interface CrawlerService {

    void insert(CrawlerContent crawlerContent);

    void update(CrawlerContent crawlerContent);

    PagenationDto searchInfoPageByParam(CrawlerContentDto crawlerContentDto, int page, int size);

    List<CrawlerContent> searchInfoByParam(CrawlerContentDto crawlerContentDto);

    List<CrawlerContent> getCrawlerInfoById(Map<String, Object> param);
}
