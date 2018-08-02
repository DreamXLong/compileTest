package com.eduhdsdk.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.classroomsdk.ShareDoc;
import com.classroomsdk.WhiteBoradManager;
import com.eduhdsdk.R;
import com.eduhdsdk.message.RoomSession;
import com.eduhdsdk.tools.ScreenScale;
import com.eduhdsdk.tools.Tools;
import com.talkcloud.roomsdk.TKRoomManager;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Administrator on 2017/5/29.
 */

public class OldMediaListAdapter extends BaseAdapter {

    private Activity activity;
    private String roomNum;
    private long localfileid = -1;
    private PopupWindow pop = null;
    private ArrayList<ShareDoc> arrayList;
    private boolean publicDocument = false;

    public void setPop(PopupWindow pop) {
        this.pop = pop;
    }

    public void setLocalfileid(long localfileid) {
        this.localfileid = localfileid;
    }

    public long getLocalfileid() {
        return localfileid;
    }

    public void isPublicDocuments(boolean b) {
        this.publicDocument = b;
    }

    public OldMediaListAdapter(Activity context, String roomNum) {
        this.activity = context;
        this.roomNum = roomNum;
    }

    public void setArrayList(ArrayList<ShareDoc> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    public ArrayList<ShareDoc> getArrayList() {
        return arrayList;
    }

    @Override
    public int getCount() {
        if (arrayList != null) {
            return arrayList.size();
        } else {
            return 0;
        }
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.old_layout_media_list_item, null);
            holder.img_media_type = (ImageView) convertView.findViewById(R.id.img_file_type);
            holder.txt_media_name = (TextView) convertView.findViewById(R.id.txt_file_name);
            holder.img_play = (ImageView) convertView.findViewById(R.id.img_eye);
            holder.img_delete = (ImageView) convertView.findViewById(R.id.img_delete);
            ScreenScale.scaleView(convertView, "MediaListAdapter");
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (publicDocument) {
            holder.img_delete.setVisibility(View.INVISIBLE);
        }

        if (arrayList.size() > 0) {
            final ShareDoc media = arrayList.get(position);
            if (media != null) {

                holder.img_media_type.setImageResource(getMediaIcon(media.getFilename()));
                holder.txt_media_name.setText(media.getFilename());
                if (TKRoomManager.getInstance().getMySelf().role == 0 /*&& media.getFilecategory() == 0*/) {
                    if(!publicDocument){
                        holder.img_delete.setVisibility(View.VISIBLE);
                    }
                } else if (TKRoomManager.getInstance().getMySelf().role == 2) {
                    holder.img_delete.setVisibility(View.GONE);
                }

                if (TKRoomManager.getInstance().getMySelf().role != 4) {

                    holder.img_play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (media.getFileid() == localfileid || TKRoomManager.isMediaPublishing) {
                                return;
                            }
                            if (pop != null) {
                                pop.dismiss();
                            }
                            TKRoomManager.isMediaPublishing = true;
                            RoomSession.isPlay = true;
                            if (localfileid != -1) {
                                TKRoomManager.getInstance().stopShareMedia();
                                WhiteBoradManager.getInstance().setCurrentMediaDoc(media);
                            } else {
                                localfileid = media.getFileid();
                                WhiteBoradManager.getInstance().setCurrentMediaDoc(media);
                                TKRoomManager.getInstance().stopShareMedia();
                                String strSwfpath = media.getSwfpath();
                                if (strSwfpath != null && !strSwfpath.isEmpty()) {
                                    int pos = strSwfpath.lastIndexOf('.');
                                    if (pos != -1) {
                                        strSwfpath = String.format("%s-%d%s", strSwfpath.substring(0, pos), 1, strSwfpath.substring(pos));
                                        String url = "http://" + WhiteBoradManager.getInstance().getFileServierUrl() + ":" + WhiteBoradManager.getInstance().getFileServierPort() + strSwfpath;

                                        HashMap<String, Object> attrMap = new HashMap<String, Object>();
                                        attrMap.put("filename", media.getFilename());
                                        attrMap.put("fileid", media.getFileid());

                                        if (RoomSession.isClassBegin) {
                                            TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()),
                                                    "__all", attrMap);
                                        } else {
                                            TKRoomManager.getInstance().startShareMedia(url, Tools.isMp4(media.getFiletype()),
                                                    TKRoomManager.getInstance().getMySelf().peerId, attrMap);
                                        }
                                    }
                                }
                            }
                        }
                    });

                    holder.img_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (pop != null) {
                                pop.dismiss();
                            }
                            if (media.getFileid() == localfileid) {
                                TKRoomManager.getInstance().stopShareMedia();
                            }
                            WhiteBoradManager.getInstance().delRoomFile(roomNum, media.getFileid(), media.isMedia(), RoomSession.isClassBegin);
                        }
                    });
                }

            }
        }
        return convertView;
    }

    private int getMediaIcon(String filename) {
        int icon = -1;
        if (filename.toLowerCase().endsWith("mp4") || filename.toLowerCase().endsWith("webm")) {
            icon = R.drawable.icon_mp4;
        } else if (filename.toLowerCase().endsWith("mp3") || filename.toLowerCase().endsWith("wav") || filename.toLowerCase().endsWith("ogg")) {
            icon = R.drawable.icon_mp3;
        }
        return icon;
    }

    class ViewHolder {
        ImageView img_media_type;
        ImageView img_play;
        ImageView img_delete;
        TextView txt_media_name;
    }
}
