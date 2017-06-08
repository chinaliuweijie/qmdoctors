package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.CollectDocumentBean;
import com.qingmiao.qmdoctor.bean.HXUserData;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.TimeUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.qingmiao.qmdoctor.widget.SwipeMenuView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 获取收藏界面
 */
public class MyCollectDocumentActivity extends BaseActivity {

    @BindView(R.id.recycleview)
    LRecyclerView lRecyclerView;
    private int  page = 0;
    private int pagecount = 1;
    private ListBaseAdapter<CollectDocumentBean.CollectData> coummunListAdapter;
    private int REQUEST_COUNT = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recycleview);
        initRecycleView();
        getDataForService();
    }

    private void getDataForService() {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("type","2");
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("token",token);
        linkedHashMap.put("page",page+"");
        linkedHashMap.put("did",did);
        startLoad(UrlGlobal.GET_COLLECTION,linkedHashMap);
    }


    private void requestData(final int type){
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("type","2");
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("token",token);
        linkedHashMap.put("page",page+"");
        linkedHashMap.put("did",did);
        OkHttpUtils.post()
                .url(UrlGlobal.GET_COLLECTION)
                .params(linkedHashMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(MyCollectDocumentActivity.this,"请求失败");
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
        tvCenter.setText("我的收藏");
        View view = findViewById(R.id.empty_view);
        lRecyclerView.setEmptyView(view);
        IconFontTextview fontTextview = (IconFontTextview) findViewById(R.id.image);
        fontTextview.setText(R.string.icons_star);
        TextView tv = (TextView) findViewById(R.id.text);
        tv.setText("暂无收藏");
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setColorResource(R.color.text)
                .build();
        lRecyclerView.addItemDecoration(divider);
        coummunListAdapter = new ListBaseAdapter<CollectDocumentBean.CollectData>(this) {
            @Override
            public int getLayoutId() {
                return R.layout.home_list_item1;
            }

            @Override
            public void onBindItemHolder(final SuperViewHolder holder, final int position) {
                final CollectDocumentBean.CollectData collectData = getDataList().get(position);
                ImageView iv = holder.getView(R.id.home_list_item_iv);
                GlideUtils.LoadImage(mContext,collectData.thumb,iv);
                TextView title = holder.getView(R.id.home_list_item_title);
                title.setText(collectData.title);
                TextView content = holder.getView(R.id.home_list_item_content);
                content.setText(collectData.description);
                TextView time =  holder.getView(R.id.home_list_item_time);
                time.setText(TimeUtils.getStrTime(collectData.time));
                RelativeLayout rlContent = holder.getView(R.id.rl_content);
                rlContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if("1".equals(collectData.source_type)){
                            // 资讯
                            Intent intent = new Intent(MyCollectDocumentActivity.this, WebViewZActivity.class);
                            intent.putExtra("url", collectData.url);
                            intent.putExtra("id", collectData.id);
                            startActivity(intent);
                        }else if("2".equals(collectData.source_type)){
                            // 圈子
                            Intent intent = new Intent(MyCollectDocumentActivity.this, WebViewCircleActivity.class);
                            intent.putExtra("url", collectData.url);
                            intent.putExtra("id", collectData.id);
                            intent.putExtra("title",collectData.title);
                            startActivity(intent);
                        }
                    }
                });
                Button btnDelete = holder.getView(R.id.btnDelete);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 删除收藏
                        OkHttpUtils.post()
                                .url(UrlGlobal.DEL_COLLECTION)
                                .addParams("did", did)
                                .addParams("token", token)
                                .addParams("type","2")
                                .addParams("id",collectData.c_id)
                                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                                .addParams("source_type",collectData.source_type)
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        ToastUtils.showLongToast(MyCollectDocumentActivity.this, "网络异常");
                                        ((SwipeMenuView) holder.itemView).quickClose();
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String msg = jsonObject.optString("msg");
                                            int code = jsonObject.optInt("code");
                                            if (code == 0) {
                                                ToastUtils.showLongToast(MyCollectDocumentActivity.this, msg);

                                                coummunListAdapter.getDataList().remove(position);
                                                coummunListAdapter.notifyItemRemoved(position);

                                                if(position != (coummunListAdapter.getDataList().size())){ // 如果移除的是最后一个，忽略 注意：这里的mDataAdapter.getDataList()不需要-1，因为上面已经-1了
                                                    coummunListAdapter.notifyItemRangeChanged(position, coummunListAdapter.getDataList().size() - position);
                                                }
                                                //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                                                coummunListAdapter.notifyDataSetChanged();
                                            } else {
                                                ToastUtils.showLongToast(MyCollectDocumentActivity.this, msg);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                });


            }
        };

        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(coummunListAdapter);
        lRecyclerView.setAdapter(mLRecyclerViewAdapter);
        lRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        lRecyclerView.setPullRefreshEnabled(true);
        lRecyclerView.setLoadMoreEnabled(true);
        lRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                requestData(0);
            }
        });
        lRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(page < pagecount){
                    page++;
                    requestData(1);
                }else{
                    lRecyclerView.setNoMore(true);
                }
            }
        });
        lRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
    //    lRecyclerView.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");
    }


    @Override
    public void initData(String data) {
        super.initData(data);
        refreshRecycle(0,data);
    }

    private void refreshRecycle(int type,String data){
        CollectDocumentBean collectDocumentBean = GsonUtil.getInstance().fromJson(data,CollectDocumentBean.class);
        if(collectDocumentBean.code == 0){
            if(collectDocumentBean.data!=null && collectDocumentBean.data.size()>0){
                if(type ==0) {
                    coummunListAdapter.setDataList(collectDocumentBean.data);
                }else if(type ==1){
                    coummunListAdapter.addAll(collectDocumentBean.data);
                }
                page = collectDocumentBean.page;
                pagecount = collectDocumentBean.pagecount;
            }else{
                ToastUtils.showLongToast(this,"没有收藏数据");
            }
        }else{
            ToastUtils.showLongToast(this,collectDocumentBean.msg);
        }
        // 停止刷新
        lRecyclerView.refreshComplete(REQUEST_COUNT);
    }



}
