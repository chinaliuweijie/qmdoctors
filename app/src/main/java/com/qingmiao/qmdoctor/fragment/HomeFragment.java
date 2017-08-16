package com.qingmiao.qmdoctor.fragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.hyphenate.easeui.utils.GlideUtils;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.DensityUtil;
import com.mylhyl.superdialog.SuperDialog;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.activity.AboutUsActivity;
import com.qingmiao.qmdoctor.activity.AddLabelActivity;
import com.qingmiao.qmdoctor.activity.DocumentTalkActivity;
import com.qingmiao.qmdoctor.activity.InstitutePrevueActivity;
import com.qingmiao.qmdoctor.activity.LabelListActivity;
import com.qingmiao.qmdoctor.activity.LoginActivity;
import com.qingmiao.qmdoctor.activity.TalkDocuterActivity;
import com.qingmiao.qmdoctor.activity.TalkPatientCaseActivity;
import com.qingmiao.qmdoctor.activity.UserRuleWebActivity;
import com.qingmiao.qmdoctor.activity.WebViewZActivity;
import com.qingmiao.qmdoctor.adapter.LabelInfoAdapter;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.DoctorDataBean;
import com.qingmiao.qmdoctor.bean.HomeDocindexBean;
import com.qingmiao.qmdoctor.bean.TagsListBean;
import com.qingmiao.qmdoctor.bean.VersionBean;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.global.MyApplication;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.ACache;
import com.qingmiao.qmdoctor.utils.DownloadUtils;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.CycleViewPager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


public class HomeFragment extends BaseFragment implements View.OnClickListener{
    @BindView(R.id.recycleview)
    LRecyclerView lRecyclerView;
    LinearLayout llDocuments,llInstitute,llDocuterTalk,llLearningTalk,llCaseTalk,llTools;
    RelativeLayout rlDocumentsMore;
    CycleViewPager viewpager;

    private ListBaseAdapter<HomeDocindexBean.JXNewsData> newsDataListBaseAdapter;
    private String did,token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        // 自动检测新版本
//        OkHttpUtils.post()
//                .url(UrlGlobal.GET_VERSION)
//                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
//                .addParams("type","2")
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        System.out.println(e.toString());
//                    //    ToastUtils.showLongToast(AboutUsActivity.this,"当前已经是最新版本!");
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        try {
//                            final VersionBean versionBean = GsonUtil.getInstance().fromJson(response,VersionBean.class);
//                            if(versionBean.code == 0){
//                                int mVersion = getVersion();
//                                int sVersion = Integer.parseInt(versionBean.data.get(0).android_version);
//                                if(sVersion>mVersion){
//                                    int[] contentPadding = {20, 0, 20, 20};
//                                    new SuperDialog.Builder(getActivity()).setTitle("提示",getResources().getColor(R.color.black_1), (int) getResources().getDimension(R.dimen.tv_sitem_title))
//                                            .setBackgroundColor(getResources().getColor(R.color.white)).setMessage("发现新版本,是否更新!",getResources().getColor(R.color.black_1),(int) getResources().getDimension(R.dimen.tv_sitem_content),contentPadding)
//                                            .setNegativeButton("确定", getResources().getColor(R.color.green),(int) getResources().getDimension(R.dimen.tv_sitem_title),-1,new SuperDialog.OnClickNegativeListener(){
//
//                                                @Override
//                                                public void onClick(View v) {
//                                                    DownloadUtils downloadUtils = new DownloadUtils(getActivity());
//                                                    downloadUtils.download(versionBean.data.get(0).android_url);
//                                                }
//                                            }).setWidth(0.7f)
//                                            .setPositiveButton("取消", getResources().getColor(R.color.green),(int) getResources().getDimension(R.dimen.tv_sitem_title),-1,new SuperDialog.OnClickPositiveListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    //  推出应用
//                                                    getActivity().finish();
//                                                }
//                                            }).build();
//                                }else{
//                                 //   ToastUtils.showLongToast(getActivity(),"当前已经是最新版本!");
//                                }
//                            }
//                        }catch (Exception e){
//                            e.printStackTrace();
//                          //  ToastUtils.showLongToast(AboutUsActivity.this,"当前已经是最新版本!");
//                        }
//
//                    }
//                });
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public int getVersion() {
        try {
            PackageManager manager = this.getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getActivity().getPackageName(), 0);
            int version = info.versionCode;
            return  version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {
        if("changeData".equals(event)) {

        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View homeView = View.inflate(mActivity, R.layout.view_recycleview, null);
        ButterKnife.bind(this, homeView);
        return homeView;
    }




    @Override
    public void initView() {
        initRecycleView();
    }

    private void setViewPagerStart(List<HomeDocindexBean.ABSData> ads) {
        if(ads!=null && ads.size()>0){
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            for (HomeDocindexBean.ABSData absData:ads) {
                map.put(absData.title,absData.img);
            }
            if(viewpager.getHandler()==null){
                viewpager.setHandler(new Handler());
            }
            viewpager.getResIdMap().clear();
            viewpager.setURLMap(map).setDuration(3000).start();
            viewpager.setViewPagerData(ads);
            viewpager.setTitleTextSize(getResources().getDimensionPixelSize(R.dimen.tv_sitem_content));
            viewpager.setOnSizeChanged();
            viewpager.setViewPagerOnClick(new CycleViewPager.ViewPagerOnClick() {
                @Override
                public void onClickPager(View view, int position, String title, String imgUrl) {
                    List<HomeDocindexBean.ABSData> viewPagerData = viewpager.getViewPagerData();
                    if(viewPagerData!=null){
                        String did = PrefUtils.getString(getActivity(),"did","");
                        String token = PrefUtils.getString(getActivity(),"token","");
                        if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(mActivity, WebViewZActivity.class);
                            intent.putExtra("url", viewPagerData.get(position).url);
                            intent.putExtra("id", viewPagerData.get(position).id);
                            intent.putExtra("isShowBottom",false);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    private void initRecycleView() {
		List<HomeDocindexBean.JXNewsData> contactModels = new ArrayList<>();
		LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        newsDataListBaseAdapter = new ListBaseAdapter<HomeDocindexBean.JXNewsData>(this.getContext()) {
            @Override
            public int getLayoutId() {
                return R.layout.home_list_item;
            }

            @Override
            public void onBindItemHolder(SuperViewHolder holder, int position) {
                HomeDocindexBean.JXNewsData jxNewsData = getDataList().get(position);
                ImageView iv = holder.getView(R.id.home_list_item_iv);
                GlideUtils.LoadImage(getContext(), jxNewsData.thumb,iv);
                TextView title = holder.getView(R.id.home_list_item_title);
                title.setText(jxNewsData.title);
                TextView content = holder.getView(R.id.home_list_item_content);
                content.setText(jxNewsData.description);
                TextView time = holder.getView(R.id.home_list_item_time);
                long l = Long.parseLong(jxNewsData.updatetime + "000");
                String timestampString = DateUtils.getTimestampString(new Date(l));
                time.setText(timestampString);
            }
        };
        lRecyclerView.setLayoutManager(layoutManager);
        lRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerDecoration divider = new DividerDecoration.Builder(this.getContext())
                .setColorResource(R.color.text)
                .build();
        lRecyclerView.addItemDecoration(divider);
        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(newsDataListBaseAdapter);
        lRecyclerView.setAdapter(mLRecyclerViewAdapter);
        lRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        lRecyclerView.setPullRefreshEnabled(true);
        mLRecyclerViewAdapter.removeFooterView();
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String did = PrefUtils.getString(getActivity(),"did","");
                String token = PrefUtils.getString(getActivity(),"token","");
                if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(mActivity, WebViewZActivity.class);
                    intent.putExtra("url", newsDataListBaseAdapter.getDataList().get(position).url);
                    intent.putExtra("id", newsDataListBaseAdapter.getDataList().get(position).id);
                    startActivity(intent);
                }
            }
        });
        lRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getServiceData();
            }
        });
        View headView = View.inflate(getContext(),R.layout.fragment_main_head,null);
        mLRecyclerViewAdapter.addHeaderView(headView);
        initHeadView(headView);
    }

    private void initHeadView(View headView) {
        llDocuments = (LinearLayout) headView.findViewById(R.id.ll_documents);
        llInstitute = (LinearLayout) headView.findViewById(R.id.ll_institute);
        llDocuterTalk = (LinearLayout) headView.findViewById(R.id.ll_docuter_talk);
        llLearningTalk = (LinearLayout) headView.findViewById(R.id.ll_learning_talk);
        llCaseTalk = (LinearLayout) headView.findViewById(R.id.ll_case_talk);
        llTools = (LinearLayout) headView.findViewById(R.id.ll_tools);
        rlDocumentsMore = (RelativeLayout) headView.findViewById(R.id.rl_documents_more);
        llDocuments.setOnClickListener(this);
        llInstitute.setOnClickListener(this);
        llDocuterTalk.setOnClickListener(this);
        llLearningTalk.setOnClickListener(this);
        llCaseTalk.setOnClickListener(this);
        llTools.setOnClickListener(this);
        rlDocumentsMore.setOnClickListener(this);
        viewpager = (CycleViewPager) headView.findViewById(R.id.viewpager);
    }


    private void getServiceData(){
        OkHttpUtils.post()
                .url(UrlGlobal.HOME_DOCINDEX)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(getContext(),"无法连接到网络,请检查网络设置");
                        // 停止刷新
                        lRecyclerView.refreshComplete(10);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        parseJson(response);
                    }
                });
    }


    private void parseJson(String response){
        HomeDocindexBean homeDocindexBean = GsonUtil.getInstance().fromJson(response,HomeDocindexBean.class);
        if(homeDocindexBean.code == 0 && homeDocindexBean.data!=null){
            // 缓存数据
            MyApplication.aCache.put("HomeListDean", response, ACache.TIME_DAY);
            newsDataListBaseAdapter.setDataList(homeDocindexBean.data.jxnews);
            // 设置viewPager 的图片
            setViewPagerStart(homeDocindexBean.data.ads);
        }else{
            ToastUtils.showLongToast(getContext(),"加载失败,请重新获取");
        }
        // 停止刷新
        lRecyclerView.refreshComplete(10);
    }



    @Override
    public void initData() {
        did = PrefUtils.getString(getContext(),"did","");
        token = PrefUtils.getString(getContext(),"token","");
        String listData = MyApplication.aCache.getAsString("HomeListDean");
        if (TextUtils.isEmpty(listData)) {
            getServiceData();
        } else {
            parseJson(listData);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
        if (viewpager != null){
            // 取消轮播定时器
            viewpager.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        String did = PrefUtils.getString(getActivity(),"did","");
        String token = PrefUtils.getString(getActivity(),"token","");
        if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return ;
        }
        switch (v.getId()){
            case R.id.ll_documents:
                Intent intent = new Intent(getActivity(),DocumentTalkActivity.class);
                intent.putExtra("c_id","8");
                startActivity(intent);
               break;
            case R.id.ll_institute:
                Intent iIntent = new Intent(getActivity(),InstitutePrevueActivity.class);
                iIntent.putExtra("c_id","9");
                startActivity(iIntent);
                break;
            case R.id.ll_docuter_talk:
                Intent dintent = new Intent(getActivity(),TalkDocuterActivity.class);
                dintent.putExtra("c_id","10");
                startActivity(dintent);
                break;
            case R.id.ll_learning_talk:
                Intent lintent = new Intent(getActivity(),DocumentTalkActivity.class);
                lintent.putExtra("c_id","12");
                startActivity(lintent);
                break;
            case R.id.ll_case_talk:
                Intent tIntent = new Intent(getActivity(),TalkPatientCaseActivity.class);
                startActivity(tIntent);
                break;
            case R.id.ll_tools:
                Intent rintent = new Intent(getActivity(),UserRuleWebActivity.class);
                rintent.putExtra("title","工具箱");
                rintent.putExtra("url",UrlGlobal.TOOLS);
                startActivity(rintent);

                break;
            case R.id.rl_documents_more:
                Intent mintent = new Intent(getActivity(),DocumentTalkActivity.class);
                mintent.putExtra("c_id","11");
                startActivity(mintent);
               break;
        }
    }
}
