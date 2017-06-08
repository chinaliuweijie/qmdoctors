package com.qingmiao.qmdoctor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.ContactAdapter;
import com.qingmiao.qmdoctor.bean.PatientFriendListBean;
import com.qingmiao.qmdoctor.bean.UserFriendBean;
import com.qingmiao.qmdoctor.fragment.ContactRecyclerFragment;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.CharacterParser;
import com.qingmiao.qmdoctor.widget.ClearEditText;
import com.qingmiao.qmdoctor.widget.PinyinComparator;
import com.qingmiao.qmdoctor.bean.ContactModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

public class AddMemberActivity extends BaseActivity {

    private ContactRecyclerFragment contactRecyclerFragment;
    @BindView(R.id.noResult)
    TextView noResult;
    @BindView(R.id.filter_edit)
    EditText filterEedit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        tvRight.setVisibility(View.VISIBLE);
        did = PrefUtils.getString(this, "did", "");
        token = PrefUtils.getString(this, "token", "");
        tvCenter.setText("患者列表");
        initViews();
        initHttp();
    }




    public void initHttp() {
        List<ContactModel> models = (List<ContactModel>) getIntent().getSerializableExtra("bundle");
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("did", did);
        linkedHashMap.put("token", token);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        startLoad(UrlGlobal.GETUSERFRIEND, linkedHashMap);
    }

    @Override
    public void initData(String data) {
        super.initData(data);
        PatientFriendListBean patientFriendListBean = GsonUtil.getInstance().fromJson(data, PatientFriendListBean.class);
        if (patientFriendListBean.code == 0) {
            List<ContactModel> contactModels = filledData(patientFriendListBean.data);
          //  Collections.sort(contactModels, new PinyinComparator());
            List<ContactModel> models = (List<ContactModel>) getIntent().getSerializableExtra("bundle");
            if(models!=null && models.size()!=0){
                // 遍历
                for (ContactModel contactModel:contactModels) {
                    for (ContactModel model:models) {
                        if(contactModel.type == ContactAdapter.ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal() && contactModel.friend.uid.equals(model.friend.uid)){
                            contactModel.isCheck = true;
                        }
                    }
                }
            }
            // 根据a-z进行排序
            Collections.sort(contactModels, new PinyinComparator());
            contactRecyclerFragment.upDataList(contactModels);
            contactRecyclerFragment.setAllData(contactModels);
        } else {
            ToastUtils.showLongToast(this, patientFriendListBean.msg);
        }
    }


    private List<ContactModel> filledData(PatientFriendListBean.FriendData date){
        CharacterParser characterParser = new CharacterParser();
        List<ContactModel> contactModels = new ArrayList<>();
//        if(date.marked_u!=null){
//            for (UserFriendBean friend:date.marked_u){
//                // 添加标新好友
//                ContactModel contactModel = new ContactModel();
//                contactModel.friend = friend;
//                contactModel.type = ContactAdapter.ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal();
//                contactModel.sortLetters = KeyOrValueGlobal.KEY_BIAOXINHUANZHE;
//                contactModels.add(contactModel);
//            }
//        }
        if(date.nomal_u!=null){
            for (UserFriendBean friend:date.nomal_u) {
                ContactModel contactModel = new ContactModel();
                contactModel.friend = friend;
                contactModel.type = ContactAdapter.ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal();
                if(TextUtils.isEmpty(contactModel.friend.getShowName())){
                    contactModel.sortLetters = "";
                }else{
                    contactModel.sortLetters = characterParser.getSelling(contactModel.friend.getShowName()).substring(0, 1).toUpperCase();
                }
//                boolean iGoto = false;
//                if(date.marked_u!=null) {
//                    for (UserFriendBean markFriend : date.marked_u) {
//                        if (friend.uid.equals(markFriend.uid)) {
//                            // 是标星好友
//                            // contactModel.sortLetters = KeyOrValueGlobal.KEY_BIAOXINHUANZHE;
//                            iGoto = true;
//                            break;
//                        }
//                    }
//                }
//                if(iGoto){
//                    continue;
//                }
                // 正则表达式，判断首字母是否是英文字母
                if(contactModel.sortLetters.matches("[A-Z]")){
                    contactModel.sortLetters = contactModel.sortLetters.toUpperCase();
                }else if(contactModel.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE)){
                }else {
                    contactModel.sortLetters = "#";
                }
                contactModels.add(contactModel);
            }
        }
        return contactModels;
    }

    private void initViews() {
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ContactModel> checkData = contactRecyclerFragment.getCheckData();
                Intent intent = new Intent();
                intent.putExtra("bundle", (Serializable) checkData);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
        contactRecyclerFragment = new ContactRecyclerFragment();
        contactRecyclerFragment.setShowSideBar(true);
        contactRecyclerFragment.setShowCheckBox(true);
        contactRecyclerFragment.setOpenSwipeButton(false);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //显示点中的Fragment对象
        transaction.add(R.id.frameLayout, contactRecyclerFragment);
        transaction.show(contactRecyclerFragment);
        transaction.commit();
        filterEedit.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    /**
     * 获取每个字符或者直接用 toCharArray 方法
     * 通过检索输入的字符，是不是顺序匹配pinyin项实现筛选
     * @param s 搜索条件
     */
    private void filterData(String s) {
        List<ContactModel> models = contactRecyclerFragment.filterData(s);
        //放置没有搜索结果的图片
        if(models.size() == 0){
            noResult.setVisibility(View.VISIBLE);
        }else {
            noResult.setVisibility(View.GONE);
        }
    }
}
