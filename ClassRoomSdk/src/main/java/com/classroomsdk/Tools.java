package com.classroomsdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/6/17.
 */

public class Tools {
    public static ProgressDialog progressDialog;

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
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(activity, android.R.style.Theme_Holo_Light_Dialog);
            }
            if (message != null) {
                progressDialog.setMessage(message);
            }
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.show();
        }
    }

    public static void HideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static boolean isMp4(String filename) {
        int icon = -1;
        if (filename.toLowerCase().endsWith("mp4") || filename.toLowerCase().endsWith("webm")) {
            return true;
        } else {
            return false;
        }
    }

    /***
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static HashMap<String, Object> toHashMap(String str) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        if (TextUtils.isEmpty(str)) {
            return null;
        } else {
            // 将json字符串转换成jsonObject
            try {
                JSONObject jsonObject = new JSONObject(str);
                Iterator it = jsonObject.keys();
                // 遍历jsonObject数据，添加到Map对象
                while (it.hasNext()) {
                    String key = String.valueOf(it.next());
                    Object value = jsonObject.get(key);
                    data.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }
    }
}
