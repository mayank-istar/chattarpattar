package com.example.complexobject.chattarpattar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

public class UserListActivity extends AppCompatActivity {

    private String email =null;
    private SharedPreferences sharedPref;
    private static final String KEY_SHARED_PREF = "ANDROID_WEB_CHAT";
    private static final int KEY_MODE_PRIVATE = 0;

    private WebSocketClient mWebSocketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        sharedPref = this.getApplicationContext().getSharedPreferences(KEY_SHARED_PREF,
                KEY_MODE_PRIVATE);

        Intent i = getIntent();
        email = i.getStringExtra("email");

        mWebSocketClient = MyApplication.getConnection(email);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email);
        editor.commit();
        MyApplication.sendMessageToServer(getJoiningJson());


    }


    public String getJoiningJson()
    {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("type", "JOINING_MESSAGE");

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

}
