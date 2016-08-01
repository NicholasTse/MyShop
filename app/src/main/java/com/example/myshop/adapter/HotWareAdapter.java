package com.example.myshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.example.myshop.R;
import com.example.myshop.bean.ShoppingCart;
import com.example.myshop.bean.Wares;
import com.example.myshop.utils.CartProvider;
import com.example.myshop.utils.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Thank on 2016/1/4.
 */
public class HotWareAdapter extends MySimpleAdapter<Wares>{

    private CartProvider mProvider;

    public HotWareAdapter(Context mContext, List<Wares> datas) {
        super(mContext, datas, R.layout.template_hot_wares);
        mProvider = new CartProvider(mContext);
    }

    @Override
    public void onBindViewData(MyViewHolder holder, final Wares item) {
        SimpleDraweeView draweeView = (SimpleDraweeView) holder.getView(R.id.draweeview);
        draweeView.setImageURI(Uri.parse(item.getImgUrl()));

        String title = item.getName().replace("\\n", "n");
        holder.getTextView(R.id.text_title).setText(title);
//        holder.getTextView(R.id.text_title).setText(item.getName());
        holder.getTextView(R.id.text_price).setText("￥" + item.getPrice());


        holder.getButton(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProvider.put(convertData(item));
                ToastUtils.show(mContext, "加入购物车成功！！");
            }
        });
    }

    public ShoppingCart convertData(Wares item) {
        ShoppingCart cart = new ShoppingCart();

        cart.setId(item.getId());
        cart.setDescription(item.getDescription());
        cart.setImgUrl(item.getImgUrl());
        cart.setName(item.getName());
        cart.setPrice(item.getPrice());

        return cart;
    }
}