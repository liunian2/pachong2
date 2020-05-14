package com.ouyang.controller;

import cn.wanghaomiao.seimi.core.Seimi;
import com.ouyang.dto.CrawlerContentDto;
import com.ouyang.dto.PagenationDto;
import com.ouyang.model.CrawlerContent;
import com.ouyang.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  RestController  相当于 @Controller + @ResponseBody; 返回json数据
 * controller
 */
@Controller
@RequestMapping("/view")
public class ViewController {

    private static int currentPage=1;
    private static int SIZE=8;

    @Autowired
    private CrawlerService crawlerService;

    @RequestMapping("/index")
    public String toIndex(){
        return "index";
    }

    @RequestMapping(value = "/startCrawler",method = RequestMethod.GET) //consumes = "application/json"
    @ResponseBody
    public String startCrawler(){
        try{
            Seimi s = new Seimi();
            s.goRun("seimiagent");
            System.out.println("爬虫完毕");
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    @RequestMapping(value = "/serchInfo",method = RequestMethod.GET)
    @ResponseBody
    public PagenationDto serchInfo(HttpServletRequest request){
        CrawlerContentDto crawlerContentDto = new CrawlerContentDto();
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String ower = request.getParameter("ower");
        String title = request.getParameter("title");
        String page = request.getParameter("page");
        if(!"".equals(page) && page != null){
            currentPage = Integer.parseInt(page);
        }
        if(startTime != null && !"".equals(startTime)){
            crawlerContentDto.setStartTime(covertTime(startTime));
        }
        if(endTime != null && !"".equals(endTime)){
            crawlerContentDto.setEndTime(covertTime(endTime));
        }
        crawlerContentDto.setTitle(title);
        crawlerContentDto.setOwer(ower);
        PagenationDto crawlerListPage = crawlerService.searchInfoPageByParam(crawlerContentDto,currentPage,SIZE);
        return crawlerListPage;
    }

    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    public String detail(HttpServletRequest request,Model model){
        String id = request.getParameter("id");
        model.addAttribute("id",id);
        return "detail";
    }

    @RequestMapping(value = "/detailById",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> detailById(HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        String id = request.getParameter("id");
        Map<String,Object> param = new HashMap<>();
        param.put("id",id);
        List<CrawlerContent> crawlerList = crawlerService.getCrawlerInfoById(param);
        String path = getFilePath();
        map.put("filePath", path);
        map.put("crawlerList", crawlerList);
        return map;
    }

    public String getFilePath(){
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //上传目录为/static/attachment/
        File finalDirect = new File(path.getAbsolutePath(),"static/attachment/");
        if (!finalDirect.exists()) {
            finalDirect.mkdirs();
        }
        String localPathName = finalDirect.getPath() + File.separator;
        return localPathName;
    }

    public Date covertTime(String timeStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date time = null;
        try {
            time = sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
