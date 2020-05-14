package com.ouyang.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import cn.wanghaomiao.xpath.model.JXDocument;
import com.ouyang.dto.CrawlerContentDto;
import com.ouyang.model.CrawlerContent;
import com.ouyang.service.CrawlerService;
import com.ouyang.service.impl.CrawlerServiceImpl;
import com.ouyang.spring.SpringUntils;
import com.ouyang.util.HttpUtil;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Crawler(name = "seimiagent" ,httpTimeOut = 300000)
public class SeimiAgentDemo extends BaseSeimiCrawler{

    private HttpSession session = null;
    private List<String> urlList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();
    boolean flag = false;

    /**
     * 在resource/config/seimi.properties中配置方便更换，当然也可以自行根据情况使用自己的统一配置中心等服务
     */
    //@Value("${seimiAgentHost}")
    private String seimiAgentHost = "192.168.43.84";
    private int seimiAgentPort = 8000;

    @Override
    public String seimiAgentHost() {
        return this.seimiAgentHost;
    }

    @Override
    public int seimiAgentPort() {
        return seimiAgentPort;
    }

    @Override
    public String[] startUrls() {
        String url1 = "https://caigou.ceair.com";
        String url2 = "http://www.airchina.com.cn";
        return new String[]{url2};
    }

    /**
     * UA库可以自行查找整理，动态 User-Agent 可用于混淆请求特征
     * @return
     */
    @Override
    public String getUserAgent(){
        String[] uas = new String[]{"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/38.0"
        };
        return uas[RandomUtils.nextInt()%uas.length]+" "+ RandomStringUtils.randomAlphanumeric(6);
    }

    @Override
    public void start(Response response) {
        String url1 = "https://caigou.ceair.com/portal/portal/opportunity";
        String url2 = "http://www.airchina.com.cn/cn/contact_us/cgpt/cgxmgg/index.shtml";
        Request seimiAgentReq = Request.build(url2,"getHtml2")
                .useSeimiAgent()
//                告诉SeimiAgent针对这个请求是否使用cookie，如果没有设置使用当前Crawler关于cookie使用条件作为默认值。
//                .setSeimiAgentUseCookie(true)
                //设置全部load完成后给SeimiAgent多少时间用于执行js并渲染页面，单位为毫秒
                .setSeimiAgentRenderTime(20000);
        push(seimiAgentReq);
    }

    /**
     * 打印网页信息
     * @param response
     */
    public void getHtml1(Response response){
        try {
            JXDocument doc = response.document();
            List<Object> titleList = doc.sel("//tr[@class='ng-scope']");
            if(titleList.size() > 0){
                for (int i = titleList.size() - 1; i >= 0; i--) {
                    Map<String, String> map = new HashMap<>();
                    Element e = (Element) titleList.get(i); // tr
                    Elements tdEles = e.children();
                    if(tdEles.size() > 0){
                        Element tdele1 = tdEles.get(0); //td
                        Element tdele2 = tdEles.get(1); //td
                        String time = tdele2.childNode(0).toString();
                        if(!"".equals(time) && time != null){
                            Date dateTime = covertTime(time);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            map.put("time", sdf.format(dateTime));
                        }else{
                            map.put("time", "");
                        }
                        Element tdAEle = tdele1.children().get(0);
                        String title = tdAEle.childNode(0).toString();
                        map.put("title", title);
                        String href = tdAEle.attr("href");
                        String contentUrl = "https://caigou.ceair.com" + href;
                        if(href.contains("javascript")){
                            continue;
                        }else{
                            push(Request.build(contentUrl, "contentBean")
                                    .useSeimiAgent().setMeta(map)
                                    .setSeimiAgentRenderTime(20000)
                            );
                        }
                    }
                }
                //分页处理
                //Thread.sleep(10000);
                //List<Object> nextUrl  = doc.sel("//div[@class='paging padding-bot-50 padding-top-20']/a[text()*='下一页']/@href");
                //System.out.println(nextUrl.size());

//                String pageUrl = "https://caigou.ceair.com/portal/portal/sysInfoList?siteFlag=false";
//                Map<String, String> param = new HashMap<>();
//                param.put("autoCount", HttpUtil.paramEncode("true"));
//                param.put("first", HttpUtil.paramEncode("1"));
//                param.put("hasNext", HttpUtil.paramEncode("true"));
//                param.put("hasPre", HttpUtil.paramEncode("false"));
//                param.put("nextPage", HttpUtil.paramEncode("2"));
//                param.put("orderBySetted", HttpUtil.paramEncode("false"));
//                param.put("pageNo", HttpUtil.paramEncode("1"));
//                param.put("pageSize", HttpUtil.paramEncode("10"));
//                param.put("prePage", HttpUtil.paramEncode("1"));
//                param.put("totalCount", HttpUtil.paramEncode("526"));
//                param.put("totalPages", HttpUtil.paramEncode("53"));
//                param.put("order", HttpUtil.paramEncode(""));
//                param.put("orderBy", HttpUtil.paramEncode(""));
//                Request seimiAgentReq = Request.build(pageUrl,"getHtml1")
//                        .setMeta(param)
//                        .setParams(param)
//                        .useSeimiAgent()
//                        .setSeimiAgentRenderTime(20000);
//                push(seimiAgentReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //返回的内容
    public void contentBean(Response response) {
        try {
            List<String> contents = new ArrayList<>();
            JXDocument doc = response.document();
            Map<String,String> map = response.getMeta();
            //标题
            String title = map.get("title");
            //时间
            String time = map.get("time");
            //地址
            String url = response.getUrl();
            //内容
            List<Object> contentDivs = doc.sel("//div[@class='templatemo-content-widget']");//div list
            for (int i = 0; i <= contentDivs.size() - 1; i++) {
                Element e = (Element) contentDivs.get(i);//外层每一个div
                //段落标题
                String titleH2 = "";
                Elements eleH2 = e.getElementsByTag("h2");
                if (eleH2.size()>0){
                    Element h2 = eleH2.get(0);
                    if(h2.childNodeSize() > 0){
                        titleH2 = h2.childNode(0).toString();
                    }
                }
                contents.add(titleH2);
                //段落内容
                Elements eleDiv = e.getElementsByClass("tender-public");
                if (eleDiv.size() > 0) {
                    Element ele1 = eleDiv.get(0);
                    Elements ele2 = ele1.children(); //div内部所有的dl
                    if (ele2.size() > 0) {
                        for (int k = 0; k <= ele2.size() - 1; k++) {  //单个dll
                            StringBuilder sb = new StringBuilder();
                            Elements ele3 = ele2.get(k).children();//dll子节点
                            if (ele3.size() > 0) {
                                for (int m = 0; m <= ele3.size() - 1; m++) { // dd dt
                                    Element eleDd = ele3.get(m);
                                    Elements ele4 = eleDd.children();
                                    if (ele4.size() > 0) {
                                        for (int n = 0; n <= ele4.size() - 1; n++) {
                                            Element ele5 = ele4.get(n);
                                            Elements ele6 = ele5.children();
                                            if (ele6.size() > 0) {
                                                for (int a = 0; a <= ele6.size() - 1; a++) {
                                                    Element ele7 = ele6.get(a);
                                                    if (ele7.childNodeSize() > 0) {
                                                        sb.append(ele7.childNode(0).toString().replace("&nbsp;", ""));
                                                    }
                                                }
                                            } else {
                                                if (ele5.childNodeSize() > 0) {
                                                    sb.append(ele5.childNode(0).toString().replace("&nbsp;", ""));
                                                }
                                            }
                                        }
                                    } else {
                                        if (eleDd.childNodeSize() > 0) {
                                            sb.append(eleDd.childNode(0).toString().replace("&nbsp;", ""));
                                        }
                                    }
                                }
                            }
                            contents.add(sb.toString());
                        }
                    }
                }
            }
            insertDb(title, url, time, "D",contents, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //国航
    public void getHtml2(Response response){
        try {
            JXDocument doc = response.document();
            List<Object> urls = doc.sel("//div[@class='serviceMsg']/ul/li/a");
            if(urls.size() > 0 ){
                for (int i = urls.size() - 1; i >= 0; i--) {
                    Element e = (Element) urls.get(i);
                    Elements timeEles = e.siblingElements();//时间
                    Element timeEle = timeEles.get(0);//(2019-11-11)
                    String time = timeEle.childNode(0).toString().replace("(","").replace(")","").trim();
                    String title = e.childNode(0).toString();
                    String href = e.attr("href");
                    String contentUrl = "http://www.airchina.com.cn" + href;
                    if(href.contains("javascript")){
                        continue;
                    }else{
                        map.put(contentUrl,time);
                    }
                }
                //分页
                List<Object> nextPage = doc.sel("//div[@id='pageBar']/div[@class='pagingNav']/a[text()*='下一页']/@href");
                if(nextPage.size() > 0){
                    String nextUrl = (String) nextPage.get(0);
                    String nextUrl2 = "http://www.airchina.com.cn" + nextUrl;
                    push(Request.build(nextUrl2, "getHtml2")
                            .useSeimiAgent()
                            .setSeimiAgentRenderTime(20000)
                    );
                    return;
                }
                Iterator<String> iterator = map.keySet().iterator();
                while(iterator.hasNext()){
                    Map<String, String> timeMap = new HashMap<>();
                    String url = iterator.next();
                    String time = map.get(url);
                    timeMap.put("time", time);
                    push(Request.build(url, "contentBean2")
                            .useSeimiAgent()
                            .setMeta(timeMap)
                            .setSeimiAgentRenderTime(20000)
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void contentBean2(Response response) {
        String url = "";
        try {
            List<String> contents = new ArrayList<>();
            Map<String,String> meta =  response.getMeta();
            String time = meta.get("time");

            JXDocument doc = response.document();
            //拿标书标题
            List<Object> contentBs = doc.sel("//div[@class='topBorder']/div/b");
            Element titleEle = (Element)contentBs.get(0);
            String title = titleEle.childNode(0).toString();
            //拿地址
            url = response.getUrl();
            //附件
            List<String> attachList = new ArrayList<>();
            List<Object> aListEles = doc.sel("//div[@class='topBorder']/a");
            if(aListEles.size() > 0){
                for(int a=0; a<=aListEles.size()-1; a++){
                    StringBuilder attachBuider = new StringBuilder();
                    Element aEle = (Element) aListEles.get(a);
                    if(aEle.childNodeSize() > 0){
                        String href = "http://www.airchina.com.cn" + aEle.attr("href");
                        String aTitle = aEle.childNode(0).toString();
                        attachBuider.append(href);
                        attachBuider.append("&&");
                        attachBuider.append(aTitle);
                        attachList.add(attachBuider.toString());
                    }
                }
            }
            //拿标书内容
            List<Object> contentPs = doc.sel("//div[@class='topBorder']/p");
            for (int i = 0; i <= contentPs.size() - 1; i++) {
                Element e = (Element) contentPs.get(i);//p
                Elements spanEles = e.children();
                StringBuilder sb = new StringBuilder();
                if(spanEles.size() > 0){
                    for(int j=0; j <= spanEles.size() -1; j++){
                        Element spanJEle = spanEles.get(j);
                        int spanJeleSize = spanJEle.childNodeSize();
                        Elements ele1 = spanJEle.children();
                        //TODO
//                        if(spanJeleSize > 0){
//                            if(spanJeleSize != ele1.size()){
//                                for(int u=0; u<=spanJeleSize-1; u++){
//                                    String text = spanJEle.childNode(u).toString();
//                                    if(!text.contains("<")){
//                                        sb.append(text);
//                                    }
//                                }
//                            }
//                        }

                        if(ele1.size() > 0){
                            for(int k=0; k<= ele1.size()-1 ; k++){
                                Elements ele2 = ele1.get(k).children();
                                if(ele2.size() > 0 ){
                                    for(int l=0; l<=ele2.size()-1; l++){
                                        Elements ele3 =ele2.get(l).children();
                                        if(ele3.size() > 0){
                                            for(int a=0; a<=ele3.size()-1;a++){
                                                if(ele3.get(a).childNodeSize()>0){
                                                    sb.append(ele3.get(a).childNode(0).toString().replace("&nbsp;",""));
                                                }
                                            }
                                        }else{
                                            if(ele2.get(l).childNodeSize()>0){
                                                sb.append(ele2.get(l).childNode(0).toString().replace("&nbsp;",""));
                                            }
                                        }
                                    }
                                }else{
                                    if(ele1.get(k).childNodeSize() > 0){
                                        sb.append(ele1.get(k).childNode(0).toString().replace("&nbsp;",""));
                                    }
                                }
                            }
                        }else{
                            if(spanEles.get(j).childNodeSize() > 0){
                                sb.append(spanEles.get(j).childNode(0).toString().replace("&nbsp;",""));
                            }
                        }
                    }
                    contents.add(sb.toString());
                }else{
                    if(e.childNodeSize() > 0){
                        contents.add(e.childNode(0).toString().replace("&nbsp;",""));//内容
                    }
                }
            }
            insertDb(title, url, time, "G",contents, attachList);
        }catch (Exception e){
            System.out.println(url);
            e.printStackTrace();
        }
    }

    /**
     * 插入数据库
     * @param title
     * @param url
     * @param time
     * @param contents
     * @param attachList
     */
    public void insertDb(String title, String url, String time,String ower, List<String> contents, List<String> attachList){
        //处理附件
        String attchmentsUrl = handleFile(attachList);
        CrawlerService crawlerService = (CrawlerServiceImpl) SpringUntils.getBean("crawlerServiceImpl");
        //如果存在相同的url更新时间不插入
        CrawlerContent crawlerContent = new CrawlerContent();
        crawlerContent.setAttchmentsUrl(attchmentsUrl);
        crawlerContent.setTitle(title);
        crawlerContent.setContextUrl(url);
        if(!"".equals(time) && time != null){
            crawlerContent.setTime(covertTime(time));
        }
        StringBuilder context = new StringBuilder("");
        if(contents.size() > 0) {
            for (int i = 0; i <= contents.size() - 1; i++) {
                context.append(contents.get(i));
                context.append("&&");
            }
        }
        crawlerContent.setOwer(ower);//G代表国航
        crawlerContent.setContent(context.toString());
        CrawlerContentDto crawlerContentDto = new CrawlerContentDto();
        BeanUtils.copyProperties(crawlerContent, crawlerContentDto);
        crawlerContent.setCreateTime(new Date());
        List<CrawlerContent> crawlerContentList = crawlerService.searchInfoByParam(crawlerContentDto);
        if(crawlerContentList != null && crawlerContentList.size() > 0){
            crawlerService.update(crawlerContent);
        }else{
            crawlerService.insert(crawlerContent);
        }
    }

    /**
     * 处理时间
     * @param timeStr
     * @return
     */
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

    /**
     * 处理附件
     * @param attachList
     * @return
     */
    public String handleFile(List<String> attachList){
        StringBuilder attachListBuilder = new StringBuilder();
        if(attachList != null && attachList.size() > 0){
            for(int i=0; i<=attachList.size()-1; i++){
                String attach = attachList.get(i);
                StringBuilder attachBuilder = new StringBuilder();
                try {
                    String[] attachArr = attach.split("&&");//文件地址&&文件标题
                    String attachUrl = attachArr[0];
                    String attachTitle = attachArr[1];
                    String suffix = attachUrl.substring(attachUrl.lastIndexOf("."));//文件后缀  .txt
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    Calendar calendar = Calendar.getInstance();
                    File path = new File(ResourceUtils.getURL("classpath:").getPath());
                    //上传目录为/static/attachment/
                    File finalDirect = new File(path.getAbsolutePath(),"static/attachment/");
                    if (!finalDirect.exists()) {
                        finalDirect.mkdirs();
                    }
                    String localFileName = sdf.format(calendar.getTime()) + suffix;
                    String localPathName = finalDirect.getPath() + File.separator + localFileName;
                    //下载文件到本地
                    downLoadFromUrl(attachUrl,localPathName);
                    attachBuilder.append(localFileName);
                    attachBuilder.append("&");
                    attachBuilder.append(attachTitle);
                    if(i == (attachList.size()-1)){
                        attachListBuilder.append(attachBuilder);
                    }else{
                        attachListBuilder.append(attachBuilder);
                        attachListBuilder.append("&&");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return attachListBuilder.toString();
    }

    /**
     * 通过远程地址下载文件
     * @param remoteFilePath
     * @param localFilePath
     * @throws IOException
     */
    public static void  downLoadFromUrl(String remoteFilePath, String localFilePath){
        try{
            URL url = new URL(remoteFilePath);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);
            File file = new File(localFilePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if(fos!=null){
                fos.close();
            }
            if(inputStream!=null){
                inputStream.close();
            }
        }catch (Exception e){
            System.out.println("文件下载出错或者文件不存在！");
        }
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    //写入文件
    public void writeToFile(String title, List<String> content) {
        String fileDir = "E:\\pachong_donghang\\";
        String fileName = title + ".doc";
        XWPFDocument document = new XWPFDocument();
        OutputStream stream = null;
        BufferedOutputStream bufferStream = null;
        try {
            File dir = new File(fileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            stream = new FileOutputStream(new File(fileDir + fileName));
            bufferStream = new BufferedOutputStream(stream, 1024);
            // 创建一个段落
            XWPFParagraph p1 = document.createParagraph();
            // 设置居中
            p1.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r1 = p1.createRun();
            // 是否加粗
            r1.setBold(true);
            // 与下一行的距离
            r1.setTextPosition(30);
            // 段落名称
            r1.setText(title);

            // 字体大小
            r1.setFontSize(18);// 字体大小
            // 增加换行
            r1.addCarriageReturn();
            // 创建第二个段落
            XWPFParagraph p2 = document.createParagraph();
            XWPFRun r2 = p2.createRun();
            for (int i = 0; i < content.size(); i++) {
                r2.setText(content.get(i));
                r2.addCarriageReturn();
            }
            // 设置字体
            r2.setFontFamily("仿宋");
            r2.setFontSize(14);// 字体大小
            document.write(stream);
            stream.close();
            bufferStream.close();
        } catch (Exception ex) {
            logger.error("写word或Excel错误文件失败：{}", ex.getMessage());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("写word或Excel错误文件失败：{}", e.getMessage());
                }
            }
            if (bufferStream != null) {
                try {
                    bufferStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("写word或Excel错误文件失败：{}", e.getMessage());
                }
            }
        }
    }

}
