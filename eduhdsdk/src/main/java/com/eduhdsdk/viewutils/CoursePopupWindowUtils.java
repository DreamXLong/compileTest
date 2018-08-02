package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.classroomsdk.ShareDoc;
import com.classroomsdk.WhiteBoradManager;
import com.eduhdsdk.R;
import com.eduhdsdk.adapter.FileListAdapter;
import com.eduhdsdk.adapter.MediaListAdapter;
import com.eduhdsdk.adapter.MemberListAdapter;
import com.eduhdsdk.adapter.OldFileListAdapter;
import com.eduhdsdk.adapter.OldMediaListAdapter;
import com.eduhdsdk.comparator.NameComparator;
import com.eduhdsdk.comparator.TimeComparator;
import com.eduhdsdk.tools.FullScreenTools;
import com.eduhdsdk.tools.ScreenScale;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.comparator.TypeComparator;
import com.talkcloud.roomsdk.RoomControler;
import com.talkcloud.roomsdk.RoomUser;
import com.talkcloud.roomsdk.TKRoomManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Administrator on 2018/4/20.
 * 课件库
 */

public class CoursePopupWindowUtils implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private String TAG = "CoursePopupWindowUtils";

    private Activity activity;
    private PopupWindow popupWindowCourse;
    private RelativeLayout ll_course_list;
    private LinearLayout ll_media_list, ll_temp;
    private ListView lv_course_data;
    private ListView lv_media_data;
    private TextView tv_popup_title;
    private ArrayList<RoomUser> memberList;
    private MediaListAdapter mediaListAdapter;
    private FileListAdapter fileListAdapter;

    private OldFileListAdapter oldFileListAdapter;
    private OldMediaListAdapter oldMediaListAdapter;


    private PopupWindowClick popup_click;
    private TextView file_tv_class, file_tv_admin, media_tv_class, media_tv_admin;
    private LinearLayout file_classifi, media_classifi;
    private ImageView iv_media_time_sort, iv_media_type_sort, iv_media_name_sort,
            iv_file_time_sort, iv_file_type_sort, iv_file_name_sort;

    private RadioButton rb_course_library, rb_media;

    private boolean isFlageFile = false;
    private boolean isFlageMedia = false;

    /**
     * 记录时间，类型，名称的排序状态      0 正常  1 上三角被选中  2  下三角被选中
     */
    int media_time_status = 0, media_type_status = 0, media_name_status = 0;
    int file_time_status = 0, file_type_status = 0, file_name_status = 0;

    public CoursePopupWindowUtils(Activity activity, ArrayList<RoomUser> memberList, String media_serial, String file_serial) {
        isFlageMedia = false;
        isFlageMedia = false;
        this.activity = activity;
        this.memberList = memberList;
        mediaListAdapter = new MediaListAdapter(activity, media_serial);
        fileListAdapter = new FileListAdapter(activity, file_serial);
        oldFileListAdapter = new OldFileListAdapter(activity, file_serial);
        oldMediaListAdapter = new OldMediaListAdapter(activity, media_serial);
    }

    public MediaListAdapter getMediaListAdapter() {
        return this.mediaListAdapter;
    }


    public FileListAdapter getFileListAdapter() {
        return this.fileListAdapter;
    }


    public OldMediaListAdapter getOldMediaListAdapter() {
        return this.oldMediaListAdapter;
    }


    public OldFileListAdapter getOldFileListAdapter() {
        return this.oldFileListAdapter;
    }

    public void setPopupWindowClick(PopupWindowClick popup_click) {
        this.popup_click = popup_click;
    }

    private View popup_window_view;
    private int type = 0;

    /**
     * 预加载popupwindow的view
     *
     * @param type 0 新版界面    1 旧版界面
     */
    public void initCoursePopupWindow(final int type) {

        if (popupWindowCourse != null) {
            return;
        }

        this.type = type;

        View contentView;
        if (type == 0) {
            contentView = LayoutInflater.from(activity).inflate(R.layout.layout_course_popupwindow, null);
        } else {
            contentView = LayoutInflater.from(activity).inflate(R.layout.old_layout_course_popupwindow, null);
        }

        ScreenScale.scaleView(contentView, "CoursePopupWindowUtils");

        rb_course_library = (RadioButton) contentView.findViewById(R.id.rb_course_library);
        rb_media = (RadioButton) contentView.findViewById(R.id.rb_media);

        RadioGroup radio_group_popup_layout = (RadioGroup) contentView.findViewById(R.id.radio_group_popup_layout);
        radio_group_popup_layout.setOnCheckedChangeListener(this);
        ll_course_list = (RelativeLayout) contentView.findViewById(R.id.ll_course_list);
        ll_media_list = (LinearLayout) contentView.findViewById(R.id.ll_media_list);

        ll_temp = (LinearLayout) contentView.findViewById(R.id.ll_temp);

        if (TKRoomManager.getInstance().getMySelf().role == 4) {
            ll_temp.setVisibility(View.GONE);
        }

        LinearLayout popup_file_title_layout = (LinearLayout) contentView.findViewById(R.id.popup_file_title_layout);
        file_tv_class = (TextView) popup_file_title_layout.findViewById(R.id.tv_class);
        file_tv_admin = (TextView) popup_file_title_layout.findViewById(R.id.tv_admin);
        file_classifi = (LinearLayout) popup_file_title_layout.findViewById(R.id.ll_classifi);
        file_tv_admin.setBackgroundResource(R.color.nothing);
        if (type == 0) {
            file_tv_class.setTextAppearance(activity, R.style.tv_class_checked);
        } else {
            file_tv_class.setTextAppearance(activity, R.style.old_tv_class_checked);
            file_tv_admin.setTextAppearance(activity, R.style.old_tv_class_no_checked);
        }

        iv_file_time_sort = (ImageView) popup_file_title_layout.findViewById(R.id.iv_time_sort);
        iv_file_type_sort = (ImageView) popup_file_title_layout.findViewById(R.id.iv_type_sort);
        iv_file_name_sort = (ImageView) popup_file_title_layout.findViewById(R.id.iv_name_sort);

        LinearLayout ll_file_time_sort = (LinearLayout) popup_file_title_layout.findViewById(R.id.ll_time_sort);
        LinearLayout ll_file_type_sort = (LinearLayout) popup_file_title_layout.findViewById(R.id.ll_type_sort);
        LinearLayout ll_file_name_sort = (LinearLayout) popup_file_title_layout.findViewById(R.id.ll_name_sort);

        ll_file_time_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (file_time_status) {
                    case 1:  //时间正序
                        if (type == 0) {
                            sortTime(false, fileListAdapter.getArrayList(), 0);
                            iv_file_time_sort.setImageResource(R.drawable.arrange_down);
                        } else {
                            sortTime(false, oldFileListAdapter.getArrayList(), 0);
                            iv_file_time_sort.setImageResource(R.drawable.old_arrange_down);
                        }

                        file_time_status = 2;
                        break;
                    case 2:   //时间倒序
                        if (type == 0) {
                            sortTime(true, fileListAdapter.getArrayList(), 0);
                            iv_file_time_sort.setImageResource(R.drawable.arrange_up);
                        } else {
                            sortTime(true, oldFileListAdapter.getArrayList(), 0);
                            iv_file_time_sort.setImageResource(R.drawable.old_arrange_up);
                        }
                        file_time_status = 1;
                        break;
                }
                file_type_status = 1;
                file_name_status = 1;
                if (type == 0) {
                    iv_file_name_sort.setImageResource(R.drawable.arrange_none);
                    iv_file_type_sort.setImageResource(R.drawable.arrange_none);
                } else {
                    iv_file_name_sort.setImageResource(R.drawable.old_arrange_none);
                    iv_file_type_sort.setImageResource(R.drawable.old_arrange_none);
                }

            }
        });

        ll_file_type_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (file_type_status) {
                    case 1:    //类型正序
                        if (type == 0) {
                            sortType(true, fileListAdapter.getArrayList(), 0);
                            iv_file_type_sort.setImageResource(R.drawable.arrange_down);
                        } else {
                            sortType(true, oldFileListAdapter.getArrayList(), 0);
                            iv_file_type_sort.setImageResource(R.drawable.old_arrange_down);
                        }
                        file_type_status = 2;
                        break;
                    case 2:    //类型倒序
                        if (type == 0) {
                            sortType(false, fileListAdapter.getArrayList(), 0);
                            iv_file_type_sort.setImageResource(R.drawable.arrange_up);
                        } else {
                            sortType(false, oldFileListAdapter.getArrayList(), 0);
                            iv_file_type_sort.setImageResource(R.drawable.old_arrange_up);
                        }
                        file_type_status = 1;
                        break;
                }
                file_time_status = 1;
                file_name_status = 1;
                if (type == 0) {
                    iv_file_name_sort.setImageResource(R.drawable.arrange_none);
                    iv_file_time_sort.setImageResource(R.drawable.arrange_none);
                } else {
                    iv_file_name_sort.setImageResource(R.drawable.old_arrange_none);
                    iv_file_time_sort.setImageResource(R.drawable.old_arrange_none);
                }
            }
        });

        ll_file_name_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (file_name_status) {
                    case 1:   //名称倒序
                        if (type == 0) {
                            sortName(true, fileListAdapter.getArrayList(), 0);
                            iv_file_name_sort.setImageResource(R.drawable.arrange_down);
                        } else {
                            sortName(true, oldFileListAdapter.getArrayList(), 0);
                            iv_file_name_sort.setImageResource(R.drawable.old_arrange_down);
                        }
                        file_name_status = 2;
                        break;
                    case 2:     //名称正序
                        if (type == 0) {
                            sortName(false, fileListAdapter.getArrayList(), 0);
                            iv_file_name_sort.setImageResource(R.drawable.arrange_up);
                        } else {
                            sortName(false, oldFileListAdapter.getArrayList(), 0);
                            iv_file_name_sort.setImageResource(R.drawable.old_arrange_up);
                        }
                        file_name_status = 1;
                        break;
                }
                file_time_status = 1;
                file_type_status = 1;
                if (type == 0) {
                    iv_file_time_sort.setImageResource(R.drawable.arrange_none);
                    iv_file_type_sort.setImageResource(R.drawable.arrange_none);
                } else {
                    iv_file_time_sort.setImageResource(R.drawable.old_arrange_none);
                    iv_file_type_sort.setImageResource(R.drawable.old_arrange_none);
                }
            }
        });

        LinearLayout popup_media_title_layout = (LinearLayout) contentView.findViewById(R.id.popup_media_title_layout);
        media_tv_class = (TextView) popup_media_title_layout.findViewById(R.id.tv_class);
        media_tv_admin = (TextView) popup_media_title_layout.findViewById(R.id.tv_admin);
        media_classifi = (LinearLayout) popup_media_title_layout.findViewById(R.id.ll_classifi);
        media_tv_admin.setBackgroundResource(R.color.nothing);
        if (type == 0) {
            media_tv_class.setTextAppearance(activity, R.style.tv_class_checked);
        } else {
            media_tv_class.setTextAppearance(activity, R.style.old_tv_class_checked);
            media_tv_admin.setTextAppearance(activity, R.style.old_tv_class_no_checked);
        }

        iv_media_time_sort = (ImageView) popup_media_title_layout.findViewById(R.id.iv_time_sort);
        iv_media_type_sort = (ImageView) popup_media_title_layout.findViewById(R.id.iv_type_sort);
        iv_media_name_sort = (ImageView) popup_media_title_layout.findViewById(R.id.iv_name_sort);
        LinearLayout ll_media_time_sort = (LinearLayout) popup_media_title_layout.findViewById(R.id.ll_time_sort);
        LinearLayout ll_media_type_sort = (LinearLayout) popup_media_title_layout.findViewById(R.id.ll_type_sort);
        LinearLayout ll_media_name_sort = (LinearLayout) popup_media_title_layout.findViewById(R.id.ll_name_sort);

        ll_media_time_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (media_time_status) {
                    case 1:  //时间正序
                        if (type == 0) {
                            sortTime(false, mediaListAdapter.getArrayList(), 1);
                            iv_media_time_sort.setImageResource(R.drawable.arrange_down);
                        } else {
                            sortTime(false, oldMediaListAdapter.getArrayList(), 1);
                            iv_media_time_sort.setImageResource(R.drawable.old_arrange_down);
                        }
                        media_time_status = 2;
                        break;
                    case 2:   //时间倒序
                        if (type == 0) {
                            sortTime(true, mediaListAdapter.getArrayList(), 1);
                            iv_media_time_sort.setImageResource(R.drawable.arrange_up);
                        } else {
                            sortTime(true, oldMediaListAdapter.getArrayList(), 1);
                            iv_media_time_sort.setImageResource(R.drawable.old_arrange_up);
                        }
                        media_time_status = 1;
                        break;
                }
                media_type_status = 1;
                media_name_status = 1;

                if (type == 0) {
                    iv_media_name_sort.setImageResource(R.drawable.arrange_none);
                    iv_media_type_sort.setImageResource(R.drawable.arrange_none);
                } else {
                    iv_media_name_sort.setImageResource(R.drawable.old_arrange_none);
                    iv_media_type_sort.setImageResource(R.drawable.old_arrange_none);
                }
            }
        });

        ll_media_type_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (media_type_status) {
                    case 1:    //类型正序
                        if (type == 0) {
                            sortType(true, mediaListAdapter.getArrayList(), 1);
                            iv_media_type_sort.setImageResource(R.drawable.arrange_down);
                        } else {
                            sortType(true, oldMediaListAdapter.getArrayList(), 1);
                            iv_media_type_sort.setImageResource(R.drawable.old_arrange_down);
                        }
                        media_type_status = 2;
                        break;
                    case 2:    //类型倒序
                        if (type == 0) {
                            sortType(false, mediaListAdapter.getArrayList(), 1);
                            iv_media_type_sort.setImageResource(R.drawable.arrange_up);
                        } else {
                            sortType(false, oldMediaListAdapter.getArrayList(), 1);
                            iv_media_type_sort.setImageResource(R.drawable.old_arrange_up);
                        }
                        media_type_status = 1;
                        break;
                }
                media_time_status = 1;
                media_name_status = 1;
                if (type == 0) {
                    iv_media_name_sort.setImageResource(R.drawable.arrange_none);
                    iv_media_time_sort.setImageResource(R.drawable.arrange_none);
                } else {
                    iv_media_name_sort.setImageResource(R.drawable.old_arrange_none);
                    iv_media_time_sort.setImageResource(R.drawable.old_arrange_none);
                }

            }
        });

        ll_media_name_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (media_name_status) {
                    case 1:   //名称倒序
                        if (type == 0) {
                            sortName(true, mediaListAdapter.getArrayList(), 1);
                            iv_media_name_sort.setImageResource(R.drawable.arrange_down);
                        } else {
                            sortName(true, oldMediaListAdapter.getArrayList(), 1);
                            iv_media_name_sort.setImageResource(R.drawable.old_arrange_down);
                        }
                        media_name_status = 2;
                        break;
                    case 2:     //名称正序
                        if (type == 0) {
                            sortName(false, mediaListAdapter.getArrayList(), 1);
                            iv_media_name_sort.setImageResource(R.drawable.arrange_up);
                        } else {
                            sortName(false, oldMediaListAdapter.getArrayList(), 1);
                            iv_media_name_sort.setImageResource(R.drawable.old_arrange_up);
                        }
                        media_name_status = 1;
                        break;
                }
                media_time_status = 1;
                media_type_status = 1;
                if (type == 0) {
                    iv_media_time_sort.setImageResource(R.drawable.arrange_none);
                    iv_media_type_sort.setImageResource(R.drawable.arrange_none);
                } else {
                    iv_media_time_sort.setImageResource(R.drawable.old_arrange_none);
                    iv_media_type_sort.setImageResource(R.drawable.old_arrange_none);
                }
            }
        });

        lv_course_data = (ListView) contentView.findViewById(R.id.lv_course_data);
        lv_media_data = (ListView) contentView.findViewById(R.id.lv_media_data);

        tv_popup_title = (TextView) contentView.findViewById(R.id.tv_popup_title);

        contentView.findViewById(R.id.iv_popup_close).setOnClickListener(this);
        contentView.findViewById(R.id.popup_take_photo).setOnClickListener(this);
        contentView.findViewById(R.id.popup_choose_photo).setOnClickListener(this);

        file_tv_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popup_click != null) {
                    popup_click.choose_class_documents();
                    isFlageFile = false;
                    if (file_tv_class != null && file_tv_admin != null) {
                        if (type == 0) {
                            file_tv_class.setTextAppearance(activity, R.style.tv_class_checked);
                            file_tv_class.setBackgroundResource(R.drawable.file_pressed_backgroud);
                            file_tv_admin.setBackgroundResource(R.color.nothing);
                            file_tv_admin.setTextAppearance(activity, R.style.tv_class_no_checked);
                        } else {
                            file_tv_class.setTextAppearance(activity, R.style.old_tv_class_checked);
                            file_tv_class.setBackgroundResource(R.drawable.old_file_pressed_backgroud);
                            file_tv_admin.setBackgroundResource(R.color.nothing);
                            file_tv_admin.setTextAppearance(activity, R.style.old_tv_class_no_checked);
                        }
                    }
                }
            }
        });

        file_tv_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popup_click != null) {
                    popup_click.choose_admin_documents();
                    isFlageFile = true;
                    if (type == 0) {
                        fileListAdapter.isPublicDocuments(true);
                        sortTime(false, fileListAdapter.getArrayList(), 0);
                    } else {
                        oldFileListAdapter.isPublicDocuments(true);
                        sortTime(false, oldFileListAdapter.getArrayList(), 0);
                    }
                    if (file_tv_class != null && file_tv_admin != null) {
                        if (type == 0) {
                            file_tv_admin.setTextAppearance(activity, R.style.tv_class_checked);
                            file_tv_admin.setBackgroundResource(R.drawable.admin_pressed_backgroud);
                            file_tv_class.setBackgroundResource(R.color.nothing);
                            file_tv_class.setTextAppearance(activity, R.style.tv_class_no_checked);
                        } else {
                            file_tv_admin.setTextAppearance(activity, R.style.old_tv_class_checked);
                            file_tv_admin.setBackgroundResource(R.drawable.old_admin_pressed_backgroud);
                            file_tv_class.setBackgroundResource(R.color.nothing);
                            file_tv_class.setTextAppearance(activity, R.style.old_tv_class_no_checked);
                        }
                    }
                }
            }
        });

        media_tv_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popup_click != null) {
                    isFlageMedia = false;
                    popup_click.choose_class_media();
                    if (media_tv_class != null && media_tv_admin != null) {
                        if (type == 0) {
                            media_tv_class.setTextAppearance(activity, R.style.tv_class_checked);
                            media_tv_class.setBackgroundResource(R.drawable.file_pressed_backgroud);
                            media_tv_admin.setBackgroundResource(R.color.nothing);
                            media_tv_admin.setTextAppearance(activity, R.style.tv_class_no_checked);
                        } else {
                            media_tv_class.setTextAppearance(activity, R.style.old_tv_class_checked);
                            media_tv_class.setBackgroundResource(R.drawable.old_file_pressed_backgroud);
                            media_tv_admin.setBackgroundResource(R.color.nothing);
                            media_tv_admin.setTextAppearance(activity, R.style.old_tv_class_no_checked);
                        }
                    }
                }
            }
        });

        media_tv_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popup_click != null) {
                    isFlageMedia = true;
                    popup_click.choose_admin_media();

                    if (type == 0) {
                        sortTime(false, mediaListAdapter.getArrayList(), 0);
                        mediaListAdapter.isPublicDocuments(true);
                    } else {
                        oldMediaListAdapter.isPublicDocuments(true);
                        sortTime(false, oldMediaListAdapter.getArrayList(), 0);
                    }

                    if (media_tv_class != null && media_tv_admin != null) {
                        if (type == 0) {
                            media_tv_class.setTextAppearance(activity, R.style.tv_class_no_checked);
                            media_tv_class.setBackgroundResource(R.color.nothing);
                            media_tv_admin.setTextAppearance(activity, R.style.tv_class_checked);
                            media_tv_admin.setBackgroundResource(R.drawable.admin_pressed_backgroud);
                        } else {
                            media_tv_admin.setTextAppearance(activity, R.style.old_tv_class_checked);
                            media_tv_admin.setBackgroundResource(R.drawable.old_admin_pressed_backgroud);
                            media_tv_class.setBackgroundResource(R.color.nothing);
                            media_tv_class.setTextAppearance(activity, R.style.old_tv_class_no_checked);
                        }
                    }
                }
            }
        });
        popup_window_view = contentView;
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
     */
    public void showCoursePopupWindow(View view, final View cb_view, int width, int height, int type, boolean padLeft) {

        if (popup_window_view == null) {
            return;
        }

        popupWindowCourse = new PopupWindow(width, height);
        popupWindowCourse.setContentView(popup_window_view);

        if (type == 0) {
            lv_media_data.setAdapter(mediaListAdapter);
            lv_course_data.setAdapter(fileListAdapter);
        } else {
            lv_media_data.setAdapter(oldMediaListAdapter);
            lv_course_data.setAdapter(oldFileListAdapter);
        }

        if (RoomControler.isDocumentClassification()) {
            file_classifi.setVisibility(View.VISIBLE);
            media_classifi.setVisibility(View.VISIBLE);
            if (type == 0) {
                //fileListAdapter.setArrayList(WhiteBoradManager.getInstance().getClassDocList());
                //mediaListAdapter.setArrayList(WhiteBoradManager.getInstance().getClassMediaList());
                if (!isFlageFile) {
                    popup_click.choose_class_documents();
                } else {
                    popup_click.choose_admin_documents();
                }

                if (!isFlageMedia) {
                    popup_click.choose_class_media();
                } else {
                    popup_click.choose_admin_media();
                }
            } else {

                if (!isFlageFile) {
                    oldFileListAdapter.setArrayList(WhiteBoradManager.getInstance().getClassDocList());
                } else {
                    oldFileListAdapter.setArrayList(WhiteBoradManager.getInstance().getAdminDocList());
                }

                oldFileListAdapter.notifyDataSetChanged();

                if (!isFlageMedia) {
                    oldMediaListAdapter.setArrayList(WhiteBoradManager.getInstance().getClassMediaList());
                } else {
                    oldMediaListAdapter.setArrayList(WhiteBoradManager.getInstance().getAdminmMediaList());
                }

//                oldFileListAdapter.setArrayList(WhiteBoradManager.getInstance().getClassDocList());
//                oldMediaListAdapter.setArrayList(WhiteBoradManager.getInstance().getClassMediaList());

                oldMediaListAdapter.notifyDataSetChanged();
            }

        } else {
            file_classifi.setVisibility(View.INVISIBLE);
            media_classifi.setVisibility(View.INVISIBLE);
            if (type == 0) {
                fileListAdapter.setArrayList(WhiteBoradManager.getInstance().getDocList());
                mediaListAdapter.setArrayList(WhiteBoradManager.getInstance().getMediaList());
            } else {
                oldFileListAdapter.setArrayList(WhiteBoradManager.getInstance().getDocList());
                oldMediaListAdapter.setArrayList(WhiteBoradManager.getInstance().getMediaList());
            }
        }

        rb_course_library.setChecked(true);

        if (type == 0) {
            sortTime(false, fileListAdapter.getArrayList(), 0);
            iv_file_time_sort.setImageResource(R.drawable.arrange_down);
            iv_file_name_sort.setImageResource(R.drawable.arrange_none);
            iv_file_type_sort.setImageResource(R.drawable.arrange_none);
        } else {
            sortTime(false, oldFileListAdapter.getArrayList(), 0);
            iv_file_time_sort.setImageResource(R.drawable.old_arrange_down);
            iv_file_name_sort.setImageResource(R.drawable.old_arrange_none);
            iv_file_type_sort.setImageResource(R.drawable.old_arrange_none);
        }

        file_time_status = 2;

        popupWindowCourse.setBackgroundDrawable(new BitmapDrawable());
        popupWindowCourse.setFocusable(false);
        popupWindowCourse.setOutsideTouchable(true);


        popupWindowCourse.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popup_click != null) {
                    if (!isInView) {
                        popup_click.close_window();
                    }
                }
            }
        });

        popupWindowCourse.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isInView = Tools.isInView(event, cb_view);
                return false;
            }
        });

        if (type == 0) {
            fileListAdapter.setPop(popupWindowCourse);
            mediaListAdapter.setPop(popupWindowCourse);
        } else {
            oldFileListAdapter.setPop(popupWindowCourse);
            oldMediaListAdapter.setPop(popupWindowCourse);
        }

        int[] reb_wb_board = new int[2];
        view.getLocationInWindow(reb_wb_board);

        //popupwindow基于屏幕左上角位移到给定view中心的偏移量
        int x = 0;
        if (padLeft) {
            x = Math.abs(view.getWidth() - popupWindowCourse.getWidth()) / 2 + FullScreenTools.getStatusBarHeight(activity);
        } else {
            x = Math.abs(view.getWidth() - popupWindowCourse.getWidth()) / 2;
        }
        int y = Math.abs(reb_wb_board[1] + view.getHeight() / 2 - popupWindowCourse.getHeight() / 2);
        popupWindowCourse.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
    }

    public void dismissPopupWindow() {
        if (popupWindowCourse != null) {
            popupWindowCourse.dismiss();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_course_library) {
            ll_course_list.setVisibility(View.VISIBLE);
            ll_media_list.setVisibility(View.GONE);
            if (type == 0) {
                rb_course_library.setTextAppearance(activity, R.style.rb_course_left_checked);
                rb_media.setTextAppearance(activity, R.style.rb_course_left_no_checked);
            } else {
                rb_course_library.setTextAppearance(activity, R.style.old_rb_course_left_checked);
                rb_media.setTextAppearance(activity, R.style.old_rb_course_left_no_checked);
            }
            tv_popup_title.setText(activity.getString(R.string.doclist) + "（" +
                    WhiteBoradManager.getInstance().getDocList().size() + "）");

        } else if (checkedId == R.id.rb_media) {
            ll_course_list.setVisibility(View.GONE);
            ll_media_list.setVisibility(View.VISIBLE);

            if (type == 0) {
                rb_course_library.setTextAppearance(activity, R.style.rb_course_left_no_checked);
                rb_media.setTextAppearance(activity, R.style.rb_course_left_checked);
            } else {
                rb_course_library.setTextAppearance(activity, R.style.old_rb_course_left_no_checked);
                rb_media.setTextAppearance(activity, R.style.old_rb_course_left_checked);
            }
            tv_popup_title.setText(activity.getString(R.string.medialist) + "（" +
                    WhiteBoradManager.getInstance().getMediaList().size() + "）");

            if (type == 0) {
                sortTime(false, mediaListAdapter.getArrayList(), 1);
                iv_media_time_sort.setImageResource(R.drawable.arrange_down);
                iv_media_name_sort.setImageResource(R.drawable.arrange_none);
                iv_media_type_sort.setImageResource(R.drawable.arrange_none);
            } else {
                sortTime(false, oldMediaListAdapter.getArrayList(), 1);
                iv_media_time_sort.setImageResource(R.drawable.old_arrange_down);
                iv_media_name_sort.setImageResource(R.drawable.old_arrange_none);
                iv_media_type_sort.setImageResource(R.drawable.old_arrange_none);
            }
            media_time_status = 2;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_popup_close) {
            if (popupWindowCourse != null) {
                popupWindowCourse.dismiss();
                if (popup_click != null) {
                    popup_click.close_window();
                }
            }
        } else if (v.getId() == R.id.popup_take_photo) {
            if (popup_click != null) {
                popup_click.take_photo();
            }
        } else if (v.getId() == R.id.popup_choose_photo) {
            if (popup_click != null) {
                popup_click.choose_photo();
            }
        }
    }

    /**
     * 定义popupwindow的接口，通过接口和activity进行通信
     */
    public interface PopupWindowClick {
        void close_window();

        void take_photo();

        void choose_photo();

        void choose_class_documents();

        void choose_admin_documents();

        void choose_class_media();

        void choose_admin_media();
    }

    private void sortTime(Boolean isup, ArrayList<ShareDoc> arrayList, int type1) {
        ShareDoc shareDoc = null;
        for (int x = arrayList.size() - 1; x >= 0; x--) {
            if (arrayList.get(x).getFileid() == 0) {
                shareDoc = arrayList.get(x);
                arrayList.remove(x);
            }
        }
        TimeComparator timeComparator = new TimeComparator();
        timeComparator.setisUp(isup);
        Collections.sort(arrayList, timeComparator);
        if (shareDoc != null) {
            arrayList.add(0, shareDoc);
        }
        if (type1 == 0) {
            if (type == 0) {
                fileListAdapter.setArrayList(arrayList);
            } else {
                oldFileListAdapter.setArrayList(arrayList);
            }
        } else {
            if (type == 0) {
                mediaListAdapter.setArrayList(arrayList);
            } else {
                oldMediaListAdapter.setArrayList(arrayList);
            }
        }
    }

    private void sortName(Boolean isup, ArrayList<ShareDoc> arrayList, int type1) {
        ShareDoc shareDoc = null;
        for (int x = arrayList.size() - 1; x >= 0; x--) {
            if (arrayList.get(x).getFileid() == 0) {
                shareDoc = arrayList.get(x);
                arrayList.remove(x);
            }
        }
        NameComparator nameComparator = new NameComparator();
        nameComparator.setisUp(isup);
        Collections.sort(arrayList, nameComparator);
        if (shareDoc != null) {
            arrayList.add(0, shareDoc);
        }
        if (type1 == 0) {
            if (type == 0) {
                fileListAdapter.setArrayList(arrayList);
            } else {
                oldFileListAdapter.setArrayList(arrayList);
            }
        } else {
            if (type == 0) {
                mediaListAdapter.setArrayList(arrayList);
            } else {
                oldMediaListAdapter.setArrayList(arrayList);
            }
        }
    }

    private void sortType(Boolean isup, ArrayList<ShareDoc> arrayList, int type1) {
        ShareDoc shareDoc = null;
        for (int x = arrayList.size() - 1; x >= 0; x--) {
            if (arrayList.get(x).getFileid() == 0) {
                shareDoc = arrayList.get(x);
                arrayList.remove(x);
            }
        }
        TypeComparator typeComparator = new TypeComparator();
        typeComparator.setisUp(isup);
        Collections.sort(arrayList, typeComparator);
        if (shareDoc != null) {
            arrayList.add(0, shareDoc);
        }
        if (type1 == 0) {
            if (type == 0) {
                fileListAdapter.setArrayList(arrayList);
            } else {
                oldFileListAdapter.setArrayList(arrayList);
            }
        } else {
            if (type == 0) {
                mediaListAdapter.setArrayList(arrayList);
            } else {
                oldMediaListAdapter.setArrayList(arrayList);
            }
        }
    }
}
