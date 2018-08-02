package com.eduhdsdk.message;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.classroomsdk.WBSession;

import com.eduhdsdk.R;
import com.eduhdsdk.interfaces.CheckForUpdateCallBack;
import com.eduhdsdk.interfaces.MeetingNotify;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.ui.OldVideoActivity;
import com.eduhdsdk.ui.OneToManyActivity;
import com.eduhdsdk.ui.OneToOneActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.talkcloud.roomsdk.RoomControler;
import com.talkcloud.roomsdk.TKRoomManager;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import skin.support.SkinCompatManager;


/***
 * xiaoyang for Customer
 */
public class RoomClient {

    private MeetingNotify notify;
    private com.eduhdsdk.interfaces.JoinmeetingCallBack callBack;
    private static String sync = "";
    static private RoomClient mInstance = null;
    public static int Kickout_ChairmanKickout = 0;
    public static int Kickout_Repeat = 1;
    private static AsyncHttpClient client = new AsyncHttpClient();

    private int port = 80;
    private String host;
    private String serial;
    private String nickname;
    private String userid;
    private String password;
    private String param;
    private String domain;
    private String servername;
    private String path;
    private String finalnickname;
    private String clientType;

    private Activity activity;
    private int type = 3;



        public static String webServer = "global.talk-cloud.net";
//    public static String webServer = "global.talk-cloud.neiwang";
//    public static String webServer = "demo.talk-cloud.net";

    public boolean isExit() {
        return isExit;
    }

    public void setExit(boolean exit) {
        isExit = exit;
    }

    private boolean isExit = false;

    static public RoomClient getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new RoomClient();
            }
            return mInstance;
        }
    }

    public void joinRoom(Activity activity, Map<String, Object> map) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("autoSubscribeAV", true);
        /*params.put("AudioSource", AudioManager.STREAM_MUSIC); //机灵宝宝*/
        TKRoomManager.getInstance().init(activity.getApplicationContext(), "talkcloud", params);
        TKRoomManager.getInstance().registerRoomObserver(RoomSession.getInstance());
        TKRoomManager.getInstance().setWhiteBoard(WBSession.getInstance());

        this.activity = activity;
        checkRoom(activity, map);
    }

    public void joinRoom(Activity activity, String temp) {
        temp = Uri.decode(temp);

        String[] temps = temp.split("&");
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < temps.length; i++) {
            String[] t = temps[i].split("=");
            if (t.length > 1) {
                map.put(t[0], t[1]);
            }
        }
        if (map.containsKey("path")) {
            String tempPath = "http://" + map.get("path");
            map.put("path", tempPath);
        }
        joinRoom(activity, map);
    }

    public void joinPlayBackRoom(final Activity activity, String temp) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("autoSubscribeAV", true);
        /*params.put("AudioSource", AudioManager.STREAM_MUSIC); //机灵宝宝*/
        TKRoomManager.getInstance().init(activity.getApplicationContext(), "talkplus", params);
        TKRoomManager.getInstance().registerRoomObserver(RoomSession.getInstance());
        TKRoomManager.getInstance().setWhiteBoard(WBSession.getInstance());

        this.activity = activity;
        String[] temps = temp.split("&");
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < temps.length; i++) {
            String[] t = temps[i].split("=");
            if (t.length > 1) {
                map.put(t[0], t[1]);
            }
        }

        if (map.containsKey("path")) {
            String tempPath = "http://" + map.get("path");
            map.put("path", tempPath);
        }

        host = map.get("host") instanceof String ? (String) map.get("host") : "";
        serial = map.get("serial") instanceof String ? (String) map.get("serial") : "";
        nickname = map.get("nickname") instanceof String ? (String) map.get("nickname") : "";
        userid = map.get("userid") instanceof String ? (String) map.get("userid") : "";
        password = map.get("password") instanceof String ? (String) map.get("password") : "";
        param = map.get("param") instanceof String ? (String) map.get("param") : "";
        domain = map.get("domain") instanceof String ? (String) map.get("domain") : "";
        finalnickname = Uri.encode(nickname);
        path = map.get("path") instanceof String ? (String) map.get("path") : "";

        if (map.get("port") instanceof Integer) {
            port = (Integer) map.get("port");
        } else if (map.get("port") instanceof String) {
            boolean isNum = ((String) map.get("port")).matches("[0-9]+");
            if (isNum) {
                port = (Integer) map.get("port");
            }
        }

        if (map.containsKey("type")) {
            if (map.get("type") instanceof Integer) {
                type = (Integer) map.get("type");
            } else if (map.get("type") instanceof String) {
                boolean isNum = ((String) map.get("type")).matches("[0-9]+");
                if (isNum) {
                    type = Integer.parseInt(map.get("type") + "");
                }
            }
        }
        getmobilename(host, port);
        String url = path + "room.json";

        /*SystemClock.sleep(3000);*/

        TKRoomManager.getInstance().getPlayBackRoomJson(url);
    }

    public void checkRoom(final Activity activity, Map<String, Object> map) {

        host = map.get("host") instanceof String ? (String) map.get("host") : "";
        serial = map.get("serial") instanceof String ? (String) map.get("serial") : "";
        nickname = map.get("nickname") instanceof String ? (String) map.get("nickname") : "";
        userid = map.get("userid") instanceof String ? (String) map.get("userid") : "";

        password = map.get("password") instanceof String ? (String) map.get("password") : "";
        param = map.get("param") instanceof String ? (String) map.get("param") : "";
        domain = map.get("domain") instanceof String ? (String) map.get("domain") : "";
        servername = map.get("servername") instanceof String ? (String) map.get("servername") : "";
        path = map.get("path") instanceof String ? (String) map.get("path") : "";

        clientType = map.get("clientType") instanceof String ? (String) map.get("clientType") : "2";
        finalnickname = Uri.encode(nickname);

        int userrole = 2;
        if (map.get("userrole") instanceof Integer) {
            userrole = (Integer) map.get("userrole");
        } else if (map.get("userrole") instanceof String) {
            boolean isNum = ((String) map.get("userrole")).matches("[0-9]+");
            if (isNum) {
                userrole = (Integer) map.get("userrole");
            }
        }

        if (map.get("port") instanceof Integer) {
            port = (Integer) map.get("port");
        } else if (map.get("port") instanceof String) {
            boolean isNum = ((String) map.get("port")).matches("[0-9]+");
            if (isNum) {
                port = (Integer) map.get("port");
            }
        }

        getmobilename(host, port);

        HashMap<String, Object> params = new HashMap<String, Object>();
        if (!param.isEmpty())
            params.put("param", param);

        params.put("userid", userid);
        params.put("password", password);
        params.put("serial", serial);
        params.put("userrole", userrole);
        params.put("nickname", nickname);
        params.put("volume", 100);

        //新加皮肤字段   1 PC   2 Android   3 IOS
        params.put("clientType", clientType);

        params.put("mobilenameOnList", RoomSession.mobilenameNotOnList);
        if (domain != null && !domain.isEmpty())
            params.put("domain", domain);
        if (servername != null && !servername.isEmpty())
            params.put("servername", servername);

        UiModeManager uiModeManager = (UiModeManager) activity.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            TKRoomManager.getInstance().setDeviceType("AndroidTV");
        } else {
            if (!Tools.isPad(activity)) {
                TKRoomManager.getInstance().setDeviceType("AndroidPad");
            } else {
                TKRoomManager.getInstance().setDeviceType("AndroidPhone");
            }
        }

        TKRoomManager.getInstance().joinRoom(host, port, finalnickname, params, new HashMap<String, Object>(), true);
    }

    String apkDownLoadUrl = "";

    public void checkForUpdate(final Activity activity, String url, final CheckForUpdateCallBack ack) {
        url = "http://" + url + ":" + 80 + "/ClientAPI/getupdateinfo";

        RequestParams params = new RequestParams();
        try {
            PackageManager manager = activity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
            int version = info.versionCode;
            params.put("type", 9);
            params.put("version", "2018072800");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("classroom", "url=" + url + "params=" + params);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                try {

                    final int nRet = response.getInt("result");
                    if (nRet == 0) {
                        apkDownLoadUrl = response.optString("updateaddr");
                        final int code = response.optInt("updateflag");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ack.callBack(code);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("emm", "error");
            }
        });
    }

    String target;

    public void downLoadApp(final Activity activity) {
        target = Environment.getExternalStorageDirectory().getAbsolutePath() + "/talkcloudHD.apk";
        if (!apkDownLoadUrl.contains("http://")) {
            apkDownLoadUrl = "http://" + apkDownLoadUrl;
        }
        if (apkDownLoadUrl != null && !apkDownLoadUrl.isEmpty()) {
            HttpUtils http = new HttpUtils();
            http.download(apkDownLoadUrl, target, new RequestCallBack<File>() {

                @Override
                public void onFailure(HttpException exception, String msg) {

                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    dialog.dismiss();
                    // 安装apk
                    installApk(activity);
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    initProgressDialog(activity, total, current);
                }
            });
        }
    }

    protected void initProgressDialog(Activity activity, long total, long current) {
        if (dialog == null) {
            dialog = new ProgressDialog(activity);
        }
        dialog.setTitle(activity.getString(R.string.updateing));//设置标题
        dialog.setMessage("");//设置dialog内容
        //        dialog.setIcon(R.drawable.icon_word);//设置图标，与为Title左侧
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 水平线进度条
        // dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//圆形进度条
        dialog.setMax((int) total);//最大值
        dialog.setProgress((int) current);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private ProgressDialog dialog;

    protected void installApk(Activity activity) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上){
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            File file = new File(target);
            Uri apkUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);//在AndroidManifest中的android:authorities值
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            activity.startActivity(intent);
        } else {
            Uri data = Uri.parse("file://" + target);
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }

    public void regiestInterface(MeetingNotify notify, com.eduhdsdk.interfaces.JoinmeetingCallBack callBack) {
        this.notify = notify;
        this.callBack = callBack;
    }

    public void setNotify(MeetingNotify notify) {
        this.notify = notify;
    }

    public void setCallBack(com.eduhdsdk.interfaces.JoinmeetingCallBack callBack) {
        this.callBack = callBack;
    }

    public void joinRoomcallBack(int code) {
        if (this.callBack != null) {

            if (code == 0) {
                Intent intent = null;
                int role = TKRoomManager.getInstance().getMySelf().role;
                int roomType = TKRoomManager.getInstance().getRoomType();
                setRoomSession(role);

                String skinId = TKRoomManager.getInstance().get_skinId();
                String tplId = TKRoomManager.getInstance().get_tplId();

                /*intent = new Intent(activity, OldVideoActivity.class);*/

                if (!TextUtils.isEmpty(tplId) && tplId.equals("classic")) {
                    String name = SkinCompatManager.getInstance().getCurSkinName();
                    if (TextUtils.isEmpty(name)) {
                        //黑色默认
                        SkinCompatManager.getInstance().loadSkin("black_skin.zip", SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                    }
                    /*//旧界面一对多
                    SkinCompatManager.getInstance().restoreDefaultTheme();*/
                    if (!TextUtils.isEmpty(skinId) && skinId.equals("origin")) {
                        intent = new Intent(activity, OldVideoActivity.class);
                    }
                } else if (!TextUtils.isEmpty(tplId) && tplId.equals("default")) {
                    //SkinCompatManager.getInstance().loadSkin("black_skin.zip", SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                    //新版界面
                    if (!TextUtils.isEmpty(skinId) && skinId.equals("black")) {
                        String name = SkinCompatManager.getInstance().getCurSkinName();
                        if (TextUtils.isEmpty(name)) {
                            //黑色默认
                            SkinCompatManager.getInstance().loadSkin("black_skin.zip", SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                        }
                    } else if (!TextUtils.isEmpty(skinId) && skinId.equals("default")) {
                        String name = SkinCompatManager.getInstance().getCurSkinName();
                        if (!TextUtils.isEmpty(name)) {
                            //少儿宇宙版
                            SkinCompatManager.getInstance().restoreDefaultTheme();
                        }
                    }

                    if (roomType == 0 && !RoomControler.isShowAssistantAV()) {
                        intent = new Intent(activity, OneToOneActivity.class);
                    } else {
                        intent = new Intent(activity, OneToManyActivity.class);
                    }

                }

                if (intent != null) {
                    activity.startActivity(intent);
                }
            }
            callBack.callBack(code);
        }
    }

    public void onPlayBackRoomJson(int code, String response) {
        if (this.callBack != null) {
            Intent intent = null;
            if (code == 0) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int role = jsonObject.optInt("roomrole", -1);
                    userid = jsonObject.optString("userid", userid);
                    serial = jsonObject.optString("serial", serial);
                    setRoomSession(role);
                    joinPlayBackRoom();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                int roomType = TKRoomManager.getInstance().getRoomType();

                String skinId = TKRoomManager.getInstance().get_skinId();
                String tplId = TKRoomManager.getInstance().get_tplId();

                if (!TextUtils.isEmpty(tplId) && tplId.equals("classic")) {

                    String name = SkinCompatManager.getInstance().getCurSkinName();
                    if (TextUtils.isEmpty(name)) {
                        //黑色默认
                        SkinCompatManager.getInstance().loadSkin("black_skin.zip", SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                    }
                   /* //旧界面一对多
                    SkinCompatManager.getInstance().restoreDefaultTheme();*/
                    if (!TextUtils.isEmpty(skinId) && skinId.equals("origin")) {
                        intent = new Intent(activity, OldVideoActivity.class);
                    }

                } else if (!TextUtils.isEmpty(tplId) && tplId.equals("default")) {

                    if (!TextUtils.isEmpty(skinId) && skinId.equals("black")) {
                        String name = SkinCompatManager.getInstance().getCurSkinName();
                        if (TextUtils.isEmpty(name)) {
                            //黑色默认
                            SkinCompatManager.getInstance().loadSkin("black_skin.zip", SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                        }

                    } else if (!TextUtils.isEmpty(skinId) && skinId.equals("default")) {

                        String name = SkinCompatManager.getInstance().getCurSkinName();
                        if (!TextUtils.isEmpty(name)) {
                            //少儿宇宙版
                            SkinCompatManager.getInstance().restoreDefaultTheme();
                        }
                    }

                    if (roomType == 0 && !RoomControler.isShowAssistantAV()) {
                        intent = new Intent(activity, OneToOneActivity.class);
                    } else {
                        intent = new Intent(activity, OneToManyActivity.class);
                    }
                }
               /* intent = new Intent(activity, OneToManyActivity.class);*/
                if (intent != null) {
                    activity.startActivity(intent);
                }
            } else {
                callBack.callBack(code);
            }
        }
    }

    private void joinPlayBackRoom() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (param != null && !param.isEmpty())
            params.put("param", param);
        if (domain != null && !domain.isEmpty())
            params.put("domain", domain);
        if (finalnickname != null && !finalnickname.isEmpty())
            params.put("servername", finalnickname);
        if (path != null && !path.isEmpty()) {
            params.put("playback", true);
        }
        if (!TextUtils.isEmpty(path)) {
            params.put("path", path);
        }
        if (type != -1) {
            params.put("type", type);
        }
        params.put("password", RoomSession.password);
        params.put("nickname", RoomSession.nickname);
        params.put("volume", 100);
        params.put("mobilenameOnList", RoomSession.mobilenameNotOnList);
        params.put("userid", RoomSession.userid);
        params.put("serial", RoomSession.serial);
        params.put("userrole", RoomSession.userrole);
        RoomSession.params = params;

        TKRoomManager.getInstance().joinPlayBackRoom(RoomSession.host, RoomSession.port,
                RoomSession.nickname, RoomSession.params, new HashMap<String, Object>(), true);
    }

    private void setRoomSession(int role) {
        RoomSession.host = host;
        RoomSession.port = port;
        RoomSession.nickname = finalnickname;
        if (param != null && !param.isEmpty()) {
            RoomSession.param = param;
        }
        RoomSession.serial = serial;
        RoomSession.password = password;
        RoomSession.userid = userid;
        RoomSession.userrole = role;
        if (domain != null && !domain.isEmpty()) {
            RoomSession.domain = domain;
        }
        if (path != null && !path.isEmpty()) {
            RoomSession.path = path;
        }
        if (servername != null && !servername.isEmpty()) {
            RoomSession.servername = servername;
        }
    }

    private void getmobilename(String host, int port) {
        String url = "http://" + host + ":" + port + "/ClientAPI/getmobilename";
        RequestParams params = new RequestParams();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                try {
                    int nRet = response.getInt("result");
                    if (nRet == 0) {
                        String mobilename = response.optJSONArray("mobilename").toString();
                        RoomSession.mobilename = mobilename;
                        try {
                            String brand = Build.MODEL;
                            if (mobilename != null && !mobilename.isEmpty()) {
                                JSONArray mNames = new JSONArray(mobilename);
                                for (int i = 0; i < mNames.length(); i++) {
                                    if (brand.toLowerCase().equals(mNames.optString(i).toLowerCase())) {
                                        RoomSession.mobilenameNotOnList = false;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("emm", "error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void kickout(int res) {
        if (notify != null) {
            notify.onKickOut(res);
        }
    }

    /***
     * 警告权限
     * @param code 1没有视频权限2没有音频权限
     */
    public void warning(int code) {
        if (notify != null) {
            notify.onWarning(code);
        }
    }

    public void onClassBegin() {
        if (notify != null) {
            notify.onClassBegin();
        }
    }

    public void onClassDismiss() {
        if (notify != null) {
            notify.onClassDismiss();
        }
    }
}
