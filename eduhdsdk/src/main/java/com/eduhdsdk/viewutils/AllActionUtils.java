package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.eduhdsdk.R;
import com.eduhdsdk.message.RoomSession;
import com.eduhdsdk.tools.FullScreenTools;
import com.eduhdsdk.tools.ScreenScale;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.ui.BasePopupWindow;
import com.talkcloud.roomsdk.TKRoomManager;


/**
 * Created by Administrator on 2018/4/24/024.
 * 操作弹窗
 */

public class AllActionUtils implements View.OnClickListener {

    private String TAG = "AllActionUtils";
    private Activity activity;
    private PopupWindow actionWindow;
    private AllPopupWindowClick all_popup_click;

    private ImageView iv_mute, iv_unmute, iv_send_gift, iv_all_recovery;
    private LinearLayout ll_mute, ll_unmute, ll_send_gift, ll_all_recovery;

    public AllActionUtils(Activity activity) {
        this.activity = activity;
    }

    public void setAllPopupWindowClick(AllActionUtils.AllPopupWindowClick all_popup_click) {
        this.all_popup_click = all_popup_click;
    }

    //判断弹框弹出时，用户的点击是在底部控件的内部还是外部
    boolean isInView = true;

    int type = 0;

    public void showAllActionView(int width, int height, final View view, final View cb_view, boolean isMute,
                                  boolean is_have_student, int type, boolean padLeft) {
        this.type = type;
        View contentView;
        if (type == 0) {
            contentView = LayoutInflater.from(activity).inflate(R.layout.layout_all_action_pop, null);
        } else {
            contentView = LayoutInflater.from(activity).inflate(R.layout.old_layout_all_action_pop, null);
        }

        ScreenScale.scaleView(contentView, "AllActionUtils");

        actionWindow = new BasePopupWindow(activity);
        actionWindow.setWidth(width);
        actionWindow.setHeight(height);

        ll_mute = (LinearLayout) contentView.findViewById(R.id.ll_mute);
        ll_unmute = (LinearLayout) contentView.findViewById(R.id.ll_unmute);
        ll_send_gift = (LinearLayout) contentView.findViewById(R.id.ll_send_gift);
        ll_all_recovery = (LinearLayout) contentView.findViewById(R.id.ll_all_recovery);

        TextView tv_popup_title = (TextView) contentView.findViewById(R.id.tv_popup_title);
        ImageView iv_popup_close = (ImageView) contentView.findViewById(R.id.iv_popup_close);

        iv_mute = (ImageView) contentView.findViewById(R.id.iv_mute);
        iv_unmute = (ImageView) contentView.findViewById(R.id.iv_unmute);
        iv_send_gift = (ImageView) contentView.findViewById(R.id.iv_send_gift);
        iv_all_recovery = (ImageView) contentView.findViewById(R.id.iv_all_recovery);

        ll_mute.setOnClickListener(this);
        ll_unmute.setOnClickListener(this);
        ll_send_gift.setOnClickListener(this);
        ll_all_recovery.setOnClickListener(this);

        if (isMute) {
            setAllMute();
        } else {
            setAllTalk();
        }

        if (!is_have_student) {
            setNoStudent();
        }

        tv_popup_title.setText(activity.getString(R.string.all_action));

        iv_popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionWindow != null) {
                    all_popup_click.all_control_window_close();
                    actionWindow.dismiss();
                }
            }
        });

        actionWindow.setContentView(contentView);
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        //这里给它设置了弹出的时间，
//        imm.toggleSoftInput(1000, InputMethodManager.HIDE_NOT_ALWAYS);
        actionWindow.setBackgroundDrawable(new BitmapDrawable());
        actionWindow.setFocusable(false);
        actionWindow.setOutsideTouchable(true);

        actionWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (all_popup_click != null) {
                    if (!isInView) {
                        all_popup_click.all_control_window_close();
                    }
                }
            }
        });

        actionWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isInView = Tools.isInView(event, cb_view);
                return false;
            }
        });

        if (RoomSession._bigroom) {
            setGifStatu();
        }

        int[] reb_wb_board = new int[2];
        view.getLocationInWindow(reb_wb_board);

        //popupwindow基于屏幕左上角位移到给定view中心的偏移量
        int x = 0 ;
        if(padLeft){
            x = Math.abs(view.getWidth() - actionWindow.getWidth()) / 2 + FullScreenTools.getStatusBarHeight(activity);
        }else {
            x = Math.abs(view.getWidth() - actionWindow.getWidth()) / 2;
        }
        int y = Math.abs(reb_wb_board[1] + view.getHeight() / 2 - actionWindow.getHeight() / 2);

        actionWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_mute) {
            if (all_popup_click != null) {
                all_popup_click.all_mute();
                dismissPopupWindow();
            }
        } else if (v.getId() == R.id.ll_unmute) {
            if (all_popup_click != null) {
                all_popup_click.all_unmute();
                dismissPopupWindow();
            }
        } else if (v.getId() == R.id.ll_send_gift) {
            if (all_popup_click != null) {
                all_popup_click.all_send_gift();
                dismissPopupWindow();
            }
        } else if (v.getId() == R.id.ll_all_recovery) {
            if (all_popup_click != null) {
                all_popup_click.all_recovery();
                dismissPopupWindow();
            }
        }
    }

    public void dismissPopupWindow() {
        if (actionWindow != null) {
            actionWindow.dismiss();
        }
    }

    public void setGifStatu() {
        if (actionWindow != null && iv_send_gift != null) {
            iv_send_gift.setImageResource(R.drawable.old_button_reward);
            ll_send_gift.setClickable(false);
        }
    }

    /**
     * 全体静音
     */
    public void setAllMute() {
        if (iv_mute != null && iv_unmute != null) {
            ll_mute.setClickable(false);
            ll_unmute.setClickable(true);
            ll_all_recovery.setClickable(true);
            ll_send_gift.setClickable(true);
            if (this.type == 0) {
                iv_all_recovery.setImageResource(R.drawable.button_restore);
                iv_send_gift.setImageResource(R.drawable.button_reward);
                iv_mute.setImageResource(R.drawable.button_jingyin_unclickable);
                iv_unmute.setImageResource(R.drawable.button_talk_all);
            } else {
                iv_all_recovery.setImageResource(R.drawable.old_button_restore);
                iv_send_gift.setImageResource(R.drawable.old_button_reward);
                iv_mute.setImageResource(R.drawable.old_button_jingyin_unclickable);
                iv_unmute.setImageResource(R.drawable.old_button_talk_all);
            }
        }
    }

    /**
     * 全体发言
     */
    public void setAllTalk() {
        if (iv_mute != null && iv_unmute != null) {
            ll_unmute.setClickable(false);
            ll_mute.setClickable(true);
            ll_all_recovery.setClickable(true);
            ll_send_gift.setClickable(true);
            if (this.type == 0) {
                iv_unmute.setImageResource(R.drawable.button_talk_all_unclickable);
                iv_mute.setImageResource(R.drawable.button_jingyin);
                iv_all_recovery.setImageResource(R.drawable.button_restore);
                iv_send_gift.setImageResource(R.drawable.button_reward);
            } else {
                iv_unmute.setImageResource(R.drawable.old_button_talk_all_unclickable);
                iv_mute.setImageResource(R.drawable.old_button_jingyin);
                iv_all_recovery.setImageResource(R.drawable.old_button_restore);
                iv_send_gift.setImageResource(R.drawable.old_button_reward);
            }
        }
    }

    /**
     * 台上没有学生
     */
    public void setNoStudent() {
        if (iv_mute != null && iv_unmute != null) {
            ll_all_recovery.setClickable(false);
            ll_mute.setClickable(false);
            ll_send_gift.setClickable(false);
            ll_unmute.setClickable(false);
            if (this.type == 0) {
                iv_mute.setImageResource(R.drawable.button_jingyin_unclickable);
                iv_unmute.setImageResource(R.drawable.button_talk_all_unclickable);
                iv_all_recovery.setImageResource(R.drawable.button_restore_unclickable);
                iv_send_gift.setImageResource(R.drawable.button_reward_unclickable);
            } else {
                iv_mute.setImageResource(R.drawable.old_button_jingyin_unclickable);
                iv_unmute.setImageResource(R.drawable.old_button_talk_all_unclickable);
                iv_all_recovery.setImageResource(R.drawable.old_button_restore_unclickable);
                iv_send_gift.setImageResource(R.drawable.old_button_reward_unclickable);
            }
        }
    }

    /**
     * 定义popupwindow的接口，通过接口和activity进行通信
     */
    public interface AllPopupWindowClick {
        void all_mute();

        void all_unmute();

        void all_send_gift();

        void all_recovery();

        void all_control_window_close();
    }
}
