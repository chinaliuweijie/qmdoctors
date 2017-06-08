package com.qingmiao.qmdoctor.fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.activity.LabelListActivity;
import com.qingmiao.qmdoctor.activity.LoginActivity;
import com.qingmiao.qmdoctor.activity.RenalCirclePagerActivity;
import com.qingmiao.qmdoctor.activity.WebViewZActivity;
import com.qingmiao.qmdoctor.adapter.CommentAdapter;
import com.qingmiao.qmdoctor.adapter.RenalCircleListAdapter;
import com.qingmiao.qmdoctor.adapter.RenalCirclrAdapter;
import com.qingmiao.qmdoctor.bean.CircleCateBean;
import com.qingmiao.qmdoctor.bean.CommunityListDean;
import com.qingmiao.qmdoctor.global.MyApplication;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.ACache;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.RefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by: jpj
 * Created time: 17/3/14
 * Description:文献Fragment
 */
public class DocuMentFragment extends BaseFragment {

	@BindView(R.id.RenalCircle_Title_left)
	TextView RenalCircleTitleLeft;
	@BindView(R.id.RenalCircle_Title_right)
	TextView RenalCircleTitleRight;
	@BindView(R.id.health_listView)
	RefreshListView renalCircleListView;
	@BindView(R.id.renalCircle_ll)
	ScrollView renalCircleLl;
	@BindView(R.id.circlr_listView_tuijian)
	ListView circlrTuijianListView;
	@BindView(R.id.circlr_listView_all)
	ListView circlrAllListView;
	@BindView(R.id.renal_srl)
	SwipeRefreshLayout registerSrl;
	private List<CommunityListDean.DataBean> commentData = new ArrayList<>();
	private CommentAdapter commentAdapter;
	private boolean isRefresh;
	private RenalCirclrAdapter renalCirclrAdapter;
	private int page = 1,pagecount =1;
	public List<CircleCateBean.DataBean> all_cate;
	public List<CircleCateBean.DataBean> tj_cate;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);//在当前界面注册一个订阅者
	}

	@Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
	public void onDataSynEvent(String event) {
		if("changeData".equals(event)) {
			isRefresh = true;
			page = 1;
			getDataFormService1();
		}
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = View.inflate(mActivity, R.layout.activity_renal_circle, null);
		ButterKnife.bind(this, view);
		registerSrl.setColorSchemeResources(R.color.green);
		registerSrl.setRefreshing(true);
		registerSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				isRefresh = true;
				page = 1;
				getDataFormService1();
			}
		});
		return view;
	}
	//初始化view
	@Override
	public void initView() {
		renalCircleListView.setVisibility(View.VISIBLE);
		renalCircleListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
			@Override
			public void onLoadingMore() {
				page++ ;
				if(page<= pagecount){
					getDataFormService1();
				}else{
					ToastUtils.showLongToast(getContext(),"滑到底了");
					registerSrl.setRefreshing(false);
					renalCircleListView.setRefreshed();
					isRefresh = false;
				}

			}
		});
		commentAdapter = new CommentAdapter(mActivity, commentData);
		renalCircleListView.setAdapter(commentAdapter);

		renalCircleLl.setVisibility(View.INVISIBLE);
		//从服务器获取数据
		String communityListDean = MyApplication.aCache.getAsString("CommunityListDean");
		if (TextUtils.isEmpty(communityListDean)) {
			getDataFormService1();
		} else {
			parseJson1(communityListDean);
		}
		String circleCateBean = MyApplication.aCache.getAsString("CircleCateBean");
		if (TextUtils.isEmpty(circleCateBean)) {
			getDataFormService();
		} else {
			parseJson2(circleCateBean);
		}
		renalCircleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				String did = PrefUtils.getString(getActivity(),"did","");
				String token = PrefUtils.getString(getActivity(),"token","");
				if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);
				}else{
					Intent intent = new Intent(mActivity, WebViewZActivity.class);
					intent.putExtra("url", commentData.get(i).url);
					intent.putExtra("id", commentData.get(i).id);
					startActivity(intent);
				}
			}
		});
		RenalCircleTitleLeft.setTextColor(Color.parseColor("#333333"));
		RenalCircleTitleRight.setTextColor(Color.parseColor("#999999"));

	}


	@Override
	public void initData() {

	}

	//圈子分类列表
	private void getDataFormService() {
		OkHttpUtils
				.post()
				.url(UrlGlobal.CLASSIFY_URL)
				.addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showLongToast(mActivity, "请求网络失败");
					}

					@Override
					public void onResponse(String response, int id) {
						parseJson2(response);

					}
				});

	}

	private void parseJson2(String response) {
		try{
			Gson gson = GsonUtil.getInstance();
			CircleCateBean circleCateBean = gson.fromJson(response, CircleCateBean.class);
			if(circleCateBean.code == 0){
				all_cate = circleCateBean.data.all_cate;
				tj_cate = circleCateBean.data.tj_cate;
				MyApplication.aCache.put("CircleCateBean", response, ACache.TIME_DAY);
				circlrAllListView.setAdapter(new RenalCircleListAdapter(mActivity,all_cate));
				circlrAllListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
						String did = PrefUtils.getString(getActivity(),"did","");
						String token = PrefUtils.getString(getActivity(),"token","");
						if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
							Intent intent = new Intent(getActivity(), LoginActivity.class);
							startActivity(intent);
						}else{
							Intent intent = new Intent(mActivity, RenalCirclePagerActivity.class);
							intent.putExtra("q_id", all_cate.get(i).q_id);
							startActivity(intent);
						}
					}
				});
				circlrTuijianListView.setAdapter(new RenalCircleListAdapter(mActivity, tj_cate));
				circlrTuijianListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
						String did = PrefUtils.getString(getActivity(),"did","");
						String token = PrefUtils.getString(getActivity(),"token","");
						if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
							Intent intent = new Intent(getActivity(), LoginActivity.class);
							startActivity(intent);
						}else{
							Intent intent = new Intent(mActivity, RenalCirclePagerActivity.class);
							intent.putExtra("q_id", tj_cate.get(i).q_id);
							startActivity(intent);
						}

					}
				});
			}else{
				ToastUtils.showLongToast(this.getContext(),circleCateBean.msg);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	//健康知识分类列表
	private void getDataFormService1() {
		OkHttpUtils
				.post()
				.url(UrlGlobal.PHOTOS_URL)
				.addParams("c_id", "4")
				.addParams("page", ""+page)
				.addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))

				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						registerSrl.setRefreshing(false);
						ToastUtils.showLongToast(mActivity, "请求网络失败");
					}

					@Override
					public void onResponse(String response, int id) {
						parseJson1(response);
                        if(page ==1) {
                            MyApplication.aCache.put("CommunityListDean", response, ACache.TIME_DAY);
                        }
					}
				});
	}

	private void parseJson1(String response) {
		Gson gson = GsonUtil.getInstance();
		CommunityListDean communityListDean = gson.fromJson(response, CommunityListDean.class);
		if(communityListDean.code == 0) {
			page = Integer.parseInt(communityListDean.page);
			pagecount = communityListDean.pagecount;
			if (page <= pagecount) {
				if(page!=1 ){
					commentData.addAll(communityListDean.data);
					commentAdapter.notifyDataSetChanged();
				}else {
					commentData.clear();
					commentData.addAll(communityListDean.data);
					commentAdapter.notifyDataSetChanged();
				}
			} else if (!isRefresh) {
				ToastUtils.showLongToast(mActivity, "到底啦!!");
			}
		}
		registerSrl.setRefreshing(false);
		renalCircleListView.setRefreshed();
		isRefresh = false;
	}

	//点击事件
	@OnClick({R.id.RenalCircle_Title_left, R.id.RenalCircle_Title_right})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.RenalCircle_Title_left:
				RenalCircleTitleLeft.setTextColor(Color.parseColor("#333333"));
				RenalCircleTitleRight.setTextColor(Color.parseColor("#999999"));
				renalCircleLl.setVisibility(View.INVISIBLE);
				renalCircleListView.setVisibility(View.VISIBLE);
			//	registerSrl.setEnabled(true);
				break;

			case R.id.RenalCircle_Title_right:
				RenalCircleTitleLeft.setTextColor(Color.parseColor("#999999"));
				RenalCircleTitleRight.setTextColor(Color.parseColor("#333333"));
				renalCircleListView.setVisibility(View.INVISIBLE);
				renalCircleLl.setVisibility(View.VISIBLE);
			//	registerSrl.setEnabled(false);
				getDataFormService();
				break;
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	//	registerSrl.setRefreshing(false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);//取消注册
	}

}
