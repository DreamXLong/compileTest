package com.eduhdsdk.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eduhdsdk.R;
import com.eduhdsdk.message.RoomClient;
import com.eduhdsdk.message.RoomSession;
import com.eduhdsdk.tools.ScreenScale;
import com.eduhdsdk.tools.Tools;
import com.eduhdsdk.ui.OneToManyActivity;
import com.talkcloud.roomsdk.RoomControler;
import com.talkcloud.roomsdk.RoomUser;
import com.talkcloud.roomsdk.TKRoomManager;

import org.json.JSONException;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/5/27.
 */

public class MemberListAdapter extends BaseAdapter {

    private ArrayList<RoomUser> userList = new ArrayList<RoomUser>();
    private Context context;
    private String pubFailUserId = "";

    public void setPubFailUserId(String pubFailUserId) {
        this.pubFailUserId = pubFailUserId;
    }

    public void setUserList(ArrayList<RoomUser> userList) {
        this.userList = userList;
    }

    public MemberListAdapter(Context context, ArrayList<RoomUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (userList.size() > 0) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_member_list_item, null);
                holder.txt_user_name = (TextView) convertView.findViewById(R.id.txt_user_name);
                holder.img_hand_up = (ImageView) convertView.findViewById(R.id.img_hand_up);
                holder.img_tool = (ImageView) convertView.findViewById(R.id.img_draw);
                holder.img_up_sd = (ImageView) convertView.findViewById(R.id.img_up_sd);
                holder.img_audio = (ImageView) convertView.findViewById(R.id.img_audio);
                holder.img_video = (ImageView) convertView.findViewById(R.id.img_video);
                holder.im_type = (ImageView) convertView.findViewById(R.id.im_type);
                holder.iv_no_speak = (ImageView) convertView.findViewById(R.id.iv_no_speak);
                holder.iv_out_room = (ImageView) convertView.findViewById(R.id.iv_out_room);
               /* holder.txt_degree = (TextView) convertView.findViewById(R.id.txt_degree);*/
                ScreenScale.scaleView(convertView, "MemberListAdapter");
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final RoomUser user = userList.get(position);
            if (user != null) {
                holder.txt_user_name.setText(user.nickName);
                if (user.properties.containsKey("raisehand")) {
                    boolean israisehand = Tools.isTure(user.properties.get("raisehand"));
                    if (israisehand) {
                        holder.img_hand_up.setVisibility(View.VISIBLE);//正在举手
                    } else {
                        holder.img_hand_up.setVisibility(View.INVISIBLE);//同意了，或者拒绝了
                    }
                } else {
                    holder.img_hand_up.setVisibility(View.INVISIBLE);//还没举手
                }

                /*if (user.role == 2) {
                    holder.txt_degree.setText("(" + context.getResources().getString(R.string.student) + ")");
                } else if (user.role == 4) {
                    holder.txt_degree.setText("(" + context.getResources().getString(R.string.lass_patrol) + ")");
                } else if (user.role == 1) {
                    holder.txt_degree.setText("(" + context.getResources().getString(R.string.assistant) + ")");
                }*/
                if (user.properties.containsKey("devicetype")) {
                    String type = (String) user.properties.get("devicetype");
                    int udpstate = 1;
                    switch (type) {
                        case "AndroidPad":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.androidpad_red);
                            } else {
                                holder.im_type.setImageResource(R.drawable.android_hd);
                            }
                            break;
                        case "iPad":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.ipad_icon_red);
                            } else {
                                holder.im_type.setImageResource(R.drawable.ipad_hd);
                            }
                            break;
                        case "AndroidPhone":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.android_red);
                            } else {
                                holder.im_type.setImageResource(R.drawable.android);
                            }
                            break;
                        case "iPhone":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.iphone_icon_red);
                            } else {
                                holder.im_type.setImageResource(R.drawable.iphone);
                            }
                            break;
                        case "WindowClient":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.win_client_icon_red);
                            } else {
                                holder.im_type.setImageResource(R.drawable.win_client);
                            }
                            break;
                        case "WindowPC":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.win_pc_icon_red);
                            } else {
                                holder.im_type.setImageResource(R.drawable.win_pc);
                            }
                            break;
                        case "MacClient":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.mac_client_red);
                            } else {
                                holder.im_type.setImageResource(R.drawable.mac_client);
                            }
                            break;
                        case "MacPC":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.mac_pc_icon_red);
                            } else {
                                holder.im_type.setImageResource(R.drawable.mac_pc);
                            }
                            break;
                        case "AndroidTV":
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.icon_tv_red);
                            } else {
                                holder.im_type.setImageResource(R.drawable.icon_tv_white);
                            }
                            break;
                        default:
                            if (udpstate > 1) {
                                holder.im_type.setImageResource(R.drawable.icon_unknown_red);
                            } else {
                                holder.im_type.setImageResource(R.drawable.icon_unknown_white);
                            }
                            break;
                    }
                }

                if (user.properties.containsKey("disablechat")) {
                    if (user.role == 1) {
                        holder.iv_no_speak.setVisibility(View.INVISIBLE);
                        holder.iv_out_room.setVisibility(View.INVISIBLE);
                    }
                    boolean temp = Tools.isTure(user.properties.get("disablechat"));
                    if (temp) {
                        holder.iv_no_speak.setImageResource(R.drawable.button_speak);
                    } else {
                        holder.iv_no_speak.setImageResource(R.drawable.button_close_speak);
                    }
                }

                if (user.role == 1) {
                    holder.img_tool.setEnabled(false);
                    holder.img_tool.setImageResource(R.drawable.button_closeeditor_unclickable);//可以画图
                } else {
                    if (user.properties.containsKey("candraw")) {
                        boolean candraw = Tools.isTure(user.properties.get("candraw"));
                        if (candraw) {
                            holder.img_tool.setImageResource(R.drawable.button_close_editor);//可以画图
                        } else {
                            holder.img_tool.setImageResource(R.drawable.button_editor);//不可以画图
                        }
                    } else {
                        holder.img_tool.setImageResource(R.drawable.button_editor);//没给过画图权限
                    }
                }

                if (user.publishState > 0) {//只要有流
                    if (user.role == 0) {
                        holder.img_up_sd.setImageResource(R.drawable.button_shangjiangtai);
                    } else if (user.role == 1) {
                        holder.img_up_sd.setImageResource(R.drawable.button_shangjiangtai);
                    } else if (user.role == 2) {
                        holder.img_up_sd.setImageResource(R.drawable.button_shangjiangtai);
                    }
                } else {
                    if (user.role == 0) {
                        holder.img_up_sd.setImageResource(R.drawable.button_xiajiangtai);
                    } else if (user.role == 1) {
                        holder.img_up_sd.setImageResource(R.drawable.button_xiajiangtai);
                    } else if (user.role == 2) {
                        holder.img_up_sd.setImageResource(R.drawable.button_xiajiangtai);
                    }
                }

                if (user.disableaudio) {
                    holder.img_audio.setImageResource(R.drawable.button_no_audio);
                    holder.img_audio.setEnabled(false);
                } else {
                    holder.img_audio.setEnabled(true);
                    if (user.publishState == 1 || user.publishState == 3) {//音频状态改的开启
                        holder.img_audio.setImageResource(R.drawable.button_close_audio);
                    } else {
                        holder.img_audio.setImageResource(R.drawable.button_audio);
                    }
                }

                if (user.disablevideo) {
                    holder.img_video.setEnabled(false);
                    holder.img_video.setImageResource(R.drawable.icon_no_video);
                } else {
                    holder.img_video.setEnabled(true);
                    if (user.publishState == 2 || user.publishState == 3) {//视频状态改的开启
                        holder.img_video.setImageResource(R.drawable.button_close_video);
                    } else {
                        holder.img_video.setImageResource(R.drawable.button_video);
                    }
                }
                if (RoomSession.isClassBegin) {
                    if (user.role == 1 && !RoomControler.isShowAssistantAV()) {
                        holder.img_up_sd.setVisibility(View.INVISIBLE);
                        holder.img_video.setVisibility(View.INVISIBLE);
                        holder.img_audio.setVisibility(View.INVISIBLE);
                        holder.img_tool.setVisibility(View.INVISIBLE);
                    } else {
                        holder.img_up_sd.setVisibility(View.VISIBLE);
                        holder.img_video.setVisibility(View.VISIBLE);
                        holder.img_audio.setVisibility(View.VISIBLE);
                        holder.img_tool.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.img_up_sd.setVisibility(View.INVISIBLE);
                    holder.img_audio.setVisibility(View.INVISIBLE);
                    holder.img_tool.setVisibility(View.INVISIBLE);
                    holder.img_video.setVisibility(View.INVISIBLE);
                }

                if (TKRoomManager.getInstance().getMySelf().role != 4) {

                    if (TKRoomManager.getInstance().getMySelf().role != 1) {

                        holder.img_tool.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //授权
                                if (user.properties.containsKey("candraw")) {
                                    boolean candraw = Tools.isTure(user.properties.get("candraw"));
                                    if (candraw) {
                                        //不可以画图
                                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                                    } else {

                                        try {
                                            if (RoomSession.publishSet.size() + RoomSession.pandingSet.size() >= TKRoomManager.getInstance().getRoomProperties().getInt("maxvideo") && user.publishState <= 1) {
                                                Toast.makeText(context, R.string.member_overload, Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        //可以画图
                                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", true);
                                        if (user.publishState == 0) {
                                            TKRoomManager.getInstance().changeUserPublish(user.peerId, 4);
                                            TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                                        }
                                    }
                                } else {

                                    try {
                                        if (RoomSession.publishSet.size() + RoomSession.pandingSet.size() >= TKRoomManager.getInstance().getRoomProperties().getInt("maxvideo") && user.publishState <= 1) {
                                            Toast.makeText(context, R.string.member_overload, Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    //可以画图
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", true);
                                    if (user.publishState == 0) {
                                        TKRoomManager.getInstance().changeUserPublish(user.peerId, 4);
                                        TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                                    }
                                }
                            }
                        });
                    }

                    holder.img_up_sd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {   //上下台

                            if (!RoomSession.isClassBegin) {
                                return;
                            }
//                        if(((Integer)user.properties.get("udpstate"))>1){//用户udp不通，不能上台！
//                            Toast.makeText(context, R.string.member_overload, Toast.LENGTH_LONG).show();
//                            return;
//                        }
                            try {
                                if (RoomSession.publishSet.size() + RoomSession.pandingSet.size() >= TKRoomManager.getInstance().getRoomProperties().getInt("maxvideo") && user.publishState <= 1) {
                                    Toast.makeText(context, R.string.member_overload, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (RoomSession.pandingSet.contains(user.peerId)) {
                                return;
                            }

                            if (user.role == 1 && !RoomControler.isShowAssistantAV()) {
                                return;
                            }
                            if (user.publishState == 0 && user.properties.containsKey("isInBackGround") && Tools.isTure(user.properties.get("isInBackGround"))) {
                                Toast.makeText(context, user.nickName + context.getResources().getString(R.string.select_back_hint), Toast.LENGTH_LONG).show();
                                return;
                            }
                            RoomSession.pandingSet.add(user.peerId);
                            user.properties.put("passivityPublish", true);
                            if (user.publishState >= 1) {//只要有流开启就是上台
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 0);
                                if (user.role == 2) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                                }
                            } else if (!user.disablevideo && !user.disableaudio) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 3);
                                TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);

                            } else if (!user.disableaudio) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 1);
                                if (user.role == 2) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "candraw", false);
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                                }
                            } else if (!user.disablevideo) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 2);
                            } else if (user.disableaudio || user.disablevideo) {
                                Toast.makeText(context, R.string.device_disable, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    holder.img_audio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                if (RoomSession.publishSet.size() + RoomSession.pandingSet.size() >= TKRoomManager.getInstance().getRoomProperties().getInt("maxvideo") && user.publishState <= 1) {
                                    Toast.makeText(context, R.string.member_overload, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //打开关闭语音
                            if (user.publishState == 0 || user.publishState == 4) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 1);
                                TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                            }

                            if (user.publishState == 1) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 4);
                            }

                            if (user.publishState == 2) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 3);
                            }

                            if (user.publishState == 3) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 2);
                            }


                            /*if (user.publishState == 0 || user.publishState == 2 || user.publishState == 4) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 0 || user.publishState == 4 ? 1 : 3);
                                TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                            } else {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 3 ? 2 : 4);

                            }*/

                        }
                    });

                    holder.img_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                if (RoomSession.publishSet.size() + RoomSession.pandingSet.size() >= TKRoomManager.getInstance().getRoomProperties().getInt("maxvideo") && user.publishState <= 1) {
                                    Toast.makeText(context, R.string.member_overload, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //打开关闭摄像头
                            if (user.publishState == 0 || user.publishState == 4) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 2);
                                TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "raisehand", false);
                            }

                            if (user.publishState == 1) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 3);
                            }

                            if (user.publishState == 2) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 4);
                            }

                            if (user.publishState == 3) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, 1);
                            }

                            /*if (user.publishState == 0 || user.publishState == 1 || user.publishState == 4) {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 0 || user.publishState == 4 ? 2 : 3);
                            } else {
                                TKRoomManager.getInstance().changeUserPublish(user.peerId, user.publishState == 2 ? 4 : 1);
                            }*/
                        }
                    });

                    holder.iv_no_speak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean b = false;
                            try {
                                if (user.properties.containsKey("disablechat")) {
                                    b = (boolean) user.properties.get("disablechat");
                                } else {
                                    b = false;
                                }
                                if (b) {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "disablechat", false);
                                } else {
                                    TKRoomManager.getInstance().changeUserProperty(user.peerId, "__all", "disablechat", true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    holder.iv_out_room.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //踢出房间
                            Tools.showDialog(context, R.string.remind, R.string.sure_get_out_the_people, new Tools.OnDialogClick() {
                                @Override
                                public void dialog_ok(Dialog dialog) {
                                    TKRoomManager.getInstance().evictUser(user.peerId, 1);
                                    dialog.dismiss();
                                }
                            }, 0);
                        }
                    });
                }
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView txt_user_name/*, txt_degree*/;
        ImageView img_hand_up;
        ImageView img_tool;
        ImageView img_up_sd;
        ImageView img_audio;
        ImageView img_video;
        ImageView im_type;
        ImageView iv_no_speak;
        ImageView iv_out_room;
    }
}
