package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/26.
 */

public class HomeDocindexBean extends BaseBean {
    public int code;
    public String status;
    public HomeDocindexData data;

    public static class HomeDocindexData{
        public List<ABSData> ads;
        public List<JXNewsData> jxnews;
    }

    public static class ABSData{
        public String c_id;
        public String content;
        public String id;
        public String img;
        public String mg_id;
        public String time;
        public String title;
        public String url;
    }

    public static class JXNewsData{
        public String description;
        public String id;
        public String thumb;
        public String title;
        public String url;
        public String updatetime;
        public String inputtime;
    }

















}
