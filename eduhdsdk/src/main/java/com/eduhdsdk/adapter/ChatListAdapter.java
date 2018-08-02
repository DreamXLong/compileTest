package com.eduhdsdk.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.classroomsdk.ShareDoc;
import com.eduhdsdk.R;
import com.eduhdsdk.entity.ChatData;
import com.eduhdsdk.interfaces.TranslateCallback;
import com.eduhdsdk.message.RoomSession;
import com.eduhdsdk.tools.Translate;
import com.eduhdsdk.ui.MyIm;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/4/28.
 */

public class ChatListAdapter extends BaseAdapter {

    private ArrayList<ChatData> chatlist;
    private Context context;

    //根据消息类型判断加载那个布局
    private final int CHAT_BG_NORMAL = 0;
    private final int CHAT_BG_SYSTEM = 1;

    public ChatListAdapter(ArrayList<ChatData> chatlist, Context context) {
        this.chatlist = chatlist;
        this.context = context;
    }

    public void setArrayList(ArrayList<ChatData> chatlist) {
        this.chatlist = chatlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return chatlist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public int getItemViewType(int position) {

        if (chatlist.size() > 0) {
            if (chatlist.get(position).isStystemMsg()) {
                return CHAT_BG_SYSTEM;
            } else {
                return CHAT_BG_NORMAL;
            }
        } else {
            return CHAT_BG_NORMAL;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ViewHolderSystem viewHolderSystem = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case CHAT_BG_NORMAL:
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.layout_chat_list_item, null);
                    holder.txt_msg_nickname = (TextView) convertView.findViewById(R.id.txt_msg_nickname);
                    holder.txt_ch_msg = (TextView) convertView.findViewById(R.id.txt_ch_msg);
                    holder.img_translation = (ImageView) convertView.findViewById(R.id.img_translation);
                    holder.txt_ts = (TextView) convertView.findViewById(R.id.txt_ts);
                    holder.txt_eng_msg = (TextView) convertView.findViewById(R.id.txt_eng_msg);
                    holder.view = (View) convertView.findViewById(R.id.view);
                    convertView.setTag(holder);
                    break;

                case CHAT_BG_SYSTEM:
                    viewHolderSystem = new ViewHolderSystem();
                    convertView = LayoutInflater.from(context).inflate(R.layout.layout_system_chat_list_item, null);
                    viewHolderSystem.txt_ch_msg = (TextView) convertView.findViewById(R.id.txt_ch_msg);
                    viewHolderSystem.txt_ts = (TextView) convertView.findViewById(R.id.txt_ts);
                    viewHolderSystem.view = (View) convertView.findViewById(R.id.view);
                    convertView.setTag(viewHolderSystem);
                    break;
            }
        } else {
            switch (type) {
                case CHAT_BG_NORMAL:
                    holder = (ViewHolder) convertView.getTag();
                    break;
                case CHAT_BG_SYSTEM:
                    viewHolderSystem = (ViewHolderSystem) convertView.getTag();
                    break;
            }
        }
        if (chatlist.size() > 0) {
            ChatData ch = chatlist.get(position);
            switch (type) {
                case CHAT_BG_NORMAL:
                    holder.txt_ts.setText(ch.getTime());
                    if (ch != null && ch.getUser() != null) {
                        ch.getUser().nickName = StringEscapeUtils.unescapeHtml4(ch.getUser().nickName);
                        holder.txt_msg_nickname.setText(ch.getUser().nickName + ":");
                        setTranslation(ch, position, holder.txt_ch_msg, holder.txt_eng_msg, holder.img_translation, holder.view, convertView);
                    }
                    break;

                case CHAT_BG_SYSTEM:
                    viewHolderSystem.txt_ts.setText(ch.getTime());
                    if (ch != null) {
                        if (ch.getUser() != null) {
                            if (ch.isStystemMsg()) {
                                if (ch.getState() == 1) {
                                    viewHolderSystem.txt_ch_msg.setText(ch.getUser().nickName + "进入教室");
                                } else {
                                    String strIsHold = ch.isHold() ? context.getString(R.string.back_msg) : context.getString(R.string.re_back_msg);
                                    String temp = (String) ch.getUser().properties.get("devicetype");
                                    strIsHold = temp + strIsHold;
                                    viewHolderSystem.txt_ch_msg.setText(strIsHold);
                                }
                            }
                        } else {
                            if (ch.isStystemMsg()) {
                                viewHolderSystem.txt_ch_msg.setText(ch.getMessage());
                            }
                        }
                    }
                    break;
            }
        }
        return convertView;
    }

    private void setTranslation(final ChatData ch, final int position, TextView txt_ch_msg,
                                TextView txt_eng_msg, ImageView img_translation, View view, View convertView) {

        if (!TextUtils.isEmpty(ch.getMessage())) {
            SpannableStringBuilder sb = getFace(ch.getMessage());
            txt_ch_msg.setText(sb);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(ch.getMessage())) {
                    Translate.getInstance().translate(position, ch.getMessage().replaceAll("(\\[em_)\\d{1}(\\])", ""));
                }
            }
        });

        if (!TextUtils.isEmpty(ch.getTrans())) {
            SpannableStringBuilder sbTrans = getFace(ch.getTrans());
            txt_eng_msg.setText(sbTrans);
        }

        if (ch.isTrans()) {
            //已经被翻译
            img_translation.setImageResource(R.drawable.translation_selected);
            view.setVisibility(View.VISIBLE);
            txt_eng_msg.setVisibility(View.VISIBLE);
        } else {
            //未被翻译
            img_translation.setImageResource(R.drawable.translation_default);
            txt_eng_msg.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }
    }

    private SpannableStringBuilder getFace(String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\[em_)\\d{1}(\\])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            try {
                String png = tempText.substring("[".length(), tempText.length() - "]".length()) + ".png";
                Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open("face/" + png));
               /* Drawable drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0, 0, 30, 30);*/
               /* ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);*/
                MyIm imageSpan = new MyIm(context, bitmap);
                sb.setSpan(imageSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb;
    }

    class ViewHolder {
        TextView txt_msg_nickname, txt_ch_msg, txt_ts, txt_eng_msg;
        ImageView img_translation;
        View view;
    }

    class ViewHolderSystem {
        TextView txt_ch_msg, txt_ts;
        View view;
    }
}
