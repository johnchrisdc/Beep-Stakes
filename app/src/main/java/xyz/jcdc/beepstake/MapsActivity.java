package xyz.jcdc.beepstake;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import xyz.jcdc.beepstake.fragment.LRT1Fragment;
import xyz.jcdc.beepstake.fragment.LRT2Fragment;
import xyz.jcdc.beepstake.fragment.MRT3Fragment;
import xyz.jcdc.beepstake.fragment.MarkerFragment;
import xyz.jcdc.beepstake.fragment.NearbyBeepSiteDialogFragment;
import xyz.jcdc.beepstake.helper.NumberHelper;
import xyz.jcdc.beepstake.model.LRT1Line;
import xyz.jcdc.beepstake.model.LRT2Line;
import xyz.jcdc.beepstake.model.Line;
import xyz.jcdc.beepstake.model.MRT3Line;
import xyz.jcdc.beepstake.model.Marker;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        Drawer.OnDrawerItemClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener,
        PermissionListener, MarkerFragment.OnMarkerClicked {

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

    private BottomSheetBehavior mBottomSheetBehavior;
    private View mBottomSheet;
    private ViewPager viewPager;
    private List<Marker> beep_markers = new ArrayList<>();

    private BottomSheetBehavior mBottomSheetBehavior_mrt3;
    private View mBottomSheet_mrt3;
    private ViewPager viewPager_mrt3;
    private List<MRT3Line> mrt3_markers = new ArrayList<>();

    private BottomSheetBehavior mBottomSheetBehavior_lrt1;
    private View mBottomSheet_lrt1;
    private ViewPager viewPager_lrt1;
    private List<LRT1Line> lrt_markers = new ArrayList<>();

    private BottomSheetBehavior mBottomSheetBehavior_lrt2;
    private View mBottomSheet_lrt2;
    private ViewPager viewPager_lrt2;
    private List<LRT2Line> lrt2_markers = new ArrayList<>();

    private boolean isPermissionGranted = false;

    private ProgressWheel progressWheel;

    private NearbyBeepSiteDialogFragment nearbyBeepSiteDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(this)
                .check();

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        mBottomSheet_mrt3 = findViewById(R.id.bottom_sheet_mrt3);
        mBottomSheetBehavior_mrt3 = BottomSheetBehavior.from(mBottomSheet_mrt3);

        viewPager_mrt3 = (ViewPager) findViewById(R.id.viewpager_mrt3);

        mBottomSheet_lrt1 = findViewById(R.id.bottom_sheet_lrt1);
        mBottomSheetBehavior_lrt1 = BottomSheetBehavior.from(mBottomSheet_lrt1);

        viewPager_lrt1 = (ViewPager) findViewById(R.id.viewpager_lrt1);

        mBottomSheet_lrt2 = findViewById(R.id.bottom_sheet_lrt2);
        mBottomSheetBehavior_lrt2 = BottomSheetBehavior.from(mBottomSheet_lrt2);

        viewPager_lrt2 = (ViewPager) findViewById(R.id.viewpager_lrt2);

        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);

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
    public void onPermissionDenied(PermissionDeniedResponse response) {
        isPermissionGranted = false;

        setDefaultLocation();
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        isPermissionGranted = true;

        if (mMap != null) {
            getLocation();

            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
        token.continuePermissionRequest();
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        switch (position) {
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

            case R.id.nearby:
                showNearbyDialog();
                break;

        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.ultra_light_with_labels));

        if (isPermissionGranted)
            getLocation();

        mMap.setPadding(mToolbar.getHeight(), mToolbar.getHeight(), mToolbar.getHeight(), mToolbar.getHeight());

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                collapseBottomSheets();
            }
        });
    }

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker_) {

        if (marker_.getTag() != null) {
            final Line marker = (Line) marker_.getTag();

            if (marker.getGroup_key() == null) {
                viewPager.setCurrentItem(marker.getPosition(), true);

                mBottomSheetBehavior_mrt3.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mBottomSheetBehavior_lrt1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior_lrt2.setState(BottomSheetBehavior.STATE_COLLAPSED);

            } else if (marker.getGroup_key().equalsIgnoreCase(MRT3Line.GROUP_KEY)) {
                viewPager_mrt3.setCurrentItem(marker.getPosition(), true);

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior_mrt3.setState(BottomSheetBehavior.STATE_EXPANDED);
                mBottomSheetBehavior_lrt1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior_lrt2.setState(BottomSheetBehavior.STATE_COLLAPSED);

            } else if (marker.getGroup_key().equalsIgnoreCase(LRT1Line.GROUP_KEY)) {
                viewPager_lrt1.setCurrentItem(marker.getPosition(), true);

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior_mrt3.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior_lrt1.setState(BottomSheetBehavior.STATE_EXPANDED);
                mBottomSheetBehavior_lrt2.setState(BottomSheetBehavior.STATE_COLLAPSED);

            } else if (marker.getGroup_key().equalsIgnoreCase(LRT2Line.GROUP_KEY)) {
                viewPager_lrt2.setCurrentItem(marker.getPosition(), true);

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior_mrt3.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior_lrt1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetBehavior_lrt2.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        }
        return false;
    }

    @Override
    public void onMarkerClicked(Marker marker) {
        LatLng loc = new LatLng(marker.getLat(), marker.getLng());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(loc)
                .zoom(15)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void collapseBottomSheets() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior_mrt3.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior_lrt1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior_lrt2.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    private void showNearbyDialog() {
        if (mLastLocation != null) {
            if (nearbyBeepSiteDialogFragment != null){
                nearbyBeepSiteDialogFragment = new NearbyBeepSiteDialogFragment();
                nearbyBeepSiteDialogFragment.setOnMarkerClicked(this);
                nearbyBeepSiteDialogFragment.setMarkers(beep_markers);
                nearbyBeepSiteDialogFragment.show(getSupportFragmentManager(), "nearby");
            }
        } else {
            new MaterialDialog.Builder(this)
                    .title("Oh snap!")
                    .content("Unable to get current location")
                    .positiveText("Dismiss")
                    .show();
        }
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

    private MaterialDialog materialProgressDialog;

    private void getLocation() {
        if (mGoogleApiClient == null) {

            materialProgressDialog = new MaterialDialog.Builder(this)
                    .title(getString(R.string.dialog_location_title))
                    .content(getString(R.string.dialog_location_message))
                    .progress(true, 0)
                    .show();

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
            } else {
                setDefaultLocation();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        materialProgressDialog.dismiss();

        new GetMarkers().execute();
        new GetMRT3Line().execute();
        new GetLRT1Line().execute();
        new GetLRT2Line().execute();
    }

    private MaterialDialog materialDialog;

    private void setDefaultLocation() {
        materialDialog = new MaterialDialog.Builder(this)
                .title("Oh snap!")
                .content("Unable to get current location, Lemme take you to my favorite LRT station. Arriving at Gilmore Station. Ubos nanaman ang pera sa Gilmore Station.")
                .positiveText("Dismiss")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();

                        LatLng gilmore = new LatLng(14.613494, 121.034195);
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(you));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(gilmore)
                                .zoom(15)
                                .build();

                        if (mMap != null)
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                })
                .show();

        new GetMarkers().execute();
        new GetMRT3Line().execute();
        new GetLRT1Line().execute();
        new GetLRT2Line().execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        materialProgressDialog.dismiss();
        setDefaultLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        materialProgressDialog.dismiss();
        setDefaultLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
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
        protected void onPostExecute(final List<Marker> markers) {
            super.onPostExecute(markers);

            if (markers != null) {
                int x = 0;
                for (Marker marker : markers) {

                    marker.setPosition(x);
                    x++;
                    LatLng marker_position = new LatLng(marker.getLat(), marker.getLng());

                    com.google.android.gms.maps.model.Marker mapMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(marker_position)
                                    .title(marker.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_grey)));
                    mapMarker.setTag(marker);

                    mbeepStations.add(mapMarker);
                    beep_markers.add(marker);

                    if (mLastLocation != null) {
                        Location target = new Location("target");
                        target.setLatitude(marker.getLat());
                        target.setLongitude(marker.getLng());

                        marker.setDistance(mLastLocation.distanceTo(target));
                    }
                }

                if (mLastLocation != null) {
                    nearbyBeepSiteDialogFragment = new NearbyBeepSiteDialogFragment();
                    nearbyBeepSiteDialogFragment.setOnMarkerClicked(MapsActivity.this);
                    nearbyBeepSiteDialogFragment.setMarkers(beep_markers);
                    nearbyBeepSiteDialogFragment.show(getSupportFragmentManager(), "nearby");
                }

                MarkersPagerAdapter markersPagerAdapter = new MarkersPagerAdapter(getSupportFragmentManager(), beep_markers);

                viewPager.setClipToPadding(false);
                viewPager.setPageMargin(5);
                viewPager.setPadding(60, 0, 60, 0);
                viewPager.setOffscreenPageLimit(3);

                viewPager.setAdapter(markersPagerAdapter);

                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        LatLng latLng = new LatLng(beep_markers.get(position).getLat(), beep_markers.get(position).getLng());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                progressWheel.setVisibility(View.GONE);
                progressWheel.stopSpinning();

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
                int x = 0;
                for (MRT3Line mrt3Line : mrt3Lines) {
                    mrt3Line.setPosition(x);
                    x++;
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

                    mapMarker.setTag(mrt3Line);

                    mMRT3Stations.add(mapMarker);
                    mrt3_markers.add(mrt3Line);
                }

                MRT3PagerAdapter markersPagerAdapter = new MRT3PagerAdapter(getSupportFragmentManager(), mrt3_markers);

                viewPager_mrt3.setClipToPadding(false);
                viewPager_mrt3.setPageMargin(5);
                viewPager_mrt3.setPadding(60, 0, 60, 0);
                viewPager_mrt3.setOffscreenPageLimit(3);

                viewPager_mrt3.setAdapter(markersPagerAdapter);

                viewPager_mrt3.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        LatLng latLng = new LatLng(mrt3_markers.get(position).getLat(), mrt3_markers.get(position).getLng());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

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
                int x = 0;
                for (LRT1Line lrt1Line : lrt1Lines) {
                    lrt1Line.setPosition(x);
                    x++;
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

                    mapMarker.setTag(lrt1Line);

                    mLRT1Stations.add(mapMarker);
                    lrt_markers.add(lrt1Line);
                }

                LRT1PagerAdapter markersPagerAdapter = new LRT1PagerAdapter(getSupportFragmentManager(), lrt_markers);

                viewPager_lrt1.setClipToPadding(false);
                viewPager_lrt1.setPageMargin(5);
                viewPager_lrt1.setPadding(60, 0, 60, 0);
                viewPager_lrt1.setOffscreenPageLimit(3);

                viewPager_lrt1.setAdapter(markersPagerAdapter);

                viewPager_lrt1.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        LatLng latLng = new LatLng(lrt_markers.get(position).getLat(), lrt_markers.get(position).getLng());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

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
                int x = 0;
                for (LRT2Line lrt2Line : lrt2Lines) {
                    lrt2Line.setPosition(x);
                    x++;
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

                    mapMarker.setTag(lrt2Line);

                    mLRT2Stations.add(mapMarker);
                    lrt2_markers.add(lrt2Line);
                }

                LRT2PagerAdapter markersPagerAdapter = new LRT2PagerAdapter(getSupportFragmentManager(), lrt2_markers);

                viewPager_lrt2.setClipToPadding(false);
                viewPager_lrt2.setPageMargin(5);
                viewPager_lrt2.setPadding(60, 0, 60, 0);
                viewPager_lrt2.setOffscreenPageLimit(3);

                viewPager_lrt2.setAdapter(markersPagerAdapter);

                viewPager_lrt2.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        LatLng latLng = new LatLng(lrt2_markers.get(position).getLat(), lrt2_markers.get(position).getLng());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

            }

        }
    }

    private class MarkersPagerAdapter extends FragmentStatePagerAdapter {

        List<Marker> markers;

        public MarkersPagerAdapter(FragmentManager fm, List<Marker> markers) {
            super(fm);
            this.markers = markers;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new MarkerFragment();
            ((MarkerFragment) fragment).setMarker(markers.get(position));
            ((MarkerFragment) fragment).setMy_location(mLastLocation);
            return fragment;
        }

        @Override
        public int getCount() {
            return markers.size();
        }

        public List<Marker> getMarkers() {
            return markers;
        }

        public void setMarkers(List<Marker> markers) {
            this.markers = markers;
        }
    }

    private class MRT3PagerAdapter extends FragmentStatePagerAdapter {

        List<MRT3Line> markers;

        public MRT3PagerAdapter(FragmentManager fm, List<MRT3Line> markers) {
            super(fm);
            this.markers = markers;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new MRT3Fragment();
            ((MRT3Fragment) fragment).setMarker(markers.get(position));
            ((MRT3Fragment) fragment).setMy_location(mLastLocation);
            return fragment;
        }

        @Override
        public int getCount() {
            return markers.size();
        }

        public List<MRT3Line> getMarkers() {
            return markers;
        }

        public void setMarkers(List<MRT3Line> markers) {
            this.markers = markers;
        }
    }

    private class LRT1PagerAdapter extends FragmentStatePagerAdapter {

        List<LRT1Line> markers;

        public LRT1PagerAdapter(FragmentManager fm, List<LRT1Line> markers) {
            super(fm);
            this.markers = markers;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new LRT1Fragment();
            ((LRT1Fragment) fragment).setMarker(markers.get(position));
            ((LRT1Fragment) fragment).setMy_location(mLastLocation);
            return fragment;
        }

        @Override
        public int getCount() {
            return markers.size();
        }

        public List<LRT1Line> getMarkers() {
            return markers;
        }

        public void setMarkers(List<LRT1Line> markers) {
            this.markers = markers;
        }
    }

    private class LRT2PagerAdapter extends FragmentStatePagerAdapter {

        List<LRT2Line> markers;

        public LRT2PagerAdapter(FragmentManager fm, List<LRT2Line> markers) {
            super(fm);
            this.markers = markers;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new LRT2Fragment();
            ((LRT2Fragment) fragment).setMarker(markers.get(position));
            ((LRT2Fragment) fragment).setMy_location(mLastLocation);
            return fragment;
        }

        @Override
        public int getCount() {
            return markers.size();
        }

        public List<LRT2Line> getMarkers() {
            return markers;
        }

        public void setMarkers(List<LRT2Line> markers) {
            this.markers = markers;
        }
    }

}
