package com.eduhdsdk.tools;

import android.util.Log;

import com.eduhdsdk.interfaces.TranslateCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/6/5.
 */

public class Translate {

    private static String sync = "";
    static private Translate mInstance = null;
//    private static final String APP_ID = "20170605000052251";//自己注册的
    private static final String APP_ID = "20180130000119815";//斌哥注册的

//    private static final String SECURITY_KEY = "sYlf3rTdnEGTOKr1FuT1";
    private static final String SECURITY_KEY = "MeLC5NI37txuT_wtTd0B";
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private TranslateCallback callback;

    public void setCallback(TranslateCallback callback) {
        this.callback = callback;
    }

    static public Translate getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new Translate();
            }
            return mInstance;
        }
    }

    public void translate(final int index, String query) {
        RequestParams params = new RequestParams();
        params.put("q", query);
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(query);
        String result = "";
        if (matcher.find()) {
            params.put("from", "zh");
            params.put("to", "en");
        } else {
            params.put("from", "en");
            params.put("to", "zh");
        }

        params.put("appid", APP_ID);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = APP_ID + query + salt + SECURITY_KEY; // 加密前的原文
        params.put("sign", MD5.md5(src));
        client.get(TRANS_API_HOST, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                try {
//                    int nRet = response.getInt("result");
                    JSONArray arr = response.optJSONArray("trans_result");
                    JSONObject resultObj = arr.optJSONObject(0);
                    String src = resultObj.optString("src");
                    String result = resultObj.optString("dst");

                    if (callback != null) {
                        callback.onResult(index, result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("emm", "error");
            }
        });
    }
}
