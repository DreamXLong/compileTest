package com.eduhdsdk.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.classroomsdk.NotificationCenter;
import com.classroomsdk.ShareDoc;
import com.classroomsdk.WBFragment;
import com.classroomsdk.WBSession;
import com.classroomsdk.WBStateCallBack;
import com.classroomsdk.WhiteBoradManager;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.ChatListAdapter;
import com.eduhdsdk.adapter.FileListAdapter;
import com.eduhdsdk.adapter.MediaListAdapter;
import com.eduhdsdk.adapter.MemberListAdapter;
import com.eduhdsdk.entity.ChatData;
import com.eduhdsdk.entity.MoveVideoInfo;
import com.eduhdsdk.entity.TimeMessage;
import com.eduhdsdk.entity.VideoItem;
import com.eduhdsdk.message.BroadcastReceiverMgr;
import com.eduhdsdk.message.RoomClient;
import com.eduhdsdk.message.RoomSession;
import com.eduhdsdk.tools.FrameAnimation;
import com.eduhdsdk.tools.FullScreenTools;
import com.eduhdsdk.tools.KeyBoardUtil;
import com.eduhdsdk.tools.LogUtils;
import com.eduhdsdk.tools.PermissionTest;
import com.eduhdsdk.tools.PersonInfo_ImageUtils;
import com.eduhdsdk.tools.PhotoUtils;
import com.eduhdsdk.tools.ScreenScale;
import com.eduhdsdk.tools.SoundPlayUtils;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.viewutils.AllActionUtils;
import com.eduhdsdk.viewutils.AllActionUtils.AllPopupWindowClick;
import com.eduhdsdk.viewutils.ChatWindowPop;
import com.eduhdsdk.viewutils.CoursePopupWindowUtils;
import com.eduhdsdk.viewutils.LayoutSizeUilts;
import com.eduhdsdk.viewutils.LayoutZoomOrIn;
import com.eduhdsdk.viewutils.MemberListPopupWindowUtils;
import com.eduhdsdk.viewutils.PageNumberPopupWindowUtils;
import com.eduhdsdk.viewutils.PlaySpeedPopupWindowUtils;
import com.eduhdsdk.viewutils.SendGiftPopUtils;
import com.eduhdsdk.viewutils.VideoTtemLayoutUtils;
import com.talkcloud.roomsdk.RoomControler;
import com.talkcloud.roomsdk.RoomUser;
import com.talkcloud.roomsdk.TKRoomManager;
import com.talkcloud.roomsdk.Trophy;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tkwebrtc.EglBase;
import org.tkwebrtc.RendererCommon;
import org.tkwebrtc.SurfaceViewRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import skin.support.widget.SkinCompatSupportable;

import android.animation.AnimatorSet;
import android.animation.Animator;


public class OneToManyActivity extends FragmentActivity implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener, WBStateCallBack, CompoundButton.OnCheckedChangeListener,
        CoursePopupWindowUtils.PopupWindowClick, ChatWindowPop.ChatPopupWindowClick, AllPopupWindowClick, PageNumberPopupWindowUtils.PageNumberClick,
        PlaySpeedPopupWindowUtils.PlaySpeedClick, SendGiftPopUtils.SendGift, MemberListPopupWindowUtils.CloseMemberListWindow {

    //顶部工具条
    private RelativeLayout rel_tool_bar, rl_back, re_top_right;
    private TextView txt_room_name, txt_class_begin, txt_hand_up;
    private TextView txt_hour, txt_min, txt_ss;
    private TextView txt_mao_01, txt_mao_02;
    private LinearLayout lin_time, ll_top;

    //加载框
    private RelativeLayout re_loading;
    private ImageView loadingImageView;
    private FrameAnimation frameAnimation;
    private TextView tv_load;

    // 底部菜单
    private RelativeLayout re_menu, rl_message, re_bottom, ll_member_list;
    private CheckBox cb_message, cb_tool_case, cb_file_person_media_list;
    private LinearLayout ll_control, ll_choice_number, ll_to_left, ll_to_right, ll_bottom_right,
            ll_small_white_board, ll_large_white_board, ll_tool_case, ll_file_list;
    private CheckBox cb_choose_photo, cb_control, cb_large_or_small, cb_choose_mouse, cb_remark, cb_member_list;
    private TextView tv_choose_number, tv_no_read_message_number;
    private ImageView iv_page_choose_icon, iv_to_left, iv_to_right, iv_small_white_board, iv_large_white_board, iv_hand;

    //MP3音频
    private GifImageView img_disk;
    private GifDrawable gifDrawable;
    private LinearLayout lin_audio_control;
    private ImageView img_play_mp3;
    private TextView txt_mp3_name;
    private TextView txt_mp3_time;
    private SeekBar sek_mp3;
    private ImageView img_voice_mp3;
    private SeekBar sek_voice_mp3;
    private LinearLayout ll_close_mp3;

    //回放使用的控件
    private LinearLayout rel_play_back, ll_voice;
    private ImageView img_play_back, img_back_play_voice;
    private SeekBar sek_play_back, sek_back_play_voice;
    private TextView txt_play_back_time, tv_play_speed;
    private RelativeLayout rel_pla_back_bar, rl_play_back;

    private View v_students;
    private RelativeLayout rel_students;
    private RelativeLayout rel_parent;
    private RelativeLayout rel_wb_container;

    private ArrayList<VideoItem> videoItems = new ArrayList<VideoItem>();
    private ArrayList<VideoItem> notMoveVideoItems = new ArrayList<VideoItem>();
    private ArrayList<VideoItem> movedVideoItems = new ArrayList<VideoItem>();
    private MemberListAdapter memberListAdapter;
    private FileListAdapter fileListAdapter;
    private MediaListAdapter mediaListAdapter;
    private ChatListAdapter chlistAdapter;

    private PowerManager pm;
    private PowerManager.WakeLock mWakeLock;
    private WBFragment wbFragment;
    private FragmentManager mediafragmentManager;
    private FragmentTransaction ft;
    private VideoFragment videofragment;

    private boolean canClassDissMiss = false;
    private boolean isZoom = false;
    private ArrayList<TimeMessage> timeMessages = new ArrayList<TimeMessage>();
    private boolean isBackApp = false;
    private Map<String, Object> onUpdateAttributeAttrs;
    private Map<String, Object> mediaAttrs;
    private String updateAttributespeerId;
    private String mediaPeerId;
    private AudioManager audioManager;
    private String mp3Duration = "00:00";
    private double vol = 0.5;
    private static boolean isMediaMute = false;
    private boolean isPlayBackPlay = true;
    private boolean isEnd = false;

    private ScreenFragment screenFragment;
    private MovieFragment movieFragment;
    private JSONObject videoarr = null;
    private Iterator<String> sIterator = null;
    private Map<String, MoveVideoInfo> stuMoveInfoMap = new HashMap<String, MoveVideoInfo>();
    private Map<String, Float> scalemap = new HashMap<String, Float>();
    private boolean isPauseLocalVideo = false;
    private boolean isOpenCamera = false;
    private NotificationManager nm = null;
    private NotificationCompat.Builder mBuilder = null;

    private JSONArray screen = null;
    private ArrayList<String> screenID = new ArrayList<>();

    static final int NONE = 0;
    int mode = NONE; // 当前的事件
    static final int DRAG = 1; // 拖动中
    static final int ZOOM = 2; // 缩放中
    private float beforeLenght, afterLenght; // 两触点距离
    private double printWidth, printHeight, nameLabelHeight;
    private CoursePopupWindowUtils coursePopupWindowUtils;
    private ChatWindowPop chatUtils;
    private AllActionUtils allActionUtils;
    private MemberListPopupWindowUtils memberListPopupWindowUtils;
    private PageNumberPopupWindowUtils pageNumberPopupWindowUtils;
    private PlaySpeedPopupWindowUtils playSpeedPopupWindowUtils;
    private SendGiftPopUtils sendGiftPopUtils;

    private PopupWindow studentPopupWindow;
    private PopupWindow popupWindowPhoto;
    private boolean huawei, oppo, voio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_many, null);
        ScreenScale.scaleView(view, "OneToManyActivity  ----    onCreate");
        setContentView(view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        RoomSession.getInstance().init(this);

        initTimeMessage();
        resultActivityBackApp();
        initMediaView();

        coursePopupWindowUtils = new CoursePopupWindowUtils(this, RoomSession.memberList,
                RoomSession.serial, RoomSession.serial);
        coursePopupWindowUtils.setPopupWindowClick(this);

        memberListPopupWindowUtils = new MemberListPopupWindowUtils(this, RoomSession.memberList);
        memberListPopupWindowUtils.setPopupWindowClick(this);

        chatUtils = new ChatWindowPop(this, RoomSession.chatList, view);
        chatUtils.setChatPopupWindowClick(this);
        allActionUtils = new AllActionUtils(this);
        allActionUtils.setAllPopupWindowClick(this);
        pageNumberPopupWindowUtils = new PageNumberPopupWindowUtils(this);
        pageNumberPopupWindowUtils.setPageNumberClick(this);
        playSpeedPopupWindowUtils = new PlaySpeedPopupWindowUtils(this);
        sendGiftPopUtils = new SendGiftPopUtils(this);
        sendGiftPopUtils.preLoadImage();
        sendGiftPopUtils.setOnSendGiftClick(this);

        //顶部工具条
        rel_tool_bar = (RelativeLayout) findViewById(R.id.rel_tool_bar);
        txt_hour = (TextView) rel_tool_bar.findViewById(R.id.txt_hour);
        txt_min = (TextView) rel_tool_bar.findViewById(R.id.txt_min);
        txt_ss = (TextView) rel_tool_bar.findViewById(R.id.txt_ss);
        txt_hand_up = (TextView) rel_tool_bar.findViewById(R.id.txt_hand_up);
        txt_room_name = (TextView) rel_tool_bar.findViewById(R.id.txt_pad_name);
        rl_back = (RelativeLayout) rel_tool_bar.findViewById(R.id.rl_back);
        txt_class_begin = (TextView) rel_tool_bar.findViewById(R.id.txt_class_begin);
        lin_time = (LinearLayout) rel_tool_bar.findViewById(R.id.lin_time);
        txt_mao_01 = (TextView) rel_tool_bar.findViewById(R.id.txt_mao_01);
        txt_mao_02 = (TextView) rel_tool_bar.findViewById(R.id.txt_mao_02);
        cb_choose_photo = (CheckBox) findViewById(R.id.cb_choose_photo);
        ll_top = (LinearLayout) rel_tool_bar.findViewById(R.id.ll_top);
        re_top_right = (RelativeLayout) rel_tool_bar.findViewById(R.id.re_top_right);


        re_loading = (RelativeLayout) findViewById(R.id.re_laoding);
        loadingImageView = (ImageView) re_loading.findViewById(R.id.loadingImageView);
        tv_load = (TextView) re_loading.findViewById(R.id.tv_load);
        if (frameAnimation == null) {
            frameAnimation = new FrameAnimation(loadingImageView, getRes(), 100, true, false, this);
        }

        //底部菜单
        re_menu = (RelativeLayout) findViewById(R.id.re_menu);
        ll_control = (LinearLayout) re_menu.findViewById(R.id.ll_control);
        cb_file_person_media_list = (CheckBox) re_menu.findViewById(R.id.cb_file_person_media_list);
        cb_tool_case = (CheckBox) re_menu.findViewById(R.id.cb_tool_case);
        ll_tool_case = (LinearLayout) re_menu.findViewById(R.id.ll_tool_case);
        rl_message = (RelativeLayout) re_menu.findViewById(R.id.rl_message);
        ll_file_list = (LinearLayout) re_menu.findViewById(R.id.ll_file_list);
        cb_message = (CheckBox) re_menu.findViewById(R.id.cb_message);
        cb_control = (CheckBox) re_menu.findViewById(R.id.cb_control);
        cb_large_or_small = (CheckBox) re_menu.findViewById(R.id.cb_large_or_small);
        cb_choose_mouse = (CheckBox) re_menu.findViewById(R.id.cb_choose_mouse);
        cb_remark = (CheckBox) re_menu.findViewById(R.id.cb_remark);
        cb_member_list = (CheckBox) re_menu.findViewById(R.id.cb_member_list);
        iv_large_white_board = (ImageView) re_menu.findViewById(R.id.iv_large_white_board);
        iv_small_white_board = (ImageView) re_menu.findViewById(R.id.iv_small_white_board);
        ll_large_white_board = (LinearLayout) re_menu.findViewById(R.id.ll_large_white_board);
        ll_small_white_board = (LinearLayout) re_menu.findViewById(R.id.ll_small_white_board);
        re_bottom = (RelativeLayout) re_menu.findViewById(R.id.re_bottom);
        ll_bottom_right = (LinearLayout) re_menu.findViewById(R.id.ll_bottom_right);
        ll_member_list = (RelativeLayout) re_menu.findViewById(R.id.ll_member_list);
        iv_hand = (ImageView) re_menu.findViewById(R.id.iv_hand);

        iv_to_left = (ImageView) re_menu.findViewById(R.id.iv_to_left);
        iv_to_right = (ImageView) re_menu.findViewById(R.id.iv_to_right);

        ll_to_left = (LinearLayout) re_menu.findViewById(R.id.ll_to_left);
        ll_to_right = (LinearLayout) re_menu.findViewById(R.id.ll_to_right);

        ll_choice_number = (LinearLayout) re_menu.findViewById(R.id.ll_choice_number);
        iv_page_choose_icon = (ImageView) re_menu.findViewById(R.id.iv_page_choose_icon);
        tv_no_read_message_number = (TextView) re_menu.findViewById(R.id.tv_no_read_message_number);
        tv_choose_number = (TextView) re_menu.findViewById(R.id.tv_choose_number);

        v_students = (View) findViewById(R.id.v_student);
        rel_students = (RelativeLayout) findViewById(R.id.rel_students);
        rel_parent = (RelativeLayout) findViewById(R.id.rel_parent);
        rel_wb_container = (RelativeLayout) findViewById(R.id.rel_wb_container);

        //回放界面
        rel_play_back = (LinearLayout) findViewById(R.id.rel_play_back);
        img_play_back = (ImageView) rel_play_back.findViewById(R.id.img_play_back);
        sek_play_back = (SeekBar) rel_play_back.findViewById(R.id.sek_play_back);
        txt_play_back_time = (TextView) rel_play_back.findViewById(R.id.txt_play_back_time);
        tv_play_speed = (TextView) rel_play_back.findViewById(R.id.tv_play_speed);
        img_back_play_voice = (ImageView) rel_play_back.findViewById(R.id.img_back_play_voice);
        sek_back_play_voice = (SeekBar) rel_play_back.findViewById(R.id.sek_back_play_voice);
        ll_voice = (LinearLayout) rel_play_back.findViewById(R.id.ll_voice);
        rel_pla_back_bar = (RelativeLayout) findViewById(R.id.rel_pla_back_bar);
        rl_play_back = (RelativeLayout) rel_pla_back_bar.findViewById(R.id.rl_play_back);

        initPlayBackView();

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 75, 0);

        ll_choice_number.setOnClickListener(this);
        txt_class_begin.setOnClickListener(this);
        txt_class_begin.setClickable(false);
        rl_back.setOnClickListener(this);

        cb_choose_photo.setOnCheckedChangeListener(this);
        cb_message.setOnCheckedChangeListener(this);
        cb_control.setOnCheckedChangeListener(this);
        cb_file_person_media_list.setOnCheckedChangeListener(this);
        cb_tool_case.setOnCheckedChangeListener(this);
        cb_choose_mouse.setOnCheckedChangeListener(this);
        cb_large_or_small.setOnCheckedChangeListener(this);
        cb_member_list.setOnCheckedChangeListener(this);
        ll_large_white_board.setOnClickListener(this);
        ll_small_white_board.setOnClickListener(this);
        ll_to_left.setOnClickListener(this);
        ll_to_right.setOnClickListener(this);
        tv_play_speed.setOnClickListener(this);
        rl_play_back.setOnClickListener(this);

        chlistAdapter = chatUtils.getChatListAdapter();
        fileListAdapter = coursePopupWindowUtils.getFileListAdapter();
        mediaListAdapter = coursePopupWindowUtils.getMediaListAdapter();
        memberListAdapter = memberListPopupWindowUtils.getMemberListAdapter();

        wbFragment = new WBFragment();
        if (RoomSession.path != null && !RoomSession.path.isEmpty()) {
            wbFragment.setPlayBack(true);
        }

        WhiteBoradManager.getInstance().setWBCallBack(this);
        WhiteBoradManager.getInstance().setFileServierUrl(RoomSession.host);
        WhiteBoradManager.getInstance().setFileServierPort(RoomSession.port);
        WhiteBoradManager.getInstance().setLocalControl(wbFragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (!wbFragment.isAdded()) {
            ft.add(R.id.wb_container, wbFragment);
            ft.commit();
        }
        TKRoomManager.getInstance().setWhiteBoard(WBSession.getInstance());

        huawei = FullScreenTools.hasNotchInScreen(this);
        oppo = FullScreenTools.hasNotchInOppo(this);
        voio = FullScreenTools.hasNotchInScreenAtVoio(this);
    }

    int wid_ratio = 4;
    int hid_ratio = 3;

    private void initWidAndHid() {
        if (TKRoomManager.getInstance().get_room_video_height() != 0 && TKRoomManager.getInstance().get_room_video_width() != 0) {
            wid_ratio = TKRoomManager.getInstance().get_room_video_width();
            hid_ratio = TKRoomManager.getInstance().get_room_video_height();
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int wid = dm.widthPixels;

        if (RoomSession.maxVideo > 7) {
            printWidth = (wid - 8 * 14) / 13;
        } else {
            printWidth = (wid - 8 * 8) / 7;
        }
        printHeight = (int) ((printWidth * (double) hid_ratio / (double) wid_ratio));
        nameLabelHeight = (int) (printWidth * ((double) 45 / (double) 240));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    private void initTimeMessage() {
        timeMessages.clear();
        String one = "<font color='#FFD700'>1</font>";
        TimeMessage tms1 = new TimeMessage();
        tms1.message = getString(R.string.classroom_tip_01_01) + "<font color='#FFD700'>1</font>" + getString(R.string.classroom_tip_01_02);
        TimeMessage tms2 = new TimeMessage();
        tms2.message = getString(R.string.classroom_tip_02_01) + "<font color='#FFD700'>1</font>" + getString(R.string.classroom_tip_02_02);
        TimeMessage tms3 = new TimeMessage();
        tms3.message = getString(R.string.classroom_tip_03_01) + "<font color='#FFD700'>1</font>" + getString(R.string.classroom_tip_03_02);
        TimeMessage tms4 = new TimeMessage();
        tms4.message = getString(R.string.classroom_tip_04_01) + "<font color='#FFD700'>3</font>" + getString(R.string.classroom_tip_04_02) + "<font color='#FFD700'>2</font>" + getString(R.string.classroom_tip_04_03);
        TimeMessage tms5 = new TimeMessage();
        tms5.message = getString(R.string.classroom_tip_05);
        tms5.hasKonwButton = false;
        timeMessages.add(tms1);
        timeMessages.add(tms2);
        timeMessages.add(tms3);
        timeMessages.add(tms4);
        timeMessages.add(tms5);
    }

    private void initMediaView() {
        img_disk = (GifImageView) findViewById(R.id.img_disk);
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.play_mp3_gif);
            img_disk.setImageDrawable(gifDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        lin_audio_control = (LinearLayout) findViewById(R.id.lin_audio_control);
        img_play_mp3 = (ImageView) lin_audio_control.findViewById(R.id.img_play);
        txt_mp3_name = (TextView) lin_audio_control.findViewById(R.id.txt_media_name);
        txt_mp3_time = (TextView) lin_audio_control.findViewById(R.id.txt_media_time);
        sek_mp3 = (SeekBar) lin_audio_control.findViewById(R.id.sek_media);
        sek_mp3.setPadding((int) (10 * ScreenScale.getWidthScale()), 0, (int) (10 * ScreenScale.getWidthScale()), 0);
        img_voice_mp3 = (ImageView) lin_audio_control.findViewById(R.id.img_media_voice);
        sek_voice_mp3 = (SeekBar) lin_audio_control.findViewById(R.id.sek_media_voice);
        sek_voice_mp3.setPadding((int) (10 * ScreenScale.getWidthScale()), 0, (int) (10 * ScreenScale.getWidthScale()), 0);
        ll_close_mp3 = (LinearLayout) lin_audio_control.findViewById(R.id.ll_close_mp3);

        ll_close_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TKRoomManager.getInstance().stopShareMedia();
                lin_audio_control.setVisibility(View.INVISIBLE);
                img_disk.setVisibility(View.GONE);
            }
        });
        img_play_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaAttrs != null) {
                    if (RoomSession.isPublish) {
                        TKRoomManager.getInstance().playMedia((Boolean) mediaAttrs.get("pause") == null ? false : (Boolean) mediaAttrs.get("pause"));
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
                            TKRoomManager.getInstance().startShareMedia(url, false, "__all", attrMap);
                        } else {
                            TKRoomManager.getInstance().startShareMedia(url, false, TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                        }
                    }
                }
            }
        });

        sek_mp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                if (isfromUser && mediaAttrs != null) {
                    currenttime = ((double) pro / (double) seekBar.getMax()) * (int) mediaAttrs.get("duration");
                    TKRoomManager.getInstance().seekMedia((long) currenttime);
                }
            }
        });

        img_voice_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMediaMute) {
                    TKRoomManager.getInstance().setRemoteAudioVolume(vol, mediaPeerId, 2);
                    img_voice_mp3.setImageResource(R.drawable.icon_voice);
                    sek_voice_mp3.setProgress((int) (vol * 100));
                } else {
                    TKRoomManager.getInstance().setRemoteAudioVolume(0, mediaPeerId, 2);
                    img_voice_mp3.setImageResource(R.drawable.icon_no_voice);
                    sek_voice_mp3.setProgress(0);
                }
                isMediaMute = !isMediaMute;
            }
        });
        sek_voice_mp3.setProgress((int) (vol * 100));
        sek_voice_mp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = (float) progress / (float) seekBar.getMax();
                if (vol > 0) {
                    img_voice_mp3.setImageResource(R.drawable.icon_voice);
                } else {
                    img_voice_mp3.setImageResource(R.drawable.icon_no_voice);
                }
                TKRoomManager.getInstance().setRemoteAudioVolume(vol, mediaPeerId, 2);
                if (fromUser) {
                    OneToManyActivity.this.vol = vol;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initPlayBackView() {

        rel_play_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        sek_play_back.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            boolean isfromUser = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    this.progress = progress;
                    isfromUser = fromUser;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                long pos = (long) (((double) progress / 100) * (endtime - starttime) + starttime);
                if (isEnd) {
                    img_play_back.setImageResource(R.drawable.btn_pause_normal);
                    TKRoomManager.getInstance().playPlayback();
                    isPlayBackPlay = !isPlayBackPlay;
                }
                isEnd = false;
                TKRoomManager.getInstance().seekPlayback(pos);
            }
        });

        img_play_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayBackPlay) {
                    TKRoomManager.getInstance().pausePlayback();
                    img_play_back.setImageResource(R.drawable.btn_play_normal);
                } else {
                    if (isEnd) {
                        TKRoomManager.getInstance().seekPlayback(starttime);
                        TKRoomManager.getInstance().playPlayback();
                        currenttime = starttime;
                        img_play_back.setImageResource(R.drawable.btn_pause_normal);
                        isEnd = false;
                    } else {
                        TKRoomManager.getInstance().playPlayback();
                        img_play_back.setImageResource(R.drawable.btn_pause_normal);
                    }
                }
                isPlayBackPlay = !isPlayBackPlay;
                WhiteBoradManager.getInstance().playbackPlayAndPauseController(isPlayBackPlay);
            }
        });

       /* final AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        final int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        sek_back_play_voice.setProgress((int) (current / max * 100));

        sek_back_play_voice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float vol = (float) progress / (float) seekBar.getMax();
                if (vol > 0) {
                    img_back_play_voice.setImageResource(R.drawable.icon_voice);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, (int) (vol * max), 0);
                } else {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0, 0);
                    img_back_play_voice.setImageResource(R.drawable.icon_no_voice);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/
    }

    @Override
    protected void onStart() {

        if (TKRoomManager.getInstance().getMySelf() != null) {
            nm.cancel(2);
            isBackApp = false;
            if (TKRoomManager.getInstance().getMySelf().role == 2 || TKRoomManager.getInstance().getMySelf().role == 0) {
                TKRoomManager.getInstance().setInBackGround(false);
                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "isInBackGround", false);
            }
        }
        isOpenCamera = false;
        mWakeLock.acquire();

        super.onStart();

        //预加载popupwindow布局
        coursePopupWindowUtils.initCoursePopupWindow(0);

        NotificationCenter.getInstance().addObserver(this, RoomSession.onRoomJoin);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRoomLeave);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onError);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onWarning);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUserJoin);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUserLeft);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUserPropertyChanged);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUserPublishState);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onKickedout);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onMessageReceived);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRemotePubMsg);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRemoteDelMsg);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onUpdateAttributeStream);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onPlayBackClearAll);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onPlayBackUpdateTime);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onPlayBackDuration);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onPlayBackEnd);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onShareMediaState);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onShareScreenState);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onShareFileState);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onAudioVolume);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRoomUser);
        NotificationCenter.getInstance().addObserver(this, RoomSession.onRoomUserNumber);
        RoomSession.getInstance().onStart();

        if (RoomSession.isInRoom) {
            initViewByRoomTypeAndTeacher();
        }
        doPlayVideo(null);
        if (TKRoomManager.getInstance().getMySelf() == null) {
            return;
        }
        if (TKRoomManager.getInstance().getMySelf().publishState == 1 || TKRoomManager.getInstance().getMySelf().publishState == 3) {
            TKRoomManager.getInstance().enableSendMyVoice(true);
        }
        TKRoomManager.getInstance().enableOtherAudio(false);
        TKRoomManager.getInstance().setMuteAllStream(false);
        RoomSession.getInstance().closeSpeaker();

        if (RoomSession.isClassBegin) {
            TKRoomManager.getInstance().pubMsg("UpdateTime", "UpdateTime", TKRoomManager.getInstance().getMySelf().peerId, null, false, null, null);
        }

        if (!RoomSession.isInRoom) {
            re_loading.setVisibility(View.VISIBLE);
            if (frameAnimation != null) {
                frameAnimation.playAnimation();
                tv_load.setText(getString(R.string.joining_classroom_home));
            }
        }
    }

    @Override
    protected void onStop() {
        if (!isFinishing()) {
            if (!isBackApp) {
                nm.notify(2, mBuilder.build());
            }
            if (TKRoomManager.getInstance().getMySelf() != null) {
                if (TKRoomManager.getInstance().getMySelf().role == 2 || TKRoomManager.getInstance().getMySelf().role == 0) {
                    TKRoomManager.getInstance().setInBackGround(true);
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "isInBackGround", true);
                }
            }
        }
        mWakeLock.release();
        if (isFinishing()) {
            timeMessages.clear();
        }
        RoomSession.getInstance().onStop();
        NotificationCenter.getInstance().removeObserver(this);
        super.onStop();
    }

    private void initViewByRoomTypeAndTeacher() {
        if (RoomSession.roomType == 0) {//一对一
            if (TKRoomManager.getInstance().getMySelf().role == 0) {//老师
                if (!RoomControler.isShowClassBeginButton()) {
                    txt_class_begin.setVisibility(View.VISIBLE);
                }
                if (RoomSession.isClassBegin) {
                    ll_tool_case.setVisibility(View.VISIBLE);
                    cb_choose_mouse.setVisibility(View.VISIBLE);
                    ll_control.setVisibility(View.VISIBLE);
                } else {
                    ll_tool_case.setVisibility(View.GONE);
                    cb_choose_mouse.setVisibility(View.GONE);
                    ll_control.setVisibility(View.GONE);
                }
                ll_file_list.setVisibility(View.VISIBLE);
                rel_play_back.setVisibility(View.INVISIBLE);
            } else if (TKRoomManager.getInstance().getMySelf().role == 2) {//学生
                ll_file_list.setVisibility(View.GONE);
                ll_tool_case.setVisibility(View.GONE);
                txt_class_begin.setVisibility(View.INVISIBLE);
                ll_control.setVisibility(View.GONE);
                ll_member_list.setVisibility(View.GONE);
                if (RoomSession.isClassBegin) {
                    txt_hand_up.setVisibility(View.VISIBLE);
                } else {
                    cb_choose_mouse.setVisibility(View.INVISIBLE);
                    txt_hand_up.setVisibility(View.INVISIBLE);
                }
                rel_play_back.setVisibility(View.INVISIBLE);
            } else if (TKRoomManager.getInstance().getMySelf().role == 4) {

                if (!RoomControler.isShowClassBeginButton() && !RoomSession.isClassBegin) {
                    txt_class_begin.setVisibility(View.INVISIBLE);
                }

                rel_play_back.setVisibility(View.INVISIBLE);
                cb_choose_mouse.setVisibility(View.GONE);
                cb_choose_photo.setVisibility(View.GONE);
                cb_remark.setVisibility(View.GONE);
                ll_tool_case.setVisibility(View.GONE);
                ll_control.setVisibility(View.GONE);

                ll_choice_number.setEnabled(false);


            } else if (TKRoomManager.getInstance().getMySelf().role == -1) {
                rel_play_back.setVisibility(View.VISIBLE);

                lin_time.setVisibility(View.GONE);
                ll_voice.setVisibility(View.GONE);
                cb_choose_mouse.setVisibility(View.GONE);
                cb_choose_photo.setVisibility(View.GONE);
                ll_choice_number.setVisibility(View.GONE);
                ll_to_right.setVisibility(View.GONE);
                ll_large_white_board.setVisibility(View.GONE);
                ll_small_white_board.setVisibility(View.GONE);
                cb_large_or_small.setVisibility(View.GONE);
                cb_remark.setVisibility(View.GONE);
                ll_bottom_right.setVisibility(View.GONE);
                ll_to_left.setVisibility(View.GONE);
                ll_member_list.setVisibility(View.GONE);

                rel_pla_back_bar.setVisibility(View.VISIBLE);
            }
        } else {  //一对多
            if (TKRoomManager.getInstance().getMySelf().role == 0) {//老师
                ll_file_list.setVisibility(View.VISIBLE);
                if (!RoomControler.isShowClassBeginButton()) {
                    txt_class_begin.setVisibility(View.VISIBLE);
                }
                if (RoomSession.isClassBegin) {
                    ll_tool_case.setVisibility(View.VISIBLE);
                    cb_choose_mouse.setVisibility(View.VISIBLE);
                    ll_control.setVisibility(View.VISIBLE);
                } else {
                    cb_choose_mouse.setVisibility(View.GONE);
                    ll_tool_case.setVisibility(View.GONE);
                    ll_control.setVisibility(View.GONE);
                }
                rel_play_back.setVisibility(View.INVISIBLE);
            } else if (TKRoomManager.getInstance().getMySelf().role == 2) {//学生
                ll_file_list.setVisibility(View.GONE);
                ll_tool_case.setVisibility(View.GONE);
                ll_control.setVisibility(View.GONE);
                ll_member_list.setVisibility(View.GONE);
                if (RoomSession.isClassBegin) {
                    txt_hand_up.setVisibility(View.VISIBLE);
                } else {
                    txt_hand_up.setVisibility(View.INVISIBLE);
                    cb_choose_mouse.setVisibility(View.INVISIBLE);
                }
                txt_class_begin.setVisibility(View.INVISIBLE);
                rel_play_back.setVisibility(View.INVISIBLE);
            } else if (TKRoomManager.getInstance().getMySelf().role == 4) {
                if (!RoomControler.isShowClassBeginButton() && !RoomSession.isClassBegin)
                    txt_class_begin.setVisibility(View.INVISIBLE);
                rel_play_back.setVisibility(View.INVISIBLE);
                cb_choose_mouse.setVisibility(View.GONE);
                cb_choose_photo.setVisibility(View.GONE);
                cb_remark.setVisibility(View.GONE);
                ll_tool_case.setVisibility(View.GONE);
                ll_control.setVisibility(View.GONE);

                ll_choice_number.setEnabled(false);

            } else if (TKRoomManager.getInstance().getMySelf().role == -1) {
                rel_play_back.setVisibility(View.VISIBLE);

                lin_time.setVisibility(View.GONE);
                ll_voice.setVisibility(View.GONE);
                cb_choose_mouse.setVisibility(View.GONE);
                cb_choose_photo.setVisibility(View.GONE);
                ll_choice_number.setVisibility(View.GONE);
                ll_to_right.setVisibility(View.GONE);
                ll_large_white_board.setVisibility(View.GONE);
                ll_small_white_board.setVisibility(View.GONE);
                cb_large_or_small.setVisibility(View.GONE);
                cb_remark.setVisibility(View.GONE);
                ll_bottom_right.setVisibility(View.GONE);
                ll_to_left.setVisibility(View.GONE);
                ll_member_list.setVisibility(View.GONE);

                rel_pla_back_bar.setVisibility(View.VISIBLE);
            }
        }
        doLayout();
    }

    private void doPlayVideo(String peerId) {
        if (isZoom || TextUtils.isEmpty(peerId)) {
            return;
        }
        RoomSession.getInstance().getPlayingList();
        for (int i = 0; i < RoomSession.playingList.size(); i++) {
            final RoomUser user = RoomSession.playingList.get(i);
            if (user == null) {
                return;
            }
            do1vsnStudentPlayVideo(user, peerId != null);
        }
    }

    private int sitpos = -1;

    private void do1vsnStudentPlayVideo(final RoomUser user, boolean force) {
        boolean hasSit = false;
        sitpos = -1;
        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(user.peerId)) {
                hasSit = true;
                sitpos = i;
            }
        }
        if (!hasSit) {
            final VideoItem stu = new VideoItem();
            final LinearLayout vdi_stu = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.video_item, null);
            stu.parent = vdi_stu;
            stu.sf_video = (SurfaceViewRenderer) vdi_stu.findViewById(R.id.sf_video);
            stu.sf_video.init(EglBase.create().getEglBaseContext(), null);
            stu.sf_video.setZOrderMediaOverlay(true);
            stu.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            stu.img_mic = (ImageView) vdi_stu.findViewById(R.id.img_mic);
            stu.img_pen = (ImageView) vdi_stu.findViewById(R.id.img_pen);
            stu.img_hand = (ImageView) vdi_stu.findViewById(R.id.img_hand_up);
            stu.txt_name = (AutoFitTextView) vdi_stu.findViewById(R.id.txt_name);
            stu.txt_gift_num = (AutoFitTextView) vdi_stu.findViewById(R.id.txt_gift_num);
            stu.rel_group = (RelativeLayout) vdi_stu.findViewById(R.id.rel_group);
            stu.img_video_back = (ImageView) vdi_stu.findViewById(R.id.img_video_back);
            stu.lin_gift = (LinearLayout) vdi_stu.findViewById(R.id.lin_gift);
            stu.lin_name_label = (LinearLayout) vdi_stu.findViewById(R.id.lin_name_label);
            stu.icon_gif = (ImageView) vdi_stu.findViewById(R.id.icon_gif);
            stu.rel_video_label = (RelativeLayout) vdi_stu.findViewById(R.id.rel_video_label);
            stu.re_background = (RelativeLayout) vdi_stu.findViewById(R.id.re_background);
            stu.tv_home = (TextView) vdi_stu.findViewById(R.id.tv_home);
            stu.re_bg = (RelativeLayout) vdi_stu.findViewById(R.id.re_bg);
            stu.bg_video_back = (ImageView) vdi_stu.findViewById(R.id.bg_video_back);
            stu.peerid = user.peerId;
            stu.role = user.role;
            stu.txt_name.setText(user.nickName);

            if (user.role == 0) {
                stu.lin_name_label.setBackgroundResource(R.drawable.rounded_video_person_name_teacher);
                stu.re_bg.setBackgroundResource(R.drawable.video_frame_teacher);
            } else {
                stu.lin_name_label.setBackgroundResource(R.drawable.rounded_video_person_name);
                stu.re_bg.setBackgroundResource(R.drawable.video_frame_student);
            }

            if (!RoomSession.isClassBegin) {
                stu.img_pen.setVisibility(View.INVISIBLE);
                stu.img_hand.setVisibility(View.INVISIBLE);
                stu.img_mic.setVisibility(View.INVISIBLE);
            }

            if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                if (RoomSession.isClassBegin) {
                    stu.img_hand.setVisibility(View.VISIBLE);
                }
            } else {
                stu.img_hand.setVisibility(View.GONE);
            }

            if (user.disableaudio) {
                stu.img_mic.setVisibility(View.VISIBLE);
                stu.img_mic.setImageResource(R.drawable.icon_audio_disable);
            } else {
                if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                    stu.img_mic.setImageResource(R.drawable.icon_audio_disable);
                    stu.img_mic.setVisibility(View.INVISIBLE);
                } else {
                    stu.img_mic.setVisibility(View.GONE);
                }
            }

            vdi_stu.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (RoomSession.userrole == 4) {
                        return false;
                    }
                    if (stu.isSplitScreen) {
                        return false;
                    }
                    if (!TKRoomManager.getInstance().getMySelf().canDraw) {
                        return false;
                    }
                    if (!RoomSession.isClassBegin) {
                        return false;
                    }
                    stu.canMove = true;
                    vdi_stu.setAlpha(0.5f);
                    return false;
                }
            });
            vdi_stu.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (RoomSession.userrole == 4) {
                        return false;
                    }
                    if (!TKRoomManager.getInstance().getMySelf().canDraw) {
                        return false;
                    }
                    if (stu.isSplitScreen) {
                        return false;
                    }
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            if (!stu.isMoved) {
                                stu.oldX = event.getRawX();
                                stu.oldY = event.getRawY() - rel_tool_bar.getHeight();
                            }
                            mode = DRAG;
                            if (!stu.canMove) {
                                return false;
                            }
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            mode = ZOOM;
                            beforeLenght = spacing(event);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mode == ZOOM) {
                                afterLenght = spacing(event);
                                float gapLenght = afterLenght - beforeLenght;
                                if (Math.abs(gapLenght) > 5f) {
                                    float scale = afterLenght / beforeLenght;
                                    LayoutZoomOrIn.zoomMouldVideoItem(stu, scale, rel_students, v_students);
                                    beforeLenght = afterLenght;
                                }
                            } else if (mode == DRAG) {
                                if (!stu.canMove) {
                                    return false;
                                }
                                if (event.getRawX() - stu.parent.getWidth() / 2 >= 0 &&
                                        event.getRawX() + stu.parent.getWidth() / 2 <= rel_students.getRight() &&
                                        event.getRawY() - stu.parent.getHeight() >= rel_students.getTop() &&
                                        event.getRawY() <= rel_students.getBottom()) {


                                    if (stu.parent.getWidth() < printWidth && stu.parent.getHeight() < printHeight) {
                                        LayoutSizeUilts.moveVideoSize(stu, printWidth, printHeight, nameLabelHeight);
                                    }

                                    LayoutZoomOrIn.layoutVideo(stu, (int) (event.getRawX() - stu.parent.getWidth() / 2),
                                            (int) (event.getRawY() - stu.parent.getHeight()));
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            vdi_stu.setAlpha(1f);
                            if (mode == DRAG) {
                                vdi_stu.setAlpha(1f);
                                if (!stu.canMove) {
                                    return false;
                                }
                                stu.canMove = false;

                                if (vdi_stu.getTop() + 10 > v_students.getBottom()) {
                                    stu.isMoved = true;
                                } else {
                                    stu.isMoved = false;
                                    stu.isSefMoved = false;
                                }

                                if (screenID.size() > 0 && !screenID.contains(stu.peerid)) {
                                    screenID.add(stu.peerid);
                                }
                                do1vsnStudentVideoLayout();
                                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                                    studentMoveOrScreen();
                                }
                                mode = NONE;
                            } else if (mode == ZOOM) {
                                vdi_stu.setAlpha(1f);

                                do1vsnStudentVideoLayout();
                                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                                    sendScaleVideoItem(true);
                                    sendStudentMove();
                                }
                                mode = NONE;
                            }
                            break;
                    }
                    return true;
                }
            });
            if (user.role == 2) {
                stu.lin_gift.setVisibility(View.VISIBLE);
            } else {
                stu.lin_gift.setVisibility(View.INVISIBLE);
            }
            if (user.role == 0) {
                videoItems.add(0, stu);
            } else {
                videoItems.add(stu);
            }
            rel_students.addView(vdi_stu);
            do1vsnStudentVideoLayout();
            if (user.publishState > 1 && user.publishState < 4 && !RoomSession.isPublish) {
                if (user.hasVideo) {
                    stu.sf_video.setVisibility(View.VISIBLE);
                    TKRoomManager.getInstance().playVideo(user.peerId, stu.sf_video, RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                    stu.img_video_back.setImageResource(R.drawable.icon_camera_close);
                    stu.bg_video_back.setVisibility(View.GONE);
                } else {
                    stu.sf_video.setVisibility(View.INVISIBLE);
                    stu.img_video_back.setImageResource(R.drawable.icon_no_camera);
                    stu.bg_video_back.setVisibility(View.VISIBLE);
                }
            } else {
                if (user.hasVideo) {
                    if ((mediaAttrs != null && mediaAttrs.containsKey("video") && (boolean) mediaAttrs.get("video"))) {
                        stu.img_video_back.setImageResource(R.drawable.icon_camera_close);
                        stu.bg_video_back.setVisibility(View.VISIBLE);
                        stu.sf_video.setVisibility(View.INVISIBLE);
                    } else {
                        if (user.publishState > 1 && user.publishState < 4) {
                            stu.sf_video.setVisibility(View.VISIBLE);
                            stu.bg_video_back.setVisibility(View.GONE);
                        } else {
                            stu.img_video_back.setImageResource(R.drawable.icon_camera_close);
                            stu.bg_video_back.setVisibility(View.VISIBLE);
                            stu.sf_video.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    stu.sf_video.setVisibility(View.INVISIBLE);
                    stu.img_video_back.setImageResource(R.drawable.icon_no_camera);
                    stu.bg_video_back.setVisibility(View.VISIBLE);
                }
            }
        } else if (force) {
            if (sitpos != -1) {
                if (user.publishState > 1 && user.publishState < 4 && !RoomSession.isPublish) {
                    if (user.hasVideo) {
                        videoItems.get(sitpos).img_video_back.setImageResource(R.drawable.icon_camera_close);
                        videoItems.get(sitpos).bg_video_back.setVisibility(View.GONE);
                        videoItems.get(sitpos).sf_video.setVisibility(View.VISIBLE);
                    } else {
                        videoItems.get(sitpos).img_video_back.setImageResource(R.drawable.icon_no_camera);
                        videoItems.get(sitpos).bg_video_back.setVisibility(View.VISIBLE);
                        videoItems.get(sitpos).sf_video.setVisibility(View.INVISIBLE);
                    }
                    TKRoomManager.getInstance().playVideo(user.peerId, videoItems.get(sitpos).sf_video, RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                } else {
                    if (user.hasVideo) {
                        if ((mediaAttrs != null && mediaAttrs.containsKey("video") && (boolean) mediaAttrs.get("video"))) {
                            videoItems.get(sitpos).img_video_back.setImageResource(R.drawable.icon_camera_close);
                            videoItems.get(sitpos).bg_video_back.setVisibility(View.VISIBLE);
                            videoItems.get(sitpos).sf_video.setVisibility(View.INVISIBLE);
                        } else {
                            if (user.publishState > 1 && user.publishState < 4) {
                                videoItems.get(sitpos).bg_video_back.setVisibility(View.GONE);
                                videoItems.get(sitpos).sf_video.setVisibility(View.VISIBLE);
                            } else {
                                videoItems.get(sitpos).img_video_back.setImageResource(R.drawable.icon_camera_close);
                                videoItems.get(sitpos).bg_video_back.setVisibility(View.VISIBLE);
                                videoItems.get(sitpos).sf_video.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        videoItems.get(sitpos).sf_video.setVisibility(View.INVISIBLE);
                        videoItems.get(sitpos).img_video_back.setImageResource(R.drawable.icon_no_camera);
                        videoItems.get(sitpos).bg_video_back.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        for (int i = 0; i < videoItems.size(); i++) {
            final VideoItem it = videoItems.get(i);
            it.parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (screenID.size() > 0) {
                        if (screenID.contains(it.peerid)) {
                            it.isSplitScreen = true;
                        }
                        do1vsnStudentVideoLayout();
                    }
                    if (scalemap != null && scalemap.size() > 0) {
                        if (scalemap.containsKey(it.peerid) && scalemap.get(it.peerid) != null) {
                            double scale = scalemap.get(it.peerid);
                            LayoutZoomOrIn.zoomMsgMouldVideoItem(it, scale, printWidth, printHeight,
                                    rel_students.getHeight() - v_students.getHeight());
                            scalemap.remove(it.peerid);
                        }
                    }

                    if (!it.isMoved) {
                        if (stuMoveInfoMap.containsKey(it.peerid) && stuMoveInfoMap.get(it.peerid) != null) {
                            String peerId = TKRoomManager.getInstance().getMySelf().peerId;
                            MoveVideoInfo mi = stuMoveInfoMap.get(it.peerid);
                            moveStudent(it.peerid, mi.top, mi.left, mi.isDrag);
                            stuMoveInfoMap.remove(it.peerid);
                        }
                        it.parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    private void sendStudentMove() {
        try {
            JSONObject data = new JSONObject();
            JSONObject moveData = new JSONObject();
            for (int i = 0; i < videoItems.size(); i++) {
                VideoItem it = videoItems.get(i);
                JSONObject md = new JSONObject();
                if (it.isMoved) {
                    double wid = rel_students.getWidth() - it.parent.getWidth();
                    double hid = rel_students.getHeight() - it.parent.getHeight() - v_students.getHeight();
                    double top = (it.parent.getTop() - v_students.getHeight()) / hid;
                    double left = it.parent.getLeft() / wid;
                    md.put("percentTop", top);
                    md.put("percentLeft", left);
                    md.put("isDrag", true);
                } else {
                    md.put("percentTop", 0);
                    md.put("percentLeft", 0);
                    md.put("isDrag", false);
                }
                moveData.put(it.peerid, md);
            }
            data.put("otherVideoStyle", moveData);
            TKRoomManager.getInstance().pubMsg("videoDraghandle", "videoDraghandle", "__allExceptSender", data.toString(), true, "ClassBegin", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void sendScaleVideoItem(boolean isZoom) {
        try {
            JSONObject data = new JSONObject();
            JSONObject scaleData = new JSONObject();
            for (int i = 0; i < videoItems.size(); i++) {
                VideoItem it = videoItems.get(i);
                JSONObject md = new JSONObject();
                double scale;
                if (isZoom) {
                    scale = it.parent.getHeight() / printHeight;
                } else {
                    scale = 1.0;
                }
                md.put("scale", scale);
                scaleData.put(it.peerid, md);
            }
            data.put("ScaleVideoData", scaleData);
            TKRoomManager.getInstance().pubMsg("VideoChangeSize", "VideoChangeSize", "__allExceptSender", data.toString(), true, "ClassBegin", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private float spacing(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }
        return 0.0f;
    }

    private void moveStudent(String peerid, float top, float left, boolean isDrag) {
        for (int i = 0; i < videoItems.size(); i++) {
            VideoItem it = videoItems.get(i);
            if (videoItems.get(i).peerid.equals(peerid)) {
                if (isDrag) {
                    int wid = (int) (rel_students.getWidth() - printWidth);
                    int hid = (int) (rel_students.getHeight() - printHeight - v_students.getHeight());
                    top = top * hid + v_students.getHeight();
                    left = left * wid;
                    it.isMoved = isDrag;
                    it.isSefMoved = true;
                    it.isMoved = true;
                    if (left < 0) {
                        left = 0;
                    }
                    if (top < v_students.getHeight()) {
                        top = v_students.getHeight();
                    }
                    int bottom = (int) (top + it.parent.getHeight());
                    if (bottom > rel_students.getHeight()) {
                        bottom = rel_students.getHeight();
                        top = (int) (rel_students.getHeight() - printHeight);
                    }

                    if (it.parent.getWidth() < printWidth && it.parent.getHeight() < printHeight) {
                        LayoutSizeUilts.moveVideoSize(it, printWidth, printHeight, nameLabelHeight);
                    }
                    LayoutZoomOrIn.layoutMouldVideo(it, (int) left, (int) top, bottom);
                } else {
                    it.isMoved = false;
                }
            }
        }
        do1vsnStudentVideoLayout();
    }

    private void do1vsnStudentUnPlayVideo(String peerId) {
        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(peerId)) {
                videoItems.get(i).sf_video.release();
                rel_students.removeView(videoItems.get(i).parent);
                videoItems.remove(i);
                do1vsnStudentVideoLayout();
            }
        }
    }

    boolean is_show_student_window = true;

    private void showStudentControlPop(final View view, final RoomUser user, final int index) {

        if (!is_show_student_window) {
            is_show_student_window = true;
            return;
        }

        if (!RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == 0) {
            return;
        }

        if (!(TKRoomManager.getInstance().getMySelf().role == 0) && !user.peerId.endsWith(TKRoomManager.getInstance().getMySelf().peerId)) {
            return;
        }
        if (user.peerId.endsWith(TKRoomManager.getInstance().getMySelf().peerId) && !RoomControler.isAllowStudentControlAV()) {
            return;
        }

       /* if (RoomSession.roomType != 0 && index >= videoItems.size()) {
            return;
        }*/

        if (!RoomSession.isClassBegin) {
            if (!RoomControler.isReleasedBeforeClass()) {
                return;
            }
        }

        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_student_control, null);

        ImageView img_up_arr = (ImageView) contentView.findViewById(R.id.up_arr);
        ImageView img_down_arr = (ImageView) contentView.findViewById(R.id.down_arr);

        ImageView img_right_arr = (ImageView) contentView.findViewById(R.id.right_arr);
        LinearLayout lin_candraw = (LinearLayout) contentView.findViewById(R.id.lin_candraw);
        LinearLayout lin_up_sd = (LinearLayout) contentView.findViewById(R.id.lin_up_sd);
        LinearLayout lin_audio = (LinearLayout) contentView.findViewById(R.id.lin_audio);
        LinearLayout lin_gift = (LinearLayout) contentView.findViewById(R.id.lin_gift);
        final ImageView img_candraw = (ImageView) contentView.findViewById(R.id.img_candraw);
        final ImageView img_up_sd = (ImageView) contentView.findViewById(R.id.img_up_sd);
        final ImageView img_audio = (ImageView) contentView.findViewById(R.id.img_audio);
        final TextView txt_candraw = (TextView) contentView.findViewById(R.id.txt_candraw);
        final TextView txt_up_sd = (TextView) contentView.findViewById(R.id.txt_up_sd);
        final TextView txt_audio = (TextView) contentView.findViewById(R.id.txt_audio);
        TextView txt_gift = (TextView) contentView.findViewById(R.id.txt_gift);

        LinearLayout lin_plit_screen = (LinearLayout) contentView.findViewById(R.id.lin_plit_screen);
        TextView txt_plit_screen = (TextView) contentView.findViewById(R.id.txt_plit_screen);
        ImageView img_plit_screen = (ImageView) contentView.findViewById(R.id.img_plit_screen);

        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            lin_plit_screen.setVisibility(View.VISIBLE);
        } else {
            lin_plit_screen.setVisibility(View.GONE);
        }

        if (index >= 0) {
            if (videoItems.get(index).isSplitScreen) {
                lin_candraw.setVisibility(View.GONE);
                txt_plit_screen.setText(getResources().getText(R.string.recovery));
                img_plit_screen.setImageResource(R.drawable.icon_reply_normal_only);
            } else {
                if (user.role != 1 && TKRoomManager.getInstance().getMySelf().role == 0) {
                    lin_candraw.setVisibility(View.VISIBLE);
                } else {
                    lin_candraw.setVisibility(View.GONE);
                }
                txt_plit_screen.setText(getResources().getText(R.string.plitscreen));
                img_plit_screen.setImageResource(R.drawable.icon_screen_normal);
            }
        }

        LinearLayout lin_video_control = (LinearLayout) contentView.findViewById(R.id.lin_video_control);
        final ImageView img_video_control = (ImageView) contentView.findViewById(R.id.img_camera);
        final TextView txt_video = (TextView) contentView.findViewById(R.id.txt_camera);
        if (user.role == 1) {
            lin_gift.setVisibility(View.GONE);
        } else {
            if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                lin_gift.setVisibility(View.GONE);
                lin_up_sd.setVisibility(View.GONE);
            } else {
                lin_gift.setVisibility(View.VISIBLE);
                lin_up_sd.setVisibility(View.VISIBLE);
            }
        }

        if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
            img_video_control.setImageResource(R.drawable.icon_control_camera_01);
            txt_video.setText(R.string.video_on);
        } else {
            img_video_control.setImageResource(R.drawable.icon_control_camera_02);
            txt_video.setText(R.string.video_off);
        }

        if (user.disableaudio) {
            lin_audio.setVisibility(View.GONE);
        } else {
            lin_audio.setVisibility(View.VISIBLE);
            if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                img_audio.setImageResource(R.drawable.icon_open_audio);
                txt_audio.setText(R.string.open_audio);
            } else {
                img_audio.setImageResource(R.drawable.icon_close_audio);
                txt_audio.setText(R.string.close_audio);
            }
        }

        if (user.properties.containsKey("candraw")) {
            boolean candraw = Tools.isTure(user.properties.get("candraw"));
            if (candraw) {      //可以画图
                img_candraw.setImageResource(R.drawable.icon_control_tools_02);
                txt_candraw.setText(R.string.no_candraw);
            } else {      //不可以画图
                img_candraw.setImageResource(R.drawable.icon_control_tools_01);
                txt_candraw.setText(R.string.candraw);
            }
        } else {     //没给过画图权限
            img_candraw.setImageResource(R.drawable.icon_control_tools_01);
            txt_candraw.setText(R.string.candraw);
        }
        if (user.publishState > 0) {  //只要视频开启就是上台
            img_up_sd.setImageResource(R.drawable.icon_control_down);
            txt_up_sd.setText(R.string.down_std);
        } else {
            img_up_sd.setImageResource(R.drawable.icon_control_up);
            txt_up_sd.setText(R.string.up_std);
        }

        //弹框高度和长度都设置成固定值（dp）
        if (TKRoomManager.getInstance().getMySelf().role == 2) {
            studentPopupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToManyActivity.this, 150f),
                    KeyBoardUtil.dp2px(OneToManyActivity.this, 70f));
        } else {
            studentPopupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToManyActivity.this, 380f),
                    KeyBoardUtil.dp2px(OneToManyActivity.this, 70f));
        }

        studentPopupWindow.setContentView(contentView);
        lin_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                    img_audio.setImageResource(R.drawable.icon_close_audio);
                    txt_audio.setText(R.string.open_audio);
                    TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 0 || user.publishState == 4 ? 1 : 3);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                } else {
                    img_audio.setImageResource(R.drawable.icon_open_audio);
                    txt_audio.setText(R.string.close_audio);
                    TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 3 ? 2 : 4);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_candraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.properties.containsKey("candraw")) {
                    boolean candraw = Tools.isTure(user.properties.get("candraw"));
                    if (candraw) {    //不可以画图
                        img_candraw.setImageResource(R.drawable.icon_control_tools_01);
                        txt_candraw.setText(R.string.candraw);
                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                    } else {   //可以画图
                        img_candraw.setImageResource(R.drawable.icon_control_tools_02);
                        txt_candraw.setText(R.string.no_candraw);
                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", true);
                    }
                } else {    //可以画图
                    img_candraw.setImageResource(R.drawable.icon_control_tools_02);
                    txt_candraw.setText(R.string.no_candraw);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", true);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, RoomUser> receiverMap = new HashMap<String, RoomUser>();
                receiverMap.put(user.peerId, user);

                if (TKRoomManager.getInstance().getTrophyList().size() > 0) {
                    //自定义奖杯
                    sendGiftPopUtils.showSendGiftPop(rel_wb_container.getWidth() / 10 * 5, rel_wb_container.getHeight() / 10 * 9, rel_wb_container, receiverMap, 0);
                } else {
                    //默认奖杯
                    RoomSession.getInstance().sendGift(receiverMap, null);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_up_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.publishState > 0) {   //只要视频开启就是上台
                    img_up_sd.setImageResource(R.drawable.icon_control_up);
                    txt_up_sd.setText(R.string.up_std);
                    TKRoomManager.getInstance().changeUserPublish(user.peerId, 0);
                    if (user.role != 1) {
                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                    }
                } else {
                    img_up_sd.setImageResource(R.drawable.icon_control_down);
                    txt_up_sd.setText(R.string.down_std);
                    TKRoomManager.getInstance().changeUserPublish(user.peerId, 3);
                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_video_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
                    img_video_control.setImageResource(R.drawable.icon_control_camera_02);
                    txt_video.setText(R.string.video_off);
                    TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 0 || user.publishState == 4 ? 2 : 3);
                } else {
                    img_video_control.setImageResource(R.drawable.icon_control_camera_01);
                    txt_video.setText(R.string.video_on);
                    TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 2 ? 4 : 1);
                }
                studentPopupWindow.dismiss();
            }
        });

        lin_plit_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentPopupWindow.dismiss();
                if (videoItems.get(index).isSplitScreen) {
                    videoItems.get(index).isSplitScreen = false;
                    videoItems.get(index).isMoved = false;
                    if (screenID.contains(videoItems.get(index).peerid)) {
                        screenID.remove(videoItems.get(index).peerid);
                    }
                } else {
                    videoItems.get(index).isSplitScreen = true;
                    if (!screenID.contains(videoItems.get(index).peerid)) {
                        screenID.add(videoItems.get(index).peerid);
                    }
                }
                do1vsnStudentVideoLayout();
                sendSplitScreen();
                sendStudentMove();
            }
        });

        studentPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                is_show_student_window = !Tools.isInView(event, view);
                return false;
            }
        });

        studentPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        studentPopupWindow.setFocusable(false);
        studentPopupWindow.setOutsideTouchable(true);

        if (RoomSession.roomType == 0) {   //一对一界面
            if (user.role == 1) {
                img_up_arr.setVisibility(View.VISIBLE);
                img_right_arr.setVisibility(View.GONE);
                RelativeLayout.LayoutParams arr_param = (RelativeLayout.LayoutParams) img_up_arr.getLayoutParams();
                arr_param.setMargins((videoItems.get(index).parent.getMeasuredWidth() / 2), 0, 0, 0);
                img_up_arr.setLayoutParams(arr_param);
//                studentPopupWindow.showAsDropDown(videoItems.get(index).parent, -videoItems.get(index).parent.getMeasuredWidth() / 2 - 20,
//                        -videoItems.get(index).parent.getHeight() - videoItems.get(index).parent.getHeight() / 2);
                studentPopupWindow.showAsDropDown(videoItems.get(0).parent, 0, 0, Gravity.BOTTOM);
            } else {
                img_up_arr.setVisibility(View.VISIBLE);
                img_right_arr.setVisibility(View.GONE);
                RelativeLayout.LayoutParams arr_param = (RelativeLayout.LayoutParams) img_up_arr.getLayoutParams();
                arr_param.setMargins((videoItems.get(index).parent.getMeasuredWidth() * 3 / 2), 0, 0, 0);

                img_up_arr.setLayoutParams(arr_param);
                studentPopupWindow.showAsDropDown(videoItems.get(index).parent, 0, 0, Gravity.BOTTOM);
            }
        } else {   //一对多界面
            setArrPosition(img_up_arr, img_down_arr, index, studentPopupWindow);
        }
    }

    private void setArrPosition(ImageView img_up_arr, ImageView img_down_arr, int index, PopupWindow popupWindow) {
        //popupwindow设置的距离坐标
        int popup_location_x = 0;
        int popup_location_y = 0;
        //上三角距离左边的距离
        int up_arr_margin_left = 0;
        RelativeLayout.LayoutParams down_arr_param = (RelativeLayout.LayoutParams) img_down_arr.getLayoutParams();
        RelativeLayout.LayoutParams up_arr_param = (RelativeLayout.LayoutParams) img_up_arr.getLayoutParams();
        //获取点击视频框相对于屏幕左边的距离
        int[] video_item = new int[2];
        videoItems.get(index).parent.getLocationInWindow(video_item);

        //获取点击视频框中心相对于左边屏幕的距离
        int video_center_left_lenth = video_item[0] + videoItems.get(index).parent.getWidth() / 2;
        if (video_center_left_lenth < popupWindow.getWidth() / 2) {
            //点击控件的中心坐标距离左边屏幕小于popupwindow的宽度
            popup_location_x = 0;
            up_arr_margin_left = video_center_left_lenth;
        } else if (video_center_left_lenth > (ScreenScale.getScreenWidth() - popupWindow.getWidth())) {
            //点击控件的中心坐标距离右边屏幕小于popupwindow的宽度
            popup_location_x = ScreenScale.getScreenWidth() - popupWindow.getWidth();
            up_arr_margin_left = popupWindow.getWidth() - (ScreenScale.getScreenWidth() - video_center_left_lenth);

        } else {
            popup_location_x = video_center_left_lenth - popupWindow.getWidth() / 2;
            up_arr_margin_left = popupWindow.getWidth() / 2;
        }

        if (videoItems.get(index).isSplitScreen) {
            down_arr_param.setMargins(up_arr_margin_left, 0, 0, 0);
            up_arr_param.setMargins(up_arr_margin_left, 0, 0, 0);
            img_up_arr.setVisibility(View.GONE);
            img_down_arr.setVisibility(View.VISIBLE);
            img_down_arr.setLayoutParams(down_arr_param);
            popup_location_y = video_item[1] - popupWindow.getHeight();
        } else {
            if (index == 0) {
                if (videoItems.get(index).isMoved) {
                    down_arr_param.setMargins(200, 0, 0, 0);
                    up_arr_param.setMargins(200, 0, 0, 0);
                } else {
                    down_arr_param.setMargins((videoItems.get(index).parent.getMeasuredWidth() / 2), 0, 0, 0);
                    up_arr_param.setMargins((videoItems.get(index).parent.getMeasuredWidth() / 2), 0, 0, 0);
                }
            } else {
                //不管是否拖拽，三角图标的margin值都是一样
                down_arr_param.setMargins(up_arr_margin_left, 0, 0, 0);
                up_arr_param.setMargins(up_arr_margin_left, 0, 0, 0);
                int[] stu_popup = new int[2];
                videoItems.get(index).parent.getLocationInWindow(stu_popup);
                if ((ScreenScale.getScreenHeight() - stu_popup[1] - videoItems.get(index).parent.getHeight() - re_menu.getHeight()) > 200) {
                    //下面弹框
                    img_up_arr.setVisibility(View.VISIBLE);
                    img_down_arr.setVisibility(View.GONE);
                    img_up_arr.setLayoutParams(up_arr_param);
                    popup_location_y = video_item[1] + videoItems.get(index).parent.getHeight();
                } else {
                    //上面弹框
                    img_up_arr.setVisibility(View.GONE);
                    img_down_arr.setVisibility(View.VISIBLE);
                    img_down_arr.setLayoutParams(down_arr_param);
                    popup_location_y = video_item[1] - popupWindow.getHeight();
                }
            }
        }
        studentPopupWindow.showAtLocation(videoItems.get(index).parent, Gravity.NO_GRAVITY, popup_location_x, popup_location_y);
    }

    private void sendSplitScreen() {
        try {
            JSONObject data = new JSONObject();
            JSONArray splitData = new JSONArray();
            for (int i = 0; i < screenID.size(); i++) {
                splitData.put(screenID.get(i));
            }
            data.put("userIDArry", splitData);
            if (screenID.size() > 0) {
                TKRoomManager.getInstance().pubMsg("VideoSplitScreen", "VideoSplitScreen", "__allExceptSender", data.toString(), true, "ClassBegin", null);
            } else {
                TKRoomManager.getInstance().delMsg("VideoSplitScreen", "VideoSplitScreen", "__allExceptSender", data.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    boolean is_show_teacher_window = true;

    private void showTeacherControlPop(final RoomUser user) {

        if (!is_show_teacher_window) {
            is_show_teacher_window = true;
            return;
        }

        if (!(TKRoomManager.getInstance().getMySelf().role == 0)) {
            return;
        }

        if (!RoomControler.isReleasedBeforeClass()) {
            if (!RoomSession.isClassBegin) {
                return;
            }
        }

        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_teacher_control, null);
        ImageView img_up_arr = (ImageView) contentView.findViewById(R.id.up_arr);
        ImageView img_down_arr = (ImageView) contentView.findViewById(R.id.down_arr);
        LinearLayout lin_video_control = (LinearLayout) contentView.findViewById(R.id.lin_video_control);
        LinearLayout lin_audio_control = (LinearLayout) contentView.findViewById(R.id.lin_audio_control);

        final ImageView img_video_control = (ImageView) contentView.findViewById(R.id.img_camera);
        final ImageView img_audio_control = (ImageView) contentView.findViewById(R.id.img_audio);
        final TextView txt_video = (TextView) contentView.findViewById(R.id.txt_camera);
        final TextView txt_audio = (TextView) contentView.findViewById(R.id.txt_audio);

        LinearLayout lin_plit_screen = (LinearLayout) contentView.findViewById(R.id.lin_plit_screen);
        TextView txt_plit_screen = (TextView) contentView.findViewById(R.id.txt_plit_screen);
        ImageView img_plit_screen = (ImageView) contentView.findViewById(R.id.img_plit_screen);


        if (TKRoomManager.getInstance().getRoomType() == 0) {
            lin_plit_screen.setVisibility(View.GONE);
        } else {
            if (RoomSession.isClassBegin) {
                lin_plit_screen.setVisibility(View.VISIBLE);
            } else {
                lin_plit_screen.setVisibility(View.GONE);
            }
        }

        RelativeLayout.LayoutParams arr_param_teacher = (RelativeLayout.LayoutParams) img_up_arr.getLayoutParams();
        RelativeLayout.LayoutParams arr_down_teacher = (RelativeLayout.LayoutParams) img_down_arr.getLayoutParams();
        if (videoItems.get(0).isSplitScreen) {
            arr_param_teacher.setMargins(100, 0, 0, 0);
            txt_plit_screen.setText(getResources().getText(R.string.recovery));
            img_plit_screen.setImageResource(R.drawable.icon_reply_normal_only);
        } else {
            txt_plit_screen.setText(getResources().getText(R.string.plitscreen));
            img_plit_screen.setImageResource(R.drawable.icon_screen_normal);
        }

        //弹框宽度和高度都设置成固定值（dp）
        final PopupWindow popupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToManyActivity.this, 253f), KeyBoardUtil.dp2px(OneToManyActivity.this, 70f));

        popupWindow.setContentView(contentView);
        if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
            img_audio_control.setImageResource(R.drawable.icon_open_audio);
            txt_audio.setText(R.string.open_audio);
        } else {
            img_audio_control.setImageResource(R.drawable.icon_close_audio);
            txt_audio.setText(R.string.close_audio);
        }
        if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
            img_video_control.setImageResource(R.drawable.icon_control_camera_01);
            txt_video.setText(R.string.video_on);
        } else {
            img_video_control.setImageResource(R.drawable.icon_control_camera_02);
            txt_video.setText(R.string.video_off);
        }

        lin_video_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
                    img_video_control.setImageResource(R.drawable.icon_control_camera_02);
                    txt_video.setText(R.string.video_off);
                    TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 0 || user.publishState == 4 ? 2 : 3);
                } else {
                    img_video_control.setImageResource(R.drawable.icon_control_camera_01);
                    txt_video.setText(R.string.video_on);
                    TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 2 ? 4 : 1);
                }
                popupWindow.dismiss();
            }
        });

        lin_audio_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                    img_audio_control.setImageResource(R.drawable.icon_close_audio);
                    txt_audio.setText(R.string.close_audio);
                    TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 0 || user.publishState == 4 ? 1 : 3);
                } else {
                    img_audio_control.setImageResource(R.drawable.icon_open_audio);
                    txt_audio.setText(R.string.open_audio);
                    TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 3 ? 2 : 4);
                }
                popupWindow.dismiss();
            }
        });

        lin_plit_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (videoItems.get(0).isSplitScreen) {
                    videoItems.get(0).isSplitScreen = false;
                    videoItems.get(0).isMoved = false;
                    if (screenID.contains(videoItems.get(0).peerid)) {
                        screenID.remove(videoItems.get(0).peerid);
                    }
                } else {
                    videoItems.get(0).isSplitScreen = true;
                    if (!screenID.contains(videoItems.get(0).peerid)) {
                        screenID.add(videoItems.get(0).peerid);
                    }
                }
                do1vsnStudentVideoLayout();
                sendSplitScreen();
                sendStudentMove();
            }
        });


        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                is_show_teacher_window = !Tools.isInView(event, videoItems.get(0).parent);
                return false;
            }
        });

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);

        //获取点击view左上角相对于屏幕原点的x，y值
        int[] video_item = new int[2];
        videoItems.get(0).parent.getLocationInWindow(video_item);
        int view_self_height = videoItems.get(0).parent.getHeight();
        int ab_height = video_item[1];

        if (videoItems.get(0).isSplitScreen) {
            img_down_arr.setVisibility(View.VISIBLE);
            img_up_arr.setVisibility(View.GONE);
            arr_down_teacher.setMargins(popupWindow.getWidth() / 2, 0, 0, 0);
            popupWindow.showAtLocation(videoItems.get(0).parent, Gravity.NO_GRAVITY, video_item[0] + (videoItems.get(0).parent.getWidth() - popupWindow.getWidth()) / 2, video_item[1] - popupWindow.getHeight());

        } else {
            int show_position_x = video_item[0];
            int show_position_y = 0;

            if ((ScreenScale.getScreenHeight() - ab_height - view_self_height - re_menu.getHeight()) > 200) {
                //弹框在下边
                img_up_arr.setVisibility(View.VISIBLE);
                img_down_arr.setVisibility(View.GONE);
                show_position_y = video_item[1] + videoItems.get(0).parent.getHeight();
            } else {
                //弹框在上边
                img_up_arr.setVisibility(View.GONE);
                img_down_arr.setVisibility(View.VISIBLE);
                show_position_y = video_item[1] - popupWindow.getHeight();
            }

            if ((ScreenScale.getScreenWidth() - video_item[0]) < popupWindow.getWidth()) {
                //弹框在最右边，需要调整三角形的margin值
                int margin_left = video_item[0] + videoItems.get(0).parent.getMeasuredWidth() / 2 - (ScreenScale.getScreenWidth() - popupWindow.getWidth());
                arr_param_teacher.setMargins(margin_left, 0, 0, 0);
                arr_down_teacher.setMargins(margin_left, 0, 0, 0);
                popupWindow.showAtLocation(videoItems.get(0).parent, Gravity.NO_GRAVITY, show_position_x, show_position_y);
            } else {
                if ((video_item[0] + videoItems.get(0).parent.getWidth() / 2) < popupWindow.getWidth() / 2) {
                    arr_param_teacher.setMargins((videoItems.get(0).parent.getWidth() / 2 + video_item[0]), 0, 0, 0);
                    arr_down_teacher.setMargins((videoItems.get(0).parent.getWidth() / 2), 0, 0, 0);
                } else {
                    arr_param_teacher.setMargins((popupWindow.getWidth() / 2), 0, 0, 0);
                    arr_down_teacher.setMargins((popupWindow.getWidth() / 2), 0, 0, 0);
                }
                popupWindow.showAtLocation(videoItems.get(0).parent, Gravity.NO_GRAVITY, show_position_x - Math.abs(videoItems.get(0).parent.getWidth() - popupWindow.getWidth()) / 2, show_position_y);
            }
        }
    }

    private void recoveryAllVideoTtems() {
        for (int x = 0; x < videoItems.size(); x++) {
            videoItems.get(x).isSplitScreen = false;
            videoItems.get(x).isMoved = false;
        }
        screenID.clear();
        stuMoveInfoMap.clear();
        do1vsnStudentVideoLayout();
        sendSplitScreen();
        sendStudentMove();
        sendScaleVideoItem(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_back || id == R.id.rl_play_back) {
            showExitDialog();
        } else if (id == R.id.txt_class_begin) {
            if (RoomSession.isClassBegin) {
                try {
                    if (canClassDissMiss || !TKRoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                        showClassDissMissDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (RoomControler.haveTimeQuitClassroomAfterClass()) {
                    RoomSession.getInstance().getSystemTime();
                } else {
                    RoomSession.getInstance().startClass();
                }
            }
        } else if (id == R.id.ll_large_white_board) {  //放大白板按钮
            wbFragment.interactiveJS("'whiteboardSDK_enlargeWhiteboard'", null);
        } else if (id == R.id.ll_small_white_board) {   //缩小白板按钮
            wbFragment.interactiveJS("'whiteboardSDK_narrowWhiteboard'", null);
        } else if (id == R.id.ll_to_left) {   //左翻页
            ShareDoc shareDoc = WhiteBoradManager.getInstance().getCurrentFileDoc();
            wbFragment.interactiveJSPaging(shareDoc, false, false);
        } else if (id == R.id.ll_to_right) {   //右翻页
            boolean nextOrAdd = total_page_num > currentNumber ? true : false;
            ShareDoc shareDoc = WhiteBoradManager.getInstance().getCurrentFileDoc();
            wbFragment.interactiveJSPaging(shareDoc, true, nextOrAdd);
        } else if (id == R.id.ll_choice_number) {  //显示页数
            if (TKRoomManager.getInstance().getMySelf().canDraw) {
                pageNumberPopupWindowUtils.showPageNumberPopupWindow(ll_choice_number, 100, iv_page_choose_icon, total_page_num);
            } else {
                if (TKRoomManager.getInstance().getMySelf().role == 2 && RoomControler.isStudentCanTurnPage()) {
                    pageNumberPopupWindowUtils.showPageNumberPopupWindow(ll_choice_number, 100, iv_page_choose_icon, total_page_num);
                }
            }
        } else if (id == R.id.tv_play_speed) {  //回放倍速
            playSpeedPopupWindowUtils.showPlaySpeedPopupWindow(tv_play_speed, 100, 280);
        }
    }

    private void showPhotoControlPop() {
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_photo_control, null);
        LinearLayout ll_popu_camera = (LinearLayout) contentView.findViewById(R.id.ll_popu_camera);
        LinearLayout ll_popu_selectphoto = (LinearLayout) contentView.findViewById(R.id.ll_popu_selectphoto);
        popupWindowPhoto = new PopupWindow(90, ScreenScale.getScreenHeight() / 6);
        popupWindowPhoto.setContentView(contentView);
        ll_popu_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPauseLocalVideo) {
                    TKRoomManager.getInstance().pauseLocalCamera();
                    isPauseLocalVideo = !isPauseLocalVideo;
                }
                isOpenCamera = true;
                isBackApp = true;
                PhotoUtils.openCamera(OneToManyActivity.this);
                popupWindowPhoto.dismiss();
            }
        });
        ll_popu_selectphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBackApp = true;
                PhotoUtils.openAlbum(OneToManyActivity.this);
                popupWindowPhoto.dismiss();
            }
        });
        popupWindowPhoto.setBackgroundDrawable(new BitmapDrawable());
        popupWindowPhoto.setFocusable(true);
        popupWindowPhoto.setOutsideTouchable(true);

        int[] width_and_height = new int[2];
        cb_choose_photo.getLocationInWindow(width_and_height);

        popupWindowPhoto.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (cb_choose_photo != null) {
                    cb_choose_photo.setChecked(false);
                }
            }
        });

        popupWindowPhoto.showAtLocation(cb_choose_photo, Gravity.NO_GRAVITY, width_and_height[0] - (Math.abs((cb_choose_photo.getWidth() - popupWindowPhoto.getWidth()) / 2)), width_and_height[1] - popupWindowPhoto.getHeight());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri;
        switch (requestCode) {
            case PhotoUtils.PHOTO_REQUEST_CAREMA:
                isBackApp = false;
                if (isPauseLocalVideo) {
                    TKRoomManager.getInstance().resumeLocalCamera();
                    isPauseLocalVideo = !isPauseLocalVideo;
                }
                isOpenCamera = false;
                if (resultCode == RESULT_CANCELED) {
                    LogUtils.isLogE("mxl", "取消了");
                    return;
                }
                if (resultCode == RESULT_OK) {
                    LogUtils.isLogE("mxl", "拍照OK");
                    if (data != null) {
                        uri = data.getData();
                    } else {
                        uri = PhotoUtils.imageUri;
                    }
                    if (!TextUtils.isEmpty(uri.toString())) {
                        try {
                            String path = PersonInfo_ImageUtils.scaleAndSaveImage(PersonInfo_ImageUtils.getRealFilePath(this,
                                    PersonInfo_ImageUtils.getFileUri(uri, this)), 800, 800, this);
                            WhiteBoradManager.getInstance().uploadRoomFile(
                                    TKRoomManager.getInstance().getRoomProperties().getString("serial"), path, RoomSession.isClassBegin);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case PhotoUtils.ALBUM_IMAGE:
                isBackApp = false;
                if (resultCode == RESULT_OK) {
                    String imagePath = null;
                    try {
                        if (Build.VERSION.SDK_INT >= 19) {
                            imagePath = PersonInfo_ImageUtils.getImageAfterKitKat(data, this);
                        } else {
                            imagePath = PersonInfo_ImageUtils.getImageBeforeKitKat(data, this);
                        }
                        if (!TextUtils.isEmpty(imagePath)) {
                            String path = PersonInfo_ImageUtils.scaleAndSaveImage(imagePath, 800, 800, this);
                            WhiteBoradManager.getInstance().uploadRoomFile(
                                    TKRoomManager.getInstance().getRoomProperties().getString("serial"), path, RoomSession.isClassBegin);
                        } else {
                            Toast.makeText(this, "图片选择失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void playSelfBeforeClassBegin() {
        RoomUser me = TKRoomManager.getInstance().getMySelf();
        if (me.role == 4) {
            return;
        }
        if (RoomControler.isReleasedBeforeClass() && me.publishState == 0) {
            TKRoomManager.getInstance().changeUserPublish(me.peerId, 3);
        } else {
            do1vsnClassBeginPlayVideo(me, me.peerId != null);
        }
    }

    private void do1vsnClassBeginPlayVideo(final RoomUser user, boolean force) {
        boolean hasSit = false;
        sitpos = -1;
        for (int i = 0; i < videoItems.size(); i++) {
            if (videoItems.get(i).peerid.equals(user.peerId)) {
                hasSit = true;
                sitpos = i;
            }
        }
        if (!hasSit) {
            final VideoItem stu = new VideoItem();
            final LinearLayout vdi_stu = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.video_item, null);
            stu.parent = vdi_stu;
            stu.sf_video = (SurfaceViewRenderer) vdi_stu.findViewById(R.id.sf_video);
            stu.sf_video.init(EglBase.create().getEglBaseContext(), null);
            stu.sf_video.setZOrderMediaOverlay(true);
            stu.img_mic = (ImageView) vdi_stu.findViewById(R.id.img_mic);
            stu.img_pen = (ImageView) vdi_stu.findViewById(R.id.img_pen);
            stu.img_hand = (ImageView) vdi_stu.findViewById(R.id.img_hand_up);
            stu.txt_name = (AutoFitTextView) vdi_stu.findViewById(R.id.txt_name);
            stu.txt_gift_num = (AutoFitTextView) vdi_stu.findViewById(R.id.txt_gift_num);
            stu.rel_group = (RelativeLayout) vdi_stu.findViewById(R.id.rel_group);
            stu.img_video_back = (ImageView) vdi_stu.findViewById(R.id.img_video_back);
            stu.lin_gift = (LinearLayout) vdi_stu.findViewById(R.id.lin_gift);
            stu.lin_name_label = (LinearLayout) vdi_stu.findViewById(R.id.lin_name_label);
            stu.rel_video_label = (RelativeLayout) vdi_stu.findViewById(R.id.rel_video_label);
            stu.re_bg = (RelativeLayout) vdi_stu.findViewById(R.id.re_bg);
            stu.bg_video_back = (ImageView) vdi_stu.findViewById(R.id.bg_video_back);

            if (user.role == 0) {
                stu.lin_name_label.setBackgroundResource(R.drawable.rounded_video_person_name_teacher);
                stu.re_bg.setBackgroundResource(R.drawable.video_frame_teacher);
            } else {
                stu.lin_name_label.setBackgroundResource(R.drawable.rounded_video_person_name);
                stu.re_bg.setBackgroundResource(R.drawable.video_frame_student);
            }

            stu.re_background = (RelativeLayout) vdi_stu.findViewById(R.id.re_background);
            stu.tv_home = (TextView) vdi_stu.findViewById(R.id.tv_home);

            stu.peerid = user.peerId;
            stu.role = user.role;
            stu.txt_name.setText(user.nickName);

            if (!RoomSession.isClassBegin) {
                stu.img_pen.setVisibility(View.INVISIBLE);
                stu.img_hand.setVisibility(View.INVISIBLE);
                stu.img_mic.setVisibility(View.INVISIBLE);
            }

            if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                if (RoomSession.isClassBegin) {
                    stu.img_hand.setVisibility(View.VISIBLE);
                }
            } else {
                stu.img_hand.setVisibility(View.GONE);
            }

            if (user.disableaudio) {
                stu.img_mic.setImageResource(R.drawable.icon_audio_disable);
                stu.img_mic.setVisibility(View.VISIBLE);
            } else {
                if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                    stu.img_mic.setImageResource(R.drawable.icon_audio_disable);
                    stu.img_mic.setVisibility(View.VISIBLE);
                } else {
                    stu.img_mic.setVisibility(View.GONE);
                }
            }
            if (user.role == 2) {
                stu.lin_gift.setVisibility(View.VISIBLE);
            } else {
                stu.lin_gift.setVisibility(View.INVISIBLE);
            }
            videoItems.add(stu);
            rel_students.addView(vdi_stu);
            do1vsnStudentVideoLayout();

            if (RoomControler.isReleasedBeforeClass()) {
                if (user.publishState > 1 && user.publishState < 4) {
                    stu.sf_video.setVisibility(View.VISIBLE);
                    stu.bg_video_back.setVisibility(View.GONE);
                } else {
                    stu.sf_video.setVisibility(View.INVISIBLE);
                    stu.bg_video_back.setVisibility(View.VISIBLE);
                }
            }
            TKRoomManager.getInstance().playVideo(user.peerId, stu.sf_video, RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
        } else if (force) {
            if (sitpos != -1) {
                if (user.disableaudio) {
                    videoItems.get(sitpos).img_mic.setVisibility(View.VISIBLE);
                    videoItems.get(sitpos).img_mic.setImageResource(R.drawable.icon_audio_disable);
                } else {
                    videoItems.get(sitpos).img_mic.setImageResource(R.drawable.icon_video_voice);
                    if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                        videoItems.get(sitpos).img_mic.setVisibility(View.VISIBLE);
                        videoItems.get(sitpos).img_mic.setImageResource(R.drawable.icon_audio_disable);
                    } else {
                        videoItems.get(sitpos).img_mic.setVisibility(View.GONE);
                    }
                }
                if (RoomControler.isReleasedBeforeClass()) {
                    if (user.publishState > 1 && user.publishState < 4) {
                        videoItems.get(sitpos).sf_video.setVisibility(View.VISIBLE);
                        videoItems.get(sitpos).bg_video_back.setVisibility(View.GONE);
                    } else {
                        videoItems.get(sitpos).sf_video.setVisibility(View.INVISIBLE);
                        videoItems.get(sitpos).bg_video_back.setVisibility(View.VISIBLE);
                    }
                }
                TKRoomManager.getInstance().playVideo(user.peerId, videoItems.get(sitpos).sf_video, RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
            }
        }
    }

    private void unPlaySelfAfterClassBegin() {
        RoomUser me = TKRoomManager.getInstance().getMySelf();
        if (me.publishState != 3) {
            if (RoomControler.isReleasedBeforeClass()) {
                TKRoomManager.getInstance().changeUserPublish(me.peerId, 0);
            } else {
                do1vsnStudentUnPlayVideo(me.peerId);
            }
        }
    }

    private void setBackgroundOrReception(boolean b, RoomUser RoomUser) {
        for (int x = 0; x < videoItems.size(); x++) {
            if (videoItems.get(x).peerid.equals(RoomUser.peerId)) {
                if (b) {
                    videoItems.get(x).re_background.setVisibility(View.VISIBLE);
                } else {
                    videoItems.get(x).re_background.setVisibility(View.GONE);
                }
                if (videoItems.get(x).tv_home != null) {
                    if (RoomUser != null && RoomUser.role == 0) {
                        videoItems.get(x).tv_home.setText(R.string.tea_background);
                    } else {
                        videoItems.get(x).tv_home.setText(R.string.background);
                    }
                }
            }
        }
    }

    private void studentMoveOrScreen() {
        boolean isScreen = false;
        for (int x = 0; x < videoItems.size(); x++) {
            if (videoItems.get(x).isSplitScreen) {
                isScreen = true;
                break;
            }
        }
        if (isScreen) {
            sendSplitScreen();
        } else {
            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                sendStudentMove();
            }
        }
    }

    private void romoveMovieFragment() {
        RoomSession.isPublish = false;
        movieFragment = MovieFragment.getInstance();
        mediafragmentManager = getSupportFragmentManager();
        ft = mediafragmentManager.beginTransaction();
        if (movieFragment.isAdded()) {
            ft.remove(movieFragment);
        }
        if (!isZoom) {
            for (int i = 0; i < videoItems.size(); i++) {
                videoItems.get(i).sf_video.setZOrderMediaOverlay(true);
                videoItems.get(i).sf_video.setVisibility(View.VISIBLE);
                videoItems.get(i).bg_video_back.setVisibility(View.GONE);
            }
        }
    }

    private void romoveScreenFragment() {
        RoomSession.isPublish = false;
        screenFragment = ScreenFragment.getInstance();
        mediafragmentManager = getSupportFragmentManager();
        ft = mediafragmentManager.beginTransaction();
        if (screenFragment.isAdded()) {
            ft.remove(screenFragment);
            ft.commitAllowingStateLoss();
        }
        if (!isZoom) {
            for (int i = 0; i < videoItems.size(); i++) {
                videoItems.get(i).sf_video.setZOrderMediaOverlay(true);
                videoItems.get(i).sf_video.setVisibility(View.VISIBLE);
                videoItems.get(i).bg_video_back.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPageFinished() {

    }

    @Override
    public void onRoomDocChange(boolean isdel, boolean ismedia) {
        if (RoomControler.isDocumentClassification()) {
            WhiteBoradManager.getInstance().getClassDocList();
            WhiteBoradManager.getInstance().getAdminDocList();
            WhiteBoradManager.getInstance().getClassMediaList();
            WhiteBoradManager.getInstance().getAdminmMediaList();
        } else {
            WhiteBoradManager.getInstance().getDocList();
            WhiteBoradManager.getInstance().getMediaList();
            fileListAdapter.notifyDataSetChanged();
            mediaListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onWhiteBoradZoom(final boolean isZoom) {
        this.isZoom = isZoom;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isZoom) {
                    for (int i = 0; i < videoItems.size(); i++) {
                        videoItems.get(i).sf_video.setZOrderMediaOverlay(false);
                        videoItems.get(i).sf_video.setVisibility(View.INVISIBLE);
                        videoItems.get(i).bg_video_back.setVisibility(View.VISIBLE);
                    }
                    rel_students.setVisibility(View.GONE);
                    lin_audio_control.setVisibility(View.GONE);
                    rel_tool_bar.setVisibility(View.GONE);

                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);

                    RelativeLayout.LayoutParams rel_wb_param = (RelativeLayout.LayoutParams) rel_wb_container.getLayoutParams();
                    rel_wb_param.width = dm.widthPixels;
                    rel_wb_param.height = dm.heightPixels - rel_tool_bar.getHeight();
                    rel_wb_container.setLayoutParams(rel_wb_param);

                } else {
                    for (int i = 0; i < videoItems.size(); i++) {
                        videoItems.get(i).sf_video.setZOrderMediaOverlay(true);
                        videoItems.get(i).sf_video.setVisibility(View.VISIBLE);
                        videoItems.get(i).bg_video_back.setVisibility(View.GONE);
                    }
                    rel_students.setVisibility(View.VISIBLE);
                    rel_tool_bar.setVisibility(View.VISIBLE);

                    /*if (RoomManager.getInstance().getMySelf().role == 0 && RoomSession.isPublish && !com.classroomsdk.Tools.isMp4(WhiteBoradManager.getInstance().getCurrentMediaDoc().getFilename())) {
                        lin_audio_control.setVisibility(View.VISIBLE);
                    } else {
                        lin_audio_control.setVisibility(View.INVISIBLE);
                    }*/

                    if (TKRoomManager.getInstance().getMySelf() != null && !TextUtils.isEmpty(TKRoomManager.getInstance().getMySelf().peerId)) {
                        doPlayVideo(TKRoomManager.getInstance().getMySelf().peerId);
                    }
                    if (!RoomSession.isClassBegin) {
                        playSelfBeforeClassBegin();
                    }
                    doLayout();
                }
            }
        });
    }

    private String url;
    private boolean isvideo;
    private long fileid;
    private boolean isWBMediaPlay = false;

    @Override
    public void onWhiteBoradMediaPublish(String url, boolean isvideo, long fileid) {
        this.url = url;
        this.isvideo = isvideo;
        this.fileid = fileid;
        isWBMediaPlay = true;
    }

    /**
     * 默认总页数为0，通过这个值设置页数选择弹框中listView的数据
     */
    int total_page_num = 1;
    int currentNumber = 1;

    /***
     *  JS调用我们的方法（改变按钮的状态）
     */
    @Override
    public void onWhiteBoradReceiveActionCommand(final String stateJson) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(stateJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Boolean remarkText = jsonObject.optBoolean("remarkText");
                Boolean toolbox = jsonObject.optBoolean("toolbox");  //工具箱状态
                Boolean custom_trophy = jsonObject.optBoolean("custom_trophy");  //奖杯

                if (jsonObject.has("chooseType")) {
                    String s = jsonObject.optString("chooseType");
                    if (s.equals("tool_pencil")) {
                        //画笔
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen_tool_pencil);
                    } else if (s.equals("tool_mouse")) {
                        //鼠标
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen);
                    } else if (s.equals("tool_text")) {
                        //文字
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen_tool_text);
                    } else if (s.equals("tool_rectangle_empty")) {
                        //空心正方形
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen_tool_rectangle_empty);
                    } else if (s.equals("tool_rectangle")) {
                        //实心正方形
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen_tool_rectangle);
                    } else if (s.equals("tool_ellipse_empty")) {
                        //实心圆
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen_tool_ellipse_empty);
                    } else if (s.equals("tool_ellipse")) {
                        //空心圆
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen_tool_ellipse);
                    } else if (s.equals("tool_line")) {
                        //线
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen_tool_line);
                    } else if (s.equals("tool_highlighter")) {
                        //荧光笔
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen_tool_highlighter);
                    } else if (s.equals("tool_arrow")) {
                        //箭头
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen_tool_arrow);
                    } else if (s.equals("tool_eraser")) {
                        //橡皮
                        cb_choose_mouse.setBackgroundResource(R.drawable.selector_bottom_left_pen_tool_eraser);
                    }
                }


                if (remarkText) {
                    boolean remark = jsonObject.optBoolean("remark");  //备注按钮状态
                    //设置备注按钮
                    if (remark) {
                        cb_remark.setChecked(true);
                    } else {
                        cb_remark.setChecked(false);
                    }
                    cb_remark.setVisibility(View.VISIBLE);
                } else {
                    cb_remark.setVisibility(View.INVISIBLE);
                }

                //设置工具箱
                if (toolbox) {
                    cb_tool_case.setChecked(true);
                } else {
                    cb_tool_case.setChecked(false);
                }

                Boolean chooseShow = jsonObject.optBoolean("chooseShow");  //授权笔状态
                //设置授权画笔
                if (chooseShow) {
                    cb_choose_mouse.setChecked(true);
                } else {
                    cb_choose_mouse.setChecked(false);
                }

                JSONObject pageJson = jsonObject.optJSONObject("page");

                if (pageJson != null && !TextUtils.isEmpty(pageJson.toString())) {

                    boolean next = pageJson.optBoolean("nextPage");  //下一页显示状态
                    boolean prev = pageJson.optBoolean("prevPage");  //上一页显示状态

                    boolean skip = pageJson.optBoolean("skipPage");  //是否跳转
                    boolean add = pageJson.optBoolean("addPage");    //白板是否加页
                    boolean nextStep = pageJson.optBoolean("nextStep");  //下一步
                    boolean prevStep = pageJson.optBoolean("prevStep");   //上一步

                    int current = pageJson.optInt("currentPage");   //当前页数
                    int totalPage = pageJson.optInt("totalPage");  //总页数

                    if (TKRoomManager.getInstance().getMySelf().role == 0 && current != 0) {
                        WhiteBoradManager.getInstance().getCurrentFileDoc().setCurrentPage(current);
                    }

                    //上一页
                    if (prev) {
                        ll_to_left.setEnabled(true);
                        iv_to_left.setImageResource(R.drawable.common_icon_left);
                    } else {
                        ll_to_left.setEnabled(false);
                        iv_to_left.setImageResource(R.drawable.common_icon_left_unclickable);
                    }

                    //下一页
                    if (next) {
                        ll_to_right.setEnabled(true);
                        iv_to_right.setImageResource(R.drawable.common_icon_right);
                    } else {
                        if (add) {
                            ll_to_right.setEnabled(true);
                            iv_to_right.setImageResource(R.drawable.common_icon_right);
                        } else {
                            ll_to_right.setEnabled(false);
                            iv_to_right.setImageResource(R.drawable.common_icon_right_unclickable);
                        }
                    }

                    ShareDoc shareDoc = WhiteBoradManager.getInstance().getCurrentFileDoc();
                    if (shareDoc != null) {
                        if (RoomControler.isDocumentClassification()) {
                            ArrayList<ShareDoc> classList = WhiteBoradManager.getInstance().getClassDocList();
                            ArrayList<ShareDoc> adminList = WhiteBoradManager.getInstance().getAdminDocList();
                            if (classList.contains(shareDoc)) {
                                for (int x = 0; x < classList.size(); x++) {
                                    if (classList.get(x) != null) {
                                        if (classList.get(x).getFileid() == shareDoc.getFileid()) {
                                            classList.get(x).setPagenum(totalPage);
                                            classList.get(x).setCurrentPage(current);
                                            classList.get(x).setPptslide(current);
                                        }
                                    }
                                }
                            } else {
                                for (int x = 0; x < adminList.size(); x++) {
                                    if (adminList.get(x) != null) {
                                        if (adminList.get(x).getFileid() == shareDoc.getFileid()) {
                                            adminList.get(x).setPagenum(totalPage);
                                            adminList.get(x).setCurrentPage(current);
                                            adminList.get(x).setPptslide(current);
                                        }
                                    }
                                }
                            }
                        } else {
                            ArrayList<ShareDoc> arrayList = WhiteBoradManager.getInstance().getDocList();
                            for (int x = 0; x < arrayList.size(); x++) {
                                if (arrayList.get(x) != null) {
                                    if (arrayList.get(x).getFileid() == shareDoc.getFileid()) {
                                        arrayList.get(x).setPagenum(totalPage);
                                        arrayList.get(x).setCurrentPage(current);
                                        arrayList.get(x).setPptslide(current);
                                    }
                                }
                            }
                        }
                        total_page_num = totalPage;
                        currentNumber = current;
                        //设置页数
                        tv_choose_number.setText(current + "/" + totalPage);
                    }
                }

                JSONObject zoomJson = jsonObject.optJSONObject("zoom");

                if (zoomJson != null && !TextUtils.isEmpty(zoomJson.toString())) {
                    boolean zoom_big = zoomJson.optBoolean("zoom_big");  //下一步
                    boolean zoom_small = zoomJson.optBoolean("zoom_small");   //上一步

                    if (zoom_big) {
                        ll_large_white_board.setClickable(true);
                        iv_large_white_board.setImageResource(R.drawable.common_icon_large);

                    } else {
                        ll_large_white_board.setClickable(false);
                        iv_large_white_board.setImageResource(R.drawable.common_icon_large_unclickable);
                    }

                    if (zoom_small) {
                        ll_small_white_board.setClickable(true);
                        iv_small_white_board.setImageResource(R.drawable.common_icon_small);
                    } else {
                        ll_small_white_board.setClickable(false);
                        iv_small_white_board.setImageResource(R.drawable.common_icon_small_unclickable);
                    }
                }
            }
        });
    }

    /***
     *
     * @param jsonProperty   JS改变用户属性
     */
    @Override
    public void onWhiteBoradSetProperty(String jsonProperty) {
        try {
            JSONObject json = new JSONObject(jsonProperty);
            String peerId = json.optString("id");
            String toID = json.optString("toID");
            JSONObject properties = json.optJSONObject("properties");

            HashMap<String, Object> map = (HashMap<String, Object>) Tools.toMap(properties);
            TKRoomManager.getInstance().changeUserProperty(peerId, toID, map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        RoomSession.isClassBegin = false;
        RoomClient.getInstance().setExit(false);
        RoomSession.playingMap.clear();
        RoomSession.playingList.clear();
        RoomSession.pandingSet.clear();
        RoomSession.chatList.clear();
        TKRoomManager.getInstance().registerRoomObserver(null);
        TKRoomManager.getInstance().setWhiteBoard(null);
        for (int i = 0; i < videoItems.size(); i++) {
            videoItems.get(i).sf_video.release();
        }
        RoomClient.getInstance().setExit(false);
        WhiteBoradManager.getInstance().clear();
        RoomSession.getInstance().closeSpeaker();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    public void showExitDialog() {
        Tools.showDialog(OneToManyActivity.this, R.string.remind, R.string.logouts, new Tools.OnDialogClick() {

            @Override
            public void dialog_ok(Dialog dialog) {
                sendGiftPopUtils.deleteImage();
                RoomClient.getInstance().setExit(true);
                TKRoomManager.getInstance().leaveRoom();
                dialog.dismiss();
            }
        }, 0);
    }

    public void showClassDissMissDialog() {
        Tools.showDialog(OneToManyActivity.this, R.string.remind, R.string.make_sure_class_dissmiss, new Tools.OnDialogClick() {
            @Override
            public void dialog_ok(Dialog dialog) {
                TKRoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new HashMap<String, Object>());
                txt_class_begin.setVisibility(View.GONE);
                RoomSession.getInstance().sendClassDissToPhp();
                dialog.dismiss();
            }
        }, 0);
    }

    public void readyForPlayVideo(String shareMediaPeerId, Map<String, Object> shareMediaAttrs) {
        for (int i = 0; i < videoItems.size(); i++) {
            TKRoomManager.getInstance().unPlayVideo(videoItems.get(i).peerid);
            videoItems.get(i).sf_video.setZOrderMediaOverlay(false);
            videoItems.get(i).sf_video.setVisibility(View.INVISIBLE);
            videoItems.get(i).bg_video_back.setVisibility(View.VISIBLE);
        }

        videofragment = VideoFragment.getInstance();
        videofragment.setStream(shareMediaPeerId, shareMediaAttrs);
        mediafragmentManager = getSupportFragmentManager();
        ft = mediafragmentManager.beginTransaction();
        if (!videofragment.isAdded()) {
            ft.replace(R.id.video_container, videofragment);
            ft.commitAllowingStateLoss();
        }
    }

    public void removeVideoFragment() {
        videofragment = VideoFragment.getInstance();
        mediafragmentManager = getSupportFragmentManager();
        ft = mediafragmentManager.beginTransaction();
        if (videofragment.isAdded()) {
            mediaListAdapter.setLocalfileid(-1);
            ft.remove(videofragment);

            ft.commitAllowingStateLoss();
            videofragment = null;
        }

        mediafragmentManager.popBackStack(null, 1);

       /* Timer mediaTimer = new Timer();
        mediaTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (videofragment.isAdded()) {
                    mediaListAdapter.setLocalfileid(-1);
                    ft.remove(videofragment);
                    ft.commitAllowingStateLoss();
                }
            }
        }, 500);*/

        if (!isZoom) {
            for (int i = 0; i < videoItems.size(); i++) {
                TKRoomManager.getInstance().playVideo(videoItems.get(i).peerid, videoItems.get(i).sf_video,
                        RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                videoItems.get(i).sf_video.setZOrderMediaOverlay(true);
                videoItems.get(i).sf_video.setVisibility(View.VISIBLE);
                videoItems.get(i).bg_video_back.setVisibility(View.GONE);
            }
        }
    }

    private void changeUserState(final RoomUser user) {
        for (int i = 0; i < videoItems.size(); i++) {
            if (user.peerId.equals(videoItems.get(i).peerid)) {
                videoItems.get(i).img_mic.setVisibility(View.VISIBLE);
                if (user.disableaudio) {
                    videoItems.get(i).img_mic.setImageResource(R.drawable.icon_audio_disable);
                    videoItems.get(i).img_mic.setVisibility(View.VISIBLE);
                } else {
                    if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                        videoItems.get(i).img_mic.setVisibility(View.VISIBLE);
                        videoItems.get(i).img_mic.setImageResource(R.drawable.icon_audio_disable);
                    } else {
                        /*videoItems.get(i).img_mic.setImageResource(R.drawable.icon_video_voice);*/
                        videoItems.get(i).img_mic.setVisibility(View.GONE);
                    }
                }

                if (user.disablevideo) {
                    videoItems.get(i).img_video_back.setImageResource(R.drawable.icon_no_camera);
                    videoItems.get(i).bg_video_back.setVisibility(View.VISIBLE);
                }

                if (user.properties.containsKey("candraw")) {
                    boolean candraw = Tools.isTure(user.properties.get("candraw"));
                    if (candraw /*&& user.role != 0*/) {
                        videoItems.get(i).img_pen.setVisibility(View.VISIBLE);//可以画图
                    } else {
                        videoItems.get(i).img_pen.setVisibility(View.INVISIBLE);//不可以画图
                    }
                } else {
                    videoItems.get(i).img_pen.setVisibility(View.INVISIBLE);//没给过画图权限
                }

                if (user.properties.containsKey("primaryColor")) {
                    String primaryColor = (String) user.properties.get("primaryColor");
                    if (!TextUtils.isEmpty(primaryColor)) {
                        int res_id = R.drawable.icon_white_pen;
                        if (primaryColor.equals("#000000")) {
                            res_id = R.drawable.icon_black_pen;
                        } else if (primaryColor.equals("#ED3E3A")) {
                            res_id = R.drawable.icon_red_pen;
                        } else if (primaryColor.equals("#09C62B")) {
                            res_id = R.drawable.icon_green_pen;
                        } else if (primaryColor.equals("#FFCC00")) {
                            res_id = R.drawable.icon_yellow_pen;
                        } else if (primaryColor.equals("#5AC9FA")) {
                            res_id = R.drawable.icon_blue_pen;
                        } else if (primaryColor.equals("#007BFF")) {
                            res_id = R.drawable.icon_blue_deep_pen;
                        } else if (primaryColor.equals("#4740D2")) {
                            res_id = R.drawable.old_icon_blue_purple_pen;
                        }
                        videoItems.get(i).img_pen.setImageResource(res_id);
                    }
                } else {
                    videoItems.get(i).img_pen.setImageResource(R.drawable.icon_white_pen);
                }

                if (user.properties.containsKey("raisehand")) {
                    boolean israisehand = Tools.isTure(user.properties.get("raisehand"));
                    if (israisehand) {
                        if (TKRoomManager.getInstance().getMySelf().role == 0) {
                            if (user.role == 2) {
                                videoItems.get(i).img_hand.setVisibility(View.VISIBLE);//正在举手
                            }
                        }
                        if (TKRoomManager.getInstance().getMySelf().role == 2) {
                            videoItems.get(i).img_hand.setVisibility(View.VISIBLE);//正在举手
                        }
                    } else {
                        videoItems.get(i).img_hand.setVisibility(View.INVISIBLE);//同意了，或者拒绝了
                    }
                } else {
                    videoItems.get(i).img_hand.setVisibility(View.INVISIBLE);//还没举手
                }

                if (user.properties.containsKey("giftnumber")) {
                    long giftnumber = user.properties.get("giftnumber") instanceof Integer ? (int) user.properties.get("giftnumber") : (long) user.properties.get("giftnumber");
                    videoItems.get(i).txt_gift_num.setText("X" + giftnumber);
                }
            }
        }
        for (int i = 0; i < videoItems.size(); i++) {
            final int finalI = i;
            final View view = videoItems.get(i).parent;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoItems.size() > finalI) {
                        RoomUser user = TKRoomManager.getInstance().getUser(videoItems.get(finalI).peerid);
                        showStudentControlPop(view, user, finalI);
                    }
                }
            });
        }
        if (videoItems.size() > 0 && TKRoomManager.getInstance().getUser(videoItems.get(0).peerid) != null &&
                TKRoomManager.getInstance().getUser(videoItems.get(0).peerid).role == 0) {
            videoItems.get(0).parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTeacherControlPop(TKRoomManager.getInstance().getUser(videoItems.get(0).peerid));
                }
            });
        }
    }


    boolean is_gif = false;
    com.bumptech.glide.load.resource.gif.GifDrawable gifDrawable_play_gift;

    private void showGiftAim(final VideoItem item, Map<String, Object> map) {

        final ImageView img_gift = new ImageView(this);
        final GifImageView iv_gif = new GifImageView(this);

        if (map.containsKey("giftinfo")) {
            //自定义奖杯
            try {
                final Map<String, Object> gift_data = (HashMap<String, Object>) map.get("giftinfo");
                String url = gift_data.get("trophyimg").toString();
                is_gif = url.endsWith(".gif");

                SoundPlayUtils.play(gift_data.get("trophyvoice").toString());
                String img_url = "http://" + RoomSession.host + ":" + RoomSession.port + url;

                if (is_gif) {
                    Glide.with(this).load(img_url).asGif().into(new SimpleTarget<com.bumptech.glide.load.resource.gif.GifDrawable>() {
                        @Override
                        public void onResourceReady(com.bumptech.glide.load.resource.gif.GifDrawable resource, GlideAnimation<? super com.bumptech.glide.load.resource.gif.GifDrawable> glideAnimation) {
                            gifDrawable_play_gift = resource;
                            iv_gif.setImageDrawable(gifDrawable_play_gift);
                            setAnimal(is_gif, img_gift, iv_gif, item.lin_gift);
                        }
                    });
                } else {
                    Glide.with(this).load(img_url).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            img_gift.setImageBitmap(resource);
                            setAnimal(is_gif, img_gift, iv_gif, item.lin_gift);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //默认动画
            SoundPlayUtils.play("");
            img_gift.setImageResource(R.drawable.ico_gift);
            setAnimal(false, img_gift, iv_gif, item.lin_gift);
        }
    }

    private void setAnimal(final boolean is_gif, final ImageView img_gift, final GifImageView iv_gif, View lin_gift) {

        RelativeLayout.LayoutParams relparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relparam.addRule(RelativeLayout.CENTER_IN_PARENT);

        if (is_gif) {
            iv_gif.setLayoutParams(relparam);
            rel_parent.addView(iv_gif);
        } else {
            img_gift.setLayoutParams(relparam);
            rel_parent.addView(img_gift);
        }

        int[] loca = new int[2];
        lin_gift.getLocationInWindow(loca);
        float x = loca[0];
        float y = loca[1];
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int wid = dm.widthPixels;
        int hid = dm.heightPixels;

        float gx = 0, gy = 0;
        if (is_gif) {
            gx = wid / 2 - iv_gif.getWidth();
            gy = hid / 2 - iv_gif.getHeight() + KeyBoardUtil.dp2px(this, 5);
        } else {
            gx = wid / 2 - img_gift.getWidth();
            gy = hid / 2 - img_gift.getHeight() + KeyBoardUtil.dp2px(this, 5);
        }

        float dx = x - gx;
        float dy = y - gy;

        ObjectAnimator smlTobigXScale = null;
        ObjectAnimator smlTobigYScale = null;
        ObjectAnimator bigToSmlXScale = null;
        ObjectAnimator bigToSmlYScale = null;
        ObjectAnimator translateX = null;
        ObjectAnimator translateY = null;

        if (is_gif) {
            smlTobigXScale = ObjectAnimator.ofFloat(iv_gif, "scaleX", 1.0f, 3.0f);
            smlTobigYScale = ObjectAnimator.ofFloat(iv_gif, "scaleY", 1.0f, 3.0f);
            bigToSmlXScale = ObjectAnimator.ofFloat(iv_gif, "scaleX", 3.0f, 0.0f);
            bigToSmlYScale = ObjectAnimator.ofFloat(iv_gif, "scaleY", 3.0f, 0.0f);
            translateX = ObjectAnimator.ofFloat(iv_gif, "translationX", 0.0f, dx);
            translateY = ObjectAnimator.ofFloat(iv_gif, "translationY", 0.0f, dy);

        } else {
            smlTobigXScale = ObjectAnimator.ofFloat(img_gift, "scaleX", 1.0f, 3.0f);
            smlTobigYScale = ObjectAnimator.ofFloat(img_gift, "scaleY", 1.0f, 3.0f);
            bigToSmlXScale = ObjectAnimator.ofFloat(img_gift, "scaleX", 3.0f, 0.0f);
            bigToSmlYScale = ObjectAnimator.ofFloat(img_gift, "scaleY", 3.0f, 0.0f);
            translateX = ObjectAnimator.ofFloat(img_gift, "translationX", 0.0f, dx);
            translateY = ObjectAnimator.ofFloat(img_gift, "translationY", 0.0f, dy);
        }
        AnimatorSet scaleSet = new AnimatorSet();
        scaleSet.play(smlTobigXScale).with(smlTobigYScale);
        scaleSet.setDuration(1000);

        scaleSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (is_gif && gifDrawable_play_gift != null) {
                    gifDrawable_play_gift.start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet scaleAndTranSet = new AnimatorSet();
        scaleAndTranSet.playTogether(bigToSmlXScale, bigToSmlYScale, translateX, translateY);
        scaleAndTranSet.setDuration(2000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleSet).before(scaleAndTranSet);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (is_gif && gifDrawable_play_gift != null) {
                    iv_gif.clearAnimation();
                    iv_gif.setVisibility(View.GONE);
                    gifDrawable_play_gift.stop();
                } else {
                    img_gift.clearAnimation();
                    img_gift.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

//    private void showGiftAim(VideoItem item,Map<String,Object> map) {
//        //初始化 Translate动画
//        final ImageView img_gift = new ImageView(this);
//        if(map.containsKey("giftinfo")){
//            //自定义动画
//            try {
//                Map<String,Object> gift_data = (HashMap<String,Object>)map.get("giftinfo");
//                String url = gift_data.get("trophyimg").toString();
//
//                SoundPlayUtils.play(gift_data.get("trophyvoice").toString());
//
//                String img_url = "http://" + RoomSession.host + ":" + RoomSession.port+url;
//                Glide.with(this).load(img_url).asBitmap().into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        img_gift.setImageBitmap(resource);
//                    }
//                });
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }else {
//            //默认动画
//            SoundPlayUtils.play("");
//            img_gift.setImageResource(R.drawable.ico_gift);
//        }
//
//        RelativeLayout.LayoutParams relparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        relparam.addRule(RelativeLayout.CENTER_IN_PARENT);
//        img_gift.setLayoutParams(relparam);
//        rel_parent.addView(img_gift);
//
//        int[] loca = new int[2];
//        item.lin_gift.getLocationInWindow(loca);
//
//        float x = loca[0];
//        float y = loca[1];
//
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        final int wid = dm.widthPixels;
//        final int hid = dm.heightPixels;
//        float gx = wid / 2 - img_gift.getWidth();
//        float gy = hid / 2 - img_gift.getHeight();
//
//        float dx = x - gx;
//        float dy = y - gy;
//        TranslateAnimation translateAnimation = new TranslateAnimation(0, dx, 0, dy);
//        ScaleAnimation scaleAnimation = new ScaleAnimation(4.0f, 0.1f, 4.0f, 0.1f);
//        AnimationSet set = new AnimationSet(true);
//        set.setFillBefore(false);
//        set.addAnimation(scaleAnimation);
//        set.addAnimation(translateAnimation);
//
//        set.setDuration(3000);
//        img_gift.clearAnimation();
//        set.cancel();
//        img_gift.startAnimation(set);
//        set.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                img_gift.clearAnimation();
//                img_gift.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//    }

    private void doLayout() {
        if (isZoom) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int wid = dm.widthPixels;
        int hid = dm.heightPixels;
        int a = dm.widthPixels / 7;
        int b = dm.widthPixels / 7 * 3 / 4;   // b = 3a/4
        int c = (int) (dm.widthPixels / 7 * 1.6);  // c = 1.6 * a
        int d = (int) (dm.widthPixels / 7 * 1.6 * 3 / 4); // d = 3c/4

        //顶部工具栏
        RelativeLayout.LayoutParams tool_bar_param = (RelativeLayout.LayoutParams) rel_tool_bar.getLayoutParams();
        tool_bar_param.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        tool_bar_param.height = (int) (dm.widthPixels / 7 * 3 / 4 * 0.4);
        rel_tool_bar.setLayoutParams(tool_bar_param);
        rel_pla_back_bar.setLayoutParams(tool_bar_param);

        //底部菜单
        RelativeLayout.LayoutParams re_menu_param = (RelativeLayout.LayoutParams) re_menu.getLayoutParams();
        re_menu_param.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        re_menu_param.height = (int) (dm.widthPixels / 7 * 3 / 4 * 0.4);
        re_menu.setLayoutParams(re_menu_param);

        RelativeLayout.LayoutParams lin_audio_control_param = (RelativeLayout.LayoutParams) lin_audio_control.getLayoutParams();
        lin_audio_control_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lin_audio_control_param.height = tool_bar_param.height * 3 / 2;
        lin_audio_control.setLayoutParams(lin_audio_control_param);

        RelativeLayout.LayoutParams students_param = new RelativeLayout.LayoutParams(0, 0);
        students_param.width = dm.widthPixels;
        if (RoomSession.maxVideo > 7) {
            students_param.height = (dm.widthPixels - 8 * 14) / 13 * hid_ratio / wid_ratio + 16;
        } else {
            students_param.height = (dm.widthPixels - 8 * 8) / 7 * hid_ratio / wid_ratio + 16;
        }
        students_param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        v_students.setLayoutParams(students_param);

        int heightStatusBar = 0;

        if (huawei || oppo || voio) {
            heightStatusBar = FullScreenTools.getStatusBarHeight(this);

            RelativeLayout.LayoutParams ll_par = (RelativeLayout.LayoutParams) ll_top.getLayoutParams();
            ll_par.leftMargin = heightStatusBar;
            ll_top.setLayoutParams(ll_par);

            RelativeLayout.LayoutParams re_top_right_par = (RelativeLayout.LayoutParams) re_top_right.getLayoutParams();
            re_top_right_par.rightMargin = heightStatusBar;
            re_top_right.setLayoutParams(re_top_right_par);

            RelativeLayout.LayoutParams re_bottom_par = (RelativeLayout.LayoutParams) re_bottom.getLayoutParams();
            re_bottom_par.leftMargin = heightStatusBar;
            re_bottom.setLayoutParams(re_bottom_par);

            RelativeLayout.LayoutParams ll_bottom_right_par = (RelativeLayout.LayoutParams) ll_bottom_right.getLayoutParams();
            ll_bottom_right_par.rightMargin = heightStatusBar;
            ll_bottom_right.setLayoutParams(ll_bottom_right_par);
        }

        RelativeLayout.LayoutParams rel_wb_param = (RelativeLayout.LayoutParams) rel_wb_container.getLayoutParams();
        rel_wb_param.height = (hid - tool_bar_param.height * 2 - students_param.height);

        if (huawei || oppo || voio) {
            rel_wb_param.width = dm.widthPixels - heightStatusBar;
            rel_wb_param.leftMargin = heightStatusBar;
        } else {
            rel_wb_param.width = dm.widthPixels;
        }
        rel_wb_container.setLayoutParams(rel_wb_param);
        if (wbFragment != null && WBSession.isPageFinish) {
            wbFragment.transmitWindowSize(rel_wb_param.width, rel_wb_param.height);
        }
    }

    private void do1vsnStudentVideoLayout() {
        if (videoItems.size() == 0) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int wid = dm.widthPixels;

        for (int i = 0; i < videoItems.size(); i++) {
            if (!videoItems.get(i).isLayoutd) {
                videoItems.get(i).oldX = videoItems.get(i).parent.getLeft();
                videoItems.get(i).oldY = videoItems.get(i).parent.getTop();
                videoItems.get(i).isLayoutd = true;
            }
        }

        notMoveVideoItems.clear();
        movedVideoItems.clear();
        for (int i = 0; i < videoItems.size(); i++) {
            if (!videoItems.get(i).isMoved) {
                if (videoItems.get(i).isSplitScreen) {
                    movedVideoItems.add(videoItems.get(i));
                } else {
                    notMoveVideoItems.add(videoItems.get(i));
                }
            } else {
                movedVideoItems.add(videoItems.get(i));
            }
        }

        for (int i = 0; i < notMoveVideoItems.size(); i++) {
            LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).rel_video_label.getLayoutParams();
            if (RoomSession.maxVideo > 7) {
                linparam.width = (wid - 8 * 14) / 13;
            } else {
                linparam.width = (wid - 8 * 8) / 7;
            }
            linparam.height = (int) ((linparam.width * (double) hid_ratio / (double) wid_ratio));
            notMoveVideoItems.get(i).rel_video_label.setLayoutParams(linparam);

            RelativeLayout.LayoutParams stu_name = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).lin_name_label.getLayoutParams();
            stu_name.width = linparam.width;
            stu_name.height = (int) (linparam.height * ((double) 45 / (double) 240));
            stu_name.bottomMargin = linparam.height / 14;
            notMoveVideoItems.get(i).lin_name_label.setLayoutParams(stu_name);

            RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).parent.getLayoutParams();
            if (RoomSession.maxVideo > 7) {
                layout.width = (wid - 8 * 14) / 13;
            } else {
                layout.width = (wid - 8 * 8) / 7;
            }
            layout.height = linparam.height;
            int leftmargin = 0;
            if (RoomSession.maxVideo > 7) {
                leftmargin = 8 * (i + 1) + ((wid - 8 * 14) / 13) * i;
            } else {
                leftmargin = 8 * (i + 1) + ((wid - 8 * 8) / 7) * i;
            }
            int firstLeftMargin = (wid - 8 * (notMoveVideoItems.size() - 1) - layout.width * notMoveVideoItems.size()) / 2;
            if (i == 0) {
                layout.leftMargin = firstLeftMargin;
            } else {
                layout.leftMargin = leftmargin + firstLeftMargin - 8;
            }
            layout.topMargin = 8;
            layout.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            notMoveVideoItems.get(i).parent.setLayoutParams(layout);

            LinearLayout.LayoutParams txt_name_par = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).txt_name.getLayoutParams();
           /* txt_name_par.height = stu_name.height / 3 * 2;*/
            /*txt_name_par.gravity = Gravity.TOP;*/
            /*txt_name_par.bottomMargin = KeyBoardUtil.dp2px(this, 1);*/
            txt_name_par.leftMargin = stu_name.width / 10;
            txt_name_par.gravity = Gravity.CENTER_VERTICAL;
            notMoveVideoItems.get(i).txt_name.setLayoutParams(txt_name_par);

            /*LinearLayout.LayoutParams txt_gift_par = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).txt_gift_num.getLayoutParams();
            txt_gift_par.height = stu_name.height / 3 * 2;
            *//*txt_gift_par.gravity = Gravity.CENTER_VERTICAL;*//*
            notMoveVideoItems.get(i).txt_gift_num.setLayoutParams(txt_gift_par);*/

            if (notMoveVideoItems.get(i).role == 2) {
                if (notMoveVideoItems.get(i).icon_gif != null) {
                    LinearLayout.LayoutParams icon_gif_par = (LinearLayout.LayoutParams) notMoveVideoItems.get(i).icon_gif.getLayoutParams();
                    icon_gif_par.height = stu_name.height / 10 * 7;
                    icon_gif_par.width = stu_name.height / 10 * 7;
                    /*icon_gif_par.gravity = Gravity.CENTER_VERTICAL;*/
                    notMoveVideoItems.get(i).icon_gif.setLayoutParams(icon_gif_par);
                }
            }

            RelativeLayout.LayoutParams imc = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).img_mic.getLayoutParams();
            imc.height = layout.height / 7;
            imc.width = layout.height / 7;
            imc.rightMargin = layout.width / 11;
            notMoveVideoItems.get(i).img_mic.setLayoutParams(imc);

            RelativeLayout.LayoutParams img_hand = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).img_hand.getLayoutParams();
            img_hand.height = layout.height / 5;
            img_hand.width = layout.height / 5;
            img_hand.leftMargin = layout.width / 14;
            notMoveVideoItems.get(i).img_hand.setLayoutParams(img_hand);

            RelativeLayout.LayoutParams img_pen_params = (RelativeLayout.LayoutParams) notMoveVideoItems.get(i).img_pen.getLayoutParams();
            img_pen_params.height = layout.height / 7;
            img_pen_params.width = layout.height / 7;
            img_pen_params.rightMargin = layout.width / 11;
            notMoveVideoItems.get(i).img_pen.setLayoutParams(img_pen_params);
        }

//        doLayout();

        boolean isScreen = false;
        for (int x = 0; x < movedVideoItems.size(); x++) {
            if (movedVideoItems.get(x).isSplitScreen) {
                isScreen = true;
            }
            if (isScreen) {
                break;
            }
        }

        if (isScreen) {
            for (int x = 0; x < movedVideoItems.size(); x++) {
                movedVideoItems.get(x).isSplitScreen = true;
                if (!screenID.contains(movedVideoItems.get(x).peerid)) {
                    screenID.add(0, movedVideoItems.get(x).peerid);
                }
            }
        }

        ArrayList<VideoItem> splitScreen = new ArrayList<VideoItem>();
        if (screenID.size() > 0 && isScreen) {
            for (int y = 0; y < screenID.size(); y++) {
                for (int x = 0; x < movedVideoItems.size(); x++) {
                    if (screenID.get(y).equals(movedVideoItems.get(x).peerid)) {
                        splitScreen.add(movedVideoItems.get(x));
                        break;
                    }
                }
            }
        }
        if (isScreen) {
            int size = movedVideoItems.size();
            if (size > 0 && size <= 2) {
                VideoTtemLayoutUtils.screenLessThree(splitScreen, rel_wb_container, nameLabelHeight);
            } else if (size > 2 && size <= 6) {
                VideoTtemLayoutUtils.screenThreeToSixth(splitScreen, rel_wb_container, nameLabelHeight);
            } else if (size == 7) {
                VideoTtemLayoutUtils.screenSeven(splitScreen, rel_wb_container, nameLabelHeight);
            } else if (size > 7 && size <= 12) {
                VideoTtemLayoutUtils.screenEightToTwelve(splitScreen, rel_wb_container, nameLabelHeight);
            } else if (size == 13) {
                VideoTtemLayoutUtils.screenThirteen(splitScreen, rel_wb_container, nameLabelHeight);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_file_person_media_list) {   //文件列表
            if (isChecked) {
                if (huawei || voio || oppo) {
                    coursePopupWindowUtils.showCoursePopupWindow(rel_wb_container, ll_file_list, rel_wb_container.getWidth() / 10 * 7,
                            rel_wb_container.getHeight() * 9 / 10, 0, true);
                } else {
                    coursePopupWindowUtils.showCoursePopupWindow(rel_wb_container, ll_file_list, rel_wb_container.getWidth() / 10 * 7,
                            rel_wb_container.getHeight() * 9 / 10, 0, false);
                }
                if (cb_tool_case.isChecked()) {
                    cb_tool_case.setChecked(false);
                }
            } else {
                coursePopupWindowUtils.dismissPopupWindow();
            }
        } else if (id == R.id.cb_member_list) {    //  花名册
            iv_hand.setVisibility(View.INVISIBLE);
            if (isChecked) {
                RoomSession.getInstance().getBigRoomUnmberAndUsers();
                if (huawei || voio || oppo) {
                    memberListPopupWindowUtils.showMemberListPopupWindow(rel_wb_container, ll_member_list,
                            rel_wb_container.getWidth() / 10 * 6, rel_wb_container.getHeight() * 95 / 100, 0, true);
                } else {
                    memberListPopupWindowUtils.showMemberListPopupWindow(rel_wb_container, ll_member_list,
                            rel_wb_container.getWidth() / 10 * 6, rel_wb_container.getHeight() * 95 / 100, 0, false);
                }

            } else {
                if (RoomSession.numberTimer != null) {
                    RoomSession.numberTimer.cancel();
                    RoomSession.numberTimer = null;
                }
                memberListPopupWindowUtils.dismissPopupWindow();
            }
        } else if (id == R.id.cb_tool_case) {    //  工具箱
            JSONObject js = new JSONObject();
            try {
                if (isChecked) {
                    js.put("isShow", true);
                } else {
                    js.put("isShow", false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            wbFragment.interactiveJS("'toolbox'", js.toString());
        } else if (id == R.id.cb_control) {   //  全体静音
            if (isChecked) {
                if (cb_tool_case.isChecked()) {
                    cb_tool_case.setChecked(false);
                }
                if (huawei || voio || oppo) {
                    allActionUtils.showAllActionView(rel_wb_container.getWidth() * 5 / 10, rel_wb_container.getHeight() * 8 / 10,
                            rel_wb_container, ll_control, isMute, is_have_student, 0, true);
                } else {
                    allActionUtils.showAllActionView(rel_wb_container.getWidth() * 5 / 10, rel_wb_container.getHeight() * 8 / 10,
                            rel_wb_container, ll_control, isMute, is_have_student, 0, false);
                }
            } else {
                allActionUtils.dismissPopupWindow();
            }
        } else if (id == R.id.cb_message) {    //聊天列表
            if (isChecked) {
                if (cb_tool_case.isChecked()) {
                    cb_tool_case.setChecked(false);
                }
                clearNoReadChatMessage();
                chatUtils.showChatPopupWindow(rel_wb_container.getWidth() * 6 / 10, rel_wb_container.getHeight(), rel_wb_container, rl_message);
            } else {
                chatUtils.dismissPopupWindow();
            }
        } else if (id == R.id.cb_choose_photo) {
            if (isChecked) {
                showPhotoControlPop();
            } else {
                if (popupWindowPhoto != null) {
                    popupWindowPhoto.dismiss();
                }
            }
        } else if (id == R.id.cb_remark) {  //备注
            String action;
            if (isChecked) {
                action = "whiteboardSDK_openDocumentRemark";
            } else {
                action = "whiteboardSDK_closeDocumentRemark";
            }
            wbFragment.interactiveJS(action, null);
        } else if (id == R.id.cb_large_or_small) {   //全屏或小屏
            if (isChecked) {
                wbFragment.changeWebPageFullScreen(true);
            } else {
                wbFragment.changeWebPageFullScreen(false);
            }
        } else if (id == R.id.cb_choose_mouse) { //画笔
            JSONObject js = new JSONObject();
            try {
                if (isChecked) {
                    js.put("isShow", true);
                } else {
                    js.put("isShow", false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            wbFragment.interactiveJS("'chooseShow'", js.toString());
        }
    }

    @Override
    public void close_window() {
        cb_file_person_media_list.setChecked(false);
    }

    @Override
    public void take_photo() {
        if (!isPauseLocalVideo) {
            TKRoomManager.getInstance().pauseLocalCamera();
            isPauseLocalVideo = !isPauseLocalVideo;
        }
        isOpenCamera = true;
        isBackApp = true;
        PhotoUtils.openCamera(OneToManyActivity.this);
    }

    @Override
    public void choose_photo() {
        isBackApp = true;
        PhotoUtils.openAlbum(OneToManyActivity.this);
    }

    //教师文件
    @Override
    public void choose_class_documents() {
        fileListAdapter.setArrayList(WhiteBoradManager.getInstance().getClassDocList());
        fileListAdapter.notifyDataSetChanged();
    }

    //公用文件
    @Override
    public void choose_admin_documents() {
        fileListAdapter.setArrayList(WhiteBoradManager.getInstance().getAdminDocList());
        fileListAdapter.notifyDataSetChanged();
    }

    //教室媒体文件
    @Override
    public void choose_class_media() {
        mediaListAdapter.setArrayList(WhiteBoradManager.getInstance().getClassMediaList());
        mediaListAdapter.notifyDataSetChanged();
    }

    //公用媒体文件
    @Override
    public void choose_admin_media() {
        mediaListAdapter.setArrayList(WhiteBoradManager.getInstance().getAdminmMediaList());
        mediaListAdapter.notifyDataSetChanged();
    }

    @Override
    public void close_chat_window() {
        cb_message.setChecked(false);
    }

    /**
     * 判断是否是全体静音
     */
    boolean isMute = true;

    /**
     * 判断台上是否有学生
     */
    boolean is_have_student = false;


    /**
     * 检测全体静音
     */
    private void checkMute() {
        isMute = true;
        is_have_student = false;
        for (RoomUser u : TKRoomManager.getInstance().getUsers().values()) {
            if ((u.publishState == 1 || u.publishState == 3) && u.role == 2) {
                isMute = false;
            }
            if (u.role == 2 && u.publishState > 0) {
                is_have_student = true;
            }
        }

        if (is_have_student) {
            if (isMute) {
                allActionUtils.setAllMute();
            } else {
                allActionUtils.setAllTalk();
            }
        } else {
            allActionUtils.setNoStudent();
        }
    }

    @Override
    public void all_mute() {
        RoomSession.getInstance().getPlayingList();
        for (int i = 0; i < RoomSession.playingList.size(); i++) {
            RoomUser u = RoomSession.playingList.get(i);
            if (u.role == 2) {
                if (u.publishState == 3) {
                    TKRoomManager.getInstance().changeUserPublish(u.peerId, 2);
                } else if (u.publishState == 1) {
                    TKRoomManager.getInstance().changeUserPublish(u.peerId, 4);
                }
            }
        }
    }

    @Override
    public void all_unmute() {
        RoomSession.getInstance().getPlayingList();
        for (int i = 0; i < RoomSession.playingList.size(); i++) {
            RoomUser u = RoomSession.playingList.get(i);
            if (u.role == 2) {
                if (u.publishState == 2) {
                    TKRoomManager.getInstance().changeUserPublish(u.peerId, 3);
                } else if (u.publishState == 4) {
                    TKRoomManager.getInstance().changeUserPublish(u.peerId, 1);
                }
            }
        }
    }

    @Override
    public void all_send_gift() {

        if (TKRoomManager.getInstance().getTrophyList() != null && TKRoomManager.getInstance().getTrophyList().size() > 0) {
            JSONObject js = new JSONObject();
            try {
                js.put("isShow", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            wbFragment.interactiveJS("'custom_trophy'", js.toString());
        }

        HashMap<String, RoomUser> receiverMap = new HashMap<String, RoomUser>();
        for (RoomUser u : TKRoomManager.getInstance().getUsers().values()) {
            if (u.role == 2) {
                receiverMap.put(u.peerId, u);
            }
        }
        if (receiverMap.size() != 0) {
            if (TKRoomManager.getInstance().getTrophyList().size() > 0) {
                //自定义奖杯
                sendGiftPopUtils.showSendGiftPop(rel_wb_container.getWidth() / 10 * 5,
                        rel_wb_container.getHeight() / 10 * 8, rel_wb_container, receiverMap, 0);
            } else {
                //默认奖杯
                RoomSession.getInstance().sendGift(receiverMap, null);
            }
        }
    }

    @Override
    public void all_recovery() {
        //全体恢复
        recoveryAllVideoTtems();
    }

    @Override
    public void all_control_window_close() {
        cb_control.setChecked(false);
    }

    @Override
    public void pageNumberClick(int position) {
        if (wbFragment != null) {
            wbFragment.interactiveJSSelectPage(position);
        }
        if (pageNumberPopupWindowUtils != null) {
            pageNumberPopupWindowUtils.dismissPopupWindow();
        }
    }

    @Override
    public void playSpeedClick(String play_speed) {
        //用户点击倍速选项
        playSpeedPopupWindowUtils.dismissPopupWindow();
    }

    @Override
    public void send_gift(Trophy trophy, HashMap<String, RoomUser> receiverMap) {
        if (trophy != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("trophyeffect", trophy.getTrophyeffect());
            map.put("trophyvoice", trophy.getTrophyvoice());
            map.put("trophyname", trophy.getTrophyname());
            map.put("trophyimg", trophy.getTrophyimg());
            RoomSession.getInstance().sendGift(receiverMap, map);
        }
    }

    @Override
    public void close_member_list_window() {
        cb_member_list.setChecked(false);
        if (RoomSession.numberTimer != null) {
            RoomSession.numberTimer.cancel();
            RoomSession.numberTimer = null;
        }
    }

    class AddTime extends TimerTask {
        @Override
        public void run() {
            RoomSession.serviceTime += 1;
            RoomSession.localTime = RoomSession.serviceTime - RoomSession.classStartTime;
            showTime();
        }
    }

    private void showTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String H = "";
                String M = "";
                String S = "";
                long temps = RoomSession.localTime;
                long tempm = temps / 60;
                long temph = tempm / 60;
                long sec = temps - tempm * 60;
                tempm = tempm - temph * 60;
                H = temph == 0 ? "00" : temph >= 10 ? temph + "" : "0" + temph;
                M = tempm == 0 ? "00" : tempm >= 10 ? tempm + "" : "0" + tempm;
                S = sec == 0 ? "00" : sec >= 10 ? sec + "" : "0" + sec;
                txt_hour.setText(H);
                txt_min.setText(M);
                txt_ss.setText(S);

                try {
                    if (TKRoomManager.getInstance().getRoomProperties() != null &&
                            TKRoomManager.getInstance().getRoomProperties().getLong("endtime") - RoomSession.serviceTime == 60 * 5) {
                        if (TKRoomManager.getInstance().getMySelf().role == 0 && RoomControler.haveTimeQuitClassroomAfterClass()) {
                            Toast.makeText(OneToManyActivity.this, getString(R.string.end_class_time), Toast.LENGTH_LONG).show();
                        }
                    }

                    if (TKRoomManager.getInstance().getRoomProperties() != null && RoomControler.haveTimeQuitClassroomAfterClass() &&
                            RoomSession.serviceTime == TKRoomManager.getInstance().getRoomProperties().getLong("endtime")) {
                        if (RoomSession.timerAddTime != null) {
                            RoomSession.timerAddTime.cancel();
                            RoomSession.timerAddTime = null;
                        }
                        if (RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == 0) {
                            try {
                                TKRoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new JSONObject().put("recordchat", true).toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            RoomSession.getInstance().sendClassDissToPhp();
                        }
                        RoomClient.getInstance().onClassDismiss();
                        RoomClient.getInstance().setExit(true);
                        TKRoomManager.getInstance().leaveRoom();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (TKRoomManager.getInstance().getRoomProperties() == null) {
                        return;
                    }
                    long nowTime = System.currentTimeMillis() / 1000;
                    long endTime = TKRoomManager.getInstance().getRoomProperties().getLong("endtime");
                    long startTime = TKRoomManager.getInstance().getRoomProperties().getLong("starttime");
                    long proTime = endTime - startTime;
                    boolean isstart = false;
                    if (RoomSession.localTime == 0) {
                        proTime = startTime - nowTime;
                        isstart = false;
                    } else {
                        proTime = endTime - RoomSession.localTime;
                        isstart = true;
                    }
                    if (TKRoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                        if (proTime <= 60) {
                            txt_class_begin.setBackgroundResource(R.drawable.selector_class_begin_bt);
                            txt_class_begin.setClickable(true);
                            canClassDissMiss = true;
                        }
                        if (isstart && proTime < -5 * 60) {   //自动下课
                            TKRoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all",
                                    new HashMap<String, Object>());
                            RoomSession.getInstance().sendClassDissToPhp();
                        }

                        if (isstart && proTime <= 60 && !timeMessages.get(2).isShowed) {
                            showTimeTipPop(timeMessages.get(2));
                            txt_hour.setTextAppearance(OneToManyActivity.this, R.style.time_yel);
                            txt_min.setTextAppearance(OneToManyActivity.this, R.style.time_yel);
                            txt_ss.setTextAppearance(OneToManyActivity.this, R.style.time_yel);
                            txt_mao_01.setTextAppearance(OneToManyActivity.this, R.style.time_yel);
                            txt_mao_02.setTextAppearance(OneToManyActivity.this, R.style.time_yel);
                        }
                        if (isstart && proTime <= -3 * 60 && !timeMessages.get(3).isShowed) {
                            showTimeTipPop(timeMessages.get(3));
                            txt_hour.setTextAppearance(OneToManyActivity.this, R.style.time_red);
                            txt_min.setTextAppearance(OneToManyActivity.this, R.style.time_red);
                            txt_ss.setTextAppearance(OneToManyActivity.this, R.style.time_red);
                            txt_mao_01.setTextAppearance(OneToManyActivity.this, R.style.time_red);
                            txt_mao_02.setTextAppearance(OneToManyActivity.this, R.style.time_red);
                        }
                        if (isstart && proTime <= -5 * 60 + 10 && !timeMessages.get(4).isShowed) {
                            showTimeTipPop(timeMessages.get(4));
                            txt_hour.setTextAppearance(OneToManyActivity.this, R.style.time_red);
                            txt_min.setTextAppearance(OneToManyActivity.this, R.style.time_red);
                            txt_ss.setTextAppearance(OneToManyActivity.this, R.style.time_red);
                            txt_mao_01.setTextAppearance(OneToManyActivity.this, R.style.time_red);
                            txt_mao_02.setTextAppearance(OneToManyActivity.this, R.style.time_red);

                        }
                    } else {
                        txt_class_begin.setBackgroundResource(R.drawable.selector_class_begin_bt);
                        txt_class_begin.setClickable(true);
                        canClassDissMiss = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showTimeTipPop(final TimeMessage tms) {
        if (!(TKRoomManager.getInstance().getMySelf().role == 0) || getfinalClassBeginMode()) {
            return;
        }
        try {
            if (!TKRoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tms.isShowed = true;
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.time_tip_pop, null);
        final TextView txt_tip = (TextView) contentView.findViewById(R.id.txt_tip);
        final TextView txt_i_know = (TextView) contentView.findViewById(R.id.txt_i_know);
        txt_tip.setText(Html.fromHtml(tms.message));
        final PopupWindow popupWindow = new PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(contentView);
        txt_i_know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        if (tms.hasKonwButton) {
            txt_i_know.setVisibility(View.VISIBLE);
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tms.count5 == 0) {
                                t.cancel();
                                if (!isFinishing()) {
                                    popupWindow.dismiss();
                                }
                            }
                            txt_i_know.setText(getResources().getString(R.string.i_konw) + tms.count5 + "'");
                            tms.count5--;
                        }
                    });
                }
            }, 1000, 1000);
        } else {
            tms.count5 = 10;
            txt_i_know.setVisibility(View.GONE);
            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tms.count5 == 0) {
                                t.cancel();
                                popupWindow.dismiss();
                            }
                            txt_tip.setText(tms.message + " " + tms.count5 + "'");
                            tms.count5--;
                        }
                    });
                }
            }, 1000, 1000);
        }
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    popupWindow.showAsDropDown(lin_time, txt_hour.getWidth() * 4, -lin_time.getMeasuredHeight());
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        }
        return super.onKeyDown(keyCode, event);
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
    protected void onResume() {
        super.onResume();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (RoomSession.isClassBegin) {
            if (TKRoomManager.getInstance().getMySelf() != null && !TextUtils.isEmpty(TKRoomManager.getInstance().getMySelf().peerId)) {
                doPlayVideo(TKRoomManager.getInstance().getMySelf().peerId);
            }
        }
        if (RoomControler.haveTimeQuitClassroomAfterClass() && !RoomSession.isClassBegin) {
            if (RoomSession.timerAfterLeaved != null) {
                RoomSession.timerAfterLeaved.cancel();
                RoomSession.timerAfterLeaved = null;
            }
            RoomSession.getInstance().getSystemNowTime();
        }
    }

    public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;
    private BroadcastReceiverMgr mBroadcastReceiver;

    //按钮1-注册广播
    public void registerIt() {
        mBroadcastReceiver = new BroadcastReceiverMgr();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(B_PHONE_STATE);
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    //按钮2-撤销广播
    public void unregisterIt() {
        unregisterReceiver(mBroadcastReceiver);
    }

    //点击通知进入一个Activity，点击返回时进入指定页面。
    public void resultActivityBackApp() {
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setTicker(getString(R.string.app_name));
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText(getString(R.string.back_hint));
        //设置点击一次后消失（如果没有点击事件，则该方法无效。）
        mBuilder.setAutoCancel(true);
        //点击通知之后需要跳转的页面
        Intent resultIntent = new Intent(this, OneToManyActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pIntent);
    }

    /**
     * 设置未读消息数
     *
     * @param num
     */
    private void setNoReadChatMessage(int num) {
        if (tv_no_read_message_number != null) {
            tv_no_read_message_number.setVisibility(View.VISIBLE);
            if (num > 99) {
                tv_no_read_message_number.setText("99+");
            } else {
                tv_no_read_message_number.setText(num + "");
            }
        }
    }

    /**
     * 清空未读消息数
     */
    private void clearNoReadChatMessage() {
        RoomSession.chatDataCache.clear();
        if (tv_no_read_message_number != null) {
            tv_no_read_message_number.setVisibility(View.GONE);
            tv_no_read_message_number.setText("");
        }
    }

    @Override
    public void didReceivedNotification(final int id, final Object... args) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    case RoomSession.onRoomJoin:
                        onRoomJoin();
                        break;

                    case RoomSession.onRoomLeave:
                        onRoomLeave();
                        break;

                    case RoomSession.onError:
                        int errorCode = (int) args[0];
                        String errMsg = (String) args[1];
                        onError(errorCode, errMsg);
                        break;

                    case RoomSession.onWarning:
                        int onWarning = (int) args[0];
                        onWarning(onWarning);
                        break;

                    case RoomSession.onUserJoin:
                        RoomUser user = (RoomUser) args[0];
                        boolean inList = (boolean) args[1];
                        onUserJoined(user, inList);
                        break;

                    case RoomSession.onUserLeft:
                        RoomUser roomUser = (RoomUser) args[0];
                        onUserLeft(roomUser);
                        break;

                    case RoomSession.onUserPropertyChanged:
                        RoomUser propertyUser = (RoomUser) args[0];
                        Map<String, Object> map = (Map<String, Object>) args[1];
                        String fromId = (String) args[2];
                        onUserPropertyChanged(propertyUser, map, fromId);
                        break;

                    case RoomSession.onUserPublishState:
                        String peerId = (String) args[0];
                        int state = (int) args[1];
                        onUserPublishState(peerId, state);
                        break;

                    case RoomSession.onKickedout:
                        break;

                    case RoomSession.onMessageReceived:
                        RoomUser chatUser = (RoomUser) args[0];
                        onMessageReceived(chatUser);
                        break;

                    case RoomSession.onRemotePubMsg:
                        String namePub = (String) args[1];
                        long pubMsgTS = (long) args[2];
                        Object dataPub = (Object) args[3];
                        boolean inListPub = (boolean) args[4];
                        onRemotePubMsg(namePub, pubMsgTS, dataPub, inListPub);
                        break;

                    case RoomSession.onRemoteDelMsg:
                        String nameDel = (String) args[1];
                        long delMsgTS = (long) args[2];
                        onRemoteDelMsg(nameDel, delMsgTS);
                        break;

                    case RoomSession.onUpdateAttributeStream:
                        String attributesPeerId = (String) args[0];
                        long streamPos = (long) args[1];
                        Boolean isPlay = (Boolean) args[2];
                        Map<String, Object> onUpdateAttribute = (Map<String, Object>) args[3];
                        onUpdateAttributeStream(attributesPeerId, streamPos, isPlay, onUpdateAttribute);
                        break;

                    case RoomSession.onPlayBackUpdateTime:
                        long backTimePos = (long) args[0];
                        onPlayBackUpdateTime(backTimePos);
                        break;

                    case RoomSession.onPlayBackClearAll:
                        onPlayBackClearAll();
                        break;

                    case RoomSession.onPlayBackDuration:
                        long startTime = (long) args[0];
                        long endTime = (long) args[1];
                        onPlayBackDuration(startTime, endTime);
                        break;

                    case RoomSession.onPlayBackEnd:
                        onPlayBackEnd();
                        break;

                    case RoomSession.onShareMediaState: //MP4 MP3
                        String shareMediaPeerId = (String) args[0];
                        int shareMediaState = (int) args[1];
                        Map<String, Object> shareMediaAttrs = (Map<String, Object>) args[2];
                        onShareMediaState(shareMediaPeerId, shareMediaState, shareMediaAttrs);
                        break;

                    case RoomSession.onShareScreenState:
                        String peerIdScreen = (String) args[0];
                        int stateScreen = (int) args[1];
                        onShareScreenState(peerIdScreen, stateScreen);
                        break;

                    case RoomSession.onShareFileState:
                        String peerIdShareFile = (String) args[0];
                        int stateShareFile = (int) args[1];
                        onShareFileState(peerIdShareFile, stateShareFile);
                        break;

                    case RoomSession.onAudioVolume:
                        String volumePeerId = (String) args[0];
                        int volume = (int) args[1];
                        onAudioVolume(volumePeerId, volume);
                        break;

                    case RoomSession.onRoomUser:
                        int roomUserCode = (int) args[0];
                        ArrayList<RoomUser> userList = (ArrayList<RoomUser>) args[1];
                        onRoomUser(roomUserCode, userList);
                        break;

                    case RoomSession.onRoomUserNumber:
                        int roomUserNumberCode = (int) args[0];
                        int roomUserNumber = (int) args[1];
                        onRoomUserNumber(roomUserNumberCode, roomUserNumber);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void onRoomUserNumber(int roomUserNumberCode, int roomUserNumber) {
        if (roomUserNumberCode == 0) {
            if (memberListPopupWindowUtils != null && memberListPopupWindowUtils.isShowing()) {
                memberListPopupWindowUtils.setTiteNumber(roomUserNumber);
            }
        }
    }

    private void onRoomUser(int roomUserCode, ArrayList<RoomUser> userList) {
        if (roomUserCode == 0 && memberListAdapter != null) {
            memberListAdapter.setUserList(userList);
            memberListAdapter.notifyDataSetChanged();
        }
    }

    private void onAudioVolume(String volumePeerId, int volume) {
        RoomUser roomUser = TKRoomManager.getInstance().getUser(volumePeerId);
        for (int x = 0; x < videoItems.size(); x++) {
            if (videoItems.get(x).peerid.equals(volumePeerId)) {
                if (volume >= 5) {
                    videoItems.get(x).img_mic.setVisibility(View.VISIBLE);
                    if (roomUser != null && roomUser.publishState == 1 || roomUser.publishState == 3) {
                        videoItems.get(x).img_mic.setImageResource(R.drawable.icon_video_voice);
                    } else {
                        videoItems.get(x).img_mic.setImageResource(R.drawable.icon_audio_disable);
                    }
                } else {
                    if (roomUser != null && roomUser.publishState == 1 || roomUser.publishState == 3) {
                        videoItems.get(x).img_mic.setVisibility(View.GONE);
                    } else {
                        videoItems.get(x).img_mic.setImageResource(R.drawable.icon_audio_disable);
                        videoItems.get(x).img_mic.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void onWarning(int onWarning) {
        if (10001 == onWarning) {
            if (isOpenCamera) {
                PhotoUtils.openCamera(OneToManyActivity.this);
            }
        }
    }

    private void onError(int errorCode, String errMsg) {
        if (errorCode == 10004) {  //UDP连接不同
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.ShowAlertDialog(OneToManyActivity.this, getString(R.string.udp_alert), 0);
                }
            });
        } else if (10002 == errorCode) {

            LogUtils.isLogE("mxl", "errorCode:+errMsg:" + errMsg + "//errorCode");

            if (frameAnimation != null) {
                frameAnimation.stopAnimation();
                re_loading.setVisibility(View.GONE);
            }

            removeVideoFragment();
            romoveScreenFragment();
            romoveMovieFragment();

            mediaListAdapter.setLocalfileid(-1);
            isWBMediaPlay = false;
            scalemap.clear();
            for (int x = 0; x < videoItems.size(); x++) {
                rel_students.removeView(videoItems.get(x).parent);
            }
            videoItems.clear();
            clear();
            finish();
        } else if (errorCode == 10005) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.ShowAlertDialog(OneToManyActivity.this, getString(R.string.fire_wall_alert), 0);
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "udpstate", 2);
                }
            });
        }
    }

    private void onRoomJoin() {

        if (TKRoomManager.getInstance().getTrophyList() != null && TKRoomManager.getInstance().getTrophyList().size() > 0) {
            SoundPlayUtils.loadTrophy(RoomSession.host, RoomSession.port, this);
        } else {
            if (TextUtils.isEmpty(TKRoomManager.getInstance().get_MP3Url())) {
                SoundPlayUtils.init(this);
            } else {
                SoundPlayUtils.loadMP3(RoomSession.host, RoomSession.port, this);
            }
        }

        if (getWindow().isActive() && frameAnimation != null) {
            frameAnimation.stopAnimation();
            re_loading.setVisibility(View.GONE);
        }

        for (RoomUser u : TKRoomManager.getInstance().getUsers().values()) {
            changeUserState(u);
        }

        initWidAndHid();

        changeUserState(TKRoomManager.getInstance().getMySelf());
        txt_room_name.setText(StringEscapeUtils.unescapeHtml4(TKRoomManager.getInstance().getRoomName()));
        initViewByRoomTypeAndTeacher();
        txt_hand_up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!RoomSession.isClassBegin) {
                    return true;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        RoomUser roomUser = TKRoomManager.getInstance().getMySelf();
                        if (roomUser != null && roomUser.publishState != 0) {
                            TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "raisehand", true);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        RoomUser user = TKRoomManager.getInstance().getMySelf();
                        //判断是否在台上
                        if (user.publishState == 0) {
                            if (TKRoomManager.getInstance().getMySelf().properties.containsKey("raisehand")) {
                                boolean israisehand = Tools.isTure(TKRoomManager.getInstance().getMySelf().properties.get("raisehand"));
                                if (israisehand) {
                                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "raisehand", false);
                                } else {
                                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "raisehand", true);
                                }
                            } else {
                                TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "raisehand", true);
                            }
                        } else {
                            TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "raisehand", false);
                        }
                        break;
                }
                return true;
            }
        });

        if (!RoomSession.isClassBegin) {
            playSelfBeforeClassBegin();
        }
        doLayout();

        if (TKRoomManager.getInstance().getMySelf().role == -1) {
            showPlayBackControlView();

            rel_students.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    showPlayBackControlView();
                    return true;
                }
            });
        }

        if (!PermissionTest.cameraIsCanUse()) {
            Tools.showDialog(this, R.string.remind, R.string.camera_hint, new Tools.OnDialogClick() {
                @Override
                public void dialog_ok(Dialog dialog) {
                    dialog.dismiss();
                    Tools.getAppDetailSettingIntent(OneToManyActivity.this);
                }
            }, 0);
        }

        if (PermissionTest.getRecordState() == -2) {
            Tools.showDialog(this, R.string.remind, R.string.mic_hint, new Tools.OnDialogClick() {
                @Override
                public void dialog_ok(Dialog dialog) {
                    dialog.dismiss();
                    Tools.getAppDetailSettingIntent(OneToManyActivity.this);
                }
            }, 0);
        }

    }

    private void showPlayBackControlView() {
        if (is_show_control_view) {
            is_show_control_view = false;
            moveUpView(rel_play_back);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            backView(rel_play_back);
                            is_show_control_view = true;
                        }
                    });
                }
            }, 3000);
        }
    }

    Timer timer = new Timer();
    boolean is_show_control_view = true;

    private void moveUpView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -120 * ScreenScale.getWidthScale());
        animator.setDuration(300);
        animator.start();
    }

    private void backView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0);
        animator.setDuration(300);
        animator.start();
    }

    private void onRoomLeave() {
        removeVideoFragment();
        romoveScreenFragment();
        romoveMovieFragment();
        mediaListAdapter.setLocalfileid(-1);
        isWBMediaPlay = false;
        scalemap.clear();

        if (RoomClient.getInstance().isExit()) {
            clear();
            finish();
        } else {
            for (int x = 0; x < videoItems.size(); x++) {
                rel_students.removeView(videoItems.get(x).parent);
            }
            videoItems.clear();

            re_loading.setVisibility(View.VISIBLE);
            if (frameAnimation != null) {
                frameAnimation.playAnimation();
                tv_load.setText(getString(R.string.connected));
            }

            if (popupWindowPhoto != null) {
                popupWindowPhoto.dismiss();
            }
        }

        if (gifDrawable != null) {
            gifDrawable.stop();
        }

        img_disk.clearAnimation();
        lin_audio_control.setVisibility(View.INVISIBLE);
    }

    private void onUserJoined(RoomUser user, boolean inList) {
        changeUserState(user);
        chlistAdapter.notifyDataSetChanged();
        memberListAdapter.notifyDataSetChanged();
        memberListPopupWindowUtils.setTiteNumber(RoomSession.memberList.size());
//        list_chat.setSelection(chlistAdapter.getCount());
    }

    private void onUserLeft(RoomUser roomUser) {
        stuMoveInfoMap.remove(roomUser.peerId);
        chlistAdapter.notifyDataSetChanged();
//        list_chat.setSelection(chlistAdapter.getCount());
        memberListAdapter.notifyDataSetChanged();
        memberListPopupWindowUtils.setTiteNumber(RoomSession.memberList.size());
        changeUserState(roomUser);
        do1vsnStudentUnPlayVideo(roomUser.peerId);
        checkMute();
    }

    private void onUserPropertyChanged(RoomUser roomUser, Map<String, Object> map, String fromId) {
        if (map.containsKey("publishstate") && roomUser.publishState == 0) {
            if (screenID.contains(roomUser.peerId)) {
                screenID.remove(roomUser.peerId);
                for (int x = 0; x < videoItems.size(); x++) {
                    if (roomUser.peerId.equals(videoItems.get(x).peerid)) {
                        videoItems.get(x).isSplitScreen = false;
                    }
                }
                if (TKRoomManager.getInstance().getMySelf().role == 0) {
                    sendSplitScreen();
                }
            }
        }

        if (map.containsKey("disablechat")) {
            if (roomUser == null) {
                return;
            }
            if (TKRoomManager.getInstance().getMySelf().peerId.equals(roomUser.peerId)) {
                chatUtils.setEdtInputHint();
            }
        }

        if (RoomSession.playingMap.containsKey(roomUser.peerId) && roomUser.publishState > 0) {
            memberListAdapter.notifyDataSetChanged();
            if (roomUser.publishState > 0) {
                doPlayVideo(roomUser.peerId);
            } else {
                do1vsnStudentUnPlayVideo(roomUser.peerId);
            }
        }

        do1vsnStudentVideoLayout();
        changeUserState(roomUser);
        /*checkRaiseHands();*/

        if (map.containsKey("isInBackGround")) {
            if (roomUser == null) {
                return;
            }
            boolean isinback = Tools.isTure(map.get("isInBackGround"));
            setBackgroundOrReception(isinback, roomUser);
        }

        if (RoomSession.isClassBegin) {
            if (TKRoomManager.getInstance().getMySelf().properties.containsKey("raisehand")) {
                boolean israisehand = Tools.isTure(TKRoomManager.getInstance().getMySelf().properties.get("raisehand"));
                RoomUser selfUser = TKRoomManager.getInstance().getMySelf();
                if (israisehand) {
                    iv_hand.setVisibility(View.VISIBLE);
                    if (selfUser != null && selfUser.publishState == 0) {
                        txt_hand_up.setText(R.string.no_raise);
                    } else {
                        txt_hand_up.setText(R.string.raiseing);
                        txt_hand_up.setBackgroundResource(R.drawable.handing_up_bg);
                        txt_hand_up.setTextAppearance(OneToManyActivity.this, R.style.white);
                    }
                } else {
                    iv_hand.setVisibility(View.INVISIBLE);
                    txt_hand_up.setText(R.string.raise); //同意了，或者拒绝了
                    txt_hand_up.setBackgroundResource(R.drawable.click_btn_xiake);
                    txt_hand_up.setTextAppearance(OneToManyActivity.this, R.style.color_hands_up);
                }
            } else {
                if (map.containsKey("raisehand")) {
                    boolean israisehand = Tools.isTure(map.get("raisehand"));
                    if (israisehand) {
                        iv_hand.setVisibility(View.VISIBLE);
                    } else {
                        iv_hand.setVisibility(View.INVISIBLE);
                    }
                }
                txt_hand_up.setText(R.string.raise); //还没举手
                txt_hand_up.setBackgroundResource(R.drawable.click_btn_xiake);
                txt_hand_up.setTextAppearance(OneToManyActivity.this, R.style.color_hands_up);
            }

            if (map.containsKey("isInBackGround")) {
                if (TKRoomManager.getInstance().getMySelf().role != 2) {
                    memberListAdapter.notifyDataSetChanged();
                    chlistAdapter.notifyDataSetChanged();
                }
            }

            if (map.containsKey("giftnumber") && !roomUser.peerId.equals(fromId)) {
                for (int i = 0; i < videoItems.size(); i++) {
                    if (roomUser.peerId.equals(videoItems.get(i).peerid)) {
                        showGiftAim(videoItems.get(i), map);
                        break;
                    }
                }
            }

            if (roomUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("candraw")
                    && !(TKRoomManager.getInstance().getMySelf().role == 0)) {
                boolean candraw = Tools.isTure(map.get("candraw"));
                if (candraw) {
                    if (TKRoomManager.getInstance().getMySelf().role != 4) {
                        cb_choose_photo.setVisibility(View.VISIBLE);
                        cb_choose_mouse.setVisibility(View.VISIBLE);
                        cb_choose_mouse.setChecked(false);
                    }
                } else {
                    cb_choose_photo.setVisibility(View.GONE);
                    cb_choose_mouse.setVisibility(View.GONE);
                    if (popupWindowPhoto != null) {
                        popupWindowPhoto.dismiss();
                    }
                }
            }
        }

        if (roomUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("volume")) {
            Number n_volume = (Number) map.get("volume");
            int int_volume = n_volume.intValue();
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, int_volume, 0);
        }

        checkMute();
        memberListAdapter.notifyDataSetChanged();
    }

    private void onUserPublishState(String peerId, int state) {
        if (state > 0) {
            doPlayVideo(peerId);
        } else {
            stuMoveInfoMap.remove(peerId);
            do1vsnStudentUnPlayVideo(peerId);

            if (TKRoomManager.getInstance().getUser(peerId).role == 0) {
                studentMoveOrScreen();
            }

            if (!RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == 0 &&
                    TKRoomManager.getInstance().getUser(peerId).role == 0 &&
                    TKRoomManager.getInstance().getMySelf().peerId.equals(peerId)) {
                playSelfBeforeClassBegin();
            }
        }

        doLayout();
        do1vsnStudentVideoLayout();
        changeUserState(TKRoomManager.getInstance().getUser(peerId));
        memberListAdapter.notifyDataSetChanged();
        if (studentPopupWindow != null) {
            studentPopupWindow.dismiss();
        }
    }

    private void onMessageReceived(RoomUser chatUser) {
        if (!chatUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && !chatUtils.popupIsShow()) {
            setNoReadChatMessage(RoomSession.chatDataCache.size());
        } else if (chatUtils.popupIsShow()) {
            RoomSession.chatDataCache.clear();
        }
        chlistAdapter.notifyDataSetChanged();
    }

    private void onRemotePubMsg(String name, long pubMsgTS, Object data, boolean inList) {

        if (name.equals("ClassBegin")) {

            if (TKRoomManager.getInstance().getMySelf().publishState == 2 || TKRoomManager.getInstance().getMySelf().publishState == 0 || TKRoomManager.getInstance().getMySelf().publishState == 4) {
                txt_hand_up.setClickable(true);
            }

            try {
                if (!TKRoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                    txt_class_begin.setBackgroundResource(R.drawable.selector_class_begin_bt);
                    txt_class_begin.setText(R.string.classdismiss);
                    txt_class_begin.setClickable(true);
                } else {
                    txt_class_begin.setBackgroundResource(R.drawable.selector_class_begin_bt);
                    txt_class_begin.setText(R.string.classdismiss);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                wbFragment.localChangeDoc();
            }
            initViewByRoomTypeAndTeacher();

            if (TKRoomManager.getInstance().getMySelf().role == 4 || TKRoomManager.getInstance().getMySelf().role == 0) {
                if (!RoomControler.isShowClassBeginButton()) {
                    txt_class_begin.setVisibility(View.VISIBLE);
                } else
                    txt_class_begin.setVisibility(View.GONE);
            } else {
                txt_class_begin.setVisibility(View.GONE);
            }

            if (!RoomControler.isReleasedBeforeClass()) {
                unPlaySelfAfterClassBegin();
            }

            if (TextUtils.isEmpty(RoomSession.path)) {
                if (!RoomControler.isReleasedBeforeClass()) {
                    if (TKRoomManager.getInstance().getMySelf().role == 0) {
                        TKRoomManager.getInstance().changeUserPublish(TKRoomManager.getInstance().getMySelf().peerId, 3);
                    } else if (RoomControler.isAutomaticUp() && RoomSession.publishSet.size() < RoomSession.maxVideo && TKRoomManager.getInstance().getMySelf().role == 2) {
                        TKRoomManager.getInstance().changeUserPublish(TKRoomManager.getInstance().getMySelf().peerId, 3);
                    }
                } else {
                    if (TKRoomManager.getInstance().getMySelf().role == 0) {
                        TKRoomManager.getInstance().changeUserPublish(TKRoomManager.getInstance().getMySelf().peerId, 3);
                    } else if (!RoomControler.isAutomaticUp() && TKRoomManager.getInstance().getMySelf().role == 2) {
                        TKRoomManager.getInstance().changeUserPublish(TKRoomManager.getInstance().getMySelf().peerId, 0);
                    } else if (RoomControler.isAutomaticUp() &&
                            TKRoomManager.getInstance().getMySelf().publishState != 3 && RoomSession.publishSet.size() < RoomSession.maxVideo) {
                        if (TKRoomManager.getInstance().getMySelf().role == 2 || TKRoomManager.getInstance().getMySelf().role == 0) {
                            TKRoomManager.getInstance().changeUserPublish(TKRoomManager.getInstance().getMySelf().peerId, 3);
                        }
                    }
                }
            }

        } else if (name.equals("UpdateTime")) {
            if (RoomSession.isClassBegin) {
                if (TKRoomManager.getInstance().getMySelf().role != -1) {
                    lin_time.setVisibility(View.VISIBLE);
                }
                if (RoomSession.timerAddTime == null) {
                    RoomSession.timerAddTime = new Timer();
                    RoomSession.timerAddTime.schedule(new AddTime(), 1000, 1000);
                }
            } else {
                if (RoomSession.timerbefClassbegin == null && !RoomSession.isClassBegin && !getfinalClassBeginMode()) {
                    RoomSession.timerbefClassbegin = new Timer();
                    RoomSession.timerbefClassbegin.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    long nowTime = System.currentTimeMillis() / 1000;
                                    long startTime = 0;
                                    if (TKRoomManager.getInstance().getRoomProperties() != null) {
                                        startTime = TKRoomManager.getInstance().getRoomProperties().optLong("starttime");
                                    }
                                    long proTime = startTime - nowTime;
                                    if (proTime == 60 && !timeMessages.get(0).isShowed) {
                                        showTimeTipPop(timeMessages.get(0));
                                    }
                                    if (proTime <= -60 && timeMessages != null && timeMessages.size() > 1 && !timeMessages.get(1).isShowed) {
                                        int overtime = Math.abs((int) (proTime / 60));
                                        timeMessages.get(1).message = getString(R.string.classroom_part_01) + "<font color='#FFD700'>" + overtime + "</font> " + getString(R.string.classroom_part_02);
                                        showTimeTipPop(timeMessages.get(1));
                                    }
                                    if (proTime <= 60) {
                                        txt_class_begin.setBackgroundResource(R.drawable.selector_class_begin_bt);
                                        txt_class_begin.setText(R.string.classbegin);
                                        txt_class_begin.setClickable(true);
                                    }
                                }
                            });
                        }
                    }, 500, 1000);
                }
            }
        }

        if (name.equals("ShowPage")) {
            ShareDoc shareDoc = WhiteBoradManager.getInstance().getCurrentFileDoc();
            if (shareDoc != null) {
                if (shareDoc.isH5Docment() || shareDoc.isDynamicPPT()) {
                    iv_large_white_board.setVisibility(View.INVISIBLE);
                    iv_small_white_board.setVisibility(View.INVISIBLE);
                } else {
                    iv_large_white_board.setVisibility(View.VISIBLE);
                    iv_small_white_board.setVisibility(View.VISIBLE);
                }
            }
            mediaListAdapter.notifyDataSetChanged();
            fileListAdapter.notifyDataSetChanged();
        }

        if (name.equals("BigRoom")) {
            if (memberListPopupWindowUtils.isShowing()) {
                RoomSession.getInstance().getBigRoomUnmberAndUsers();
            }
            allActionUtils.setGifStatu();
        }

        if (name.equals("StreamFailure")) {

            Map<String, Object> mapdata = null;
            if (data instanceof String) {
                String str = (String) data;
                try {
                    JSONObject js = new JSONObject(str);
                    mapdata = Tools.toMap(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mapdata = (Map<String, Object>) data;
            }
            String stupeerid = (String) mapdata.get("studentId");

            RoomSession.pandingSet.remove(stupeerid);
            memberListAdapter.setPubFailUserId(stupeerid);
            memberListAdapter.notifyDataSetChanged();

            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                RoomUser u = TKRoomManager.getInstance().getUser(stupeerid);
                if (u != null) {
                    if (u.properties.containsKey("passivityPublish")) {
                        int failuretype = -1;
                        if (u.properties.get("failuretype") != null) {
                            failuretype = (Integer) u.properties.get("failuretype");
                        }
                        switch (failuretype) {
                            case 1:
                                Toast.makeText(this, R.string.udp_faild, Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(this, R.string.publish_faild, Toast.LENGTH_LONG).show();
                                break;
                            case 3:
                                Toast.makeText(this, R.string.member_overload, Toast.LENGTH_LONG).show();
                                break;
                            case 4:
                                Toast.makeText(this, u.nickName + getResources().getString(R.string.select_back_hint), Toast.LENGTH_LONG).show();
                                break;
                            case 5:
                                Toast.makeText(this, R.string.udp_break, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    u.properties.remove("passivityPublish");
                }
            }
        }

        if (name.equals("videoDraghandle")) {
            if (studentPopupWindow != null) {
                studentPopupWindow.dismiss();
            }
            JSONObject mapdata = null;
            if (data instanceof String) {
                String str = (String) data;
                try {
                    mapdata = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mapdata = new JSONObject((Map<String, Object>) data);
            }
            videoarr = mapdata.optJSONObject("otherVideoStyle");
            if (videoarr != null) {
                sIterator = videoarr.keys();
                while (sIterator.hasNext()) {
                    String peerid = sIterator.next();
                    // 根据key获得value, value也可以是JSONObject,JSONArray,使用对应的参数接收即可
                    JSONObject videoinfo = videoarr.optJSONObject(peerid);
                    if (videoinfo != null) {
                        float left = (float) videoinfo.optDouble("percentLeft");
                        float top = (float) videoinfo.optDouble("percentTop");
                        boolean isDrag = Tools.isTure(videoinfo.opt("isDrag"));
                        MoveVideoInfo mi = new MoveVideoInfo();
                        if (inList && TKRoomManager.getInstance().getMySelf().peerId.equals(peerid)) {
                            mi.top = 0;
                            mi.left = 0;
                            mi.isDrag = false;
                        } else {
                            mi.top = top;
                            mi.left = left;
                            mi.isDrag = isDrag;
                        }
                        stuMoveInfoMap.put(peerid, mi);
                        if (inList) {
                            continue;
                        }
                        moveStudent(peerid, top, left, isDrag);
                    }
                }
            }
            if (inList) {
                JSONObject jsonObject = new JSONObject();
                JSONObject moveData = new JSONObject();
                try {
                    Set set = stuMoveInfoMap.keySet();
                    if (set != null) {
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()) {
                            JSONObject md = new JSONObject();
                            String peerid = (String) iterator.next();
                            MoveVideoInfo moveVideoInfo = stuMoveInfoMap.get(peerid);
                            md.put("percentTop", moveVideoInfo.top);
                            md.put("percentLeft", moveVideoInfo.left);
                            md.put("isDrag", moveVideoInfo.isDrag);
                            moveData.put(peerid, md);
                        }
                    }
                    jsonObject.put("otherVideoStyle", moveData);
                    TKRoomManager.getInstance().pubMsg("videoDraghandle", "videoDraghandle", "__allExceptSender",
                            jsonObject.toString(), true, "ClassBegin", null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (name.equals("VideoSplitScreen")) {
            if (studentPopupWindow != null) {
                studentPopupWindow.dismiss();
            }
            JSONObject splitScreen = null;
            if (data instanceof String) {
                String str = (String) data;
                try {
                    splitScreen = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                splitScreen = new JSONObject((Map<String, Object>) data);
            }
            screen = splitScreen.optJSONArray("userIDArry");
            try {
                screenID.clear();
                for (int y = 0; y < screen.length(); y++) {
                    String peerid = (String) screen.get(y);
                    screenID.add(peerid);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (videoItems.size() > 0) {
                for (int x = 0; x < videoItems.size(); x++) {
                    if (screenID.contains(videoItems.get(x).peerid)) {
                        videoItems.get(x).isSplitScreen = true;
                    } else {
                        videoItems.get(x).isSplitScreen = false;
                    }
                }
                do1vsnStudentVideoLayout();
            }
        }

        if (name.equals("VideoChangeSize")) {
            if (studentPopupWindow != null) {
                studentPopupWindow.dismiss();
            }
            JSONObject mapdata = null;
            if (data instanceof String) {
                String str = (String) data;
                try {
                    mapdata = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mapdata = new JSONObject((Map<String, Object>) data);
            }
            JSONObject scaleVideoData = mapdata.optJSONObject("ScaleVideoData");
            if (scaleVideoData != null) {
                Iterator<String> scaleKeys = scaleVideoData.keys();
                while (scaleKeys.hasNext()) {
                    String peerid = scaleKeys.next();
                    JSONObject videoinfo = scaleVideoData.optJSONObject(peerid);
                    float scale = (float) videoinfo.optDouble("scale");
                    scalemap.put(peerid, scale);
                    for (int x = 0; x < videoItems.size(); x++) {
                        if (videoItems.get(x).peerid.equals(peerid)) {
                            LayoutZoomOrIn.zoomMsgMouldVideoItem(videoItems.get(x), scale, printWidth, printHeight,
                                    rel_students.getHeight() - v_students.getHeight());
                        }
                    }
                }
            }
            do1vsnStudentVideoLayout();
        }

        if (name.equals("EveryoneBanChat")) {

            ChatData ch = new ChatData();
            ch.setStystemMsg(true);
            ch.setMsgTime(System.currentTimeMillis());
            ch.setMessage(getString(R.string.chat_prompt_yes));
            ch.setTrans(false);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date curDate = null;
            if (StringUtils.isEmpty(RoomSession.path)) {
                curDate = new Date(System.currentTimeMillis());//获取当前时间
            } else {
                curDate = new Date(pubMsgTS);
            }
            String str = formatter.format(curDate);
            ch.setTime(str);
            RoomSession.chatList.add(ch);

            if (chatUtils.popupIsShow()) {
                chatUtils.setEdtInputHint();
            }
            chlistAdapter.notifyDataSetChanged();

            if (chatUtils.popupIsShow()) {
                chatUtils.setTitelPrompt();
            }


            if (TKRoomManager.getInstance().getMySelf().role != 0) {
                if (inList) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "disablechat", true);
                }
            }
        }
    }

    private void onRemoteDelMsg(String name, long delMsgTS) {

        if (name.equals("ClassBegin")) {
            txt_class_begin.setVisibility(View.GONE);
            cb_choose_mouse.setVisibility(View.GONE);
            txt_hand_up.setVisibility(View.INVISIBLE);
            ll_tool_case.setVisibility(View.GONE);
            ll_control.setVisibility(View.GONE);

            try {
                if (!TKRoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                    if (RoomSession.userrole == 0) {
                        txt_class_begin.setBackgroundResource(R.drawable.selector_class_begin_bt);
                        txt_class_begin.setText(R.string.classbegin);
                        txt_class_begin.setClickable(true);
                        txt_class_begin.setVisibility(View.VISIBLE);
                    } else {
                        if (!RoomControler.isNotLeaveAfterClass()) {
                            sendGiftPopUtils.deleteImage();
                            RoomClient.getInstance().setExit(true);
                            TKRoomManager.getInstance().leaveRoom();
                        }
                    }
                    txt_hand_up.setClickable(false);
                    txt_hand_up.setText(R.string.raise);
                    lin_time.setVisibility(View.INVISIBLE);
                    txt_hour.setText("00");
                    txt_min.setText("00");
                    txt_ss.setText("00");
                    recoveryAllVideoTtems();
                    memberListAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (!RoomControler.isNotLeaveAfterClass()) {
                        RoomSession.chatList.clear();
                        chlistAdapter.notifyDataSetChanged();
                    }
                }
            }, 250);
        }

        if (name.equals("VideoSplitScreen")) {
            if (studentPopupWindow != null) {
                studentPopupWindow.dismiss();
            }
            if (studentPopupWindow != null) {
                studentPopupWindow.dismiss();
            }
            screenID.clear();
            if (videoItems.size() > 0) {
                for (int x = 0; x < videoItems.size(); x++) {
                    videoItems.get(x).isSplitScreen = false;
                }
            }
            do1vsnStudentVideoLayout();
        }

        if (name.equals("EveryoneBanChat")) {

            ChatData ch = new ChatData();
            ch.setStystemMsg(true);
            ch.setMsgTime(System.currentTimeMillis());
            ch.setMessage(getString(R.string.chat_prompt_no));
            ch.setTrans(false);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date curDate = null;
            if (StringUtils.isEmpty(RoomSession.path)) {
                curDate = new Date(System.currentTimeMillis());
            } else {
                curDate = new Date(delMsgTS);
            }
            String str = formatter.format(curDate);
            ch.setTime(str);

            RoomSession.chatList.add(ch);

            if (TKRoomManager.getInstance().getMySelf().role != 0) {
                if (chatUtils.popupIsShow()) {
                    chatUtils.setEdtInputHint();
                }
            }
            chlistAdapter.notifyDataSetChanged();

            if (chatUtils.popupIsShow()) {
                chatUtils.setTitelPrompt();
            }
        }
    }

    private void onPlayBackEnd() {
        img_play_back.setImageResource(R.drawable.btn_play_normal);
        sek_play_back.setProgress(0);
        isPlayBackPlay = false;
        isEnd = true;
    }

    long starttime;
    long endtime;
    long currenttime;

    private void onPlayBackUpdateTime(long backTimePos) {
        this.currenttime = backTimePos;
        double pos = (double) (currenttime - starttime) / (double) (endtime - starttime);
        sek_play_back.setProgress((int) (pos * 100));
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
        Date curDate = new Date(currenttime - starttime);//获取当前时间
        Date daDate = new Date(endtime - starttime);
        String strcur = formatter.format(curDate);
        String strda = formatter.format(daDate);
        txt_play_back_time.setText(strcur + "/" + strda);
    }

    private void onPlayBackDuration(long startTime, long endTime) {
        this.starttime = startTime;
        this.endtime = endTime;
    }

    private void onUpdateAttributeStream(String attributesPeerId, long pos, boolean isPlay, Map<String, Object> onUpdateAttribute) {

        this.onUpdateAttributeAttrs = onUpdateAttribute;
        this.updateAttributespeerId = attributesPeerId;

        if (onUpdateAttribute.containsKey("video") && (boolean) onUpdateAttribute.get("video")) {
            if (videofragment == null) {
                readyForPlayVideo(attributesPeerId, onUpdateAttribute);
                if (wbFragment != null) {
                    wbFragment.closeNewPptVideo();
                }

                RoomSession.isPublish = true;
                RoomSession.isPlay = false;
                TKRoomManager.isMediaPublishing = false;
                isWBMediaPlay = false;
                isMediaMute = false;

                Object objfileid = onUpdateAttribute.get("fileid");
                long fileid = -1;
                if (objfileid != null) {
                    if (objfileid instanceof String) {
                        fileid = Long.valueOf(objfileid.toString());
                    } else if (objfileid instanceof Number) {
                        fileid = ((Number) objfileid).longValue();
                    }
                }
                mediaListAdapter.setLocalfileid(fileid);
            } else {
                videofragment.controlMedia(onUpdateAttribute, pos, isPlay);
            }
        } else {
            if (sek_mp3 != null) {
                int curtime = (int) ((double) pos / (int) onUpdateAttribute.get("duration") * 100);
                sek_mp3.setProgress(curtime);
            }
            if (img_play_mp3 != null) {
                if (!isPlay) {
                    img_play_mp3.setImageResource(R.drawable.btn_pause_normal);
                    gifDrawable.start();
                } else {
                    img_play_mp3.setImageResource(R.drawable.btn_play_normal);
                    gifDrawable.stop();
                }
            }
            if (txt_mp3_time != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
                Date curDate = new Date(pos);//获取当前时间
                Date daDate = new Date((int) onUpdateAttribute.get("duration"));
                String strcur = formatter.format(curDate);
                String strda = formatter.format(daDate);
                txt_mp3_time.setText(strcur + "/" + strda);
            }
            if (txt_mp3_name != null) {
                txt_mp3_name.setText((String) onUpdateAttribute.get("filename"));
            }
        }
    }

    private void onPlayBackClearAll() {
        if (chlistAdapter != null) {
            chlistAdapter.notifyDataSetChanged();
        }
    }

    private void onShareScreenState(String peerIdScreen, int state) {

        screenFragment = ScreenFragment.getInstance();
        mediafragmentManager = getSupportFragmentManager();
        ft = mediafragmentManager.beginTransaction();
        if (state == 0) {
            if (screenFragment.isAdded()) {
                ft.remove(screenFragment);
                ft.commitAllowingStateLoss();
                LogUtils.isLogE("mxl", "screenFragment");
            }
            if (!isZoom) {
                for (int i = 0; i < videoItems.size(); i++) {
                    TKRoomManager.getInstance().playVideo(videoItems.get(i).peerid, videoItems.get(i).sf_video,
                            RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                    videoItems.get(i).sf_video.setZOrderMediaOverlay(true);
                    videoItems.get(i).sf_video.setVisibility(View.VISIBLE);
                    videoItems.get(i).bg_video_back.setVisibility(View.GONE);
                }
            }

        } else if (state == 1) {
            if (wbFragment != null) {
                wbFragment.closeNewPptVideo();
            }

            for (int i = 0; i < videoItems.size(); i++) {
                TKRoomManager.getInstance().unPlayVideo(videoItems.get(i).peerid);
                videoItems.get(i).sf_video.setZOrderMediaOverlay(false);
                videoItems.get(i).sf_video.setVisibility(View.INVISIBLE);
            }

            screenFragment.setPeerId(peerIdScreen);
            if (!screenFragment.isAdded()) {
                ft.replace(R.id.video_container, screenFragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    private void onShareFileState(String peerIdShareFile, int state) {
        movieFragment = MovieFragment.getInstance();
        mediafragmentManager = getSupportFragmentManager();
        ft = mediafragmentManager.beginTransaction();

        if (state == 0) {
            if (movieFragment.isAdded()) {
                ft.remove(movieFragment);
                ft.commitAllowingStateLoss();
            }
            if (!isZoom) {
                for (int i = 0; i < videoItems.size(); i++) {
                    TKRoomManager.getInstance().playVideo(videoItems.get(i).peerid,
                            videoItems.get(i).peerid, RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                    videoItems.get(i).sf_video.setZOrderMediaOverlay(true);
                    videoItems.get(i).sf_video.setVisibility(View.VISIBLE);
                    videoItems.get(i).bg_video_back.setVisibility(View.GONE);
                }
            }

        } else if (state == 1) {

            if (wbFragment != null) {
                wbFragment.closeNewPptVideo();
            }
            movieFragment.setShareFilePeerId(peerIdShareFile);

            for (int i = 0; i < videoItems.size(); i++) {
                TKRoomManager.getInstance().unPlayVideo(videoItems.get(i).peerid);
                videoItems.get(i).sf_video.setZOrderMediaOverlay(false);
                videoItems.get(i).sf_video.setVisibility(View.INVISIBLE);
            }

            if (!movieFragment.isAdded()) {
                ft.replace(R.id.video_container, movieFragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    private void onShareMediaState(String shareMediaPeerId, int shareMediaState, Map<String, Object> shareMediaAttrs) {

        this.mediaAttrs = shareMediaAttrs;
        this.mediaPeerId = shareMediaPeerId;

        if (shareMediaState == 0) {    //媒体结束播放
            mediaListAdapter.setLocalfileid(-1);
            mp3Duration = "00:00";

            if (shareMediaAttrs.containsKey("video")) {
                if ((boolean) shareMediaAttrs.get("video")) {
                    removeVideoFragment();
                } else {
                    lin_audio_control.setVisibility(View.INVISIBLE);
                    img_disk.setVisibility(View.INVISIBLE);
                    gifDrawable.stop();
                    img_disk.clearAnimation();
                }
            }

            ShareDoc media = WhiteBoradManager.getInstance().getCurrentMediaDoc();

            if (RoomSession.isPlay) {
                RoomSession.isPlay = false;

                if (!RoomControler.isDocumentClassification()) {
                    if (media.getFileid() == mediaListAdapter.getLocalfileid()) {
                        return;
                    }
                    mediaListAdapter.setLocalfileid(media.getFileid());
                }

                WhiteBoradManager.getInstance().setCurrentMediaDoc(media);
                String strSwfpath = media.getSwfpath();
                int pos = strSwfpath.lastIndexOf('.');
                strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
                String url = "http://" + WhiteBoradManager.getInstance().getFileServierUrl() + ":" + WhiteBoradManager.getInstance().getFileServierPort() + strSwfpath;
                TKRoomManager.isMediaPublishing = true;
                HashMap<String, Object> attrMap = new HashMap<String, Object>();
                attrMap.put("filename", media.getFilename());
                attrMap.put("fileid", media.getFileid());
                if (RoomSession.isClassBegin) {
                    TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()), "__all", attrMap);
                } else {
                    TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()),
                            TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                }
            }
            if (isWBMediaPlay) {
                TKRoomManager.isMediaPublishing = true;
                HashMap<String, Object> attrMap = new HashMap<String, Object>();
                attrMap.put("filename", "");
                attrMap.put("fileid", fileid);
                if (RoomSession.isClassBegin) {
                    TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()), "__all", attrMap);
                } else {
                    TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()),
                            TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                }
                isWBMediaPlay = false;
            }
            RoomSession.isPlay = false;

        } else if (shareMediaState == 1) {    //媒体开始播放

            if (wbFragment != null) {
                wbFragment.closeNewPptVideo();
            }

            isWBMediaPlay = false;
            isMediaMute = false;
            Object objfileid = shareMediaAttrs.get("fileid");
            long fileid = -1;
            if (objfileid != null) {
                if (objfileid instanceof String) {
                    fileid = Long.valueOf(objfileid.toString());
                } else if (objfileid instanceof Number) {
                    fileid = ((Number) objfileid).longValue();
                }
            }
            mediaListAdapter.setLocalfileid(fileid);

            if (shareMediaAttrs.containsKey("video")) {

                if ((boolean) shareMediaAttrs.get("video")) {
                    readyForPlayVideo(shareMediaPeerId, shareMediaAttrs);
                } else {
                    if (TKRoomManager.getInstance().getMySelf().role == 0) {
                        lin_audio_control.setVisibility(View.VISIBLE);
                        img_disk.setVisibility(View.VISIBLE);
                        gifDrawable.start();
                    } else {
                        lin_audio_control.setVisibility(View.INVISIBLE);
                        img_disk.setVisibility(View.VISIBLE);
                        boolean ispause = shareMediaAttrs.get("pause") == null ? false : (boolean) shareMediaAttrs.get("pause");
                        if (ispause) {
                            gifDrawable.stop();
                        } else {
                            gifDrawable.start();
                        }
                    }

                    vol = 0.5;
                    sek_voice_mp3.setProgress((int) (vol * 100));
                    int da = (int) shareMediaAttrs.get("duration");
                    SimpleDateFormat formatter = new SimpleDateFormat("mm:ss ");
                    Date daDate = new Date(da);
                    mp3Duration = formatter.format(daDate);
                    txt_mp3_time.setText("00:00" + "/" + mp3Duration);
                    if (txt_mp3_name != null) {
                        txt_mp3_name.setText((String) shareMediaAttrs.get("filename"));
                    }
                }
            }
        }
    }

    private int[] getRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.loading);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }

}
