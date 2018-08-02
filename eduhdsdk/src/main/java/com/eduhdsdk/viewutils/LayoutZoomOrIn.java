package com.eduhdsdk.viewutils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eduhdsdk.entity.VideoItem;

import org.tkwebrtc.RendererCommon;

/**
 * Created by Administrator on 2017/12/11/011.
 */

public class LayoutZoomOrIn {

    public static void zoomVideoItem(VideoItem videoItem, double scale, RelativeLayout rel_students, View v_students) {
        if (videoItem.parent.getHeight() * scale < rel_students.getHeight() &&
                (videoItem.parent.getWidth() * scale - videoItem.parent.getWidth()) / 2 < videoItem.parent.getLeft()
                && videoItem.parent.getHeight() * scale > v_students.getHeight() - 40) {
            if (scale > 1) {
                if (videoItem.parent.getTop() >= 0 &&
                        v_students.getTop() - videoItem.parent.getBottom() >= (videoItem.parent.getHeight() * scale - videoItem.parent.getHeight()) / 2) {
                    scaleVedioItem(videoItem, scale, rel_students);
                }
            } else {
                scaleVedioItem(videoItem, scale, rel_students);
            }
        }
    }

    public static void scaleVedioItem(VideoItem videoItem, double scale, RelativeLayout rel_students) {
        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) videoItem.parent.getLayoutParams();
        if (videoItem.parent.getTop() == 0) {
            if (scale > 1) {
                relparam.topMargin = 0;
            } else {
                relparam.topMargin = (int) ((videoItem.parent.getHeight() - videoItem.parent.getHeight() * scale) / 2);
            }
        } else {
            relparam.topMargin = (int) ((videoItem.parent.getTop() - (videoItem.parent.getHeight() * scale - videoItem.parent.getHeight()) / 2));
        }

        if (videoItem.parent.getLeft() == 0) {
            if (scale > 1) {
                relparam.leftMargin = 0;
            } else {
                relparam.leftMargin = (int) ((videoItem.parent.getWidth() - videoItem.parent.getWidth() * scale) / 2);
            }
        } else {
            relparam.leftMargin = (int) (videoItem.parent.getLeft() - (videoItem.parent.getWidth() * scale - videoItem.parent.getWidth()));
        }

        if (videoItem.parent.getRight() == rel_students.getRight()) {
            if (scale < 1) {
                relparam.leftMargin = (int) (rel_students.getRight() - videoItem.parent.getWidth() * scale / 2 -
                        (videoItem.parent.getWidth() - videoItem.parent.getWidth() * scale) / 2);
            }
        }

        relparam.width = (int) (videoItem.parent.getWidth() * scale);
        relparam.height = (int) (videoItem.parent.getHeight() * scale);
        videoItem.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) videoItem.rel_video_label.getLayoutParams();
        linparam.width = (int) (videoItem.parent.getWidth() * scale);
        linparam.height = (int) (videoItem.parent.getHeight() * scale - videoItem.lin_name_label.getHeight());
        videoItem.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) videoItem.sf_video.getLayoutParams();
       /* sf_videoParam.width = (int) (videoItem.parent.getWidth() * scale);
        sf_videoParam.height = (int) (videoItem.parent.getHeight() * scale);*/
        sf_videoParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoItem.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        videoItem.sf_video.setLayoutParams(sf_videoParam);

        LinearLayout.LayoutParams nameparam = (LinearLayout.LayoutParams) videoItem.lin_name_label.getLayoutParams();
        nameparam.width = (int) (videoItem.parent.getWidth() * scale);
        nameparam.height = videoItem.lin_name_label.getHeight();
        videoItem.lin_name_label.setLayoutParams(nameparam);
    }

    public static void zoomMouldVideoItem(VideoItem videoItem, double scale, RelativeLayout rel_students, View v_students) {
        if (videoItem.parent.getHeight() * scale < rel_students.getHeight() - v_students.getHeight() &&
                (videoItem.parent.getWidth() * scale - videoItem.parent.getWidth()) / 2 < videoItem.parent.getLeft()
                && videoItem.parent.getHeight() * scale > v_students.getHeight() - 40) {
            if (scale > 1) {
                if (videoItem.parent.getTop() > v_students.getBottom()) {
                    scaleMouldVedioItem(videoItem, scale, rel_students, v_students);
                }
            } else {
                narrowMouldVideoItem(videoItem, scale, rel_students, v_students);
            }
        }
    }

    public static void narrowMouldVideoItem(VideoItem videoItem, double scale, RelativeLayout rel_students, View v_students) {
        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) videoItem.parent.getLayoutParams();
        relparam.topMargin = videoItem.parent.getTop() + (int) ((videoItem.parent.getHeight() - videoItem.parent.getHeight() * scale) / 2);
        relparam.leftMargin = (int) (videoItem.parent.getLeft() + (videoItem.parent.getWidth() - videoItem.parent.getWidth() * scale) / 2);

        if (videoItem.parent.getRight() == rel_students.getRight()) {
            relparam.leftMargin = (int) (rel_students.getRight() - videoItem.parent.getWidth() * scale / 2 -
                    (videoItem.parent.getWidth() - videoItem.parent.getWidth() * scale) / 2);
        }

        relparam.width = (int) (videoItem.parent.getWidth() * scale);
        relparam.height = (int) (videoItem.parent.getHeight() * scale);
        videoItem.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) videoItem.rel_video_label.getLayoutParams();
        linparam.width = (int) (videoItem.parent.getWidth() * scale);
        linparam.height = (int) (videoItem.parent.getHeight() * scale - videoItem.lin_name_label.getHeight());
        videoItem.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) videoItem.sf_video.getLayoutParams();
       /* sf_videoParam.width = (int) (videoItem.parent.getWidth() * scale);
        sf_videoParam.height = (int) (videoItem.parent.getHeight() * scale);*/
        sf_videoParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoItem.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        videoItem.sf_video.setLayoutParams(sf_videoParam);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) videoItem.lin_name_label.getLayoutParams();
        nameparam.width = (int) (videoItem.parent.getWidth() * scale);
        nameparam.height = videoItem.lin_name_label.getHeight();
        videoItem.lin_name_label.setLayoutParams(nameparam);
    }

    public static void scaleMouldVedioItem(VideoItem videoItem, double scale, RelativeLayout rel_students, View v_students) {
        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) videoItem.parent.getLayoutParams();
        if (videoItem.parent.getTop() == v_students.getBottom()) {
            if (scale > 1) {
                relparam.topMargin = v_students.getBottom();
            } else {
                relparam.topMargin = (int) ((videoItem.parent.getHeight() - videoItem.parent.getHeight() * scale) / 2);
            }
        } else {
            if ((int) ((videoItem.parent.getTop() - (videoItem.parent.getHeight() * scale - videoItem.parent.getHeight()) / 2)) < v_students.getBottom()) {
                relparam.topMargin = v_students.getBottom();
            } else {
                relparam.topMargin = (int) ((videoItem.parent.getTop() - (videoItem.parent.getHeight() * scale - videoItem.parent.getHeight()) / 2));
            }
        }

        if (videoItem.parent.getLeft() == 0) {
            if (scale > 1) {
                relparam.leftMargin = 0;
            } else {
                relparam.leftMargin = (int) ((videoItem.parent.getWidth() - videoItem.parent.getWidth() * scale) / 2);
            }
        } else {
            if ((int) (videoItem.parent.getLeft() - (videoItem.parent.getWidth() * scale - videoItem.parent.getWidth())) < 0) {
                relparam.leftMargin = 0;
            } else {
                relparam.leftMargin = (int) (videoItem.parent.getLeft() - (videoItem.parent.getWidth() * scale - videoItem.parent.getWidth()));
            }
        }

        if (videoItem.parent.getRight() == rel_students.getRight()) {
            if (scale < 1) {
                relparam.leftMargin = (int) (rel_students.getRight() - videoItem.parent.getWidth() * scale / 2 -
                        (videoItem.parent.getWidth() - videoItem.parent.getWidth() * scale) / 2);
            }
        }

        relparam.width = (int) (videoItem.parent.getWidth() * scale);
        relparam.height = (int) (videoItem.parent.getHeight() * scale);
        videoItem.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) videoItem.rel_video_label.getLayoutParams();
        linparam.width = relparam.width;
        linparam.height = (int) (videoItem.parent.getHeight() * scale - videoItem.lin_name_label.getHeight());
        videoItem.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) videoItem.sf_video.getLayoutParams();
        sf_videoParam.width = (int) (videoItem.parent.getWidth() * scale);
        sf_videoParam.height = (int) (videoItem.parent.getHeight() * scale);
        sf_videoParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoItem.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        videoItem.sf_video.setLayoutParams(sf_videoParam);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) videoItem.lin_name_label.getLayoutParams();
        nameparam.width = relparam.width;
        nameparam.height = videoItem.lin_name_label.getHeight();
        videoItem.lin_name_label.setLayoutParams(nameparam);
    }

    public static void zoomMsgVideoItem(VideoItem videoItem, double scale, double printWidth, double printHeight) {
        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) videoItem.parent.getLayoutParams();
        relparam.width = (int) (printWidth * scale);
        relparam.height = (int) (printHeight * scale);
        videoItem.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) videoItem.rel_video_label.getLayoutParams();
        linparam.width = (int) (printWidth * scale);
        linparam.height = (int) (printHeight * scale - videoItem.lin_name_label.getHeight());
        videoItem.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) videoItem.sf_video.getLayoutParams();
        /*sf_videoParam.width = (int) (printWidth * scale);
        sf_videoParam.height = (int) (printHeight * scale - videoItem.lin_name_label.getHeight());*/
        sf_videoParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoItem.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        videoItem.sf_video.setLayoutParams(sf_videoParam);

        LinearLayout.LayoutParams nameparam = (LinearLayout.LayoutParams) videoItem.lin_name_label.getLayoutParams();
        nameparam.width = (int) (printWidth * scale);
        nameparam.height = videoItem.lin_name_label.getHeight();
        videoItem.lin_name_label.setLayoutParams(nameparam);
    }

    public static void zoomMsgMouldVideoItem(VideoItem videoItem, double scale, double printWidth, double printHeight, int maxHehiht) {

        if (printHeight * scale > maxHehiht) {
            scale = maxHehiht / printHeight;
        }

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) videoItem.parent.getLayoutParams();
        relparam.width = (int) (printWidth * scale);
        relparam.height = (int) (printHeight * scale);
        videoItem.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) videoItem.rel_video_label.getLayoutParams();
        linparam.width = (int) (printWidth * scale);
        linparam.height = (int) (printHeight * scale - videoItem.lin_name_label.getHeight());
        videoItem.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) videoItem.sf_video.getLayoutParams();
       /* sf_videoParam.width = (int) (printWidth * scale);
        sf_videoParam.height = (int) (printHeight * scale - videoItem.lin_name_label.getHeight());*/
        sf_videoParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoItem.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        videoItem.sf_video.setLayoutParams(sf_videoParam);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) videoItem.lin_name_label.getLayoutParams();
        nameparam.width = (int) (printWidth * scale);
        nameparam.height = videoItem.lin_name_label.getHeight();
        videoItem.lin_name_label.setLayoutParams(nameparam);
    }

    public static void layoutVideoItem(VideoItem it, int left) {

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.width = it.parent.getWidth();
        relparam.height = it.parent.getHeight();
        relparam.topMargin = 0;
        relparam.leftMargin = left;
        relparam.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relparam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        it.parent.setTranslationX(0);
        it.parent.setTranslationY(0);
        it.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) it.rel_video_label.getLayoutParams();
        linparam.width = it.parent.getWidth();
        linparam.height = it.parent.getHeight() - it.lin_name_label.getHeight();
        it.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) it.sf_video.getLayoutParams();
        /*sf_videoParam.width = it.parent.getWidth();
        sf_videoParam.height = it.parent.getHeight();*/
        sf_videoParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        it.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        it.sf_video.setLayoutParams(sf_videoParam);

        LinearLayout.LayoutParams nameparam = (LinearLayout.LayoutParams) it.lin_name_label.getLayoutParams();
        nameparam.width = it.parent.getWidth();
        nameparam.height = it.lin_name_label.getHeight();
        it.lin_name_label.setLayoutParams(nameparam);
    }

    public static void layoutVideo(VideoItem it, int x, int y) {
        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.topMargin = y;
        relparam.leftMargin = x;
        relparam.bottomMargin = 0;
        relparam.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relparam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        it.parent.setLayoutParams(relparam);
    }

    public static void layoutMouldVideo(VideoItem it, int x, int y, int bottom) {
        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.bottomMargin = bottom;
        relparam.topMargin = y;
        relparam.leftMargin = x;
        relparam.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relparam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        it.parent.setLayoutParams(relparam);
    }
}
