package com.example.myshop.bean.msg;


public class CreateOrderRespMsg extends BaseRespMsg {

    private OrderRespMsg data;

    public OrderRespMsg getData() {
        return data;
    }

    public void setData(OrderRespMsg data) {
        this.data = data;
    }

}


