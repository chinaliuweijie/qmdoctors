package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/26.
 */

public class DocumentTalkBean extends BaseBean {
    public int code;
    public int page;
    public int pagecount;
    public String status;
    public List<DocumentTalkData> data;


    public static class DocumentTalkData{
        public String author;
        public String cate;
        public String description;
        public String id;
        public String inputtime;
        public String l_tj;
        public String pdf;
        public String s_tj;
        public String source;
        public String status;
        public String thumb;
        public String title;
        public String updatetime;
        public String url;
    }









}
