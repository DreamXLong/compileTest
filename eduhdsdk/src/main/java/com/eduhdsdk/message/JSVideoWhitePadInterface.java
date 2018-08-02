package com.eduhdsdk.message;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2017/5/3.
 */

public class JSVideoWhitePadInterface {

    private static String sync = "";
    static private JSVideoWhitePadInterface mInstance = null;
    public static boolean isClassbegin = false;

    static public JSVideoWhitePadInterface getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new JSVideoWhitePadInterface();
            }
            return mInstance;
        }
    }

    private com.eduhdsdk.message.VideoWBCallback callBack;

    public void setVideoWBCallBack(com.eduhdsdk.message.VideoWBCallback wbCallBack) {
        this.callBack = wbCallBack;
    }

    @org.xwalk.core.JavascriptInterface
    public void pubMsg(String js) {
        if (callBack != null)
            callBack.pubMsg(js);

    }

    @org.xwalk.core.JavascriptInterface
    public void delMsg(String js) {
        if (callBack != null)
            callBack.delMsg(js);

    }

    @org.xwalk.core.JavascriptInterface
    public void onPageFinished(String temp) {
        if (callBack != null)
            callBack.onPageFinished();
    }

    @org.xwalk.core.JavascriptInterface
    public void printLogMessage(String msg) {
        Log.d("emm", msg);
    }


    @org.xwalk.core.JavascriptInterface
    public void exitAnnotation(String state) {
        if (callBack != null) {
            callBack.exitAnnotation(state);
        }
    }

    @org.xwalk.core.JavascriptInterface
    public void saveValueByKey(String data) {
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsData = new JSONObject(data);
                String key = jsData.optString("key");
                String value = jsData.optString("value");
                if (callBack != null) {
                    callBack.saveValueByKey(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @org.xwalk.core.JavascriptInterface
    public void getValueByKey(String data) {
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject jsData = new JSONObject(data);
                String key = jsData.optString("key");
                int callbackID = jsData.optInt("callbackID");
                if (callBack != null) {
                    callBack.getValueByKey(key, callbackID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
