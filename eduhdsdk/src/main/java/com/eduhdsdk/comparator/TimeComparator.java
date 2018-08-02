package com.eduhdsdk.comparator;

import android.support.annotation.NonNull;

import com.classroomsdk.ShareDoc;

import java.util.Comparator;

/**
 * Created by Administrator on 2018/5/7/007.
 */

public class TimeComparator implements Comparator<ShareDoc> {

    private boolean isUp = true;

    public void setisUp(boolean isUp) {
        this.isUp = isUp;
    }

    @Override
    public int compare(ShareDoc o1, ShareDoc o2) {
        if (isUp) {
            return (int) (o1.getFileid() - o2.getFileid());
        } else {
            return (int) (o2.getFileid() - o1.getFileid());
        }
    }
}
