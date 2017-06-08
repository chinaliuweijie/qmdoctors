package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */

public class ProfessionalFieldBean extends BaseBean {
    public int code;
    public String status;
    public List<ProfessionalData> data;


    public static class ProfessionalData{
        public String disease_name;
        public String id;
        public String mg_id;
        public String time;
    }





}
