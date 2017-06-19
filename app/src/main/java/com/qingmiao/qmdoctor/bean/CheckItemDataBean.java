package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/6/19.
 */

public class CheckItemDataBean extends BaseBean {
    public int code;
    public String status;
    public List<CheckItemBean> data;


    public static class CheckItemBean{
        public List<String> data;
        public String demo;
        public String field_cn;
        public String field_en;
        public List<String> time;
        public String unit;
    }


}
