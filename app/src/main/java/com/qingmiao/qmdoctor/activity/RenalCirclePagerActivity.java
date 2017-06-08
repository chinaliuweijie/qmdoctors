package com.qingmiao.qmdoctor.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.RenalCircleAdapter;
import com.qingmiao.qmdoctor.bean.CircleCatePagerBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.LinkedHashMap;
import butterknife.BindView;
import okhttp3.Call;

/**
 * 我的圈子条目页面
 */

public class RenalCirclePagerActivity extends BaseActivity {

    private String q_id;
    @BindView(R.id.recycleview)
    LRecyclerView recycleview;
    RenalCircleAdapter renalCircleAdapter;
    LRecyclerViewAdapter mLRecyclerViewAdapter;
    private int page = 1,pageCount;
    private int REQUEST_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recycleview);
        Intent intent = getIntent();
        q_id = intent.getStringExtra("q_id");
        initView();
        getDataForService();
    }
    private void getDataForService() {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("q_id",q_id);
        linkedHashMap.put("page",page+"");
        linkedHashMap.put("sign",MD5Util.MD5(GetTime.getTimestamp()));
        startLoad(UrlGlobal.LIST_URL,linkedHashMap);
    }


    @Override
    public void initData(String data) {
        super.initData(data);
        upDataResponse(data, 0);
    }

    private void requestData(final int i) {
        if( i == 0){
            page = 1;
        }else if(i == 1){
            page ++ ;
        }
        OkHttpUtils.post()
                .url(UrlGlobal.LIST_URL)
                .addParams("q_id", q_id)
                .addParams("page", page+"")
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(RenalCirclePagerActivity.this,"请求失败");
                        // 停止刷新
                        recycleview.refreshComplete(REQUEST_COUNT);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        upDataResponse(response, i);
                    }
                });
    }

    private void upDataResponse(String response, int i) {
        CircleCatePagerBean circleCatePagerBean = GsonUtil.getInstance().fromJson(response,CircleCatePagerBean.class);
        if(circleCatePagerBean.code == 0){
            try {
                page = Integer.parseInt(circleCatePagerBean.page);
                pageCount = circleCatePagerBean.pagecount;
                if(circleCatePagerBean.data!=null){
                    if(i == 0) {
                        renalCircleAdapter.setDataList(circleCatePagerBean.data);
                        if(circleCatePagerBean.data == null  || circleCatePagerBean.data.size() == 0){
                            ToastUtils.showLongToast(this,"没有数据");
                        }
                    }else if(i == 1){
                        renalCircleAdapter.addAll(circleCatePagerBean.data);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                ToastUtils.showLongToast(this,"没有数据");
            }
        }else{
            ToastUtils.showLongToast(RenalCirclePagerActivity.this,circleCatePagerBean.msg);
        }
        // 停止刷新
        recycleview.refreshComplete(REQUEST_COUNT);
    }


    private void initView() {
        ivLeft.setVisibility(View.VISIBLE);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText("肾友圈");
        ivRight.setVisibility(View.INVISIBLE);
        ivRightRed.setVisibility(View.INVISIBLE);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("发帖");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RenalCirclePagerActivity.this, SendMessageActivity.class).putExtra("q_id", q_id));
            }
        });
        initRecycleView();
    }

    private void initRecycleView() {
        renalCircleAdapter = new RenalCircleAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleview.setLayoutManager(linearLayoutManager);
        recycleview.setItemAnimator(new DefaultItemAnimator());
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setColorResource(R.color.text)
                .build();
        recycleview.addItemDecoration(divider);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(renalCircleAdapter);
        recycleview.setAdapter(mLRecyclerViewAdapter);
        recycleview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recycleview.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        recycleview.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // 加载更多
                if(page < pageCount){
                    requestData(1);
                }else{
                    recycleview.setNoMore(true);
                }
            }
        });
        recycleview.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(0);
            }
        });
        //设置底部加载文字提示
      //  recycleview.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(RenalCirclePagerActivity.this, WebViewCircleActivity.class);
                intent.putExtra("url", renalCircleAdapter.getDataList().get(position).url);
                intent.putExtra("id", renalCircleAdapter.getDataList().get(position).id);
                intent.putExtra("title",renalCircleAdapter.getDataList().get(position).title);
                startActivity(intent);
            }
        });
    }

}
