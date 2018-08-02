package com.eduhdsdk.viewutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.eduhdsdk.R;
import com.eduhdsdk.adapter.ChatListAdapter;
import com.eduhdsdk.adapter.FaceGVAdapter;
import com.eduhdsdk.entity.ChatData;
import com.eduhdsdk.interfaces.TranslateCallback;
import com.eduhdsdk.message.RoomSession;
import com.eduhdsdk.tools.KeyBoardUtil;
import com.eduhdsdk.tools.ScreenScale;
import com.eduhdsdk.tools.SoftKeyBoardListener;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.tools.Translate;
import com.eduhdsdk.ui.BasePopupWindow;
import com.eduhdsdk.ui.MyIm;
import com.talkcloud.roomsdk.RoomUser;
import com.talkcloud.roomsdk.TKRoomManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2018/4/23/023.
 */

public class ChatWindowPop implements TranslateCallback {

    private String TAG = "ChatWindowPop";
    private Activity activity;
    private ChatPopupWindowClick chatPopupWindowClick;
    private PopupWindow popupWindow;
    private PopupWindow chatWindow;
    private EditText edt_input_small_window;
    private TextView edt_input;
    private List<String> staticFacesList;
    private ArrayList<ChatData> msgList = new ArrayList<ChatData>();
    private ArrayList<ChatData> chatList;
    private ChatListAdapter chlistAdapter;
    private ListView lv_chat;
    private ImageView iv_title_chat;
    private TextView tv_title_chat;

    //缓存聊天框中输入的内容，每次发送消息后置空
    private String edt_input_content = "";
    //默认用户可以发言
    private boolean is_forbid_chat = false;

    /**
     * 判断是否要隐藏输入小弹框,
     */
    private boolean is_hide_chat_input_popup = true;

    private View contentView;

    public ChatWindowPop(Activity activity, ArrayList<ChatData> chatList, View contentView) {
        this.activity = activity;
        this.chatList = chatList;
        chlistAdapter = new ChatListAdapter(chatList, activity);
        initStaticFaces();
        this.contentView = contentView;

        Translate.getInstance().setCallback(this);

        SoftKeyBoardListener.setListener(activity, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //键盘显示
            }

            @Override
            public void keyBoardHide(int height) {
                //键盘隐藏
            }
        });
    }

    private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            staticFacesList.clear();
            String[] faces = activity.getAssets().list("face");
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setChatPopupWindowClick(ChatWindowPop.ChatPopupWindowClick chatPopupWindowClick) {
        this.chatPopupWindowClick = chatPopupWindowClick;
    }

    public ChatListAdapter getChatListAdapter() {
        return this.chlistAdapter;
    }


    //判断弹框弹出时，用户的点击是在底部控件的内部还是外部
    boolean isInView = true;

    public void showChatPopupWindow(final int width, final int height, final View view, final View cb_view) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.layout_chat_pop, null);
        ScreenScale.scaleView(contentView, "ChatWindowPop");

        popupWindow = new BasePopupWindow(activity);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);

        ImageView im_close_chat = (ImageView) contentView.findViewById(R.id.iv_popup_close);
        TextView txt_send = (TextView) contentView.findViewById(R.id.txt_send);
        lv_chat = (ListView) contentView.findViewById(R.id.lv_chat);
        LinearLayout view_chat_input = (LinearLayout) contentView.findViewById(R.id.view_chat_input);
        TextView tv_popup_title = (TextView) contentView.findViewById(R.id.tv_popup_title);

        LinearLayout ll_put = (LinearLayout) contentView.findViewById(R.id.ll_put);

        LinearLayout ll_title_chat = (LinearLayout) contentView.findViewById(R.id.ll_title_chat);
        iv_title_chat = (ImageView) contentView.findViewById(R.id.iv_title_chat);
        tv_title_chat = (TextView) contentView.findViewById(R.id.tv_title_chat);

        if (TKRoomManager.getInstance().getMySelf().role == 0) {
            ll_title_chat.setVisibility(View.VISIBLE);
        } else {
            ll_title_chat.setVisibility(View.GONE);
        }

        setTitelPrompt();

        ll_title_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] roles = {2};
                HashMap<String, Object> property = new HashMap<>();
                try {
                    JSONObject data = new JSONObject();
                    if (RoomSession._possibleSpeak) {
                        data.put("isAllBanSpeak", true);
                        TKRoomManager.getInstance().pubMsg("EveryoneBanChat", "EveryoneBanChat", "__all",
                                data.toString(), true, null, null);
                        property.put("disablechat", true);
                        TKRoomManager.getInstance().changeUserPropertyByRole(roles, "__all", property);
                    } else {
                        data.put("isAllBanSpeak", false);
                        TKRoomManager.getInstance().delMsg("EveryoneBanChat", "EveryoneBanChat", "__all", data.toString());
                        property.put("disablechat", false);
                        TKRoomManager.getInstance().changeUserPropertyByRole(roles, "__all", property);
                    }
                    RoomSession._possibleSpeak = !RoomSession._possibleSpeak;
                    setTitelPrompt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (TKRoomManager.getInstance().getMySelf().role == 4) {
            ll_put.setVisibility(View.GONE);
        }

        tv_popup_title.setText(activity.getString(R.string.chat));

        final ImageView iv_chat = (ImageView) view_chat_input.findViewById(R.id.iv_chat);

        edt_input = (TextView) view_chat_input.findViewById(R.id.edt_input_chat);
        lv_chat.setAdapter(chlistAdapter);

        lv_chat.setSelection(chlistAdapter.getCount() - 1);

        if (TKRoomManager.getInstance().getMySelf().properties.containsKey("disablechat")) {
            is_forbid_chat = (boolean) TKRoomManager.getInstance().getMySelf().properties.get("disablechat");
        }

        /*if (RoomSession._possibleSpeak) {
            if (is_forbid_chat) {
                edt_input.setHint(R.string.no_say_something);
            } else {
                edt_input.setHint(R.string.say_something);
            }
        } else {
            edt_input.setHint(R.string.no_say_something);
        }*/

        if (is_forbid_chat) {
            edt_input.setHint(R.string.no_say_something);
            edt_input.setEnabled(false);
        } else {
            edt_input.setHint(R.string.say_something);
            edt_input.setEnabled(true);
        }

        edt_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (RoomSession._possibleSpeak) {
                    if (TKRoomManager.getInstance().getMySelf().role != 4 && !is_forbid_chat) {
                        showChatInputPopupWindow(view, true);
                    }
                }*/

                if (TKRoomManager.getInstance().getMySelf().role != 4 && !is_forbid_chat) {
                    showChatInputPopupWindow(view, true);
                }

            }
        });

        iv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (is_forbid_chat) {
                        Toast.makeText(activity, R.string.the_user_is_forbid_speak, Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (TKRoomManager.getInstance().getMySelf().role != 4 && !is_forbid_chat) {
                    showChatInputPopupWindow(view, false);
                }
            }
        });

        im_close_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    if (chatPopupWindowClick != null) {
                        chatPopupWindowClick.close_chat_window();
                    }
                }
            }
        });

        txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edt_input.getText().toString().trim();
                if (msg != null && !msg.isEmpty() && TKRoomManager.getInstance().getMySelf().role != 4) {
                    boolean isSend = false;
                    if (chatList.size() > 0) {
                        msgList.clear();
                        for (int x = 0; x < chatList.size(); x++) {
                            msgList.add(chatList.get(x));
                        }
                        Collections.sort(msgList, Collections.reverseOrder());
                        for (int x = 0; x < msgList.size(); x++) {
                            if(msgList.get(x).getUser()!=null){
                                if (msgList.get(x).getUser().peerId.equals(TKRoomManager.getInstance().getMySelf().peerId)) {
                                    if(!TextUtils.isEmpty(msgList.get(x).getMessage())){
                                        if (msgList.get(x).getMessage().equals(msg) && System.currentTimeMillis() -
                                                msgList.get(x).getMsgTime() <= 10 * 60) {
                                            isSend = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    edt_input.setText("");
                    edt_input_content = "";
                    if (isSend) {
                        Toast.makeText(activity, activity.getString(R.string.chat_hint), Toast.LENGTH_SHORT).show();
                    } else {
                        TKRoomManager.getInstance().sendMessage(msg, 0);
                    }
                }
            }
        });
        popupWindow.setContentView(contentView);

        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);

        int[] reb_wb_board = new int[2];
        view.getLocationInWindow(reb_wb_board);

        //popupwindow基于屏幕左上角位移到给定view中心的偏移量
        int x = Math.abs(view.getWidth() - popupWindow.getWidth()) / 2;
        int y = Math.abs(reb_wb_board[1] + view.getHeight() / 2 - popupWindow.getHeight() / 2);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!isInView) {
                    chatPopupWindowClick.close_chat_window();
                }
            }
        });

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isInView = Tools.isInView(event, cb_view);
                return false;
            }
        });
    }

    public void setTitelPrompt() {
        if (iv_title_chat != null && tv_title_chat != null) {
            if (RoomSession._possibleSpeak) {
                iv_title_chat.setImageResource(R.drawable.icon_shutup_default);
                tv_title_chat.setText(activity.getString(R.string.popup_title_chat));
            } else {
                iv_title_chat.setImageResource(R.drawable.icon_shutup_press);
                tv_title_chat.setText(activity.getString(R.string.popup_title_chat_cancel));
            }
        }
    }

    public boolean popupIsShow() {
        if (popupWindow == null) {
            return false;
        } else {
            return popupWindow.isShowing();
        }
    }

    public void setEdtInputHint() {

        if (TKRoomManager.getInstance().getMySelf().properties.containsKey("disablechat")) {
            is_forbid_chat = (boolean) TKRoomManager.getInstance().getMySelf().properties.get("disablechat");
        }

        if (is_forbid_chat) {
            if (edt_input != null) {
                edt_input.setHint(R.string.no_say_something);
                edt_input.setEnabled(false);
            }
        } else {
            if (edt_input != null) {
                edt_input.setHint(R.string.say_something);
                edt_input.setEnabled(true);
            }
        }

        /*if (RoomSession._possibleSpeak) {
            edt_input.setHint(R.string.say_something);
            edt_input.setEnabled(true);
        } else {
            edt_input.setHint(R.string.no_say_something);
            edt_input.setEnabled(false);
        }*/
    }

    private void delete() {
        if (edt_input_small_window.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(edt_input_small_window.getText());
            int iCursorStart = Selection.getSelectionStart(edt_input_small_window.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "[em_1]";
                        ((Editable) edt_input_small_window.getText()).delete(iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        ((Editable) edt_input_small_window.getText()).delete(iCursorEnd - 1, iCursorEnd);
                    }
                } else {
                    ((Editable) edt_input_small_window.getText()).delete(iCursorStart, iCursorEnd);
                }
            }
        }
    }

    private boolean isDeletePng(int cursor) {
        String st = "[em_1]";
        String content = edt_input_small_window.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(),
                    content.length());
            String regex = "(\\[em_)\\d{1}(\\])";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    private void insert(CharSequence text) {
        if (edt_input_small_window == null) {
            return;
        }
        int iCursorStart = Selection.getSelectionStart((edt_input_small_window.getText()));
        int iCursorEnd = Selection.getSelectionEnd((edt_input_small_window.getText()));
        if (iCursorStart != iCursorEnd) {
            ((Editable) edt_input_small_window.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((edt_input_small_window.getText()));
        ((Editable) edt_input_small_window.getText()).insert(iCursor, text);
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            String[] splitText = png.split("\\.");
            String tempText = "[" + splitText[0] + "]";
            Bitmap bitmap = BitmapFactory.decodeStream(activity.getAssets().open("face/" + png));
           /* Drawable drawable = new BitmapDrawable(bitmap);
            drawable.setBounds(0, 0, 50, 50);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);*/
            MyIm imageSpan = new MyIm(activity, bitmap);
            sb.append(tempText);
            sb.setSpan(imageSpan, sb.length() - tempText.length(), sb.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    public void dismissPopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private PopupWindow chat_input_popup;

    /**
     * 显示软键盘上方一小条输入栏
     *
     * @param view
     * @param is_show_broad true 显示键盘   false 显示表情
     */
    public void showChatInputPopupWindow(final View view, boolean is_show_broad) {

        if (chat_input_popup != null && chat_input_popup.isShowing()) {
            chat_input_popup.dismiss();
            chat_input_popup = null;
        }

        final View contentView = LayoutInflater.from(activity).inflate(R.layout.layout_chat_message_edit_input, null);
        ScreenScale.scaleView(contentView, "ChatInput");

        chat_input_popup = new PopupWindow(activity);
        chat_input_popup.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        chat_input_popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView txt_send = (TextView) contentView.findViewById(R.id.txt_send);
        final ImageView iv_chat = (ImageView) contentView.findViewById(R.id.iv_chat);
        final ImageView iv_broad = (ImageView) contentView.findViewById(R.id.iv_broad);
        edt_input_small_window = (EditText) contentView.findViewById(R.id.edt_input_chat);

        final GridView chart_face_gv = (GridView) contentView.findViewById(R.id.chart_face_gv);

        FaceGVAdapter mGvAdapter = new FaceGVAdapter(staticFacesList, activity);
        chart_face_gv.setAdapter(mGvAdapter);
        chart_face_gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        chart_face_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position == 41) {
                        delete();
                    }
                    if (position < 8) {
                        insert(getFace(staticFacesList.get(position)));
                    }
                    chatWindow.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        iv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示表情的时候，设置不可获取焦点，否则会出现表情框与键盘同时存在的情况
                edt_input_small_window.setFocusable(false);
                chart_face_gv.setVisibility(View.VISIBLE);
                iv_chat.setVisibility(View.GONE);
                iv_broad.setVisibility(View.VISIBLE);
                KeyBoardUtil.hideKeyBoard(activity, edt_input_small_window);
            }
        });

        iv_broad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户想要弹出键盘的时候，让edittext继续获得焦点
                edt_input_small_window.setFocusableInTouchMode(true);
                chart_face_gv.setVisibility(View.GONE);
                iv_chat.setVisibility(View.VISIBLE);
                iv_broad.setVisibility(View.GONE);
                KeyBoardUtil.showKeyBoard(activity, edt_input_small_window);
            }
        });

        txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edt_input_small_window.getText().toString().trim();

                if (msg != null && !msg.isEmpty()) {
                    boolean isSend = false;
                    if (chatList.size() > 0) {
                        msgList.clear();
                        for (int x = 0; x < chatList.size(); x++) {
                            msgList.add(chatList.get(x));
                        }
                        Collections.sort(msgList, Collections.reverseOrder());
                        for (int x = 0; x < msgList.size(); x++) {
                            if (msgList.get(x).getUser() != null) {
                                if (msgList.get(x).getUser().peerId.equals(TKRoomManager.getInstance().getMySelf().peerId) &&
                                        msgList.get(x).getMessage() != null) {
                                    if (msgList.get(x).getMessage().equals(msg) && System.currentTimeMillis() - msgList.get(x).getMsgTime() <= 10 * 60) {
                                        isSend = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    edt_input_small_window.setText("");
                    edt_input_content = "";
                    if (isSend) {
                        Toast.makeText(activity, activity.getString(R.string.chat_hint), Toast.LENGTH_SHORT).show();
                    } else {
                        TKRoomManager.getInstance().sendMessage(msg, 0);
                    }
                    chat_input_popup.dismiss();
                }
            }
        });

        edt_input_small_window.setText(edt_input_content);

        chat_input_popup.setContentView(contentView);
        chat_input_popup.setBackgroundDrawable(null);

        chat_input_popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        chat_input_popup.setFocusable(true);
        chat_input_popup.setBackgroundDrawable(new BitmapDrawable());
        chat_input_popup.setOutsideTouchable(false);
        chat_input_popup.setTouchable(true);

        chat_input_popup.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        chat_input_popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                edt_input_content = edt_input_small_window.getText().toString();
                edt_input.setText(edt_input_content);
                if (SoftKeyBoardListener.isSoftShowing(activity)) {
                    KeyBoardUtil.showKeyBoard(activity, edt_input_small_window);
                }
            }
        });

        if (!is_show_broad) {
            //显示表情的时候，设置不可获取焦点，否则会出现表情框与键盘同时存在的情况
            edt_input_small_window.setFocusable(false);
            chart_face_gv.setVisibility(View.VISIBLE);
            iv_chat.setVisibility(View.GONE);
            iv_broad.setVisibility(View.VISIBLE);
            KeyBoardUtil.hideKeyBoard(activity, edt_input_small_window);
        } else {
            KeyBoardUtil.showKeyBoard(activity, edt_input_small_window);
        }
    }

    @Override
    public void onResult(final int index, final String result) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (RoomSession.chatList.size() > index) {
                    RoomSession.chatList.get(index).setTrans(true);
                    RoomSession.chatList.get(index).setTrans(result);
                    chlistAdapter.setArrayList(RoomSession.chatList);
                    lv_chat.setSelection(index);
                }
            }
        });
    }

    /**
     * 定义popupwindow的接口，通过接口和activity进行通信
     */
    public interface ChatPopupWindowClick {
        void close_chat_window();
    }
}
