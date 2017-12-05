package com.lp.volley;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by LP on 2017/12/05.
 */

public class MyApp extends Application {
    private static RequestQueue quenes;

    @Override
    public void onCreate() {
        super.onCreate();
        quenes = Volley.newRequestQueue(getApplicationContext());
    }

    public static RequestQueue getHttpQuenes(){
        return quenes;
    }
}
