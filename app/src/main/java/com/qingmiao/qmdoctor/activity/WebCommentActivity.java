package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.CommentListViewAdapter;
import com.qingmiao.qmdoctor.adapter.RenalCircleAdapter;
import com.qingmiao.qmdoctor.bean.CircleCatePagerBean;
import com.qingmiao.qmdoctor.bean.CommentBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import okhttp3.Call;

public class WebCommentActivity extends BaseActivity {

    @BindView(R.id.comment_lv)
    LRecyclerView commentLv;
    private int page = 1;
    private int pageCount = 1;
    private String id;
    private CommentListViewAdapter commentListViewAdapter;
    private int REQUEST_COUNT = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_comment);
        initView();
        loadData();
    }

    private void loadData() {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("id",id);
        linkedHashMap.put("page", "" + page);
        linkedHashMap.put("sign",MD5Util.MD5(GetTime.getTimestamp()));
        startLoad(UrlGlobal.GET_INFORMATION_COMMENT,linkedHashMap);
    }


    @Override
    public void initData(String data) {
        super.initData(data);
        upDataResponse(data,0);
    }

    private void initView() {
        ivLeft.setVisibility(View.VISIBLE);
        tvCenter.setText("");
        id = getIntent().getStringExtra("id");
        initRecycleView();
    }

    private void initRecycleView() {
        commentListViewAdapter = new CommentListViewAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentLv.setLayoutManager(linearLayoutManager);
        commentLv.setItemAnimator(new DefaultItemAnimator());
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setColorResource(R.color.text)
                .build();
        commentLv.addItemDecoration(divider);
        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(commentListViewAdapter);
        commentLv.setAdapter(mLRecyclerViewAdapter);
        commentLv.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        commentLv.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        commentLv.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // 加载更多
                if(page < pageCount){
                    requestData(1);
                }else{
                    commentLv.setNoMore(true);
                }
            }
        });
        commentLv.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(0);
            }
        });
        //设置底部加载文字提示
     //   commentLv.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        View view = View.inflate(this,R.layout.view_commennt_hot,null);
        mLRecyclerViewAdapter.addHeaderView(view);
    }



    private void requestData(final int i) {
        if( i == 0){
            page = 1;
        }else if(i == 1){
            page ++ ;
        }
        OkHttpUtils.post()
                .url(UrlGlobal.GET_INFORMATION_COMMENT)
                .addParams("id", id)
                .addParams("page", page+"")
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(WebCommentActivity.this,"请求失败");
                        // 停止刷新
                        commentLv.refreshComplete(REQUEST_COUNT);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        upDataResponse(response, i);
                    }
                });
    }


    private void upDataResponse(String data, int i) {
        CommentBean commentBean = GsonUtil.getInstance().fromJson(data, CommentBean.class);
        if(commentBean.code == 0){
            page = Integer.parseInt(commentBean.page);
            pageCount = commentBean.pagecount;
            if(commentBean.data!=null){
                if(i == 0) {
                    commentListViewAdapter.setDataList(commentBean.data);
                }else if(i == 1){
                    commentListViewAdapter.addAll(commentBean.data);
                }
            }
        }else{
            ToastUtils.showLongToast(WebCommentActivity.this,commentBean.msg);
        }
        // 停止刷新
        commentLv.refreshComplete(REQUEST_COUNT);
    }

}
