package com.eduhdsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.classroomsdk.Packager;
import com.classroomsdk.ShareDoc;
import com.classroomsdk.WhiteBoradManager;
import com.eduhdsdk.R;
import com.eduhdsdk.message.RoomSession;
import com.eduhdsdk.tools.ScreenScale;
import com.talkcloud.roomsdk.TKRoomManager;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/5/28.
 */

public class FileListAdapter extends BaseAdapter {

    private Context context;
    private String roomNum;
    private PopupWindow pop = null;
    private ArrayList<ShareDoc> arrayList;
    private boolean publicDocument = false;

    public void setPop(PopupWindow pop) {
        this.pop = pop;
    }

    public FileListAdapter(Context context, String Roomnum) {
        this.context = context;
        this.roomNum = Roomnum;
    }

    public void setArrayList(ArrayList<ShareDoc> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    public void isPublicDocuments(boolean b) {
        this.publicDocument = b;
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
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (arrayList.size() > 0) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_file_list_item, null);
                holder.img_file_type = (ImageView) convertView.findViewById(R.id.img_file_type);
                holder.txt_file_name = (TextView) convertView.findViewById(R.id.txt_file_name);
                holder.img_eye = (ImageView) convertView.findViewById(R.id.img_eye);
                holder.img_delete = (ImageView) convertView.findViewById(R.id.img_delete);
                ScreenScale.scaleView(convertView, "FilelistAdapter");
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (publicDocument) {
                holder.img_delete.setVisibility(View.INVISIBLE);
            }

            final ShareDoc fileDoc = arrayList.get(position);
            if (fileDoc != null) {
                if (fileDoc.getFileid() == 0) {
                    fileDoc.setFilename(context.getString(R.string.share_pad));
                }

               /* int icon = getFileIcon(fileDoc.getFilename());
                holder.img_file_type.setImageResource(icon);*/
                setFileIcom(fileDoc.getFilename(),holder.img_file_type);


                holder.txt_file_name.setText(fileDoc.getFilename());

                if (fileDoc.getFileid() == WhiteBoradManager.getInstance().getCurrentFileDoc().getFileid()) {
                    holder.img_eye.setImageResource(R.drawable.icon_file_showing);
                } else {
                    holder.img_eye.setImageResource(R.drawable.icon_show_file);
                }
                if (TKRoomManager.getInstance().getMySelf().role == 0 /*&& fileDoc.getFilecategory() == 0*/) {
                    if (fileDoc.getFileid() == 0) {
                        holder.img_delete.setVisibility(View.GONE);
                    } else {
                        if(!publicDocument){
                            holder.img_delete.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (TKRoomManager.getInstance().getMySelf().role == 2) {
                    holder.img_delete.setVisibility(View.GONE);
                }

                if (TKRoomManager.getInstance().getMySelf().role != 4) {

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fileDoc.getFileid() == WhiteBoradManager.getInstance().getCurrentFileDoc().getFileid()) {
                                return;
                            }
                            WhiteBoradManager.getInstance().localChangeDoc(fileDoc);
                            notifyDataSetChanged();
                            if (RoomSession.isClassBegin) {
                                JSONObject data = new JSONObject();
                                data = Packager.pageSendData(fileDoc);
                                TKRoomManager.getInstance().pubMsg("ShowPage", "DocumentFilePage_ShowPage", "__all", data.toString(), true, null, null);
                            }
                            if (pop != null) {
                                pop.dismiss();
                            }
                        }
                    });

                    holder.img_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (pop != null) {
                                pop.dismiss();
                            }
                            WhiteBoradManager.getInstance().delRoomFile(roomNum, fileDoc.getFileid(), fileDoc.isMedia(), RoomSession.isClassBegin);
                        }
                    });
                }
            }
        }
        return convertView;
    }

    private void setFileIcom(String filename, ImageView img_file_type) {

        if (filename == null && filename.isEmpty()) {
            img_file_type.setImageResource(R.drawable.icon_empty);
        }
        if (filename.toLowerCase().endsWith(".pptx") || filename.toLowerCase().endsWith(".ppt") || filename.toLowerCase().endsWith(".pps")) {
            img_file_type.setImageResource(R.drawable.icon_ppt);
        } else if (filename.toLowerCase().endsWith(".docx") || filename.toLowerCase().endsWith(".doc")) {
            img_file_type.setImageResource(R.drawable.icon_word);
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")
                || filename.toLowerCase().endsWith(".png") || filename.toLowerCase().endsWith(".gif")
                || filename.toLowerCase().endsWith(".bmp")) {
            img_file_type.setImageResource(R.drawable.icon_images);
        } else if (filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx")
                || filename.toLowerCase().endsWith(".xlt") || filename.toLowerCase().endsWith("xlsm")) {
            img_file_type.setImageResource(R.drawable.icon_excel);
        } else if (filename.toLowerCase().endsWith(".pdf")) {
            img_file_type.setImageResource(R.drawable.icon_pdf);
        } else if (filename.equals(context.getResources().getString(R.string.share_pad))) {
            img_file_type.setImageResource(R.drawable.icon_empty);
        } else if (filename.toLowerCase().endsWith(".txt")) {

            img_file_type.setImageResource(R.drawable.icon_text_pad);
        } else if (filename.toLowerCase().endsWith(".zip")) {
            img_file_type.setImageResource(R.drawable.icon_h5);
        }
    }

    private int getFileIcon(String filename) {
        int icon = -1;
        if (filename == null && filename.isEmpty()) {
            icon = R.drawable.icon_empty;
        }
        if (filename.toLowerCase().endsWith(".pptx") || filename.toLowerCase().endsWith(".ppt") || filename.toLowerCase().endsWith(".pps")) {
            icon = R.drawable.icon_ppt;
        } else if (filename.toLowerCase().endsWith(".docx") || filename.toLowerCase().endsWith(".doc")) {
            icon = R.drawable.icon_word;
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")
                || filename.toLowerCase().endsWith(".png") || filename.toLowerCase().endsWith(".gif")
                || filename.toLowerCase().endsWith(".bmp")) {
            icon = R.drawable.icon_images;
        } else if (filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx")
                || filename.toLowerCase().endsWith(".xlt") || filename.toLowerCase().endsWith("xlsm")) {
            icon = R.drawable.icon_excel;
        } else if (filename.toLowerCase().endsWith(".pdf")) {
            icon = R.drawable.icon_pdf;
        } else if (filename.equals(context.getResources().getString(R.string.share_pad))) {
            icon = R.drawable.icon_empty;
        } else if (filename.toLowerCase().endsWith(".txt")) {
            icon = R.drawable.icon_text_pad;
        } else if (filename.toLowerCase().endsWith(".zip")) {
            icon = R.drawable.icon_h5;
        }
        return icon;
    }

    class ViewHolder {
        ImageView img_file_type;
        TextView txt_file_name;
        ImageView img_eye;
        ImageView img_delete;
    }
}
