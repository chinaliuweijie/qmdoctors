package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/26.
 */

public class PatientAllInfoBean extends BaseBean {
    public int code;
    public String status;
    public PatientData data;


    public static class PatientData{
        //病例标签
        public Object u_tag;
        //病例描述
        public List<PatientSickDesc> sick_desc;
        //病人信息
        public PatientUserInfo u_info;
        //病例检查信息
        public List<PatientUserSick> user_sick;

        /**
         *
         *  "bl_id": "37",
         "sick_id": "19",
         "sick_msg": "蛋白质：阴性,潜血：阴性",
         "sick_table": "ncg_data",
         "sick_title": "尿常规",
         "time": "1387416600",
         "user_id": "55"
         */
        public static class PatientUserSick extends BaseBean{
            public String bl_id;
            public String sick_id;
            public String sick_msg;
            public String sick_table;
            public String sick_title;
            public String time;
            public String user_id;
        }


        public static class PatientUserInfo{
            public String age;
            public String avatar;
            public String birth_date;
            public String remark_names;
            public String city;
            public String hx_uname;
            public String if_vis;
            public String mobile;
            public String nickname;
            public String province;
            public String relation;
            public String sex;
            public String token;
            public String uid;
            public String user_name;
        }



        public static class PatientSickDesc{
            public String id;
            public String uid;
            public String sick_desc;
            public String did;
            public String time;
            public String pic;
            public String thumb_pic;
            public String sound;
            public String sound_time;
        }
    }




}
