package com.qingmiao.qmdoctor.widget;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

import com.qingmiao.qmdoctor.R;

/**
 * 加载提醒对话框
 */
public class LoadingDialog extends ProgressDialog
{
    private String strLoad;

    public LoadingDialog(Context context)
    {
        this(context,R.style.alert_dialog);
    }

    public LoadingDialog(Context context, int theme)
    {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context)
    {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_load_progress);
        TextView tvLoad = (TextView) findViewById(R.id.tv_load_dialog);
        if(!TextUtils.isEmpty(strLoad)){
            tvLoad.setText(strLoad);
        }
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }


    public void setLoadText(String loadText){
        this.strLoad = loadText;
    }


    @Override
    public void show()
    {
        super.show();
    }
}