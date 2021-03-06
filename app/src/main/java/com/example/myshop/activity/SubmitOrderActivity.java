package com.example.myshop.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myshop.Contants;
import com.example.myshop.MyShopApplication;
import com.example.myshop.R;
import com.example.myshop.adapter.OrderWareAdapter;
import com.example.myshop.adapter.layoutmanager.FullyLinearLayoutManager;
import com.example.myshop.bean.Charge;
import com.example.myshop.bean.ShoppingCart;
import com.example.myshop.bean.msg.BaseRespMsg;
import com.example.myshop.bean.msg.CreateOrderRespMsg;
import com.example.myshop.http.OkHttpHelper;
import com.example.myshop.http.SpotsCallBack;
import com.example.myshop.http.TokenCallBack;
import com.example.myshop.utils.CartProvider;
import com.example.myshop.utils.JSONUtil;
import com.example.myshop.widget.MyToolbar;
import com.pingplusplus.android.PaymentActivity;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitOrderActivity extends BaseActivity implements View.OnClickListener{

    /**
     * 银联支付渠道
     */
    private static final String CHANNEL_UPACP = "upacp";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    /**
     * 百度支付渠道
     */
    private static final String CHANNEL_BFB = "bfb";
    /**
     * 京东支付渠道
     */
    private static final String CHANNEL_JDPAY_WAP = "jdpay_wap";

    private MyToolbar mToolbar;
    private RecyclerView mRecycler;
    private RelativeLayout mRlayout_alipay, mRlayout_wx, mRlayout_bfb;
    private RadioButton mRbtn_alipay, mRbtn_wx, mRbtn_bfb;
    private TextView mTxtTotal;
    private Button mBtnSubmit;

    private CartProvider mProvider;
    private OrderWareAdapter mAdapter;
    private OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();

    private String payChannel = CHANNEL_ALIPAY;
    private String orderNum;
    private float totalPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_order);


        initView();
        initEvent();
        initData();
    }

    private void initView() {

        mToolbar = (MyToolbar) findViewById(R.id.toolbar);
        mRecycler = (RecyclerView) findViewById(R.id.recycler_items);

        mRlayout_alipay = (RelativeLayout) findViewById(R.id.relayout_alipay);
        mRlayout_wx = (RelativeLayout) findViewById(R.id.relayout_wx);
        mRlayout_bfb = (RelativeLayout) findViewById(R.id.relayout_bfb);

        mRbtn_alipay = (RadioButton) findViewById(R.id.rbtn_alipay);
        mRbtn_wx = (RadioButton) findViewById(R.id.rbtn_wx);
        mRbtn_bfb = (RadioButton) findViewById(R.id.rbtn_bfb);

        mTxtTotal = (TextView) findViewById(R.id.txt_total);
        mBtnSubmit = (Button) findViewById(R.id.btn_submitOrder);

    }

    private void initEvent() {
        mToolbar.getmLeftButton().setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);

        mRlayout_alipay.setOnClickListener(this);
        mRlayout_wx.setOnClickListener(this);
        mRlayout_bfb.setOnClickListener(this);

    }

    private void initData() {

        mProvider = CartProvider.getInstance(this);
        mAdapter = new OrderWareAdapter(this, mProvider.getAll());

        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setAdapter(mAdapter);

        totalPrice = mAdapter.getTotalPrice();
        mTxtTotal.setText("实付：¥" + totalPrice);

    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();
        switch (viewId) {

            case R.id.toolbar_leftButton:
                onBackPressed();
                break;
            case R.id.btn_submitOrder:
                postOrder();
                break;
            case R.id.relayout_alipay:
                resetRadioButton();
                payChannel = CHANNEL_ALIPAY;
                mRbtn_alipay.setChecked(true);
                break;
            case R.id.relayout_wx:
                resetRadioButton();
                payChannel = CHANNEL_WECHAT;
                mRbtn_wx.setChecked(true);
                break;
            case R.id.relayout_bfb:
                resetRadioButton();
                payChannel = CHANNEL_BFB;
                mRbtn_bfb.setChecked(true);
                break;
            default:
                break;
        }

    }

    //将radiobutton全部设置不选中
    private void resetRadioButton() {
        mRbtn_alipay.setChecked(false);
        mRbtn_wx.setChecked(false);
        mRbtn_bfb.setChecked(false);
    }

    private void postOrder() {

        final List<ShoppingCart> carts = mAdapter.getDatas();
        List<WareItem> wareitems = new ArrayList<>(carts.size());

        for (ShoppingCart cart : carts) {
            WareItem item = new WareItem();
            item.setWare_id(cart.getId());
            item.setAmount(cart.getPrice().intValue());
            wareitems.add(item);
        }

        String item_json = JSONUtil.toJSON(wareitems);

        Map<String, String> params = new HashMap<>(5);
        params.put("user_id",MyShopApplication.getInstance().getUser().getId()+"");
        params.put("item_json",item_json);
        params.put("pay_channel",payChannel);
        params.put("amount",(int)totalPrice+"");
        params.put("addr_id",1+"");
        params.put("token", MyShopApplication.getInstance().getToken());

        mBtnSubmit.setEnabled(false);
        mHttpHelper.doPost(Contants.API.ORDER_CREATE, params, new TokenCallBack<CreateOrderRespMsg>(this) {

            @Override
            public void onSuccess(Response response, CreateOrderRespMsg respMsg) {

                mBtnSubmit.setEnabled(true);
                orderNum = respMsg.getData().getOrderNum();
                Charge charge = respMsg.getData().getCharge();

                openPaymentActivity(JSONUtil.toJSON(charge));


            }

            @Override
            public void onError(Response response, int code, Exception e) {
                mBtnSubmit.setEnabled(true);
            }
        });

    }

    private void openPaymentActivity(String charge){

        Intent intent = new Intent();
        String packageName = getPackageName();
        ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
        intent.setComponent(componentName);
        intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
        startActivityForResult(intent, Contants.REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Contants.REQUEST_CODE_PAYMENT) {

            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                switch (result) {
                    case Contants.ORDER_SUCCESS:
                        changeOrderStatus(1);
                        break;

                    case Contants.ORDER_FAIL:
                        changeOrderStatus(-1);
                        break;

                    case Contants.ORDER_CANCEL:
                        changeOrderStatus(-2);
                        break;

                    default:
                        changeOrderStatus(0);
                        break;
                }
            }
        }
    }

    private void changeOrderStatus(final int status) {

        Map<String,String> params = new HashMap<>(5);
        params.put("order_num",orderNum);
        params.put("status",status+"");
        params.put("token", MyShopApplication.getInstance().getToken());

        mHttpHelper.doPost(Contants.API.ORDER_COMPLEPE, params, new TokenCallBack<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg respMsg) {
                showResultAct(status);
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                showResultAct(-1);
            }
        });

    }


    private void showResultAct(int status) {

        Intent intent = new Intent(SubmitOrderActivity.this, PayResultActivity.class);
        intent.putExtra(Contants.ORDER_STATUS, status);
        startActivity(intent);
        SubmitOrderActivity.this.finish();

    }

    private class WareItem {

        private  Long ware_id;
        private  int amount;

        public void setWare_id(Long ware_id) {
            this.ware_id = ware_id;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public Long getWare_id() {
            return ware_id;
        }

        public int getAmount() {
            return amount;
        }
    }

}
