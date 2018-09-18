package com.example.cart.decoration;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.cart.view.CartRecycleView;
import com.example.cart.view.CheckBox;
import com.example.cart.R;
import com.example.cart.listener.CartGroupListener;
import com.example.cart.listener.DecorationListener;
import com.example.cart.listener.OnGroupClickListener;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 作者:Created by sinbara on 2018/9/15.
 * 邮箱:hrb940258169@163.com
 */

public class CartDecoration extends BaseDecoration implements OnGroupClickListener {

    private Paint mGroutPaint;
    /**
     * 使用软引用缓存图片，防止内存泄露
     */
    private SparseArray<Reference<Bitmap>> mBitmapCache = new SparseArray<>();

    private DecorationListener decorationListener;

    private HashMap<String,Boolean> selectCache=new HashMap<>();

    private int limitPosition;

    private CartGroupListener cartGroupListener;

    /**
     * 缓存View
     */
    private SparseArray<View> headViewMap = new SparseArray<>();

    private CartDecoration(DecorationListener decorationListener) {
        super();
        this.decorationListener = decorationListener;
        //设置悬浮栏的画笔---mGroutPaint
        mGroutPaint = new Paint();
        mGroutPaint.setColor(mGroupBackground);
        setOnGroupClickListener(this);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        ((CartRecycleView)parent).stickyHeaderPosArray.clear();
        if (((CartRecycleView)parent).onGroupClickListener==null){
            ((CartRecycleView)parent).onGroupClickListener=this;
        }
        //绘制
        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        String preGroupName;
        String curGroupName;
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(childView);
            if (position>=limitPosition&&i==0){
                break;
            }
            curGroupName = getGroupName(position);
            if (i == 0) {
                preGroupName = curGroupName;
            } else {
                preGroupName = getGroupName(position - 1);
            }
            boolean isFirstInGroup = i != 0 && TextUtils.equals(curGroupName, preGroupName);
            if (isFirstInGroup || curGroupName == null) {
                //绘制分割线
                if (mDivideHeight != 0) {
                    float bottom = childView.getTop();
                    if (bottom < mGroupHeight) {
                        //高度小于顶部悬浮栏时，跳过绘制
                        continue;
                    }
                    c.drawRect(left, bottom - mDivideHeight, right, bottom, mDividePaint);
                }
            } else {
                int viewBottom = childView.getBottom();
                //top 决定当前顶部第一个悬浮Group的位置
                int bottom = Math.max(mGroupHeight, childView.getTop() + parent.getPaddingTop());
                if (position + 1 < itemCount) {
                    //下一组的第一个View接近头部
                    if (isLastLineInGroup(parent, position) && viewBottom < bottom) {
                        bottom = viewBottom;
                    }
                }
                c.drawRect(left, bottom - mGroupHeight, right, bottom, mGroutPaint);
                //根据position获取View
                View groupView;
                if (headViewMap.get(position) == null) {
                    groupView = getGroupView(position);
                    if (groupView == null) {
                        return;
                    }
                    groupView.setDrawingCacheEnabled(true);
                    //手动对view进行测量，指定groupView的高度、宽度
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(right, mGroupHeight);
                    groupView.setLayoutParams(layoutParams);
                    groupView.measure(
                            View.MeasureSpec.makeMeasureSpec(right, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(mGroupHeight, View.MeasureSpec.EXACTLY));
                    groupView.layout(left, bottom - mGroupHeight, right, bottom);
                } else {
                    groupView = headViewMap.get(position);
                }
                Bitmap bitmap;
                if (mBitmapCache.get(position) != null && mBitmapCache.get(position).get() != null) {
                    bitmap = mBitmapCache.get(position).get();
                } else {
                    bitmap = Bitmap.createBitmap(groupView.getDrawingCache());
                    mBitmapCache.put(position, new SoftReference<Bitmap>(bitmap));
                }

                c.drawBitmap(bitmap, left, bottom - mGroupHeight, null);
                //将头部信息放进array，用于处理点击事件
                stickyHeaderPosArray.put(position,bottom);
                ((CartRecycleView)parent).stickyHeaderPosArray.put(position, bottom);
            }
        }
    }

    /**
     * 获取组名
     *
     * @param position position
     * @return 组名
     */
    @Override
    String getGroupName(int position) {
        if (decorationListener != null) {
            return decorationListener.getGroupName(position);
        } else {
            return null;
        }
    }

    /**
     * 获取组View
     *
     * @param position position
     * @return 组名
     */
    private View getGroupView(int position) {
        if (decorationListener != null) {
            View view=decorationListener.getGroupView(position);
            if (view!=null){
                CheckBox checkBox= (CheckBox) view.findViewById(R.id.checkbox);
                if (selectCache.containsKey(getGroupName(position))){
                    boolean check=selectCache.get(getGroupName(position));
                    checkBox.setChecked(check);
                }
            }
            return view;
        } else {
            return null;
        }
    }

    @Override
    public void onClick(int position) {
        if (selectCache.containsKey(getGroupName(position))){
            boolean checked=selectCache.get(getGroupName(position));
            checked=!checked;
            selectCache.put(getGroupName(position),checked);
            if (getGroupName(position).equals("全球购")){
                cartGroupListener.selectAllGlobal(checked);
            }else {
                cartGroupListener.selectAllInland(checked);
            }
        }else {
            selectCache.put(getGroupName(position),true);
            if (getGroupName(position).equals("全球购")){
                cartGroupListener.selectAllGlobal(true);
            }else {
                cartGroupListener.selectAllInland(true);
            }
        }
        mBitmapCache.clear();
    }

    public static class Builder {
        CartDecoration mDecoration;

        private Builder(DecorationListener listener) {
            mDecoration = new CartDecoration(listener);
        }

        public static CartDecoration.Builder init(DecorationListener listener) {
            return new CartDecoration.Builder(listener);
        }

        /**
         * 设置Group高度
         *
         * @param groutHeight 高度
         * @return this
         */
        public CartDecoration.Builder setGroupHeight(int groutHeight) {
            mDecoration.mGroupHeight = groutHeight;
            return this;
        }


        /**
         * 设置Group背景
         *
         * @param background 背景色
         */
        public CartDecoration.Builder setGroupBackground(@ColorInt int background) {
            mDecoration.mGroupBackground = background;
            mDecoration.mGroutPaint.setColor(mDecoration.mGroupBackground);
            return this;
        }

        /**
         * 设置分割线高度
         *
         * @param height 高度
         * @return this
         */
        public CartDecoration.Builder setDivideHeight(int height) {
            mDecoration.mDivideHeight = height;
            return this;
        }

        /**
         * 设置分割线颜色
         *
         * @param color color
         * @return this
         */
        public CartDecoration.Builder setDivideColor(@ColorInt int color) {
            mDecoration.mDivideColor = color;
            return this;
        }

        /**
         * 设置点击事件
         *
         * @param listener 点击事件
         * @return this
         */
        public CartDecoration.Builder setOnClickListener(OnGroupClickListener listener) {
            mDecoration.setOnGroupClickListener(listener);
            return this;
        }

        /**
         * 重置span
         *
         * @param recyclerView      recyclerView
         * @param gridLayoutManager gridLayoutManager
         * @return this
         */
        public CartDecoration.Builder resetSpan(RecyclerView recyclerView, GridLayoutManager gridLayoutManager) {
            mDecoration.resetSpan(recyclerView, gridLayoutManager);
            return this;
        }

        public CartDecoration.Builder setLimitPosition(int position){
            mDecoration.setLimitPosition(position);
            return this;
        }

        public CartDecoration.Builder setCartGroupListener(CartGroupListener cartGroupListener){
            mDecoration.setCartGroupListener(cartGroupListener);
            return this;
        }

        public CartDecoration build() {
            return mDecoration;
        }
    }

    public void setGroupSelect(String name,boolean checked){
        if (!selectCache.containsKey(name)){
            selectCache.put(name,checked);
            mBitmapCache.clear();
            return;
        }
        if (selectCache.get(name)!=checked){
            selectCache.put(name,checked);
            mBitmapCache.clear();
        }
    }

    public void setLimitPosition(int limitPosition) {
        this.limitPosition = limitPosition;
    }

    public void setCartGroupListener(CartGroupListener cartGroupListener) {
        this.cartGroupListener = cartGroupListener;
    }
}
