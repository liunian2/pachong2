package com.ouyang.main;

import cn.wanghaomiao.seimi.core.Seimi;

public class Boot {
    public static void main(String[] args){
        Seimi s = new Seimi();
        s.goRun("seimiagent");
    }
}
