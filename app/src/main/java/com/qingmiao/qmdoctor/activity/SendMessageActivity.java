package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.nanchen.compresshelper.CompressHelper;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.SendMessageBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.IconFontTextview;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

public class SendMessageActivity extends BaseActivity {
    @BindView(R.id.sendMessage_selector_iv)
    RelativeLayout sendMessageSelectorIv;
    @BindView(R.id.sendMessage_photo_iv)
    IconFontTextview sendMessagePhotoIv;
    @BindView(R.id.sendMessage_photo_content_ll)
    LinearLayout sendMessagePhotoContentLl;
    @BindView(R.id.sendMessage_photo_content_count)
    TextView sendMessagePhotoContentCount;
    @BindView(R.id.sendMessage_horizontal_photo)
    HorizontalScrollView sendMessageHorizontalPhoto;
    @BindView(R.id.sendMessage_title_ed)
    EditText sendMessageTitleEd;
    @BindView(R.id.sendMessage_content_ed)
    EditText sendMessageContentEd;
    private int count = 0;
    private ArrayList<File> mPhotos = new ArrayList<>();
    private String q_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
      //  ButterKnife.bind(this);
        Intent intent = getIntent();
        q_id = intent.getStringExtra("q_id");
        initView();
    }

    private void initView() {
        tvCenter.setText("肾友圈");
        ivRightBig.setVisibility(View.GONE);
        ivRightRed.setVisibility(View.GONE);
        ivLeft.setVisibility(View.VISIBLE);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("发布");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  refreshLayout.setRefreshing(true);
                String title = sendMessageTitleEd.getText().toString().trim();
                String content = sendMessageContentEd.getText().toString().trim();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    ToastUtils.showLongToast(SendMessageActivity.this, "标题或内容不能为空");
                    return;
                }
                showLoadingDialog(UrlGlobal.CATEGORY_URL,"上传数据中");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("did", did);
                hashMap.put("token", token);
                hashMap.put("q_id", q_id);
                hashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                hashMap.put("content", content);
                hashMap.put("title", title);
                hashMap.put("type", "2");
                post_file(UrlGlobal.CATEGORY_URL, hashMap, mPhotos);

            }
        });
     //   sendMessageTitleEd.setFilters(new InputFilter[]{emojiFilter});
     //   sendMessageContentEd.setFilters(new InputFilter[]{emojiFilter});
    }


    InputFilter emojiFilter = new InputFilter() {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                ToastUtils.showLongToast(SendMessageActivity.this,"不支持输入表情");
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                sendMessageHorizontalPhoto.setVisibility(View.VISIBLE);
                sendMessagePhotoContentLl.removeView(sendMessageSelectorIv);
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                for (String photo : photos) {
//                    PhotoUtils.compressPicture(photo, SendMessageActivity.this.getCacheDir() + "" + (++countPhoto) + ".png");
                    File file = new File(photo);
                    File newFile = CompressHelper.getDefault(this).compressToFile(file);
                    mPhotos.add(newFile);
                }
                count = count + photos.size();
                refreshAdpater(photos);
            }
        }
    }

    private void refreshAdpater(ArrayList<String> paths) {

        for (String path : paths) {
            final View view = View.inflate(this, R.layout.sendmessage_photo_item, null);
            ImageView photo = (ImageView) view.findViewById(R.id.sendMessage_photo_item_iv);

            Glide.with(this)
                    .load(path)
                    .into(photo);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    int i = sendMessagePhotoContentLl.indexOfChild(view);
                    mPhotos.remove(i);
                    sendMessagePhotoContentLl.removeView(view);
                    count--;
                    sendMessagePhotoContentCount.setText(count + "/9");
                }
            });
            sendMessagePhotoContentLl.addView(view);
        }
        sendMessagePhotoContentLl.addView(sendMessageSelectorIv);
        sendMessagePhotoContentCount.setText(count + "/9");

    }

    @OnClick({R.id.sendMessage_selector_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendMessage_selector_iv:
                if (count >= 9) {
                    ToastUtils.showLongToast(this, "超出可选照片数");
                    return;
                }
                PhotoPicker.builder()
                        .setPhotoCount(9-mPhotos.size())
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(this, PhotoPicker.REQUEST_CODE);
                break;
        }
    }

    protected void post_file(final String url, final Map<String, Object> map, ArrayList<File> files) {

        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (files != null && files.size() != 0) {
            for (File file : files) {
                requestBody.addFormDataPart("file[]", file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
            }
        }
        if (map != null) {
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url(url).post(requestBody.build()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                     //   refreshLayout.setRefreshing(false);
                        ToastUtils.showLongToast(SendMessageActivity.this, "网络异常");
                        dismissLoadDialog();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      //  refreshLayout.setRefreshing(false);
                        try {
                            dismissLoadDialog();
                            String string = response.body().string();
                            SendMessageBean sendMessageBean = GsonUtil.getInstance().fromJson(string, SendMessageBean.class);
                            if (sendMessageBean.code == 0) {
                                ToastUtils.showLongToast(SendMessageActivity.this, "发帖成功");
                                Intent intent = new Intent(SendMessageActivity.this, WebViewCircleActivity.class);
                                intent.putExtra("id", sendMessageBean.id);
                                intent.putExtra("url", sendMessageBean.url);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtils.showLongToast(SendMessageActivity.this, "发帖失败");
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
