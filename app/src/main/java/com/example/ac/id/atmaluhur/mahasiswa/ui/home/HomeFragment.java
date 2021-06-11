package com.example.ac.id.atmaluhur.mahasiswa.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.ac.id.atmaluhur.mahasiswa.R;
import com.example.ac.id.atmaluhur.mahasiswa.databinding.FragmentHomeBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener  {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    final int PROXIMITY_RADIUS = 10000;

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiiClient;
    //tambahan tutorial ketiga
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    double latitude, longitude;
    double endLatitude, endLongitude;
    // private GoogleMap mMap;

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) checkLocationPermission();

        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.mapFrame, mMapFragment).commit();
        mMapFragment.getMapAsync(this);

        Log.d("HomeFragment", "onCreateView");

        ///AIzaSyCp3QLBAcCiGgs7o9Wz4laNgWDQ6yMij5s
        //binding.mapView.

        //final TextView textView = binding.textHome;
        /*
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //permission was granted. Do the
                    //contacts-related task you need to do.
                    if ( ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){

                        if (mGoogleApiiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText( getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d( "onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        Toast.makeText( requireContext(), "Your Current Location", Toast.LENGTH_LONG).show();

        if (mGoogleApiiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates( mGoogleApiiClient, (com.google.android.gms.location.LocationListener) this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
    }

    @Override
    public void onConnected(@Nullable @org.jetbrains.annotations.Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("onConnectionSuspended", String.valueOf( i ) );
    }

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {
        Log.e("onConnectionFailed", connectionResult.getErrorMessage() );
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        Log.d( "HomeFragment", "OnMapReady" );
        mMap = googleMap;
        //zoomin zoom out
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        //location tuk tutorial kedua
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)   {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected void buildGoogleApiClient() {
        mGoogleApiiClient = new GoogleApiClient
                .Builder(requireContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiiClient.connect();
    }

    public void checkLocationPermission() {
        if ( ContextCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale( requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) ) {
                ActivityCompat.requestPermissions( requireActivity(), new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_LOCATION );
            } else {
                ActivityCompat.requestPermissions( requireActivity(), new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
}