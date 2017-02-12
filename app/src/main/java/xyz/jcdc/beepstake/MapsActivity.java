package xyz.jcdc.beepstake;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import xyz.jcdc.beepstake.model.LRT1Line;
import xyz.jcdc.beepstake.model.LRT2Line;
import xyz.jcdc.beepstake.model.MRT3Line;
import xyz.jcdc.beepstake.model.Marker;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Context mContext;

    private GoogleMap mMap;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_maps);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.hopper));

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        new GetMarkers().execute();
        new GetMRT3Line().execute();
        new GetLRT1Line().execute();
        new GetLRT2Line().execute();
    }

    private class GetMarkers extends AsyncTask<String, String, List<Marker>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Marker> doInBackground(String... strings) {
            try {
                return Marker.getMarkers();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Marker> markers) {
            super.onPostExecute(markers);

            if (markers != null) {
                for (Marker marker : markers){
                    LatLng marker_position = new LatLng(marker.getLat(), marker.getLng());
                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(marker_position)
                                    .title(marker.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_teal)));
                }
            }

        }
    }

    private class GetMRT3Line extends AsyncTask<String, String, List<MRT3Line>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<MRT3Line> doInBackground(String... strings) {
            try {
                return MRT3Line.getLine();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MRT3Line> mrt3Lines) {
            super.onPostExecute(mrt3Lines);

            if (mrt3Lines != null){
                LatLng prev_LatLng = null;
                for (MRT3Line mrt3Line : mrt3Lines){
                    if (prev_LatLng != null){
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(prev_LatLng, new LatLng(mrt3Line.getLat(), mrt3Line.getLng()))
                                .width(5)
                                .color(ContextCompat.getColor(mContext, R.color.color_mrt3)));
                    }
                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(mrt3Line.getLat(), mrt3Line.getLng()))
                                    .title(mrt3Line.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_train_mrt3)));
                    prev_LatLng = new LatLng(mrt3Line.getLat(), mrt3Line.getLng());
                }

            }

        }
    }

    private class GetLRT1Line extends AsyncTask<String, String, List<LRT1Line>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LRT1Line> doInBackground(String... strings) {
            try {
                return LRT1Line.getLine();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<LRT1Line> lrt1Lines) {
            super.onPostExecute(lrt1Lines);

            if (lrt1Lines != null){
                LatLng prev_LatLng = null;
                for (LRT1Line lrt1Line : lrt1Lines){
                    if (prev_LatLng != null){
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(prev_LatLng, new LatLng(lrt1Line.getLat(), lrt1Line.getLng()))
                                .width(5)
                                .color(ContextCompat.getColor(mContext, R.color.color_lrt1)));
                    }

                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(lrt1Line.getLat(), lrt1Line.getLng()))
                                    .title(lrt1Line.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_train_lrt1)));

                    prev_LatLng = new LatLng(lrt1Line.getLat(), lrt1Line.getLng());
                }

            }

        }
    }

    private class GetLRT2Line extends AsyncTask<String, String, List<LRT2Line>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LRT2Line> doInBackground(String... strings) {
            try {
                return LRT2Line.getLine();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<LRT2Line> lrt2Lines) {
            super.onPostExecute(lrt2Lines);

            if (lrt2Lines != null){
                LatLng prev_LatLng = null;
                for (LRT2Line lrt2Line : lrt2Lines){
                    if (prev_LatLng != null){
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(prev_LatLng, new LatLng(lrt2Line.getLat(), lrt2Line.getLng()))
                                .width(5)
                                .color(ContextCompat.getColor(mContext, R.color.color_lrt2)));
                    }

                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(lrt2Line.getLat(), lrt2Line.getLng()))
                                    .title(lrt2Line.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_train_lrt2)));

                    prev_LatLng = new LatLng(lrt2Line.getLat(), lrt2Line.getLng());
                }

            }

        }
    }

}
