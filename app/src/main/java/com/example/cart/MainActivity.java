package com.example.cart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cart.decoration.CartDecoration;
import com.example.cart.listener.CartGroupListener;
import com.example.cart.listener.DecorationListener;
import com.example.cart.view.CartRecycleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view)
    CartRecycleView recyclerView;

    CartAdapter cartAdapter;
    List<String> list;
    int listSize=25;
    DecorationListener listener=new DecorationListener() {
        @Override
        public String getGroupName(int position) {
            //获取组名，用于判断是否是同一组
            if (list.size() > position) {
                if (position < 10) {
                    return "好设计";
                } else if ( position >= 10 && position < 20) {
                    return "全球购";
                }
            }
            return null;
        }

        @Override
        public View getGroupView(int position) {
            //获取自定定义的组View
            if (list.size() > position) {
                View view = getLayoutInflater().inflate(R.layout.layout_cart_decoration_view, null, false);
                TextView textView = view.findViewById(R.id.tv);
                textView.setText(getGroupName(position));
                return view;
            } else {
                return null;
            }
        }
    };
    CartGroupListener cartGroupListener=new CartGroupListener() {
        @Override
        public void selectAllGlobal(boolean is) {
            Toast.makeText(MainActivity.this,(is?"选中":"没有选中")+"所有全球购商品",Toast.LENGTH_SHORT).show();
            cartAdapter.notifyDataSetChanged();
        }

        @Override
        public void selectAllInland(boolean is) {
            Toast.makeText(MainActivity.this,(is?"选中":"没有选中")+"所有非跨境商品",Toast.LENGTH_SHORT).show();
            cartAdapter.notifyDataSetChanged();
        }
    };
    private CartDecoration decoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        list = new ArrayList<>();
        init();
    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        decoration = CartDecoration.Builder
                .init(listener)
                .setCartGroupListener(cartGroupListener)
               .setGroupHeight(dip2px(50)).build();  //设置高度
        decoration.setLimitPosition(25);
        recyclerView.addItemDecoration(decoration);
        loadData();
    }

    private void loadData() {
        for (int i = 0; i < listSize; i++) {
            list.add("第" + i + "项");
        }
        setAdapter();
    }

    private void setAdapter() {
        if (cartAdapter == null) {
            cartAdapter = new CartAdapter(this, list);
            recyclerView.setAdapter(cartAdapter);
        } else {
            cartAdapter.notifyDataSetChanged();
        }
    }

    private int dip2px(int dipValue){
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        float scale=dm.density;
        return (int) (dipValue * scale + 0.5f);
    }

}
