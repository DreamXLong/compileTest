/*
 * This is the source code of Emm for Android v. 1.3.2.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013.
 */

package com.eduhdsdk.tools;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.talkcloud.roomsdk.DispatchQueue;
import com.talkcloud.roomsdk.FastDateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

public class LogUtils {

    public static boolean Bugswitch = false;

    public static void isLogE(String name, String message) {
        if (Bugswitch) {
            Log.e(name, message);
        }
    }

    public static void isLogD(String name, String message) {
        if (Bugswitch) {
            Log.d(name, message);
        }
    }

    public static void isLogI(String name, String message) {
        if (Bugswitch) {
            Log.i(name, message);
        }
    }
}
