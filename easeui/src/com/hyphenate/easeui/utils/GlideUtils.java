package com.hyphenate.easeui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.*;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.widget.GlideCircleTransform;

/**
 * Created by Administrator on 2017/5/2.
 */

public class GlideUtils {
    public static final String SERVER_URL = "https://api.green-bud.cn";
    /**
     * 加载网络图片
     * @param mContext
     * @param path
     * @param imageview
     */
    public static void LoadImage(Context mContext, String path,
                                 ImageView imageview) {
        if(TextUtils.isEmpty(path)){
            Glide.with(mContext).load(R.drawable.ic_load_deferent).into(imageview);
            return;
        }
        if(!path.contains("http")){
            Glide.with(mContext).load(SERVER_URL+path).error(R.drawable.ic_load_deferent).into(imageview);
        }else{
            Glide.with(mContext).load(path).error(R.drawable.ic_load_deferent)
                    .into(imageview);
        }
    }

    /**
     * 加载带尺寸的图片
     * @param mContext
     * @param path
     * @param Width
     * @param Height
     * @param imageview
     */
    public static void LoadImageWithSize(Context mContext, String path,
                                         int Width, int Height, ImageView imageview) {
        Glide.with(mContext).load(path).override(Width, Height)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageview);
    }

    /**
     * 加载本地图片
     * @param mContext
     * @param path
     * @param imageview
     */
    public static void LoadImageWithLocation(Context mContext, Integer path,
                                             ImageView imageview) {
        Glide.with(mContext).load(path).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageview);
    }




    /**
     * 圆形加载
     * @param mContext
     * @param path
     * @param imageview
     */
    public static void LoadCircleImage(final Context mContext, String path, ImageView imageview) {
            Glide.with(mContext).load(path).centerCrop()
                  //  .placeholder(R.drawable.user_photo)
                  //  .error(R.drawable.ic_user_avatar)
                    .transform(new GlideCircleTransform(mContext,2,mContext.getResources().getColor(R.color.alpha)))
                    .into(imageview);


    }

    public static void LoadCircleImage(Context mContext, int id,ImageView imageview) {
        Glide.with(mContext).load(id).centerCrop()
                //.placeholder(R.mipmap.user_photo)
                .transform(new GlideCircleTransform(mContext,2,mContext.getResources().getColor(R.color.alpha)))
                .into(imageview);
    }


    public static void LoadNullCircleImage(Context mContext,ImageView imageview) {
        Glide.with(mContext).load(R.drawable.ic_user_avatar).centerCrop()
                //.placeholder(R.mipmap.user_photo)
                .transform(new GlideCircleTransform(mContext,2,mContext.getResources().getColor(R.color.alpha)))
                .into(imageview);
    }

    public static void LoadNullCircleImage(Context mContext,int id,ImageView imageview) {
        Glide.with(mContext).load(id).centerCrop()
                //.placeholder(R.mipmap.user_photo)
                .transform(new GlideCircleTransform(mContext,2,mContext.getResources().getColor(R.color.alpha)))
                .into(imageview);
    }


    public static void LoadAvatarImageView(Context mContext, String path,ImageView imageview){
        if(TextUtils.isEmpty(path)){
            Glide.with(mContext).load(R.drawable.ic_user_avatar).error(R.drawable.ic_user_avatar).into(imageview);
            return;
        }
        if(!path.contains("http")){
            Glide.with(mContext).load(SERVER_URL+path).error(R.drawable.ic_user_avatar).into(imageview);
        }else{
            Glide.with(mContext).load(path).error(R.drawable.ic_user_avatar).into(imageview);
        }
    }


    public static void LoadCircleAvatarImage(Context mContext, String path,ImageView imageview){
        if(TextUtils.isEmpty(path)){
            GlideUtils.LoadNullCircleImage(mContext,imageview);
        }else{
            if(!path.contains("http")){
                LoadCircleImage(mContext,path,imageview);
            }else{
                LoadCircleImage(mContext,path,imageview);
            }
        }
    }










}