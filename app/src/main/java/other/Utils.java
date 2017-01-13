package other;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ComplexObject on 10-01-2017.
 */

public class Utils {
    private Context context;
    private SharedPreferences sharedPref;

    private static final String KEY_SHARED_PREF = "ANDROID_WEB_CHAT";
    private static final int KEY_MODE_PRIVATE = 0;
    private static final String KEY_SESSION_ID = "sessionId",
            FLAG_MESSAGE = "message";

    public Utils(Context context) {
        this.context = context;
        sharedPref = this.context.getSharedPreferences(KEY_SHARED_PREF,
                KEY_MODE_PRIVATE);
    }

    public void storeSessionId(String sessionId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_SESSION_ID, sessionId);
        editor.commit();
    }

    public String getSessionId() {
        return sharedPref.getString(KEY_SESSION_ID, null);
    }

    public String getDoubtMessageJSON(String message)
    {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("type", "ASK_DOUBT");
            jObj.put("sessionId", getSessionId());
            jObj.put("message", message);
            jObj.put("senderId", 3);
            jObj.put("slideId", 55);
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
    public String getGroupMessageJSON(String message)
    {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("type", "GROUP_CHAT");
            jObj.put("sessionId", getSessionId());
            jObj.put("message", message);
            jObj.put("senderId", 3);
            jObj.put("groupId", 1);
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
    public String getSendMessageJSON(String message) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("type", "USER_CHAT");
            jObj.put("sessionId", getSessionId());
            jObj.put("message", message);
            jObj.put("senderId", 3);
            jObj.put("receiverId", 3);
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
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

    public String getNotificationMessageJSON(String s) {

String str="";
        JSONArray userArray = new JSONArray();


        try {
            JSONObject obj1 = new JSONObject();
            obj1.put("id", 3);
            JSONObject obj2 = new JSONObject();
            obj2.put("id", 12);
            userArray.put(obj1);
            userArray.put(obj2);
            JSONObject data = new JSONObject();
            data.put("userIds", userArray);
            data.put("notification_type", "UPDATE_COMPLEX_OBJECT");
            data.put("message", "Update Complex Object Update Complex Object Update Complex Object");
            data.put("type", "NOTIFICATION");
            str =  data.toString();

            /*data.put("batch_group_id",35);
		    data.put("sender_id",5);*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
return str;
    }


}
