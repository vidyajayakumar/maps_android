package com.vidya.maps_vidya_c0778642;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public
class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowCloseListener, OnMarkerClickListener, OnMapClickListener, OnMapLongClickListener, OnPolygonClickListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap googleMap;

    private ArrayList<LatLng> latLngs;


    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        latLngs = new ArrayList<>();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public
    void onMapReady(GoogleMap map) {
        googleMap = map;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Set listeners for click events.
//        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnPolygonClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerDragListener(this);

    }

    // [maps_poly_activity_on_map_ready]
    Polygon polygon;

    private
    void markQuadrilateral() {
        // [START maps_poly_activity_add_polygon]
        // Add polygons to indicate areas on the map.

        if (latLngs.size() == 4) {
//            polygon.remove();

            polygon = googleMap.addPolygon(new PolygonOptions()
                                                   .clickable(true)
                                                   .fillColor(3)
                                                   .add(new LatLng(latLngs.get(0).latitude, latLngs.get(0).longitude),
                                                        new LatLng(latLngs.get(1).latitude, latLngs.get(1).longitude),
                                                        new LatLng(latLngs.get(2).latitude, latLngs.get(2).longitude),
                                                        new LatLng(latLngs.get(3).latitude, latLngs.get(3).longitude)));

            // Store a data object with the polygon, used here to indicate an arbitrary type.
            polygon.setTag("alpha");
            // [END maps_poly_activity_add_polygon]

            // Style the polygon.
            stylePolygon(polygon);
            distance();

            // Position the map's camera near Alice Springs in the center of Australia,
            // and set the zoom factor so most of Australia shows on the screen.
//    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-28, 100), 4));
        }

    }
    // [END maps_poly_activity_on_map_ready]


    // [START maps_poly_activity_style_polyline]
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 4;

    /**
     * Styles the polyline, based on type.
     *
     * @param polyline The polyline object that needs styling.
     */
    private
    void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10)
                );
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }
    // [END maps_poly_activity_style_polyline]

    // [START maps_poly_activity_on_polyline_click]
    private static final int PATTERN_GAP_LENGTH_PX = 15;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    /**
     * Listens for clicks on a polyline.
     *
     * @param polyline The polyline object that the user has clicked.
     */
//    @Override
    public
    void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                       Toast.LENGTH_SHORT).show();
    }
    // [END maps_poly_activity_on_polyline_click]

    /**
     * Listens for clicks on a polygon.
     *
     * @param polygon The polygon object that the user has clicked.
     */
//    @Override
    public
    void onPolygonClick(Polygon polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        int color = polygon.getStrokeColor() ^ 0x00ffffff;
        polygon.setStrokeColor(color);
        color = polygon.getFillColor() ^ 0x00ffffff;
        polygon.setFillColor(color);
    }

    private static final int COLOR_WHITE_ARGB = 0xffffffff;

    /**
     * Styles the polygon, based on type.
     *
     * @param polygon The polygon object that needs styling.
     */
    private
    void stylePolygon(Polygon polygon) {

        int strokeColor = 0xfff00000; //red
        int fillColor   = 0xff81C784;

        polygon.setStrokePattern(null);
        polygon.setStrokeWidth(8);
        polygon.setStrokeColor(Color.RED);
        polygon.setFillColor(0x5900FF00);
    }


    double dist[] = {0, 0, 0, 0};

    private
    void distance() {
        dist = null;
//        ArrayList<double> dist = new ArrayList<>();
        for (int i = 0; i < latLngs.size()-1;i++) {
            double lon1  = latLngs.get(i).longitude;
            double lon2  = latLngs.get(i+1).longitude;
            double lat1  = latLngs.get(i).latitude;
            double lat2  = latLngs.get(i+1).latitude;
            double theta = lon1 - lon2;
            double temp = Math.sin(deg2rad(lat1))
                    * Math.sin(deg2rad(lat2))
                    + Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2))
                    * Math.cos(deg2rad(theta));
            temp    = Math.acos(temp);
            temp    = rad2deg(temp);
            temp    = temp * 60 * 1.1515;
            dist[i] = temp;
            System.out.printf("distance%s%n", temp);
            Log.i(TAG, "distance: " + temp);

        }
        Log.i(TAG, "distance: " + dist);
//        return dist;
    }

    private
    double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private
    double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public
    void onMapClick(LatLng latLng) {

    }

    @Override
    public
    void onMapLongClick(LatLng latLng) {
        if (latLngs.size() < 4) {
            latLngs.add(latLng);
            LatLng mlat = latLng;
            markLocation(latLng);
        } else {
            googleMap.clear();
            latLngs.clear();
        }
    }

    private
    void markLocation(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Log.i(TAG, "onMapLongClick: " + listAddresses.get(0).toString());


            if (listAddresses != null && listAddresses.size() > 0) {
                String address = "";
                String title   = "";
                String snippet = "";


                if (listAddresses.get(0).getThoroughfare() != null) {
                    address += listAddresses.get(0).getThoroughfare() + " ";
                    title += listAddresses.get(0).getThoroughfare() + " ";
                }
                if (listAddresses.get(0).getSubThoroughfare() != null) {
                    address += listAddresses.get(0).getSubThoroughfare() + " ";
                    title += listAddresses.get(0).getSubThoroughfare() + " ";
                }

                if (listAddresses.get(0).getLocality() != null) {
                    address += listAddresses.get(0).getLocality() + " ";
                    snippet += listAddresses.get(0).getLocality() + " ";
                }

                if (listAddresses.get(0).getPostalCode() != null) {
                    address += listAddresses.get(0).getPostalCode() + " ";
                    title += listAddresses.get(0).getPostalCode();
                }

                if (listAddresses.get(0).getAdminArea() != null) {
                    address += listAddresses.get(0).getAdminArea();
                    snippet += listAddresses.get(0).getAdminArea();
                }

                Toast.makeText(MapsActivity.this, address, Toast.LENGTH_SHORT).show();
                Log.i("Address", address);


                Marker m1 = googleMap.addMarker(new MarkerOptions()
                                                        .position(latLng)
                                                        .title(title)
                                                        .snippet(snippet)
                                                        .draggable(true));
                markQuadrilateral();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public
    boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public
    void onInfoWindowClick(Marker marker) {

    }

    @Override
    public
    void onInfoWindowClose(Marker marker) {

    }

    LatLng tempLat;
    int position;

    @Override
    public
    void onMarkerDragStart(Marker marker) {
        Log.i(TAG, "onMarkerDragStart: " + marker);

        tempLat = marker.getPosition();
        //        position = -1;
        for (int i = 0; i < latLngs.size(); i++) {
            if (latLngs.get(i) == tempLat) {
                position = i;
                polygon.remove();
                break;  // uncomment to get the first instance
            }
        }
    }

    @Override
    public
    void onMarkerDrag(Marker marker) {
    }

    @Override
    public
    void onMarkerDragEnd(Marker marker) {
        Log.i(TAG, "onMarkerDragEnd: " + marker);
        latLngs.set(position, marker.getPosition());
        markLocation(marker.getPosition());

        markQuadrilateral();

    }
    // [END maps_poly_activity_style_polygon]


}

















