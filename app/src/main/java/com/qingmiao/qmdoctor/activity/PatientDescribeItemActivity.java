package com.qingmiao.qmdoctor.activity;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.hyphenate.easeui.utils.GlideUtils;
import com.hyphenate.util.DensityUtil;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.PatientDescListBean;
import com.qingmiao.qmdoctor.bean.PicBean;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.bean.SoundBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.MediaPlayUtil;
import com.qingmiao.qmdoctor.utils.TimeUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.qingmiao.qmdoctor.widget.AutoSplitTextView;
import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import butterknife.BindView;


public class PatientDescribeItemActivity extends BaseActivity {

    @BindView(R.id.et_detail)
    TextView edDetail;
    @BindView(R.id.iv_acatar)
    ImageView ivAcatar;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_data)
    AutoSplitTextView tvData;
    @BindView(R.id.ll_picker)
    LinearLayout llPicker;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_content)
    LinearLayout llContent;

    private String uid;
    MediaPlayUtil mediaPlayUtil;
    // 上次的播放音频的view
    private ImageView mIvVoice,mIvVoiceAnim;
    private AnimationDrawable mImageAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_describe_item);
        initView();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initView() {
        mediaPlayUtil = MediaPlayUtil.getInstance();
        uid = getIntent().getStringExtra("uid");
        tvCenter.setText("描述详情");
        PatientDescListBean.DescData descData = (PatientDescListBean.DescData) getIntent().getSerializableExtra("obj");
        if(!TextUtils.isEmpty(descData.sick_desc)){
            tvData.setText(descData.sick_desc);
        }else{
            tvData.setVisibility(View.GONE);
        }
        tvDate.setText(descData.time);
        GlideUtils.LoadImage(PatientDescribeItemActivity.this,descData.avatar,ivAcatar);
        tvNick.setText(descData.d_name);
        edDetail.setText(descData.msg);
        if(!TextUtils.isEmpty(descData.sound)){
            String pathArray[] = descData.sound.split(",");
            for (String path:pathArray) {
                View view = View.inflate(PatientDescribeItemActivity.this,R.layout.view_voice,null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(com.qingmiao.qmdoctor.utils.DensityUtil.dip2px(PatientDescribeItemActivity.this,228),LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = 20;
                llContent.addView(view,llContent.getChildCount(),layoutParams);
                // 设置点击
                TextView voiceLen = (TextView) view.findViewById(R.id.chat_tv_voice_len);
                final ImageView mIvVoice = (ImageView) view.findViewById(R.id.iv_voice_image);
                final ImageView mIvVoiceAnim = (ImageView) view.findViewById(R.id.iv_voice_image_anim);
                voiceLen.setText(MediaPlayUtil.getRingDuring(path)+"秒");
                final String sound = path;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaPlayUtil.isPlaying()) {
                            mediaPlayUtil.stop();
                            mImageAnim.stop();
                            PatientDescribeItemActivity.this.mIvVoice.setVisibility(View.VISIBLE);
                            PatientDescribeItemActivity.this.mIvVoiceAnim.setVisibility(View.INVISIBLE);
                            // 播放
                            startAnim(mIvVoice ,mIvVoiceAnim);
                            mediaPlayUtil.play(sound);
                            // 记录上次的view
                            PatientDescribeItemActivity.this.mIvVoice = mIvVoice;
                            PatientDescribeItemActivity.this.mIvVoiceAnim = mIvVoiceAnim;
                        } else {
                            startAnim(mIvVoice ,mIvVoiceAnim);
                            mediaPlayUtil.play(sound);
                            // 记录上次的view
                            PatientDescribeItemActivity.this.mIvVoice = mIvVoice;
                            PatientDescribeItemActivity.this.mIvVoiceAnim = mIvVoiceAnim;
                        }
                    }
                });
            }
        }
        if(!TextUtils.isEmpty(descData.thumb_pic)){
             String picArray [] =  descData.thumb_pic.split(",");
            for (String pic:picArray) {
                ImageView iv = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = 20;
                iv.setLayoutParams(layoutParams);
                llContent.addView(iv,llContent.getChildCount(),layoutParams);
                GlideUtils.LoadImageWithLocation(this,pic,iv);
            }
        }
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
        mIvVoice.setVisibility(View.INVISIBLE);
        mImageAnim.start();
        mediaPlayUtil.setPlayOnCompleteListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mIvVoice.setVisibility(View.VISIBLE);
                mIvVoiceAnim.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayUtil!=null && mediaPlayUtil.isPlaying()) {
            mediaPlayUtil.stop();
            mImageAnim.stop();
        }
    }



}
