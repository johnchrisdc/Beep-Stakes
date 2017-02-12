package xyz.jcdc.beepstake;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import xyz.jcdc.beepstake.model.LRT1Line;
import xyz.jcdc.beepstake.model.LRT2Line;
import xyz.jcdc.beepstake.model.MRT3Line;
import xyz.jcdc.beepstake.model.Marker;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, Drawer.OnDrawerItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;

    private GoogleMap mMap;

    private Toolbar mToolbar;

    private List<com.google.android.gms.maps.model.Marker> mbeepStations = new ArrayList<>();
    private boolean isShowingBeepStations = true;

    private List<com.google.android.gms.maps.model.Marker> mMRT3Stations = new ArrayList<>();
    private List<Polyline> polylines_MRT3 = new ArrayList<>();
    private boolean isShowingMRT3Stations = true;

    private List<com.google.android.gms.maps.model.Marker> mLRT1Stations = new ArrayList<>();
    private List<Polyline> polylines_LRT1 = new ArrayList<>();
    private boolean isShowingLRT1Stations = true;

    private List<com.google.android.gms.maps.model.Marker> mLRT2Stations = new ArrayList<>();
    private List<Polyline> polylines_LRT2 = new ArrayList<>();
    private boolean isShowingLRT2Stations = true;

    private Drawer drawer;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_maps);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        new DrawerBuilder().withActivity(this).build();

        PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(1).withName("Home");
        SecondaryDrawerItem about = new SecondaryDrawerItem().withIdentifier(2).withName("About");

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        home,
                        new DividerDrawerItem(),
                        about
                )
                .withOnDrawerItemClickListener(this)
                .build();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        switch (position){
            case 0:
                break;

            case 2:
                Intent i = new Intent(mContext, AboutActivity.class);
                startActivity(i);
                break;
        }
        drawer.closeDrawer();
        drawer.setSelectionAtPosition(0, false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.layers:
                showLayersDialog();
                break;

        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.ultra_light_with_labels));

        getLocation();

        mMap.setPadding(mToolbar.getHeight(), mToolbar.getHeight(), mToolbar.getHeight(), mToolbar.getHeight());

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        mMap.getUiSettings().setCompassEnabled(true);

        new GetMarkers().execute();
        new GetMRT3Line().execute();
        new GetLRT1Line().execute();
        new GetLRT2Line().execute();
    }

    private void showLayersDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_layers);
        dialog.setTitle("Layers");

        Button dismiss = (Button) dialog.findViewById(R.id.dismiss);
        SwitchCompat switchCompat_beep_sites = (SwitchCompat) dialog.findViewById(R.id.switch_beep_sites);
        SwitchCompat switchCompat_MRT3 = (SwitchCompat) dialog.findViewById(R.id.switch_MRT3);
        SwitchCompat switchCompat_LRT1 = (SwitchCompat) dialog.findViewById(R.id.switch_LRT1);
        SwitchCompat switchCompat_LRT2 = (SwitchCompat) dialog.findViewById(R.id.switch_LRT2);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        switchCompat_beep_sites.setChecked(isShowingBeepStations);
        switchCompat_beep_sites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    addAllBeepSiteMarkers();
                } else {
                    removeAllBeepSiteMarkers();
                }
                isShowingBeepStations = checked;
            }
        });

        switchCompat_MRT3.setChecked(isShowingMRT3Stations);
        switchCompat_MRT3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    addAllMRT3Markers();
                } else {
                    removeAllMRT3Markers();
                }
                isShowingMRT3Stations = checked;
            }
        });

        switchCompat_LRT1.setChecked(isShowingLRT1Stations);
        switchCompat_LRT1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    addAllLRT1Markers();
                } else {
                    removeAllLRT1Markers();
                }
                isShowingLRT1Stations = checked;
            }
        });

        switchCompat_LRT2.setChecked(isShowingLRT2Stations);
        switchCompat_LRT2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    addAllLRT2Markers();
                } else {
                    removeLRT2Markers();
                }
                isShowingLRT2Stations = checked;
            }
        });

        dialog.show();
    }

    private void removeAllBeepSiteMarkers() {
        for (com.google.android.gms.maps.model.Marker marker : mbeepStations) {
            marker.setVisible(false);
        }
    }

    private void addAllBeepSiteMarkers() {
        for (com.google.android.gms.maps.model.Marker marker : mbeepStations) {
            marker.setVisible(true);
        }
    }

    private void removeAllMRT3Markers() {
        for (com.google.android.gms.maps.model.Marker marker : mMRT3Stations) {
            marker.setVisible(false);
        }

        for (Polyline polyline : polylines_MRT3) {
            polyline.setVisible(false);
        }
    }

    private void addAllMRT3Markers() {
        for (com.google.android.gms.maps.model.Marker marker : mMRT3Stations) {
            marker.setVisible(true);
        }

        for (Polyline polyline : polylines_MRT3) {
            polyline.setVisible(true);
        }
    }

    private void removeAllLRT1Markers() {
        for (com.google.android.gms.maps.model.Marker marker : mLRT1Stations) {
            marker.setVisible(false);
        }

        for (Polyline polyline : polylines_LRT1) {
            polyline.setVisible(false);
        }
    }

    private void addAllLRT1Markers() {
        for (com.google.android.gms.maps.model.Marker marker : mLRT1Stations) {
            marker.setVisible(true);
        }

        for (Polyline polyline : polylines_LRT1) {
            polyline.setVisible(true);
        }
    }

    private void removeLRT2Markers() {
        for (com.google.android.gms.maps.model.Marker marker : mLRT2Stations) {
            marker.setVisible(false);
        }

        for (Polyline polyline : polylines_LRT2) {
            polyline.setVisible(false);
        }
    }

    private void addAllLRT2Markers() {
        for (com.google.android.gms.maps.model.Marker marker : mLRT2Stations) {
            marker.setVisible(true);
        }

        for (Polyline polyline : polylines_LRT2) {
            polyline.setVisible(true);
        }
    }

    private void getLocation(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();


            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng you = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(you));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(you)
                        .zoom(15)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        } catch (SecurityException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private class GetMarkers extends AsyncTask<String, String, List<Marker>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Marker> doInBackground(String... strings) {
            try {
                return Marker.getMarkers();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Marker> markers) {
            super.onPostExecute(markers);

            if (markers != null) {
                for (Marker marker : markers) {
                    LatLng marker_position = new LatLng(marker.getLat(), marker.getLng());

                    com.google.android.gms.maps.model.Marker mapMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(marker_position)
                                    .title(marker.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_grey)));

                    mbeepStations.add(mapMarker);
                }
            }

        }
    }

    private class GetMRT3Line extends AsyncTask<String, String, List<MRT3Line>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<MRT3Line> doInBackground(String... strings) {
            try {
                return MRT3Line.getLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MRT3Line> mrt3Lines) {
            super.onPostExecute(mrt3Lines);

            if (mrt3Lines != null) {
                LatLng prev_LatLng = null;
                for (MRT3Line mrt3Line : mrt3Lines) {
                    if (prev_LatLng != null) {
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(prev_LatLng, new LatLng(mrt3Line.getLat(), mrt3Line.getLng()))
                                .width(5)
                                .color(ContextCompat.getColor(mContext, R.color.color_mrt3)));
                        polylines_MRT3.add(line);
                    }
                    com.google.android.gms.maps.model.Marker mapMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(mrt3Line.getLat(), mrt3Line.getLng()))
                                    .title(mrt3Line.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_train_mrt3)));
                    prev_LatLng = new LatLng(mrt3Line.getLat(), mrt3Line.getLng());

                    mMRT3Stations.add(mapMarker);
                }

            }

        }
    }

    private class GetLRT1Line extends AsyncTask<String, String, List<LRT1Line>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LRT1Line> doInBackground(String... strings) {
            try {
                return LRT1Line.getLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<LRT1Line> lrt1Lines) {
            super.onPostExecute(lrt1Lines);

            if (lrt1Lines != null) {
                LatLng prev_LatLng = null;
                for (LRT1Line lrt1Line : lrt1Lines) {
                    if (prev_LatLng != null) {
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(prev_LatLng, new LatLng(lrt1Line.getLat(), lrt1Line.getLng()))
                                .width(5)
                                .color(ContextCompat.getColor(mContext, R.color.color_lrt1)));
                        polylines_LRT1.add(line);
                    }

                    com.google.android.gms.maps.model.Marker mapMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(lrt1Line.getLat(), lrt1Line.getLng()))
                                    .title(lrt1Line.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_train_lrt1)));

                    prev_LatLng = new LatLng(lrt1Line.getLat(), lrt1Line.getLng());

                    mLRT1Stations.add(mapMarker);
                }

            }

        }
    }

    private class GetLRT2Line extends AsyncTask<String, String, List<LRT2Line>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LRT2Line> doInBackground(String... strings) {
            try {
                return LRT2Line.getLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<LRT2Line> lrt2Lines) {
            super.onPostExecute(lrt2Lines);

            if (lrt2Lines != null) {
                LatLng prev_LatLng = null;
                for (LRT2Line lrt2Line : lrt2Lines) {
                    if (prev_LatLng != null) {
                        Polyline line = mMap.addPolyline(new PolylineOptions()
                                .add(prev_LatLng, new LatLng(lrt2Line.getLat(), lrt2Line.getLng()))
                                .width(5)
                                .color(ContextCompat.getColor(mContext, R.color.color_lrt2)));
                        polylines_LRT2.add(line);
                    }

                    com.google.android.gms.maps.model.Marker mapMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(new LatLng(lrt2Line.getLat(), lrt2Line.getLng()))
                                    .title(lrt2Line.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_train_lrt2)));

                    prev_LatLng = new LatLng(lrt2Line.getLat(), lrt2Line.getLng());

                    mLRT2Stations.add(mapMarker);
                }

            }

        }
    }

}
