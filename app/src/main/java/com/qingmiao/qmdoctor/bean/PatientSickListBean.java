package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/6/19.
 */

public class PatientSickListBean extends BaseBean {
    public int code ;
    public String status;
    public List<SickBean> data;

    public static class SickBean{
        public String sick_id;
        public String sick_name;
        public String table_name;
    }
}
