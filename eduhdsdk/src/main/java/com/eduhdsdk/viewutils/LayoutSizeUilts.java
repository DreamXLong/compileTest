package com.eduhdsdk.viewutils;

import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eduhdsdk.entity.VideoItem;

import org.tkwebrtc.RendererCommon;


/**
 * Created by Administrator on 2017/11/22/022.
 */

public class LayoutSizeUilts {

    /***
     *
     * @param it
     * @param rel_wb_container   分屏时 视频框大小
     * @param x
     * @param y
     */
    public static void videoSize(VideoItem it, RelativeLayout rel_wb_container, int x, int y, double nameLabelHeight) {

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.width = rel_wb_container.getWidth() / x;
        relparam.height = rel_wb_container.getHeight() / y;
        it.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) it.rel_video_label.getLayoutParams();
        linparam.width = rel_wb_container.getWidth() / x;
        linparam.height = rel_wb_container.getHeight() / y;
        it.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams imc = (RelativeLayout.LayoutParams) it.img_mic.getLayoutParams();
        imc.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imc.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imc.rightMargin = relparam.width / 16;
        it.img_mic.setLayoutParams(imc);
        it.img_pen.setLayoutParams(imc);

        RelativeLayout.LayoutParams imhand = (RelativeLayout.LayoutParams) it.img_hand.getLayoutParams();
        imhand.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imhand.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imhand.leftMargin = relparam.width / 16;
        it.img_hand.setLayoutParams(imhand);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) it.sf_video.getLayoutParams();
        sf_videoParam.width = rel_wb_container.getWidth() / x;
        sf_videoParam.height = rel_wb_container.getHeight() / y;
        it.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        it.sf_video.setLayoutParams(sf_videoParam);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) it.lin_name_label.getLayoutParams();
        nameparam.width = rel_wb_container.getWidth() / x;
        /*nameparam.height = (int) (nameLabelHeight * ((double) 35 / (double) 240));*/
        nameparam.height = (int) nameLabelHeight;
        nameparam.bottomMargin = linparam.height / 12;
        it.lin_name_label.setLayoutParams(nameparam);

        LinearLayout.LayoutParams txt_name_par = (LinearLayout.LayoutParams) it.txt_name.getLayoutParams();
        txt_name_par.gravity = Gravity.CENTER_VERTICAL;
        txt_name_par.leftMargin = relparam.width / 5;
        it.txt_name.setLayoutParams(txt_name_par);
    }

    /***
     *
     * @param it
     * @param rel_wb_container   分屏时旧版视频框大小
     * @param x
     * @param y
     */
    public static void videoOldSize(VideoItem it, RelativeLayout rel_wb_container, int x, int y, double nameLabelHeight) {

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.width = rel_wb_container.getWidth() / x;
        relparam.height = rel_wb_container.getHeight() / y;
        it.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) it.rel_video_label.getLayoutParams();
        linparam.width = rel_wb_container.getWidth() / x;
        linparam.height = rel_wb_container.getHeight() / y;
        it.rel_video_label.setLayoutParams(linparam);

        /*RelativeLayout.LayoutParams imc = (RelativeLayout.LayoutParams) it.img_mic.getLayoutParams();
        imc.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imc.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        it.img_mic.setLayoutParams(imc);
        it.img_pen.setLayoutParams(imc);*/

        RelativeLayout.LayoutParams imhand = (RelativeLayout.LayoutParams) it.img_hand.getLayoutParams();
        imhand.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imhand.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        it.img_hand.setLayoutParams(imhand);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) it.sf_video.getLayoutParams();
        sf_videoParam.width = rel_wb_container.getWidth() / x;
        sf_videoParam.height = rel_wb_container.getHeight() / y;
        it.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        it.sf_video.setLayoutParams(sf_videoParam);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) it.lin_name_label.getLayoutParams();
        nameparam.width = rel_wb_container.getWidth() / x;
        nameparam.height = (int) nameLabelHeight;
        it.lin_name_label.setLayoutParams(nameparam);

    }

    /***
     *
     * @param it
     * @param rel_wb_container   分屏时 13路视频老师框大小
     * @param x
     * @param y
     */
    public static void videoThirteenSize(VideoItem it, RelativeLayout rel_wb_container, int x, int y, double nameLabelHeight) {

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.width = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4;
        relparam.height = rel_wb_container.getHeight() / y;
        it.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) it.rel_video_label.getLayoutParams();
        linparam.width = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4;
        linparam.height = (int) (rel_wb_container.getHeight() / y - nameLabelHeight);
        it.rel_video_label.setLayoutParams(linparam);

        LinearLayout.LayoutParams imc = (LinearLayout.LayoutParams) it.img_mic.getLayoutParams();
        imc.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        imc.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        it.img_mic.setLayoutParams(imc);
        it.img_pen.setLayoutParams(imc);
        RelativeLayout.LayoutParams imhand = (RelativeLayout.LayoutParams) it.img_hand.getLayoutParams();
        imhand.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imhand.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        it.img_hand.setLayoutParams(imhand);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) it.sf_video.getLayoutParams();
        /*sf_videoParam.width = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4;
        sf_videoParam.height = rel_wb_container.getHeight() / y;*/
        it.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        it.sf_video.setLayoutParams(sf_videoParam);

        LinearLayout.LayoutParams nameparam = (LinearLayout.LayoutParams) it.lin_name_label.getLayoutParams();
        nameparam.width = (rel_wb_container.getWidth() - rel_wb_container.getWidth() / 4) / 4;
        nameparam.height = (int) nameLabelHeight;
        it.lin_name_label.setLayoutParams(nameparam);
    }

    /***
     * @param it
     * @param printWidth
     * @param printHeight
     * @param nameLabelHeight   13路视频移动大小
     */
    public static void moveVideoSize(VideoItem it, double printWidth, double printHeight, double nameLabelHeight) {

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.width = (int) printWidth;
        relparam.height = (int) printHeight;
        it.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) it.rel_video_label.getLayoutParams();
        linparam.width = (int) printWidth;
        linparam.height = (int) (printHeight - nameLabelHeight);
        it.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) it.sf_video.getLayoutParams();
       /* sf_videoParam.width = (int) printWidth;
        sf_videoParam.height = (int) (printHeight - nameLabelHeight);*/
        it.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        it.sf_video.setLayoutParams(sf_videoParam);

        LinearLayout.LayoutParams imc = (LinearLayout.LayoutParams) it.img_mic.getLayoutParams();
        imc.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        imc.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        it.img_mic.setLayoutParams(imc);
        it.img_pen.setLayoutParams(imc);
        RelativeLayout.LayoutParams imhand = (RelativeLayout.LayoutParams) it.img_hand.getLayoutParams();
        imhand.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imhand.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        it.img_hand.setLayoutParams(imhand);

        LinearLayout.LayoutParams nameparam = (LinearLayout.LayoutParams) it.lin_name_label.getLayoutParams();
        nameparam.width = (int) printWidth;
        nameparam.height = (int) nameLabelHeight;
        it.lin_name_label.setLayoutParams(nameparam);
    }

    /***
     * @param it
     * @param printWidth
     * @param printHeight
     * @param nameLabelHeight   13路视频移动大小
     */
    public static void moveOldVideoSize(VideoItem it, double printWidth, double printHeight, double nameLabelHeight) {

        RelativeLayout.LayoutParams relparam = (RelativeLayout.LayoutParams) it.parent.getLayoutParams();
        relparam.width = (int) printWidth;
        relparam.height = (int) printHeight;
        it.parent.setLayoutParams(relparam);

        LinearLayout.LayoutParams linparam = (LinearLayout.LayoutParams) it.rel_video_label.getLayoutParams();
        linparam.width = (int) printWidth;
        linparam.height = (int) (printHeight - nameLabelHeight);
        it.rel_video_label.setLayoutParams(linparam);

        RelativeLayout.LayoutParams sf_videoParam = (RelativeLayout.LayoutParams) it.sf_video.getLayoutParams();
        sf_videoParam.width = (int) printWidth;
        sf_videoParam.height = (int) printHeight;
        it.sf_video.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        it.sf_video.setLayoutParams(sf_videoParam);

        LinearLayout.LayoutParams imc = (LinearLayout.LayoutParams) it.img_mic.getLayoutParams();
        imc.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        imc.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        it.img_mic.setLayoutParams(imc);

        RelativeLayout.LayoutParams img_pen = (RelativeLayout.LayoutParams) it.img_pen.getLayoutParams();
        img_pen.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        img_pen.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        it.img_pen.setLayoutParams(img_pen);

        RelativeLayout.LayoutParams imhand = (RelativeLayout.LayoutParams) it.img_hand.getLayoutParams();
        imhand.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imhand.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        it.img_hand.setLayoutParams(imhand);

        RelativeLayout.LayoutParams nameparam = (RelativeLayout.LayoutParams) it.lin_name_label.getLayoutParams();
        nameparam.width = (int) printWidth;
        nameparam.height = (int) nameLabelHeight;
        it.lin_name_label.setLayoutParams(nameparam);
    }
}
