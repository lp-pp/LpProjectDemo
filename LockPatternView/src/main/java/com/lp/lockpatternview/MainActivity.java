package com.lp.lockpatternview;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LockPatternView.OnPatternChangeListener {

    private TextView loakHint;
    private LockPatternView lockPatternView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        setContentView(R.layout.activity_main);
        loakHint = (TextView) findViewById(R.id.activity_main_lock_hint);
        lockPatternView = (LockPatternView) findViewById(R.id.activity_main_lock);
        lockPatternView.setPatternChangeListener(this);
    }

    @Override
    public void onPatternChange(String passwordStr) {
        if (!TextUtils.isEmpty(passwordStr)){
            loakHint.setText(passwordStr);
        } else {
            loakHint.setText("至少5个图案");
        }
    }

    @Override
    public void onPatternStart(boolean isStart) {
        if (isStart){
            loakHint.setText("请绘制图案");
        }
    }
}
