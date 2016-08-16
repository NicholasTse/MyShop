package com.example.myshop.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.myshop.Contants;
import com.example.myshop.MyShopApplication;
import com.example.myshop.R;
import com.example.myshop.adapter.AddrListAdapter;
import com.example.myshop.adapter.MySimpleAdapter;
import com.example.myshop.bean.Address;
import com.example.myshop.bean.msg.BaseRespMsg;
import com.example.myshop.http.OkHttpHelper;
import com.example.myshop.http.TokenCallBack;
import com.example.myshop.widget.MyToolbar;
import com.squareup.okhttp.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddrListActivity extends AppCompatActivity implements View.OnClickListener{

    private MyToolbar mToolbar;
    private RecyclerView mRecyclerview;

    private AddrListAdapter mAdapter;
    private OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrlist);

        initData();
    }

    private void initData() {
        mToolbar = (MyToolbar) findViewById(R.id.toolbar);
        mRecyclerview = (RecyclerView) findViewById(R.id.recycler_addr);

        mToolbar.setRightButtonOnClickListener(this);
        mToolbar.getmLeftButton().setOnClickListener(this);

        requestData();
    }

    //请求地址列表
    private void requestData() {
        long userId = MyShopApplication.getInstance().getUser().getId();
        String token = MyShopApplication.getInstance().getToken();
        mHttpHelper.doGet(Contants.API.ADDRESS_LIST + "?user_id=" + userId + "&token=" + token, new TokenCallBack<List<Address>>(this) {

            @Override
            public void onSuccess(Response response, List<Address> datas) {
                showData(datas);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

    //显示地址列表
    private void showData(List<Address> datas) {
        Collections.sort(datas);
        if (mAdapter == null) {
            mAdapter = new AddrListAdapter(this, datas, new AddrListAdapter.AddressListener() {
                @Override
                public void setDefault(Address address) {
                    updateDefaultAddr(address);
                }

                @Override
                public void eidtAddr(Address address) {

                }

                @Override
                public void delAddr(Address address) {

                }
            });

            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerview.setItemAnimator(new DefaultItemAnimator());
        } else {

            mAdapter.notifyDataSetChanged();

        }
    }


    //更新默认地址
    private void updateDefaultAddr(Address address) {

        Map<String,String> params = new HashMap<>();
        params.put("id",address.getId()+"");
        params.put("consignee",address.getConsignee());
        params.put("phone",address.getPhone());
        params.put("addr",address.getAddr());
        params.put("zip_code",address.getZip_code());
        params.put("is_default",address.getIsDefault()+"");
        params.put("token", MyShopApplication.getInstance().getToken());

        mHttpHelper.doPost(Contants.API.ADDRESS_UPDATE, params, new TokenCallBack<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg msg) {
                if(msg.getStatus() == BaseRespMsg.STATUS_SUCCESS){

                    requestData();
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.toolbar_leftButton:
                onBackPressed();
                break;
            case R.id.toolbar_rightButton:
                Intent intent = new Intent(AddrListActivity.this, CreateAddrActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}