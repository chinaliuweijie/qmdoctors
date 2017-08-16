package com.qingmiao.qmdoctor.utils;

import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by hfx on 14-10-15.
 */
public class MediaPlayUtil {
    private static MediaPlayUtil mMediaPlayUtil;
    private MediaPlayer mMediaPlayer;


    public void setPlayOnCompleteListener(MediaPlayer.OnCompletionListener playOnCompleteListener){
        if(mMediaPlayer != null){
            mMediaPlayer.setOnCompletionListener(playOnCompleteListener);
        }
    }

    public static MediaPlayUtil getInstance(){
        if(mMediaPlayUtil == null){
            mMediaPlayUtil = new MediaPlayUtil();
        }
        return  mMediaPlayUtil;
    }

    private MediaPlayUtil(){
        mMediaPlayer = new MediaPlayer();
    }

    public static int getRingDuring(String mUri){
        String duration=null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();

        try {
            if (mUri != null) {
                HashMap<String, String> headers=null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }

            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        LogUtil.LogShitou("duration "+duration);
        try{
            double d = Integer.parseInt(duration)/1000d ;
            return (int) Math.round(d);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }



    public void play(String soundFilePath){
        if(mMediaPlayer == null){
            return;
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(soundFilePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pause(){
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
        }
    }

    public void stop(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
    }

    public int getCurrentPosition(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
            return mMediaPlayer.getCurrentPosition();
        }else{
            return 0;
        }
    }

    public int getDutation(){
        if(mMediaPlayer!= null && mMediaPlayer.isPlaying()){
            return mMediaPlayer.getDuration();
        }else{
            return 0;
        }
    }

    public boolean isPlaying(){
        if(mMediaPlayer != null){
            return mMediaPlayer.isPlaying();
        }else{
            return false;
        }
    }
}
