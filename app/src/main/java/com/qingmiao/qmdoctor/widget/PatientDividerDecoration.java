package com.qingmiao.qmdoctor.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.qingmiao.qmdoctor.adapter.ContactAdapter;
import static com.qingmiao.qmdoctor.adapter.ContactAdapter.ITEM_TYPE;

/**
 * A simple divider decoration with customizable colour, height, and left and right padding.
 * Used only for LRecyclerView
 */
public class PatientDividerDecoration extends RecyclerView.ItemDecoration {

    private int mHeight;
    private int mLPadding;
    private int mRPadding;
    private Paint mPaint;

    private PatientDividerDecoration(int height, int lPadding, int rPadding, int colour) {
        mHeight = height;
        mLPadding = lPadding;
        mRPadding = rPadding;
        mPaint = new Paint();
        mPaint.setColor(colour);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        RecyclerView.Adapter adapter = parent.getAdapter();

        LRecyclerViewAdapter lRecyclerViewAdapter;
        if (adapter instanceof LRecyclerViewAdapter) {
            lRecyclerViewAdapter = (LRecyclerViewAdapter) adapter;
        } else {
            throw new RuntimeException("the adapter must be LRecyclerViewAdapter");
        }

        int count = parent.getChildCount();
        // 判断患者列表是否显示分割线
        if(lRecyclerViewAdapter.getInnerAdapter() instanceof ContactAdapter){
            ContactAdapter contactAdapter = (ContactAdapter) lRecyclerViewAdapter.getInnerAdapter();
            for (int i = 0; i < count; i++) {
                final View child = parent.getChildAt(i);
                final int top = child.getBottom();
                final int bottom = top + mHeight;

                int left = child.getLeft() + mLPadding;
                int right = child.getRight() - mRPadding;

                int position = parent.getChildAdapterPosition(child);

                c.save();

             //   ContactModel contactModel = contactAdapter.getDataList().get(position);

                if (lRecyclerViewAdapter.isRefreshHeader(position) || lRecyclerViewAdapter. isHeader(position) || lRecyclerViewAdapter.isFooter(position)) {
                    c.drawRect(0, 0, 0, 0, mPaint);
                }else {
                    // position -2 是减去刷新的view  和  header  view
                    int Dcount = lRecyclerViewAdapter.getHeaderViewsCount() + 1;
                    if(contactAdapter.getItemViewType(position - Dcount)==ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()){

                    }else if(contactAdapter.getItemViewType(position - Dcount)==ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()){
                        // 判断下一个view 是不是顶部标题  如果是顶部标题  不要绘制分割线   如果是内容 继续绘制分割线
                        try{
                            if(contactAdapter.getItemViewType(position - lRecyclerViewAdapter.getHeaderViewsCount())==ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()){

                            }else if(contactAdapter.getItemViewType(position - lRecyclerViewAdapter.getHeaderViewsCount())==ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()){
                                c.drawRect(left, top, right, bottom, mPaint);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                c.restore();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();

        LRecyclerViewAdapter lRecyclerViewAdapter;
        if (adapter instanceof LRecyclerViewAdapter) {
            lRecyclerViewAdapter = (LRecyclerViewAdapter) adapter;
        } else {
            throw new RuntimeException("the adapter must be LRecyclerViewAdapter");
        }

        int position = parent.getChildAdapterPosition(view);

        if (lRecyclerViewAdapter.isRefreshHeader(position) || lRecyclerViewAdapter. isHeader(position) || lRecyclerViewAdapter.isFooter(position)) {
            outRect.bottom = 0;
            outRect.set(0, 0, 0, 0);
        } else {
            int Dcount = lRecyclerViewAdapter.getHeaderViewsCount() + 1;
            if(lRecyclerViewAdapter.getItemViewType(position)==ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()){
                outRect.set(0, 0, 0, 0);
            }else if(lRecyclerViewAdapter.getItemViewType(position)==ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()){
                // 判断下一个view 是不是顶部标题  如果是顶部标题  不要绘制分割线   如果是内容 继续绘制分割线
                try{
                    if(lRecyclerViewAdapter.getItemViewType(position + lRecyclerViewAdapter.getHeaderViewsCount())==ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()){
                        outRect.set(0, 0, 0, 0);
                    }else if(lRecyclerViewAdapter.getItemViewType(position + lRecyclerViewAdapter.getHeaderViewsCount())==ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()){
                        outRect.set(0, 0, 0, mHeight);
                    }else{
                        outRect.set(0, 0, 0, 0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    outRect.set(0, 0, 0, 0);
                }
            }else{
                outRect.set(0, 0, 0, 0);
            }

          //  outRect.set(0, 0, 0, mHeight);
        }

    }

    /**
     * A basic builder for divider decorations. The default builder creates a 1px thick black divider decoration.
     */
    public static class Builder {
        private Context mContext;
        private Resources mResources;
        private int mHeight;
        private int mLPadding;
        private int mRPadding;
        private int mColour;

        public Builder(Context context) {
            mContext = context;
            mResources = context.getResources();
            mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 1f, context.getResources().getDisplayMetrics());
            mLPadding = 0;
            mRPadding = 0;
            mColour = Color.BLACK;
        }

        /**
         * Set the divider height in pixels
         * @param pixels height in pixels
         * @return the current instance of the Builder
         */
        public Builder setHeight(float pixels) {
            mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixels, mResources.getDisplayMetrics());

            return this;
        }

        /**
         * Set the divider height in dp
         * @param resource height resource id
         * @return the current instance of the Builder
         */
        public Builder setHeight(@DimenRes int resource) {
            mHeight = mResources.getDimensionPixelSize(resource);
            return this;
        }

        /**
         * Sets both the left and right padding in pixels
         * @param pixels padding in pixels
         * @return the current instance of the Builder
         */
        public Builder setPadding(float pixels) {
            setLeftPadding(pixels);
            setRightPadding(pixels);

            return this;
        }

        /**
         * Sets the left and right padding in dp
         * @param resource padding resource id
         * @return the current instance of the Builder
         */
        public Builder setPadding(@DimenRes int resource) {
            setLeftPadding(resource);
            setRightPadding(resource);
            return this;
        }

        /**
         * Sets the left padding in pixels
         * @param pixelPadding left padding in pixels
         * @return the current instance of the Builder
         */
        public Builder setLeftPadding(float pixelPadding) {
            mLPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixelPadding, mResources.getDisplayMetrics());

            return this;
        }

        /**
         * Sets the right padding in pixels
         * @param pixelPadding right padding in pixels
         * @return the current instance of the Builder
         */
        public Builder setRightPadding(float pixelPadding) {
            mRPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixelPadding, mResources.getDisplayMetrics());

            return this;
        }

        /**
         * Sets the left padding in dp
         * @param resource left padding resource id
         * @return the current instance of the Builder
         */
        public Builder setLeftPadding(@DimenRes int resource) {
            mLPadding = mResources.getDimensionPixelSize(resource);

            return this;
        }

        /**
         * Sets the right padding in dp
         * @param resource right padding resource id
         * @return the current instance of the Builder
         */
        public Builder setRightPadding(@DimenRes int resource) {
            mRPadding = mResources.getDimensionPixelSize(resource);

            return this;
        }

        /**
         * Sets the divider colour
         * @param resource the colour resource id
         * @return the current instance of the Builder
         */
        public Builder setColorResource(@ColorRes int resource) {
            setColor(ContextCompat.getColor(mContext,resource));

            return this;
        }

        /**
         * Sets the divider colour
         * @param color the colour
         * @return the current instance of the Builder
         */
        public Builder setColor(@ColorInt int color) {
            mColour = color;

            return this;
        }

        /**
         * Instantiates a DividerDecoration with the specified parameters.
         * @return a properly initialized DividerDecoration instance
         */
        public PatientDividerDecoration build() {
            return new PatientDividerDecoration(mHeight, mLPadding, mRPadding, mColour);
        }
    }
}
