package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.classroomsdk.WhiteBoradManager;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.ChoseNumberAdapter;
import com.eduhdsdk.adapter.FaceGVAdapter;
import com.eduhdsdk.adapter.MemberListAdapter;
import com.eduhdsdk.adapter.OldMemberListAdapter;
import com.eduhdsdk.message.RoomSession;
import com.eduhdsdk.tools.FullScreenTools;
import com.eduhdsdk.tools.KeyBoardUtil;
import com.eduhdsdk.tools.ScreenScale;
import com.eduhdsdk.tools.SoftKeyBoardListener;
import com.eduhdsdk.tools.Tools;
import com.talkcloud.roomsdk.RoomUser;
import com.talkcloud.roomsdk.TKRoomManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2018/4/20.
 * 花名册
 */

public class MemberListPopupWindowUtils implements View.OnClickListener {

    private String TAG = "CoursePopupWindowUtils";

    private Activity activity;
    private PopupWindow popupWindowMemberList, munber_input_popup;
    private ArrayList<RoomUser> memberList;
    private MemberListAdapter memberListAdapter;
    private OldMemberListAdapter oldMemberListAdapter;
    private TextView tv_popup_title;

    private ListView lv_student_name_data;
    private LinearLayout ll_choice_number;
    private EditText edt_input_munber;
    private TextView et_number, tv_number_total;
    private CloseMemberListWindow popup_click;
    private ImageView im_to_left, im_to_right;
    private int postion = 0;

    public MemberListPopupWindowUtils(Activity activity, ArrayList<RoomUser> memberList) {
        this.activity = activity;
        this.memberList = memberList;
        memberListAdapter = new MemberListAdapter(activity, memberList);
        oldMemberListAdapter = new OldMemberListAdapter(activity, memberList);
    }

    public MemberListAdapter getMemberListAdapter() {
        return this.memberListAdapter;
    }

    public OldMemberListAdapter getOldMemberListAdapter() {
        return this.oldMemberListAdapter;
    }

    public void setPopupWindowClick(CloseMemberListWindow popup_click) {
        this.popup_click = popup_click;
    }

    //判断弹框弹出时，用户的点击是在底部控件的内部还是外部
    boolean isInView = true;

    /**
     * 弹出popupwindow
     *
     * @param view
     * @param cb_view
     * @param width
     * @param height
     * @param type    0 新版界面    1 旧版界面
     */
    public void showMemberListPopupWindow(final View view, final View cb_view, int width, int height, int type, boolean padLeft) {
        postion = type;

        View contentView;
        if (type == 0) {
            contentView = LayoutInflater.from(activity).inflate(R.layout.layout_member_list_popupwindow, null);
        } else {
            contentView = LayoutInflater.from(activity).inflate(R.layout.old_layout_member_list_popupwindow, null);
        }

        ScreenScale.scaleView(contentView, "CoursePopupWindowUtils");

        tv_popup_title = (TextView) contentView.findViewById(R.id.tv_popup_title);

        lv_student_name_data = (ListView) contentView.findViewById(R.id.lv_student_name_data);
        ll_choice_number = (LinearLayout) contentView.findViewById(R.id.ll_choice_number);
        et_number = (TextView) contentView.findViewById(R.id.et_number);
        im_to_left = (ImageView) contentView.findViewById(R.id.im_to_left);
        im_to_right = (ImageView) contentView.findViewById(R.id.im_to_right);
        tv_number_total = (TextView) contentView.findViewById(R.id.tv_number_total);

        contentView.findViewById(R.id.iv_popup_close).setOnClickListener(this);
        contentView.findViewById(R.id.im_to_left).setOnClickListener(this);
        contentView.findViewById(R.id.im_to_right).setOnClickListener(this);

        ll_choice_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMunberInputPopupWindow(view);
            }
        });
        setTiteNumber(RoomSession.memberList.size());

        setLefeStatus();
        setRightStatus();

        popupWindowMemberList = new PopupWindow(width, height);
        popupWindowMemberList.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindowMemberList.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindowMemberList.setContentView(contentView);

        if (type == 0) {
            lv_student_name_data.setAdapter(memberListAdapter);
        } else {
            lv_student_name_data.setAdapter(oldMemberListAdapter);
        }

        popupWindowMemberList.setBackgroundDrawable(new BitmapDrawable());
        popupWindowMemberList.setFocusable(false);
        popupWindowMemberList.setOutsideTouchable(true);

        popupWindowMemberList.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popup_click != null) {
                    if (!isInView) {
                        popup_click.close_member_list_window();
                    }
                }
            }
        });

        popupWindowMemberList.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isInView = Tools.isInView(event, cb_view);
                return false;
            }
        });

        int[] reb_wb_board = new int[2];
        view.getLocationInWindow(reb_wb_board);

        //popupwindow基于屏幕左上角位移到给定view中心的偏移量
        int x = 0;
        if (padLeft) {
            x = Math.abs(view.getWidth() - popupWindowMemberList.getWidth()) / 2 + FullScreenTools.getStatusBarHeight(activity);
        } else {
            x = Math.abs(view.getWidth() - popupWindowMemberList.getWidth()) / 2;
        }
        int y = Math.abs(reb_wb_board[1] + view.getHeight() / 2 - popupWindowMemberList.getHeight() / 2);

        tv_popup_title.setText(activity.getString(R.string.userlist) + "（" + memberList.size() + "）");

        popupWindowMemberList.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
    }

    public void dismissPopupWindow() {
        if (popupWindowMemberList != null) {
            popupWindowMemberList.dismiss();
        }
    }

    public boolean isShowing() {
        if (popupWindowMemberList != null) {
            return popupWindowMemberList.isShowing();
        } else {
            return false;
        }
    }

    public void setTiteNumber(int number) {
        if (tv_popup_title != null) {
            tv_popup_title.setText(activity.getString(R.string.userlist) + "（" + number + "）");
        }
        if (tv_number_total != null && number != 0) {
            int total = 1;
            if (number % 20 == 0) {
                total = number / 20;
            } else {
                total = number / 20 + 1;
            }
            tv_number_total.setText(total + "");
        }
        if (im_to_right != null) {
            setRightStatus();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_popup_close) {
            if (popupWindowMemberList != null) {
                popupWindowMemberList.dismiss();
                if (popup_click != null) {
                    popup_click.close_member_list_window();
                }
            }
        } else if (v.getId() == R.id.im_to_left) {
            String chooseMember = et_number.getText().toString();
            int[] roles = {1, 2};
            if (!TextUtils.isEmpty(chooseMember)) {
                int start = (Integer.parseInt(chooseMember) - 2) * 20;
                int max = (Integer.parseInt(chooseMember) - 1) * 20 - 1;
                RoomSession.start = start;
                RoomSession.max = max;

                HashMap hashMap = new HashMap<String, Object>();
                hashMap.put("ts", "asc");
                hashMap.put("role", "asc");
                TKRoomManager.getInstance().getRoomUsers(roles, start, max, null, hashMap);
            }
        } else if (v.getId() == R.id.im_to_right) {
            String chooseMember = et_number.getText().toString();
            int[] roles = {1, 2};
            if (!TextUtils.isEmpty(chooseMember)) {
                int start = (Integer.parseInt(chooseMember)) * 20;
                int max = (Integer.parseInt(chooseMember) + 1) * 20 - 1;
                RoomSession.start = start;
                RoomSession.max = max;

                HashMap hashMap = new HashMap<String, Object>();
                hashMap.put("ts", "asc");
                hashMap.put("role", "asc");
                TKRoomManager.getInstance().getRoomUsers(roles, start, max, null, hashMap);
            }
            et_number.setText(Integer.parseInt(chooseMember) + 1 + "");
            setRightStatus();
        }
    }

    private void setLefeStatus() {
        if (et_number != null && im_to_left != null) {
            if (et_number.getText().toString().equals("1")) {
                im_to_left.setClickable(false);
                if (postion == 0) {
                    im_to_left.setImageResource(R.drawable.munber_common_icon_left_unclickable);
                } else {
                    im_to_left.setImageResource(R.drawable.old_munber_common_icon_left_unclickable);
                }
            } else {
                im_to_left.setClickable(true);
                if (postion == 0) {
                    im_to_left.setImageResource(R.drawable.munber_common_icon_left);
                } else {
                    im_to_left.setImageResource(R.drawable.old_munber_common_icon_left);
                }
            }
        }
    }

    private void setRightStatus() {
        if (et_number != null && im_to_right != null && tv_number_total != null) {
            String totalNumber = tv_number_total.getText().toString();
            String etNumber = et_number.getText().toString();
            if (!TextUtils.isEmpty(totalNumber) && !TextUtils.isEmpty(etNumber)) {
                if (totalNumber.equals("1")) {
                    im_to_right.setClickable(false);
                    if (postion == 0) {
                        im_to_right.setImageResource(R.drawable.munber_common_icon_right_unclickable);
                    } else {
                        im_to_right.setImageResource(R.drawable.old_munber_common_icon_right_unclickable);
                    }
                } else {
                    if (etNumber.equals(totalNumber)) {
                        im_to_right.setClickable(false);
                        if (postion == 0) {
                            im_to_right.setImageResource(R.drawable.munber_common_icon_right_unclickable);
                        } else {
                            im_to_right.setImageResource(R.drawable.old_munber_common_icon_right_unclickable);
                        }
                    } else {
                        im_to_right.setClickable(true);
                        if (postion == 0) {
                            im_to_right.setImageResource(R.drawable.munber_common_icon_right);
                        } else {
                            im_to_right.setImageResource(R.drawable.old_munber_common_icon_right);
                        }

                    }
                }
            }
        }
    }

    /**
     * 显示软键盘上方一小条输入栏
     *
     * @param view
     */
    public void showMunberInputPopupWindow(final View view) {

        if (munber_input_popup != null && munber_input_popup.isShowing()) {
            munber_input_popup.dismiss();
            munber_input_popup = null;
        }

        final View contentView = LayoutInflater.from(activity).inflate(R.layout.layout_munber_input, null);
        ScreenScale.scaleView(contentView, "MunberInput");

        munber_input_popup = new PopupWindow(activity);
        munber_input_popup.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        munber_input_popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView txt_jump = (TextView) contentView.findViewById(R.id.txt_jump);
        edt_input_munber = (EditText) contentView.findViewById(R.id.edt_input_munber);

        txt_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                munber_input_popup.dismiss();
                KeyBoardUtil.hideInputMethod(activity);
            }
        });

        munber_input_popup.setContentView(contentView);
        munber_input_popup.setBackgroundDrawable(null);

        munber_input_popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        munber_input_popup.setFocusable(true);
        munber_input_popup.setBackgroundDrawable(new BitmapDrawable());
        munber_input_popup.setOutsideTouchable(false);
        munber_input_popup.setTouchable(true);

        munber_input_popup.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        munber_input_popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String edt_input_content = edt_input_munber.getText().toString();
                if (tv_number_total != null) {
                    String total = tv_number_total.getText().toString();
                    if (!TextUtils.isEmpty(total) && !TextUtils.isEmpty(edt_input_content)) {
                        if (Integer.parseInt(edt_input_content) > Integer.parseInt(total)) {
                            edt_input_content = total;
                        }
                    }
                }
                et_number.setText(edt_input_content);
                if (SoftKeyBoardListener.isSoftShowing(activity)) {
                    KeyBoardUtil.showKeyBoard(activity, edt_input_munber);
                }
            }
        });
        KeyBoardUtil.showKeyBoard(activity, edt_input_munber);
    }

    public interface CloseMemberListWindow {
        void close_member_list_window();
    }
}
