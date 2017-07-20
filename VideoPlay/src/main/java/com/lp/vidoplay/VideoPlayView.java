package com.lp.vidoplay;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author lipengy
 * Created by mac on 17/7/20.
 * 自定义视频播放器View
 */

public class VideoPlayView extends View {
    int defaultWidth = 1920;
    int defaultHeight = 1080;

    public VideoPlayView(Context context){
        super(context);
    }

    public VideoPlayView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public VideoPlayView(Context context, AttributeSet attributeSet, int defStyleAttr){
        super(context, attributeSet, defStyleAttr);
    }

    /**
     * 重写父类方法
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(defaultWidth, widthMeasureSpec);
        int height = getDefaultSize(defaultHeight, heightMeasureSpec);
        setMeasuredDimension(width,height);
    }
}
