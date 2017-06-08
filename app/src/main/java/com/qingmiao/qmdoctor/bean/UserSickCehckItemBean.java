package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/5.
 */

public class UserSickCehckItemBean extends BaseBean {
    public int code ;
    public String bl_id;
    public String sick_title;
    public String status;
    public String time;
    public List<UserSickData> data;

    public static class UserSickData{
        public String data;
        public String demo;
        public String field_cn;
        public String field_en;
        public String unit;
    }




}
