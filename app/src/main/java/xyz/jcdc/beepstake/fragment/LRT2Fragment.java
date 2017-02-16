package xyz.jcdc.beepstake.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.jcdc.beepstake.R;
import xyz.jcdc.beepstake.model.LRT2Line;

/**
 * Created by jcdc on 2/14/17.
 */

public class LRT2Fragment extends Fragment {

    private LRT2Line marker;

    private TextView place_name;
    private TextView place_address;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.row_lrt2, container, false);

        place_name = (TextView) v.findViewById(R.id.place_name) ;
        place_address = (TextView) v.findViewById(R.id.place_address);

        if (marker != null){
            place_name.setText(marker.getName());
            place_address.setText("LRT 2");
        }

        return v;
    }

    public void setMarker(LRT2Line marker) {
        this.marker = marker;
    }
}
