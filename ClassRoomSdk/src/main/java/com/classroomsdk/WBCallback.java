package com.classroomsdk;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/11.
 */

public interface WBCallback {

    void pubMsg(String js);

    void delMsg(String js);

    void onPageFinished();

    void changePageFullScreen(String isFull);

    void onJsPlay(String url, boolean isvideo, long fileid);

    void receiveJSActionCommand(String stateJson);

    void setProperty(String jsonProperty);

    void saveValueByKey(String key,String value);

    void getValueByKey(String key,int callBackId);

}
