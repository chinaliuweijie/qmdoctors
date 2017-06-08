package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/25.
 */

public class VersionBean extends BaseBean {
    public int code;
    public String status;
    public List<VersionData> data;

    /**
     *    "android_desc": "患者端",
     "android_up_sta": "1",
     "android_url": "https://api.green-bud.cn/android/sys_patient.apk",
     "android_version": "3",
     "id": "12",
     "ios_desc": "http://192.168.199.193/index.php/Admin/Setting/version",
     "ios_up_sta": "1",
     "ios_url": "http://192.168.199.193/index.php/Admin/Setting/version",
     "ios_version": "1.0",
     "mg_id": "1",
     "time": "1495442338",
     "type": "1"
     */
    public static class VersionData{
        public String android_desc;
        public String android_up_sta;
        public String android_url;
        public String android_version;
        public String id ;
        public String ios_desc ;
        public String ios_up_sta ;
        public String ios_url ;
        public String ios_version ;
        public String mg_id ;
        public String time ;
        public String type ;






    }













}
