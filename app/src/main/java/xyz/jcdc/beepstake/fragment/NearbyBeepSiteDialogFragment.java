package xyz.jcdc.beepstake.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.jcdc.beepstake.R;
import xyz.jcdc.beepstake.model.Marker;

/**
 * Created by jcdc on 2/18/17.
 */

public class NearbyBeepSiteDialogFragment extends AppCompatDialogFragment implements MarkerFragment.OnMarkerClicked {

    private Context context;

    private List<Marker> markers;

    private ViewPager viewPager;

    private MarkerFragment.OnMarkerClicked onMarkerClicked;

    public void setOnMarkerClicked(MarkerFragment.OnMarkerClicked onMarkerClicked) {
        this.onMarkerClicked = onMarkerClicked;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        View v = inflater.inflate(R.layout.nearby_beep_site_dialog_fragment, container, false);

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);

        if (markers != null) {
            List<Marker> markers_ = new ArrayList<>();

            for (Marker marker : markers){
                markers_.add(marker);
            }
            Collections.sort(markers_);
            MarkersPagerAdapter markersPagerAdapter = new MarkersPagerAdapter(getChildFragmentManager(), markers_.subList(0, 5));
            markersPagerAdapter.setOnMarkerClicked(this);
            viewPager.setClipToPadding(false);
            viewPager.setPageMargin(5);
            viewPager.setPadding(60, 0, 60, 0);
            viewPager.setOffscreenPageLimit(3);

            viewPager.setAdapter(markersPagerAdapter);

        }
        return v;
    }

    @Override
    public void onMarkerClicked(Marker marker) {
        onMarkerClicked.onMarkerClicked(marker);
        dismiss();
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<Marker> markers) {
        this.markers = markers;
    }

    private class MarkersPagerAdapter extends FragmentStatePagerAdapter implements MarkerFragment.OnMarkerClicked {

        List<Marker> markers;

        private MarkerFragment.OnMarkerClicked onMarkerClicked;

        public void setOnMarkerClicked(MarkerFragment.OnMarkerClicked onMarkerClicked) {
            this.onMarkerClicked = onMarkerClicked;
        }

        public MarkersPagerAdapter(FragmentManager fm, List<Marker> markers) {
            super(fm);
            this.markers = markers;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new MarkerFragment();
            ((MarkerFragment) fragment).setMarker(markers.get(position));
            ((MarkerFragment) fragment).setOnMarkerClicked(this);
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

        @Override
        public void onMarkerClicked(Marker marker) {
            onMarkerClicked.onMarkerClicked(marker);
        }
    }
}
