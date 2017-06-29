package com.qingmiao.qmdoctor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.ContactAdapter;
import com.qingmiao.qmdoctor.bean.ContactModel;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.bean.TagDataInfoBean;
import com.qingmiao.qmdoctor.bean.UserFriendBean;
import com.qingmiao.qmdoctor.fragment.ContactRecyclerFragment;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.qingmiao.qmdoctor.widget.CharacterParser;
import com.qingmiao.qmdoctor.widget.PinyinComparator;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;


public class AddLabelActivity extends BaseActivity implements ILibelInfoView {
    EditText etSetLabel;
    TextView tvContentSize;
    private ContactRecyclerFragment contactRecyclerFragment;
    private static final int START_ADDMEMBER_ACTIVITY_REQUEST = 200;
    private LibelInfoPresenter libelInfoPresenter;
    private String id,name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_label);
        initView();
        tvCenter.setText("标签");
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_set_label:
                break;
            case R.id.ll_add_user:
                Intent intent = new Intent(AddLabelActivity.this,AddMemberActivity.class);
                intent.putExtra("bundle", (Serializable) contactRecyclerFragment.getDataList());
                startActivityForResult(intent,START_ADDMEMBER_ACTIVITY_REQUEST);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == START_ADDMEMBER_ACTIVITY_REQUEST && resultCode== Activity.RESULT_OK){
            // 启动选择患者界面成功
            List<ContactModel> models = (List<ContactModel>) data.getSerializableExtra("bundle");
            tvContentSize.setText("标签成员("+models.size()+")");
            contactRecyclerFragment.upDataList(models);
        }
    }

    public void initView(){
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        tvRight.setText("保存");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存用户的标签信息
                String name = etSetLabel.getText().toString();
                List<ContactModel> dataList = contactRecyclerFragment.getDataList();
                if(TextUtils.isEmpty(name) ){
                    ToastUtils.showLongToast(AddLabelActivity.this,"请填写标签名称.");
                    return ;
                }
                if(dataList ==null ){
                    dataList = new ArrayList<ContactModel>();
                }
                StringBuffer sb = new StringBuffer();
                for (ContactModel contactModel:dataList) {
                    sb.append(contactModel.friend.uid+",");
                }
                if(sb.length()>0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put("did",did);
                linkedHashMap.put("token",token);
                linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                linkedHashMap.put("tag_name",name);
                linkedHashMap.put("uids",sb.toString());
                if(!TextUtils.isEmpty(id)&& !TextUtils.isEmpty(name)){
                    linkedHashMap.put("id",id);
                    libelInfoPresenter.startLoad(UrlGlobal.UPDATA_EDIT_TAGS,linkedHashMap);
                }else{
                    libelInfoPresenter.startLoad(UrlGlobal.ADD_LABEL,linkedHashMap);
                }
                EventBus.getDefault().post("updata_patient");
            }
        });
        libelInfoPresenter = new LibelInfoPresenter(this);
        contactRecyclerFragment = new ContactRecyclerFragment();
        View view = View.inflate(this,R.layout.view_add_label_header,null);
        contactRecyclerFragment.addHeaderView(view);
        contactRecyclerFragment.setOpenSwipeButton(true);
        contactRecyclerFragment.setShowSideBar(false);
        etSetLabel = (EditText) view.findViewById(R.id.et_set_label);
        LinearLayout ll_add_user = (LinearLayout) view.findViewById(R.id.ll_add_user);
        tvContentSize = (TextView) view.findViewById(R.id.tv_conntent_size);
        ll_add_user.setOnClickListener(this);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //显示点中的Fragment对象
        transaction.add(R.id.frameLayout, contactRecyclerFragment);
        transaction.show(contactRecyclerFragment);
        transaction.commit();
        if(!TextUtils.isEmpty(id) && !TextUtils.isEmpty(name)) {
            //获取数据
            etSetLabel.setText(name);
            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put("id",id);
            linkedHashMap.put("did", did);
            linkedHashMap.put("token", token);
            linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
            startLoad(UrlGlobal.GET_SHOWTAGS,linkedHashMap);
        }
    }

    @Override
    public void initData(String data) {
        super.initData(data);
        TagDataInfoBean tagDataInfoBean = GsonUtil.getInstance().fromJson(data,TagDataInfoBean.class);
        List<ContactModel> models = filledData(tagDataInfoBean.data.user_info);
        contactRecyclerFragment.upDataList(models);
        tvContentSize.setText("标签成员("+models.size()+")");
    }


    private List<ContactModel> filledData(List<UserFriendBean> user_info){
        CharacterParser characterParser = new CharacterParser();
        List<ContactModel> mSortList = new ArrayList<ContactModel>();
        if(user_info!=null) {
            for (int i = 0; i < user_info.size(); i++) {
                ContactModel contactModel = new ContactModel();
                contactModel.friend = user_info.get(i);
                //汉字转换成拼音
                String pinyin;
                if (!TextUtils.isEmpty(contactModel.friend.getShowName())) {
                    pinyin = characterParser.getSelling(contactModel.friend.getShowName());
                } else {
                    pinyin = characterParser.getSelling(contactModel.friend.hx_uname);
                }
                String sortString;
                if (pinyin.contains("biaoxinghuanzhe")) {
                    sortString = KeyOrValueGlobal.KEY_BIAOXINHUANZHE;
                } else {
                    sortString = pinyin.substring(0, 1).toUpperCase();
                }
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    contactModel.sortLetters = sortString.toUpperCase();
                } else if (sortString.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE)) {
                    contactModel.sortLetters = sortString;
                } else {
                    contactModel.sortLetters = "#";
                }
                contactModel.type = ContactAdapter.ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal();
                mSortList.add(contactModel);
            }
        }
        // 根据a-z进行排序
        Collections.sort(mSortList, new PinyinComparator());
        return mSortList;
    }




    @Override
    public void showLibelProgress(String uri) {
        showLoadingDialog(uri,"保存标签中");
    }

    @Override
    public void hideLibelProgress(String uri) {
        dismissLoadDialog();
    }

    @Override
    public void getLibelData(String uri, String data) {
        LogUtil.LogShitou(uri);
        Result result = GsonUtil.getInstance().fromJson(data,Result.class);
        if(result.code == 0){
            EventBus.getDefault().post("updataLabeList");
            finish();
        }else{
            ToastUtils.showLongToast(this,result.msg);
        }
    }
}
