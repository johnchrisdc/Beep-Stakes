package xyz.jcdc.beepstake.model;

/**
 * Created by jcdc on 2/12/17.
 */

public class Line {

    private String marker_type;
    private String name;
    private String address;
    private String group_key;
    private double lng;
    private double lat;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGroup_key() {
        return group_key;
    }

    public void setGroup_key(String group_key) {
        this.group_key = group_key;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getMarker_type() {
        return marker_type;
    }

    public void setMarker_type(String marker_type) {
        this.marker_type = marker_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
