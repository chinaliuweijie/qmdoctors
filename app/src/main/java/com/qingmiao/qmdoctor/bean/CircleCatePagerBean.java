package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * company : 青苗
 * Created by  on 2017/3/3.
 */
public class CircleCatePagerBean extends BaseBean{
    /**
     * code : 0
     * status : success
     * page : 1
     * pagecount : 1
     * data : [{"id":"15","title":"测试1","thumbs":"a:3:{i:0;s:36:\"attachment/201702/14881644706826.jpg\";i:1;s:36:\"attachment/201702/14881644723021.jpg\";i:2;s:36:\"attachment/201702/14881644763218.jpg\";}","user_id":"1","description":"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊","content":"啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈","inputtime":"1488164892","updatetime":null,"q_id":"7","click":null,"s_tj":"0","l_tj":"1","q_name":"A病","user_name":"小白","url":"192.168.199.204/Home/Index/forum/id/15"},{"id":"16","title":"测试的啊","thumbs":"a:1:{i:0;s:36:\"attachment/201702/14881644723021.jpg\";}","user_id":"1","description":"sdfsdfsdfsdfsdfdsfds","content":"sdfsdfsdfsdfsdfdsfdsfsf","inputtime":"1488181314","updatetime":null,"q_id":"7","click":null,"s_tj":"1","l_tj":"1","q_name":"A病","user_name":"小白","url":"192.168.199.204/Home/Index/forum/id/16"}]
     */

    public int code;
    public String status;
    public String page;
    public int pagecount;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * id : 15
         * title : 测试1
         * thumbs : a:3:{i:0;s:36:"attachment/201702/14881644706826.jpg";i:1;s:36:"attachment/201702/14881644723021.jpg";i:2;s:36:"attachment/201702/14881644763218.jpg";}
         * user_id : 1
         * description : 啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊
         * content : 啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈
         * inputtime : 1488164892
         * updatetime : null
         * q_id : 7
         * click : null
         * s_tj : 0
         * l_tj : 1
         * q_name : A病
         * user_name : 小白
         * url : 192.168.199.204/Home/Index/forum/id/15
         *
         * author	14
         *
         avatar	/attachment/201705/590aa564481ef.png
         content	山西龙首镇有个村子
         description	山西龙首镇有
         id	8
         imgs	Array
         inputtime	1493889407
         l_tj	0
         q_id	7
         q_name	AA病
         s_tj	0
         status	0
         thumbs	Array
         title	震惊！！！女子这样赚钱,一年净赚100w。
         type	2
         updatetime	1493889407
         url	api.green-bud.cn/Home/Index/forum/id/8
         user_name	得到
         */
        public String author;
        public String avatar;
        public String content;
        public String description;
        public String id;
        public List<String> imgs;
        public String inputtime;
        public String l_tj;
        public String q_id;
        public String q_name;
        public String s_tj;
        public String status;
        public List<String> thumbs;
        public String title;
        public String type;
        public String updatetime;
        public String url;
        public String user_name;
    }
}




