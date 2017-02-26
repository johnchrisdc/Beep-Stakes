package xyz.jcdc.beepstake.model;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import xyz.jcdc.beepstake.R;

/**
 * Created by jcdc on 2/26/17.
 */

public class BGCBusStop {

    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public static class Feature {
        private Geometry geometry;
        private Properties properties;
        private int position;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

        public static class Geometry {
            double[] coordinates;

            public double[] getCoordinates() {
                return coordinates;
            }

            public void setCoordinates(double[] coordinates) {
                this.coordinates = coordinates;
            }
        }

        public static class Properties {
            private String name;
            private String description;

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }

    public static BGCBusStop getBgcBusStop(Context context) throws UnsupportedEncodingException {
        InputStream is = context.getResources().openRawResource(R.raw.bgc_bus_stops);
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        return new Gson().fromJson(reader, BGCBusStop.class);
    }

}
