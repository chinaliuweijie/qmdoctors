package com.qingmiao.qmdoctor.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/18.
 */

public class DoctorDataBean extends BaseBean {
    public int code;
    public String status;
    public DoacorData data;


    public static class DoacorData implements Serializable {
        public String did ;
        public String hx_uname;
        public String mobile;
        public String sex;
        public String status;
        public String token;

    //    public String password;
        public String d_name;
     //   public String age;
        public String department;
        public String hospital;
        public String professional_field;
        public String job_title;
        public String certificate;
        public String avatar;
        public String desc;
        public String nickname;


    }




}
