package com.qingmiao.qmdoctor.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.hyphenate.easeui.utils.GlideUtils;
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
import com.qingmiao.qmdoctor.widget.CustomFramlayout;
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
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.id_gallery)
    LinearLayout gallery;
    @BindView(R.id.customFram)
    CustomFramlayout customFramlayout;
    @BindView(R.id.scollview)
    HorizontalScrollView HorizontalScrollView;
    /**
     * 输入法管理器
     */
    private InputMethodManager mInputMethodManager;


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
                // 正则表达式，判断首字母是否是英文字母
                if(contactModel.sortLetters.matches("[A-Z]")){
                    contactModel.sortLetters = contactModel.sortLetters.toUpperCase();
                }else if(contactModel.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE)){
                }else {
                    contactModel.sortLetters = "#";
                }
                contactModel.pinyinName = characterParser.getSelling(contactModel.friend.getShowName());
                contactModels.add(contactModel);
            }
        }
        return contactModels;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
        filterEedit.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                   // 此处为得到焦点时的处理内容
                    llSearch.setVisibility(View.VISIBLE);
                } else {
                   // 此处为失去焦点时的处理内容
                    llSearch.setVisibility(View.GONE);
                }
            }

        });
        filterEedit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(!filterEedit.getText().toString().equals("")){
                        return false;
                    }
                    int count = gallery.getChildCount();
                    if(count == 0){
                        return false;
                    }
                    int position = count-1;
                    ImageView iv = (ImageView) gallery.getChildAt(position);
                    boolean isTransparent = (boolean) iv.getTag(R.string.istrans);
                    if(!isTransparent){
                        iv.setAlpha(80);
                        iv.setTag(R.string.istrans,true);
                    }else{
                        // 删除view
                        int item = (int) iv.getTag(R.string.position);
                        ContactModel contactModel = contactRecyclerFragment.getAdapter().getContactNames().get(item);
                        contactModel.isCheck = false;
                        contactRecyclerFragment.getAdapter().notifyItemChanged(item);
                        gallery.removeViewAt(position);
                    }
                    return true;
                }
                return false;
            }
        });

        llSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                llSearch.setVisibility(View.GONE);
                // 编辑框失去焦点
                filterEedit.getText().clear();
                filterEedit.setFocusable(false);
                filterEedit.setFocusableInTouchMode(false);
                filterEedit.clearFocus();
                if (mInputMethodManager.isActive()) {
                    mInputMethodManager.hideSoftInputFromWindow(filterEedit.getWindowToken(), 0);// 隐藏输入法
                }
            }
        });
        filterEedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterEedit.setFocusable(true);//设置输入框可聚集
                filterEedit.setFocusableInTouchMode(true);//设置触摸聚焦
                filterEedit.requestFocus();//请求焦点
                filterEedit.findFocus();//获取焦点
                mInputMethodManager.showSoftInput(filterEedit, InputMethodManager.SHOW_FORCED);// 显示输入法
            }
        });
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        contactRecyclerFragment.setOnItemClickListener(new ContactRecyclerFragment.OnContactItemClickListener() {
            @Override
            public void onItemClick(View view, final int position, final ContactModel model) {
                // 编辑框失去焦点
                if(model == null || model.friend == null){
                    return;
                }
                filterEedit.getText().clear();
                filterEedit.setFocusable(false);
                filterEedit.setFocusableInTouchMode(false);
                filterEedit.clearFocus();
                if (mInputMethodManager.isActive()) {
                    mInputMethodManager.hideSoftInputFromWindow(filterEedit.getWindowToken(), 0);// 隐藏输入法
                }
                if(model.isCheck){
                    // 选中添加到view
                    // 添加到布局
                    ImageView iv = new ImageView(AddMemberActivity.this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(filterEedit.getHeight(),filterEedit.getHeight());
                    iv.setLayoutParams(layoutParams);
                    iv.setPadding(5,5,5,5);
                    iv.setTag(R.string.app_name,model.friend.uid);
                    iv.setTag(R.string.position,position);
                    iv.setTag(R.string.istrans,false);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 删除选中
                            model.isCheck = false;
                            contactRecyclerFragment.getAdapter().notifyItemChanged(position);
                            deleteImage(model);
                        }
                    });
                    gallery.addView(iv);
                    GlideUtils.LoadImage(AddMemberActivity.this,model.friend.avatar,iv);
                    HorizontalScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            HorizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT);//滚动到右部
                        }
                    });
                    filterEedit.setCompoundDrawables(null,null,null,null);
                }else{
                    // 删除 view
                    deleteImage(model);
                }
           }
        });
        // 设置顶部滑动布局的最大宽度
        WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int min = filterEedit.getMinWidth();
        customFramlayout.setLayoutParamsMaxWidth(width-(min==0?250:min));
    }


    private void deleteImage(ContactModel model){
        int count = gallery.getChildCount();
        if(count == 0){
            return;
        }
        int temp = -1;
        for (int i = 0; i < count; i++) {
            View iv = gallery.getChildAt(i);
            if(model.friend.uid.equals(iv.getTag(R.string.app_name))){
                // 删除view
                temp = i;
                break;
            }
        }
        if(temp!=-1){
            gallery.removeViewAt(temp);
        }
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
            llSearch.setVisibility(View.VISIBLE);
        }else {
            noResult.setVisibility(View.GONE);
            if(s.equals("")){
                llSearch.setVisibility(View.VISIBLE);
            }else{
                llSearch.setVisibility(View.GONE);
            }
        }
        if(s.equals("")){
//            filterEedit.getText().clear();
//            filterEedit.setFocusable(false);
//            filterEedit.setFocusableInTouchMode(false);
//            filterEedit.clearFocus();
//            if (mInputMethodManager.isActive()) {
//                mInputMethodManager.hideSoftInputFromWindow(filterEedit.getWindowToken(), 0);// 隐藏输入法
//            }
        }
    }
}
