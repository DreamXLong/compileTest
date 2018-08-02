package com.eduhdsdk.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.classroomsdk.Config;
import com.classroomsdk.NotificationCenter;
import com.classroomsdk.ShareDoc;
import com.classroomsdk.Tools;
import com.classroomsdk.WBSession;
import com.classroomsdk.WhiteBoradManager;
import com.eduhdsdk.R;
import com.eduhdsdk.animutil.AnimationsContainer;
import com.eduhdsdk.message.JSVideoWhitePadInterface;
import com.eduhdsdk.message.RoomSession;
import com.eduhdsdk.message.VideoWBCallback;
import com.eduhdsdk.tools.FrameAnimation;
import com.eduhdsdk.tools.ScreenScale;
import com.talkcloud.roomsdk.RoomControler;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;


public class VideoFragment extends Fragment implements NotificationCenter.NotificationCenterDelegate, VideoWBCallback {

    private static String TAG = "VideoFragment";

    private static String sync = "";
    private static volatile  VideoFragment mInstance = null;
    private View fragmentView;
    private RelativeLayout lin_video_play;
    private SurfaceViewRenderer suf_mp4;
    private ImageView img_close_mp4;
    private LinearLayout lin_video_control, ll_close_mp3;
    private ImageView img_play_mp4;
    private TextView txt_mp4_name;
    private TextView txt_mp4_time;
    private SeekBar sek_mp4;
    private ImageView img_voice_mp4;
    private XWalkView xWalkView;
    private SeekBar sek_voice_mp4;
    private String shareMediaPeerId;
    private Map<String, Object> shareMediaAttrs;
    private double vol = 0.5;
    private boolean isMute = false;
    private double ratio = (double) 16 / (double) 9;

    private EglRenderer.FrameListener frameListener;
    private RelativeLayout re_laoding;
    private ImageView loadingImageView;
    private FrameAnimation frameAnimation;

    private SharedPreferences spkv = null;
    private SharedPreferences.Editor editor = null;


    public void setStream(String shareMediaPeerId, Map<String, Object> shareMediaAttrs) {
        this.shareMediaPeerId = shareMediaPeerId;
        this.shareMediaAttrs = shareMediaAttrs;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 界面类型  0  新版界面   1  旧界面
     */
    private int type = 0;

    public void setType(int type) {
        this.type = type;
    }


    static public VideoFragment getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new VideoFragment();
            }
            return mInstance;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (fragmentView == null) {

            if (type == 0) {
                fragmentView = inflater.inflate(R.layout.fragment_video, null);
            } else {
                fragmentView = inflater.inflate(R.layout.old_fragment_video, null);
            }

            fragmentView.bringToFront();

            lin_video_play = (RelativeLayout) fragmentView.findViewById(R.id.lin_video_play);
            suf_mp4 = (SurfaceViewRenderer) fragmentView.findViewById(R.id.suf_mp4);
            suf_mp4.init(EglBase.create().getEglBaseContext(), null);

            re_laoding = (RelativeLayout) fragmentView.findViewById(R.id.re_laoding);
            loadingImageView = (ImageView) fragmentView.findViewById(R.id.loadingImageView);
            frameAnimation = new FrameAnimation(loadingImageView, getRes(), 25, true,
                    true, getContext());

            img_close_mp4 = (ImageView) fragmentView.findViewById(R.id.img_close_mp4);
            lin_video_control = (LinearLayout) fragmentView.findViewById(R.id.lin_video_control);

            img_play_mp4 = (ImageView) lin_video_control.findViewById(R.id.img_play);
            txt_mp4_name = (TextView) lin_video_control.findViewById(R.id.txt_media_name);
            txt_mp4_time = (TextView) lin_video_control.findViewById(R.id.txt_media_time);
            sek_mp4 = (SeekBar) lin_video_control.findViewById(R.id.sek_media);
            sek_mp4.setPadding((int) (10 * ScreenScale.getWidthScale()), 0, (int) (10 * ScreenScale.getWidthScale()), 0);
            img_voice_mp4 = (ImageView) lin_video_control.findViewById(R.id.img_media_voice);
            sek_voice_mp4 = (SeekBar) lin_video_control.findViewById(R.id.sek_media_voice);
            sek_voice_mp4.setPadding((int) (10 * ScreenScale.getWidthScale()), 0, (int) (10 * ScreenScale.getWidthScale()), 0);
            ll_close_mp3 = (LinearLayout) lin_video_control.findViewById(R.id.ll_close_mp3);
            ll_close_mp3.setVisibility(View.GONE);

            xWalkView = (XWalkView) fragmentView.findViewById(R.id.video_white_board);

            XWalkPreferences.setValue("enable-javascript", true);
            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
            XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
            XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
            XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
//            xWalkView.setZOrderOnTop(true);
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
            if (Config.isWhiteVideoBoardTest) {
//                xWalkView.loadUrl("http://192.168.1.192:8444/publish/index.html#/mobileApp?loadComponentName=" + "videoDrawWhiteboardComponent");//支祥
//                xWalkView.loadUrl("http://192.168.1.64:8585/publish/index.html#/mobileApp?loadComponentName=" + "videoDrawWhiteboardComponent");
                xWalkView.loadUrl("http://192.168.1.251:9251/publish/index.html#/mobileApp?loadComponentName=" + "videoDrawWhiteboardComponent");//建行
//                xWalkView.loadUrl("http://192.168.1.220:9251/publish/index.html#/mobileApp?loadComponentName=" + "videoDrawWhiteboardComponent");
//                 xWalkView.loadUrl("http://192.168.1.108:8444/publish/index.html#/mobileApp?loadComponentName=" + "videoDrawWhiteboardComponent");//李珂
//                xWalkView.loadUrl("http://192.168.1.228:8444/publish/index.html#/mobileApp?loadComponentName=" + "videoDrawWhiteboardComponent");//李凯
//              xWalkView.loadUrl("http://192.168.1.182:1314/publish/index.html#/mobileApp_videoWhiteboard?videoDrawingBoardType=" + "mediaVideo");//广生
//                xWalkView.loadUrl("http://192.168.1.64:8585/publish/index.html#/mobileApp?loadComponentName=" + "videoDrawWhiteboardComponent");
            } else {
                xWalkView.loadUrl("file:///android_asset/react_mobile_new_publishdir/index.html#/mobileApp?loadComponentName=" + "videoDrawWhiteboardComponent");
            }
        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }

        ScreenScale.scaleView(fragmentView, "VideoFragment");
        return fragmentView;
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

    public void setVoice() {
        if (img_voice_mp4 != null && sek_voice_mp4 != null) {
            if (isMute) {
                TKRoomManager.getInstance().setRemoteAudioVolume(0, shareMediaPeerId, 2);
                img_voice_mp4.setImageResource(R.drawable.icon_no_voice);
                sek_voice_mp4.setProgress(0);
            } else {
                TKRoomManager.getInstance().setRemoteAudioVolume(vol, shareMediaPeerId, 2);
                img_voice_mp4.setImageResource(R.drawable.icon_voice);
                sek_voice_mp4.setProgress((int) (vol * 100));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        re_laoding.setVisibility(View.VISIBLE);

        if (frameAnimation != null) {
            frameAnimation.playAnimation();
        }

        if (shareMediaPeerId != null) {
            suf_mp4.setZOrderOnTop(true);
            suf_mp4.setZOrderMediaOverlay(true);
            suf_mp4.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            TKRoomManager.getInstance().playMedia(shareMediaPeerId, suf_mp4);
            suf_mp4.requestLayout();
            if (txt_mp4_name != null) {
                txt_mp4_name.setText((String) shareMediaAttrs.get("filename"));
            }
        }
        lin_video_play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showControlView();
                return true;
            }
        });

        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            lin_video_control.setVisibility(View.VISIBLE);
            img_close_mp4.setVisibility(View.VISIBLE);
        } else {
            lin_video_control.setVisibility(View.INVISIBLE);
            img_close_mp4.setVisibility(View.INVISIBLE);
        }
        img_close_mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TKRoomManager.getInstance().unPlayMedia(shareMediaPeerId);*/
                TKRoomManager.getInstance().stopShareMedia();
                TKRoomManager.getInstance().delMsg("VideoWhiteboard", "VideoWhiteboard", "__all", null);
            }
        });
        img_play_mp4.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (RoomSession.isPublish) {
                            boolean ispause = (Boolean) shareMediaAttrs.get("pause") == null ? false : (Boolean) shareMediaAttrs.get("pause");
                            TKRoomManager.getInstance().playMedia(ispause);
                            if (ispause) {
                                if (TKRoomManager.getInstance().getMySelf().role == 0 && RoomSession.isClassBegin && RoomControler.isShowVideoWhiteBoard()) {
                                    TKRoomManager.getInstance().delMsg("VideoWhiteboard", "VideoWhiteboard", "__all", null);
                                }
                            } else {
                                if (TKRoomManager.getInstance().getMySelf().role == 0 && RoomSession.isClassBegin && RoomControler.isShowVideoWhiteBoard()) {
                                    JSONObject js = new JSONObject();
                                    try {
                                        js.put("videoRatio", ratio);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    TKRoomManager.getInstance().pubMsg("VideoWhiteboard", "VideoWhiteboard", "__all", js.toString(), true, "ClassBegin", null);
                                }
                            }
                        } else {
                            ShareDoc media = WhiteBoradManager.getInstance().getCurrentMediaDoc();
                            WhiteBoradManager.getInstance().setCurrentMediaDoc(media);
                            String strSwfpath = media.getSwfpath();
                            int pos = strSwfpath.lastIndexOf('.');
                            strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
                            String url = "http://" + WhiteBoradManager.getInstance().getFileServierUrl() + ":" + WhiteBoradManager.getInstance().getFileServierPort() + strSwfpath;

                            HashMap<String, Object> attrMap = new HashMap<String, Object>();
                            attrMap.put("filename", media.getFilename());
                            attrMap.put("fileid", media.getFileid());

                            if (RoomSession.isClassBegin) {
                                TKRoomManager.getInstance().startShareMedia(url, true, "__all", attrMap);
                            } else {
                                TKRoomManager.getInstance().startShareMedia(url, true, TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                            }
                        }
                    }
                });

        sek_mp4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pro = 0;
            boolean isfromUser = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    pro = progress;
                    isfromUser = fromUser;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double currenttime = 0;
                if (isfromUser && shareMediaAttrs != null) {
                    currenttime = ((double) pro / (double) seekBar.getMax()) * (int) shareMediaAttrs.get("duration");
                    TKRoomManager.getInstance().seekMedia((long) currenttime);
                }
            }
        });

        TKRoomManager.getInstance().setRemoteAudioVolume(vol, shareMediaPeerId, 2);
        img_voice_mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMute) {
                    TKRoomManager.getInstance().setRemoteAudioVolume(vol, shareMediaPeerId, 2);
                    img_voice_mp4.setImageResource(R.drawable.icon_voice);
                    sek_voice_mp4.setProgress((int) (vol * 100));
                } else {
                    TKRoomManager.getInstance().setRemoteAudioVolume(0, shareMediaPeerId, 2);
                    img_voice_mp4.setImageResource(R.drawable.icon_no_voice);
                    sek_voice_mp4.setProgress(0);
                }
                isMute = !isMute;
            }
        });
        sek_voice_mp4.setProgress((int) (vol * 100));
        sek_voice_mp4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = (float) progress / (float) seekBar.getMax();
                if (vol > 0) {
                    img_voice_mp4.setImageResource(R.drawable.icon_voice);
                } else {
                    img_voice_mp4.setImageResource(R.drawable.icon_no_voice);
                }
                TKRoomManager.getInstance().setRemoteAudioVolume(vol, shareMediaPeerId, 2);
                if (fromUser) {
                    VideoFragment.this.vol = vol;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        frameListener = new EglRenderer.FrameListener() {
            @Override
            public void onFrame(final Bitmap bitmap) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (frameAnimation != null) {
                            frameAnimation.stopAnimation();
                        }
                        re_laoding.setVisibility(View.GONE);
                        showControlView();
                    }
                });
            }
        };

        suf_mp4.addFrameListener(frameListener, 0);
    }

    public void controlMedia(Map<String, Object> onUpdateAttributeAttrs, long pos, boolean isPlay) {
        if (sek_mp4 != null) {
            int curtime = (int) ((double) pos / (int) onUpdateAttributeAttrs.get("duration") * 100);
            sek_mp4.setProgress(curtime);
        }
        if (img_play_mp4 != null) {
            if (!isPlay) {
                img_play_mp4.setImageResource(R.drawable.btn_pause_normal);
            } else {
                img_play_mp4.setImageResource(R.drawable.btn_play_normal);
            }
        }

        if (txt_mp4_time != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
            Date curDate = new Date(pos);
            Date daDate = new Date((int) onUpdateAttributeAttrs.get("duration"));
            String strcur = formatter.format(curDate);
            String strda = formatter.format(daDate);
            txt_mp4_time.setText(strcur + "/" + strda);
        }

        if (txt_mp4_name != null) {
            txt_mp4_name.setText((String) onUpdateAttributeAttrs.get("filename"));
        }

        if (onUpdateAttributeAttrs.containsKey("width") && onUpdateAttributeAttrs.containsKey("height") &&
                ((int) onUpdateAttributeAttrs.get("width")) != 0 && ((int) onUpdateAttributeAttrs.get("height")) != 0) {
            int wid = (int) onUpdateAttributeAttrs.get("width");
            int hid = (int) onUpdateAttributeAttrs.get("height");
            ratio = (double) wid / (double) hid;
        }

        RoomSession.dragVideo = false;

        RoomSession.getInstance().onStart();
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
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        RoomSession.jsVideoWBTempMsg = new JSONArray();
        suf_mp4.release();
        isMute = false;
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
                    /*img_close_mp4.setVisibility(View.GONE);*/
//                    if (Config.isWhiteVideoBoardTest) {
//                        xWalkView.loadUrl("http://192.168.1.182:1314/publish/index.html#/mobileApp_videoWhiteboard");//广生
//                    } else {
//                        xWalkView.loadUrl("file:///android_asset/react_mobile_video_whiteboard_publishdir/index.html#/mobileApp_videoWhiteboard");
//                    }
                }
            }
        } else {
            if (name.equals("VideoWhiteboard")) {
                RoomSession.jsVideoWBTempMsg = new JSONArray();
                if (xWalkView != null) {
                    if (suf_mp4 != null) {
                        suf_mp4.setZOrderMediaOverlay(true);
                    }
                    xWalkView.setVisibility(View.GONE);
                   /* xWalkView.setZOrderOnTop(false);*/
                   /* if (TKRoomManager.getInstance().getMySelf().role == 0) {
                        img_close_mp4.setVisibility(View.VISIBLE);
                    }*/
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
            /*final JSONObject js = new JSONObject();
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
    }


    @Override
    public void onResume() {
        super.onResume();
        if (xWalkView != null) {
            xWalkView.onShow();
        }
    }

    /***
     * @param state    退出视频标注
     */
    @Override
    public void exitAnnotation(String state) {
        if (!TextUtils.isEmpty(state) && state.equals("media")) {
            TKRoomManager.getInstance().playMedia(true);
            if (TKRoomManager.getInstance().getMySelf().role == 0 && RoomSession.isClassBegin && RoomControler.isShowVideoWhiteBoard()) {
                TKRoomManager.getInstance().delMsg("VideoWhiteboard", "VideoWhiteboard", "__all", null);
            }
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

    private void moveUpView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -120 * ScreenScale.getWidthScale());
        animator.setDuration(1000);
        animator.start();
    }

    private void backView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0);
        animator.setDuration(300);
        animator.start();
    }

    /**
     * 过滤掉用户多次点击的操作，防止动画错乱
     */
    boolean is_show_control_view = true;

    /**
     * 显示媒体控制栏，三 秒后隐藏
     */
    private void showControlView() {
        if (is_show_control_view) {
            is_show_control_view = false;
            moveUpView(lin_video_control);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            backView(lin_video_control);
                            is_show_control_view = true;
                        }
                    });
                }
            }, 3000);
        }
    }

    Timer timer = new Timer();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
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
