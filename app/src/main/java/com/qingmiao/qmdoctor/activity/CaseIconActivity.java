package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.CaseIconAdapter;
import com.qingmiao.qmdoctor.bean.CheckItemDataBean;
import com.qingmiao.qmdoctor.bean.PatientSickListBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.ViewPagerIndicator;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import butterknife.BindView;
import okhttp3.Call;


public class CaseIconActivity extends BaseActivity {

    @BindView(R.id.dataIcon_title)
    ViewPagerIndicator dataIconTitle;
    @BindView(R.id.dataIcon_vp)
    ViewPager dataIconVp;
    private List<String> mNameList = new ArrayList<>();
    private List<View> mViewList = new ArrayList<>();
    private List<PatientSickListBean.SickBean> data;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_icon);
        initView();
        initData();
    }



    private void initData() {
        uid = this.getIntent().getStringExtra("uid");
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("did", did);
        linkedHashMap.put("token", token);
        linkedHashMap.put("uid",uid);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        startLoad(UrlGlobal.GET_SICKLIST,linkedHashMap);
    }


    @Override
    public void initData(String response) {
        super.initData(response);
        PatientSickListBean checkProjectBean = GsonUtil.getInstance().fromJson(response, PatientSickListBean.class);
        data = checkProjectBean.data;
        if (checkProjectBean.code == 0 && data != null) {
            mViewList.clear();
            for (PatientSickListBean.SickBean dataBean : data) {
                mNameList.add(dataBean.sick_name);
                View view = View.inflate(CaseIconActivity.this, R.layout.data_icon_layout, null);
                mViewList.add(view);
            }
            //设置viewpager联动
            dataIconTitle.setVisibleTabCount(4);
            dataIconTitle.setTabItemTitles(mNameList);
            dataIconVp.setAdapter(new Madapter());
            dataIconTitle.setViewPager(dataIconVp, 0);
        }else{
            ToastUtils.showLongToast(CaseIconActivity.this,"没有数据");
        }
    }

    private void initData1(final ListView listView, String table_name, String sick_id, final LinearLayout linearLayout) {

        OkHttpUtils.post()
                .url(UrlGlobal.GET_CHECK_ITEM_DATA)
                .addParams("did", did)
                .addParams("token", token)
                 .addParams("uid",uid)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .addParams("table_name", table_name)
                .addParams("sick_id", sick_id)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(CaseIconActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        CheckItemDataBean caseIconBean = GsonUtil.getInstance().fromJson(response, CheckItemDataBean.class);
                        final List<CheckItemDataBean.CheckItemBean> data = caseIconBean.data;
                        if (caseIconBean.code == 0 && data != null && data.size() > 0) {

                            listView.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.GONE);

                            listView.setAdapter(new CaseIconAdapter(CaseIconActivity.this, data));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(CaseIconActivity.this, BigCaseIconActivity.class);
                                    Bundle bundle = new Bundle();
                                    ArrayList<String> dataCase = (ArrayList<String>) data.get(i).data;
                                    ArrayList<String> time = (ArrayList<String>) data.get(i).time;
                                    bundle.putString("demo", data.get(i).demo);
                                    bundle.putStringArrayList("data", dataCase);
                                    bundle.putStringArrayList("time", time);
                                    bundle.putString("unit", data.get(i).unit);
                                    bundle.putString("title", data.get(i).field_cn);
                                    intent.putExtra("bundle", bundle);
                                    startActivity(intent);
                                }
                            });
                        }else {
                            listView.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    //初始化控件
    private void initView() {
        tvCenter.setText("数据图表");
        ivLeft.setVisibility(View.VISIBLE);
        ivRightBig.setVisibility(View.VISIBLE);
        ivRightBig.setText(R.string.icons_more);
        ivRightBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    class Madapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mNameList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mViewList.get(position);
            ListView listView = (ListView) view.findViewById(R.id.dataicon_list);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.bg_ll);
            String table_name = data.get(position).table_name;
            String sick_id = data.get(position).sick_id;
            initData1(listView, table_name, sick_id, linearLayout);
            container.addView(view);
            return view;
        }
    }

}
