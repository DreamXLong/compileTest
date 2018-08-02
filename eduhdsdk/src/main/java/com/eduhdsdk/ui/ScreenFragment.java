package com.eduhdsdk.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.eduhdsdk.R;
import com.talkcloud.roomsdk.TKRoomManager;

import org.tkwebrtc.EglBase;
import org.tkwebrtc.RendererCommon;
import org.tkwebrtc.SurfaceViewRenderer;

public class ScreenFragment extends Fragment {

    private static String sync = "";
    static private ScreenFragment mInstance = null;
    private View fragmentView;
    private LinearLayout lin_video_play;
    private SurfaceViewRenderer suf_mp4;
    private String peerIdScreen;

    public void setPeerId(String peerIdScreen) {
        this.peerIdScreen = peerIdScreen;
        TKRoomManager.getInstance().playScreen(peerIdScreen, suf_mp4);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    static public ScreenFragment getInstance() {
        synchronized (sync) {
            if (mInstance == null) {
                mInstance = new ScreenFragment();
            }
            return mInstance;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_screen, null);
            fragmentView.bringToFront();
            lin_video_play = (LinearLayout) fragmentView.findViewById(R.id.lin_video_play);
            suf_mp4 = (SurfaceViewRenderer) fragmentView.findViewById(R.id.suf_mp4);
            suf_mp4.init(EglBase.create().getEglBaseContext(), null);
//            suf_mp4.setZOrderOnTop(true);
            suf_mp4.setZOrderMediaOverlay(true);

        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (peerIdScreen != null) {
            suf_mp4.setEnableHardwareScaler(true);
            suf_mp4.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            TKRoomManager.getInstance().playScreen(peerIdScreen, suf_mp4);
            suf_mp4.requestLayout();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        suf_mp4.release();
        suf_mp4 = null;
        super.onDestroyView();
        mInstance = null;
    }
}
