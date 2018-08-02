package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eduhdsdk.R;
import com.eduhdsdk.adapter.ChoseNumberAdapter;
import com.eduhdsdk.tools.Tools;
import com.talkcloud.roomsdk.TKRoomManager;

import java.util.ArrayList;
import java.util.List;

import skin.support.SkinCompatManager;


/**
 * Created by Administrator on 2018/4/20.
 * 选择白板页数
 */

public class PageNumberPopupWindowUtils {

    private String TAG = "PageNumberPopupWindowUtils";

    private Activity activity;
    private PopupWindow popupWindowPageNumber;
    private List<String> data;
    private ListView listView;
    private PageNumberClick pageNumberClick;
    private ImageView iv_page_choose_icon;

    public PageNumberPopupWindowUtils(Activity activity) {
        this.activity = activity;
    }

    public void setPageNumberClick(PageNumberClick pageNumberClick) {
        this.pageNumberClick = pageNumberClick;
    }

    /**
     * 显示页数window
     *
     * @param view
     * @param width
     * @param iv_page_choose_icon
     * @param data_num            数据源的数量    （根据这个动态设置window的高度）
     */
    public void showPageNumberPopupWindow(View view, int width, final ImageView iv_page_choose_icon, int data_num) {

        this.iv_page_choose_icon = iv_page_choose_icon;
        int height = 0;

        if (data_num == 0) {
            return;
        }

        //防止用户多次点击，出现多个popupwindow的情况
        if (popupWindowPageNumber != null) {
            if (popupWindowPageNumber.isShowing()) {
                popupWindowPageNumber.dismiss();
                iv_page_choose_icon.setImageResource(R.drawable.common_icon_xiala);
                return;
            }
        }

        View contentView = LayoutInflater.from(activity).inflate(R.layout.popup_page_number_layout, null);

        data = new ArrayList<String>();

        for (int i = 1; i <= data_num; i++) {
            if (i < 10) {
                data.add("0" + i);
            } else {
                data.add(i + "");
            }
        }

        listView = (ListView) contentView.findViewById(R.id.lv_page_number);
        ChoseNumberAdapter choseNumberAdapter = new ChoseNumberAdapter(activity, data);
        listView.setAdapter(choseNumberAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pageNumberClick != null) {
                    pageNumberClick.pageNumberClick(position + 1);
                }
            }
        });

        //数据源不足五条时，动态设置弹框的高度
        if (data_num < 5) {
            //item布局的高度 * item的数量 + window布局的上下padding值
            height = data_num * 60 + 35;
        } else {
            height = 5 * 60 + 35;
        }

        popupWindowPageNumber = new PopupWindow(width, height);

        popupWindowPageNumber.setContentView(contentView);
        popupWindowPageNumber.setBackgroundDrawable(new BitmapDrawable());
        popupWindowPageNumber.setFocusable(false);
        popupWindowPageNumber.setOutsideTouchable(true);
//        popupWindowPageNumber.showAtLocation(view, Gravity.CENTER, 0, 0);
        //设置popupwindow在底部页数布局的正上方
//        popupWindowPageNumber.showAsDropDown(view, Math.abs((int) (popupWindowPageNumber.getWidth() - view.getWidth()) / 3), 0, Gravity.TOP);
        popupWindowPageNumber.showAsDropDown(view, -15, 0, Gravity.TOP);
        popupWindowPageNumber.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (iv_page_choose_icon != null) {
                    iv_page_choose_icon.setImageResource(R.drawable.common_icon_xiala);
                }
            }
        });
        iv_page_choose_icon.setImageResource(R.drawable.common_icon_up);
    }

    public void dismissPopupWindow() {
        if (popupWindowPageNumber != null) {
            popupWindowPageNumber.dismiss();
            iv_page_choose_icon.setImageResource(R.drawable.common_icon_xiala);
        }
    }

    /**
     * 定义popupwindow的接口，通过接口和activity进行通信
     */
    public interface PageNumberClick {
        void pageNumberClick(int position);
    }
}
