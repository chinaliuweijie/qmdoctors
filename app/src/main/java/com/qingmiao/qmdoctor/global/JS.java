package com.qingmiao.qmdoctor.global;
import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.qingmiao.qmdoctor.activity.WebCommentActivity;

//        import com.qingmiao.qmpatient.global.UrlGlobal;
//        import com.qingmiao.qmpatient.model.bean.WebShareBean;
//        import com.qingmiao.qmpatient.utils.GsonUtil;
//        import com.qingmiao.qmpatient.view.activity.WebCommentActivity;
//        import com.umeng.socialize.ShareAction;
//        import com.umeng.socialize.UMShareListener;
//        import com.umeng.socialize.bean.SHARE_MEDIA;
//        import com.umeng.socialize.media.UMImage;
//        import com.umeng.socialize.media.UMWeb;

/**
 * company : 青苗
 * Created by 刘伟杰 on 2017/3/2.
 */

public class JS {
    /*
       * 绑定的object对象
       * */
    private Activity context;
    private String id;

    public JS(Activity context, String id) {
        this.context = context;
        this.id = id;
    }

    /*
     * JS调用android的方法
     * */
    @JavascriptInterface
    public void postShare(String s1) {
//        WebShareBean webShareBean = GsonUtil.getInstance().fromJson(s1, WebShareBean.class);
//        UMImage image = new UMImage(context, webShareBean.thumb);//网络图片
//        image.setThumb(image);
//        UMWeb web = new UMWeb(UrlGlobal.SERVER_URL + webShareBean.url);
//        web.setTitle(webShareBean.title);//标题
//        web.setThumb(image);  //缩略图
//        web.setDescription(webShareBean.description);//描述
//        System.out.println(webShareBean.action);
//        if (webShareBean.action.equals("qq")) {
//            showShare(web, SHARE_MEDIA.QQ);
//        } else if (webShareBean.action.equals("wechat")) {
//            showShare(web, SHARE_MEDIA.WEIXIN);
//        } else if (webShareBean.action.equals("wechat_timeline")) {
//            showShare(web, SHARE_MEDIA.WEIXIN_CIRCLE);
//        } else if (webShareBean.action.equals("sina")) {
//            showShare(web, SHARE_MEDIA.SINA);
//        }
    }

//    private void showShare(UMWeb web, SHARE_MEDIA shareMedia) {
//        new ShareAction(context)
//                .setPlatform(shareMedia)
//                .withMedia(web)
//                .setCallback(new UMShareListener() {
//                    @Override
//                    public void onStart(SHARE_MEDIA share_media) {
//                    }
//
//                    @Override
//                    public void onResult(SHARE_MEDIA share_media) {
//                        Toast.makeText(context, "分享成功!", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                    @Override
//                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//                        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancel(SHARE_MEDIA share_media) {
//                        Toast.makeText(context, "分享取消!", Toast.LENGTH_SHORT).show();
//                    }
//                }).share();
//    }

    @JavascriptInterface
    public void commentList() {
        context.startActivity(new Intent(context, WebCommentActivity.class).putExtra("id", id));
    }
}
