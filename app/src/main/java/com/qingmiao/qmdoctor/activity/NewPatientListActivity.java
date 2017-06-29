package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.ContactAdapter;
import com.qingmiao.qmdoctor.adapter.LabelInfoAdapter;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.NewFriendBean;
import com.qingmiao.qmdoctor.bean.TagsListBean;
import com.qingmiao.qmdoctor.bean.UserFriendBean;
import com.qingmiao.qmdoctor.bean.UserSickCehckItemBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;

import java.util.LinkedHashMap;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/6/23.
 */

public class NewPatientListActivity extends BaseActivity {

    @BindView(R.id.recycleview)
    LRecyclerView lRecyclerView;
    private ListBaseAdapter<UserFriendBean> newFriendAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recycleview);
        initView();
        initData();
    }

    private void initData() {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>() ;
        linkedHashMap.put("did",did);
        linkedHashMap.put("token",token);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        startLoad(UrlGlobal.GET_NEW_FRIENDS,linkedHashMap);
    }


    @Override
    public void initData(String data) {
        super.initData(data);
        NewFriendBean newFriendBean = GsonUtil.getInstance().fromJson(data,NewFriendBean.class);
        if(newFriendBean.code == 0){
            newFriendAdapter.setDataList(newFriendBean.data.nomal_u);
        }else{
            ToastUtils.showLongToast(this,newFriendBean.msg);
        }
    }

    private void initView() {
        tvCenter.setText("新增患者");
        tvRight.setText("添加");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPatientListActivity.this,AddPatientActivity.class);
                startActivity(intent);
            }
        });
        ivRight.setText(R.string.icons_add);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewPatientListActivity.this,AddPatientActivity.class));
            }
        });
        initRecycleView();
    }

    private void initRecycleView() {
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setColorResource(R.color.text)
                .build();
        lRecyclerView.addItemDecoration(divider);

        newFriendAdapter = new ListBaseAdapter<UserFriendBean>(this) {
            @Override
            public int getLayoutId() {
                return R.layout.item_new_friend;
            }

            @Override
            public void onBindItemHolder(SuperViewHolder holder, int position) {
                ImageView iv = holder.getView(R.id.iv_photo);
                TextView tvName = holder.getView(R.id.title);
                GlideUtils.LoadAvatarImageView(mContext,getDataList().get(position).avatar,iv);
                tvName.setText(getDataList().get(position).getShowName());
            }
        };
        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(newFriendAdapter);
        lRecyclerView.setAdapter(mLRecyclerViewAdapter);
        lRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        lRecyclerView.setPullRefreshEnabled(false);
        mLRecyclerViewAdapter.removeFooterView();
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(NewPatientListActivity.this, PatientDataActivity.class);
                intent.putExtra("uid", newFriendAdapter.getDataList().get(position).uid);
                startActivity(intent);
            }
        });
    }
}
