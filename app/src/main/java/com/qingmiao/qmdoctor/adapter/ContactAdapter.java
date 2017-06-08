package com.qingmiao.qmdoctor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.ContactModel;
import com.qingmiao.qmdoctor.fragment.ContactRecyclerFragment;
import com.qingmiao.qmdoctor.widget.SwipeMenuView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<ContactModel> mContactNames; // 联系人名称字符串
    private List<String> characterList; // 字母List
    private boolean isShowChecker = false;
    private boolean isOpenSwipeButton ;
    private List<ContactModel> mAllDatas;
    ContactRecyclerFragment.OnContactItemClickListener onItemClickListener;


    public void setOnItemClickListener(ContactRecyclerFragment.OnContactItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setShowChecker(boolean showChecker) {
        isShowChecker = showChecker;
    }

    public enum ITEM_TYPE {
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }

    public ContactAdapter(Context context, List<ContactModel> contactNames) {
        mContext = context;
        characterList = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
        mContactNames = contactNames;
        this.mAllDatas = mContactNames;
        initContact();
    }

    public List<ContactModel> getmAllDatas() {
        return mAllDatas;
    }

    public void setmAllDatas(List<ContactModel> mAllDatas) {
        this.mAllDatas = mAllDatas;
    }

    private void initContact() {
        List<ContactModel> newModels = new ArrayList<>();
        characterList.clear();
        List<String> indexNames = new ArrayList<>();
        for (int i = 0; i < mContactNames.size() ; i++) {
            String pinyinIndex = mContactNames.get(i).sortLetters;
            if(!indexNames.contains(pinyinIndex)){
                // 第一次添加
                indexNames.add(pinyinIndex);
                // 添加头部
                ContactModel contactModel = new ContactModel();
                contactModel.sortLetters = pinyinIndex;
                contactModel.type = ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal();
                contactModel.friend = null;
                newModels.add(contactModel);
                newModels.add(mContactNames.get(i));
                characterList.add(pinyinIndex);
            }else{
                newModels.add(mContactNames.get(i));
            }
        }
        mContactNames.clear();
        mContactNames.addAll(newModels);
    }

    public List<ContactModel> getDataList(){
        List<ContactModel> models = new ArrayList<>();
        // 去掉标题
        for (ContactModel contactModel: mContactNames) {
            if(contactModel.type == ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal() && contactModel.friend!=null){
                models.add(contactModel);
            }
        }
        return models;
    }

    public void setOpenSwipeButton(boolean openSwipeButton) {
        isOpenSwipeButton = openSwipeButton;
    }

    public void updateListView(List<ContactModel> contactModels){
        mContactNames.clear();
        mContactNames.addAll(contactModels);
        initContact();
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()) {
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_character, parent, false));
        } else {
            return new ContactHolder(mLayoutInflater.inflate(R.layout.item_contact, parent, false),isShowChecker);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mTextView.setText(mContactNames.get(position).sortLetters);
        } else if (holder instanceof ContactHolder) {
            if(!TextUtils.isEmpty(mContactNames.get(position).friend.remark_names)){
                ((ContactHolder) holder).mTextView.setText(mContactNames.get(position).friend.remark_names);
            }else if(!TextUtils.isEmpty(mContactNames.get(position).friend.user_name)){
                ((ContactHolder) holder).mTextView.setText(mContactNames.get(position).friend.user_name);
            }else if(!TextUtils.isEmpty(mContactNames.get(position).friend.nickname)){
                ((ContactHolder) holder).mTextView.setText(mContactNames.get(position).friend.nickname);
            }else if(!TextUtils.isEmpty(mContactNames.get(position).friend.mobile)){
                ((ContactHolder) holder).mTextView.setText(mContactNames.get(position).friend.mobile);
            }
            GlideUtils.LoadAvatarImageView(mContext,mContactNames.get(position).friend.avatar,((ContactHolder) holder).ivPhoto);
            // 设置
            if(isOpenSwipeButton){
                //这句话关掉IOS阻塞式交互效果 并依次打开左滑右滑
                ((SwipeMenuView)((ContactHolder)holder).swipe_content).setSwipeEnable(true);
                ((SwipeMenuView)((ContactHolder)holder).swipe_content).setIos(false).setLeftSwipe(true);
            }else{
                ((SwipeMenuView)((ContactHolder)holder).swipe_content).setSwipeEnable(false);
            }
            if( ((ContactHolder) holder).isShowCheck){
                ((ContactHolder) holder).ll_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mContactNames.get(position).isCheck){
                            ((ContactHolder) holder).cbChecked.setChecked(false);
                            mContactNames.get(position).isCheck = false;
                        }else{
                            ((ContactHolder) holder).cbChecked.setChecked(true);
                            mContactNames.get(position).isCheck = true;
                        }
                    }
                });
                ((ContactHolder) holder).cbChecked.setChecked(mContactNames.get(position).isCheck);
            }

            if(onItemClickListener!=null){
                ((ContactHolder) holder).ll_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContactModel contactModel = mContactNames.get(position);
                        if(contactModel!=null && contactModel.friend!=null){
                            onItemClickListener.onItemClick(v,position,contactModel);
                        }
                    }
                });
            }
            ((ContactHolder) holder).btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 检查
                    ContactModel deleModel = mContactNames.get(position);
                    int iSize = 0;
                    for (int i = 0; i < mContactNames.size(); i++) {
                        ContactModel contactModel = mContactNames.get(i);
                        if(contactModel.sortLetters.equals(deleModel.sortLetters)){
                            iSize++;
                        }
                    }
                    mContactNames.remove(position);
                    ContactAdapter.this.notifyItemRemoved(position);//推荐用这个
                    if(position != (getDataList().size())){ // 如果移除的是最后一个，忽略 注意：这里的mDataAdapter.getDataList()不需要-1，因为上面已经-1了
                        notifyItemRangeChanged(position, mContactNames.size() - position);
                    }
                    if(iSize == 2){
                        mContactNames.remove(position-1);
                    }
                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                    notifyDataSetChanged();
                }
            });
        }
    }





    @Override
    public int getItemViewType(int position) {
        return mContactNames.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mContactNames == null ? 0 : mContactNames.size();
    }

    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        CharacterHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.character);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        CheckBox cbChecked;
        View swipe_content;
        LinearLayout ll_content;
        boolean isShowCheck ;
        ImageView ivPhoto;
        Button btnDelete;


        ContactHolder(View view,boolean isShowCheck) {
            super(view);
            swipe_content = view.findViewById(R.id.swipe_content);
            mTextView = (TextView) view.findViewById(R.id.title);
            cbChecked = (CheckBox) view.findViewById(R.id.cbChecked);
            ll_content = (LinearLayout) view.findViewById(R.id.ll_content);
            ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
            if(isShowCheck){
                cbChecked.setVisibility(View.VISIBLE);
            }else{
                cbChecked.setVisibility(View.GONE);
            }
            this.isShowCheck  = isShowCheck;
            btnDelete = (Button) view.findViewById(R.id.btnDelete);
        }
    }

    public int getScrollPosition(String character) {
        if (characterList.contains(character)) {
            for (int i = 0; i < mContactNames.size(); i++) {
                if (mContactNames.get(i).sortLetters.equals(character)) {
                    return i;
                }
            }
        }
        return -1; // -1不会滑动
    }

    /**
     * 得到选中的联系人
     * @return
     */
    public List<ContactModel> getCheckModel(){
        List<ContactModel> models = new ArrayList<>();
        for (ContactModel contactModel:mContactNames) {
            if(contactModel.isCheck && contactModel.type == ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal() && contactModel.friend!=null){
                models.add(contactModel);
            }
        }
        return models;
    }




}
