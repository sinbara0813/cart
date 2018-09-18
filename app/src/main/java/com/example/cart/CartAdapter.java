package com.example.cart;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cart.view.CheckBox;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者:Created by sinbara on 2018/9/15.
 * 邮箱:hrb940258169@163.com
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemHolder> {

    private Context context;
    private List<String> list;

    public CartAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cart_item_view, parent, false);
        return new CartItemHolder(view);
    }

    @Override
    public void onBindViewHolder(CartItemHolder holder, int position) {
        holder.contentTv.setText("第" + position + "项");
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class CartItemHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.checkbox)
        CheckBox checkbox;
        @Bind(R.id.content_tv)
        TextView contentTv;
        private Integer[] colors = new Integer[]{
                Color.BLUE, Color.CYAN, Color.DKGRAY, Color.GREEN, Color.RED, Color.YELLOW
        };

        public CartItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            contentTv.setBackgroundColor(colors[new Random().nextInt(6)]);
        }
    }
}
