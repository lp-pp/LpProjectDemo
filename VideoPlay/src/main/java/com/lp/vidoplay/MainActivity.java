package com.lp.vidoplay;

import android.content.pm.ActivityInfo;
import android.media.AudioManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.R.attr.id;

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
                if (videoPlayView.isPlay()){
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
                updateTextViewWithTimeformat(tv_current_time, progress);
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
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress. 0);
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
            public void Click(View v) {
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

    Handler UIhandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage();

        }
    };

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
