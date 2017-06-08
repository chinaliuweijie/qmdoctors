package com.qingmiao.qmdoctor.activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.hyphenate.easeui.utils.GlideUtils;
import com.mylhyl.superdialog.SuperDialog;
import com.nanchen.compresshelper.CompressHelper;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.DoctorDataBean;
import com.qingmiao.qmdoctor.bean.HXUserData;
import com.qingmiao.qmdoctor.bean.LoginBean;
import com.qingmiao.qmdoctor.bean.PicBean;
import com.qingmiao.qmdoctor.bean.ProfessionalFieldBean;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.BitmapUtils;
import com.qingmiao.qmdoctor.utils.FileUtil;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;

public class DoctorDataActivity extends BaseActivity implements ILibelInfoView{
    @BindView(R.id.medata_icon_iv)
    ImageView ivMedataIcon;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.medata_name_tv)
    EditText medataName;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.ll_sex)
    LinearLayout llSex;
    @BindView(R.id.et_hospital)
    EditText etHospital;
    @BindView(R.id.et_course)
    EditText etCourse;
    @BindView(R.id.tv_position)
    TextView tvPosition;
    @BindView(R.id.tv_down)
    IconFontTextview tvDown;
    @BindView(R.id.ll_field0)
    LinearLayout llField0;
    @BindView(R.id.ll_field1)
    LinearLayout llField1;
    @BindView(R.id.ll_check)
    LinearLayout llCheck;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.btn_affirm)
    Button btnAffirm;
    @BindView(R.id.ll_photo)
    LinearLayout llPhoto;
    @BindView(R.id.ll_hide_photo)
    LinearLayout llHidePhoto;
    @BindView(R.id.tv_photo_down)
    IconFontTextview tvPhotoDown;
    @BindView(R.id.et_opinion)
    EditText etOpinion;
    private String nickname;
    private String name;
    private String hospotal;
    private String course;
    private String position;
    private String opinion;
    private String did;
    private String token;
    private List<ProfessionalFieldBean.ProfessionalData> professionalDatas = new ArrayList<>();
    private StringBuffer sb = new StringBuffer();
    private boolean ischecked = false;
    private boolean isPhoto = true;
    private String sex = "0";
    private LoginBean loginBean = null;
    private List<String> ids = new ArrayList<String>();
    DoctorDataBean doctorDataBean;
    ProfessionalFieldBean professionalFieldBean;
    private String avatarUri = null;
    //证书路劲
    private String certificateUri = null;
    private LibelInfoPresenter infoPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_data);
        loginBean =  (LoginBean) PrefUtils.getBean(this, KeyOrValueGlobal.LOGINBEAN);
        if(loginBean!=null){
            did = loginBean.did;
            token = loginBean.token;
        }
        String phone =  PrefUtils.getString(this,"username","");
        tvPhone.setText(phone);
        initView();
    }

    private void initView() {
        // 获取医生本人的信息
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("did",did);
        linkedHashMap.put("token",token);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        startLoad(UrlGlobal.GET_DOCTOR_SELF_INFO,linkedHashMap);
        infoPresenter = new LibelInfoPresenter(this);
    }

    @Override
    public void initData(String data) {
        LogUtil.LogShitou(data);
        // 得到用户数据
        doctorDataBean = GsonUtil.getInstance().fromJson(data, DoctorDataBean.class);
        setViewData(doctorDataBean);
    }

    private void loadProfessions(String data) {
        professionalFieldBean = GsonUtil.getInstance().fromJson(data, ProfessionalFieldBean.class);
        if (professionalFieldBean.code == 0) {
            for (int i = 0; i < professionalFieldBean.data.size(); i++) {
                final CheckBox cb = (CheckBox) View.inflate(this, R.layout.view_doctor_profession, null);
                cb.setTag(professionalFieldBean.data.get(i));
                cb.setText(professionalFieldBean.data.get(i).disease_name);
                if (i % 2 == 0) {
                    llField0.addView(cb);
                } else if (i % 2 == 1) {
                    llField1.addView(cb);
                }
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            ProfessionalFieldBean.ProfessionalData professionalData = (ProfessionalFieldBean.ProfessionalData) cb.getTag();
                            professionalDatas.add(professionalData);
                        } else {
                            ProfessionalFieldBean.ProfessionalData professionalData = (ProfessionalFieldBean.ProfessionalData) cb.getTag();
                            if (professionalDatas.contains(professionalData)) {
                                professionalDatas.remove(professionalData);
                            }
                        }
                    }
                });


                if (ids != null && ids.contains(professionalFieldBean.data.get(i).id)) {
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }
            }
        }
    }

    private void setViewData(DoctorDataBean doctorDataBean){
        if(doctorDataBean==null || doctorDataBean.data == null){
            return ;
        }
        tvPhone.setText(doctorDataBean.data.mobile);
        medataName.setText(doctorDataBean.data.nickname);
        etName.setText(doctorDataBean.data.d_name);
        tvSex.setText(doctorDataBean.data.sex.equals("0")?"男":"女");
        etHospital.setText(doctorDataBean.data.hospital);
        etCourse.setText(doctorDataBean.data.department);
        tvPosition.setText(doctorDataBean.data.job_title);
        if(doctorDataBean.data.professional_field!=null) {
            String[] strIds = doctorDataBean.data.professional_field.split(",");
            if (strIds != null) {
                for (int i = 0; i < strIds.length; i++) {
                    ids.add(strIds[i]);
                }
            }
        }
        if(!TextUtils.isEmpty(doctorDataBean.data.certificate)){
            Glide.with(this).load(doctorDataBean.data.certificate).centerCrop().error(R.drawable.icon_photo).into(ivPhoto);
        }
        if(!TextUtils.isEmpty(doctorDataBean.data.avatar)){
            Glide.with(this).load(doctorDataBean.data.avatar).centerCrop().error(R.drawable.ic_doctor_avatar).into(ivMedataIcon);
        }
        etOpinion.setText(doctorDataBean.data.desc);
        // 判断状态
        if("0".equals(doctorDataBean.data.status)){
            // 新注册的用户
            ToastUtils.showLongToast(this,"请上传资质证明");
        }else if("1".equals(doctorDataBean.data.status)){
            // 审核中
            startDialog();
        }else if("2".equals(doctorDataBean.data.status)){
            // 审核成功
        }else if("3".equals(doctorDataBean.data.status)){
            // 审核失败
            startDialog();
        }
        if (professionalFieldBean == null) {
            llCheck.setVisibility(View.GONE);
            tvDown.setText(R.string.icons_down);
            ischecked = false;
        } else if(professionalFieldBean != null){
                // 请求数据
            llCheck.setVisibility(View.VISIBLE);
            tvDown.setText(R.string.icons_back_right);
            ischecked = true;
            }
    }





    @OnClick({R.id.et_name, R.id.ll_sex,R.id.ll_photo,
            R.id.ll_nichen, R.id.ll_down, R.id.iv_photo, R.id.btn_affirm,R.id.medata_icon_iv})
    public void onClick(View view) {
        try{
            switch (view.getId()) {
                case R.id.et_name:

                    break;
                case R.id.ll_sex:
                    // 隐藏输入法
                    hideSoftKeyboard();
                    int[] contentPadding = {20, 0, 20, 20};
                    new SuperDialog.Builder(this).setTitle("提示",getResources().getColor(R.color.black_1), (int) getResources().getDimension(R.dimen.tv_sitem_title))
                            .setBackgroundColor(getResources().getColor(R.color.white)).setMessage("选择性别",getResources().getColor(R.color.black_1),(int) getResources().getDimension(R.dimen.tv_sitem_content),contentPadding)
                            .setNegativeButton("男", getResources().getColor(R.color.green),(int) getResources().getDimension(R.dimen.tv_sitem_title),-1,new SuperDialog.OnClickNegativeListener(){

                                @Override
                                public void onClick(View v) {
                                    tvSex.setText(R.string.text_boy);
                                    sex = "0";
                                }
                            }).setWidth(0.7f)
                            .setPositiveButton("女", getResources().getColor(R.color.green),(int) getResources().getDimension(R.dimen.tv_sitem_title),-1,new SuperDialog.OnClickPositiveListener() {
                                @Override
                                public void onClick(View v) {
                                    tvSex.setText(R.string.text_woman);
                                    sex = "1";
                                }
                            }).build();
                    break;
                case R.id.ll_nichen:
                    // @"住院医师",@"主治医师",@"副主任医师",@"主任医师"
                    hideSoftKeyboard();
                    final String [] strs = {"住院医师","主治医师","副主任医师","主任医师"};
                    new SuperDialog.Builder(this)
                            .setBackgroundColor(getResources().getColor(R.color.white))
                            .setTitle("选择职称", getResources().getColor(R.color.text), (int) getResources().getDimension(R.dimen.tv_sview_title))
                            .setCanceledOnTouchOutside(true)
                            .setWidth(1.0f)
                            .setRadius(10)
                            //setItems默认是底部位置
                            .setItems(strs,getResources().getColor(R.color.black_1), (int) getResources().getDimension(R.dimen.tv_sitem_title), new SuperDialog.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    tvPosition.setText(strs[position]);
                                }
                            })
                            //.setGravity(Gravity.CENTER)请在setItems之后设置setGravity
                            .setNegativeButton("取消选择",getResources().getColor(R.color.black_1), (int) getResources().getDimension(R.dimen.tv_sitem_title),-1, null)
                            .setConfigDialog(new SuperDialog.ConfigDialog() {
                                @Override
                                public void onConfig(Dialog dialog, Window window, WindowManager
                                        .LayoutParams wlp, DisplayMetrics dm) {
                                    //window.setWindowAnimations(R.style.dialogWindowAnim);
                                    wlp.y = 0;//底部距离
                                    window.setBackgroundDrawableResource(R.color.backdrop);
                                }
                            })
                            .setItemsBottomMargin(20)
                            .setWindowAnimations(R.style.dialogWindowAnim)//动画
                            .build();
                    break;
                case R.id.ll_photo:
//                if (isPhoto) {
//                    llHidePhoto.setVisibility(View.GONE);
//                    tvPhotoDown.setText(R.string.icons_back_right);
//                    isPhoto = false;
//                } else {
//                    llHidePhoto.setVisibility(View.VISIBLE);
//                    tvPhotoDown.setText(R.string.icons_down);
//                    isPhoto = true;
//                }
                    break;
                case R.id.ll_down:
                    if (ischecked) {
                        llCheck.setVisibility(View.GONE);
                        tvDown.setText(R.string.icons_down);
                        ischecked = false;
                    } else {
                        if(professionalFieldBean == null){
                            // 请求数据
                            LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
                            linkedHashMap.put("did",did);
                            linkedHashMap.put("token",token);
                            linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                            infoPresenter.startLoad(UrlGlobal.GET_PROFESSIONAL_FIELD,linkedHashMap);
                        }
                        llCheck.setVisibility(View.VISIBLE);
                        tvDown.setText(R.string.icons_back_right);
                        ischecked = true;
                    }
                    break;
                case R.id.iv_photo:
                    //0新注册用户1待审核2审核通过3审核失败
                    if(TextUtils.isEmpty(doctorDataBean.data.certificate)|| "0".equals(doctorDataBean.data.status) || "3".equals(doctorDataBean.data.status)){
                        PhotoPicker.builder()
                                .setPhotoCount(1)
                                .setShowCamera(true)
                                .setShowGif(true)
                                .setPreviewEnabled(false)
                                .start(this, PhotoPicker.REQUEST_CODE);
                    }else if("1".equals(doctorDataBean.data.status)){
                        startDialog();
                    }else if("3".equals(doctorDataBean.data.status)){
                        startDialog();
                    }
                    break;
                case R.id.btn_affirm:
                    nickname = medataName.getText().toString().trim();
                    name = etName.getText().toString().trim();
                    hospotal = etHospital.getText().toString().trim();
                    course = etCourse.getText().toString().trim();
                    position = tvPosition.getText().toString().trim();
                    opinion = etOpinion.getText().toString().trim();

                    for (int i = 0; i <professionalDatas.size() ; i++) {
                        sb.append(professionalDatas.get(i).id+",");
                    }
                    if(!TextUtils.isEmpty(sb)){
                        sb.deleteCharAt(sb.length() - 1);
                    }

                    initHttp();
                    break;
                case R.id.medata_icon_iv:
                    PhotoPicker.builder()
                            .setPhotoCount(1)
                            .setShowCamera(true)
                            .setShowGif(true)
                            .setPreviewEnabled(false)
                            .start(this, PhotoPicker.REQUEST_CODE+1);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Glide.with(this)
                        .load(photos.get(0)).centerCrop()
                        .into(ivPhoto);
                certificateUri = photos.get(0);
            }
            return ;
        }else if(resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE+1){
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Glide.with(this)
                        .load(photos.get(0)).centerCrop()
                        .into(ivMedataIcon);
                avatarUri = photos.get(0);
            }
            return ;
        }

    }



    /**
     * 上传头像，如果成功后，需要上传证书，在上传证书
     */
    private void upLoadAvatarPic(String fileName,String filePath){
        File newFile = null;
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            newFile = CompressHelper.getDefault(this).compressToFile(file);
        }else{
            newFile = new File(filePath);
        }
        OkHttpUtils.post()
                .addFile("file",fileName,newFile)
                .url(UrlGlobal.UPLOAD_PIC)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissLoadDialog();
                        ToastUtils.showLongToast(DoctorDataActivity.this,"上传头像失败,请重新上传.");
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        PicBean picBean = GsonUtil.getInstance().fromJson(response,PicBean.class);
                        // 检查有没有证书上传
                        if(picBean.code!=0){
                            ToastUtils.showLongToast(DoctorDataActivity.this,"上传头像失败,请重新上传."+picBean.status);
                            return;
                        }
                        if(!TextUtils.isEmpty(certificateUri)){
                            // 上传证书
                            upLoadCertificatePic("avatarImage.jpg",certificateUri,picBean);
                        }else{
                            initAllData(picBean,null);
                        }

                    }
                });
    }


    private void upLoadCertificatePic(String fileName, String filePath, final PicBean acatarBean){
        File newFile = null;
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            newFile = CompressHelper.getDefault(this).compressToFile(file);
        }else{
            newFile = new File(filePath);
        }
        OkHttpUtils.post()
                .addFile("file",fileName,newFile)
                .url(UrlGlobal.UPLOAD_PIC)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissLoadDialog();
                        ToastUtils.showLongToast(DoctorDataActivity.this,"上传证书失败,请重新上传."+e.getMessage());
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            PicBean certificateBean = GsonUtil.getInstance().fromJson(response,PicBean.class);
                            if(certificateBean.code!=0){
                                ToastUtils.showLongToast(DoctorDataActivity.this,"上传证书失败,请重新上传."+certificateBean.msg);
                                return;
                            }
                            initAllData(acatarBean,certificateBean);
                        }catch (Exception e){
                            e.printStackTrace();
                            ToastUtils.showLongToast(DoctorDataActivity.this,"上传证书失败,请重新上传."+e.getMessage() +id);
                            return;
                        }
                    }
                });
    }


    private void initAllData(final PicBean acatarBean,PicBean certificateBean){

        LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
        params.put("did",did);
        params.put("token",token);
        params.put("d_name",name);
        params.put("sex",sex);
        params.put("hospital",hospotal);
        params.put("department",course);
        params.put("professional_field", sb.toString());
        params.put("job_title",position);
        params.put("desc",opinion);
        params.put("nickname",nickname);
        params.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        if(acatarBean!=null){
            params.put("avatar",acatarBean.thumb_url);
        }else{
            try {
                if(doctorDataBean.data.avatar!=null) {
                    params.put("avatar", doctorDataBean.data.avatar);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(certificateBean!=null){
            params.put("certificate",certificateBean.thumb_url);
        }else{
//            try {
//                if(doctorDataBean.data.certificate!=null) {
//                    params.put("certificate", doctorDataBean.data.certificate.replaceAll(UrlGlobal.SERVER_URL,""));
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
        // 上传所有数据
        OkHttpUtils.post()
                .url(UrlGlobal.SAVE_USER)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(DoctorDataActivity.this,"上传资料失败,请重新上传.");
                        dismissLoadDialog();
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Result result = GsonUtil.getInstance().fromJson(response,Result.class);
                        if(result.code == 0){
                            //保存用户的信息到数据库
                            List<HXUserData> mData = DataSupport.where("did = ?", did).find(HXUserData.class);
                            HXUserData hxUserData = new HXUserData();
                            hxUserData.setUser_name(name);
                            hxUserData.setNickname(nickname);
                            hxUserData.setDid(did);
                            LoginBean login = app.getLogin();
                            if(login!=null){
                                hxUserData.setHx_name(login.hx_uname);
                            }else{
                                if(mData.size()>0){
                                    try {
                                        hxUserData.setHx_name(mData.get(0).getHx_name());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                            try {
                                if(acatarBean!=null){
                                    hxUserData.setAvatar(UrlGlobal.SERVER_URL+acatarBean.thumb_url);
                                }else{
                                    hxUserData.setAvatar(doctorDataBean.data.avatar);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if(mData.size() == 0){
                                hxUserData.save();
                            }else{
                                hxUserData.updateAll("did = ?", did);
                            }
                        //    hxUserData.setTellphone();
                            EventBus.getDefault().post("changeData");
//                            if("0".equals(doctorDataBean.data.status)){
//                                // 再次请求接口
//                                LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
//                                linkedHashMap.put("did",did);
//                                linkedHashMap.put("token",token);
//                                linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
//                                infoPresenter.startLoad(UrlGlobal.GET_DOCTOR_SELF_INFO,linkedHashMap);
//                            }else{
//                                dismissLoadDialog();
//                            }
                            finish();
                            dismissLoadDialog();
                        }else{
                            dismissLoadDialog();
                            ToastUtils.showLongToast(DoctorDataActivity.this,result.msg);
                        }

                    }
                });
    }




    public void initHttp() {
        showLoadingDialog(null,"上传中");
        // 先上传图片  拿到string 后在上传数据
        if(!TextUtils.isEmpty(avatarUri)){
            upLoadAvatarPic("touxiang.jpg",avatarUri);
        }else if(!TextUtils.isEmpty(certificateUri)){
            // 上传证书
            upLoadCertificatePic("avatarImage.jpg",certificateUri,null);
        }else {
            initAllData(null,null);
        }
    }

    @Override
    public void showLibelProgress(String uri) {
        if(UrlGlobal.GET_PROFESSIONAL_FIELD.equals(uri)){
            showLoadingDialog(uri,"加载中");
        }

    }

    @Override
    public void hideLibelProgress(String uri) {
        if(UrlGlobal.GET_PROFESSIONAL_FIELD.equals(uri)){
            dismissLoadDialog();
        }
    }


    @Override
    public void getLibelData(String uri, String data) {
        if(UrlGlobal.GET_PROFESSIONAL_FIELD.equals(uri)){
            loadProfessions(data);
        }else if(UrlGlobal.GET_DOCTOR_SELF_INFO.equals(uri)){
            doctorDataBean = GsonUtil.getInstance().fromJson(data, DoctorDataBean.class);
            setViewData(doctorDataBean);
            dismissLoadDialog();
        }
    }



    private void startDialog() {
        final android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(this, R.style.dialog).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Window win = dialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.5
        lp.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.65
        win.setAttributes(lp);
        if("1".equals(doctorDataBean.data.status)){
            View view = View.inflate(this, R.layout.dialog_audit_hint, null);
            win.setContentView(view);
            view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }else if("3".equals(doctorDataBean.data.status)){
            View view = View.inflate(this, R.layout.dialog_audit_fail, null);
            win.setContentView(view);
            view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }
}