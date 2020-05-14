package com.ouyang.dao;

import com.ouyang.dto.CrawlerContentDto;
import com.ouyang.model.CrawlerContent;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface CrawlerMapper {

    void create(CrawlerContent crawlerContent);

    void updateByUrl(CrawlerContent crawlerContent);

    int count(CrawlerContentDto crawlerContentDto);

    List<CrawlerContent> list(Date time);

    List<CrawlerContent> searchInfoPageByParam(CrawlerContentDto crawlerContentDto);

    List<CrawlerContent> searchInfoByParam(CrawlerContentDto crawlerContentDto);

    List<CrawlerContent> getCrawlerInfoById(Map<String, Object> param);
}
