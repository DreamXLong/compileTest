package com.eduhdsdk.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eduhdsdk.R;
import com.eduhdsdk.message.RoomClient;
import com.eduhdsdk.ui.OneToManyActivity;
import com.talkcloud.roomsdk.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Administrator on 2017/6/17.
 */

public class Tools {

    public static ProgressDialog progressDialog;
    public static boolean isDialogShow = true;

    public static boolean isTure(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else if (o instanceof Number) {
            Number n = (Number) o;
            return n.longValue() != 0;
        } else {
            return false;
        }
    }

    public static long toLong(Object o) {
        long temLong = 0;
        if (o instanceof Integer) {
            int tem = (Integer) o;
            temLong = tem;
        } else if (o instanceof String) {
            String temstr = (String) o;
            temLong = Long.valueOf(temstr);
        } else {
            temLong = (Long) o;
        }
        return temLong;
    }

    public static void ShowProgressDialog(final Activity activity, final String message) {
        if (!activity.isFinishing()) {
            progressDialog = new ProgressDialog(activity, android.R.style.Theme_Holo_Light_Dialog);
            if (message != null) {
                progressDialog.setMessage(message);
            }
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.show();
            isDialogShow = true;
        }
    }

    /**
     * 弹出对话框，只有确定按钮
     *
     * @param activity
     * @param message
     */
    public static void ShowAlertDialog(final Activity activity, final String message, int type) {

        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.nothing);
        View v;
        if (type == 0) {
            v = LayoutInflater.from(activity).inflate(R.layout.layout_net_dialog, null);
        } else {
            v = LayoutInflater.from(activity).inflate(R.layout.old_layout_net_dialog, null);
        }

        TextView tv_dialog_message = (TextView) v.findViewById(R.id.tv_exit_dialog_message);
        tv_dialog_message.setText(message);
        v.findViewById(R.id.iv_close_popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        v.findViewById(R.id.bt_ok_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setContentView(v);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = KeyBoardUtil.dp2px(activity, 300f); // 宽度
        lp.height = KeyBoardUtil.dp2px(activity, 160f); // 高度
        dialogWindow.setAttributes(lp);
        dialog.show();

        /*if (!activity.isFinishing()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.remind);
            builder.setMessage(message);
            builder.setPositiveButton(R.string.sure,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }*/
    }

    public static void HideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            isDialogShow = false;
            progressDialog = null;
        }
    }

    /**
     * 弹出自定义文字的对话框
     *
     * @param activity
     * @param title
     * @param message
     * @param dialogClick
     * @param type        0  新版界面   1 旧版界面
     */
    public static void showDialog(Context activity, int title, int message, final OnDialogClick dialogClick, int type) {

        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.nothing);
        View v;
        if (type == 0) {
            v = LayoutInflater.from(activity).inflate(R.layout.layout_exit_dialog, null);
        } else {
            v = LayoutInflater.from(activity).inflate(R.layout.old_layout_exit_dialog, null);
        }

        TextView tv_dialog_title = (TextView) v.findViewById(R.id.tv_dialog_title);
        TextView tv_dialog_message = (TextView) v.findViewById(R.id.tv_exit_dialog_message);

        tv_dialog_title.setText(title);
        tv_dialog_message.setText(message);
        v.findViewById(R.id.iv_close_popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        v.findViewById(R.id.bt_cancel_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        v.findViewById(R.id.bt_ok_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialogClick.dialog_ok(dialog);
                }
            }
        });

        dialog.setContentView(v);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = KeyBoardUtil.dp2px(activity, 300f); // 宽度
        lp.height = KeyBoardUtil.dp2px(activity, 160f); // 高度
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    public interface OnDialogClick {
        void dialog_ok(Dialog dialog);
    }

    /**
     * 跳转到权限设置界面
     */
    public static void getAppDetailSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

    /**
     * 判断点击事件是否在指定view的内部
     *
     * @param event
     * @param view
     * @return
     */
    public static boolean isInView(MotionEvent event, View view) {
        int[] view_data = new int[2];
        view.getLocationInWindow(view_data);

        if (event.getRawX() > view_data[0] && event.getRawX() < (view_data[0] + view.getWidth()) && event.getRawY() > view_data[1] && event.getRawY() < (view_data[1] + view.getHeight())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }


    public static boolean isMp4(String fileType) {
        if (fileType.toLowerCase().endsWith("mp4") || fileType.toLowerCase().endsWith("webm")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 为CheckBox动态设置selector
     *
     * @param cbButton
     * @param no_select 未选中图片
     * @param select    选中图片
     */
    public static void setSelectorDrawable(Activity activity, CheckBox cbButton, int select, int no_select) {
        StateListDrawable drawable = new StateListDrawable();
        //选中
        drawable.addState(new int[]{android.R.attr.state_checked}, activity.getResources().getDrawable(select));
        //未选中
        drawable.addState(new int[]{-android.R.attr.state_checked}, activity.getResources().getDrawable(no_select));
        cbButton.setBackgroundDrawable(drawable);
    }
}
