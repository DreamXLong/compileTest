package com.classroomsdk;

import android.util.Log;

import com.talkcloud.roomsdk.IRoomWhiteBoard;
import com.talkcloud.roomsdk.RoomUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2018/5/15.
 */

public class WBSession implements IRoomWhiteBoard {

    public static final int onCheckRoom = 101;
    public static final int onFileList = 102;
    public static final int onRemoteMsg = 103;
    public static final int onRoomConnected = 104;
    public static final int onUserJoined = 105;
    public static final int onUserLeft = 106;
    public static final int onUserChanged = 107;
    public static final int onRoomLeaved = 108;
    public static final int onPlayBackClearAll = 109;
    public static final int onRoomConnectFaild = 110;
    public static final int participantEvicted = 111;
    public static final int duration = 112;
    public static final int playbackEnd = 113;
    public static final int playback_updatetime = 114;
    public static final int participantPublished = 115;

    public static boolean isPageFinish = false;

    private ConcurrentHashMap<Integer, ArrayList<Object[]>> messageBuffer = new ConcurrentHashMap<>();

    private static String sync = "";
    static private WBSession mInstance = null;
    //buffer params
    JSONObject jsonObject = null;

    static public WBSession getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new WBSession();
            }
            return mInstance;
        }
    }

    @Override
    public void onCheckRoom(JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onCheckRoom, jsonObject);
        } else {
            addMessageToBuffer(onCheckRoom, jsonObject);
        }
    }

    @Override
    public void onFileList(Object o) {
        ArrayList<ShareDoc> docList = new ArrayList<ShareDoc>();
        ShareDoc mBlankShareDoc = new ShareDoc();
        mBlankShareDoc.setFileid(0);
        mBlankShareDoc.setCurrentPage(1);
        mBlankShareDoc.setPagenum(1);
        mBlankShareDoc.setFilename("白板");
        mBlankShareDoc.setFiletype("whiteboard");
        mBlankShareDoc.setSwfpath("");
        mBlankShareDoc.setPptslide(1);
        mBlankShareDoc.setPptstep(0);
        mBlankShareDoc.setSteptotal(0);
        mBlankShareDoc.setGeneralFile(true);
        mBlankShareDoc.setDynamicPPT(false);
        mBlankShareDoc.setH5Docment(false);
        mBlankShareDoc.setMedia(false);
        WhiteBoradManager.getInstance().addDocList(mBlankShareDoc);
        try {
            JSONArray jsobjs = new JSONArray(o.toString());
            for (int i = 0; i < jsobjs.length(); i++) {
                JSONObject jsobj = jsobjs.optJSONObject(i);
                ShareDoc doc = new ShareDoc();

                doc.setPdfpath(jsobj.optString("pdfpath"));
                doc.setFilepath(jsobj.optString("filepath"));
                doc.setAnimation(jsobj.optInt("animation"));
                doc.setStatus(jsobj.optInt("status"));
                doc.setDownloadpath(jsobj.optString("downloadpath"));
                doc.setPagenum(jsobj.optInt("pagenum"));
                doc.setUploadusername(jsobj.optString("uploadusername"));
                doc.setNewfilename(jsobj.optString("newfilename"));
                doc.setUploaduserid(jsobj.optInt("uploaduserid"));
                doc.setSwfpath(jsobj.optString("swfpath"));
                doc.setFileid(jsobj.optInt("fileid"));
                doc.setIsconvert(jsobj.optInt("isconvert"));
                doc.setSize(jsobj.optInt("size"));
                doc.setCompanyid(jsobj.optInt("companyid"));
                doc.setFileserverid(jsobj.optInt("fileserverid"));
                doc.setUploadtime(jsobj.optString("uploadtime"));
                doc.setActive(jsobj.optInt("active"));
                doc.setFilename(jsobj.optString("filename"));
                doc.setFiletype(jsobj.optString("filetype"));
                doc.setCurrentPage(1);
                doc.setCurrentTime(jsobj.optDouble("currenttime"));
                doc.setType(jsobj.optInt("type"));
                doc.setMedia(getIsMedia(doc.getFilename()));
                doc.setFileprop(jsobj.optInt("fileprop"));
                doc.setFilecategory(jsobj.optInt("filecategory"));
                doc.setDynamicPPT(doc.getFileprop() == 2);
                doc.setGeneralFile(doc.getFileprop() == 0);
                doc.setH5Docment(doc.getFileprop() == 3);
//                if(doc.getFileprop() != 1){
                WhiteBoradManager.getInstance().addDocList(doc);
//                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationCenter.getInstance().postNotificationName(onFileList);
    }

    @Override
    public boolean onRemoteMsg(boolean add, String id, String name, long ts, Object data, String fromID, String associatedMsgID, String associatedUserID, JSONObject serverDate) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onRemoteMsg, add, id, name, ts, data, fromID, associatedMsgID, associatedUserID, serverDate);
        } else {
            addMessageToBuffer(onRemoteMsg, add, id, name, ts, data, fromID, associatedMsgID, associatedUserID, serverDate);
        }
        if (name.equals("WBPageCount") || name.equals("SharpsChange")) {
            return true;
        }
        return false;
    }

    @Override
    public void onRoomConnected(JSONArray jsonArray, List list, JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onRoomConnected, jsonArray, list, jsonObject);
        } else {
            addMessageToBuffer(onRoomConnected, jsonArray, list, jsonObject);
        }
    }

    @Override
    public void onUserJoined(RoomUser roomUser, boolean inList, JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onUserJoined, roomUser, inList, jsonObject);
        } else {
            addMessageToBuffer(onUserJoined, roomUser, inList, jsonObject);
        }
    }

    @Override
    public void onUserLeft(RoomUser roomUser, String peerid) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onUserLeft, roomUser, peerid);
        } else {
            addMessageToBuffer(onUserLeft, roomUser, peerid);
        }
    }

    @Override
    public void onUserChanged(RoomUser roomUser, Map<String, Object> map, String fromId, JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onUserChanged, roomUser, map, fromId, jsonObject);
        } else {
            addMessageToBuffer(onUserChanged, roomUser, map, fromId, jsonObject);
        }
    }

    @Override
    public void onRoomLeaved() {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onRoomLeaved);
        } else {
            addMessageToBuffer(onRoomLeaved);
        }
    }

    @Override
    public void onPlayBackClearAll() {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onPlayBackClearAll);
        } else {
            addMessageToBuffer(onPlayBackClearAll);
        }
    }

    @Override
    public void onRoomConnectFaild() {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(onRoomConnectFaild);
        } else {
            addMessageToBuffer(onRoomConnectFaild);
        }
    }

    @Override
    public void participantEvicted(JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(participantEvicted, jsonObject);
        } else {
            addMessageToBuffer(participantEvicted, jsonObject);
        }
    }

    @Override
    public void duration(JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(duration, jsonObject);
        } else {
            addMessageToBuffer(duration, jsonObject);
        }
    }

    @Override
    public void playbackEnd() {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(playbackEnd);
        } else {
            addMessageToBuffer(playbackEnd);
        }
    }

    @Override
    public void playback_updatetime(JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(playback_updatetime, jsonObject);
        } else {
            addMessageToBuffer(playback_updatetime, jsonObject);
        }
    }

    @Override
    public void participantPublished(RoomUser roomUser, JSONObject jsonObject) {
        if (isPageFinish) {
            onPageFinished();
            NotificationCenter.getInstance().postNotificationName(participantPublished, jsonObject);
        } else {
            addMessageToBuffer(participantPublished, jsonObject);
        }
    }

    private boolean getIsMedia(String filename) {
        boolean isMedia = false;
        if (filename.toLowerCase().endsWith(".mp3")
                || filename.toLowerCase().endsWith("mp4")
                || filename.toLowerCase().endsWith("webm")
                || filename.toLowerCase().endsWith("ogg")
                || filename.toLowerCase().endsWith("wav")) {
            isMedia = true;
        }
        return isMedia;
    }

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

    public void onPageFinished() {
        isPageFinish = true;
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

    public void onRelease() {
        isPageFinish = false;
        messageBuffer.clear();
    }
}
