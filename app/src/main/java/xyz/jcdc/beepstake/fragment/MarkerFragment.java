package xyz.jcdc.beepstake.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import xyz.jcdc.beepstake.R;
import xyz.jcdc.beepstake.helper.NumberHelper;
import xyz.jcdc.beepstake.model.Marker;

/**
 * Created by jcdc on 2/14/17.
 */

public class MarkerFragment extends Fragment {

    private Marker marker;

    private TextView place_name;
    private TextView place_address;
    private TextView place_distance;

    private Location my_location;

    private OnMarkerClicked onMarkerClicked;

    public void setOnMarkerClicked(OnMarkerClicked onMarkerClicked) {
        this.onMarkerClicked = onMarkerClicked;
    }

    public interface OnMarkerClicked {
        void onMarkerClicked(Marker marker);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.row_marker, container, false);

        place_name = (TextView) v.findViewById(R.id.place_name) ;
        place_address = (TextView) v.findViewById(R.id.place_address);
        place_distance = (TextView) v.findViewById(R.id.place_distance);

        if (marker != null){
            place_name.setText(marker.getName());
            place_address.setText(marker.getAddress());

            if (my_location != null){

                Location target = new Location("target");
                target.setLatitude(marker.getLat());
                target.setLongitude(marker.getLng());

                place_distance.setText(NumberHelper.formatNumber(my_location.distanceTo(target)) + "m away");
            } else {
                place_distance.setText(NumberHelper.formatNumber(marker.getDistance()) + "m away");
            }

        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMarkerClicked != null)
                    onMarkerClicked.onMarkerClicked(marker);
            }
        });

        return v;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void setMy_location(Location my_location) {
        this.my_location = my_location;
    }
}
