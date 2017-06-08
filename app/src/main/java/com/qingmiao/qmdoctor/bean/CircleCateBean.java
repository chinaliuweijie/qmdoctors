package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * company : 青苗
 * Created by 杜新 on 2017/3/3.
 */

public class CircleCateBean extends BaseBean{


    /**
     * code : 0
     * data : [{"mg_id":"1","q_id":"7","q_name":"A病","q_status":"0","q_time":"1488162324"},{"mg_id":"1","q_id":"8","q_name":"B病","q_status":"0","q_time":"1488162357"},{"mg_id":"1","q_id":"9","q_name":"C病","q_status":"0","q_time":"1488162460"}]
     * status : success
     */

    public int code;
    public String status;
    public ListCircleData data;



    public static class ListCircleData{
        public List<DataBean> all_cate;
        public List<DataBean> tj_cate;
    }



    public static class DataBean {
        /**
         "if_tj": "1",
         "mg_id": "1",
         "q_id": "7",
         "q_name": "血尿",
         "q_status": "0",
         "q_time": "1494172413",
         "type": "1"
         */
        public String if_tj ;
        public String mg_id;
        public String q_id;
        public String q_name;
        public String q_status;
        public String q_time;
        public String type;
    }
}
