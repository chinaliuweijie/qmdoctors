package com.qingmiao.qmdoctor.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.Contact;
import com.qingmiao.qmdoctor.adapter.ContactAdapter;
import com.qingmiao.qmdoctor.bean.ContactModel;
import com.qingmiao.qmdoctor.fragment.BaseFragment;
import com.qingmiao.qmdoctor.fragment.ContactRecyclerFragment;
import com.qingmiao.qmdoctor.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ContactRecyclerView extends RelativeLayout {

    public String did;
    public String token;
    @BindView(R.id.contact_list)
    LRecyclerView contactList;
    @BindView(R.id.sidrbar)
    SideBar sideBar;
    @BindView(R.id.dialog)
    TextView dialog;
    LRecyclerViewAdapter mLRecyclerViewAdapter;
    private ContactAdapter adapter;
    private View headerView, footerView;
    private OnRefreshListener listener;
    private boolean isShowSideBar;
    private boolean isShowCheckBox;
    private boolean isOpenSwipeButton;
    private ContactRecyclerFragment.OnContactItemClickListener onItemClickListener;
    private int REQUEST_COUNT = 10;
    private LinearLayoutManager layoutManager;
    private Context mContext;


    public ContactRecyclerView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ContactRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public ContactRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }




    public void initView() {
        View view = View.inflate(getContext(), R.layout.fragment_contact, null);
        this.addView(view);
        ButterKnife.bind(this, view);
        did = PrefUtils.getString(mContext, "did", "");
        token = PrefUtils.getString(mContext, "token", "");
        sideBar.setTextView(dialog);
        WindowManager wm = ((Activity)mContext).getWindowManager();
        Display d = wm.getDefaultDisplay();
        //拿到布局参数
        ViewGroup.LayoutParams l = sideBar.getLayoutParams();
        l.height=d.getHeight()/2;
        // 设置右侧[A-Z]快速导航栏触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                layoutManager.scrollToPositionWithOffset(adapter.getScrollPosition(s), 0);
            }
        });
        if (isShowSideBar) {
            sideBar.setVisibility(View.VISIBLE);
        } else {
            sideBar.setVisibility(View.GONE);
        }
        initDatas();
        initRecycleView();
    }

    private void initRecycleView() {
        List<ContactModel> contactModels = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this.getContext());
        contactList.setLayoutManager(layoutManager);
        contactList.setItemAnimator(new DefaultItemAnimator());
        PatientDividerDecoration divider = new PatientDividerDecoration.Builder(this.getContext())
                .setColorResource(R.color.text)
                .build();
        contactList.addItemDecoration(divider);

        adapter = new ContactAdapter(this.getContext(), contactModels);
        adapter.setShowChecker(isShowCheckBox);
        adapter.setOpenSwipeButton(isOpenSwipeButton);
        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        contactList.setAdapter(mLRecyclerViewAdapter);
        contactList.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        contactList.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        if (headerView != null) {
            mLRecyclerViewAdapter.addHeaderView(headerView);
        } else {
          //  mLRecyclerViewAdapter.removeHeaderView();
        }
        if (footerView != null) {
            mLRecyclerViewAdapter.addFooterView(footerView);
        } else {
            mLRecyclerViewAdapter.removeFooterView();
        }
        if(listener != null){
            contactList.setPullRefreshEnabled(true);
            contactList.setOnRefreshListener(listener);
        }else{
            contactList.setPullRefreshEnabled(false);
        }
        if(onItemClickListener!=null){
            adapter.setOnItemClickListener(onItemClickListener);
        }
    }


//    public void setContactPullRefresh(OnRefreshListener listener){
//        if(contactList!=null) {
//            contactList.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
//            contactList.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
//            contactList.setPullRefreshEnabled(true);
//            contactList.setOnRefreshListener(listener);
//        }
//    }

//    public boolean isCanRefresh(){
//        if(contactList!=null) {
//           return contactList.isOnTop();
//        }
//        return false;
//    }


    public void setRefreshComplete(){
        // 停止刷新
        contactList.refreshComplete(REQUEST_COUNT);
    }



    public void setOnItemClickListener(ContactRecyclerFragment.OnContactItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    public void addHeaderView(View view) {
        this.headerView = view;
    }

    public void setRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
    }

    public void addFooterView(View view) {
        this.footerView = view;
    }

    public void setOpenSwipeButton(boolean openSwipeButton) {
        isOpenSwipeButton = openSwipeButton;
    }


    public void setShowSideBar(boolean isShow) {
        this.isShowSideBar = isShow;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
    }



    private void initDatas() {
    }

    public void upDataList(List<ContactModel> contactModels) {
        adapter.updateListView(contactModels);
    }

    public void setAllData(List<ContactModel> contactModels){
        adapter.setmAllDatas(contactModels);
    }

    public ContactAdapter getAdapter() {
        return adapter;
    }

    public List<ContactModel> getDataList(){
        return adapter.getDataList();
    }


    public List<ContactModel> getCheckData() {
        return adapter.getCheckModel();
    }


    public List<ContactModel> filterData(String s) {
        List<ContactModel> tempExaple = new ArrayList<>();
        if(TextUtils.isEmpty(s)){
            upDataList(adapter.getmAllDatas());
            return adapter.getmAllDatas();
        }else{
            for (int i = 0; i < adapter.getDataList().size(); i++) {
                ContactModel contactModel = adapter.getDataList().get(i);
                if (contactModel.type == ContactAdapter.ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal() && contactModel.friend != null) {
                    if (contactModel.friend.nickname.contains(s)) {
                        tempExaple.add(contactModel);
                    }
                }
            }
            upDataList(tempExaple);
            return tempExaple;
        }
    }




}
