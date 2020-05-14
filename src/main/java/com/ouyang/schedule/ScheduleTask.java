package com.ouyang.schedule;

import cn.wanghaomiao.seimi.core.Seimi;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {

    //@Scheduled(cron = "0/5 * * * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=500000)
    private void configureTasks() {
        System.out.println("定时任务");
        Seimi s = new Seimi();
        s.goRun("seimiagent");
    }
}
