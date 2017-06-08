package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.TalkDocuterAdapter;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.CircleCateBean;
import com.qingmiao.qmdoctor.bean.DocCircleCateBean;
import com.qingmiao.qmdoctor.bean.TalkDocuterBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 医生交流
 */
public class TalkDocuterActivity extends BaseActivity {

    @BindView(R.id.recycleview)
    LRecyclerView lRecyclerView;
    private ListBaseAdapter<DocCircleCateBean.DocCircleData> talkDocuterAdapter;
    private int REQUEST_COUNT = 10;
    private String c_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recycleview);
        getIntentData();
        initRecycleView();
        getServiceData();
    }

    private void getServiceData(){
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("c_id",c_id);
        startLoad(UrlGlobal.GETDOCCIRCLE_CATE,linkedHashMap);

    }

    private void getIntentData() {
        tvCenter.setText("医生交流");
        c_id = this.getIntent().getStringExtra("c_id");
    }

    private void requestData(final int type){
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("c_id",c_id);
        OkHttpUtils.post()
                .url(UrlGlobal.GETDOCCIRCLE_CATE)
                .params(linkedHashMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(TalkDocuterActivity.this,"请求失败");
                        // 停止刷新
                        lRecyclerView.refreshComplete(REQUEST_COUNT);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        refreshRecycle(type,response);
                    }
                });
    }


    private void initRecycleView() {
        View view = findViewById(R.id.empty_view);
        lRecyclerView.setEmptyView(view);
        IconFontTextview fontTextview = (IconFontTextview) findViewById(R.id.image);
        fontTextview.setText(R.string.icons_star);
        TextView tv = (TextView) findViewById(R.id.text);
        tv.setText("暂无数据");
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setColorResource(R.color.text)
                .build();
   //     lRecyclerView.addItemDecoration(divider);
        talkDocuterAdapter = new ListBaseAdapter<DocCircleCateBean.DocCircleData>(this) {
            @Override
            public int getLayoutId() {
                return R.layout.circle_list_item;
            }

            @Override
            public void onBindItemHolder(SuperViewHolder holder, int position) {
                TextView tv = holder.getView(R.id.circle_list_item_name);
                tv.setText(getDataList().get(position).q_name);
            }
        };
        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(talkDocuterAdapter);
        lRecyclerView.setAdapter(mLRecyclerViewAdapter);
        lRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        lRecyclerView.setPullRefreshEnabled(true);
        lRecyclerView.setLoadMoreEnabled(false);

        mLRecyclerViewAdapter.removeFooterView();
        lRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(0);
            }
        });
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(TalkDocuterActivity.this, RenalCirclePagerActivity.class);
                intent.putExtra("q_id", talkDocuterAdapter.getDataList().get(position).q_id);
                startActivity(intent);
            }
        });
        lRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
    }


    @Override
    public void initData(String data) {
        super.initData(data);
        refreshRecycle(0,data);
    }

    /**
     * @param type  0 代表刷新   1 代表加载更多
     * @param data
     */
    private void refreshRecycle(int type,String data){
        DocCircleCateBean circleCateBean = GsonUtil.getInstance().fromJson(data, DocCircleCateBean.class);
        if(circleCateBean.code == 0){
            if(circleCateBean.data!=null ){
                if(type == 0){
                    talkDocuterAdapter.setDataList(circleCateBean.data);
                }
            }else{
                ToastUtils.showLongToast(this,"没有数据");
            }
        }else{
            ToastUtils.showLongToast(this,circleCateBean.msg);
        }
        // 停止刷新
        lRecyclerView.refreshComplete(REQUEST_COUNT);
    }
}
