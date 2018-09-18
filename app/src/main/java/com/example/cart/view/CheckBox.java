package com.example.cart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.cart.R;
import com.example.cart.listener.CheckStateChangeListener;


public class CheckBox extends LinearLayout {

    private boolean isChecked;
    private int selectedId;
    private int unselectedId;
    private ImageView imageView;
    private boolean isSingle;
    private boolean isClick;

    private CheckStateChangeListener nCheckedChangeListener;


    public void setOnStatusChangedListener(CheckStateChangeListener l) {
        this.nCheckedChangeListener = l;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        if (isChecked) {
            imageView.setBackgroundResource(selectedId);
        } else {
            imageView.setBackgroundResource(unselectedId);
        }
    }

    public CheckBox(Context context) {
        super(context);
        imageView = new ImageView(context);
        setGravity(Gravity.CENTER);
        addView(imageView);
        initView();
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.checkBox);
        isSingle=typedArray.getBoolean(R.styleable.checkBox_is_single,false);
        isClick=typedArray.getBoolean(R.styleable.checkBox_is_clickable,true);
        imageView = new ImageView(context);
        setGravity(Gravity.CENTER);
        addView(imageView);
        initView();
    }

    private void initView() {
        selectedId = R.mipmap.icon_shopcart_aselected;//ic_personal_turn_out_season_normal;; ic_login_checked
        unselectedId = R.mipmap.icon_shopcart_unaselected;//ic_personal_turn_out_season_actives;; ic_login_uncheck
        setChecked(isChecked);
        if (isClick){
            this.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    updateStatus(v);
                }

            });
        }
    }

    public void setCheckColorId(int selectedId,int unselectedId){
        this.selectedId=selectedId;
        this.unselectedId=unselectedId;
        setChecked(isChecked);
    }

    private void updateStatus(View v) {
        if (isSingle&&isChecked){
            return;
        }
        setChecked(!isChecked);
        if (nCheckedChangeListener != null) {
            nCheckedChangeListener.onStatusChanged(v, isChecked);
        }
    }

    public void setBackgroundResource(int selectedID, int unselectedID) {
        this.selectedId = selectedID;
        this.unselectedId = unselectedID;

        setChecked(isChecked);
    }

    public void isSingle(boolean is){
        isSingle=is;
    }

    public void setLayoutParam(LayoutParams layoutParam){
        setLayoutParams(layoutParam);
    }

}
