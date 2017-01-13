package com.example.complexobject.chattarpattar;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import other.Utils;

/**
 * Created by ComplexObject on 11-01-2017.
 */

public class MyApplication extends Application {


    private static WebSocketClient mWebSocketClient;

    public static WebSocketClient getConnection(String email) {
        if( mWebSocketClient == null) {
            // initialize connection object here
            connectWebSocket(email);
        }
        return mWebSocketClient;
    }

    public MyApplication() {


    }

    private static void connectWebSocket(String email) {
        System.out.println("making websocket");
        URI uri;
        try {
            uri = new URI("ws://192.168.1.21:4567/chat/"+email);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                //mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                // Message will be in JSON format
                System.out.println("message is "+s);

            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);


            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                e.printStackTrace();
            }
        };
        mWebSocketClient.connect();
        System.out.println(">>>>>>>>"+mWebSocketClient.getReadyState());
    }

    public static void sendMessageToServer(String message) {
        System.out.println("sending message to server "+message);
        if (mWebSocketClient != null && mWebSocketClient.getReadyState()== org.java_websocket.WebSocket.READYSTATE.OPEN) {
            mWebSocketClient.send(message);
        }
        else
        {
            System.out.println(mWebSocketClient.getReadyState());
        }
    }

}
