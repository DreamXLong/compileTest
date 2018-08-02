package com.eduhdsdk.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.classroomsdk.Config;
import com.classroomsdk.NotificationCenter;
import com.classroomsdk.Tools;
import com.classroomsdk.WBSession;
import com.eduhdsdk.R;
import com.eduhdsdk.animutil.AnimationsContainer;
import com.eduhdsdk.message.JSVideoWhitePadInterface;
import com.eduhdsdk.message.RoomSession;
import com.eduhdsdk.message.VideoWBCallback;
import com.eduhdsdk.tools.FrameAnimation;
import com.talkcloud.roomsdk.TKRoomManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tkwebrtc.EglBase;
import org.tkwebrtc.EglRenderer;
import org.tkwebrtc.RendererCommon;
import org.tkwebrtc.SurfaceViewRenderer;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/2/27/027.
 */

public class MovieFragment extends android.support.v4.app.Fragment implements VideoWBCallback, NotificationCenter.NotificationCenterDelegate {

    private static String sync = "";
    static private MovieFragment mInstance = null;
    private View fragmentView;
    LinearLayout re_video_play;
    SurfaceViewRenderer suf_mp4;
    private String peerIdShareFile;
    private EglRenderer.FrameListener frameListener;
    private RelativeLayout re_laoding;

    private ImageView loadingImageView;
    private AnimationsContainer.FramesSequenceAnimation animation;

    private XWalkView xWalkView;

    private SharedPreferences spkv = null;
    private SharedPreferences.Editor editor = null;

    private FrameAnimation frameAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setShareFilePeerId(String peerIdShareFile) {
        this.peerIdShareFile = peerIdShareFile;
        TKRoomManager.getInstance().playFile(peerIdShareFile, suf_mp4);
    }

    static public MovieFragment getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new MovieFragment();
            }
            return mInstance;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_movie, null);
            fragmentView.bringToFront();

            re_video_play = (LinearLayout) fragmentView.findViewById(R.id.re_video_play);
            suf_mp4 = (SurfaceViewRenderer) fragmentView.findViewById(R.id.suf_mp4);
            suf_mp4.init(EglBase.create().getEglBaseContext(), null);
            suf_mp4.setZOrderMediaOverlay(true);

            re_laoding = (RelativeLayout) fragmentView.findViewById(R.id.re_laoding);
            loadingImageView = (ImageView) fragmentView.findViewById(R.id.loadingImageView);

            if (frameAnimation == null) {
                frameAnimation = new FrameAnimation(loadingImageView, getRes(), 25, true, true, getContext());
            }

            if (animation == null) {
                /*animation = new AnimationsContainer(R.array.frame_mp4, 30, getContext())
                        .createProgressDialogAnim(loadingImageView);*/
            }

            xWalkView = (XWalkView) fragmentView.findViewById(R.id.movie_white_board);
            XWalkPreferences.setValue("enable-javascript", true);
            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
            XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
            XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
            XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
            XWalkSettings webs = xWalkView.getSettings();
            webs.setJavaScriptEnabled(true);
            webs.setCacheMode(WebSettings.LOAD_DEFAULT);
            webs.setDomStorageEnabled(true);
            webs.setDatabaseEnabled(true);
            webs.setAllowFileAccess(true);
            webs.setSupportZoom(false);
            webs.setBuiltInZoomControls(false);
            webs.setLoadWithOverviewMode(false);
            webs.setJavaScriptCanOpenWindowsAutomatically(true);
            webs.setLoadWithOverviewMode(true);
            webs.setDomStorageEnabled(true);
            webs.setUseWideViewPort(true);
            webs.setMediaPlaybackRequiresUserGesture(false);
            webs.setSupportSpatialNavigation(true);
            webs.setAllowFileAccessFromFileURLs(true);
            webs.setLayoutAlgorithm(XWalkSettings.LayoutAlgorithm.NORMAL);
            webs.setUseWideViewPort(true);
            xWalkView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            xWalkView.setHorizontalScrollBarEnabled(false);
            JSVideoWhitePadInterface.getInstance().setVideoWBCallBack(this);
            xWalkView.addJavascriptInterface(JSVideoWhitePadInterface.getInstance(), "JSVideoWhitePadInterface");
            xWalkView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            xWalkView.setBackgroundColor(0);

            xWalkView.setUIClient(new XWalkUIClient(xWalkView) {
                @Override
                protected Object getBridge() {
                    return super.getBridge();
                }

                @Override
                public void onPageLoadStarted(XWalkView view, String url) {
                    super.onPageLoadStarted(view, url);
                }

                @Override
                public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
                    super.onPageLoadStopped(view, url, status);
                }
            });

            if (RoomSession.isShowVideoWB) {
                xWalkView.setVisibility(View.VISIBLE);
                xWalkView.setZOrderOnTop(true);
            } else {
                xWalkView.setVisibility(View.INVISIBLE);
            }

            if (Config.isWhiteMovieBoardTest) {
//                xWalkView.loadUrl("http://192.168.1.108:8444/publish/index.html#/mobileApp?loadComponentName=" + "localFileVideoDrawWhiteboardComponent");//李
                xWalkView.loadUrl("http://192.168.1.251:9251/publish/index.html#/mobileApp?loadComponentName=" + "localFileVideoDrawWhiteboardComponent");//建行
//                xWalkView.loadUrl("http://192.168.1.182:1314/publish/index.html#/mobileApp_videoWhiteboard?videoDrawingBoardType=" + "fileVideo");//广生
//                xWalkView.loadUrl("http://192.168.1.64:8585/publish/index.html#/mobileApp?loadComponentName=" + "localFileVideoDrawWhiteboardComponent");
            } else {
                xWalkView.loadUrl("file:///android_asset/react_mobile_new_publishdir/index.html#/mobileApp?loadComponentName=" + "localFileVideoDrawWhiteboardComponent");
            }
        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        re_laoding.setVisibility(View.VISIBLE);

        if (frameAnimation != null) {
            frameAnimation.playAnimation();
        }

       /* if (animation != null) {
            animation.start();
        }*/


        if (peerIdShareFile != null) {
            suf_mp4.setZOrderOnTop(true);
            suf_mp4.setZOrderMediaOverlay(true);
            suf_mp4.setEnableHardwareScaler(true);
            suf_mp4.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            TKRoomManager.getInstance().playFile(peerIdShareFile, suf_mp4);
            suf_mp4.requestLayout();
        }

        frameListener = new EglRenderer.FrameListener() {
            @Override
            public void onFrame(final Bitmap bitmap) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (frameAnimation != null) {
                            frameAnimation.stopAnimation();
                            frameAnimation = null;
                        }
                       /* if (animation != null) {
                            animation.stop();
                        }*/
                        re_laoding.setVisibility(View.GONE);
                    }
                });
            }
        };
        suf_mp4.addFrameListener(frameListener, 0);
    }

    @Override
    public void onStop() {
        if (xWalkView != null) {
            xWalkView.onHide();
        }
        if (suf_mp4 != null && frameListener != null) {
            suf_mp4.removeFrameListener(frameListener);
            frameListener = null;
        }

        if (frameAnimation != null) {
            frameAnimation.stopAnimation();
            frameAnimation = null;
        }
        re_laoding.setVisibility(View.GONE);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        RoomSession.jsVideoWBTempMsg = new JSONArray();
        suf_mp4.release();
        suf_mp4 = null;
        if (xWalkView != null) {
            xWalkView.removeAllViews();
            xWalkView.onDestroy();
            xWalkView = null;
        }
        mInstance = null;
        super.onDestroyView();
    }

    private boolean onRemoteMsg(boolean add, String id, String name, long ts, Object data, String fromID, String associatedMsgID, String associatedUserID, JSONObject jsonObjectRemoteMsg) {
        if (add) {
            if (name.equals("VideoWhiteboard")) {
                if (xWalkView != null) {
                    if (suf_mp4 != null) {
                        suf_mp4.setZOrderMediaOverlay(false);
                        suf_mp4.setZOrderOnTop(false);
                    }
                    xWalkView.setVisibility(View.VISIBLE);
                    xWalkView.setZOrderOnTop(true);
                }
            }
        } else {
            if (name.equals("VideoWhiteboard")) {
                RoomSession.jsVideoWBTempMsg = new JSONArray();
                if (xWalkView != null) {
                    if (suf_mp4 != null) {
                        suf_mp4.setZOrderMediaOverlay(true);
                    }
                   /* xWalkView.setVisibility(View.INVISIBLE);*/
                    /*xWalkView.setZOrderOnTop(false);*/
                }
            }
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
            if (add) {
                if (associatedMsgID.equals("VideoWhiteboard") || id.equals("VideoWhiteboard")) {
                    xWalkView.loadUrl("javascript:JsSocket.pubMsg(" + jsobj.toString() + ")");
                }
            } else {
                if (associatedMsgID.equals("VideoWhiteboard") || id.equals("VideoWhiteboard")) {
                    xWalkView.loadUrl("javascript:JsSocket.delMsg(" + jsobj.toString() + ")");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        spkv = context.getSharedPreferences("dataphone", MODE_PRIVATE);
        editor = spkv.edit();

        NotificationCenter.getInstance().addObserver(this, WBSession.onRemoteMsg);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRoomLeaved);
        NotificationCenter.getInstance().addObserver(this, WBSession.onPlayBackClearAll);
        NotificationCenter.getInstance().addObserver(this, WBSession.onRoomConnectFaild);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NotificationCenter.getInstance().removeObserver(this);
    }

    private void roomPlaybackClearAll() {
        JSONObject js = new JSONObject();
        xWalkView.loadUrl("javascript:JsSocket.playback_clearAll(" + js.toString() + ")");
    }

    private void roomDisConnect() {
        if (xWalkView != null) {
            JSONObject js = new JSONObject();
            xWalkView.loadUrl("javascript:JsSocket.disconnect(" + js.toString() + ")");
        }
    }

    @Override
    public void pubMsg(String js) {

        try {
            JSONObject jsobj = new JSONObject(js);
            String msgName = jsobj.optString("name");
            String msgId = jsobj.optString("id");
            String toId = jsobj.optString("toID");
            String data = jsobj.optString("data");
            String associatedMsgID = jsobj.optString("associatedMsgID");
            String associatedUserID = jsobj.optString("associatedUserID");
            boolean save = jsobj.optBoolean("do_not_save", false);
            if (jsobj.has("do_not_save")) {
                save = false;
            } else {
                save = true;
            }
            TKRoomManager.getInstance().pubMsg(msgName, msgId, toId, data, save, associatedMsgID, associatedUserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delMsg(String js) {
        try {
            JSONObject jsobj = new JSONObject(js);
            String msgName = jsobj.optString("name");
            String msgId = jsobj.optString("id");
            String toId = jsobj.optString("toID");
            String data = jsobj.optString("data");
//            Map<String,Object> datamap = new HashMap<String,Object>();
//            if(data!=null){
//                datamap =  toMap(data);
//            }
            TKRoomManager.getInstance().delMsg(msgName, msgId, toId, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageFinished() {
        final JSONObject j = new JSONObject();
        try {
            j.put("isSendLogMessage", true);
            j.put("debugLog", true);
            j.put("myself", TKRoomManager.getInstance().getMySelf().toJson());
            j.put("_tplId", TKRoomManager.getInstance().get_tplId());
            j.put("_skinId", TKRoomManager.getInstance().get_skinId());
            j.put("_skinResource", TKRoomManager.getInstance().get_skinResource());
            if (Tools.isTablet(getContext())) {
                j.put("deviceType", "pad");
            } else {
                j.put("deviceType", "phone");
            }
            j.put("clientType", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    xWalkView.loadUrl("javascript:JsSocket.updateFakeJsSdkInitInfo('" + j.toString() + "')");
                }
            });
        }
        if (RoomSession.jsVideoWBTempMsg.length() > 0) {
           /* final JSONObject js = new JSONObject();
            try {
                js.put("type", "room-msglist");
                js.put("message", RoomSession.jsVideoWBTempMsg);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        xWalkView.loadUrl("javascript:JsSocket.msgList(" + RoomSession.jsVideoWBTempMsg.toString() + ")");
//                        RoomSession.jsVideoWBTempMsg = new JSONArray();
                    }
                });
            }
        }

        if (xWalkView != null) {
            xWalkView.onShow();
            DisplayMetrics dm = new DisplayMetrics();
            if (getActivity() != null) {
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                final int wid = dm.widthPixels;
                final int hid = dm.heightPixels;
                transmitWindowSize(wid, hid);
            }
        }
        WBSession.getInstance().onPageFinished();
    }

    /***
     * @param state  退出标注
     */
    @Override
    public void exitAnnotation(String state) {
        if (TextUtils.isEmpty(state) && state.equals("file")) {

        }
    }

    @Override
    public void saveValueByKey(String key, String value) {
        if (editor != null) {
            editor.putString(key, value);
            editor.commit();
        }
    }

    @Override
    public void getValueByKey(String key, final int callbackId) {
        if (spkv != null) {
            final String value = spkv.getString(key, "");
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (xWalkView != null) {
                            xWalkView.loadUrl("javascript:JsSocket.JsSocketCallback(" + callbackId + ",'" + value + "')");
                        }
                    }
                });
            }
        }
    }

    public void transmitWindowSize(int wid, int hid) {
        try {
            final JSONObject js = new JSONObject();
            js.put("windowWidth", wid);
            js.put("windowHeight", hid);

            if (xWalkView != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        xWalkView.loadUrl("javascript:JsSocket.receiveActionCommand(" + "'whiteboardSDK_updateWhiteboardSize'" + "," + js.toString() + ")");
//                        RoomSession.jsVideoWBTempMsg = new JSONArray();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didReceivedNotification(final int id, final Object... args) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    case WBSession.onRemoteMsg:
                        boolean addRemoteMsg = (boolean) args[0];
                        String idRemoteMsg = (String) args[1];
                        String nameRemoteMsg = (String) args[2];
                        long tsRemoteMsg = (long) args[3];
                        Object dataRemoteMsg = (Object) args[4];
                        String fromIDRemoteMsg = (String) args[5];
                        String associatedMsgIDRemoteMsg = (String) args[6];
                        String associatedUserIDRemoteMsg = (String) args[7];
                        JSONObject jsonObjectRemoteMsg = (JSONObject) args[8];
                        onRemoteMsg(addRemoteMsg, idRemoteMsg, nameRemoteMsg, tsRemoteMsg, dataRemoteMsg, fromIDRemoteMsg,
                                associatedMsgIDRemoteMsg, associatedUserIDRemoteMsg, jsonObjectRemoteMsg);
                        break;
                    case WBSession.onRoomLeaved:
                        roomDisConnect();
                        break;
                    case WBSession.onPlayBackClearAll:
                        roomPlaybackClearAll();
                        break;
                    case WBSession.onRoomConnectFaild:
                        roomDisConnect();
                        break;
                }
            }
        });
    }

    private int[] getRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.frame_mp4);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }
}
