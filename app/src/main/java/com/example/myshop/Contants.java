package com.example.myshop;

/**
 * Created by Thank on 2015/12/21.
 */
public class Contants {

    public static final String  COMPAINGAIN_ID="compaigin_id";
    public static final String  WARE="ware";
    public  static final String DES_KEY="Cniao5_123456";

    public static final String USER_JSON="user_json";
    public static final String TOKEN="token";
    public  static final int REQUEST_CODE=0;

    public static final String SMS_KEY = "15d6d1cb54dab";
    public static final String SMS_SECRET = "a49e1a16c5eb9460b6db56537897a821";

    public static class API {

        public static final String BASE_URL = "http://112.124.22.238:8081/course_api/";
        public static final String BANNER_URL = BASE_URL + "banner/query";
        public static final String CAMPAIGN_HOME_URL = BASE_URL + "campaign/recommend";
        public static final String WARES_HOT_URL = BASE_URL + "wares/hot";
        public static final String CATEGORY_LIST_URL = BASE_URL +"category/list";
        public static final String WARES_LIST = BASE_URL +"wares/list";
        public static final String WARES_CAMPAIN_LIST = BASE_URL +"wares/campaign/list";

        public static final String WARES_DETAIL=BASE_URL +"wares/detail.html";
        public static final String LOGIN=BASE_URL +"auth/login";

    }
}
