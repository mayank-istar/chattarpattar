package com.example.complexobject.chattarpattar;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import other.Message;
import other.MessagesListAdapter;
import other.Utils;

public class MainActivity extends Activity {

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnSend;
    private Button btnJM;
    private Button Notification;
    private EditText inputMsg;

    private WebSocketClient mWebSocketClient;

    // Chat messages list adapter
    private MessagesListAdapter adapter;
    private List<Message> listMessages;
    private ListView listViewMessages;

    private Utils utils;

    // Client name
    private Integer clientId = 3;
    private String name = null;
    private String email =null;
    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "message", TAG_EXIT = "exit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting the person name from previous screen
        Intent i = getIntent();
        email = i.getStringExtra("email");

        mWebSocketClient = MyApplication.getConnection(email);

        btnSend = (Button) findViewById(R.id.btnSend);
        btnJM = (Button)findViewById(R.id.btnJOIN_MSG);
        Notification = (Button) findViewById(R.id.btnNotice);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);

        utils = new Utils(getApplicationContext());

        Notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageToServer(utils.getNotificationMessageJSON(inputMsg.getText()
                        .toString()));

                inputMsg.setText("");
            }
        });

        btnJM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageToServer(utils.getJoiningJson());
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Sending message to web socket server
                  sendMessageToServer(utils.getSendMessageJSON(inputMsg.getText()
                            .toString()));

                inputMsg.setText("");
            }
        });



        listMessages = new ArrayList<Message>();

        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);


    }

    private void connectWebSocket() {
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
               // parseMessage(message);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                utils.storeSessionId(null);

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


    /**
     * Method to send message to web socket server
     * */
    private void sendMessageToServer(String message) {
        System.out.println("sending message to server "+message);
        if (mWebSocketClient != null && mWebSocketClient.getReadyState()== org.java_websocket.WebSocket.READYSTATE.OPEN) {
            mWebSocketClient.send(message);
        }
        else
        {
            System.out.println(mWebSocketClient.getReadyState());
        }
    }


    /**
     * Parsing the JSON message received from server The intent of message will
     * be identified by JSON node 'flag'. flag = self, message belongs to the
     * person. flag = new, a new person joined the conversation. flag = message,
     * a new message received from server. flag = exit, somebody left the
     * conversation.
     * */
    private void parseMessage(final String msg) {
    System.out.println("message got in parseMEssage>>>>>>>>>>>>>>>>>>>>>>"+msg);
        try {
            JSONObject jObj = new JSONObject(msg);
            String type = jObj.getString("type");
            switch (type)
            {
                case "JOINING_MESSAGE":
                    String sessionId = jObj.getString("sessionId");
                    utils.storeSessionId(sessionId);
                    break;
                case "USER_CHAT":

                   break;
            }

            /*// JSON node 'flag'
            String flag = jObj.getString("flag");

            // if flag is 'self', this JSON contains session id
            if (flag.equalsIgnoreCase(TAG_SELF)) {

                String sessionId = jObj.getString("sessionId");

                // Save the session id in shared preferences
                utils.storeSessionId(sessionId);

                Log.e(TAG, "Your session id: " + utils.getSessionId());

            } else if (flag.equalsIgnoreCase(TAG_NEW)) {
                // If the flag is 'new', new person joined the room
                String name = jObj.getString("name");
                String message = jObj.getString("message");

                // number of people online
                String onlineCount = jObj.getString("onlineCount");

                showToast(name + message + ". Currently " + onlineCount
                        + " people online!");

            } else if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
                // if the flag is 'message', new message received
                String fromName = name;
                String message = jObj.getString("message");
                System.out.println("message is "+message);
                //String sessionId = jObj.getString("sessionId");
                boolean isSelf = true;

                // Checking if the message was sent by you
                //if (!sessionId.equals(utils.getSessionId())) {
                  //  fromName = jObj.getString("name");
                   // isSelf = false;
                //}

                Message m = new Message(fromName, message, isSelf);

                // Appending the message to chat list
                appendMessage(m);

            } else if (flag.equalsIgnoreCase(TAG_EXIT)) {
                // If the flag is 'exit', somebody left the conversation
                String name = jObj.getString("name");
                String message = jObj.getString("message");

                showToast(name + message);
            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    /**
     * Appending message to list view
     * */
    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listMessages.add(m);

                adapter.notifyDataSetChanged();

                // Playing device's notification
                playBeep();
            }
        });
    }

    private void showToast(final String message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Plays device's default notification sound
     * */
    public void playBeep() {

        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();


}
