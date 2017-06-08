package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/27.
 */

public class DocCircleCateBean extends BaseBean {
    public int code;
    public String status;
    public List<DocCircleData> data;


    public static class DocCircleData{
        public String if_tj;
        public String mg_id;
        public String q_id;
        public String q_name;
        public String q_status;
        public String q_time;
        public String type;
    }




}
