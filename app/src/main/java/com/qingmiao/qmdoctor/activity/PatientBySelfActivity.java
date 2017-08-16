package com.qingmiao.qmdoctor.activity;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.hyphenate.easeui.utils.GlideUtils;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.nanchen.compresshelper.CompressHelper;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.PicBean;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.bean.SoundBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.DensityUtil;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.MediaPlayUtil;
import com.qingmiao.qmdoctor.utils.TimeUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.qingmiao.qmdoctor.widget.IconFontTextview;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.fragment.PhotoPickerFragment;
import me.iwf.photopicker.utils.ImageCaptureManager;
import me.iwf.photopicker.utils.PermissionsUtils;

import static com.qingmiao.qmdoctor.R.string.position;


public class PatientBySelfActivity extends BaseActivity implements ILibelInfoView {

    @BindView(R.id.tv_data)
    TextView tvData;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.et_detail)
    EditText etDetail;
    @BindView(R.id.voice_recorder)
    EaseVoiceRecorderView voiceRecorderView;
    @BindView(R.id.iv_voice)
    IconFontTextview ivVoice;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.ll_picker)
    LinearLayout llPicker;

    private LibelInfoPresenter libelInfoPresenter;
    private String uid;
    private MediaPlayUtil mMediaPlayUtil;
    private AnimationDrawable mImageAnim;
    // 上次的播放音频的view
    private ImageView mIvVoice,mIvVoiceAnim;
    private ImageCaptureManager captureManager;
    private TimePickerView pvTime;
    private String voicePath,picPath,picThumbPath;
    private String locationVoiceLength;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_describe);
        initView();
    }

    private void initView() {
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("发布");
        tvCenter.setText("病情描述");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etDetail.getText().toString();
                if(!TextUtils.isEmpty(content) || !TextUtils.isEmpty(picPath) || !TextUtils.isEmpty(voicePath)){
                    String strTime = tvData.getText().toString() + " " + tvTime.getText().toString();
                    LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
                    linkedHashMap.put("uid",uid);
                    linkedHashMap.put("did", did);
                    linkedHashMap.put("token", token);
                    linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                    linkedHashMap.put("sick_desc",content);
                    linkedHashMap.put("time", TimeUtils.getLongTime(strTime,"yyyy-MM-dd HH:mm:ss")+"");
                    if(!TextUtils.isEmpty(voicePath)){
                        linkedHashMap.put("sound",voicePath);
                        linkedHashMap.put("sound_time",locationVoiceLength);
                    }
                    if(!TextUtils.isEmpty(picThumbPath)){
                        linkedHashMap.put("thumb_pic",picThumbPath);
                    }
                    if(!TextUtils.isEmpty(picPath)){
                        linkedHashMap.put("pic",picPath);
                    }
                    libelInfoPresenter.startLoad(UrlGlobal.SET_PATIENT_DESC,linkedHashMap);
                }else{
                    ToastUtils.showLongToast(PatientBySelfActivity.this,"请输入描述信息");
                }
            }
        });
//        etDetail.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String content = etDetail.getText().toString();
//                tvLength.setText(content.length()+"/" + maxlength);
//                if(content.length() >= maxlength){
//                    ToastUtils.showLongToast(PatientBySelfActivity.this,"最多可编辑200个文字");
//                }
//            }
//        });
        libelInfoPresenter = new LibelInfoPresenter(this);
        uid = getIntent().getStringExtra("uid");
        ivVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(voiceRecorderView!=null){
                    return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {

                        @Override
                        public void onVoiceRecordComplete(final String voiceFilePath, int voiceTimeLength) {
                            //sendVoiceMessage(voiceFilePath, voiceTimeLength);
                            LogUtil.LogShitou(voiceFilePath + voiceTimeLength);
                            if(TextUtils.isEmpty(locationVoiceLength)){
                                locationVoiceLength = voiceTimeLength+"";
                            }
                            // 上传语音
                            uoloadFile(UrlGlobal.UPLOAD_SOUND,voiceFilePath);
                            View view = View.inflate(PatientBySelfActivity.this,R.layout.view_voice,null);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(PatientBySelfActivity.this,228),LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.topMargin = 20;
                            llContent.addView(view,llContent.getChildCount()-1,layoutParams);
                            // 设置点击
                            TextView voiceLen = (TextView) view.findViewById(R.id.chat_tv_voice_len);
                            final ImageView mIvVoice = (ImageView) view.findViewById(R.id.iv_voice_image);
                            final ImageView mIvVoiceAnim = (ImageView) view.findViewById(R.id.iv_voice_image_anim);
                            voiceLen.setText(voiceTimeLength+"秒");
                            final String sound = voiceFilePath;
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mMediaPlayUtil.isPlaying()) {
                                        mMediaPlayUtil.stop();
                                        mImageAnim.stop();
                                        PatientBySelfActivity.this.mIvVoice.setVisibility(View.VISIBLE);
                                        PatientBySelfActivity.this.mIvVoiceAnim.setVisibility(View.INVISIBLE);
                                        // 播放
                                        startAnim(mIvVoice ,mIvVoiceAnim);
                                        mMediaPlayUtil.play(sound);
                                        // 记录上次的view
                                        PatientBySelfActivity.this.mIvVoice = mIvVoice;
                                        PatientBySelfActivity.this.mIvVoiceAnim = mIvVoiceAnim;
                                    } else {
                                        startAnim(mIvVoice ,mIvVoiceAnim);
                                        mMediaPlayUtil.play(sound);
                                        // 记录上次的view
                                        PatientBySelfActivity.this.mIvVoice = mIvVoice;
                                        PatientBySelfActivity.this.mIvVoiceAnim = mIvVoiceAnim;
                                    }
                                }
                            });
                        }
                    });
                }
                return false;
            }
        });
        mMediaPlayUtil  = MediaPlayUtil.getInstance();
        captureManager = new ImageCaptureManager(this);
        initTimePicker();
    }

    private void uoloadFile(String url,String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            ToastUtils.showLongToast(PatientBySelfActivity.this,"当前文件不存在!");
            return ;
        }
        LinkedHashMap<String,String> linkedMap = new LinkedHashMap<String, String>();
        linkedMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        HashMap<String,File> hashFile = new HashMap<String, File>();
        // 如果是图片  压缩图片
        if(UrlGlobal.UPLOAD_PIC.equals(url)){
            file = CompressHelper.getDefault(this).compressToFile(file);
        }
        hashFile.put(file.getName(),file);
        libelInfoPresenter.loadFileParems(url,linkedMap,"file",hashFile);
    }




    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11\
        String strtime = getTime(new Date());
        String time[] = strtime.split("@@");
        tvData.setText(time[0]);
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
                tvData.setText(time[0]);
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
                .setContentSize(com.hyphenate.util.DensityUtil.px2dip(this,getResources().getDimension(R.dimen.tv_sview_title_detail)))
                .setTitleSize((int) getResources().getDimension(R.dimen.tv_sview_title_detail))
                //  .setTitleText("")
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
        llPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show();
            }
        });
    }


    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd@@HH:mm:ss");
        return format.format(date);
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
        mMediaPlayUtil.setPlayOnCompleteListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mIvVoice.setVisibility(View.VISIBLE);
                mIvVoiceAnim.setVisibility(View.INVISIBLE);
            }
        });
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
        if(UrlGlobal.SET_PATIENT_DESC.equals(uri)){
            Result result = GsonUtil.getInstance().fromJson(data,Result.class);
            if(result.code == 0){
                EventBus.getDefault().post("updataPatient");
                finish();
            }else{
                ToastUtils.showLongToast(this,result.msg);
            }
        }else if(UrlGlobal.UPLOAD_SOUND.equals(uri)){
            // 上传语音
            SoundBean soundBean = GsonUtil.getInstance().fromJson(data,SoundBean.class);
            if(soundBean.code!=0){
                ToastUtils.showLongToast(this,"上传失败,请重新上传."+soundBean.msg);
                return;
            }
            if(TextUtils.isEmpty(voicePath)){
                voicePath = soundBean.sound;
            }else{
                voicePath += ","+soundBean.sound;
            }
        }else if(UrlGlobal.UPLOAD_PIC.equals(uri)){
            PicBean picBean = GsonUtil.getInstance().fromJson(data,PicBean.class);
            if(picBean.code!=0){
                ToastUtils.showLongToast(this,"上传失败,请重新上传."+picBean.msg);
                return;
            }
            if(TextUtils.isEmpty(picPath)){
                picPath = picBean.img_url;
                picThumbPath =picBean.thumb_url;
            }else{
                picPath += ","+picBean.img_url;
                picThumbPath +=  ","+picBean.thumb_url;
            }
        }
    }


    public void activityCamera() {
        openCamera();
    }

    public void openCamera() {
        try {
            Intent intent = captureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @OnClick({R.id.iv_take_picture,R.id.iv_picture,R.id.iv_voice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_take_picture:
                activityCamera();
                break;
            case R.id.iv_picture:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(false)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(this, PhotoPicker.REQUEST_CODE);
                break;
            case R.id.iv_voice:
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                String path = photos.get(0);
                uoloadFile(UrlGlobal.UPLOAD_PIC,path);
                ImageView iv = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = 20;
                iv.setLayoutParams(layoutParams);
                llContent.addView(iv,llContent.getChildCount()-1,layoutParams);
                GlideUtils.LoadImageWithLocation(this,path,iv);
            }
            return ;
        }else if(resultCode == RESULT_OK && requestCode == 1){
                if (captureManager == null) {
                    captureManager = new ImageCaptureManager(this);
                }
                captureManager.galleryAddPic();
                String path = captureManager.getCurrentPhotoPath();
                uoloadFile(UrlGlobal.UPLOAD_PIC,path);
                ImageView iv = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = 20;
                iv.setLayoutParams(layoutParams);
                llContent.addView(iv,llContent.getChildCount()-1,layoutParams);
                GlideUtils.LoadImageWithLocation(this,path,iv);
            }
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
