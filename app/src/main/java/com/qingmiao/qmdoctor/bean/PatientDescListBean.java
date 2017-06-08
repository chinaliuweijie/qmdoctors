package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/2.
 */

public class PatientDescListBean extends BaseBean{
    public int code ;
    public String status;
    public String pagecount;
    public String page;
    public List<DescData> data;


    public static class DescData{
        public String id;
        public String uid;
        public String sick_desc;
        public String did;
        public String time;
        public String d_name;
        public String avatar;
    }

}
