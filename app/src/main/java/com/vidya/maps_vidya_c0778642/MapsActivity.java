package com.vidya.maps_vidya_c0778642;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public
class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowCloseListener,
        OnMarkerClickListener,
        OnMapClickListener,
        OnMapLongClickListener,
        OnPolygonClickListener,
        GoogleMap.OnPolylineClickListener {
    private static final String TAG = "MapsActivity";
    private static final int PATTERN_GAP_LENGTH_PX = 15;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    int tag = 0;
    private String mtag[] = {"A", "B", "C", "D"};
    private Polygon polygon;
    private double dist[] = {0, 0, 0, 0};
    private LatLng tempLat;
    private int position;
    private GoogleMap googleMap;
    private ArrayList<LatLng> latLngs;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private UiSettings uiSetting;
    private Marker mSelectedMarker;

    public
    void centerMapOnLocation(Location location, String title) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
    }

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

        locationManager  = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public
            void onLocationChanged(Location location) {
                centerMapOnLocation(location, "Your Location");
            }

            @Override
            public
            void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public
            void onProviderEnabled(String provider) {
            }

            @Override
            public
            void onProviderDisabled(String provider) {
            }
        };
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
        uiSetting = googleMap.getUiSettings();
        uiSetting.setMyLocationButtonEnabled(true);
        //        googleMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(44, -80);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 5));


        // Set listeners for click events.
        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnPolygonClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMarkerClickListener(this);


        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                                                           .clickable(true)
                                                           .add(
                                                                   new LatLng(-35.016, 143.321),
                                                                   new LatLng(-34.747, 145.592),
                                                                   new LatLng(-34.364, 147.891),
                                                                   new LatLng(-33.501, 150.217),
                                                                   new LatLng(-32.306, 149.248),
                                                                   new LatLng(-32.491, 147.309)));
        // [END maps_poly_activity_add_polyline]
        // [START_EXCLUDE silent]
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");
        // [END maps_poly_activity_add_polyline_set_tag]
        // Style the polyline.
        stylePolyline(polyline1);


    }

    private
    void markQuadrilateral() {
        if (latLngs.size() == 4) {
            Log.i(TAG, "markQuadrilateral: updating: " + latLngs);
//            markings();
            polygon = googleMap.addPolygon(new PolygonOptions()
                                                   .clickable(true)
                                                   .fillColor(3)
                                                   .add(latLngs.get(0), latLngs.get(1), latLngs.get(2), latLngs.get(3))
            );
            stylePolygon(polygon);

            for (int l = 0; l < latLngs.size() - 1; l++) {
                dist[l] = distance(latLngs.get(l), latLngs.get(l + 1));
            }
            dist[latLngs.size()] = distance(latLngs.get(0), latLngs.get(3));
            markPolylines(latLngs.get(0), latLngs.get(1), "A");
            markPolylines(latLngs.get(1), latLngs.get(2), "B");
            markPolylines(latLngs.get(2), latLngs.get(3), "C");
            markPolylines(latLngs.get(0), latLngs.get(3), "D");
        }
    }

    private ArrayList<Polyline> lines;
    private Polyline polyline;

    private
    boolean markPolylines(LatLng prelat, LatLng curlat, String t) {
        polyline = googleMap.addPolyline(new PolylineOptions()
                                                 .clickable(true)
                                                 .add(prelat, curlat));
        polyline.setTag(t);
        stylePolyline(polyline);
        return true;
    }

    private
    void markings() {
        googleMap.clear();
        ArrayList<LatLng> a = latLngs;
        markLocation(a.get(0));
        markLocation(a.get(1));
        markLocation(a.get(2));
        markLocation(a.get(3));
    }

    private
    void stylePolyline(Polyline polyline) {
//        polyline.setEndCap(new RoundCap());
        polyline.setWidth(10);
        polyline.setColor(Color.RED);
        polyline.setJointType(JointType.ROUND);
    }

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

    @Override
    public
    void onPolygonClick(Polygon polygon) {
        double distance = totalDistance();
        Toast.makeText(this, "Polygon Distance = " + distance, Toast.LENGTH_LONG).show();
    }

    private
    void stylePolygon(Polygon polygon) {
        polygon.setStrokePattern(null);
        polygon.setStrokeWidth(8);
        polygon.setStrokeColor(Color.TRANSPARENT);
        polygon.setFillColor(0x5900FF00);
    }

    private
    double distance(LatLng mlat1, LatLng mlat2) {
        double lon1  = mlat1.longitude;
        double lon2  = mlat2.longitude;
        double lat1  = mlat1.latitude;
        double lat2  = mlat2.latitude;
        double theta = lon1 - lon2;
        double temp = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        temp = Math.acos(temp);
        temp = rad2deg(temp);
        temp = temp * 60 * 1.1515;
        Log.i(TAG, "distance: " + temp);
        return temp;
    }

    private
    double totalDistance() {
        double sum = 0;
        for (int i = 0; i < dist.length; i++) {
            sum = +dist[i];
            return sum;
        }
        return sum;
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
//            if (latLngs.size() > 0) {
//                markPolyline(latLngs.get(latLngs.size() - 1), latLng);
//            }
            tag += 1;
        } else {
            googleMap.clear();
            tag = 0;
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

//                =======================================================================

                Bitmap.Config           conf    = Bitmap.Config.ARGB_8888;
                Bitmap                  bmp     = Bitmap.createBitmap(100, 100, conf);
                android.graphics.Canvas canvas1 = new Canvas(bmp);

// paint defines the text color, stroke width and size
                Paint color = new Paint();
                color.setTextSize(45);
                color.setColor(Color.BLACK);

// modify canvas
                canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.round_pin_drop_black_48), 0, 0, color);
                canvas1.drawText(mtag[tag], 0, -40 , color);

// add marker to Map
//                =======================================================================


                Marker m1 = googleMap.addMarker(new MarkerOptions()
                                                        .position(latLng)
                                                        .title(title)
                                                        .snippet(snippet)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                                        .anchor(0.5f, 1)
                                                        .visible(true)
                                                        .draggable(true));
                m1.showInfoWindow();
                m1.setTag(mtag[tag]);
                markQuadrilateral();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public
    boolean onMarkerClick(Marker marker) {
        Log.i(TAG, "onMarkerClick: markerPosition " + marker.getPosition());
        Toast.makeText(MapsActivity.this, "TAG: " + marker.getTag().toString(), Toast.LENGTH_SHORT).show();

        if (marker.equals(mSelectedMarker)) {
            mSelectedMarker = null;
            tag             = 0;
            googleMap.clear();
            return true;
        }
        mSelectedMarker = marker;
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

    @Override
    public
    void onMarkerDragStart(Marker marker) {
    }

    @Override
    public
    void onMarkerDrag(Marker marker) {

    }

    @Override
    public
    void onMarkerDragEnd(Marker marker) {
        Log.i(TAG, "onMarkerDragEnd: Position: " + marker.getPosition());
        Toast.makeText(this, "Tag:" + marker.getTag().toString(), Toast.LENGTH_SHORT).show();
        marker.getTag();

        try {
            if ("A".equals(marker.getTag().toString())) {
                position = 0;
            } else if ("B".equals(marker.getTag().toString())) {
                position = 1;
            } else if ("C".equals(marker.getTag().toString())) {
                position = 2;
            } else if ("D".equals(marker.getTag().toString())) {
                position = 3;
            }
            latLngs.set(position, new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

        } catch (Exception e) {
            e.printStackTrace();
        }
        latLngs.set(position, new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
        Log.i(TAG, "markQuadrilateral: position: " + position + " Updated = " + latLngs.get(position));

        markQuadrilateral();
    }
}

















