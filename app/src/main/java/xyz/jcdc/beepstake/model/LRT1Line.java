package xyz.jcdc.beepstake.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.jcdc.beepstake.Variables;

/**
 * Created by jcdc on 2/12/17.
 */

public class LRT1Line extends Line {

    public static final String GROUP_KEY = "lrt1-line";

    public static List<LRT1Line> getLine() throws Exception {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Variables.LINES_LRT1)
                .build();

        Response response = client.newCall(request).execute();
        String body = response.body().string();

        Type listType = new TypeToken<List<LRT1Line>>() {}.getType();

        return new Gson().fromJson(body, listType);
    }
}
