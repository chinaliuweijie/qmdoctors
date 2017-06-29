package com.qingmiao.qmdoctor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.activity.DoctorDataActivity;
import com.qingmiao.qmdoctor.activity.LoginActivity;
import com.qingmiao.qmdoctor.activity.MainActivity;
import com.qingmiao.qmdoctor.activity.MyCollectDocumentActivity;
import com.qingmiao.qmdoctor.activity.MyPatientActivity;
import com.qingmiao.qmdoctor.activity.SettingActivity;
import com.qingmiao.qmdoctor.bean.DoctorDataBean;
import com.qingmiao.qmdoctor.bean.HXUserData;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.PullScrollView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * company : 青苗
 * Created by on 2017/2/23.
 */
public class MeFragment extends BaseFragment implements View.OnClickListener{

	@BindView(R.id.refresh_layout)
	PullScrollView refreshLayout;
	@BindView(R.id.me_icon)
	ImageView meIcon;
	@BindView(R.id.tv_login)
	TextView tvLogin;
	private String token;
	private String did;
	private MainActivity activity;
	private boolean isInitData = false;
	private DoctorDataBean doctorDataBean;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (MainActivity) getActivity();
		isInitData = false;
		EventBus.getDefault().register(this);//在当前界面注册一个订阅者
	}




	@Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
	public void onDataSynEvent(String event) {
		if("changeData".equals(event)) {
			isInitData = false;
			did = PrefUtils.getString(getActivity(),"did","");
			token = PrefUtils.getString(getActivity(),"token","");
			refreshLayout.refreshWithPull();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);//取消注册
	}

	@Override
	public void onStart() {
		super.onStart();
		did = PrefUtils.getString(getActivity(),"did","");
		token = PrefUtils.getString(getActivity(),"token","");
		if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)){
			tvLogin.setText("未登录");
			GlideUtils.LoadNullCircleImage(getActivity(),R.drawable.ic_doctor_avatar,meIcon);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
		} else {
			startCount ++ ;
			if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)){
				tvLogin.setText("未登录");
				GlideUtils.LoadNullCircleImage(getActivity(),R.drawable.ic_doctor_avatar,meIcon);
				if(startCount ==1){
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);
				}
			}else{
				if(!isInitData) {
					refreshLayout.refreshWithPull();
				}
			}
		}
	}


	private void getServiceData() {
		LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
		linkedHashMap.put("did",did);
		linkedHashMap.put("token",token);
		linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
		OkHttpUtils.post()
				.url(UrlGlobal.GET_DOCTOR_SELF_INFO)
				.params(linkedHashMap)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showLongToast(getContext(),"无法连接到网络，请检查网络设置");
						tvLogin.setText("无法连接到网络，请检查网络设置");
						refreshLayout.setRefreshCompleted();
					}

					@Override
					public void onResponse(String response, int id) {
						refreshLayout.setRefreshCompleted();
						DoctorDataBean doctorDataBean = GsonUtil.getInstance().fromJson(response,DoctorDataBean.class);
						MeFragment.this.doctorDataBean = doctorDataBean;
						if(doctorDataBean.code == 0){
							setViewData(doctorDataBean);
							isInitData = true;
						}else if(doctorDataBean.code == 402){
							// 资质正在审核
							ToastUtils.showLongToast(getActivity(),doctorDataBean.msg);
							setViewData(doctorDataBean);
						}else if(doctorDataBean.code == 401){
							// 登录过期  删除did  token
							PrefUtils.putBean(getActivity(),KeyOrValueGlobal.LOGINBEAN,null);
							PrefUtils.putString(getActivity(),"did","");
							PrefUtils.putString(getActivity(),"token","");
							did = "" ;
							token = "";
							ToastUtils.showLongToast(getActivity(),doctorDataBean.msg);
							setViewData(doctorDataBean);
						}else{
							PrefUtils.putBean(getActivity(),KeyOrValueGlobal.LOGINBEAN,null);
							PrefUtils.putString(getActivity(),"did","");
							PrefUtils.putString(getActivity(),"token","");
							did = "" ;
							token = "";
							ToastUtils.showLongToast(getActivity(),doctorDataBean.msg);
							setViewData(doctorDataBean);
						}
					}
				});

//		LinkedHashMap<String,String> linkedHashMap1 = new LinkedHashMap<>();
//		linkedHashMap1.put("uid",81+"");
//		linkedHashMap1.put("token","4db3852e394704a49ae8b8beb3e34e7be3b9cf67");
//		linkedHashMap1.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
//		linkedHashMap1.put("sick_id",27+"");
//		linkedHashMap1.put("check_item","降钙素原(PCT):5:red");
//		linkedHashMap1.put("bl_id","452");
//		linkedHashMap1.put("field_pct_pct","5");
//		linkedHashMap1.put("time","1480000000");
//		OkHttpUtils.post()
//				.url(UrlGlobal.text)
//				.params(linkedHashMap1)
//				.build()
//				.execute(new StringCallback() {
//					@Override
//					public void onError(Call call, Exception e, int id) {
//						LogUtil.LogShitou(e.getMessage());
//					}
//
//					@Override
//					public void onResponse(String response, int id) {
//						LogUtil.LogShitou(response);
//					}
//				});
	}

	private void setViewData(DoctorDataBean doctorDataBean) {
		if(doctorDataBean!=null && doctorDataBean.data !=null && doctorDataBean.code == 0){
			if(!TextUtils.isEmpty(doctorDataBean.data.d_name)){
				tvLogin.setText(doctorDataBean.data.d_name);
			}else if(!TextUtils.isEmpty(doctorDataBean.data.nickname)){
				tvLogin.setText(doctorDataBean.data.nickname);
			}else {
				tvLogin.setText(doctorDataBean.data.mobile);
			}
			if(!TextUtils.isEmpty(doctorDataBean.data.avatar)){
				GlideUtils.LoadCircleImage(getActivity(), doctorDataBean.data.avatar,meIcon);
			}else{
				GlideUtils.LoadNullCircleImage(getActivity(),R.drawable.ic_doctor_avatar,meIcon);
			}
			// 保存自己的用户信息到数据库
			List<HXUserData> mData = DataSupport.where("did = ?", did).find(HXUserData.class);
			HXUserData hxUserData = new HXUserData();
			hxUserData.setUser_name(doctorDataBean.data.d_name);
			hxUserData.setNickname(doctorDataBean.data.nickname);
			hxUserData.setDid(doctorDataBean.data.did);
			hxUserData.setHx_name(doctorDataBean.data.hx_uname);
			hxUserData.setAvatar(doctorDataBean.data.avatar);
			hxUserData.setMobile(doctorDataBean.data.mobile);
			if(mData.size() == 0){
				hxUserData.save();
			}else{
				hxUserData.updateAll("did = ?", did);
			}
			// 判断状态
			if("0".equals(doctorDataBean.data.status)){
				ToastUtils.showLongToast(getContext(),"请上传资质证明");
			}else if("1".equals(doctorDataBean.data.status)){
				startDialog();
			}else if("2".equals(doctorDataBean.data.status)){

			}else if("3".equals(doctorDataBean.data.status)){
				startDialog();
			}
		}else{
			if(doctorDataBean!=null){
				tvLogin.setText(doctorDataBean.msg);
			}else{
				tvLogin.setText("未登录");
			}
			GlideUtils.LoadNullCircleImage(getActivity(),R.drawable.ic_doctor_avatar,meIcon);
		}
	}


	private void startDialog() {
		final android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(getContext(), R.style.dialog).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		Window win = dialog.getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		WindowManager m = getActivity().getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		lp.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.5
		lp.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.65
		win.setAttributes(lp);
		if("1".equals(doctorDataBean.data.status)){
			View view = View.inflate(getContext(), R.layout.dialog_audit_hint, null);
			win.setContentView(view);
			view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}else if("3".equals(doctorDataBean.data.status)){
			View view = View.inflate(getContext(), R.layout.dialog_audit_fail, null);
			win.setContentView(view);
			view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = View.inflate(activity, R.layout.fragment_me, null);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void initView() {
		//设置头部加载颜色
		//refreshLayout.setHeaderViewColor(R.color.colorAccent, R.color.colorAccent ,android.R.color.white);
		refreshLayout.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
		refreshLayout.setRefreshListener(new PullScrollView.RefreshListener() {
			@Override
			public void onRefresh() {
				getServiceData();
			}
		});
	}

	@Override
	public void initData() {

	}

	@OnClick({R.id.medata_rl, R.id.rl_me_patient, R.id.rl_me_collect, R.id.rl_setting,R.id.tv_login,R.id.rl_login})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.medata_rl:
				if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
					ToastUtils.showLongToast(activity,"还未登录，请先登录");
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);
				}else{
					if(doctorDataBean!=null){
						if(doctorDataBean.code == 0){
							Intent intent = new Intent(activity, DoctorDataActivity.class);
							startActivity(intent);
						}else{
							ToastUtils.showLongToast(activity,doctorDataBean.msg);
						}
					}else{
						ToastUtils.showLongToast(activity,"未连接网络");
					}
				}
				break;
			case R.id.rl_me_patient:
				if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
					ToastUtils.showLongToast(activity,"还未登录，请先登录");
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);
				}else{
					if(doctorDataBean!=null){
						if(doctorDataBean.code == 0){
							startActivity(new Intent(activity, MyPatientActivity.class));
						}else{
							ToastUtils.showLongToast(activity,doctorDataBean.msg);
						}
					}else{
						ToastUtils.showLongToast(activity,"未连接网络");
					}
				}
				break;
			case R.id.rl_me_collect:
				if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
					ToastUtils.showLongToast(activity,"还未登录，请先登录");
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);
				}else{
					if(doctorDataBean!=null){
						if(doctorDataBean.code == 0){
							startActivity(new Intent(activity, MyCollectDocumentActivity.class));
						}else{
							ToastUtils.showLongToast(activity,doctorDataBean.msg);
						}
					}else{
						ToastUtils.showLongToast(activity,"未连接网络");
					}
				}
				break;
			case R.id.rl_setting:
				startActivity(new Intent(activity, SettingActivity.class));
				break;
			case R.id.rl_login :
				if (TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
					startActivity(new Intent(activity,LoginActivity.class));
				}
				break;
		}
	}
}
