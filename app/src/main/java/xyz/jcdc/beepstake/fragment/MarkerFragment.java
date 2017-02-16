package xyz.jcdc.beepstake.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.jcdc.beepstake.R;
import xyz.jcdc.beepstake.model.Marker;

/**
 * Created by jcdc on 2/14/17.
 */

public class MarkerFragment extends Fragment {

    private Marker marker;

    private TextView place_name;
    private TextView place_address;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.row_marker, container, false);

        place_name = (TextView) v.findViewById(R.id.place_name) ;
        place_address = (TextView) v.findViewById(R.id.place_address);

        if (marker != null){
            place_name.setText(marker.getName());
            place_address.setText(marker.getAddress());
        }

        return v;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
