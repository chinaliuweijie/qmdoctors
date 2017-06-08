package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.LabelInfoAdapter;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.HXUserData;
import com.qingmiao.qmdoctor.bean.TagsListBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.qingmiao.qmdoctor.widget.SwipeMenuView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;


/**
 * 我的患者
 */
public class MyPatientActivity extends BaseActivity {

    @BindView(R.id.recycleview)
    LRecyclerView lRecyclerView;
    ListBaseAdapter<HXUserData> hxUserDataListBaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recycleview);
        initRecycleView();
        getDataForDB();
    }

    private void initRecycleView() {
        tvCenter.setText("我的患者");
        View view = findViewById(R.id.empty_view);
        lRecyclerView.setEmptyView(view);
        IconFontTextview fontTextview = (IconFontTextview) findViewById(R.id.image);
        fontTextview.setText(R.string.icons_addfriend);
        TextView tv = (TextView) findViewById(R.id.text);
        tv.setText("暂无关注");
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setColorResource(R.color.text)
                .build();
        lRecyclerView.addItemDecoration(divider);
        hxUserDataListBaseAdapter = new ListBaseAdapter<HXUserData>(this) {
            @Override
            public int getLayoutId() {
                return R.layout.patient_list_item;
            }

            @Override
            public void onBindItemHolder(SuperViewHolder holder, final int position) {
                HXUserData hxUserData = getDataList().get(position);
                TextView title = holder.getView(R.id.patient_list_item_title);
                if(!TextUtils.isEmpty(hxUserData.getRemark_names())){
                    title.setText(hxUserData.getRemark_names());
                }else if(!TextUtils.isEmpty(hxUserData.getUser_name())){
                    title.setText(hxUserData.getUser_name());
                }else if(!TextUtils.isEmpty(hxUserData.getNickname())){
                    title.setText(hxUserData.getNickname());
                }else if(!TextUtils.isEmpty(hxUserData.getMobile())){
                    title.setText(hxUserData.getMobile());
                }else{
                    title.setText(hxUserData.getHx_name());
                }
                ImageView iv = holder.getView(R.id.patient_list_item_iv);
                GlideUtils.LoadAvatarImageView(mContext,hxUserData.getAvatar(),iv);
                Button btnDelete = holder.getView(R.id.btnDelete);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 删除我的患者
                        final HXUserData hxUserData = hxUserDataListBaseAdapter.getDataList().get(position);
                        OkHttpUtils.post()
                                .url(UrlGlobal.DELETE_PATIENT)
                                .addParams("did", did)
                                .addParams("token", token)
                                .addParams("uid", hxUserData.getUid())
                                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        ToastUtils.showLongToast(MyPatientActivity.this, "网络异常");
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String msg = jsonObject.optString("msg");
                                            int code = jsonObject.optInt("code");
                                            if (code == 0) {
                                                DataSupport.deleteAll(HXUserData.class, "uid = ? and doctordid=?", hxUserData.getUid(),did);
                                                ToastUtils.showLongToast(MyPatientActivity.this, msg);


                                                hxUserDataListBaseAdapter.getDataList().remove(position);
                                                hxUserDataListBaseAdapter.notifyItemRemoved(position);

                                                if(position != (hxUserDataListBaseAdapter.getDataList().size())){ // 如果移除的是最后一个，忽略 注意：这里的mDataAdapter.getDataList()不需要-1，因为上面已经-1了
                                                    hxUserDataListBaseAdapter.notifyItemRangeChanged(position, hxUserDataListBaseAdapter.getDataList().size() - position);
                                                }
                                                //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                                                hxUserDataListBaseAdapter.notifyDataSetChanged();


                                                // 通知联系人列表界面刷新
                                                EventBus.getDefault().post("updata_patient");
                                                Map<String,String> map = new HashMap<String, String>();
                                                //　通知聊天列表界面删除
                                                map.put("delete_patient",hxUserData.getHx_name());
                                                EventBus.getDefault().post(map);
                                            } else {
                                                ToastUtils.showLongToast(MyPatientActivity.this, msg);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                    }
                });
                RelativeLayout rlContent = holder.getView(R.id.rl_content);
                rlContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HXUserData hxUserData = hxUserDataListBaseAdapter.getDataList().get(position);
                        Intent intent = new Intent(MyPatientActivity.this, PatientDataActivity.class);
                        intent.putExtra("uid", hxUserData.getUid());
                        intent.putExtra("isMarked",hxUserData.getIsMarked());
                        intent.putExtra("isFriend",hxUserData.getIsFriend());
                        startActivity(intent);
                    }
                });
            }
        };

        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(hxUserDataListBaseAdapter);
        lRecyclerView.setAdapter(mLRecyclerViewAdapter);
        lRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        lRecyclerView.setPullRefreshEnabled(false);
        mLRecyclerViewAdapter.removeFooterView();
    }


    private void getDataForDB() {
        List<HXUserData> hxUserDatas = DataSupport.where("isFriend = ? and doctordid=?", "1",did).find(HXUserData.class);
        hxUserDataListBaseAdapter.setDataList(hxUserDatas);
        if(hxUserDatas==null || hxUserDatas.size()==0){
            ToastUtils.showLongToast(this,"当前没有添加患者");
        }

    }


}
