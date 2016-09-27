package com.example.liangchenzhou.weatherlife;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.v13.app.FragmentCompat;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * The Fragment for map displaying
 */
public class MapsFrag extends Fragment implements OnMapReadyCallback {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private GoogleMap mMap;
    private LocationManager locationManager;
  //  private Button zoomIn, zoomOut;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MapsFrag() {
        // Required empty public constructor
    }

    public static MapsFrag newInstance(String param1, String param2) {
        MapsFrag fragment = new MapsFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationManager = (LocationManager) getActivity().getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().
                findFragmentById(R.id.mapFrag);

        mapFragment.getMapAsync(this);

//        ActivityCompat.requestPermissions(getActivity(),
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                0);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //onmapready method, be used for handle the actions for map like location event and uisettings
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // declare a locationManager and add a locationListener
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 3000, 8, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //   mMap.clear();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Home"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

                    if (getArguments() != null) {
                        String criterias = getArguments().getString("criteria");
                        if (criterias != "") {
                            //displaySearchResultOutDoorSports(criterias);
                            displaySearchResultOutDoorSports(criterias + ", melbourne");

                        }
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // mMap.clear();
                        Location newL = locationManager.getLastKnownLocation(provider);
                        LatLng latLng = new LatLng(newL.getLatitude(), newL.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("New Home"));

                    }

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }


//        this.displayNearby();
    }

    //display nearby test method
    public void displayNearby() {
        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194?q=restaurants");
        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivity(intent);
        }
    }

    //search the nearby outdoor and indoor sports method
    public void displaySearchResultOutDoorSports(String criteria){
        List<Address> list = null;
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
        try {
            list = geocoder.getFromLocationName("Caulfield Station", 1);
            for (Address address : list) {
               LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(criteria).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
