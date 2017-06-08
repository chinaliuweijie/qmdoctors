package com.qingmiao.qmdoctor.global;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.qingmiao.qmdoctor.activity.WebCommentActivity;
import com.qingmiao.qmdoctor.activity.WebViewCircleActivity;
import com.qingmiao.qmdoctor.bean.WebShareBean;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * company : 青苗
 * Created by 杜新 on 2017/3/2.
 */

public class JSObject {
    /*
       * 绑定的object对象
       * */
    private Context context;
   // public ClickJs clickJs;
    private String id;


    public JSObject(Context context) {
        this.context = context;
    }

//    public void setClickJs(ClickJs clickJs) {
//        this.clickJs = clickJs;
//    }

    public void setId(String id) {
        this.id = id;
    }

    /*
             * JS调用android的方法
             * @JavascriptInterface仍然必不可少
             *
             * */
    @JavascriptInterface
    public void postShare(String s1) {

        // 分享的点击
//        if(clickJs!=null){
//            clickJs.onClickShare(s1);
//        }
        WebShareBean webShareBean = GsonUtil.getInstance().fromJson(s1, WebShareBean.class);
        UMImage image = new UMImage(context, webShareBean.thumb);//网络图片
        image.setThumb(image);
        UMWeb web = new UMWeb(UrlGlobal.SERVER_URL + webShareBean.url);
        web.setTitle(webShareBean.title);//标题
        web.setThumb(image);  //缩略图
        web.setDescription(webShareBean.description);//描述
        System.out.println(webShareBean.action);
        if (webShareBean.action.equals("qq")) {
            showShare(web, SHARE_MEDIA.QQ);
        } else if (webShareBean.action.equals("wechat")) {
            showShare(web, SHARE_MEDIA.WEIXIN);
        } else if (webShareBean.action.equals("wechat_timeline")) {
            showShare(web, SHARE_MEDIA.WEIXIN_CIRCLE);
        } else if (webShareBean.action.equals("sina")) {
            showShare(web, SHARE_MEDIA.SINA);
        }

    }

        private void showShare(UMWeb web, SHARE_MEDIA shareMedia) {
        new ShareAction((Activity) context)
                .setPlatform(shareMedia)
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        ToastUtils.showLongToast(context, "分享成功!");
                    }


                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        ToastUtils.showLongToast(context, throwable.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        ToastUtils.showLongToast(context, "分享取消!");
                    }
                }).share();
    }



    @JavascriptInterface
    public void commentList() {
        Intent intent = new Intent(context, WebCommentActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

//    public interface ClickJs{
//        public void onClickCommentList();
//        public void onClickShare(String str);
//
//    }


}
