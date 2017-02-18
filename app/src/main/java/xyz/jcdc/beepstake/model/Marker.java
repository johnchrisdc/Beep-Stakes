package xyz.jcdc.beepstake.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.jcdc.beepstake.Variables;

/**
 * Created by jcdc on 2/12/17.
 */

public class Marker extends Line implements Serializable, Comparable<Marker>{

    private float distance;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public static List<Marker> getMarkers() throws Exception {

        OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();

        Request request = new Request.Builder()
                .url(Variables.MARKERS)
                .build();

        Response response = client.newCall(request).execute();
        String body = response.body().string();

        Type listType = new TypeToken<List<Marker>>() {}.getType();

        return new Gson().fromJson(body, listType);
    }

    @Override
    public int compareTo(Marker another) {
        int lastCmp = Float.compare(distance, another.getDistance());
        return (lastCmp != 0 ? lastCmp : Float.compare(distance, another.getDistance()));
    }

}
