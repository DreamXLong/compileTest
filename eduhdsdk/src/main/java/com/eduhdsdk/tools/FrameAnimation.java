package com.eduhdsdk.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

/**
 * Created by Administrator on 2018/6/20/020.
 */

public class FrameAnimation {

    private boolean mIsRepeat;

    private AnimationListener mAnimationListener;

    private ImageView mImageView;

    private int[] mFrameRess;

    /**
     * 每帧动画的播放间隔数组
     */
    private int[] mDurations;

    /**
     * 每帧动画的播放间隔
     */
    private int mDuration;

    /**
     * 下一遍动画播放的延迟时间
     */
    private int mDelay;

    private int mLastFrame;

    private boolean mNext;

    private boolean mPause;

    private int mCurrentSelect;

    private int mCurrentFrame;

    private static final int SELECTED_A = 1;

    private static final int SELECTED_B = 2;

    private static final int SELECTED_C = 3;

    private static final int SELECTED_D = 4;

    private boolean mIsShow;
    private Context mContext;
    private Runnable playByDurationAndDelyRunable, playAndDelayRunable, playByDurationRunable, playRunable;

    /**
     * @param iv       播放动画的控件
     * @param frameRes 播放的图片数组
     * @param duration 每帧动画的播放间隔(毫秒)
     * @param isRepeat 是否循环播放
     */
    public FrameAnimation(ImageView iv, int[] frameRes, int duration, boolean isRepeat, boolean isShow, Context context) {
        this.mContext = context;
        this.mImageView = iv;
        this.mFrameRess = frameRes;
        this.mDuration = duration;
        this.mLastFrame = frameRes.length - 1;
        this.mIsRepeat = isRepeat;
        this.mIsShow = isShow;
    }

    public void playAnimation() {
        mPause = false;
        play(0);
    }

    public void stopAnimation() {
        /*play(mLastFrame + 1);*/
        mPause = true;

        mImageView.removeCallbacks(playByDurationAndDelyRunable);
        mImageView.removeCallbacks(playAndDelayRunable);
        mImageView.removeCallbacks(playByDurationRunable);
        mImageView.removeCallbacks(playRunable);

        playByDurationAndDelyRunable = null;
        playAndDelayRunable = null;
        playByDurationRunable = null;
        playRunable = null;

    }

    /**
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param isRepeat  是否循环播放
     */
    public FrameAnimation(ImageView iv, int[] frameRess, int[] durations, boolean isRepeat, Context context) {
        this.mContext = context;
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDurations = durations;
        this.mLastFrame = frameRess.length - 1;
        this.mIsRepeat = isRepeat;
        playByDurations(0);
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param duration  每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    public FrameAnimation(ImageView iv, int[] frameRess, int duration, int delay, Context context) {
        this.mContext = context;
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDuration = duration;
        this.mDelay = delay;
        this.mLastFrame = frameRess.length - 1;
        playAndDelay(0);
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    public FrameAnimation(ImageView iv, int[] frameRess, int[] durations, int delay, Context context) {
        this.mContext = context;
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDurations = durations;
        this.mDelay = delay;
        this.mLastFrame = frameRess.length - 1;
        playByDurationsAndDelay(0);
    }

    private void playByDurationsAndDelay(final int i) {

        playByDurationAndDelyRunable = new Runnable() {
            @Override
            public void run() {
                if (mPause) {   // 暂停和播放需求
                    mCurrentSelect = SELECTED_A;
                    mCurrentFrame = i;
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }

                if (mIsShow) {
                    RelativeLayout.LayoutParams tool_bar_param = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
                    tool_bar_param.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                    tool_bar_param.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                    tool_bar_param.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mImageView.setLayoutParams(tool_bar_param);
                    mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

               /* BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds=true;
                BitmapFactory.decodeResource(mContext.getResources(),
                        mFrameRess[i],options) ;
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;
                Bitmap bitmap= BitmapFactory.decodeFile(path, options);*/

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inTempStorage = new byte[1024 * 1024 * 5]; //5MB的临时存储空间
                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(),
                        mFrameRess[i], options);
                mImageView.setImageBitmap(bm);

               /* mImageView.setImageResource(mFrameRess[i]);*/
               /* mImageView.setBackgroundResource(mFrameRess[i]);*/

                if (i == mLastFrame) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationRepeat();
                    }
                    mNext = true;
                    playByDurationsAndDelay(0);
                } else {
                    playByDurationsAndDelay(i + 1);
                }
            }
        };

        mImageView.postDelayed(playByDurationAndDelyRunable, mNext && mDelay > 0 ? mDelay : mDurations[i]);
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }


    private void playAndDelay(final int i) {

        playAndDelayRunable = new Runnable() {
            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_B;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                mNext = false;
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }

                if (mIsShow) {
                    RelativeLayout.LayoutParams tool_bar_param = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
                    tool_bar_param.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                    tool_bar_param.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                    tool_bar_param.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mImageView.setLayoutParams(tool_bar_param);
                    mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

               /* BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;   //宽高为原来的2分之1
                options.inTempStorage = new byte[1024 * 1024 * 2]; //5MB的临时存储空间
                int width = mImageView.getWidth();
                int heigth = mImageView.getHeight();
                int size[] = getImageWidthHeight(mContext, mFrameRess[i]);

                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), mFrameRess[i], options);
                mImageView.setImageBitmap(bm);*/

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inTempStorage = new byte[1024 * 1024 * 5]; //5MB的临时存储空间
                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(),
                        mFrameRess[i], options);
                mImageView.setImageBitmap(bm);

                /*mImageView.setImageResource(mFrameRess[i]);*/

                if (i == mLastFrame) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationRepeat();
                    }
                    mNext = true;
                    playAndDelay(0);
                } else {
                    playAndDelay(i + 1);
                }
            }
        };

        mImageView.postDelayed(playAndDelayRunable, mNext && mDelay > 0 ? mDelay : mDuration);
    }

    private void playByDurations(final int i) {

        playByDurationRunable = new Runnable() {
            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_C;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }

                if (mIsShow) {
                    RelativeLayout.LayoutParams tool_bar_param = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
                    tool_bar_param.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                    tool_bar_param.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                    tool_bar_param.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mImageView.setLayoutParams(tool_bar_param);
                    mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

               /* BitmapFactory.Options options = new BitmapFactory.Options();
                *//*options.inSampleSize = 2; *//*  //宽高为原来的2分之1
                options.inTempStorage = new byte[1024 * 1024 * 5]; //5MB的临时存储空间
                int width = mImageView.getWidth();
                int heigth = mImageView.getHeight();
                int size[] = getImageWidthHeight(mContext, mFrameRess[i]);

                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), mFrameRess[i], options);
                mImageView.setImageBitmap(bm);*/

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inTempStorage = new byte[1024 * 1024 * 5]; //5MB的临时存储空间
                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(),
                        mFrameRess[i], options);
                mImageView.setImageBitmap(bm);


                /*mImageView.setImageResource(mFrameRess[i]);*/

               /* mImageView.setBackgroundResource(mFrameRess[i]);*/

                if (i == mLastFrame) {
                    if (mIsRepeat) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationRepeat();
                        }
                        playByDurations(0);
                    } else {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationEnd();
                        }
                    }
                } else {

                    playByDurations(i + 1);
                }
            }
        };

        mImageView.postDelayed(playByDurationRunable, mDurations[i]);
    }

    private void play(final int i) {

        playRunable = new Runnable() {
            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_D;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                if (i >= mFrameRess.length) {
                    mImageView.setVisibility(View.GONE);
                    return;
                }

                if (mIsShow) {
                    RelativeLayout.LayoutParams tool_bar_param = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
                    tool_bar_param.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                    tool_bar_param.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                    tool_bar_param.addRule(RelativeLayout.CENTER_IN_PARENT);
                    mImageView.setLayoutParams(tool_bar_param);
                    mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                mImageView.setVisibility(View.VISIBLE);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inTempStorage = new byte[1024 * 1024 * 5]; //5MB的临时存储空间
                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(),
                        mFrameRess[i], options);
                mImageView.setImageBitmap(bm);


               /* BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;   //宽高为原来的2分之1
                options.inTempStorage = new byte[1024 * 1024 * 2]; //5MB的临时存储空间
                int width = mImageView.getWidth();
                int heigth = mImageView.getHeight();
                int size[] = getImageWidthHeight(mContext, mFrameRess[i]);

                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), mFrameRess[i], options);
                mImageView.setImageBitmap(bm);
//                Glide.with(mContext).load(mFrameRess[i]).override(width,heigth).fitCenter().skipMemoryCache(true).into(mImageView);
               *//* mImageView.setImageResource(mFrameRess[i]);*/



                if (i == mLastFrame) {
                    if (mIsRepeat) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationRepeat();
                        }
                        play(0);
                    } else {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationEnd();
                        }
                    }
                } else {
                    play(i + 1);
                }
            }
        };

        mImageView.postDelayed(playRunable, mDuration);
    }

    public static int[] getImageWidthHeight(Context mContext, int path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), path, options);
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }

    public static interface AnimationListener {

        /**
         * <p>Notifies the start of the animation.</p>
         */
        void onAnimationStart();

        /**
         * <p>Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         */
        void onAnimationEnd();

        /**
         * <p>Notifies the repetition of the animation.</p>
         */
        void onAnimationRepeat();
    }

    /**
     * <p>Binds an animation listener to this animation. The animation listener
     * is notified of animation events such as the end of the animation or the
     * repetition of the animation.</p>
     *
     * @param listener the animation listener to be notified
     */
    public void setAnimationListener(AnimationListener listener) {
        this.mAnimationListener = listener;
    }

    public void release() {
        pauseAnimation();
    }

    public void pauseAnimation() {
        this.mPause = true;
    }

    public boolean isPause() {
        return this.mPause;
    }

    public void restartAnimation() {
        if (mPause) {
            mPause = false;
            switch (mCurrentSelect) {
                case SELECTED_A:
                    playByDurationsAndDelay(mCurrentFrame);
                    break;
                case SELECTED_B:
                    playAndDelay(mCurrentFrame);
                    break;
                case SELECTED_C:
                    playByDurations(mCurrentFrame);
                    break;
                case SELECTED_D:
                    play(mCurrentFrame);
                    break;
                default:
                    break;
            }
        }
    }
}
