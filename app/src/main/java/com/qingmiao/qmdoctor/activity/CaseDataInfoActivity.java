package com.qingmiao.qmdoctor.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.PatientCaseDetailedAdapter;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.UserSickCehckItemBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.TimeUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class CaseDataInfoActivity extends BaseActivity implements ILibelInfoView{

    @BindView(R.id.recycleview)
    LRecyclerView datadetailVp;
    @BindView(R.id.datadetail_bottom_name)
    TextView datadetailBottomName;
    @BindView(R.id.datadetail_time)
    TextView datadetailTime;
    @BindView(R.id.datadetail_tvLeft)
    IconFontTextview datadetailTvLeft;
    @BindView(R.id.datadetail_tvRight)
    IconFontTextview datadetailTvRight;
    private String bl_id,uid;
    private String startPosition,endPosition;
    private LibelInfoPresenter infoPresenter;
    private ListBaseAdapter<UserSickCehckItemBean.UserSickData> userSickDataListBaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_detail);
        getShare();//获取共享数据
        initView();
        initHttp(null);
    }

    private void getShare() {
        Intent intent = getIntent();
        bl_id = intent.getStringExtra("bl_id");
        uid = intent.getStringExtra("uid");
        startPosition = intent.getStringExtra("startPosition");
        endPosition = intent.getStringExtra("endPosition");
    }

    private void initHttp(String next) {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("did", did);
        linkedHashMap.put("token", token);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("bl_id", bl_id);
        linkedHashMap.put("type", "2");
        linkedHashMap.put("uid", uid);
        if(TextUtils.isEmpty(next)){
            startLoad(UrlGlobal.GET_USERSICK_CHECKITEMDATA,linkedHashMap);
        }else{
            linkedHashMap.put("next", next);
            infoPresenter.startLoad(UrlGlobal.GET_USERSICK_CHECKITEMDATA,linkedHashMap);
        }
    }


    @Override
    public void initData(String data) {
        super.initData(data);
        UserSickCehckItemBean userSickCehckItemBean = GsonUtil.getInstance().fromJson(data,UserSickCehckItemBean.class);
        if(userSickCehckItemBean.code == 0){
            userSickDataListBaseAdapter.setDataList(userSickCehckItemBean.data);
            datadetailBottomName.setText(userSickCehckItemBean.sick_title);
            datadetailTime.setText(TimeUtils.getStrTime(userSickCehckItemBean.time));
            bl_id = userSickCehckItemBean.bl_id;
            datadetailTvLeft.setVisibility(View.VISIBLE);
            datadetailTvRight.setVisibility(View.VISIBLE);
            if(startPosition.equals(bl_id)){
                datadetailTvLeft.setVisibility(View.INVISIBLE);
            }else if(endPosition.equals(bl_id)){
                datadetailTvRight.setVisibility(View.INVISIBLE);
            }
        }else{
            ToastUtils.showLongToast(this,userSickCehckItemBean.msg);
            userSickDataListBaseAdapter.setDataList(new ArrayList<UserSickCehckItemBean.UserSickData>());
            datadetailBottomName.setText("");
            datadetailTime.setText(TimeUtils.getStrTime(""));
        }
    }

    private void initView() {
        ivLeft.setVisibility(View.VISIBLE);
        tvCenter.setText("数据详情");
        initRecycleView();
        infoPresenter = new LibelInfoPresenter(this);
    }

    private void initRecycleView() {
        datadetailVp.setLayoutManager(new LinearLayoutManager(this));
        datadetailVp.setItemAnimator(new DefaultItemAnimator());
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setColorResource(R.color.text)
                .build();
        datadetailVp.addItemDecoration(divider);
        userSickDataListBaseAdapter = new ListBaseAdapter<UserSickCehckItemBean.UserSickData>(this) {
            @Override
            public int getLayoutId() {
                return R.layout.view_patient_sick_details_item;
            }

            @Override
            public void onBindItemHolder(SuperViewHolder holder, int position) {
                TextView title = holder.getView(R.id.tv_title);
                TextView content = holder.getView(R.id.tv_content);
                title.setText(getDataList().get(position).field_cn);
                content.setText(getDataList().get(position).data);
                TextView fanwei = holder.getView(R.id.tv_fanwei);
                if(getDataList().get(position).demo!=null && getDataList().get(position).demo.contains("阴性") ){
                    fanwei.setText("阴性");
                }else{
                    fanwei.setText(getDataList().get(position).demo + getDataList().get(position).unit);
                }


            }
        };
        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(userSickDataListBaseAdapter);
        datadetailVp.setAdapter(mLRecyclerViewAdapter);
        datadetailVp.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        datadetailVp.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        datadetailVp.setPullRefreshEnabled(false);
        mLRecyclerViewAdapter.removeFooterView();
    }

    @OnClick({R.id.datadetail_tvLeft, R.id.datadetail_tvRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.datadetail_tvLeft:
                if(!startPosition.equals(bl_id)){
                    initHttp("2");
                }
                break;
            case R.id.datadetail_tvRight:
                if(!endPosition.equals(bl_id)){
                    initHttp("1");
                }
                break;
        }
    }

    @Override
    public void showLibelProgress(String uri) {
        showLoadingDialog(uri,"加载数据中");
    }

    @Override
    public void hideLibelProgress(String uri) {
        dismissLoadDialog();
    }

    @Override
    public void getLibelData(String uri, String data) {
        UserSickCehckItemBean userSickCehckItemBean = GsonUtil.getInstance().fromJson(data,UserSickCehckItemBean.class);
        if(userSickCehckItemBean.code == 0){
            userSickDataListBaseAdapter.setDataList(userSickCehckItemBean.data);
            datadetailBottomName.setText(userSickCehckItemBean.sick_title);
            datadetailTime.setText(TimeUtils.getStrTime(userSickCehckItemBean.time));
            bl_id = userSickCehckItemBean.bl_id;
            datadetailTvLeft.setVisibility(View.VISIBLE);
            datadetailTvRight.setVisibility(View.VISIBLE);
            if(startPosition.equals(bl_id)){
                datadetailTvLeft.setVisibility(View.INVISIBLE);
            }else if(endPosition.equals(bl_id)){
                datadetailTvRight.setVisibility(View.INVISIBLE);
            }
        }else{
            ToastUtils.showLongToast(this,userSickCehckItemBean.msg);
            userSickDataListBaseAdapter.setDataList(new ArrayList<UserSickCehckItemBean.UserSickData>());
            datadetailBottomName.setText("");
            datadetailTime.setText(TimeUtils.getStrTime(""));
        }
    }
}
