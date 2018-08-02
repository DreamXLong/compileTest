package com.classroomsdk;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/9.
 */

public interface WBStateCallBack {
    void onPageFinished();

    void onRoomDocChange(boolean isdel, boolean ismedia);

    void onWhiteBoradZoom(boolean isZoom);

    void onWhiteBoradMediaPublish(String url, boolean isvideo, long fileid);

    void onWhiteBoradReceiveActionCommand(String jsonObject);

    void onWhiteBoradSetProperty(String jsonProperty);
}
