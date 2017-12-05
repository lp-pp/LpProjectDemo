package com.lp.volley;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        volley_Get();
    }

    private void volley_Get(){
        String url = "http://apis.juhe.cn/mobile/get?phone=13429667914&key=335adcc4e891ba4e4be6d7534fd54c5d";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError Error) {
                Toast.makeText(MainActivity.this, Error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        request.setTag("abcGet");
        MyApp.getHttpQuenes().add(request);
    }
}
