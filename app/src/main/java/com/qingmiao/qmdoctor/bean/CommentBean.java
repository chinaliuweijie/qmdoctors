package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/8.
 */

public class CommentBean extends BaseBean {
    public int code;
    public String page;
    public int pagecount;
    public String status;
    public List<DataBean> data;


    public static class DataBean{
        public String avatar;
        public String content;
        public String id;
        public String m_id;
        public String news_id;
        public String time;
        public String type;
        public String user_name;
    }



}
