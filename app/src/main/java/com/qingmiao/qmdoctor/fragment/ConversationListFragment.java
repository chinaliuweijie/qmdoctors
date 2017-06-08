package com.qingmiao.qmdoctor.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.adapter.EaseConversationAdapter;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.util.NetUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.activity.ChatActivity;
import com.qingmiao.qmdoctor.activity.LoginActivity;
import com.qingmiao.qmdoctor.activity.PatientDataActivity;
import com.qingmiao.qmdoctor.bean.HXUserData;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ConversationListFragment extends EaseConversationListFragment {

    private TextView errorText;
    private String did,token;
    public int startCount = 0;

    @Override
    protected void initView() {
        super.initView();
        View errorView = (LinearLayout) View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
        did = PrefUtils.getString(getActivity(),"did","");
        token = PrefUtils.getString(getActivity(),"token","");
        EventBus.getDefault().register(this);//在当前界面注册一个订阅者
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
        } else {
            startCount ++ ;
            if(startCount == 1 && (TextUtils.isEmpty(did)|| TextUtils.isEmpty(token))){
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String data) {
        if("changeData".equals(data)){
            did = PrefUtils.getString(getActivity(),"did","");
            token = PrefUtils.getString(getActivity(),"token","");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(Map<String,String> map) {
        LogUtil.LogShitou(map.toString());
        Iterator iterator = map.keySet().iterator();
        String key = "" ;
        if(iterator.hasNext()){
            key = (String) iterator.next();
        }
        if("delete_patient".equals(key)){
            EaseConversationAdapter adapter = conversationListView.getAdapter();
            if(adapter!=null){
                List<EMConversation> conversationList = adapter.getConversationList();
                int position = 0;
                if(conversationList!=null  && conversationList.size() >0){
                    for (int i = 0; i < conversationList.size(); i++) {
                        EMConversation emConversation = conversationList.get(i);
                        if(map.get("delete_patient").equals(emConversation.conversationId())){
                            try {
                                // delete conversation
                                EMClient.getInstance().chatManager().deleteConversation(emConversation.conversationId(), true);
                                refresh();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        
        }

    }


    @Override
    protected void setUpView() {
        super.setUpView();
        // register context menu
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.conversationId();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                ToastUtils.showLongToast(getActivity(), R.string.Cant_chat_with_yourself);
                else {
                    // 跳转界面
                    List<HXUserData> userDatas = DataSupport.where("hx_name = ?", username).find(HXUserData.class);
                    if(userDatas!=null&& userDatas.size()>0){
                        Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
                        chatIntent.putExtra("hx",username);
                        chatIntent.putExtra("uid",userDatas.get(0).getUid());
                        startActivity(chatIntent);
                    }else{
                        Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
                        chatIntent.putExtra("hx",username);
                        startActivity(chatIntent);
                    }
                }
            }
        });
        conversationListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if(index == 0){
                    boolean deleteMessage = true;
                    EMConversation tobeDeleteCons = conversationListView.getItem(position);
                    if (tobeDeleteCons == null) {
                        return true;
                    }
                    if(tobeDeleteCons.getType() == EMConversation.EMConversationType.GroupChat){
                        EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.conversationId());
                    }
                    try {
                        // delete conversation
                        EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.conversationId(), deleteMessage);
                        //    InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
                        //   inviteMessgeDao.deleteMessage(tobeDeleteCons.conversationId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    refresh();
                    // update unread count
                    //   ((MainActivity) getActivity()).updateUnreadLabel();
                }

                return false;
            }
        });


        super.setUpView();
        //end of red packet code
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())){
            errorText.setText(R.string.can_not_connect_chat_server_connection);
            errorItemContainer.setVisibility(View.GONE);
        } else {
            errorText.setText(R.string.the_current_network);
            errorItemContainer.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }
}
