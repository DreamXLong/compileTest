package com.eduhdsdk.message;

import android.app.UiModeManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.classroomsdk.NotificationCenter;
import com.classroomsdk.WhiteBoradManager;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.ChatData;
import com.eduhdsdk.tools.SoundPlayUtils;
import com.eduhdsdk.tools.Tools;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.talkcloud.roomsdk.RoomControler;
import com.talkcloud.roomsdk.RoomUser;
import com.talkcloud.roomsdk.Stream;
import com.talkcloud.roomsdk.TKRoomManager;
import com.talkcloud.roomsdk.TKRoomManagerObserver;
import com.talkcloud.roomsdk.TkAudioStatsReport;
import com.talkcloud.roomsdk.TkVideoStatsReport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tkwebrtc.MediaCodecVideoEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import static org.chromium.base.ThreadUtils.runOnUiThread;

/**
 * Created by Administrator on 2017/12/12.
 */

public class RoomSession implements TKRoomManagerObserver {

    public static final int onRoomJoin = 1;
    public static final int onRoomLeave = 2;
    public static final int onError = 3;
    public static final int onWarning = 4;
    public static final int onUserJoin = 5;
    public static final int onUserLeft = 6;
    public static final int onUserPropertyChanged = 7;
    public static final int onUserPublishState = 8;
    public static final int onKickedout = 9;
    public static final int onMessageReceived = 10;
    public static final int onRemotePubMsg = 11;
    public static final int onRemoteDelMsg = 12;
    public static final int onUpdateAttributeStream = 13;
    public static final int onPlayBackClearAll = 14;
    public static final int onPlayBackUpdateTime = 15;
    public static final int onPlayBackDuration = 16;
    public static final int onPlayBackEnd = 17;
    public static final int onShareMediaState = 18;
    public static final int onShareScreenState = 19;
    public static final int onShareFileState = 20;
    public static final int onAudioVolume = 21;
    public static final int onRoomUser = 22;
    public static final int onRoomUserNumber = 23;

    public static boolean isClassBegin = false;
    public static HashSet<String> publishSet = new HashSet<String>();
    public static HashSet<String> pandingSet = new HashSet<String>();
    public static JSONArray jsVideoWBTempMsg = new JSONArray();
    public static boolean isShowVideoWB = false;
    public static List<ChatData> chatDataCache = new ArrayList<ChatData>();
    private AsyncHttpClient client = new AsyncHttpClient();
    private Context context;

    public static HashMap<String, Object> params = new HashMap<String, Object>();
    public static HashMap<String, Boolean> playingMap = new HashMap<String, Boolean>();
    public static List<RoomUser> playingList = Collections.synchronizedList(new ArrayList<RoomUser>());
    public static ArrayList<RoomUser> memberList = new ArrayList<RoomUser>();
    public static ArrayList<ChatData> chatList = new ArrayList<ChatData>();

    //room params
    public static String host = "";
    public static int port = 80;
    public static String nickname = "";
    public static String userid = "";
    public static String serial = "";
    public static String password = "";
    public static int userrole = -1;
    public static String path = "";
    public static int type = -1;
    public static String servername;
    public static String param = "";
    public static String domain = "";
    public static String mobilename = "";
    public static boolean mobilenameNotOnList = true;

    public static long classStartTime, serviceTime, localTime, syetemTime;
    public static Timer timerAddTime, timerbefClassbegin, timerAfterLeaved, tSendGift, numberTimer;

    private boolean isToast = false;
    private String sendgiftlock = "sendgiftlock";
    private boolean isSending = false;

    private static String sync = "";
    static private RoomSession mInstance = null;
    public static boolean isPublish = false;
    public static boolean isPlay = false;
    public static int maxVideo = -1;
    public static boolean isInRoom = false;
    public static int roomType = -1;
    public static boolean _bigroom = false;

    public static boolean _possibleSpeak = true;

    private boolean isPlayBackPlay = true;
    private boolean isEnd = false;
    private boolean isActivityStart = false;

    public static int start = 0;
    public static int max = 19;

    public static boolean dragVideo = false;

    private ConcurrentHashMap<Integer, ArrayList<Object[]>> messageBuffer = new ConcurrentHashMap<>();

    static public RoomSession getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new RoomSession();
            }
            return mInstance;
        }
    }

    public void init(Context context) {
        this.context = context;
    }

    public void addTempVideoWBRemoteMsg(boolean add, String id, String name, long ts, Object data, String fromID, String associatedMsgID, String associatedUserID) {
        if (add) {
            if (name.equals("VideoWhiteboard")) {
                isShowVideoWB = true;
            }
            JSONObject jsobj = new JSONObject();
            try {
                jsobj.put("id", id);
                jsobj.put("ts", ts);
                jsobj.put("data", data == null ? null : data.toString());
                jsobj.put("name", name);
                jsobj.put("fromID", fromID);
                if (!associatedMsgID.equals("")) {
                    jsobj.put("associatedMsgID", associatedMsgID);
                }
                if (!associatedUserID.equals("")) {
                    jsobj.put("associatedUserID", associatedUserID);
                }

                if (associatedMsgID.equals("VideoWhiteboard") || id.equals("VideoWhiteboard")) {
                    jsVideoWBTempMsg.put(jsobj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            isShowVideoWB = false;
        }
    }

    public void getBigRoomUnmberAndUsers() {
        if (RoomSession._bigroom) {
            if (RoomSession.numberTimer == null) {
                RoomSession.numberTimer = new Timer();
                final int[] role = {1, 2};
                TKRoomManager.getInstance().getRoomUserNum(role, null);

                final HashMap hashMap = new HashMap<String, Object>();
                hashMap.put("ts", "asc");
                hashMap.put("role", "asc");
                TKRoomManager.getInstance().getRoomUsers(role, start, max, null, hashMap);

                RoomSession.numberTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TKRoomManager.getInstance().getRoomUserNum(role, null);
                                TKRoomManager.getInstance().getRoomUsers(role, start, max, null, hashMap);
                            }
                        });
                    }
                }, 2000, 1000);
            }
        }
    }

    public void getGiftNum(String roomNum, final String peerId) {

        String url = "http://" + host + ":" + port + "/ClientAPI/getgiftinfo";
        RequestParams params = new RequestParams();
        params.put("serial", roomNum);
        params.put("receiveid", peerId);

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                try {
                    int nRet = response.getInt("result");
                    if (nRet == 0) {
                        JSONArray infos = response.optJSONArray("giftinfo");
                        JSONObject info = infos.getJSONObject(0);
                        final long gifnum = info.optInt("giftnumber", 0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "giftnumber", gifnum);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("emm", "error=" + throwable.toString());
            }
        });
    }

    public void getSystemTime() {
        String timeUrl = "http://" + host + ":" + port + "/ClientAPI/systemtime";
        client.post(timeUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                try {
                    syetemTime = response.getLong("time");

                    if (TKRoomManager.getInstance().getRoomProperties() != null) {
                        if (TKRoomManager.getInstance().getRoomProperties().getLong("endtime") <= syetemTime) {
                            Toast.makeText(context, context.getString(R.string.checkmeeting_error_5001), Toast.LENGTH_SHORT).show();
                            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                                RoomClient.getInstance().setExit(true);
                                TKRoomManager.getInstance().leaveRoom();
                            }
                            return;
                        } else if (TKRoomManager.getInstance().getRoomProperties().getLong("endtime") > syetemTime &&
                                TKRoomManager.getInstance().getRoomProperties().getLong("endtime") - 5 * 60 <= syetemTime
                                ) {
                            long time = TKRoomManager.getInstance().getRoomProperties().getLong("endtime") - syetemTime;
                            if (time >= 60) {
                                Toast.makeText(context, time / 60 + context.getString(R.string.end_class_tip_minute), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, time + context.getString(R.string.end_class_tip_second), Toast.LENGTH_SHORT).show();
                            }
                            startClass();
                        } else {
                            startClass();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("emm", "error");
                RoomClient.getInstance().joinRoomcallBack(-1);
            }
        });
    }

    public void getSystemNowTime() {
        if (timerAfterLeaved != null) {
            timerAfterLeaved.cancel();
            timerAfterLeaved = null;
        }
        timerAfterLeaved = new Timer();

        String timeUrl = "http://" + host + ":" + port + "/ClientAPI/systemtime";

        client.post(timeUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                try {
                    if (TKRoomManager.getInstance().getRoomProperties() != null) {
                        syetemTime = response.optLong("time");
                        final long endClassTime = TKRoomManager.getInstance().getRoomProperties().optLong("endtime");
                        if (endClassTime > syetemTime) {

                            long time = TKRoomManager.getInstance().getRoomProperties().optLong("endtime") - syetemTime;
                            if (time <= 5 * 60) {
                                if (time - 5 * 60 == syetemTime) {
                                    isToast = true;
                                }
                                if (time >= 60) {
                                    Toast.makeText(context, time / 60 + context.getString(R.string.end_class_tip_minute), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, time + context.getString(R.string.end_class_tip_second), Toast.LENGTH_SHORT).show();
                                }
                            }

                            timerAfterLeaved.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    syetemTime += 1;

                                    if (endClassTime - syetemTime == 5 * 60 && !isToast) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, context.getString(R.string.end_class_time), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    if (endClassTime == syetemTime) {
                                        if (timerAfterLeaved != null) {
                                            timerAfterLeaved.cancel();
                                            timerAfterLeaved = null;
                                        }
                                        RoomClient.getInstance().setExit(true);
                                        TKRoomManager.getInstance().leaveRoom();
                                    }
                                }
                            }, 1000, 1000);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("emm", "error");
                RoomClient.getInstance().joinRoomcallBack(-1);
            }
        });
    }

    /**
     * 发送奖杯
     *
     * @param userMap
     * @param map
     */
    public void sendGift(final HashMap<String, RoomUser> userMap, final Map<String, Object> map) {
        synchronized (sendgiftlock) {
            if (isSending) {
                return;
            }
            isSending = true;
            tSendGift = new Timer();
            tSendGift.schedule(new TimerTask() {
                int count = 0;

                @Override
                public void run() {
                    if (count == 2) {
                        isSending = false;
                        tSendGift.cancel();
                    } else {
                        count++;
                    }
                }
            }, 0, 1000);

            String url = "http://" + host + ":" + port + "/ClientAPI/sendgift";
            RequestParams params = new RequestParams();
            params.put("serial", TKRoomManager.getInstance().getRoomProperties().optString("serial"));
            params.put("sendid", TKRoomManager.getInstance().getMySelf().peerId);
            params.put("sendname", TKRoomManager.getInstance().getMySelf().nickName);
            HashMap<String, String> js = new HashMap<String, String>();
            for (RoomUser u : userMap.values()) {
                js.put(u.peerId, u.nickName);
            }
            params.put("receivearr", js);
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                    try {
                        int nRet = response.getInt("result");
                        if (nRet == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (RoomUser u : userMap.values()) {
                                        long giftnumber = 0;
                                        if (u.properties.containsKey("giftnumber")) {
                                            giftnumber = u.properties.get("giftnumber") instanceof Integer ? (int) u.properties.get("giftnumber") : (long) u.properties.get("giftnumber");
                                        }
                                        giftnumber++;

                                        HashMap<String, Object> gift_send_data = new HashMap<String, Object>();
                                        gift_send_data.put("giftnumber", giftnumber);
                                        if (map != null) {
                                            gift_send_data.put("giftinfo", map);
                                        }
                                        TKRoomManager.getInstance().changeUserProperty(u.peerId, "__all", gift_send_data);
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("emm", "error=" + throwable.toString());
                }
            });
        }
    }

    private void sendClassBeginToPhp() {
        if (!(TKRoomManager.getInstance().getMySelf().role == 0)) {
            return;
        }
        String webFun_controlroom = "http://" + host + ":" + port + "/ClientAPI" + "/roomstart";
        RequestParams params = new RequestParams();
        try {
            params.put("serial", TKRoomManager.getInstance().getRoomProperties().get("serial"));
            params.put("companyid", TKRoomManager.getInstance().getRoomProperties().get("companyid"));
            client.post(webFun_controlroom, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                    try {
                        int nRet = response.getInt("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("emm", "error=" + throwable.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendClassDissToPhp() {
        String webFun_controlroom = "http://" + host + ":" + port + "/ClientAPI" + "/roomover";
        RequestParams params = new RequestParams();
        try {
            params.put("act", 3);
            params.put("serial", TKRoomManager.getInstance().getRoomProperties().get("serial"));
            params.put("companyid", TKRoomManager.getInstance().getRoomProperties().get("companyid"));
            client.post(webFun_controlroom, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                    try {
                        int nRet = response.getInt("result");
                        if (nRet != 0) {
                            Log.e("demo", "下课接口调用失败，失败数据：");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("emm", "error=" + throwable.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startClass() {
        TKRoomManager.getInstance().stopShareMedia();
//        lin_audio_control.setVisibility(View.INVISIBLE);
        try {
            long expires = TKRoomManager.getInstance().getRoomProperties().optLong("endtime") + 5 * 60;
            if (RoomControler.isNotLeaveAfterClass()) {
                TKRoomManager.getInstance().delMsg("__AllAll", "__AllAll", "__none", new HashMap<String, Object>());
            }
            TKRoomManager.getInstance().pubMsg("ClassBegin", "ClassBegin", "__all", new JSONObject().put("recordchat", true).toString(), true, expires);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendClassBeginToPhp();
    }

    public void releaseTimer() {
        if (timerAddTime != null) {
            timerAddTime.cancel();
            timerAddTime = null;
        }
        if (timerbefClassbegin != null) {
            timerbefClassbegin.cancel();
            timerbefClassbegin = null;
        }

        if (timerAfterLeaved != null) {
            timerAfterLeaved.cancel();
            timerAfterLeaved = null;
        }
    }

    @Override
    public void onRoomJoined() {

        if (TKRoomManager.getInstance().getRoomProperties().has("vcodec")) {
            int vcodec = TKRoomManager.getInstance().getRoomProperties().optInt("vcodec");
            switch (vcodec) {
                case 0:
                    if (!MediaCodecVideoEncoder.isVp8HwSupported() && TKRoomManager.getInstance().get_room_video_width() > 640)
                        TKRoomManager.getInstance().setVideoProfile(640, 480);
                    break;
                case 1:
                    if (!MediaCodecVideoEncoder.isVp9HwSupported() && TKRoomManager.getInstance().get_room_video_width() > 640)
                        TKRoomManager.getInstance().setVideoProfile(640, 480);
                    break;
                case 2:
                    if (!MediaCodecVideoEncoder.isH264HwSupported() && TKRoomManager.getInstance().get_room_video_width() > 640)
                        TKRoomManager.getInstance().setVideoProfile(640, 480);
                    break;
            }
        }

        closeSpeaker();

        if (RoomControler.haveTimeQuitClassroomAfterClass()) {
            if (timerAfterLeaved != null) {
                timerAfterLeaved.cancel();
                timerAfterLeaved = null;
            } else {
                timerAfterLeaved = new Timer();
            }
            getSystemNowTime();
        }
        WhiteBoradManager.getInstance().setUserrole(TKRoomManager.getInstance().getMySelf().role);
        getGiftNum(serial, TKRoomManager.getInstance().getMySelf().peerId);

        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            TKRoomManager.getInstance().pubMsg("UpdateTime", "UpdateTime", "__all", null, false, null, null);
        }

        try {
            maxVideo = TKRoomManager.getInstance().getRoomProperties().getInt("maxvideo");
            if (maxVideo <= 7) {
                maxVideo = 7;
            } else {
                maxVideo = 13;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isInRoom = true;
        roomType = TKRoomManager.getInstance().getRoomType();

        TKRoomManager.getInstance().getMySelf().nickName = StringEscapeUtils.unescapeHtml4(TKRoomManager.getInstance().getMySelf().nickName);
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRoomJoin);
        } else {
            addMessageToBuffer(onRoomJoin);
        }
    }

    public void closeSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isWiredHeadsetOn()) {
                    TKRoomManager.getInstance().useLoudSpeaker(false);
                } else {
                    TKRoomManager.getInstance().useLoudSpeaker(true);
                    openSpeaker();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开扬声器
     */
    private void openSpeaker() {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() != Configuration.UI_MODE_TYPE_TELEVISION) {
            try {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(AudioManager.ROUTE_SPEAKER);
                int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                if (!audioManager.isSpeakerphoneOn()) {
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                            audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                            AudioManager.STREAM_VOICE_CALL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRoomLeaved() {
        if (TKRoomManager.getInstance().getTrophyList() != null && TKRoomManager.getInstance().getTrophyList().size() > 0) {
            SoundPlayUtils.releaseTrophy();
        } else {
            SoundPlayUtils.release();
        }
        WhiteBoradManager.getInstance().clear();
        publishSet.clear();
        isClassBegin = false;
        TKRoomManager.isMediaPublishing = false;
        isPublish = false;
        isPlay = false;
        isInRoom = false;
        _bigroom = false;
        playingMap.clear();
        playingList.clear();
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRoomLeave);
        } else {
            addMessageToBuffer(onRoomLeave);
        }
    }

    @Override
    public void onError(int errorCode, String errMsg) {
        if (errorCode == 3001 || errorCode == 3002 || errorCode == 3003 || errorCode == 4007 || errorCode == 4008
                || errorCode == 4110 || errorCode == 0 || errorCode == 4012 || errorCode == 3) {
            RoomClient.getInstance().joinRoomcallBack(errorCode);
        } else if (errorCode == 10002) {
            RoomClient.getInstance().setExit(true);
            WhiteBoradManager.getInstance().clear();
            playingMap.clear();
            playingList.clear();
            publishSet.clear();
            isClassBegin = false;
            isInRoom = false;
            TKRoomManager.isMediaPublishing = false;
            isPublish = false;
            isPlay = false;
            RoomClient.getInstance().joinRoomcallBack(-1);
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onError, errorCode, errMsg);
        } else {
            addMessageToBuffer(onError, errorCode, errMsg);
        }
    }

    @Override
    public void onWarning(int warning) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onWarning, warning);
        } else {
            addMessageToBuffer(onWarning, warning);
        }
    }

    @Override
    public void onUserJoined(RoomUser roomUser, boolean inList) {
        roomUser.nickName = StringEscapeUtils.unescapeHtml4(roomUser.nickName);
        ChatData ch = new ChatData();
        ch.setState(1);
        ch.setInOut(true);
        ch.setStystemMsg(true);
        ch.setUser(roomUser);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        ch.setTime(str);
        if (roomUser.role != 4) {
            chatList.add(ch);
        }
        if (inList && roomUser.role != 4) {
            if (roomUser.role == 0 && TKRoomManager.getInstance().getMySelf().role == 0 ||
                    (TKRoomManager.getInstance().getRoomType() == 0 && roomUser.role == TKRoomManager.getInstance().getMySelf().role)) {
                TKRoomManager.getInstance().evictUser(roomUser.peerId);
            }
        }
        if (roomUser.properties.containsKey("isInBackGround") && TKRoomManager.getInstance().getMySelf().role != 2) {
            if (roomUser == null) {
                return;
            }
            boolean isinback = Tools.isTure(roomUser.properties.get("isInBackGround"));
            ChatData ch2 = new ChatData();
            ch.setState(2);
            ch.setHold(isinback);
            ch.setStystemMsg(true);
            ch.setUser(roomUser);
            SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
            Date curDate2 = new Date(System.currentTimeMillis());//获取当前时间
            String str2 = formatter2.format(curDate2);
            ch.setTime(str2);
            if (roomUser.role != 4) {
                chatList.add(ch2);
            }
        }
        getMemberList();
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUserJoin, roomUser, inList);
        } else {
            addMessageToBuffer(onUserJoin, roomUser, inList);
        }
    }

    private void getMemberList() {
        memberList.clear();
        for (RoomUser u : TKRoomManager.getInstance().getUsers().values()) {
            if (!u.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && (u.role == 2 || u.role == 1)) {
                if (u.role == 1) {
                    memberList.add(0, u);
                } else {
                    memberList.add(u);
                }
            }
        }
    }

    @Override
    public void onUserLeft(RoomUser roomUser) {
        ChatData ch = new ChatData();
        ch.setState(1);
        ch.setInOut(false);
        ch.setStystemMsg(true);
        ch.setUser(roomUser);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        ch.setTime(str);
        if (roomUser != null && roomUser.role != 4) {
            chatList.add(ch);
        }
        RoomSession.publishSet.remove(roomUser.peerId);
        getMemberList();
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUserLeft, roomUser);
        } else {
            addMessageToBuffer(onUserLeft, roomUser);
        }
    }

    @Override
    public void onUserPropertyChanged(RoomUser roomUser, Map<String, Object> map, String fromId) {
        pandingSet.remove(roomUser.peerId);
        if (map.containsKey("isInBackGround")) {
            boolean isinback = Tools.isTure(map.get("isInBackGround"));
            if (TKRoomManager.getInstance().getMySelf().role != 2) {
                ChatData ch = new ChatData();
                ch.setState(2);
                ch.setHold(isinback);
                ch.setStystemMsg(true);
                ch.setUser(roomUser);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                ch.setTime(str);
                if (roomUser != null && roomUser.role != 4) {
                    chatList.add(ch);
                }
                getMemberList();
            }
        }

        if (playingMap.containsKey(roomUser.peerId) && roomUser.publishState > 0) {
            getMemberList();
        }

        if (roomUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("servername") && !fromId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
            String servername = (String) map.get("servername");
            SharedPreferences sp = context.getSharedPreferences("classroom", context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("servername", servername);
            editor.commit();
            TKRoomManager.getInstance().switchService(servername);
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUserPropertyChanged, roomUser, map, fromId);
          /*  if (messageBuffer.size() > 0) {
                addMessageToBuffer(onUserPropertyChanged, roomUser, map, fromId);
            } else {
                NotificationCenter.getInstance().postNotificationName(onUserPropertyChanged, roomUser, map, fromId);
            }*/
        } else {
            addMessageToBuffer(onUserPropertyChanged, roomUser, map, fromId);
        }
    }

    @Override
    public void onUserPublishState(String peerId, int state) {
        RoomUser roomUser = TKRoomManager.getInstance().getUser(peerId);
        if (roomUser != null) {
            roomUser.properties.remove("passivityPublish");
        }
        publishSet.remove(peerId);

        if (state > 0) {
            playingMap.put(peerId, state > 1 && state < 4);
            if (state >= 1) {
                publishSet.add(peerId);
            }
        } else {
            playingMap.remove(peerId);
        }

        getPlayingList();

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUserPublishState, peerId, state);
        } else {
            addMessageToBuffer(onUserPublishState, peerId, state);
        }
    }

    public void getPlayingList() {
        playingList.clear();
        for (String p : playingMap.keySet()) {
            RoomUser u = TKRoomManager.getInstance().getUser(p);
            if (playingList.size() <= RoomSession.maxVideo && u != null) {
                if (u.role == 0) {
                    playingList.add(0, u);
                } else {
                    playingList.add(u);
                }
            }
        }
    }

    @Override
    public void onKickedout(int reason) {
        RoomClient.getInstance().setExit(true);
        TKRoomManager.getInstance().leaveRoom();
        RoomClient.getInstance().kickout(reason == 1 ? RoomClient.Kickout_ChairmanKickout : RoomClient.Kickout_Repeat);
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onKickedout, reason);
        } else {
            addMessageToBuffer(onKickedout, reason);
        }
    }

    @Override
    public void onMessageReceived(RoomUser roomUser, JSONObject jsonObject, long ts) {
        ChatData ch = new ChatData();
        ch.setUser(roomUser);
        ch.setStystemMsg(false);
        ch.setMsgTime(System.currentTimeMillis());
        int type = jsonObject.optInt("type");
        if (type == 0) {
            chatDataCache.add(ch);
            ch.setMessage(jsonObject.optString("msg"));
            ch.setTrans(false);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date curDate = null;
            if (StringUtils.isEmpty(RoomSession.path)) {
                curDate = new Date(System.currentTimeMillis());//获取当前时间
            } else {
                curDate = new Date(ts);
            }
            String str = formatter.format(curDate);
            ch.setTime(str);
            chatList.add(ch);
        }

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onMessageReceived, roomUser, jsonObject, ts);
        } else {
            addMessageToBuffer(onMessageReceived, roomUser, jsonObject, ts);
        }
    }

    @Override
    public void onRemotePubMsg(String id, String name, long ts, Object data, boolean inList, String fromID,
                               String associatedMsgID, String associatedUserID) {
        RoomSession.getInstance().addTempVideoWBRemoteMsg(true, id, name, ts, data, fromID, associatedMsgID, associatedUserID);


        if (name.equals("ClassBegin")) {
            if (isClassBegin) {
                return;
            }
            isClassBegin = true;

            if (RoomControler.haveTimeQuitClassroomAfterClass()) {
                if (timerAfterLeaved != null) {
                    timerAfterLeaved.cancel();
                    timerAfterLeaved = null;
                }
            }

            classStartTime = ts;
            TKRoomManager.getInstance().pubMsg("UpdateTime", "UpdateTime", TKRoomManager.getInstance().getMySelf().peerId, null, false, null, null);
            if (timerbefClassbegin != null) {
                timerbefClassbegin.cancel();
                timerbefClassbegin = null;
            }

            if (TKRoomManager.getInstance().getMySelf().role == 0 && !inList) {
               /* TKRoomManager.getInstance().unPublishMedia();*/
                TKRoomManager.getInstance().unPlayMedia(TKRoomManager.getInstance().getMySelf().peerId);
            }

            if (userrole == 2 && RoomControler.isAutoHasDraw() && roomType == 0) {
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "candraw", true);
            }

            RoomClient.getInstance().onClassBegin();

        } else if (name.equals("BigRoom")) {
            _bigroom = true;
        } else if (name.equals("UpdateTime")) {
            if (RoomSession.isClassBegin) {
                if (timerbefClassbegin != null) {
                    timerbefClassbegin.cancel();
                    timerbefClassbegin = null;
                }
                serviceTime = ts;
                localTime = serviceTime - classStartTime;
            } else {
                if (!RoomSession.isClassBegin) {
                    if (TKRoomManager.getInstance().getMySelf().role == 0 && getfinalClassBeginMode()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        try {
                            long expires = TKRoomManager.getInstance().getRoomProperties().getLong("endtime") + 5 * 60;
                            if (RoomControler.isNotLeaveAfterClass()) {
                                TKRoomManager.getInstance().delMsg("__AllAll", "__AllAll", "__none", new HashMap<String, Object>());
                            }
                            TKRoomManager.getInstance().pubMsg("ClassBegin", "ClassBegin", "__all", new HashMap<String, Object>(), true, expires);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else if (name.equals("EveryoneBanChat")) {
            _possibleSpeak = false;
            /*if (TKRoomManager.getInstance().getMySelf().role != 0) {
                if (TKRoomManager.getInstance().getMySelf().properties != null &&
                        TKRoomManager.getInstance().getMySelf().properties.containsKey("disablechat")) {
                    if ((boolean) (TKRoomManager.getInstance().getMySelf().properties.get("disablechat"))) {
                        TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                                "__all", "disablechat", true);
                    }
                }
            }*/
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRemotePubMsg, id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID);
        } else {
            addMessageToBuffer(onRemotePubMsg, id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID);
        }
    }

    private boolean getfinalClassBeginMode() {
        boolean isauto = true;
        try {
            isauto = TKRoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035") ? false : RoomControler.isAutoClassBegin();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isauto;
    }

    @Override
    public void onRemoteDelMsg(String id, String name, long ts, Object data, boolean inList,
                               String fromID, String associatedMsgID, String associatedUserID) {
        RoomSession.getInstance().addTempVideoWBRemoteMsg(false, id, name, ts, data, fromID, associatedMsgID, associatedUserID);
        if (name.equals("ClassBegin")) {
            isClassBegin = false;

            if (!RoomControler.isNotLeaveAfterClass()) {
                if (TKRoomManager.getInstance().getMySelf().role != 0) {
                    TKRoomManager.getInstance().changeUserPublish(TKRoomManager.getInstance().getMySelf().peerId, 0);
                }
                TKRoomManager.getInstance().delMsg("__AllAll", "__AllAll", "__none", new HashMap<String, Object>());
            }

            localTime = 0;
            if (timerAddTime != null) {
                if (!RoomControler.haveTimeQuitClassroomAfterClass()) {
                    timerAddTime.cancel();
                    timerAddTime = null;
                }
            }

            RoomClient.getInstance().onClassDismiss();
           /* TKRoomManager.getInstance().unPublishMedia();*/
            TKRoomManager.getInstance().unPlayMedia(fromID);
        } else if (name.equals("EveryoneBanChat")) {
            _possibleSpeak = true;
        }

        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRemoteDelMsg, id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID);
        } else {
            addMessageToBuffer(onRemoteDelMsg, id, name, ts, data, inList, fromID, associatedMsgID, associatedUserID);
        }
    }

    @Override
    public void onUpdateAttributeStream(String peerid, long pos, boolean isPlay, HashMap<String, Object> hashMap) {

        /*if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onUpdateAttributeStream, peerid, pos, isPlay, hashMap);
        } else {
            addMessageToBuffer(onUpdateAttributeStream, peerid, pos, isPlay, hashMap);
        }*/
    }

    @Override
    public void onPlayBackClearAll() {
        isEnd = false;
        if (chatList != null) {
            chatList.clear();
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onPlayBackClearAll);
        } else {
            addMessageToBuffer(onPlayBackClearAll);
        }
    }

    @Override
    public void onPlayBackUpdateTime(long pos) {
        if (isEnd) {
            return;
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onPlayBackUpdateTime, pos);
        } else {
            addMessageToBuffer(onPlayBackUpdateTime, pos);
        }
    }

    @Override
    public void onPlayBackDuration(long startTime, long endTime) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onPlayBackDuration, startTime, endTime);
        } else {
            addMessageToBuffer(onPlayBackDuration, startTime, endTime);
        }
    }

    @Override
    public void onPlayBackEnd() {
        isPlayBackPlay = false;
        isEnd = true;
        TKRoomManager.getInstance().pausePlayback();
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onPlayBackEnd);
        } else {
            addMessageToBuffer(onPlayBackEnd);
        }
    }

    @Override
    public void onShareMediaState(String peerId, int state, Map<String, Object> attrs) {
        if (state == 0) {
            isPublish = false;
            TKRoomManager.getInstance().delMsg("VideoWhiteboard", "VideoWhiteboard", "__all", null);
        } else if (state == 1) {
            isPublish = true;
            isPlay = false;
            TKRoomManager.isMediaPublishing = false;
        }
        if (isActivityStart) {
            onStart();
            if (state == 0 && dragVideo) {
                addMessageToBuffer(onShareMediaState, peerId, state, attrs);
            } else {
                NotificationCenter.getInstance().postNotificationName(onShareMediaState, peerId, state, attrs);
            }
            /*NotificationCenter.getInstance().postNotificationName(onShareMediaState, peerId, state, attrs);*/
        } else {
            addMessageToBuffer(onShareMediaState, peerId, state, attrs);
        }
    }


    @Override
    public void onShareScreenState(String peerId, int state) {
        if (state == 0) {
            isPublish = false;
        } else if (state == 1) {
            isPublish = true;
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onShareScreenState, peerId, state);
        } else {
            addMessageToBuffer(onShareScreenState, peerId, state);
        }
    }

    @Override
    public void onShareFileState(String peerId, int state) {
        if (state == 0) {
            isPublish = false;
        } else if (state == 1) {
            isPublish = true;
        }
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onShareFileState, peerId, state);
        } else {
            addMessageToBuffer(onShareFileState, peerId, state);
        }
    }

    @Override
    public void onAudioVolume(String peerId, int volume) {
        NotificationCenter.getInstance().postNotificationName(onAudioVolume, peerId, volume);
        /*if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onAudioVolume, peerId, volume);
        } else {
            addMessageToBuffer(onAudioVolume, peerId, volume);
        }*/
    }

    @Override
    public void onPlayBackRoomJson(int code, String response) {
        RoomClient.getInstance().onPlayBackRoomJson(code, response);
    }


    @Override
    public void onVideoStatsReport(String s, TkVideoStatsReport tkVideoStatsReport) {

    }

    @Override
    public void onAudioStatsReport(String s, TkAudioStatsReport tkAudioStatsReport) {

    }

    @Override
    public void onGetRoomUsersBack(int code, ArrayList<RoomUser> arrayList) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRoomUser, code, arrayList);
        } else {
            addMessageToBuffer(onRoomUser, code, arrayList);
        }
    }

    //获取教室人数
    @Override
    public void onGetRoomUserNumBack(int code, int number) {
        if (isActivityStart) {
            onStart();
            NotificationCenter.getInstance().postNotificationName(onRoomUserNumber, code, number);
        } else {
            addMessageToBuffer(onRoomUserNumber, code, number);
        }
    }

    @Override
    public void onAudioRoomSwitch(String s, boolean b) {

    }

    @Override
    public void onFirstVideoFrame(String s, int i, int i1, int i2) {

    }

    @Override
    public void onFirstAudioFrame(String s, int i) {

    }

    /*@Override
    public void onStartSwitchAudioOnly() {

    }

    @Override
    public void onStopSwitchAudioOnly() {

    }*/

    private void addMessageToBuffer(int key, Object... args) {
        if (messageBuffer.containsKey(key) && messageBuffer.get(key) != null) {
            ArrayList<Object[]> bufValue = messageBuffer.get(key);
            bufValue.add(args);
        } else {
            ArrayList<Object[]> bufValue = new ArrayList<>();
            bufValue.add(args);
            messageBuffer.put(key, bufValue);
        }
    }

    public void onStart() {
        isActivityStart = true;
        /*Iterator<Integer> it = messageBuffer.keySet().iterator();
        while (it.hasNext()) {
            int key = it.next();
            ArrayList<Object[]> bufValue = messageBuffer.get(key);
            if (bufValue != null) {
                for (int i = 0; i < bufValue.size(); i++) {
                    NotificationCenter.getInstance().postNotificationName(key, bufValue.get(i));
                    messageBuffer.remove(key);
                }
            }
        }*/
        for (int k : messageBuffer.keySet()) {
            ArrayList<Object[]> bufValue = messageBuffer.get(k);
            if (bufValue != null) {
                for (int i = 0; i < bufValue.size(); i++) {
                    NotificationCenter.getInstance().postNotificationName(k, bufValue.get(i));
                }
            }
        }
        messageBuffer.clear();
    }

    public void onStop() {
        isActivityStart = false;
        messageBuffer.clear();
        releaseTimer();
        _possibleSpeak = true;
    }

    public int[] getRes() {
        TypedArray typedArray = context.getResources().obtainTypedArray(R.array.loading);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }
}
