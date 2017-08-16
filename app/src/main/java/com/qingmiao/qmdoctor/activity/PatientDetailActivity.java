package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.hyphenate.chat.EMTextMessageBody;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.utils.MediaPlayUtil;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.qingmiao.qmdoctor.widget.SwipeMenuView;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.PatientDescListBean;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.TimeUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import butterknife.BindView;
import okhttp3.Call;

public class PatientDetailActivity extends BaseActivity implements ILibelInfoView{
    @BindView(R.id.lRecycleView_sick_desc)
    LRecyclerView lRecyclerViewSickDesc;
    private String uid;
    private ListBaseAdapter<PatientDescListBean.DescData> sickDescAdapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    LinearLayoutManager linearLayoutManager;
    //当前分页
    private int mPage = 1;
    private int REQUEST_COUNT = 10;
    //总共的页数
    private int mPageCount = 1;
    private LibelInfoPresenter libelInfoPresenter;
    private int deletePosition = 0;
    private MediaPlayUtil mMediaPlayUtil;
    private AnimationDrawable mImageAnim;
    // 上次的播放音频的view
    private ImageView mIvVoice,mIvVoiceAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        initView();
        getInitData();
        EventBus.getDefault().register(this);//在当前界面注册一个订阅者
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {
        if("updataPatient".equals(event)) {
            lRecyclerViewSickDesc.refresh();
            lRecyclerViewSickDesc.smoothScrollToPosition(0);
        }
    }



    private void getInitData() {
        uid = getIntent().getStringExtra("uid");
        sickDescAdapter.clear();
       // lRecyclerViewSickDesc.refresh();
       // lRecyclerViewSickDesc.smoothScrollToPosition(0);
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("did", did);
        linkedHashMap.put("token", token);
        linkedHashMap.put("uid", uid);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("page",mPage+"");
        startLoad(UrlGlobal.GET_PATIENT_DESCLIST,linkedHashMap);
    }

    private void initView() {
        tvCenter.setText("病情描述");
        initRecycleView();
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("新增");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientDetailActivity.this,PatientBySelfActivity.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });
        libelInfoPresenter = new LibelInfoPresenter(this);
        mMediaPlayUtil = MediaPlayUtil.getInstance();
    }

    private void initRecycleView() {
        linearLayoutManager = new LinearLayoutManager(this);
        lRecyclerViewSickDesc.setLayoutManager(linearLayoutManager);
        lRecyclerViewSickDesc.setItemAnimator(new DefaultItemAnimator());
        sickDescAdapter = new ListBaseAdapter<PatientDescListBean.DescData>(this) {
            @Override
            public int getLayoutId() {
                return R.layout.patient_describe_item;
            }

            @Override
            public void onBindItemHolder(SuperViewHolder holder, final int position) {
                SwipeMenuView swipe = holder.getView(R.id.swipe_content);
                IconFontTextview deleteView = holder.getView(R.id.delete);
                if(did.equals(getDataList().get(position).did)){
                    swipe.setSwipeEnable(true);
                    swipe.setIos(false).setLeftSwipe(true);
                    deleteView.setVisibility(View.VISIBLE);
                }else{
                    swipe.setSwipeEnable(false);
                    swipe.setIos(false).setLeftSwipe(false);
                    deleteView.setVisibility(View.GONE);
                }
                TextView tvTime = holder.getView(R.id.tv_time);
                TextView tvContent = holder.getView(R.id.tv_content);
                ImageView ivContent = holder.getView(R.id.iv_content);
                RelativeLayout voiceLayout = holder.getView(R.id.voice_layout);

               if(!TextUtils.isEmpty(getDataList().get(position).pic)){
                    tvContent.setVisibility(View.GONE);
                    ivContent.setVisibility(View.VISIBLE);
                    voiceLayout.setVisibility(View.GONE);
                    String path [] = getDataList().get(position).thumb_pic.split(",");
                    GlideUtils.LoadImageFull(PatientDetailActivity.this,path[0],ivContent);
                }else if(!TextUtils.isEmpty(getDataList().get(position).sound)){
                    TextView voiceLen = holder.getView(R.id.chat_tv_voice_len);
                    final ImageView mIvVoice = holder.getView(R.id.iv_voice_image);
                    final ImageView mIvVoiceAnim = holder.getView(R.id.iv_voice_image_anim);
                    tvContent.setVisibility(View.GONE);
                    ivContent.setVisibility(View.GONE);
                    voiceLayout.setVisibility(View.VISIBLE);
                    voiceLen.setText(getDataList().get(position).sound_time+"秒");
                    voiceLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!TextUtils.isEmpty(getDataList().get(position).sound)){
                                String path [] = getDataList().get(position).sound.split(",");
                                if (mMediaPlayUtil.isPlaying()) {
                                    mMediaPlayUtil.stop();
                                    mImageAnim.stop();
                                    PatientDetailActivity.this.mIvVoice.setVisibility(View.VISIBLE);
                                    PatientDetailActivity.this.mIvVoiceAnim.setVisibility(View.GONE);
                                    // 播放
                                    startAnim(mIvVoice ,mIvVoiceAnim);
                                    mMediaPlayUtil.play(path[0]);
                                    // 记录上次的view
                                    PatientDetailActivity.this.mIvVoice = mIvVoice;
                                    PatientDetailActivity.this.mIvVoiceAnim = mIvVoiceAnim;
                                } else {
                                    startAnim(mIvVoice ,mIvVoiceAnim);
                                    mMediaPlayUtil.play(path[0]);
                                // 记录上次的view
                                    PatientDetailActivity.this.mIvVoice = mIvVoice;
                                    PatientDetailActivity.this.mIvVoiceAnim = mIvVoiceAnim;
                                }
                            }
                        }
                    });
                }else if(!TextUtils.isEmpty(getDataList().get(position).sick_desc)){
                   tvContent.setText(getDataList().get(position).sick_desc);
                   tvContent.setVisibility(View.VISIBLE);
                   ivContent.setVisibility(View.GONE);
                   voiceLayout.setVisibility(View.GONE);
               }

                String time = TimeUtils.getStrTime(getDataList().get(position).time);
                if(TextUtils.isEmpty(time)){
                    tvTime.setText(getDataList().get(position).time);
                }else{
                    tvTime.setText(time);
                }

                ImageView ivHead = holder.getView(R.id.iv_head_portrait);
                GlideUtils.LoadCircleAvatarImage(PatientDetailActivity.this,getDataList().get(position).avatar,ivHead);
                TextView tvName = holder.getView(R.id.tv_name);
                tvName.setText(getDataList().get(position).d_name);
                Button btnDelete = holder.getView(R.id.btnDelete);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItemClick(getDataList().get(position));
                        deletePosition = position;
                    }
                });
                deleteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItemClick(getDataList().get(position));
                        deletePosition = position;
                    }
                });
                LinearLayout llMore = holder.getView(R.id.ll_more);
                llMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PatientDescListBean.DescData descData = sickDescAdapter.getDataList().get(position);
                        Intent intent = new Intent(PatientDetailActivity.this,PatientDescribeItemActivity.class);

//                        String nickName = descData.d_name;
//                        if(!TextUtils.isEmpty(descData.sick_desc)){
//                            intent.putExtra("data",descData.sick_desc);
//                            intent.putExtra("type",0);
//                        }else if(!TextUtils.isEmpty(descData.pic)){
//                            intent.putExtra("data",descData.pic);
//                            intent.putExtra("type",2);
//                        }else if(!TextUtils.isEmpty(descData.sound)){
//                            intent.putExtra("data",descData.sound);
//                            intent.putExtra("type",1);
//                        }


//                        intent.putExtra("avatar",descData.avatar);
//                        intent.putExtra("nickname",nickName);
//                        intent.putExtra("msg",descData.msg);
//                        intent.putExtra("time",descData.time);

                        intent.putExtra("uid",uid);
                        intent.putExtra("obj",descData);
                        startActivity(intent);
                    }
                });


            }
        };
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(sickDescAdapter);
        lRecyclerViewSickDesc.setAdapter(mLRecyclerViewAdapter);
        lRecyclerViewSickDesc.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lRecyclerViewSickDesc.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        lRecyclerViewSickDesc.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // 加载更多
                if(mPage < mPageCount){
                    requestData(1);
                }else{
                    lRecyclerViewSickDesc.setNoMore(true);
                }
            }
        });
        lRecyclerViewSickDesc.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(0);
                mPage = 1;
            }
        });
    }

    private void deleteItemClick(PatientDescListBean.DescData descData) {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("did", did);
        linkedHashMap.put("token", token);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("id",descData.id);
        libelInfoPresenter.startLoad(UrlGlobal.DELPATIENT_SICKDESC,linkedHashMap);
    }



    private void requestData(final int type) {
        OkHttpUtils.post()
                .url(UrlGlobal.GET_PATIENT_DESCLIST)
                .addParams("did", did)
                .addParams("token", token)
                .addParams("uid", uid)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .addParams("page",mPage+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(PatientDetailActivity.this, "网络异常");
                        // 显示加载失败
                        lRecyclerViewSickDesc.refreshComplete(REQUEST_COUNT);
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                        lRecyclerViewSickDesc.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
                            @Override
                            public void reload() {
                                requestData(0);
                            }
                        });
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        if(type == 0){
                            // 下啦刷新
                            sickDescAdapter.clear();
                        }
                        if(type == 1){
                            // 上啦加载
                            PatientDescListBean patientInfoBean = GsonUtil.getInstance().fromJson(response, PatientDescListBean.class);
                            if(patientInfoBean.code == 0){
                                mPage ++;
                            }
                        }
                        initData(response);
                    }
                });
    }

    /**
     * 语音播放效果
     *
     */
    public void startAnim(final ImageView mIvVoice ,final ImageView mIvVoiceAnim ) {
        if(mImageAnim!=null && mImageAnim.isRunning()){
            mImageAnim.stop();
        }
        mImageAnim = (AnimationDrawable) mIvVoiceAnim.getBackground();
        mIvVoiceAnim.setVisibility(View.VISIBLE);
        mIvVoice.setVisibility(View.GONE);
        mImageAnim.start();
        mMediaPlayUtil.setPlayOnCompleteListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mIvVoice.setVisibility(View.VISIBLE);
                mIvVoiceAnim.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void initData(String data) {
        super.initData(data);
        PatientDescListBean patientInfoBean = GsonUtil.getInstance().fromJson(data, PatientDescListBean.class);
        try {
            int count = Integer.parseInt(patientInfoBean.pagecount);
            mPageCount = count;
        }catch (Exception e){
            e.printStackTrace();
        }
        if(patientInfoBean.code == 0 ){
            if(patientInfoBean.data ==null){
                patientInfoBean.data = new ArrayList<>();
            }
            sickDescAdapter.addAll(patientInfoBean.data);
          //  REQUEST_COUNT = patientInfoBean.data.size();
            // 停止刷新
            lRecyclerViewSickDesc.refreshComplete(REQUEST_COUNT);
            if(mPage>=mPageCount){
              //  lRecyclerViewSickDesc.setNoMore(true);
            }
        }else{
            ToastUtils.showLongToast(this,patientInfoBean.msg);
            // 显示加载失败
            lRecyclerViewSickDesc.refreshComplete(REQUEST_COUNT);
            mLRecyclerViewAdapter.notifyDataSetChanged();
            lRecyclerViewSickDesc.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
                @Override
                public void reload() {
                    requestData(0);
                }
            });
        }
    }

    @Override
    public void showLibelProgress(String uri) {
        showLoadingDialog(uri,"删除中");
    }

    @Override
    public void hideLibelProgress(String uri) {
        dismissLoadDialog();
    }

    @Override
    public void getLibelData(String uri, String data) {
        Result result = GsonUtil.getInstance().fromJson(data,Result.class);
        if(result.code == 0){
            // 删除成功
            sickDescAdapter.remove(deletePosition);
            lRecyclerViewSickDesc.setNoMore(false);
            EventBus.getDefault().post("updataPatientData");
    //        sickDescAdapter.notifyDataSetChanged();
        }else{
            ToastUtils.showLongToast(this,result.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayUtil!=null && mMediaPlayUtil.isPlaying()) {
            mMediaPlayUtil.stop();
            mImageAnim.stop();
        }
    }
}
