package com.qingmiao.qmdoctor.activity;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
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


public class PatientDescribeActivity extends BaseActivity implements ILibelInfoView {

    @BindView(R.id.et_detail)
    EditText edDetail;
    @BindView(R.id.tv_length)
    TextView tvLength;
    @BindView(R.id.iv_acatar)
    ImageView ivAcatar;
    @BindView(R.id.tv_nick)
    TextView tvNick;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_data)
    AutoSplitTextView tvData;
    @BindView(R.id.iv_data)
    ImageView ivData;
    @BindView(R.id.voice_layout)
    RelativeLayout voiceLayout ;
    @BindView(R.id.iv_voice_image)
    ImageView mIvVoice;
    @BindView(R.id.iv_voice_image_anim)
    ImageView mIvVoiceAnim;
    @BindView(R.id.chat_tv_voice_len)
    TextView tvVoiceLength;
    @BindView(R.id.ll_picker)
    LinearLayout llPicker;
    @BindView(R.id.tv_date)
    TextView tvDate;
    private String uid;
    private AnimationDrawable mImageAnim;
    private MediaPlayUtil mMediaPlayUtil;
    private int maxlength = 200;
    private LibelInfoPresenter libelInfoPresenter;
    private TimePickerView pvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_describe1);
        initView();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initView() {
        uid = getIntent().getStringExtra("uid");
        tvCenter.setText("患者描述");
        tvRight.setText("发布");
        tvRight.setVisibility(View.VISIBLE);
        edDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = edDetail.getText().toString();
                tvLength.setText(content.length()+"/" + maxlength);
                if(content.length() >= maxlength){
                    ToastUtils.showLongToast(PatientDescribeActivity.this,"最多可编辑200个文字");
                }
            }
        });
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 提交
                int  type = getIntent().getIntExtra("type",0);
                String strTime = tvDate.getText().toString() + " " + tvTime.getText().toString();
                switch(type){
                    case 0:
                        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<String, String>();
                        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                        linkedHashMap.put("uid",uid);
                        linkedHashMap.put("did", did);
                        linkedHashMap.put("token", token);
                        linkedHashMap.put("sick_desc",getIntent().getStringExtra("data"));
                        linkedHashMap.put("msg",edDetail.getText().toString());
                        linkedHashMap.put("time", TimeUtils.getLongTime(strTime,"yyyy-MM-dd HH:mm:ss")+"");
                        libelInfoPresenter.startLoad(UrlGlobal.SET_PATIENT_DESC,linkedHashMap);
                        break;
                    case 1:
                        // 语音
                        String data = getIntent().getStringExtra("data");
                        SoundBean soundBean = (SoundBean) getIntent().getSerializableExtra("sound");
                        LinkedHashMap<String,String> hashMap = new LinkedHashMap<String, String>();
                        hashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                        hashMap.put("uid",uid);
                        hashMap.put("did", did);
                        hashMap.put("token", token);
                        hashMap.put("sound",soundBean.sound);
                        hashMap.put("sound_time",MediaPlayUtil.getRingDuring(data)+"");
                        hashMap.put("msg",edDetail.getText().toString());
                        hashMap.put("time",TimeUtils.getLongTime(strTime,"yyyy-MM-dd HH:mm:ss")+"");
                        libelInfoPresenter.startLoad(UrlGlobal.SET_PATIENT_DESC,hashMap);
                        break;
                    case 2:
                        // 图片
                        PicBean picBean = (PicBean) getIntent().getSerializableExtra("pic");
                        LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                        map.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                        map.put("uid",uid);
                        map.put("did", did);
                        map.put("token", token);
                        map.put("pic",picBean.img_url);
                        map.put("thumb_pic",picBean.thumb_url);
                        map.put("msg",edDetail.getText().toString());
                        map.put("time",TimeUtils.getLongTime(strTime,"yyyy-MM-dd HH:mm:ss")+"");
                        libelInfoPresenter.startLoad(UrlGlobal.SET_PATIENT_DESC,map);
                        break;
                    default:
                        break;
                }
            }
        });
        String data = getIntent().getStringExtra("data");
        String avatar = getIntent().getStringExtra("avatar");
        String nickname = getIntent().getStringExtra("nickname");
        GlideUtils.LoadImage(PatientDescribeActivity.this,avatar,ivAcatar);
        tvNick.setText(nickname);
        int  type = getIntent().getIntExtra("type",0);
        if(type == 0){
            tvData.setText(data);
            tvData.setVisibility(View.VISIBLE);
            ivData.setVisibility(View.GONE);
            voiceLayout.setVisibility(View.GONE);
        }else if(type == 1){
            mMediaPlayUtil = MediaPlayUtil.getInstance();
            // 设置文件时间长
            tvVoiceLength.setText(MediaPlayUtil.getRingDuring(data)+"秒");
            tvData.setVisibility(View.GONE);
            ivData.setVisibility(View.GONE);
            voiceLayout.setVisibility(View.VISIBLE);
            voiceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String data = getIntent().getStringExtra("data");
                    if (mMediaPlayUtil.isPlaying()) {
                        mMediaPlayUtil.stop();
                        mImageAnim.stop();
                        mIvVoice.setVisibility(View.VISIBLE);
                        mIvVoiceAnim.setVisibility(View.GONE);
                    } else {
                        startAnim();
                        mMediaPlayUtil.play(data);
                    }
                }
            });
        }else if(type ==2){
            tvData.setVisibility(View.GONE);
            ivData.setVisibility(View.VISIBLE);
            voiceLayout.setVisibility(View.GONE);
            GlideUtils.LoadImageWithLocation(PatientDescribeActivity.this,data,ivData);
        }
        libelInfoPresenter = new LibelInfoPresenter(this);
        llPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        initTimePicker();
    }

    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11\
        String strtime = getTime(new Date());
        String time[] = strtime.split("@@");
        tvDate.setText(time[0]);
        tvTime.setText(time[1]);
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(new Date());
        //时间选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //选中事件回调
                String strtime = getTime(date);
                String time[] = strtime.split("@@");
                tvDate.setText(time[0]);
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false)
                .setDividerColor(getResources().getColor(R.color.text))
                .setCancelText("取消")
                .setSubmitText("确定")
                .setSubmitColor(getResources().getColor(R.color.colorAccent))
                .setCancelColor(getResources().getColor(R.color.colorAccent))
                .setContentSize(DensityUtil.px2dip(this,getResources().getDimension(R.dimen.tv_sview_title_detail)))
                .setTitleSize((int) getResources().getDimension(R.dimen.tv_sview_title_detail))
              //  .setTitleText("")
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
    }


    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd@@HH:mm:ss");
        return format.format(date);
    }

    /**
     * 语音播放效果
     *
     */
    public void startAnim() {

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
    protected void onStop() {
        super.onStop();
        if (mMediaPlayUtil!=null && mMediaPlayUtil.isPlaying()) {
            mMediaPlayUtil.stop();
            mImageAnim.stop();
            mIvVoice.setVisibility(View.VISIBLE);
            mIvVoiceAnim.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLibelProgress(String uri) {
        showLoadingDialog(uri,"提交中");
    }

    @Override
    public void hideLibelProgress(String uri) {
        dismissLoadDialog();
    }

    @Override
    public void getLibelData(String uri, String data) {
        LogUtil.LogShitou(data);
        Result result = GsonUtil.getInstance().fromJson(data,Result.class);
        if(result.code == 0){
            EventBus.getDefault().post("updataPatient");
            finish();
        }else{
            ToastUtils.showLongToast(this,result.msg);
        }
    }
}
