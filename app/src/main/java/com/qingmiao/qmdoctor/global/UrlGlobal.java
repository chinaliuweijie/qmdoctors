package com.qingmiao.qmdoctor.global;
/**
 * company : 青苗
 * Created by 姬鹏杰 on 17/3/14
 */
public class UrlGlobal {
    //服务器根地址
     public static final String SERVER_URL = "https://api.green-bud.cn";
    // 新的测试地址
    // public static final String SERVER_URL = "http://192.168.70.52";

    //医生登录接口
    public static final String LOGIN_URL = SERVER_URL + "/Api/Doctor/login";
    //医生注册接口   第三方注册
    public static final String REGISTER_URL = SERVER_URL + "/Api/Doctor/register";
    public static final String AUTH_REGISTER_URL = SERVER_URL + "/Api/Doctor/authRegister";

    //医生退出登录
    public static final String LOGOUT_URL = SERVER_URL + "/Api/Doctor/logout";
    //保存医生信息
    public static final String SAVE_USER = SERVER_URL + "/Api/Doctor/saveDoctorInfo";
    //发送短信验证
    public static final String SENDMESSAGE = SERVER_URL + "/Api/User/sendCode";
    //患者修改 重置密码
    public static final String RESETPASSWORD = SERVER_URL + "/Api/User/renewPassword";
    //用户获取本人信息
    public static final String USERGET = SERVER_URL + "/Api/User/getUserSelfInfo";
    //发布圈子接口地址
    public static final String CATEGORY_URL = SERVER_URL + "/Api/Community/circleAdd";
    //获取资讯列表
    public static final String PHOTOS_URL = SERVER_URL + "/Api/Community/getInformationList";
    // 新的首页获取资讯
    public static final String HOME_DOCINDEX = SERVER_URL + "/Api/Community/docIndex" ;

    //  获取患者信息
    public static final String GETUSERMESSAGE = SERVER_URL + "/Api/Doctor/getPatientInfomation";
    public static final String ADDUSERMESSAGE = SERVER_URL + "/Api/Doctor/addPatientFriends";
    public static final String GETUSERFRIEND = SERVER_URL + "/Api/Doctor/getPatientFriendsList";
    //获取圈子分类
    public static final String CLASSIFY_URL = SERVER_URL + "/Api/Community/getCircleCate";
    //获取圈子列表
    public static final String LIST_URL = SERVER_URL + "/Api/Community/getCircleList";
    //用户进行评论咨询
    public static final String COMMENT_URL = SERVER_URL + "/Api/Community/informationCommentAdd";
    //用户进行评论圈子
    public static final String COMMENT_RING_URL = SERVER_URL + "/Api/Community/circleCommentAdd";
    //标星好友
    public static final String PATIENT_STAR = SERVER_URL + "/Api/Doctor/markedPatientFriends";
    //投诉患者
    public static final String PATIENT_COMPLAIN = SERVER_URL + "/Api/Doctor/complainPatient";
    //删除患者好友
    public static final String DELETE_PATIENT = SERVER_URL + "/Api/Doctor/delPatientFriends";
    //取消标星
    public static final String PATIENT_STAR_CANCEL = SERVER_URL + "/Api/Doctor/delMarkedPatientFriends";
    //获取患者病情描述列表
    public static final String GET_PATIENT_DESCLIST = SERVER_URL + "/Api/Doctor/getPatientSickDescList";
    //获取患者信息 患者的标签 患者的病情描述 患者的检查病例
    public static final String GET_PATIENT_INFO = SERVER_URL + "/Api/Doctor/getPatientAllInfo";
    //为患者添加病情描述
    public static final String SET_PATIENT_DESC = SERVER_URL + "/Api/Doctor/patientSickDescAdd";


    //添加标签
    public static final String ADD_LABEL = SERVER_URL + "/Api/Doctor/addTags";
    //医生获取本人信息
    public static final String GET_DOCTOR_SELF_INFO = SERVER_URL + "/Api/Doctor/getDoctorSelfInfo";
    //上传图片
    public static final String UPLOAD_PIC = SERVER_URL + "/Api/Community/uploadPic";
    //上传语音
    public static final String UPLOAD_SOUND = SERVER_URL + "/Api/Community/uploadSound";
    //获取标签列表
    public static final String GET_TAGSLIST = SERVER_URL + "/Api/Doctor/getTagsList";
    //删除标签
    public static final String DELETE_TAGS = SERVER_URL + "/Api/Doctor/deleteTags";
    //显示标签
    public static final String GET_SHOWTAGS = SERVER_URL + "/Api/Doctor/showTags";
    //修改标签
    public static final String UPDATA_EDIT_TAGS = SERVER_URL + "/Api/Doctor/editTags";
    // 患者好友添加备注
    public static final String REMARK_PATIENT = SERVER_URL + "/Api/Doctor/remarkPatient";
    // 删除患者病情描述
    public static final String DELPATIENT_SICKDESC = SERVER_URL + "/Api/Doctor/delPatientSickDesc";

    public static final String GET_USERSICK_CHECKITEMDATA = SERVER_URL + "/Api/Index/getUserSickCheckItemData";
    // 添加收藏
    public static final String ADD_COLLECTION = SERVER_URL + "/Api/Community/collectionAdd";
    // 获取咨询评论
    public static final String GET_INFORMATION_COMMENT = SERVER_URL + "/Api/Community/getInformationComment";
    //  重置密码
    public static final String RENEW_PASSWORD = SERVER_URL + "/Api/User/renewPassword";
    // 意见反馈
    public static final String SUGGESTION_ADD = SERVER_URL + "/Api/Community/suggestionAdd";
    // 本地头像地址
    // public static final String LOCATION_PIC = Environment.getExternalStorageDirectory() + "/qmtx/" + "touxiang.jpg";
    // 获取版本号
    public static final String GET_VERSION = SERVER_URL + "/Api/Index/getVersion";
    // 获取收藏
    public static final String GET_COLLECTION = SERVER_URL + "/Api/Community/getCollection" ;
    // 获取医生擅长的领域
    public static final String GET_PROFESSIONAL_FIELD = SERVER_URL + "/Api/Doctor/getProfessionalField" ;
    // 是否已经收藏
    public static final String IF_COLLECTION =  SERVER_URL + "/Api/Community/ifCollection";
    //删除收藏
    public static final String DEL_COLLECTION = SERVER_URL + "/Api/Community/delCollection" ;
    // 用户协议
    public static final String USER_RULE = SERVER_URL + "/index.php/Home/Index/rule" ;
    // 功能介绍
    public static final String FUNCINTRO = SERVER_URL + "/Home/Index/funcintro" ;
    // 系统通知
    public static final String SYSNOT = SERVER_URL + "/Home/Index/sysnot" ;
    // 获取资讯列表
    public static final String GET_INFORMATIONLIST = SERVER_URL + "/Api/Community/getInformationList" ;
    // 工具箱
    public static final String TOOLS = SERVER_URL +"/html_statics/medical_tools.html";
    // 获取医生圈子分类
    public static final String GETDOCCIRCLE_CATE = SERVER_URL + "/Api/Community/getDocCircleCate" ;
    //获取病况列表
    public static final String GET_SICKLIST = SERVER_URL + "/Api/Doctor/getSickList" ;
    //获取用户某一病况的检查项目
    public static final String GET_CHECK_ITEM_DATA = SERVER_URL + "/Api/Doctor/getUserCheckItemData" ;
    // 获取最新好友
    public static final String GET_NEW_FRIENDS = SERVER_URL + "/Api/Doctor/getNewFriends" ;


    //public static final String text = SERVER_URL + "/Api/Index/changeUserSick" ;
}
