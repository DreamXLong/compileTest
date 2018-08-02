package com.classroomsdk;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.talkcloud.roomsdk.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/3.
 */

public class JSWhitePadInterface {

    private static String sync = "";
    static private JSWhitePadInterface mInstance = null;
    public static boolean isClassbegin = false;

    static public JSWhitePadInterface getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new JSWhitePadInterface();
            }
            return mInstance;
        }
    }

    private WBCallback callBack;

    public void setWBCallBack(WBCallback wbCallBack) {
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
    public void changeWebPageFullScreen(String isFull) {
        if (callBack != null) {
            callBack.changePageFullScreen(isFull);
        }
    }

    @org.xwalk.core.JavascriptInterface
    public void unpublishNetworkMedia(String jsonObject) {
        if (callBack != null) {

        }
    }

    @org.xwalk.core.JavascriptInterface
    public void publishNetworkMedia(final String videoData) {
        try {
            JSONObject jsdata = new JSONObject(videoData);
            String url = jsdata.optString("url");
            JSONObject attributes = jsdata.optJSONObject("attributes");
            String source = jsdata.optString("source");

            long fileid = ((Number) attributes.opt("fileid")).longValue();
            boolean isvideo = Tools.isTure(jsdata.opt("video"));
            if (TKRoomManager.getInstance().getMySelf().role == 2 && !TKRoomManager.getInstance().getMySelf().canDraw) {
                return;
            }
            boolean issuccess = TKRoomManager.getInstance().stopShareMedia();
            if (!issuccess) {
                TKRoomManager.isMediaPublishing = true;
                if (isClassbegin) {
                    HashMap<String, Object> attrMap = new HashMap<String, Object>();
                    attrMap.put("filename", "");
                    attrMap.put("fileid", fileid);
                    attrMap.put("source", source);
                    TKRoomManager.getInstance().startShareMedia(url, isvideo, "__all", attrMap);
                } else {
                    HashMap<String, Object> attrMap = new HashMap<String, Object>();
                    attrMap.put("filename", "");
                    attrMap.put("fileid", fileid);
                    attrMap.put("source", source);
                    TKRoomManager.getInstance().startShareMedia(url, isvideo, TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                }
            }
            if (callBack != null) {
                callBack.onJsPlay(url, isvideo, fileid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @org.xwalk.core.JavascriptInterface
    public void sendActionCommand(String jsonObject) {
        if (callBack != null) {
            try {
                JSONObject json = new JSONObject(jsonObject);
                if (json.optString("action").equals("viewStateUpdate")) {
                    String stateJson = json.optString("cmd");
                    callBack.receiveJSActionCommand(stateJson);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @org.xwalk.core.JavascriptInterface
    public void setProperty(String jsonProperty) {
        if (callBack != null) {
            callBack.setProperty(jsonProperty);
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
