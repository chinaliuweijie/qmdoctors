package com.qingmiao.qmdoctor.bean;

/**
 * Created by Administrator on 2017/5/27.
 */

public class TalkDocuterBean {
    // 0 代表标题   1 代表条目  默认是条目
    public int type =1;
    // 条目的数据
    public CircleCateBean.DataBean itemDataBean;
    // 标题数据
    public String title;
}
