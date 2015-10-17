package se.driessen.johan.lightson;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by johan on 2015-10-17.
 */
public class Hue {
    private static final String BASE_URL = "http://192.168.2.150/api/johandevuser";

    private Context context;

    public Hue(Context context) {
        this.context = context;
        Ion.getDefault(context).configure().setLogging("HUE-ION", Log.DEBUG);
    }

    public void turnLightOn(int light) {
        JsonObject json = new JsonObject();
        json.addProperty("on", true);

        putToHue(String.format("/lights/%d/state", light), json);
    }

    public void turnLightOff(int light) {
        JsonObject json = new JsonObject();
        json.addProperty("on", false);

        putToHue(String.format("/lights/%d/state", light), json);
    }

    private void putToHue(String resource, JsonObject json) {
        Ion.with(context)
                .load("PUT", BASE_URL + resource)
                .setJsonObjectBody(json)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null)
                            Log.e("HUE", e.getMessage());
                    }
                });
    }
}
