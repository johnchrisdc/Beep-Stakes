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

public class MRT3Line extends Line {

    public static final String GROUP_KEY = "mrt3-line";

    public static List<MRT3Line> getLine() throws Exception {

        OkHttpClient client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();

        Request request = new Request.Builder()
                .url(Variables.LINES_MRT3)
                .build();

        Response response = client.newCall(request).execute();
        String body = response.body().string();

        Type listType = new TypeToken<List<MRT3Line>>() {}.getType();

        return new Gson().fromJson(body, listType);
    }

}
