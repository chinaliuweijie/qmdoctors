package com.qingmiao.qmdoctor.bean;

// FIXME generate failure  field _$Status323
// FIXME generate failure  field _$Data261

import java.util.List;

/**
 * Created by Administrator on 2017/3/22.
 */

public class AddUserInfo {

    /**
     * code : 0
     * data : {"nomal_u":[{"avatar":"avatar","nickname":"书中自有颜如玉","remark_names":"书中自有颜如玉","uid":"61"}]}
     * status : success
     */

    private int code;
    private DataBean data;
    private String status;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class DataBean {
        private List<NomalUBean> nomal_u;

        public List<NomalUBean> getNomal_u() {
            return nomal_u;
        }

        public void setNomal_u(List<NomalUBean> nomal_u) {
            this.nomal_u = nomal_u;
        }

        public static class NomalUBean {
            /**
             * avatar : avatar
             * nickname : 书中自有颜如玉
             * remark_names : 书中自有颜如玉
             * uid : 61
             */

            private String avatar;
            private String nickname;
            private String remark_names;
            private String uid;
            private String sortLetters;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getRemark_names() {
                return remark_names;
            }

            public void setRemark_names(String remark_names) {
                this.remark_names = remark_names;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getSortLetters() {
                return sortLetters;
            }
            public void setSortLetters(String sortLetters) {
                this.sortLetters = sortLetters;
            }
        }
    }
}
