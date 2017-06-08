package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */

public class CollectDocumentBean extends BaseBean {
    public int code ;
    public int page;
    public int pagecount;
    public String status;
    public List<CollectData> data;


    public static class CollectData {
        public String c_id;
        public String description;
        public String id;
        public String source_type;
        public String thumb;
        public String time;
        public String title;
        public String url;
    }

}
