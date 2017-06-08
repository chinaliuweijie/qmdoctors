package com.qingmiao.qmdoctor.bean;

/**
 * Created by Administrator on 2017/4/17.
 */

public class PatientInfomationBean extends BaseBean{
    public int code;
    public String status;
    public InfomationData data;


    /**
     *      "age": "28",
     "avatar": "/attachment/201704/58eee7913a58e.png",
     "hx_uname": "hx_13152007672",
     "is_friend": 1,
     "is_marked": 2,
     "mobile": "13152007672",
     "nickname": "Ghost",
     "sex": "0",
     "uid": "48",
     "user_name": "高端"
     */
    public static class InfomationData extends BaseBean{
        public String  age;
        public String  avatar;
        public String  hx_uname;
        public String  is_friend;
        public String  is_marked;
        public String  mobile;
        public String  nickname;
        public String  sex;
        public String  uid;
        public String  user_name;
    }







}
