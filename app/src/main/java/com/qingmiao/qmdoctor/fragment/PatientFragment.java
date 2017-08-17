package com.qingmiao.qmdoctor.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.activity.AddPatientActivity;
import com.qingmiao.qmdoctor.activity.LabelListActivity;
import com.qingmiao.qmdoctor.activity.LoginActivity;
import com.qingmiao.qmdoctor.activity.MainActivity;
import com.qingmiao.qmdoctor.activity.NewPatientListActivity;
import com.qingmiao.qmdoctor.activity.PatientDataActivity;
import com.qingmiao.qmdoctor.adapter.ContactAdapter;
import com.qingmiao.qmdoctor.bean.HXUserData;
import com.qingmiao.qmdoctor.bean.PatientFriendListBean;
import com.qingmiao.qmdoctor.bean.UserFriendBean;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.CharacterParser;
import com.qingmiao.qmdoctor.widget.ClearEditText;
import com.qingmiao.qmdoctor.widget.PinyinComparator;
import com.qingmiao.qmdoctor.bean.ContactModel;
import com.qingmiao.qmdoctor.widget.SearchView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by: jpj
 * Created time: 17/3/14
 * Description:消息Fragment
 */
public class PatientFragment extends BaseFragment {

	private String did;
	private String token;
	ContactRecyclerFragment contactRecyclerFragment;
	SearchView filterEdit;
	LinearLayout llAddPatient,llLabel;
	boolean isInit = false;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isInit = false;
		EventBus.getDefault().register(this);//在当前界面注册一个订阅者
	}




	@Override
	public void initView() {
		filterEdit.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				try{
					filterData(s.toString());
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.i("",s.toString());
			}
		});
		filterEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					MainActivity mainActivity = (MainActivity) getActivity();
					mainActivity.hideSoftKeyboard();
					return true;
				}
				return false;
			}
		});


		contactRecyclerFragment.setOnItemClickListener(new ContactRecyclerFragment.OnContactItemClickListener() {
			@Override
			public void onItemClick(View view, int position, ContactModel model) {
				if(model!=null){
					if(model.friend!=null){
						String uid = model.friend.uid;
						Intent intent = new Intent(getActivity(), PatientDataActivity.class);
						intent.putExtra("uid",uid);
						if(model.friendLibeType == 1){
							intent.putExtra("isMarked","1");
						}else if(model.friendLibeType == 2){
							intent.putExtra("isMarked","2");
						}
						intent.putExtra("isFriend","1");
						startActivity(intent);
					}
				}
			}
		});
	}

	@Override
	public void initData() {
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// 加载布局
		View gView = View.inflate(mActivity, R.layout.fragment_home, null);
		contactRecyclerFragment = new ContactRecyclerFragment();
		View childview = View.inflate(this.getContext(),R.layout.head_listview,null);
		contactRecyclerFragment.addHeaderView(childview);
		contactRecyclerFragment.setRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				initHttp();
				if(filterEdit!=null){
					filterEdit.getText().clear();
				}
			}
		});
		contactRecyclerFragment.setOpenSwipeButton(false);
		contactRecyclerFragment.setShowSideBar(true);
		FragmentManager fm =  getChildFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		//显示点中的Fragment对象
		transaction.replace(R.id.frameLayout, contactRecyclerFragment);
		transaction.commit();
		llAddPatient = (LinearLayout) childview.findViewById(R.id.ll_add_patient);
		llLabel = (LinearLayout) childview.findViewById(R.id.ll_label);
		filterEdit = (SearchView) childview.findViewById(R.id.filter_edit);
		llAddPatient.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);
				}else{
					Intent intent = new Intent(getActivity(), AddPatientActivity.class);
					startActivity(intent);
				}
			}
		});
		llLabel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(intent);
				}else{
					Intent intent = new Intent(getActivity(), LabelListActivity.class);
					startActivity(intent);
				}
			}
		});
		LinearLayout llTag = (LinearLayout) childview.findViewById(R.id.ll_tag);
		llTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),NewPatientListActivity.class);
				startActivity(intent);
			}
		});

		filterEdit.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
					case MotionEvent.ACTION_DOWN:
						setFilterEditFocus(true);
					break;
					case MotionEvent.ACTION_MOVE:
						break;
					case MotionEvent.ACTION_UP:
						break;
					default:
						break;
				}
				return false;
			}
		});
		ButterKnife.bind(this, gView);
		return gView;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);//取消注册
	}

	public void setFilterEditFocus(boolean isFocus){
		if(isFocus){
			filterEdit.setFocusable(true);
			filterEdit.setFocusableInTouchMode(true);
			filterEdit.requestFocus();
		}else{
			filterEdit.setFocusable(false);
			filterEdit.setFocusableInTouchMode(false);
			filterEdit.clearFocus();
		}
	}




	@Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
	public void onDataSynEvent(String event) {
		LogUtil.LogShitou(event);
		if("updata_patient".equals(event)){
			isInit = false;
			if(contactRecyclerFragment.getRecycleView()!=null){
				contactRecyclerFragment.getRecycleView().forceToRefresh();
			}
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
		} else {
			if(!isInit) {
				if(contactRecyclerFragment.getRecycleView()!=null){
					contactRecyclerFragment.getRecycleView().forceToRefresh();
				}
			}
			startCount ++ ;
			if(startCount == 1 && (TextUtils.isEmpty(did)|| TextUtils.isEmpty(token))){
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
			}
			setFilterEditFocus(false);
			if(startCount >0) {
				contactRecyclerFragment.getRecycleView().setOnScrollListener(new RecyclerView.OnScrollListener() {
					@Override
					public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
						super.onScrollStateChanged(recyclerView, newState);
						if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
							setFilterEditFocus(false);
						}
					}

					@Override
					public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
						super.onScrolled(recyclerView, dx, dy);
					}
				});
			}
		}
	}


	private void setRefreshComplete(){
		if(contactRecyclerFragment!=null){
			contactRecyclerFragment.setRefreshComplete();
		}
	}

	public void initHttp() {
		did = PrefUtils.getString(mActivity, "did", "");
		token = PrefUtils.getString(mActivity, "token", "");
		OkHttpUtils.post()
				.url(UrlGlobal.GETUSERFRIEND)
				.addParams("did", did)
				.addParams("token", token)
				.addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						ToastUtils.showLongToast(mActivity, "请求网络失败");
						System.out.println("response====================+++++++++++++++++++++");
						setRefreshComplete();
					}

					@Override
					public void onResponse(String response, int id) {
						PatientFriendListBean patientFriendListBean = GsonUtil.getInstance().fromJson(response, PatientFriendListBean.class);
						if(patientFriendListBean.code == 0){
                            List<ContactModel> filterDateList = filledData(patientFriendListBean.data);
							contactRecyclerFragment.getAdapter().updateListView(filterDateList);
							contactRecyclerFragment.getAdapter().setmAllDatas(filterDateList);
							//保存好友到数据库中
							for (int i = 0; i < filterDateList.size(); i++) {
								ContactModel contactModel = filterDateList.get(i);
								UserFriendBean friend = contactModel.friend;
								if(contactModel.friendLibeType == 1){
									// 标新好友
									List<HXUserData>  hxUserDatas = DataSupport.where("uid = ? and doctordid=?", friend.uid,did).find(HXUserData.class);
									HXUserData hxUserData = new HXUserData();
									hxUserData.setAvatar(friend.avatar);
									hxUserData.setHx_name(friend.hx_uname);
									hxUserData.setNickname(friend.nickname);
									hxUserData.setUid(friend.uid);
									hxUserData.setDoctordid(did);
									hxUserData.setIsFriend("1");
									hxUserData.setIsMarked("1");
									hxUserData.setMobile(friend.mobile);
									hxUserData.setRemark_names(friend.remark_names);
									hxUserData.setUser_name(friend.user_name);

									if(hxUserDatas!=null && hxUserDatas.size()>0){
										hxUserData.updateAll("uid = ? and doctordid = ?", friend.uid,did);
									}else{
										hxUserData.save();
									}
								}else if(contactModel.friendLibeType == 2){
									//  普通好友
									List<HXUserData>  hxUserDatas = DataSupport.where("uid = ? and doctordid=?", friend.uid,did).find(HXUserData.class);
									HXUserData hxUserData = new HXUserData();
									hxUserData.setAvatar(friend.avatar);
									hxUserData.setDoctordid(did);
									hxUserData.setHx_name(friend.hx_uname);
									hxUserData.setNickname(friend.nickname);
									hxUserData.setUid(friend.uid);
									hxUserData.setIsFriend("1");
									hxUserData.setIsMarked("2");
									hxUserData.setMobile(friend.mobile);
									hxUserData.setRemark_names(friend.remark_names);
									hxUserData.setUser_name(friend.user_name);
									if(hxUserDatas!=null && hxUserDatas.size()>0){
										hxUserData.updateAll("uid = ? and doctordid = ?", friend.uid,did);
									}else{
										hxUserData.save();
									}
								}
							}
							if(filterDateList.size()>0){
								isInit = true;
							}else{
								//查询数据库
								//initHttp();
							}
						}else if(patientFriendListBean.code == 408){
							// 请上传你的资质证明
							ToastUtils.showLongToast(mActivity,patientFriendListBean.msg);
							// 跳转到个人资料界面
						//	startActivity(new Intent(mActivity, DoctorDataActivity.class));
							List<ContactModel> emptList = new ArrayList<ContactModel>();
							contactRecyclerFragment.getAdapter().updateListView(emptList);
							contactRecyclerFragment.getAdapter().setmAllDatas(emptList);
						}
						else{
							List<ContactModel> emptList = new ArrayList<ContactModel>();
							contactRecyclerFragment.getAdapter().updateListView(emptList);
							contactRecyclerFragment.getAdapter().setmAllDatas(emptList);
						//	ToastUtils.showLongToast(mActivity, patientFriendListBean.msg);
						}
						setRefreshComplete();
					}
		});
	}


	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<ContactModel> filledData(PatientFriendListBean.FriendData date){
		CharacterParser characterParser = new CharacterParser();
		List<ContactModel> contactModels = new ArrayList<>();
		if(date.marked_u!=null) {
			for (UserFriendBean friend : date.marked_u) {
				// 添加标新好友
				ContactModel contactModel = new ContactModel();
				contactModel.friend = friend;
				contactModel.type = ContactAdapter.ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal();
				contactModel.sortLetters = KeyOrValueGlobal.KEY_BIAOXINHUANZHE;
				contactModel.pinyinName = characterParser.getSelling(contactModel.friend.getShowName());
				contactModel.friendLibeType = 1;
				contactModels.add(contactModel);
			}
		}
		if(date.nomal_u!=null) {
			for (UserFriendBean friend : date.nomal_u) {
				ContactModel contactModel = new ContactModel();
				contactModel.friend = friend;
				contactModel.type = ContactAdapter.ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal();
				if (TextUtils.isEmpty(contactModel.friend.getShowName())) {
					contactModel.sortLetters = "";
				} else {
					contactModel.sortLetters = characterParser.getSelling(contactModel.friend.getShowName()).substring(0, 1).toUpperCase();
				}
				contactModel.pinyinName = characterParser.getSelling(contactModel.friend.getShowName());
				boolean iGoto = false;
				if (date.marked_u != null) {
					for (UserFriendBean markFriend : date.marked_u) {
						if (friend.uid.equals(markFriend.uid)) {
							// 是标星好友
							iGoto = true;
							break;
						}
					}
				}
				if (iGoto) {
					contactModel.friendLibeType = 1;
				}else{
					contactModel.friendLibeType = 2;
				}
				// 正则表达式，判断首字母是否是英文字母
				if (contactModel.sortLetters.matches("[A-Z]")) {
					contactModel.sortLetters = contactModel.sortLetters.toUpperCase();
				} else if (contactModel.sortLetters.equals(KeyOrValueGlobal.KEY_BIAOXINHUANZHE)) {
				} else {
					contactModel.sortLetters = "#";
				}
				contactModels.add(contactModel);
			}
		}
		// 根据a-z进行排序
		Collections.sort(contactModels, new PinyinComparator());
		return contactModels;
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		CharacterParser characterParser = new CharacterParser();
		List<ContactModel> filterDateList = new ArrayList<ContactModel>();
		if(TextUtils.isEmpty(filterStr)){
			if(contactRecyclerFragment.getAdapter()!=null) {
				filterDateList = contactRecyclerFragment.getAdapter().getmAllDatas();
			}
		}else{
			filterDateList.clear();
			for(ContactModel contactModel : contactRecyclerFragment.getAdapter().getmAllDatas()){
				String name = contactModel.friend.getShowName();
				if(name!=null && (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString()))){
					filterDateList.add(contactModel);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, new PinyinComparator());
		contactRecyclerFragment.getAdapter().updateListView(filterDateList);
	}
}
