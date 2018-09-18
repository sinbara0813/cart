package com.example.cart.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.cart.listener.OnGroupClickListener;

/**
 * Name: d2c
 * Anthor: hrb
 * Date: 2017/8/2 16:40
 * Copyright (c) 2016 d2cmall. All rights reserved.
 */
public class CartRecycleView extends RecyclerView {

    public OnGroupClickListener onGroupClickListener;
    private Context context;

    public CartRecycleView(Context context) {
        this(context,null);
    }

    public CartRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CartRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        this.context=context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(getContext(), gestureListener);
        }
        if (gestureDetector.onTouchEvent(ev)){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 记录每个头部和悬浮头部的坐标信息【用于点击事件】
     * 位置由子类添加
     */
    public SparseArray<Integer> stickyHeaderPosArray = new SparseArray<>();
    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (int i = 0; i < stickyHeaderPosArray.size(); i++) {
                int value = stickyHeaderPosArray.valueAt(i);
                float y = e.getY();
                float x = e.getX();
                if (value - dip2px(50) <= y && y <= value&&x<dip2px(56)) {
                    //如果点击到分组头
                    //onGroupClick(stickyHeaderPosArray.keyAt(i));
                    if (onGroupClickListener!=null){
                        onGroupClickListener.onClick(stickyHeaderPosArray.keyAt(i));
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    private int dip2px(int dipValue){
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        float scale=dm.density;
        return (int) (dipValue * scale + 0.5f);
    }
}
