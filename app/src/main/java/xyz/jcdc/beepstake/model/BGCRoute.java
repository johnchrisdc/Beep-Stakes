package xyz.jcdc.beepstake.model;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import xyz.jcdc.beepstake.R;

/**
 * Created by jcdc on 2/25/17.
 */

public class BGCRoute {

    List<double[]> coordinates = new ArrayList<>();

    public BGCRoute() {
    }

    public List<double[]> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<double[]> coordinates) {
        this.coordinates = coordinates;
    }

    public static BGCRoute getCentralRoute(Context context) throws UnsupportedEncodingException {
        InputStream is = context.getResources().openRawResource(R.raw.bgc_central_route);
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        return new Gson().fromJson(reader, BGCRoute.class);
    }

    public static BGCRoute getEastRoute(Context context) throws UnsupportedEncodingException {
        InputStream is = context.getResources().openRawResource(R.raw.bgc_east_route);
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        return new Gson().fromJson(reader, BGCRoute.class);
    }

    public static BGCRoute getUpperWestRoute(Context context) throws UnsupportedEncodingException {
        InputStream is = context.getResources().openRawResource(R.raw.bgc_upper_west_route);
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        return new Gson().fromJson(reader, BGCRoute.class);
    }

    public static BGCRoute getLowerWestRoute(Context context) throws UnsupportedEncodingException {
        InputStream is = context.getResources().openRawResource(R.raw.bgc_lower_west_route);
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        return new Gson().fromJson(reader, BGCRoute.class);
    }

    public static BGCRoute getNightRoute(Context context) throws UnsupportedEncodingException {
        InputStream is = context.getResources().openRawResource(R.raw.bgc_night_route);
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        return new Gson().fromJson(reader, BGCRoute.class);
    }

}