package com.eduhdsdk.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
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
import com.eduhdsdk.viewutils.ChatWindowPop;
import com.eduhdsdk.viewutils.CoursePopupWindowUtils;
import com.eduhdsdk.viewutils.MemberListPopupWindowUtils;
import com.eduhdsdk.viewutils.PageNumberPopupWindowUtils;
import com.eduhdsdk.viewutils.PlaySpeedPopupWindowUtils;
import com.eduhdsdk.viewutils.SendGiftPopUtils;
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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class OneToOneActivity extends FragmentActivity implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener, WBStateCallBack, CompoundButton.OnCheckedChangeListener,
        CoursePopupWindowUtils.PopupWindowClick, ChatWindowPop.ChatPopupWindowClick, PageNumberPopupWindowUtils.PageNumberClick, SendGiftPopUtils.SendGift, MemberListPopupWindowUtils.CloseMemberListWindow {

    //视频
    private LinearLayout vdi_teacher, vdi_stu_in_sd;
    private VideoItem teacherItem, stu_in_sd;
    private FrameLayout video_container;

    //顶部工具条
    private RelativeLayout rel_tool_bar, re_top_right;
    private RelativeLayout rl_back;
    private TextView txt_room_name, txt_class_begin;
    private TextView txt_hour, txt_min, txt_ss;
    private TextView txt_mao_01, txt_mao_02, txt_hand_up;
    private LinearLayout lin_time, ll_top;

    //加载框
    private RelativeLayout re_loading;
    private ImageView loadingImageView;
    private FrameAnimation frameAnimation;
    private TextView tv_load;

    //底部菜单
    private RelativeLayout re_menu, rl_choose_photo, rl_message, re_bottom, ll_member_list;
    private CheckBox cb_file_person_media_list, cb_choose_mouse, cb_tool_case, cb_message;
    private CheckBox cb_choose_photo, cb_large_or_small, cb_remark, cb_control, cb_member_list;
    private TextView tv_choose_number, tv_no_read_message_number;
    private LinearLayout ll_control, ll_choice_number, ll_to_left, ll_to_right, ll_bottom_right,
            ll_small_white_board, ll_large_white_board, ll_tool_case, ll_file_list;
    private ImageView iv_page_choose_icon, iv_to_left, iv_to_right, iv_small_white_board, iv_large_white_board, iv_hand;

    //回放使用的控件
    private LinearLayout rel_play_back, ll_voice;
    private ImageView img_play_back, img_back_play_voice;
    private SeekBar sek_play_back, sek_back_play_voice;
    private TextView txt_play_back_time, tv_play_speed;
    private RelativeLayout rel_pla_back_bar, rl_play_back;

    //MP3  mp4
    private LinearLayout lin_audio_control;
    private ImageView img_play_mp3, img_voice_mp3;
    private TextView txt_mp3_name, txt_mp3_time;
    private SeekBar sek_mp3, sek_voice_mp3;
    private LinearLayout ll_close_mp3;
    private GifImageView img_disk;
    private GifDrawable gifDrawable;

    private MemberListAdapter memberListAdapter;
    private FileListAdapter fileListAdapter;
    private MediaListAdapter mediaListAdapter;
    private ChatListAdapter chlistAdapter;
    public static boolean noHaveVideo = false;
    private PowerManager pm;
    private PowerManager.WakeLock mWakeLock;
    private CoursePopupWindowUtils coursePopupWindowUtils;
    private MemberListPopupWindowUtils memberListPopupWindowUtils;
    private ChatWindowPop chatUtils;
    private PageNumberPopupWindowUtils pageNumberPopupWindowUtils;
    private PlaySpeedPopupWindowUtils playSpeedPopupWindowUtils;
    private SendGiftPopUtils sendGiftPopUtils;

    private Animation operatingAnim;
    private LinearLayout lin_menu;
    private RelativeLayout rel_parent;
    private RelativeLayout rel_wb_container;
    private FragmentManager fragmentManager;
    private FragmentTransaction ft;
    private VideoFragment videofragment;
    private WBFragment wbFragment;

    private final int REQUEST_CODED = 3;
    private boolean canClassDissMiss = false;
    private boolean isZoom = false;
    private ArrayList<TimeMessage> timeMessages = new ArrayList<TimeMessage>();
    private boolean isBackApp = false;

    private AudioManager audioManager;
    private String mp3Duration = "00:00";
    private double vol = 0.5;
    private static boolean isMediaMute = false;
    private boolean isPlayBackPlay = true;
    private boolean isEnd = false;

    private ScreenFragment screenFragment;
    private MovieFragment movieFragment;
    private PopupWindow popupWindowPhoto;

    private boolean isPauseLocalVideo = false;
    private boolean isOpenCamera = false;
    private NotificationManager nm = null;
    private NotificationCompat.Builder mBuilder = null;
    private PopupWindow studentPopupWindow;
    private Map<String, Object> mediaAttrs;
    private String mediaPeerId;
    private Map<String, Object> onUpdateAttributeAttrs;
    private String updateAttributespeerId;
    private boolean huawei, oppo, voio;
    private RelativeLayout gifview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_one, null);
        ScreenScale.scaleView(view, "OneToOneActivity  ----    onCreate");

        setContentView(view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        gifview = (RelativeLayout) findViewById(R.id.mainLayout);

       /* getExData();*/

        initTimeMessage();
        resultActivityBackApp();
        initVideoItem();
        initMediaView();

        coursePopupWindowUtils = new CoursePopupWindowUtils(this, RoomSession.memberList, RoomSession.serial, RoomSession.serial);
        coursePopupWindowUtils.setPopupWindowClick(this);

        memberListPopupWindowUtils = new MemberListPopupWindowUtils(this, RoomSession.memberList);
        memberListPopupWindowUtils.setPopupWindowClick(this);


        chatUtils = new ChatWindowPop(this, RoomSession.chatList, null);
        chatUtils.setChatPopupWindowClick(this);
        pageNumberPopupWindowUtils = new PageNumberPopupWindowUtils(this);
        pageNumberPopupWindowUtils.setPageNumberClick(this);
        playSpeedPopupWindowUtils = new PlaySpeedPopupWindowUtils(this);
        sendGiftPopUtils = new SendGiftPopUtils(this);
        sendGiftPopUtils.preLoadImage();
        sendGiftPopUtils.setOnSendGiftClick(this);

        //顶部工具条
        rel_tool_bar = (RelativeLayout) findViewById(R.id.rel_tool_bar);
        rl_back = (RelativeLayout) rel_tool_bar.findViewById(R.id.rl_back);
        txt_room_name = (TextView) rel_tool_bar.findViewById(R.id.txt_pad_name);
        txt_class_begin = (TextView) rel_tool_bar.findViewById(R.id.txt_class_begin);
        lin_time = (LinearLayout) rel_tool_bar.findViewById(R.id.lin_time);
        txt_mao_01 = (TextView) rel_tool_bar.findViewById(R.id.txt_mao_01);
        txt_mao_02 = (TextView) rel_tool_bar.findViewById(R.id.txt_mao_02);
        txt_hour = (TextView) rel_tool_bar.findViewById(R.id.txt_hour);
        txt_min = (TextView) rel_tool_bar.findViewById(R.id.txt_min);
        txt_ss = (TextView) rel_tool_bar.findViewById(R.id.txt_ss);
        txt_hand_up = (TextView) rel_tool_bar.findViewById(R.id.txt_hand_up);
        ll_top = (LinearLayout) rel_tool_bar.findViewById(R.id.ll_top);
        re_top_right = (RelativeLayout) rel_tool_bar.findViewById(R.id.re_top_right);


        video_container = (FrameLayout) findViewById(R.id.video_container);

        re_loading = (RelativeLayout) findViewById(R.id.re_laoding);
        loadingImageView = (ImageView) findViewById(R.id.loadingImageView);
        tv_load = (TextView) re_loading.findViewById(R.id.tv_load);
        if (frameAnimation == null) {
            frameAnimation = new FrameAnimation(loadingImageView, getRes(), 100, true, false, this);
        }

        //回放控件
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

        //底部菜单
        re_menu = (RelativeLayout) findViewById(R.id.re_menu);
        rl_choose_photo = (RelativeLayout) re_menu.findViewById(R.id.rl_choose_photo);
        cb_file_person_media_list = (CheckBox) re_menu.findViewById(R.id.cb_file_person_media_list);
        ll_tool_case = (LinearLayout) re_menu.findViewById(R.id.ll_tool_case);
        rl_message = (RelativeLayout) re_menu.findViewById(R.id.rl_message);
        ll_file_list = (LinearLayout) re_menu.findViewById(R.id.ll_file_list);
        cb_tool_case = (CheckBox) re_menu.findViewById(R.id.cb_tool_case);
        cb_message = (CheckBox) re_menu.findViewById(R.id.cb_message);
        cb_choose_photo = (CheckBox) re_menu.findViewById(R.id.cb_choose_photo);
        cb_large_or_small = (CheckBox) re_menu.findViewById(R.id.cb_large_or_small);
        cb_member_list = (CheckBox) re_menu.findViewById(R.id.cb_member_list);
        cb_remark = (CheckBox) re_menu.findViewById(R.id.cb_remark);
        iv_large_white_board = (ImageView) re_menu.findViewById(R.id.iv_large_white_board);
        iv_small_white_board = (ImageView) re_menu.findViewById(R.id.iv_small_white_board);
        ll_large_white_board = (LinearLayout) re_menu.findViewById(R.id.ll_large_white_board);
        ll_small_white_board = (LinearLayout) re_menu.findViewById(R.id.ll_small_white_board);
        iv_to_left = (ImageView) re_menu.findViewById(R.id.iv_to_left);
        iv_to_right = (ImageView) re_menu.findViewById(R.id.iv_to_right);
        ll_to_left = (LinearLayout) re_menu.findViewById(R.id.ll_to_left);
        ll_to_right = (LinearLayout) re_menu.findViewById(R.id.ll_to_right);
        re_bottom = (RelativeLayout) re_menu.findViewById(R.id.re_bottom);
        ll_bottom_right = (LinearLayout) re_menu.findViewById(R.id.ll_bottom_right);
        ll_member_list = (RelativeLayout) re_menu.findViewById(R.id.ll_member_list);
        iv_hand = (ImageView) re_menu.findViewById(R.id.iv_hand);


        ll_control = (LinearLayout) re_menu.findViewById(R.id.ll_control);
        cb_control = (CheckBox) re_menu.findViewById(R.id.cb_control);

        ll_control.setVisibility(View.GONE);

        cb_choose_mouse = (CheckBox) re_menu.findViewById(R.id.cb_choose_mouse);
        ll_choice_number = (LinearLayout) re_menu.findViewById(R.id.ll_choice_number);
        tv_no_read_message_number = (TextView) re_menu.findViewById(R.id.tv_no_read_message_number);
        iv_page_choose_icon = (ImageView) re_menu.findViewById(R.id.iv_page_choose_icon);
        tv_choose_number = (TextView) re_menu.findViewById(R.id.tv_choose_number);

        cb_file_person_media_list.setOnCheckedChangeListener(this);
        cb_tool_case.setOnCheckedChangeListener(this);
        cb_message.setOnCheckedChangeListener(this);
        cb_choose_photo.setOnCheckedChangeListener(this);
        cb_large_or_small.setOnCheckedChangeListener(this);
        cb_remark.setOnCheckedChangeListener(this);
        cb_member_list.setOnCheckedChangeListener(this);
        cb_choose_mouse.setOnCheckedChangeListener(this);
        ll_large_white_board.setOnClickListener(this);
        ll_small_white_board.setOnClickListener(this);
        ll_to_left.setOnClickListener(this);
        ll_to_right.setOnClickListener(this);
        ll_choice_number.setOnClickListener(this);
        tv_play_speed.setOnClickListener(this);
        rl_play_back.setOnClickListener(this);

        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);
        rel_parent = (RelativeLayout) findViewById(R.id.rel_parent);
        rel_wb_container = (RelativeLayout) findViewById(R.id.rel_wb_container);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 75, 0);

        RoomSession.getInstance().init(this);

        rl_back.setOnClickListener(this);
        txt_class_begin.setOnClickListener(this);

        operatingAnim = AnimationUtils.loadAnimation(OneToOneActivity.this, R.anim.disk_aim);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        fileListAdapter = coursePopupWindowUtils.getFileListAdapter();
        memberListAdapter = memberListPopupWindowUtils.getMemberListAdapter();
        mediaListAdapter = coursePopupWindowUtils.getMediaListAdapter();
        chlistAdapter = chatUtils.getChatListAdapter();

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
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
            }
        });
        img_play_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaAttrs != null) {
                    if (RoomSession.isPublish) {
                        TKRoomManager.getInstance().playMedia((Boolean) mediaAttrs.get("pause") == null ?
                                false : (Boolean) mediaAttrs.get("pause"));
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
                    OneToOneActivity.this.vol = vol;
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

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    this.progress = progress;
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
                        currenttime = starttime;
                        TKRoomManager.getInstance().playPlayback();
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
    }

    @Override
    protected void onStart() {
        if (TKRoomManager.getInstance().getMySelf() != null) {
            if (nm != null) {
                nm.cancel(2);
            }
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
        RoomSession.getInstance().onStart();

        if (RoomSession.isInRoom) {
            initViewByRoomTypeAndTeacher();
        }

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

    private void initVideoItem() {
        //老师视频
        vdi_teacher = (LinearLayout) findViewById(R.id.vdi_teacher);
        teacherItem = new VideoItem();
        teacherItem.parent = vdi_teacher;
        teacherItem.sf_video = (SurfaceViewRenderer) vdi_teacher.findViewById(R.id.sf_video);
        teacherItem.sf_video.init(EglBase.create().getEglBaseContext(), null);
        teacherItem.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        teacherItem.img_mic = (ImageView) vdi_teacher.findViewById(R.id.img_mic);
        teacherItem.img_pen = (ImageView) vdi_teacher.findViewById(R.id.img_pen);
        teacherItem.img_hand = (ImageView) vdi_teacher.findViewById(R.id.img_hand_up);
        teacherItem.txt_name = (AutoFitTextView) vdi_teacher.findViewById(R.id.txt_name);
        teacherItem.txt_gift_num = (AutoFitTextView) vdi_teacher.findViewById(R.id.txt_gift_num);
        teacherItem.rel_group = (RelativeLayout) vdi_teacher.findViewById(R.id.rel_group);
        teacherItem.img_video_back = (ImageView) vdi_teacher.findViewById(R.id.img_video_back);
        teacherItem.lin_gift = (LinearLayout) vdi_teacher.findViewById(R.id.lin_gift);
        teacherItem.icon_gif = (ImageView) vdi_teacher.findViewById(R.id.icon_gif);
        teacherItem.lin_name_label = (LinearLayout) vdi_teacher.findViewById(R.id.lin_name_label);
        teacherItem.rel_video_label = (RelativeLayout) vdi_teacher.findViewById(R.id.rel_video_label);
        teacherItem.re_background = (RelativeLayout) vdi_teacher.findViewById(R.id.re_background);
        teacherItem.tv_home = (TextView) vdi_teacher.findViewById(R.id.tv_home);
        teacherItem.re_bg = (RelativeLayout) vdi_teacher.findViewById(R.id.re_bg);
        teacherItem.lin_gift.setVisibility(View.INVISIBLE);
        teacherItem.img_pen.setVisibility(View.INVISIBLE);
        teacherItem.bg_video_back = (ImageView) vdi_teacher.findViewById(R.id.bg_video_back);

        //学生视频
        vdi_stu_in_sd = (LinearLayout) findViewById(R.id.vdi_stu_in_sd);
        stu_in_sd = new VideoItem();
        stu_in_sd.parent = vdi_stu_in_sd;
        stu_in_sd.sf_video = (SurfaceViewRenderer) vdi_stu_in_sd.findViewById(R.id.sf_video);
        stu_in_sd.sf_video.init(EglBase.create().getEglBaseContext(), null);
        stu_in_sd.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        stu_in_sd.img_mic = (ImageView) vdi_stu_in_sd.findViewById(R.id.img_mic);
        stu_in_sd.img_pen = (ImageView) vdi_stu_in_sd.findViewById(R.id.img_pen);
        stu_in_sd.img_hand = (ImageView) vdi_stu_in_sd.findViewById(R.id.img_hand_up);
        stu_in_sd.txt_name = (AutoFitTextView) vdi_stu_in_sd.findViewById(R.id.txt_name);
        stu_in_sd.txt_gift_num = (AutoFitTextView) vdi_stu_in_sd.findViewById(R.id.txt_gift_num);
        stu_in_sd.rel_group = (RelativeLayout) vdi_stu_in_sd.findViewById(R.id.rel_group);
        stu_in_sd.img_video_back = (ImageView) vdi_stu_in_sd.findViewById(R.id.img_video_back);
        stu_in_sd.lin_gift = (LinearLayout) vdi_stu_in_sd.findViewById(R.id.lin_gift);
        stu_in_sd.icon_gif = (ImageView) vdi_stu_in_sd.findViewById(R.id.icon_gif);
        stu_in_sd.lin_name_label = (LinearLayout) vdi_stu_in_sd.findViewById(R.id.lin_name_label);
        stu_in_sd.rel_video_label = (RelativeLayout) vdi_stu_in_sd.findViewById(R.id.rel_video_label);
        stu_in_sd.re_background = (RelativeLayout) vdi_stu_in_sd.findViewById(R.id.re_background);
        stu_in_sd.tv_home = (TextView) vdi_stu_in_sd.findViewById(R.id.tv_home);
        stu_in_sd.re_bg = (RelativeLayout) vdi_stu_in_sd.findViewById(R.id.re_bg);
        stu_in_sd.bg_video_back = (ImageView) vdi_stu_in_sd.findViewById(R.id.bg_video_back);
    }

    private void initViewByRoomTypeAndTeacher() {

        if (RoomSession.roomType == 0) {       //一对一
            ll_control.setVisibility(View.GONE);
            if (TKRoomManager.getInstance().getMySelf().role == 0) {//老师
                ll_file_list.setVisibility(View.VISIBLE);
                rl_choose_photo.setVisibility(View.GONE);
                if (RoomSession.isClassBegin) {
                    ll_tool_case.setVisibility(View.VISIBLE);
                    cb_choose_mouse.setVisibility(View.VISIBLE);
                } else {
                    ll_tool_case.setVisibility(View.GONE);
                    cb_choose_mouse.setVisibility(View.GONE);
                }
                if (!RoomControler.isShowClassBeginButton()) {
                    txt_class_begin.setVisibility(View.VISIBLE);
                }
            } else if (TKRoomManager.getInstance().getMySelf().role == 2) {//学生
                ll_file_list.setVisibility(View.GONE);
                ll_tool_case.setVisibility(View.GONE);
                txt_class_begin.setVisibility(View.INVISIBLE);
                ll_member_list.setVisibility(View.GONE);

                txt_hand_up.setVisibility(View.INVISIBLE);

            } else if (TKRoomManager.getInstance().getMySelf().role == 4) {  //巡课
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

            } else if (TKRoomManager.getInstance().getMySelf().role == -1) {  //回放
                rel_play_back.setVisibility(View.VISIBLE);
                lin_time.setVisibility(View.GONE);
                ll_voice.setVisibility(View.GONE);

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

        RoomUser roomUser = TKRoomManager.getInstance().getUser(peerId);
        if (roomUser.role == 0) {
            teacherItem.rel_group.setVisibility(View.INVISIBLE);
        }

        stu_in_sd.lin_gift.setVisibility(View.INVISIBLE);

        for (int i = 0; i < RoomSession.playingList.size(); i++) {
            final RoomUser user = RoomSession.playingList.get(i);
            if (user == null) {
                return;
            }
            if (RoomSession.roomType == 0) {  //1对1
                if (user.role == 0) {//老师
                    doTeacherVideoPlay(user);
                } else if (user.role == 2) {//学生
                    stu_in_sd.rel_group.setVisibility(View.VISIBLE);
                    stu_in_sd.lin_gift.setVisibility(View.VISIBLE);
                    stu_in_sd.txt_name.setText(user.nickName);
                    stu_in_sd.lin_name_label.setBackgroundResource(R.drawable.rounded_video_person_name);
                    stu_in_sd.re_bg.setBackgroundResource(R.drawable.video_frame_student);

                    if (user.disableaudio) {
                        stu_in_sd.img_mic.setImageResource(R.drawable.icon_audio_disable);
                        stu_in_sd.img_mic.setVisibility(View.VISIBLE);
                    } else {
                        if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                            if (RoomSession.isClassBegin) {
                                stu_in_sd.img_mic.setVisibility(View.VISIBLE);
                                stu_in_sd.img_mic.setImageResource(R.drawable.icon_audio_disable);
                            } else {
                                stu_in_sd.img_mic.setVisibility(View.GONE);
                            }
                        } else {
                            /*stu_in_sd.img_mic.setImageResource(R.drawable.icon_video_voice);*/
                            stu_in_sd.img_mic.setVisibility(View.GONE);
                        }
                    }
                    if (user.publishState > 1 && user.publishState < 4 && !user.disablevideo) {
                        if (user.hasVideo) {
                            stu_in_sd.sf_video.setVisibility(View.VISIBLE);
                            stu_in_sd.bg_video_back.setVisibility(View.GONE);
                            TKRoomManager.getInstance().playVideo(user.peerId, stu_in_sd.sf_video,
                                    RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                        } else {
                            stu_in_sd.sf_video.setVisibility(View.INVISIBLE);
                            stu_in_sd.img_video_back.setImageResource(R.drawable.icon_camera_close);
                            stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
                        }
                    } else {
                        stu_in_sd.sf_video.setVisibility(View.INVISIBLE);
                        if (user.hasVideo) {
                            stu_in_sd.img_video_back.setImageResource(R.drawable.icon_camera_close);
                            stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
                        } else {
                            stu_in_sd.img_video_back.setImageResource(R.drawable.icon_no_camera);
                            stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
    }

    private void doUnPlayVideo(RoomUser user) {
        if (RoomSession.roomType == 0) {
            if (user.role == 0) {//老师
                doTeacherVideoUnPlay(user);
            } else if (user.role == 2) {//学生
                stu_in_sd.lin_name_label.setVisibility(View.INVISIBLE);
                stu_in_sd.sf_video.setVisibility(View.INVISIBLE);
                stu_in_sd.img_mic.setVisibility(View.INVISIBLE);
                stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
            }
        }
    }

    private void doTeacherVideoPlay(final RoomUser user) {
        teacherItem.rel_group.setVisibility(View.VISIBLE);
        if (user.publishState > 1 && user.publishState < 4 && !user.disablevideo) {
            if (user.hasVideo) {
                teacherItem.sf_video.setVisibility(View.VISIBLE);
                teacherItem.bg_video_back.setVisibility(View.GONE);
                TKRoomManager.getInstance().playVideo(user.peerId, teacherItem.sf_video,
                        RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
            } else {
                teacherItem.sf_video.setVisibility(View.INVISIBLE);
                teacherItem.img_video_back.setImageResource(R.drawable.icon_camera_close);
                teacherItem.bg_video_back.setVisibility(View.VISIBLE);
            }
        } else {
            teacherItem.sf_video.setVisibility(View.INVISIBLE);
            teacherItem.img_video_back.setImageResource(R.drawable.icon_camera_close);
            teacherItem.bg_video_back.setVisibility(View.VISIBLE);
        }
        teacherItem.txt_name.setText(user.nickName);
        teacherItem.lin_name_label.setBackgroundResource(R.drawable.rounded_video_person_name_teacher);
        teacherItem.re_bg.setBackgroundResource(R.drawable.video_frame_teacher);
        vdi_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeacherControlPop(user);
            }
        });
    }

    private void doTeacherVideoUnPlay(final RoomUser user) {
        teacherItem.sf_video.setVisibility(View.INVISIBLE);
        teacherItem.img_mic.setVisibility(View.INVISIBLE);
        teacherItem.bg_video_back.setVisibility(View.VISIBLE);
        teacherItem.img_pen.setVisibility(View.INVISIBLE);
    }

    boolean is_show_student_window = true;

    private void showStudentControlPop(final RoomUser user) {

        if (!is_show_student_window) {
            is_show_student_window = true;
            return;
        }

        if (studentPopupWindow != null && studentPopupWindow.isShowing()) {
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
        if (!RoomSession.isClassBegin) {
            if (!RoomControler.isReleasedBeforeClass()) {
                return;
            }
        }
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_student_control_one_to_one, null);
//        ScreenScale.scaleView(contentView);
        LinearLayout lin_candraw = (LinearLayout) contentView.findViewById(R.id.lin_candraw);
        LinearLayout lin_audio = (LinearLayout) contentView.findViewById(R.id.lin_audio);
        LinearLayout lin_gift = (LinearLayout) contentView.findViewById(R.id.lin_gift);

        final ImageView img_candraw = (ImageView) contentView.findViewById(R.id.img_candraw);
        final ImageView img_audio = (ImageView) contentView.findViewById(R.id.img_audio);
        final TextView txt_candraw = (TextView) contentView.findViewById(R.id.txt_candraw);
        final TextView txt_audio = (TextView) contentView.findViewById(R.id.txt_audio);
        ImageView right_arr = (ImageView) contentView.findViewById(R.id.right_arr);
        right_arr.setVisibility(View.VISIBLE);


        if (TKRoomManager.getInstance().getMySelf().role == 2) {
            lin_candraw.setVisibility(View.GONE);
        }
        LinearLayout lin_video_control = (LinearLayout) contentView.findViewById(R.id.lin_video_control);
        final ImageView img_video_control = (ImageView) contentView.findViewById(R.id.img_camera);
        final TextView txt_video = (TextView) contentView.findViewById(R.id.txt_camera);
        if (user.role == 1) {
            lin_gift.setVisibility(View.GONE);
        } else {
            if (user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                lin_gift.setVisibility(View.GONE);
            } else {
                lin_gift.setVisibility(View.VISIBLE);
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
            if (candraw) {
                img_candraw.setImageResource(R.drawable.icon_control_tools_02);
                txt_candraw.setText(R.string.no_candraw);
            } else {
                img_candraw.setImageResource(R.drawable.icon_control_tools_01);
                txt_candraw.setText(R.string.candraw);
            }
        } else {
            img_candraw.setImageResource(R.drawable.icon_control_tools_01);
            txt_candraw.setText(R.string.candraw);
        }

//        studentPopupWindow = new PopupWindow(160, ScreenScale.getScreenHeight() / 12 * 8);
        studentPopupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToOneActivity.this, 80f), KeyBoardUtil.dp2px(OneToOneActivity.this, 260f));
        studentPopupWindow.setContentView(contentView);
        studentPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                is_show_student_window = !Tools.isInView(event, stu_in_sd.rel_group);
                return false;
            }
        });

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
                    if (candraw) {   //不可以画图
                        img_candraw.setImageResource(R.drawable.icon_control_tools_01);
                        txt_candraw.setText(R.string.candraw);
                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                    } else {  //可以画图
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
                    sendGiftPopUtils.showSendGiftPop(rel_wb_container.getWidth() / 10 * 5, rel_wb_container.getHeight() / 10 * 5, rel_wb_container, receiverMap, 0);
                } else {
                    //默认奖杯
                    RoomSession.getInstance().sendGift(receiverMap, null);
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
        studentPopupWindow.setFocusable(false);
        studentPopupWindow.setOutsideTouchable(true);
        studentPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        if (RoomSession.roomType == 0) {
           /* studentPopupWindow.showAsDropDown(vdi_stu_in_sd, -KeyBoardUtil.px2dp(this,700),
                    -vdi_stu_in_sd.getMeasuredHeight() / 2 - vdi_stu_in_sd.getMeasuredHeight() / 3);*/
//            studentPopupWindow.showAsDropDown(vdi_stu_in_sd, -vdi_stu_in_sd.getMeasuredWidth(),
//                    -vdi_stu_in_sd.getMeasuredHeight() / 2 - vdi_stu_in_sd.getMeasuredHeight() / 3);
            studentPopupWindow.showAsDropDown(vdi_stu_in_sd, -(studentPopupWindow.getWidth()), -(vdi_stu_in_sd.getMeasuredHeight() + studentPopupWindow.getHeight()) / 2, Gravity.CENTER_VERTICAL);
        }
    }

    PopupWindow popupWindow;

    boolean is_show_teacher_window = true;

    private void showTeacherControlPop(final RoomUser user) {

        if (!is_show_teacher_window) {
            is_show_teacher_window = true;
            return;
        }

        if (popupWindow != null && popupWindow.isShowing()) {
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

        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_av_control, null);
        LinearLayout lin_video_control = (LinearLayout) contentView.findViewById(R.id.lin_video_control);
        LinearLayout lin_audio_control = (LinearLayout) contentView.findViewById(R.id.lin_audio_control);
        LinearLayout lin_plit_screen = (LinearLayout) contentView.findViewById(R.id.lin_plit_screen);
        final ImageView img_video_control = (ImageView) contentView.findViewById(R.id.img_camera);
        final ImageView img_audio_control = (ImageView) contentView.findViewById(R.id.img_audio);
        final TextView txt_video = (TextView) contentView.findViewById(R.id.txt_camera);
        final TextView txt_audio = (TextView) contentView.findViewById(R.id.txt_audio);
        ImageView right_arr = (ImageView) contentView.findViewById(R.id.right_arr);
        right_arr.setVisibility(View.VISIBLE);
        lin_plit_screen.setVisibility(View.GONE);

        popupWindow = new PopupWindow(KeyBoardUtil.dp2px(OneToOneActivity.this, 80), KeyBoardUtil.dp2px(OneToOneActivity.this, 115));
        popupWindow.setContentView(contentView);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                is_show_teacher_window = !Tools.isInView(event, vdi_teacher);
                return false;
            }
        });

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

        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        popupWindow.showAsDropDown(vdi_teacher, -vdi_teacher.getMeasuredWidth(),
//                -vdi_teacher.getMeasuredHeight() / 2 - vdi_teacher.getMeasuredHeight() / 3);

        popupWindow.showAsDropDown(vdi_teacher, -(popupWindow.getWidth()), -(vdi_teacher.getMeasuredHeight() + popupWindow.getHeight()) / 2, Gravity.CENTER_VERTICAL);
    }

    private void getExData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        RoomSession.host = bundle.getString("host");
        RoomSession.port = bundle.getInt("port");
        RoomSession.nickname = bundle.getString("nickname");

        if (bundle.containsKey("param")) {
            RoomSession.param = bundle.getString("param");
        } else {
            RoomSession.serial = bundle.getString("serial");
            RoomSession.password = bundle.getString("password");
            RoomSession.userid = bundle.getString("userid");
            RoomSession.userrole = bundle.getInt("userrole");
        }
        RoomSession.domain = bundle.getString("domain");
        if (bundle.containsKey("path")) {
            RoomSession.path = bundle.getString("path");
        }
        if (bundle.containsKey("type")) {
            RoomSession.type = bundle.getInt("type");
        }
        if (bundle.containsKey("servername")) {
            RoomSession.servername = bundle.getString("servername");
        }

        try {
            String brand = Build.MODEL;
            RoomSession.mobilename = bundle.getString("mobilename");
            if (RoomSession.mobilename != null && !RoomSession.mobilename.isEmpty()) {
                JSONArray mNames = new JSONArray(RoomSession.mobilename);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_back || id == R.id.rl_play_back) {
            showExitDialog();
        } else if (id == R.id.txt_class_begin) {
            if (RoomSession.isClassBegin) {
                try {
                    if (canClassDissMiss || !TKRoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                        txt_class_begin.setSelected(false);
                        showClassDissMissDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (RoomControler.haveTimeQuitClassroomAfterClass()) {
                    RoomSession.getInstance().getSystemTime();
                } else {
                    txt_class_begin.setSelected(true);
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
        } else if (id == R.id.ll_choice_number) { //选择页数
            if (TKRoomManager.getInstance().getMySelf().canDraw) {
                pageNumberPopupWindowUtils.showPageNumberPopupWindow(ll_choice_number, 100, iv_page_choose_icon, total_page_num);
            } else {
                if (TKRoomManager.getInstance().getMySelf().role == 2 && RoomControler.isStudentCanTurnPage()) {
                    pageNumberPopupWindowUtils.showPageNumberPopupWindow(ll_choice_number, 100, iv_page_choose_icon, total_page_num);
                }
            }
        } else if (id == R.id.tv_play_speed) {    //回放倍速
            playSpeedPopupWindowUtils.showPlaySpeedPopupWindow(tv_play_speed, 100, 280);
        }
    }

    private void showPhotoControlPop() {
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_photo_control, null);

        LinearLayout ll_popu_camera = (LinearLayout) contentView.findViewById(R.id.ll_popu_camera);
        LinearLayout ll_popu_selectphoto = (LinearLayout) contentView.findViewById(R.id.ll_popu_selectphoto);
        popupWindowPhoto = new PopupWindow(100, ScreenScale.getScreenHeight() / 5);
        ScreenScale.scaleView(contentView, "onetoone   showPhotoControPop");
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
                PhotoUtils.openCamera(OneToOneActivity.this);
                popupWindowPhoto.dismiss();
            }
        });
        ll_popu_selectphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBackApp = true;
                PhotoUtils.openAlbum(OneToOneActivity.this);
                popupWindowPhoto.dismiss();
            }
        });
        popupWindowPhoto.setBackgroundDrawable(new BitmapDrawable());
        popupWindowPhoto.setFocusable(false);
        popupWindowPhoto.setOutsideTouchable(false);

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
                    LogUtils.isLogD("mxl", "取消了");
                    return;
                }
                if (resultCode == RESULT_OK) {
                    LogUtils.isLogD("mxl", "拍照OK");
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
       /* me.publishState = 3;*/
        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            doTeacherVideoPlay(me);
            teacherItem.rel_group.setVisibility(View.VISIBLE);
            teacherItem.sf_video.setVisibility(View.VISIBLE);
            teacherItem.bg_video_back.setVisibility(View.GONE);
            teacherItem.re_bg.setBackgroundResource(R.drawable.video_frame_teacher);
            if (RoomControler.isReleasedBeforeClass() && me.publishState == 0) {
                TKRoomManager.getInstance().changeUserPublish(me.peerId, 3);
            } else {
                teacherItem.txt_name.setText(me.nickName);
                teacherItem.sf_video.setVisibility(View.VISIBLE);
                teacherItem.bg_video_back.setVisibility(View.GONE);
                TKRoomManager.getInstance().playVideo(me.peerId, teacherItem.sf_video,
                        RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
            }
            if (stu_in_sd.rel_group != null && !RoomSession.isClassBegin) {
                stu_in_sd.rel_group.setVisibility(View.VISIBLE);
                stu_in_sd.re_bg.setVisibility(View.VISIBLE);
                stu_in_sd.img_video_back.setImageResource(R.drawable.icon_student_one_to_one);
                stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
                stu_in_sd.sf_video.setVisibility(View.INVISIBLE);
                stu_in_sd.lin_name_label.setVisibility(View.INVISIBLE);
                stu_in_sd.img_mic.setVisibility(View.INVISIBLE);
                stu_in_sd.img_hand.setVisibility(View.INVISIBLE);
                stu_in_sd.img_pen.setVisibility(View.INVISIBLE);

            }
        } else if (TKRoomManager.getInstance().getMySelf().role == 2) {
            if (RoomSession.roomType == 0) {
                if (stu_in_sd.sf_video != null) {
                    if (!RoomSession.isClassBegin) {
                        teacherItem.rel_group.setVisibility(View.VISIBLE);
                        teacherItem.re_bg.setVisibility(View.VISIBLE);
                        teacherItem.img_video_back.setImageResource(R.drawable.icon_teacher_one_to_one);
                        teacherItem.bg_video_back.setVisibility(View.VISIBLE);
                        teacherItem.sf_video.setVisibility(View.INVISIBLE);
                        teacherItem.lin_name_label.setVisibility(View.INVISIBLE);
                        teacherItem.img_mic.setVisibility(View.INVISIBLE);
                        teacherItem.img_hand.setVisibility(View.INVISIBLE);
                        teacherItem.img_pen.setVisibility(View.INVISIBLE);
                    }
                    stu_in_sd.txt_name.setText(TKRoomManager.getInstance().getMySelf().nickName);
                    stu_in_sd.re_bg.setBackgroundResource(R.drawable.video_frame_student);
                    teacherItem.re_bg.setBackgroundResource(R.drawable.video_frame_teacher);
                    if (RoomControler.isReleasedBeforeClass()) {
                        if (me.publishState > 1 && me.publishState < 4 && !me.disablevideo) {
                            stu_in_sd.sf_video.setVisibility(View.VISIBLE);
                            stu_in_sd.bg_video_back.setVisibility(View.GONE);
                        } else {
                            stu_in_sd.sf_video.setVisibility(View.INVISIBLE);
                            stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (me.hasVideo) {
                            stu_in_sd.sf_video.setVisibility(View.VISIBLE);
                            stu_in_sd.bg_video_back.setVisibility(View.GONE);
                        } else {
                            stu_in_sd.sf_video.setVisibility(View.INVISIBLE);
                            stu_in_sd.img_video_back.setImageResource(R.drawable.icon_camera_close);
                            stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
                        }
                    }
                    if (RoomControler.isReleasedBeforeClass() && me.publishState == 0) {
                        TKRoomManager.getInstance().changeUserPublish(me.peerId, 3);
                    } else {
                        TKRoomManager.getInstance().playVideo(me.peerId, stu_in_sd.sf_video,
                                RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
                    }
                }
            }
        } else if (TKRoomManager.getInstance().getMySelf().role == 4) {
            if (!RoomSession.isClassBegin) {
                teacherItem.rel_group.setVisibility(View.INVISIBLE);
                stu_in_sd.rel_group.setVisibility(View.INVISIBLE);
                stu_in_sd.lin_gift.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void unPlaySelfAfterClassBegin() {
        if (RoomSession.roomType == 0) {
            if (TKRoomManager.getInstance().getMySelf().role == 2) {
                TKRoomManager.getInstance().unPlayVideo(TKRoomManager.getInstance().getMySelf().peerId);
                stu_in_sd.sf_video.setVisibility(View.GONE);
                stu_in_sd.lin_name_label.setVisibility(View.GONE);
                stu_in_sd.img_video_back.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setBackgroundOrReception(boolean b, RoomUser RoomUser) {
        if (RoomUser != null && RoomUser.role == 0) {
            if (b) {
                teacherItem.re_background.setVisibility(View.VISIBLE);
                teacherItem.tv_home.setText(R.string.tea_background);
            } else {
                teacherItem.re_background.setVisibility(View.GONE);
            }
        }

        if (stu_in_sd.parent != null) {
            if (stu_in_sd.peerid != null) {
                if (!stu_in_sd.peerid.isEmpty()) {
                    if (stu_in_sd.peerid.equals(RoomUser.peerId)) {
                        if (b) {
                            stu_in_sd.re_background.setVisibility(View.VISIBLE);
                            stu_in_sd.tv_home.setText(R.string.background);
                        } else {
                            stu_in_sd.re_background.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }

    private void romoveMovieFragment() {
        RoomSession.isPublish = false;
        movieFragment = MovieFragment.getInstance();
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (movieFragment.isAdded()) {
            ft.remove(movieFragment);
            ft.commitAllowingStateLoss();
        }
    }

    private void romoveScreenFragment() {
        RoomSession.isPublish = false;
        screenFragment = ScreenFragment.getInstance();
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (screenFragment.isAdded()) {
            ft.remove(screenFragment);
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void onPageFinished() {
       /* TKRoomManager.getInstance().joinPlayBackRoom(RoomSession.host, RoomSession.port,
                RoomSession.nickname, RoomSession.params, new HashMap<String, Object>(), true);*/
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
                    lin_menu.setVisibility(View.GONE);
                    lin_audio_control.setVisibility(View.GONE);
                    rel_tool_bar.setVisibility(View.GONE);
                    stu_in_sd.rel_group.setVisibility(View.GONE);
                    stu_in_sd.sf_video.setVisibility(View.GONE);
                    stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
                    teacherItem.rel_group.setVisibility(View.GONE);
                    teacherItem.sf_video.setVisibility(View.GONE);
                    teacherItem.bg_video_back.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams rel_wb_param = (LinearLayout.LayoutParams) rel_wb_container.getLayoutParams();
                    rel_wb_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    rel_wb_param.height = LinearLayout.LayoutParams.MATCH_PARENT;
                    rel_wb_container.setLayoutParams(rel_wb_param);

                } else {
                    lin_menu.setVisibility(View.VISIBLE);
                    rel_tool_bar.setVisibility(View.VISIBLE);

                    stu_in_sd.rel_group.setVisibility(View.VISIBLE);
                    teacherItem.rel_group.setVisibility(View.VISIBLE);
                    teacherItem.rel_group.setVisibility(View.VISIBLE);
                    teacherItem.sf_video.setVisibility(View.VISIBLE);
                    teacherItem.bg_video_back.setVisibility(View.GONE);
                    if (TKRoomManager.getInstance().getMySelf().role == 0 && RoomSession.isPublish && !com.classroomsdk.Tools.isMp4(WhiteBoradManager.getInstance().getCurrentMediaDoc().getFilename())) {
                        lin_audio_control.setVisibility(View.VISIBLE);
                    } else {
                        lin_audio_control.setVisibility(View.INVISIBLE);
                    }

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
                    }

                    total_page_num = totalPage;
                    currentNumber = current;
                    //设置页数
                    tv_choose_number.setText(current + "/" + totalPage);
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
     * @param jsonProperty JS改变用户属性
     */
    @Override
    public void onWhiteBoradSetProperty(String jsonProperty) {
        try {
            JSONObject json = new JSONObject(jsonProperty);
            String peerId = json.optString("id");
            String toID = json.optString("toID");
            JSONObject properties = json.optJSONObject("properties");
            if (properties != null && !TextUtils.isEmpty(properties.toString()) &&
                    !TextUtils.isEmpty(peerId) && !TextUtils.isEmpty(toID)) {
                String primaryColor = properties.optString("primaryColor");
                TKRoomManager.getInstance().changeUserProperty(peerId, toID, "primaryColor", primaryColor);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   /* public void joinRoom() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (!RoomSession.param.isEmpty())
            params.put("param", RoomSession.param);

        params.put("userid", RoomSession.userid);
        params.put("password", RoomSession.password);
        params.put("serial", RoomSession.serial);
        params.put("userrole", RoomSession.userrole);
        params.put("nickname", RoomSession.nickname);
        params.put("volume", 100);

        params.put("mobilenameOnList", RoomSession.mobilenameNotOnList);
        if (RoomSession.domain != null && !RoomSession.domain.isEmpty())
            params.put("domain", RoomSession.domain);
        if (RoomSession.servername != null && !RoomSession.servername.isEmpty())
            params.put("servername", RoomSession.servername);
        TKRoomManager.getInstance().setDeviceType("AndroidPad");

        if (RoomSession.path != null && !RoomSession.path.isEmpty()) {
            params.put("path", RoomSession.path);
            if (RoomSession.type != -1)
                params.put("type", RoomSession.type);
            TKRoomManager.getInstance().joinPlayBackRoom(RoomSession.host, RoomSession.port, RoomSession.nickname, params, new HashMap<String, Object>(), true);
        } else {
            TKRoomManager.getInstance().joinRoom(RoomSession.host, RoomSession.port, RoomSession.nickname, params, new HashMap<String, Object>(), true);
        }
    }*/

    private void checkRaiseHands() {
        int count = 0;
        for (RoomUser u : TKRoomManager.getInstance().getUsers().values()) {
            if (u.role == 2) {
                if (u.properties.containsKey("raisehand")) {
                    boolean israisehand = Tools.isTure(u.properties.get("raisehand"));
                    if (israisehand) {
                        count++;
                    }
                }
            }
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
        teacherItem.sf_video.release();
        stu_in_sd.sf_video.release();

        RoomClient.getInstance().setExit(false);
        WhiteBoradManager.getInstance().clear();
        RoomSession.getInstance().closeSpeaker();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    public void showExitDialog() {

        Tools.showDialog(OneToOneActivity.this, R.string.remind, R.string.logouts, new Tools.OnDialogClick() {
            @Override
            public void dialog_ok(Dialog dialog) {
                sendGiftPopUtils.deleteImage();
                RoomClient.getInstance().setExit(true);
                TKRoomManager.getInstance().leaveRoom();
                if (TKRoomManager.getInstance() != null) {
                    if (TKRoomManager.getInstance().getRoomStatus() == 0 || TKRoomManager.getInstance().getRoomStatus() == 3 ||
                            TKRoomManager.getInstance().getRoomStatus() == 6) {
                        finish();
                    }
                }
                dialog.dismiss();
            }
        }, 0);
    }

    public void showClassDissMissDialog() {

        Tools.showDialog(OneToOneActivity.this, R.string.remind, R.string.make_sure_class_dissmiss, new Tools.OnDialogClick() {
            @Override
            public void dialog_ok(Dialog dialog) {
                try {
                    TKRoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new JSONObject().put("recordchat", true).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                txt_class_begin.setVisibility(View.INVISIBLE);
                RoomSession.getInstance().sendClassDissToPhp();
                dialog.dismiss();
            }
        }, 0);

    }

    public void readyForPlayVideo(String shareMediaPeerId, Map<String, Object> shareMediaAttrs) {
        videofragment = VideoFragment.getInstance();
        videofragment.setStream(shareMediaPeerId, shareMediaAttrs);
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (!videofragment.isAdded()) {
            ft.replace(R.id.video_container, videofragment);
            ft.commitAllowingStateLoss();
        }
    }

    public void removeVideoFragment() {
        videofragment = VideoFragment.getInstance();
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (videofragment.isAdded()) {
            mediaListAdapter.setLocalfileid(-1);
            ft.remove(videofragment);
            ft.commitAllowingStateLoss();
            /*ft.commit();*/
        }
    }

    private void changeUserState(final RoomUser user) {
        if (user.role == 0) {
            teacherItem.img_pen.setVisibility(View.INVISIBLE);
            teacherItem.img_hand.setVisibility(View.INVISIBLE);
            teacherItem.peerid = user.peerId;

            if (user.disablevideo) {
                teacherItem.img_video_back.setImageResource(R.drawable.icon_no_camera);
                teacherItem.bg_video_back.setVisibility(View.VISIBLE);
            }

            if (user.disableaudio) {
                teacherItem.img_mic.setImageResource(R.drawable.icon_audio_disable);
                teacherItem.img_mic.setVisibility(View.VISIBLE);
            } else {
                if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                    if (RoomSession.isClassBegin) {
                        teacherItem.img_mic.setVisibility(View.VISIBLE);
                        teacherItem.img_mic.setImageResource(R.drawable.icon_audio_disable);
                    }
                } else {
                    teacherItem.img_mic.setVisibility(View.GONE);
                    /*teacherItem.img_mic.setImageResource(R.drawable.icon_video_voice);*/
                }
            }

            if (user.properties.containsKey("primaryColor")) {
                String primaryColor = (String) user.properties.get("primaryColor");
                if (RoomSession.isClassBegin) {
                    teacherItem.img_pen.setVisibility(View.VISIBLE);
                }
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
                    teacherItem.img_pen.setImageResource(res_id);
                }
            } else {
                teacherItem.img_pen.setImageResource(R.drawable.icon_white_pen);
            }
        }

        if (RoomSession.roomType == 0) {
            if (user.role == 2) {
                stu_in_sd.peerid = user.peerId;
            }
            if (!user.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && user.role == TKRoomManager.getInstance().getMySelf().role) {
                return;
            }
            if (user.role == 2) {

                if (user.publishState > 0) {
                    stu_in_sd.rel_group.setVisibility(View.VISIBLE);
                    stu_in_sd.img_mic.setVisibility(View.VISIBLE);
                    stu_in_sd.lin_name_label.setVisibility(View.VISIBLE);
                } else {
                    stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
                }

                if (user.disablevideo) {
                    stu_in_sd.img_video_back.setImageResource(R.drawable.icon_no_camera);
                    stu_in_sd.bg_video_back.setVisibility(View.VISIBLE);
                }

                if (user.disableaudio) {
                    stu_in_sd.img_mic.setImageResource(R.drawable.icon_audio_disable);
                } else {
                    if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                        if (user.publishState == 0) {
                            stu_in_sd.img_mic.setVisibility(View.GONE);
                        } else {
                            if (RoomSession.isClassBegin) {
                                stu_in_sd.img_mic.setImageResource(R.drawable.icon_audio_disable);
                            }
                        }
                    } else {
                        stu_in_sd.img_mic.setVisibility(View.GONE);
                        /*stu_in_sd.img_mic.setImageResource(R.drawable.icon_video_voice);*/
                    }
                }

                if (user.properties.containsKey("candraw")) {
                    boolean candraw = Tools.isTure(user.properties.get("candraw"));
                    if (candraw) {
                        stu_in_sd.img_pen.setVisibility(View.VISIBLE);//可以画图
                    } else {
                        stu_in_sd.img_pen.setVisibility(View.INVISIBLE);//不可以画图
                    }
                } else {
                    stu_in_sd.img_pen.setVisibility(View.INVISIBLE);//没给过画图权限
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
                        stu_in_sd.img_pen.setImageResource(res_id);
                    }
                } else {
                    stu_in_sd.img_pen.setImageResource(R.drawable.icon_white_pen);
                }

                if (user.properties.containsKey("raisehand")) {
                    boolean israisehand = Tools.isTure(user.properties.get("raisehand"));
                    if (israisehand) {
                        stu_in_sd.img_hand.setVisibility(View.VISIBLE);//正在举手
                    } else {
                        stu_in_sd.img_hand.setVisibility(View.INVISIBLE);//同意了，或者拒绝了
                    }
                } else {
                    stu_in_sd.img_hand.setVisibility(View.INVISIBLE);//还没举手
                }

                if (user.properties.containsKey("giftnumber")) {
                    long giftnumber = user.properties.get("giftnumber") instanceof Integer ? (int) user.properties.get("giftnumber") : (long) user.properties.get("giftnumber");
                    stu_in_sd.txt_gift_num.setText("X" + giftnumber);
                }

                stu_in_sd.rel_group.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showStudentControlPop(user);
                    }
                });
            }
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
        relparam.addRule(RelativeLayout.CENTER_VERTICAL);
        relparam.leftMargin = rel_wb_container.getWidth() / 2;

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
            gy = hid / 2 - iv_gif.getHeight();
        } else {
            gx = wid / 2 - img_gift.getWidth();
            gy = hid / 2 - img_gift.getHeight();
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
            smlTobigXScale = ObjectAnimator.ofFloat(iv_gif, "scaleX", 1.0f, 4.0f);
            smlTobigYScale = ObjectAnimator.ofFloat(iv_gif, "scaleY", 1.0f, 4.0f);
            bigToSmlXScale = ObjectAnimator.ofFloat(iv_gif, "scaleX", 4.0f, 0.0f);
            bigToSmlYScale = ObjectAnimator.ofFloat(iv_gif, "scaleY", 4.0f, 0.0f);
            translateX = ObjectAnimator.ofFloat(iv_gif, "translationX", 0.0f, dx);
            translateY = ObjectAnimator.ofFloat(iv_gif, "translationY", 0.0f, dy);
        } else {
            smlTobigXScale = ObjectAnimator.ofFloat(img_gift, "scaleX", 1.0f, 4.0f);
            smlTobigYScale = ObjectAnimator.ofFloat(img_gift, "scaleY", 1.0f, 4.0f);
            bigToSmlXScale = ObjectAnimator.ofFloat(img_gift, "scaleX", 4.0f, 0.0f);
            bigToSmlYScale = ObjectAnimator.ofFloat(img_gift, "scaleY", 4.0f, 0.0f);
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

    int wid_ratio = 4;
    int hid_ratio = 3;

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

        if (TKRoomManager.getInstance().get_room_video_height() != 0 && TKRoomManager.getInstance().get_room_video_width() != 0) {
            wid_ratio = TKRoomManager.getInstance().get_room_video_width();
            hid_ratio = TKRoomManager.getInstance().get_room_video_height();
        }

        //顶部工具栏
        RelativeLayout.LayoutParams tool_bar_param = (RelativeLayout.LayoutParams) rel_tool_bar.getLayoutParams();
        tool_bar_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
        tool_bar_param.height = dm.widthPixels / 7 * 3 / 4 * 4 / 10;
        rel_tool_bar.setLayoutParams(tool_bar_param);
        rel_pla_back_bar.setLayoutParams(tool_bar_param);

        //底部菜单
        RelativeLayout.LayoutParams re_menu_param = (RelativeLayout.LayoutParams) re_menu.getLayoutParams();
        re_menu_param.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        re_menu_param.height = dm.widthPixels / 7 * 3 / 4 * 4 / 10;
        re_menu.setLayoutParams(re_menu_param);

        //右边老师和学生视频大小
        LinearLayout.LayoutParams menu_param = (LinearLayout.LayoutParams) lin_menu.getLayoutParams();
        menu_param.width = dm.widthPixels / 7 * 16 / 10;
        menu_param.height = hid - dm.widthPixels / 7 * hid_ratio / wid_ratio * 4 / 10 * 2;
        lin_menu.setLayoutParams(menu_param);

        RelativeLayout.LayoutParams lin_audio_control_param = (RelativeLayout.LayoutParams) lin_audio_control.getLayoutParams();
        lin_audio_control_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lin_audio_control_param.height = tool_bar_param.height * 3 / 2;
        lin_audio_control.setLayoutParams(lin_audio_control_param);

        RelativeLayout.LayoutParams stu_param_menu = (RelativeLayout.LayoutParams) teacherItem.sf_video.getLayoutParams();
        stu_param_menu.width = menu_param.width - KeyBoardUtil.dp2px(this, 16);
        stu_param_menu.height = stu_param_menu.width * hid_ratio / wid_ratio;
        teacherItem.sf_video.setLayoutParams(stu_param_menu);
        teacherItem.rel_group.setLayoutParams(stu_param_menu);
        stu_in_sd.sf_video.setLayoutParams(stu_param_menu);
        stu_in_sd.rel_group.setLayoutParams(stu_param_menu);

        RelativeLayout.LayoutParams stu_parent_menu = (RelativeLayout.LayoutParams) teacherItem.rel_group.getLayoutParams();
        stu_parent_menu.width = menu_param.width - KeyBoardUtil.dp2px(this, 16);
        stu_parent_menu.height = stu_param_menu.width * hid_ratio / wid_ratio;
        teacherItem.rel_group.setLayoutParams(stu_parent_menu);

        LinearLayout.LayoutParams stu_video_label_menu = (LinearLayout.LayoutParams) teacherItem.rel_video_label.getLayoutParams();
        stu_video_label_menu.width = menu_param.width - KeyBoardUtil.dp2px(this, 16);
        stu_video_label_menu.height = stu_param_menu.width * hid_ratio / wid_ratio;
        teacherItem.rel_video_label.setLayoutParams(stu_video_label_menu);
        stu_in_sd.rel_video_label.setLayoutParams(stu_video_label_menu);

        RelativeLayout.LayoutParams stu_name_menu = (RelativeLayout.LayoutParams) teacherItem.lin_name_label.getLayoutParams();
        stu_name_menu.width = menu_param.width - KeyBoardUtil.dp2px(this, 16);
        stu_name_menu.height = (int) (stu_name_menu.width * ((double) 30 / (double) 240));
        stu_name_menu.bottomMargin = stu_video_label_menu.height / 12;
        stu_name_menu.addRule(RelativeLayout.CENTER_VERTICAL);
        teacherItem.lin_name_label.setLayoutParams(stu_name_menu);
        stu_in_sd.lin_name_label.setLayoutParams(stu_name_menu);

        LinearLayout.LayoutParams txt_name_par = (LinearLayout.LayoutParams) stu_in_sd.txt_name.getLayoutParams();
        txt_name_par.leftMargin = stu_param_menu.width / 10;
        /*txt_name_par.bottomMargin = stu_name_menu.height / 10;*/
        txt_name_par.gravity = Gravity.CENTER_VERTICAL;
        teacherItem.txt_name.setLayoutParams(txt_name_par);
        stu_in_sd.txt_name.setLayoutParams(txt_name_par);

        LinearLayout.LayoutParams icon_gif_par = (LinearLayout.LayoutParams) stu_in_sd.icon_gif.getLayoutParams();
        icon_gif_par.height = stu_name_menu.height / 10 * 6;
        icon_gif_par.width = stu_name_menu.height / 10 * 6;
        icon_gif_par.gravity = Gravity.CENTER_VERTICAL;
        stu_in_sd.icon_gif.setLayoutParams(icon_gif_par);

        RelativeLayout.LayoutParams img_hand_par = (RelativeLayout.LayoutParams) stu_in_sd.img_hand.getLayoutParams();
        img_hand_par.height = (int) (stu_param_menu.height / 6);
        img_hand_par.width = (int) (stu_param_menu.height / 6);
        img_hand_par.leftMargin = stu_param_menu.width / 12;
        stu_in_sd.img_hand.setLayoutParams(img_hand_par);

        RelativeLayout.LayoutParams img_mic_par = (RelativeLayout.LayoutParams) stu_in_sd.img_mic.getLayoutParams();
        img_mic_par.height = (int) (stu_param_menu.height / 7);
        img_mic_par.width = (int) (stu_param_menu.height / 7);
        img_mic_par.rightMargin = stu_param_menu.width / 16;
        stu_in_sd.img_mic.setLayoutParams(img_mic_par);
        teacherItem.img_mic.setLayoutParams(img_mic_par);

        RelativeLayout.LayoutParams img_pen_par = (RelativeLayout.LayoutParams) stu_in_sd.img_pen.getLayoutParams();
        img_pen_par.height = (int) (stu_param_menu.height / 7);
        img_pen_par.width = (int) (stu_param_menu.height / 7);
        img_pen_par.rightMargin = stu_param_menu.width / 16;
        stu_in_sd.img_pen.setLayoutParams(img_pen_par);
        teacherItem.img_pen.setLayoutParams(img_pen_par);

        LinearLayout.LayoutParams stu_par_menu = (LinearLayout.LayoutParams) teacherItem.parent.getLayoutParams();
        stu_par_menu.width = menu_param.width - KeyBoardUtil.dp2px(this, 16);
        stu_par_menu.height = stu_param_menu.height;
        stu_par_menu.gravity = Gravity.CENTER_HORIZONTAL;
        teacherItem.parent.setLayoutParams(stu_par_menu);
        stu_in_sd.parent.setLayoutParams(stu_par_menu);

       /* LinearLayout.LayoutParams lin_audio_control_param = (LinearLayout.LayoutParams) lin_audio_control.getLayoutParams();
        lin_audio_control_param.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lin_audio_control_param.height = (int) (hid * ((double) 115 / (double) disHid));
        lin_audio_control.setLayoutParams(lin_audio_control_param);*/

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

        //白板大小
        LinearLayout.LayoutParams rel_wb_param = (LinearLayout.LayoutParams) rel_wb_container.getLayoutParams();
        rel_wb_param.height = (int) (hid - (dm.widthPixels / 7 * hid_ratio / wid_ratio * 0.4) * 2);
        if (huawei || oppo || voio) {
            rel_wb_param.width = (int) (dm.widthPixels - dm.widthPixels / 7 * 1.6 - heightStatusBar);
            rel_wb_param.leftMargin = heightStatusBar;
        } else {
            rel_wb_param.width = (int) (dm.widthPixels - dm.widthPixels / 7 * 1.6);
        }
        rel_wb_container.setLayoutParams(rel_wb_param);
        if (wbFragment != null && WBSession.isPageFinish) {
            wbFragment.transmitWindowSize(rel_wb_param.width, rel_wb_param.height);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_file_person_media_list) {   //文件列表
            if (isChecked) {
                if (cb_tool_case.isChecked()) {
                    cb_tool_case.setChecked(false);
                }
                if (huawei || voio || oppo) {
                    coursePopupWindowUtils.showCoursePopupWindow(rel_wb_container, ll_file_list, rel_wb_container.getWidth() / 10 * 7,
                            rel_wb_container.getHeight() * 9 / 10, 0, true);
                } else {
                    coursePopupWindowUtils.showCoursePopupWindow(rel_wb_container, ll_file_list, rel_wb_container.getWidth() / 10 * 7,
                            rel_wb_container.getHeight() * 9 / 10, 0, false);
                }
            } else {
                coursePopupWindowUtils.dismissPopupWindow();
            }
        } else if (id == R.id.cb_member_list) {    //  花名册
            iv_hand.setVisibility(View.INVISIBLE);
            if (isChecked) {
                if (huawei || voio || oppo) {
                    memberListPopupWindowUtils.showMemberListPopupWindow(rel_wb_container, ll_member_list,
                            rel_wb_container.getWidth() / 10 * 7, rel_wb_container.getHeight() * 9 / 10, 0, true);
                } else {
                    memberListPopupWindowUtils.showMemberListPopupWindow(rel_wb_container, ll_member_list,
                            rel_wb_container.getWidth() / 10 * 7, rel_wb_container.getHeight() * 9 / 10, 0, false);
                }
                if (cb_tool_case.isChecked()) {
                    cb_tool_case.setChecked(false);
                }
            } else {
                memberListPopupWindowUtils.dismissPopupWindow();
            }

        } else if (id == R.id.cb_tool_case) {  //工具箱
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
        } else if (id == R.id.cb_message) {  //  聊天列表
            if (isChecked) {
                if (cb_tool_case.isChecked()) {
                    cb_tool_case.setChecked(false);
                }
                clearNoReadChatMessage();
                chatUtils.showChatPopupWindow(rel_wb_container.getWidth() * 8 / 10, rel_wb_container.getHeight(),
                        rel_wb_container, rl_message);
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
        } else if (id == R.id.cb_remark) {     //备注
            String action;
            if (isChecked) {
                action = "'whiteboardSDK_openDocumentRemark'";
            } else {
                action = "'whiteboardSDK_closeDocumentRemark'";
            }
            wbFragment.interactiveJS(action, null);
        } else if (id == R.id.cb_large_or_small) {     //全屏或小屏
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
        PhotoUtils.openCamera(OneToOneActivity.this);
    }

    @Override
    public void choose_photo() {
        isBackApp = true;
        PhotoUtils.openAlbum(OneToOneActivity.this);
    }

    //教室文件
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
                    if (TKRoomManager.getInstance().getRoomProperties() != null && TKRoomManager.getInstance().getRoomProperties().getLong("endtime") - RoomSession.serviceTime == 60 * 5) {
                        if (TKRoomManager.getInstance().getMySelf().role == 0 && RoomControler.haveTimeQuitClassroomAfterClass()) {
                            Toast.makeText(OneToOneActivity.this, getString(R.string.end_class_time), Toast.LENGTH_LONG).show();
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
                        proTime = endTime - RoomSession.serviceTime;
                        isstart = true;
                    }
                    if (TKRoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                        if (proTime <= 60) {
                            txt_class_begin.setSelected(false);
                            txt_class_begin.setClickable(true);
                            canClassDissMiss = true;
                        }
                        if (isstart && proTime < -5 * 60) {//自动下课
                            TKRoomManager.getInstance().delMsg("ClassBegin", "ClassBegin", "__all", new JSONObject().put("recordchat", true).toString());
                            RoomSession.getInstance().sendClassDissToPhp();
                        }

                        if (isstart && proTime <= 60 && !timeMessages.get(2).isShowed) {
                            showTimeTipPop(timeMessages.get(2));

                            txt_hour.setTextAppearance(OneToOneActivity.this, R.style.time_yel);
                            txt_min.setTextAppearance(OneToOneActivity.this, R.style.time_yel);
                            txt_ss.setTextAppearance(OneToOneActivity.this, R.style.time_yel);
                            txt_mao_01.setTextAppearance(OneToOneActivity.this, R.style.time_yel);
                            txt_mao_02.setTextAppearance(OneToOneActivity.this, R.style.time_yel);
                        }
                        if (isstart && proTime <= -3 * 60 && !timeMessages.get(3).isShowed) {
                            showTimeTipPop(timeMessages.get(3));

                            txt_hour.setTextAppearance(OneToOneActivity.this, R.style.time_red);
                            txt_min.setTextAppearance(OneToOneActivity.this, R.style.time_red);
                            txt_ss.setTextAppearance(OneToOneActivity.this, R.style.time_red);
                            txt_mao_01.setTextAppearance(OneToOneActivity.this, R.style.time_red);
                            txt_mao_02.setTextAppearance(OneToOneActivity.this, R.style.time_red);

                        }
                        if (isstart && proTime <= -5 * 60 + 10 && !timeMessages.get(4).isShowed) {
                            showTimeTipPop(timeMessages.get(4));
                            txt_hour.setTextAppearance(OneToOneActivity.this, R.style.time_red);
                            txt_min.setTextAppearance(OneToOneActivity.this, R.style.time_red);
                            txt_ss.setTextAppearance(OneToOneActivity.this, R.style.time_red);
                            txt_mao_01.setTextAppearance(OneToOneActivity.this, R.style.time_red);
                            txt_mao_02.setTextAppearance(OneToOneActivity.this, R.style.time_red);
                        }
                    } else {
                        txt_class_begin.setSelected(false);
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

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        joinRoom();
        if (grantResults.length == 0 || permissions.length == 0) {
            return;
        }

        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.CAMERA.equals(permissions[i])) {
                int grantResult = grantResults[0];
                boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
                if (!granted) {
                    noHaveVideo = true;
                    Tools.ShowAlertDialog(this, getString(R.string.camera_hint));
                    RoomClient.getInstance().warning(1);
                } else {
                    noHaveVideo = false;
                }
            }
            if (Manifest.permission.RECORD_AUDIO.equals(permissions[i])) {
                int grantResult = grantResults[0];
                boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
                if (!granted) {
                    Tools.ShowAlertDialog(this, getString(R.string.mic_hint));
                    RoomClient.getInstance().warning(2);
                }
            }
        }
    }*/

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

        doLayout();

        if (videofragment != null && onUpdateAttributeAttrs != null && onUpdateAttributeAttrs.containsKey("video")
                && (boolean) onUpdateAttributeAttrs.get("video")) {
            videofragment.setVoice();
        }
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
        Intent resultIntent = new Intent(this, OneToOneActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pIntent);
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
                        Map<String, Object> dateAttributeAttrs = (Map<String, Object>) args[3];
                        onUpdateAttributeStream(attributesPeerId, streamPos, isPlay, dateAttributeAttrs);
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

                    default:
                        break;
                }
            }
        });
    }

    private void onAudioVolume(String volumePeerId, int volume) {
        RoomUser roomUser = TKRoomManager.getInstance().getUser(volumePeerId);
        if (roomUser != null && roomUser.role == 0) {
            if (volume >= 5) {
                teacherItem.img_mic.setVisibility(View.VISIBLE);
                if (roomUser.publishState == 1 || roomUser.publishState == 3) {
                    teacherItem.img_mic.setImageResource(R.drawable.icon_video_voice);
                } else {
                    teacherItem.img_mic.setImageResource(R.drawable.icon_audio_disable);
                }
            } else {
                if (roomUser.publishState == 0 || roomUser.publishState == 1 || roomUser.publishState == 3) {
                    teacherItem.img_mic.setVisibility(View.GONE);
                } else {
                    teacherItem.img_mic.setVisibility(View.VISIBLE);
                    teacherItem.img_mic.setImageResource(R.drawable.icon_audio_disable);
                }
            }
        } else if (roomUser != null && roomUser.role == 2) {
            if (volume >= 5) {
                stu_in_sd.img_mic.setVisibility(View.VISIBLE);
                if (roomUser.publishState == 1 || roomUser.publishState == 3) {
                    stu_in_sd.img_mic.setImageResource(R.drawable.icon_video_voice);
                } else {
                    if (roomUser.publishState == 0) {
                        stu_in_sd.img_mic.setVisibility(View.GONE);
                    } else {
                        stu_in_sd.img_mic.setImageResource(R.drawable.icon_audio_disable);
                    }
                }
            } else {
                if (roomUser.publishState == 0 || roomUser.publishState == 1 || roomUser.publishState == 3) {
                    stu_in_sd.img_mic.setVisibility(View.GONE);
                } else {
                    stu_in_sd.img_mic.setImageResource(R.drawable.icon_audio_disable);
                    stu_in_sd.img_mic.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void onShareFileState(String peerIdShareFile, int stateShareFile) {
        movieFragment = MovieFragment.getInstance();
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        if (stateShareFile == 0) {
            if (movieFragment.isAdded()) {
                ft.remove(movieFragment);
                ft.commitAllowingStateLoss();
            }
        } else if (stateShareFile == 1) {
            if (wbFragment != null) {
                wbFragment.closeNewPptVideo();
            }
            movieFragment.setShareFilePeerId(peerIdShareFile);
            if (!movieFragment.isAdded()) {
                ft.replace(R.id.video_container, movieFragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    private void onShareScreenState(String peerIdScreen, int stateScreen) {

        screenFragment = ScreenFragment.getInstance();
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();

        if (stateScreen == 0) {
            if (screenFragment.isAdded()) {
                ft.remove(screenFragment);
                ft.commitAllowingStateLoss();
            }
        } else {
            if (wbFragment != null) {
                wbFragment.closeNewPptVideo();
            }
            screenFragment.setPeerId(peerIdScreen);
            if (!screenFragment.isAdded()) {
                ft.replace(R.id.video_container, screenFragment);
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
                    img_disk.clearAnimation();
                    img_disk.setVisibility(View.INVISIBLE);
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
                attrMap.put("fileid", fileid);

                if (RoomSession.isClassBegin) {
                    TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()), "__all", attrMap);
                } else {
                    TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()), TKRoomManager.getInstance().getMySelf().peerId, attrMap);
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
                    TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()), TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                }
                isWBMediaPlay = false;
            }

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
                    img_voice_mp3.setImageResource(R.drawable.icon_voice);
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

    private void onPlayBackEnd() {
        img_play_back.setImageResource(R.drawable.btn_play_normal);
        sek_play_back.setProgress(0);
        isPlayBackPlay = false;
        isEnd = true;
    }

    long starttime;
    long endtime;
    long currenttime;

    private void onPlayBackDuration(long startTime, long endTime) {
        this.starttime = startTime;
        this.endtime = endTime;
    }

    private void onPlayBackClearAll() {
        if (chlistAdapter != null) {
            chlistAdapter.notifyDataSetChanged();
        }
    }

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

    private void onUpdateAttributeStream(String attributesPeerId, long streamPos, Boolean isPlay, Map<String, Object> dateAttributeAttrs) {

        this.onUpdateAttributeAttrs = dateAttributeAttrs;
        this.updateAttributespeerId = attributesPeerId;

        if (dateAttributeAttrs.containsKey("video") && (boolean) dateAttributeAttrs.get("video")) {
            if (videofragment == null) {
                readyForPlayVideo(attributesPeerId, dateAttributeAttrs);
                if (wbFragment != null) {
                    wbFragment.closeNewPptVideo();
                }

                RoomSession.isPublish = true;
                RoomSession.isPlay = false;
                TKRoomManager.isMediaPublishing = false;
                isWBMediaPlay = false;
                isMediaMute = false;
                Object objfileid = dateAttributeAttrs.get("fileid");
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
                videofragment.controlMedia(dateAttributeAttrs, streamPos, isPlay);
            }
        } else {
            if (sek_mp3 != null) {
                int curtime = (int) ((double) streamPos / (int) dateAttributeAttrs.get("duration") * 100);
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
                Date curDate = new Date(streamPos);//获取当前时间
                Date daDate = new Date((int) dateAttributeAttrs.get("duration"));
                String strcur = formatter.format(curDate);
                String strda = formatter.format(daDate);
                txt_mp3_time.setText(strcur + "/" + strda);
            }
            if (txt_mp3_name != null) {
                txt_mp3_name.setText((String) dateAttributeAttrs.get("filename"));
            }
        }
    }

    private void onRemoteDelMsg(String nameDel, long delMsgTS) {

        if (nameDel.equals("ClassBegin")) {
            teacherItem.txt_name.setText("");
            txt_hand_up.setVisibility(View.INVISIBLE);
            cb_choose_mouse.setVisibility(View.GONE);
            ll_tool_case.setVisibility(View.GONE);

            try {
                if (!TKRoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                    if (RoomSession.userrole == 0) {
                        txt_class_begin.setText(R.string.classbegin);
                        txt_class_begin.setVisibility(View.VISIBLE);
                        txt_class_begin.setClickable(true);
                        txt_class_begin.setBackgroundResource(R.drawable.selector_class_begin_bt);
                    } else {
                        if (!RoomControler.isNotLeaveAfterClass()) {
                            sendGiftPopUtils.deleteImage();
                            RoomClient.getInstance().setExit(true);
                            TKRoomManager.getInstance().leaveRoom();
                        }
                    }
                    txt_hand_up.setText(R.string.raise);
                    lin_time.setVisibility(View.INVISIBLE);
                    txt_hour.setText("00");
                    txt_min.setText("00");
                    txt_ss.setText("00");
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

        if (nameDel.equals("EveryoneBanChat")) {

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

            if (chatUtils.popupIsShow()) {
                chatUtils.setEdtInputHint();
            }
            chlistAdapter.notifyDataSetChanged();

            if (chatUtils.popupIsShow()) {
                chatUtils.setTitelPrompt();
            }
        }
    }

    private void onRemotePubMsg(String namePub, long pubMsgTS, Object dataPub, boolean inListPub) {
        if (namePub.equals("ClassBegin")) {
            try {
                if (!TKRoomManager.getInstance().getRoomProperties().getString("companyid").equals("10035")) {
                    txt_class_begin.setText(R.string.classdismiss);
                } else {
                    txt_class_begin.setText(R.string.classdismiss);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!RoomControler.isReleasedBeforeClass()) {
                unPlaySelfAfterClassBegin();
            }

            if (TKRoomManager.getInstance().getMySelf().role == 0) {
                wbFragment.localChangeDoc();
            }

            initViewByRoomTypeAndTeacher();

            if (TKRoomManager.getInstance().getMySelf().role == 4 || TKRoomManager.getInstance().getMySelf().role == 0) {
                if (!RoomControler.isShowClassBeginButton()) {
                    txt_class_begin.setVisibility(View.VISIBLE);
                } else
                    txt_class_begin.setVisibility(View.INVISIBLE);
            } else {
                txt_class_begin.setVisibility(View.INVISIBLE);
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

        } else if (namePub.equals("UpdateTime")) {
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
                                    if (proTime <= -60 && timeMessages != null && timeMessages.size() > 1 &&
                                            !timeMessages.get(1).isShowed) {
                                        int overtime = Math.abs((int) (proTime / 60));
                                        timeMessages.get(1).message = getString(R.string.classroom_part_01) + "<font color='#FFD700'>" + overtime + "</font> " + getString(R.string.classroom_part_02);
                                        showTimeTipPop(timeMessages.get(1));
                                    }
                                    if (proTime <= 60) {
                                        txt_class_begin.setText(R.string.classbegin);
                                        txt_class_begin.setClickable(true);
                                    }
                                }
                            });
                        }
                    }, 500, 1000);
                } else if (!RoomSession.isClassBegin) {
                    if (TKRoomManager.getInstance().getMySelf().role == 0 && getfinalClassBeginMode()) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        try {
                            long expires = TKRoomManager.getInstance().getRoomProperties().getLong("endtime") + 5 * 60;
                            if (RoomControler.isNotLeaveAfterClass()) {
                                TKRoomManager.getInstance().delMsg("__AllAll", "__AllAll", "__none", new HashMap<String, Object>());
                            }
                            TKRoomManager.getInstance().pubMsg("ClassBegin", "ClassBegin", "__all", new JSONObject().put("recordchat", true).toString(), true, expires);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (namePub.equals("ShowPage")) {
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

        if (namePub.equals("StreamFailure")) {
            Map<String, Object> mapdata = null;
            if (dataPub instanceof String) {
                String str = (String) dataPub;
                try {
                    JSONObject js = new JSONObject(str);
                    mapdata = Tools.toMap(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mapdata = (Map<String, Object>) dataPub;
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

        if (namePub.equals("EveryoneBanChat")) {

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
                if (inListPub) {
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId,
                            "__all", "disablechat", true);
                }
            }
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

    private void onUserPublishState(String peerId, int state) {
        if (state > 0) {
            doPlayVideo(peerId);
        } else {
            doUnPlayVideo(TKRoomManager.getInstance().getUser(peerId));
            if (!RoomSession.isClassBegin && TKRoomManager.getInstance().getMySelf().role == 0 &&
                    TKRoomManager.getInstance().getUser(peerId).role == 0 &&
                    TKRoomManager.getInstance().getMySelf().peerId.equals(peerId)) {
                playSelfBeforeClassBegin();
            }
        }
       /* doLayout();*/
        changeUserState(TKRoomManager.getInstance().getUser(peerId));
        memberListAdapter.notifyDataSetChanged();
        if (studentPopupWindow != null) {
            studentPopupWindow.dismiss();
        }
    }

    private void onUserPropertyChanged(RoomUser propertyUser, Map<String, Object> map, String fromId) {
        if (RoomSession.playingMap.containsKey(propertyUser.peerId) && propertyUser.publishState > 0) {
            RoomSession.playingMap.put(propertyUser.peerId, propertyUser.publishState >= 1 && propertyUser.publishState <= 4);
            memberListAdapter.notifyDataSetChanged();
            if (propertyUser.publishState > 0) {
                doPlayVideo(propertyUser.peerId);
            } else {
                doUnPlayVideo(propertyUser);
            }
        }
        changeUserState(propertyUser);
        /*checkRaiseHands();*/
        if (map.containsKey("isInBackGround")) {
            if (propertyUser == null) {
                return;
            }
            boolean isinback = Tools.isTure(map.get("isInBackGround"));
            setBackgroundOrReception(isinback, propertyUser);
        }

        if (map.containsKey("disablechat")) {
            if (propertyUser == null) {
                return;
            }
            if (TKRoomManager.getInstance().getMySelf().peerId.equals(propertyUser.peerId)) {
                chatUtils.setEdtInputHint();
            }
        }

        if (RoomSession.isClassBegin) {
            if (TKRoomManager.getInstance().getMySelf().properties.containsKey("raisehand")) {
                boolean israisehand = Tools.isTure(TKRoomManager.getInstance().getMySelf().properties.get("raisehand"));
                RoomUser roomUser = TKRoomManager.getInstance().getMySelf();
                if (israisehand) {
                    iv_hand.setVisibility(View.VISIBLE);
                    txt_hand_up.setText(R.string.raiseing);
                    txt_hand_up.setBackgroundResource(R.drawable.handing_up_bg);
                    txt_hand_up.setTextAppearance(OneToOneActivity.this, R.style.white);
                } else {
                    iv_hand.setVisibility(View.INVISIBLE);
                    txt_hand_up.setText(R.string.raise); //同意了，或者拒绝了
                    txt_hand_up.setBackgroundResource(R.drawable.click_btn_xiake);
                    txt_hand_up.setTextAppearance(OneToOneActivity.this, R.style.color_hands_up);
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
                txt_hand_up.setTextAppearance(OneToOneActivity.this, R.style.color_hands_up);
            }

            if (map.containsKey("isInBackGround")) {
                if (TKRoomManager.getInstance().getMySelf().role != 2) {
                    chlistAdapter.notifyDataSetChanged();
                    memberListAdapter.notifyDataSetChanged();
                }
            }
            if (map.containsKey("giftnumber") && !propertyUser.peerId.equals(fromId)) {
                if (RoomSession.roomType == 0) {
                    showGiftAim(stu_in_sd, map);
                }
            }

            if (propertyUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("candraw")
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
                }
            }
        }

        if (propertyUser.peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) && map.containsKey("volume")) {
            Number n_volume = (Number) map.get("volume");
            int int_volume = n_volume.intValue();
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, int_volume, 0);
        }
        memberListAdapter.notifyDataSetChanged();
    }

    private void onUserLeft(RoomUser roomUser) {
        chlistAdapter.notifyDataSetChanged();
        memberListAdapter.notifyDataSetChanged();
        memberListPopupWindowUtils.setTiteNumber(RoomSession.memberList.size());
        changeUserState(roomUser);
        doUnPlayVideo(roomUser);
        if (RoomSession.roomType == 0 && roomUser != null && roomUser.role == 2) {
            stu_in_sd.txt_name.setText("");
            stu_in_sd.txt_gift_num.setText("X" + 0);
        }
    }

    private void onUserJoined(RoomUser user, boolean inList) {
        changeUserState(user);
        chlistAdapter.notifyDataSetChanged();
        memberListAdapter.notifyDataSetChanged();
        memberListPopupWindowUtils.setTiteNumber(RoomSession.memberList.size());
    }

    private void onWarning(int onWarning) {
        if (10001 == onWarning) {
            if (isOpenCamera) {
                if (10001 == onWarning) {
                    if (isOpenCamera) {
                        PhotoUtils.openCamera(OneToOneActivity.this);
                    }
                }
            }
        }
    }

    private void onError(int errorCode, String errMsg) {

        if (errorCode == 10004) {  //UDP连接不同
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.ShowAlertDialog(OneToOneActivity.this, getString(R.string.udp_alert), 0);
                }
            });

        } else if (10002 == errorCode) {

            if (frameAnimation != null) {
                frameAnimation.stopAnimation();
                re_loading.setVisibility(View.GONE);
            }

            removeVideoFragment();
            romoveScreenFragment();
            romoveMovieFragment();

            mediaListAdapter.setLocalfileid(-1);
            isWBMediaPlay = false;

            clear();
            finish();
        } else if (errorCode == 10005) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.ShowAlertDialog(OneToOneActivity.this, getString(R.string.fire_wall_alert), 0);
                    TKRoomManager.getInstance().changeUserProperty(TKRoomManager.getInstance().getMySelf().peerId, "__all", "udpstate", 2);
                }
            });
        }
    }

    private void onRoomLeave() {

        removeVideoFragment();
        romoveScreenFragment();
        romoveMovieFragment();
        mediaListAdapter.setLocalfileid(-1);
        isWBMediaPlay = false;

        if (RoomClient.getInstance().isExit()) {
            clear();
            finish();
        } else {

            re_loading.setVisibility(View.VISIBLE);
            if (frameAnimation != null) {
                frameAnimation.playAnimation();
                tv_load.setText(getString(R.string.connected));
            }

            if (popupWindowPhoto != null) {
                popupWindowPhoto.dismiss();
            }
        }
        if (img_disk != null) {
            img_disk.clearAnimation();
            img_disk.setVisibility(View.INVISIBLE);
        }
        lin_audio_control.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
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
//            rel_wb_container.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    showPlayBackControlView();
//                    return true;
//                }
//            });

            rel_parent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    showPlayBackControlView();
                    return true;
                }
            });


            rel_parent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }

        if (!PermissionTest.cameraIsCanUse()) {
            Tools.showDialog(this, R.string.remind, R.string.camera_hint, new Tools.OnDialogClick() {
                @Override
                public void dialog_ok(Dialog dialog) {
                    dialog.dismiss();
                    Tools.getAppDetailSettingIntent(OneToOneActivity.this);
                }
            }, 0);
        }
        if (PermissionTest.getRecordState() == -2) {
            Tools.showDialog(this, R.string.remind, R.string.mic_hint, new Tools.OnDialogClick() {
                @Override
                public void dialog_ok(Dialog dialog) {
                    dialog.dismiss();
                    Tools.getAppDetailSettingIntent(OneToOneActivity.this);
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
