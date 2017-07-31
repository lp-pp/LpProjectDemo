package com.lp.vidoplay;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author mac lipengy
 * @version 1.0
 * @date 2017.07.29 18:32:17
 */

public class MainActivity extends AppCompatActivity {
    private RelativeLayout rl_videoplay, rl_option;
    private LinearLayout ll_processbar, ll_playtime;
    private VideoPlayView videoPlayView;
    private SeekBar sb_play, sb_volume;
    private ImageView iv_playcontroll, iv_volume, iv_screen;
    private TextView tv_current_time, tv_total_time;
    private int screen_width, screen_heigth;
    private AudioManager audioManager;
    private boolean isFullScreen = false; //竖屏
    private boolean isVisible = true; //全屏时，隐藏播放按钮

    private static final int UPDATE_UI = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //初始化布局
        ininView();
        initUI();

        //点击事件监听
        setPlayEventListener();

        String videoPath = "https://github.com/lp-pp/LpProjectDemo/tree/master/VideoPlay";
        Uri uri = Uri.parse(videoPath);
        videoPlayView.setVideoURI(uri);

        videoPlayView.start();
        UIhandler.sendEmptyMessage(UPDATE_UI);
    }

    /**
     * initview
     * 初始化布局
     */
    private void ininView() {
        videoPlayView = (VideoPlayView) findViewById(R.id.videoplay_view);
        rl_videoplay = (RelativeLayout) findViewById(R.id.videoplay_layout);
        rl_option = (RelativeLayout) findViewById(R.id.option_layout);
        ll_processbar = (LinearLayout) findViewById(R.id.processbr_layout);
        ll_playtime = (LinearLayout) findViewById(R.id.playtime_layout);
        sb_play = (SeekBar) findViewById(R.id.play_seek);
        sb_volume = (SeekBar) findViewById(R.id.volume_seek);
        iv_playcontroll = (ImageView) findViewById(R.id.pause_img);
        iv_volume = (ImageView) findViewById(R.id.volume_img);
        iv_screen = (ImageView) findViewById(R.id.screen_img);
        tv_current_time = (TextView) findViewById(R.id.current_time_play);
        tv_total_time = (TextView) findViewById(R.id.total_time_play);
    }

    /**
     * initUI
     * 获取UI界面的值
     */
    private void initUI() {
        screen_width = getResources().getDisplayMetrics().widthPixels;
        screen_heigth = getResources().getDisplayMetrics().heightPixels;
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE); //获取音频服务

        int streamVolumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        sb_volume.setMax(streamVolumeMax);   //进度条的最大音量
        sb_volume.setProgress(streamVolume); //进度条的当期音量
    }

    /**
     * 点击事件监听
     */
    private void setPlayEventListener() {
        /**
         * 监听视频的播放和暂停
         */
        iv_playcontroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoPlayView.isPlaying()){
                    iv_playcontroll.setImageResource(R.drawable.play_btn_style);
                    //暂停播放
                    videoPlayView.pause();
                } else {
                    iv_playcontroll.setImageResource(R.drawable.pause_btn_style);
                    //继续播放
                    videoPlayView.start();
                    UIhandler.sendEmptyMessage(UPDATE_UI);
                }
            }
        });

        /**
         * 监听视频播放进度拖动事件
         */
        sb_play.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updataTextViewWithTimeFormat(tv_current_time, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UIhandler.removeMessages(UPDATE_UI);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                videoPlayView.seekTo(progress);
                UIhandler.sendEmptyMessage(UPDATE_UI);

            }
        });

        /**
         * 监听音频进度拖动事件
         */
        sb_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * 全屏或半屏的切换
         */
        iv_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFullScreen){ //横屏状态
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //改为竖屏
                } else { //竖屏状态
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //改为横屏
                }
            }
        });

        rl_videoplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible){
                    ll_processbar.setVisibility(View.GONE);
                    isVisible = false;
                } else {
                    ll_processbar.setVisibility(View.VISIBLE);
                    isVisible = true;
                }
            }
        });

    }

    /**
     * updataTextViewWithTimeFormat
     * 对时间进行格式化处理
     * @param textView  TextView控件
     * @param millisecond  时间，毫秒
     */
    private void updataTextViewWithTimeFormat(TextView textView, int millisecond) {
        int second = millisecond / 1000;
        int hh = second / 3600;  //时
        int mm = second % 3600 / 60;  //分钟
        int ss = second % 60;  //秒
        String str = null;
        if (hh != 0) {
            str = String.format("%02d:%02d:%02d", hh, mm, ss); //格式化
            //%02d  如果只有个位，那么，十位就会用0填充
        } else {
            str = String.format("%02d:%02d", mm, ss);
        }
        textView.setText(str);
    }

    /**
     * UIHandler
     * 通过 handler 刷新自己，实现进度条更新的效果
     */
    private Handler UIhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == UPDATE_UI) { //标识
                int currentPosition = videoPlayView.getCurrentPosition(); //视频当前播放时间，毫秒
                int totalDuration = videoPlayView.getDuration(); //视频总时间，毫秒

                //格式化视频播放时间
                updataTextViewWithTimeFormat(tv_current_time, currentPosition);
                updataTextViewWithTimeFormat(tv_total_time, totalDuration);

                //播放进度条
                sb_play.setMax(totalDuration);
                sb_play.setProgress(currentPosition);

                UIhandler.sendEmptyMessageDelayed(UPDATE_UI, 500); //自己刷新自己
                //达到刷新的效果
            }
        }
    };

    /**
     * 暂停
     */
    @Override
    protected void onPause() {
        super.onPause();
        UIhandler.removeMessages(UPDATE_UI); //停止 handler 自动刷新
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 监听到屏幕方向的改变
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /**
         * 当屏幕方向为横屏的时候
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //横屏时，音频控制可见
            iv_volume.setVisibility(View.VISIBLE);
            sb_volume.setVisibility(View.VISIBLE);
            isFullScreen = true;
        } else {/** 当屏幕方向为竖屏的时候 */
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, FontDisplayUtil.dip2px(this, 240));
            //竖屏时，音频控制不可见
            iv_volume.setVisibility(View.GONE);
            sb_volume.setVisibility(View.GONE);
            isFullScreen = false;
        }
    }

    /**
     * setVideoViewScale
     * 横屏竖屏转换时，视频大小处理
     * @param width
     * @param height
     */
    private void setVideoViewScale(int width, int height) { //像素，需要转换
        ViewGroup.LayoutParams layoutParams = videoPlayView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        videoPlayView.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams1 = rl_videoplay.getLayoutParams();
        layoutParams1.width = width;
        layoutParams1.height = height;
        rl_videoplay.setLayoutParams(layoutParams1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
