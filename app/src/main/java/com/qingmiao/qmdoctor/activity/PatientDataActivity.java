package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.google.gson.internal.LinkedTreeMap;
import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.PatientCaseTestAdapter;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.HXUserData;
import com.qingmiao.qmdoctor.bean.PatientAllInfoBean;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.qingmiao.qmdoctor.widget.InterceptTouchLinearLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;

public class PatientDataActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.patientdata_dos_ll)
    InterceptTouchLinearLayout patientdataDosLl;
    @BindView(R.id.ll_patientdata_case_test)
    InterceptTouchLinearLayout llPatientdataCaseTest;

    @BindView(R.id.patientdata_icon_iv)
    ImageView patientdataIconIv;
    @BindView(R.id.patientdata_name_tvT)
    TextView patientdataNameTvT;
    @BindView(R.id.patientdata_name_tv)
    TextView patientdataNameTv;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_lable)
    TextView tvLable ;
    @BindView(R.id.rv_sick_desc)
    LRecyclerView rvSickDesc ;
    private ListBaseAdapter<PatientAllInfoBean.PatientData.PatientSickDesc> patientSickDescAdapter;
    @BindView(R.id.rl_patient_info)
    RelativeLayout rlPatientInfo ;
    @BindView(R.id.activity_patient_data)
    RelativeLayout activityPatientData;
    @BindView(R.id.btn_addorsend)
    Button btnAddOrSend;
    @BindView(R.id.rv_case_test)
    LRecyclerView recyclerviewCaseTest;
    private PatientCaseTestAdapter patientCaseTestAdapter;
    @BindView(R.id.iv_title)
    IconFontTextview ivStarTitle;
    private AlertDialog dialog;
    String uid,isFriend,isMarked;
    private String hx_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_data);
        getParameter();
        initView();
        patientdataDosLl.setOnClickListener(this);
        btnAddOrSend.setOnClickListener(this);
        patientdataNameTvT.setOnClickListener(this);
        rlPatientInfo.setOnClickListener(this);
        llPatientdataCaseTest.setOnClickListener(this);
        findViewById(R.id.ll_label).setOnClickListener(this);
        ivRight.setText(R.string.icons_more);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
            }
        });
        getDataFormService();
        EventBus.getDefault().register(this);//在当前界面注册一个订阅者
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {
        if("updataLabeList".equals(event) || "updataPatient".equals(event) || "updataPatientData".equals(event)) {
            getDataFormService();
        }
    }

    private void initView() {
        initRecycleView();
        tvCenter.setText("患者资料");
    }

    private void initRecycleView() {
        recyclerviewCaseTest.setLayoutManager(new LinearLayoutManager(this));
        recyclerviewCaseTest.setItemAnimator(new DefaultItemAnimator());
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setColorResource(R.color.text)
                .build();
        recyclerviewCaseTest.addItemDecoration(divider);
        patientCaseTestAdapter = new PatientCaseTestAdapter(this);
        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(patientCaseTestAdapter);
        recyclerviewCaseTest.setAdapter(mLRecyclerViewAdapter);
        recyclerviewCaseTest.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerviewCaseTest.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        recyclerviewCaseTest.setPullRefreshEnabled(false);
        mLRecyclerViewAdapter.removeFooterView();
        rvSickDesc.addItemDecoration(divider);
        rvSickDesc.setLayoutManager(new LinearLayoutManager(this));
        rvSickDesc.setItemAnimator(new DefaultItemAnimator());
        patientSickDescAdapter = new ListBaseAdapter<PatientAllInfoBean.PatientData.PatientSickDesc>(this) {

            @Override
            public int getLayoutId() {
                return R.layout.view_patient_deck_item;
            }

            @Override
            public void onBindItemHolder(SuperViewHolder holder, int position) {
                PatientAllInfoBean.PatientData.PatientSickDesc patientSickDesc = getDataList().get(position);
                TextView tvSickSesc = holder.getView(R.id.tv_sick_desc);
                IconFontTextview icon = holder.getView(R.id.icon);
                TextView tvTime = holder.getView(R.id.tv_voice_time);
                if(!TextUtils.isEmpty(patientSickDesc.pic)){
                    icon.setText(R.string.icons_pic);
                    icon.setVisibility(View.VISIBLE);
                    tvSickSesc.setText("图片");
                    tvTime.setVisibility(View.GONE);
                }else if(!TextUtils.isEmpty(patientSickDesc.sound)){
                    icon.setText(R.string.icons_xinhao);
                    icon.setVisibility(View.VISIBLE);
                    tvSickSesc.setText("语音");
                    tvTime.setVisibility(View.VISIBLE);
                    tvTime.setText(patientSickDesc.sound_time+"s");
                }else{
                    tvSickSesc.setText(getDataList().get(position).sick_desc);
                    tvTime.setVisibility(View.GONE);
                    icon.setVisibility(View.GONE);
                }
            }
        };

        LRecyclerViewAdapter SickDescAdapter = new LRecyclerViewAdapter(patientSickDescAdapter);
        rvSickDesc.setAdapter(SickDescAdapter);
        rvSickDesc.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        rvSickDesc.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        rvSickDesc.setPullRefreshEnabled(false);
        SickDescAdapter.removeFooterView();
    }

    private void getParameter() {
        uid = this.getIntent().getStringExtra("uid");
        isFriend = this.getIntent().getStringExtra("isFriend");
        isMarked = this.getIntent().getStringExtra("isMarked");
        // 查询数据库
        if(TextUtils.isEmpty(isFriend) || TextUtils.isEmpty(isMarked)){
            List<HXUserData> userDatas = DataSupport.where("uid = ?", uid).find(HXUserData.class);
            isFriend = userDatas.get(0).getIsFriend();
            isMarked =  userDatas.get(0).getIsMarked();
        }
        if(TextUtils.isEmpty(isFriend) ){
            isFriend = "2";
        }
        if(TextUtils.isEmpty(isMarked) ){
            isMarked = "2";
        }
        if("1".equals(isFriend) && "1".equals(isMarked)){
            // 标新 好友
            ivStarTitle.setTextColor(getResources().getColor(R.color.yellow));
        }else{
            ivStarTitle.setTextColor(getResources().getColor(R.color.gray));
        }
        if("1".equals(isFriend)){
            // 是好友
            btnAddOrSend.setText("发送消息");
        }else if("2".equals(isFriend)){
            // 不是好友
            btnAddOrSend.setText("添加好友");
        }
    }

    @Override
    public void initData(String data) {
        super.initData(data);
        PatientAllInfoBean patientInfoBean = GsonUtil.getInstance().fromJson(data, PatientAllInfoBean.class);
        if(patientInfoBean.code == 0){
            if(patientInfoBean.data.user_sick!=null) {
                patientCaseTestAdapter.setDataList(patientInfoBean.data.user_sick);
                patientCaseTestAdapter.notifyDataSetChanged();
            }
            // 添加别的信息
            if(patientInfoBean.data.u_tag!=null){
                StringBuffer sb = new StringBuffer();
                if(patientInfoBean.data.u_tag instanceof List){
                List<String> stringList = (List<String>) patientInfoBean.data.u_tag;
                for (int i = 0; i <stringList.size() ; i++) {
                   sb.append(stringList.get(i)+",");
                }
                if(!TextUtils.isEmpty(sb)){
                    sb.deleteCharAt(sb.length() - 1);
                }
                }else if(patientInfoBean.data.u_tag instanceof LinkedTreeMap){
                    LinkedTreeMap linkedTreeMap = (LinkedTreeMap) patientInfoBean.data.u_tag;
                    Iterator it = linkedTreeMap.keySet().iterator();
                    while (it.hasNext()) {
                        //it.next()得到的是key，tm.get(key)得到obj
                        sb.append(linkedTreeMap.get(it.next())+",");
                    }
                    if(!TextUtils.isEmpty(sb)){
                        sb.deleteCharAt(sb.length() - 1);
                    }
                }
                tvLable.setText(sb.toString());
            }
            if(patientInfoBean.data.sick_desc!=null){
                patientSickDescAdapter.setDataList(patientInfoBean.data.sick_desc);
                patientSickDescAdapter.notifyDataSetChanged();
            }

            if(patientInfoBean.data.u_info!=null){
                GlideUtils.LoadAvatarImageView(this,patientInfoBean.data.u_info.avatar,patientdataIconIv);
                patientdataNameTvT.setText(patientInfoBean.data.u_info.user_name);
                patientdataNameTv.setText("账号:"+patientInfoBean.data.u_info.mobile);
                tvPhone.setText("昵称:"+patientInfoBean.data.u_info.nickname);
                hx_name = patientInfoBean.data.u_info.hx_uname;
            }
        }else{
            ToastUtils.showLongToast(this,patientInfoBean.msg);
        }
    }

    private void getDataFormService() {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("did",did);
        linkedHashMap.put("token",token);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("uid",uid);
        linkedHashMap.put("limit",1+"");
        startLoad(UrlGlobal.GET_PATIENT_INFO,linkedHashMap);
    }

    // 显示底部dialog
    private void startDialog() {
        dialog = new AlertDialog.Builder(this, R.style.dialog).create();
        dialog.show();
        Window win = dialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        View view = View.inflate(this, R.layout.patient_data_more, null);
        win.setContentView(view);
        win.setWindowAnimations(R.style.dialogWindowAnim);
        IconFontTextview star = (IconFontTextview) view.findViewById(R.id.icon_star);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        if("1".equals(isMarked)){
            star.setTextColor(getResources().getColor(R.color.yellow));
            tv.setText("取消星标");
        }else if("2".equals(isMarked)){
            star.setTextColor(getResources().getColor(R.color.gray));
            tv.setText("标为星标");
        }
        view.findViewById(R.id.patient_more_setStar).setOnClickListener(this);
        view.findViewById(R.id.patient_more_complain).setOnClickListener(this);
        view.findViewById(R.id.patient_more_delete).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.patient_more_setStar:
                if("1".equals(isFriend)){
                    if("1".equals(isMarked)){
                        // 是标新患者   取消标新
                        OkHttpUtils.post()
                                .url(UrlGlobal.PATIENT_STAR)
                                .addParams("did", did)
                                .addParams("token", token)
                                .addParams("uid", uid)
                                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        ToastUtils.showLongToast(PatientDataActivity.this, "网络异常");
                                    }
                                    @Override
                                    public void onResponse(String response, int id) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String msg = jsonObject.optString("msg");
                                            int code = jsonObject.optInt("code");
                                            System.out.println(code);
                                            if (code == 0) {
                                                ivStarTitle.setTextColor(getResources().getColor(R.color.gray));
                                                isMarked = "2";
                                                // 更新数据库
                                                HXUserData userData = new HXUserData();
                                                userData.setIsMarked(isMarked);
                                                userData.updateAll("uid = ?", uid);
                                                EventBus.getDefault().post("updata_patient");
                                            } else {
                                                ToastUtils.showLongToast(PatientDataActivity.this, msg);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        dialog.dismiss();
                    }else if("2".equals(isMarked)){
                        // 不是标星患者
                        OkHttpUtils.post()
                                .url(UrlGlobal.PATIENT_STAR)
                                .addParams("did", did)
                                .addParams("token", token)
                                .addParams("uid", uid)
                                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        ToastUtils.showLongToast(PatientDataActivity.this, "网络异常");
                                    }
                                    @Override
                                    public void onResponse(String response, int id) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String msg = jsonObject.optString("msg");
                                            int code = jsonObject.optInt("code");
                                            System.out.println(code);
                                            if (code == 0) {
                                                ivStarTitle.setTextColor(getResources().getColor(R.color.yellow));
                                                isMarked = "1";
                                                HXUserData userData = new HXUserData();
                                                userData.setIsMarked(isMarked);
                                                userData.updateAll("uid = ?", uid);
                                                EventBus.getDefault().post("updata_patient");
                                            } else {
                                                ToastUtils.showLongToast(PatientDataActivity.this, msg);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        dialog.dismiss();
                    }
                }else if("2".equals(isFriend)){
                    // 不是好友
                    ToastUtils.showLongToast(this,"该患者不是好友,请先添加好友");
                }
                break;
            case R.id.rl_patient_info:
                Intent iIntent = new Intent(this,PatientInfoDetailsActivity.class);
                iIntent.putExtra("uid",uid);
                startActivity(iIntent);
                break;
            case R.id.ll_label:
                 //病症标签
                Intent labelIntent = new Intent(PatientDataActivity.this, LabelListActivity.class);
                startActivity(labelIntent);
                break;
            //投诉
            case R.id.patient_more_complain:
                Intent complainIntent = new Intent(this, ComplainActivity.class);
                complainIntent.putExtra("uid",uid);
                startActivity(complainIntent);
                dialog.dismiss();
                break;
            //删除好友
            case R.id.patient_more_delete:
                if("1".equals(isFriend)){
                    OkHttpUtils.post()
                            .url(UrlGlobal.DELETE_PATIENT)
                            .addParams("did", did)
                            .addParams("token", token)
                            .addParams("uid", uid)
                            .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    ToastUtils.showLongToast(PatientDataActivity.this, "网络异常");
                                }
                                @Override
                                public void onResponse(String response, int id) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String msg = jsonObject.optString("msg");
                                        int code = jsonObject.optInt("code");
                                        if (code == 0) {
                                            btnAddOrSend.setText("添加好友");
                                        //    star.setTextColor(Color.parseColor("#f7ba2a"));
                                            ivStarTitle.setTextColor(getResources().getColor(R.color.gray));
                                            isFriend = "2";
                                            isMarked = "2";
                                            DataSupport.deleteAll(HXUserData.class, "uid = ? and doctordid=?", uid,did);
                                            ToastUtils.showLongToast(PatientDataActivity.this, msg);
                                            // 通知联系人列表界面刷新
                                            EventBus.getDefault().post("updata_patient");
                                            // 通知聊天列表页面删除
                                            Map<String,String> map = new HashMap<String, String>();
                                            map.put("delete_patient",hx_name);
                                            EventBus.getDefault().post(map);
                                        } else {
                                            ToastUtils.showLongToast(PatientDataActivity.this, msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    dialog.dismiss();
                }else if("2".equals(isFriend)){
                    // 不是好友
                    ToastUtils.showLongToast(this,"该患者不是好友,请先添加好友");
                }
                break;
            case R.id.patientdata_dos_ll:
                Intent intent = new Intent(this,PatientDetailActivity.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
                break;
            case R.id.ll_patientdata_case_test:
                //病例检查
                Intent intent1 = new Intent(PatientDataActivity.this,PatientCaseTestActivity.class) ;
                intent1.putExtra("uid",uid);
                startActivity(intent1);
                break;
            case R.id.btn_addorsend:
                // 添加好友
                if(btnAddOrSend.getText().equals("添加好友")){
                    // 添加好友
                    addHttp();
                }else if(btnAddOrSend.getText().equals("发送消息")){
                    // 发送消息
                    if(!TextUtils.isEmpty(hx_name)){
                        Intent chatIntent = new Intent(PatientDataActivity.this,ChatActivity.class);
                        chatIntent.putExtra("hx",hx_name);
                        chatIntent.putExtra("uid",uid);
                        startActivity(chatIntent);
                    }
                 //   finish();
                }
                break;
            case R.id.btn_cancel:
                if(dialog!=null) {
                    dialog.dismiss();
                }
                break;
        }
    }
    public void addHttp() {
        OkHttpUtils.post()
                .url(UrlGlobal.ADDUSERMESSAGE)
                .addParams("uid", uid)
                .addParams("did", did)
                .addParams("token", token)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(PatientDataActivity.this, "请求网络失败");
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Result result  = GsonUtil.getInstance().fromJson(response,Result.class);
                        if(result.code == 0){
                            btnAddOrSend.setText("发送消息");
                            isFriend = "1";
                            List<HXUserData> userDatas = DataSupport.where("uid = ?", uid).find(HXUserData.class);
                            if(userDatas!=null  && userDatas.size() >0){
                                HXUserData userData = new HXUserData();
                                userData.setIsFriend(isFriend);
                                userData.updateAll("uid = ?", uid);
                            }else{
                                HXUserData userData = new HXUserData();
                                userData.setIsFriend(isFriend);
                                userData.setUid(uid);
                                userData.setDoctordid(did);
                                userData.save();
                            }
                            // 添加好友 通知联系人列表界面刷新
                            EventBus.getDefault().post("updata_patient");
                        }else{
                        }
                        ToastUtils.showLongToast(PatientDataActivity.this,result.msg);
                    }
             });
    }
}