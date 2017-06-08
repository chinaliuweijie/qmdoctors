package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
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
import com.qingmiao.qmdoctor.bean.DocumentTalkBean;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import okhttp3.Call;

/**
 *  规范解读   学术交流
 */
public class DocumentTalkActivity extends BaseActivity {

    @BindView(R.id.recycleview)
    LRecyclerView lRecyclerView;
    private int  page = 0;
    private int pagecount = 1;
    private ListBaseAdapter<DocumentTalkBean.DocumentTalkData> coummunListAdapter;
    private int REQUEST_COUNT = 10;
    private String c_id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recycleview);
        getIntentData();
        initRecycleView();
        getDataForService();
    }

    private void getIntentData() {
        c_id = this.getIntent().getStringExtra("c_id");
        if(!TextUtils.isEmpty(c_id)&&c_id.equals("8")){
            tvCenter.setText("规范解读");
        }else if(!TextUtils.isEmpty(c_id)&&c_id.equals("12")){
            tvCenter.setText("学术交流");
        }else if(!TextUtils.isEmpty(c_id)&&c_id.equals("11")){
            tvCenter.setText("精选文章");
        }
    }

    private void getDataForService() {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("page",page+"");
        linkedHashMap.put("c_id",c_id);
        startLoad(UrlGlobal.GET_INFORMATIONLIST,linkedHashMap);
    }


    private void requestData(final int type){
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("page",page+"");
        linkedHashMap.put("c_id",c_id);
        OkHttpUtils.post()
                .url(UrlGlobal.GET_INFORMATIONLIST)
                .params(linkedHashMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(DocumentTalkActivity.this,"请求失败");
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
        lRecyclerView.addItemDecoration(divider);
        coummunListAdapter = new ListBaseAdapter<DocumentTalkBean.DocumentTalkData>(this) {
            @Override
            public int getLayoutId() {
                return R.layout.home_list_item;
            }

            @Override
            public void onBindItemHolder(final SuperViewHolder holder, final int position) {
                final DocumentTalkBean.DocumentTalkData collectData = getDataList().get(position);
                ImageView iv = holder.getView(R.id.home_list_item_iv);
                GlideUtils.LoadImage(mContext,collectData.thumb,iv);
                TextView title = holder.getView(R.id.home_list_item_title);
                title.setText(collectData.title);
                TextView content = holder.getView(R.id.home_list_item_content);
                content.setText(collectData.description);
                TextView time =  holder.getView(R.id.home_list_item_time);
                time.setText(TimeUtils.getStrTime(collectData.updatetime));
                RelativeLayout rlContent = holder.getView(R.id.rl_content);
                rlContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     Intent intent ;
                    if(!TextUtils.isEmpty(c_id)&&"8".equals(c_id)){
                         intent = new Intent(DocumentTalkActivity.this, PDFWebViewActivity.class);
                    }else{
                        intent = new Intent(DocumentTalkActivity.this, WebViewZActivity.class);
                    }
                    intent.putExtra("url", collectData.url);
                    intent.putExtra("id", collectData.id);
                    intent.putExtra("title",collectData.title);
                    startActivity(intent);

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

    /**
     * @param type  0 代表刷新   1 代表加载更多
     * @param data
     */
    private void refreshRecycle(int type,String data){
        DocumentTalkBean collectDocumentBean = GsonUtil.getInstance().fromJson(data,DocumentTalkBean.class);
        if(collectDocumentBean.code == 0){
            if(collectDocumentBean.data!=null && collectDocumentBean.data.size()>0){
                if(type == 0){
                    coummunListAdapter.setDataList(collectDocumentBean.data);
                }else if(type ==1){
                    coummunListAdapter.addAll(collectDocumentBean.data);
                }
                page = collectDocumentBean.page;
                pagecount = collectDocumentBean.pagecount;
            }else{
                ToastUtils.showLongToast(this,"没有数据");
            }
        }else{
            ToastUtils.showLongToast(this,collectDocumentBean.msg);
        }
        // 停止刷新
        lRecyclerView.refreshComplete(REQUEST_COUNT);
    }


    
}
