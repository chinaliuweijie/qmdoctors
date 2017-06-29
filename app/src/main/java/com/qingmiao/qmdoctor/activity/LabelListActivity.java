package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.ContactAdapter;
import com.qingmiao.qmdoctor.adapter.LabelInfoAdapter;
import com.qingmiao.qmdoctor.bean.ContactModel;
import com.qingmiao.qmdoctor.bean.TagsListBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/4/19.
 */

public class LabelListActivity extends BaseActivity implements ILibelInfoView {
    @BindView(R.id.recyclerview)
    LRecyclerView recyclerview;
    private List<TagsListBean.Tags> tagsList;
    LabelInfoAdapter labelInfoAdapter;
    LibelInfoPresenter libelInfoPresenter;
    @BindView(R.id.ll_addlible)
    LinearLayout llAddLabel;
    int deletePosition;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_libel);
        initView();
        tvCenter.setText("标签");
        initHttp();
        EventBus.getDefault().register(this);//在当前界面注册一个订阅者
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {
        if("updataLabeList".equals(event)) {
            initHttp();
        }
    }



    private void initHttp() {
        // 获取医生本人的信息
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("did", did);
        linkedHashMap.put("token", token);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        startLoad(UrlGlobal.GET_TAGSLIST, linkedHashMap);
    }



    @Override
    public void showLibelProgress(String uri) {
        showLoadingDialog(uri,"删除标签中");
    }

    @Override
    public void hideLibelProgress(String uri) {
        dismissLoadDialog();
    }

    @Override
    public void getLibelData(String uri, String data) {
        switch (uri){
            case UrlGlobal.DELETE_TAGS:
                //删除标签
                labelInfoAdapter.getDataList().remove(deletePosition);
                labelInfoAdapter.notifyItemRemoved(deletePosition);

                if(deletePosition != (labelInfoAdapter.getDataList().size())){ // 如果移除的是最后一个，忽略 注意：这里的mDataAdapter.getDataList()不需要-1，因为上面已经-1了
                    labelInfoAdapter.notifyItemRangeChanged(deletePosition, labelInfoAdapter.getDataList().size() - deletePosition);
                }
                //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                labelInfoAdapter.notifyDataSetChanged();
                EventBus.getDefault().post("updata_patient");
                break;
            case "1":

                break;
        }

    }

    @Override
    public void initData(String data) {
        super.initData(data);

        TagsListBean tagsListBean = GsonUtil.getInstance().fromJson(data, TagsListBean.class);
        if (tagsListBean.code == 0) {
            tagsList.clear();
            tagsList.addAll(tagsListBean.data);
            labelInfoAdapter.setDataList(tagsList);
            labelInfoAdapter.notifyDataSetChanged();
        } else {
            ToastUtils.showLongToast(this, tagsListBean.msg);
        }
    }

    private void initView() {
        llAddLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加新标签
                Intent intent = new Intent(LabelListActivity.this,AddLabelActivity.class);
                startActivity(intent);
            }
        });
        tagsList = new ArrayList<>();
        libelInfoPresenter = new LibelInfoPresenter(this);
        initRecycelView();
    }

    private void initRecycelView() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setColorResource(R.color.text)
                .build();
        recyclerview.addItemDecoration(divider);
        labelInfoAdapter = new LabelInfoAdapter(this,tagsList);
        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(labelInfoAdapter);
        recyclerview.setAdapter(mLRecyclerViewAdapter);
        recyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerview.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        recyclerview.setPullRefreshEnabled(false);
        mLRecyclerViewAdapter.removeFooterView();
        labelInfoAdapter.setOnItemClickListener(new LabelInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TagsListBean.Tags tags, int position) {
                Intent intent = new Intent(LabelListActivity.this,AddLabelActivity.class);
                intent.putExtra("id",tags.lab_id);
                intent.putExtra("name",tags.tag_name);
                startActivity(intent);
            }

            @Override
            public void onItemMenuClick(TagsListBean.Tags tags, int position) {
                //调用删除的借口
                LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put("did",did);
                linkedHashMap.put("token",token);
                linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                linkedHashMap.put("id",tags.lab_id);
                libelInfoPresenter.startLoad(UrlGlobal.DELETE_TAGS,linkedHashMap);
                deletePosition = position;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }
}
