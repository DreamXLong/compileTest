package com.eduhdsdk.message;

import com.talkcloud.roomsdk.RoomUser;

/**
 * Created by Administrator on 2018/7/5/005.
 */

public interface RoomUserCallBack {

    void onSuccess(RoomUser roomUser);

    void onFailure(String errorMessage);
}
