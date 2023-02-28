package com.example.transbetxi.ui.main.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.transbetxi.R;
import com.example.transbetxi.data.DirectionsResponse;
import com.example.transbetxi.data.OpenRouteServiceApi;
import com.example.transbetxi.databinding.FragmentStageMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StageMapFragment extends Fragment {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private FragmentStageMapBinding stageMapBinding;
    private MapView mapView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OpenRouteServiceApi api;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private GeoPoint userLocation;
    private ArrayList<GeoPoint> routePoints = new ArrayList<>();


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
            return;
        }

        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // Check if the user has granted permission to access their location
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public StageMapFragment() {
        // Required empty public constructor
    }

/*    public static StageMap newInstance(String param1, String param2) {
        StageMap fragment = new StageMap();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openrouteservice.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(OpenRouteServiceApi.class);
        super.onCreate(savedInstanceState);
    }

    private void getDirections(GeoPoint startLocation, GeoPoint endLocation) {
        Call<DirectionsResponse> call = api.getDirections(
                "5b3ce3597851110001cf6248fc68f7aa319246b6b0dea070598478ef",
                startLocation.getLongitude() + "," + startLocation.getLatitude(), // start point, // start point
                endLocation.getLongitude() + "," + endLocation.getLatitude() // end point
        );

        call.enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.isSuccessful()) {
                    DirectionsResponse directions = response.body();
                    List<List<Double>> coords = directions.getFeatures().get(0).getGeometry().getCoordinates();
                    for (List<Double> coord : coords) {
                        routePoints.add(new GeoPoint(coord.get(1), coord.get(0)));
                    }
                    displayRoute();
                } else {
                    // Handle the error
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                // Handle the failure
            }
        });
    }

    private void displayRoute() {
        if (routePoints == null || routePoints.isEmpty()) {
            return;
        }

        Polyline trayecto = new Polyline(mapView, true);
        trayecto.setWidth(10f);
        trayecto.setColor(Color.BLUE);
        trayecto.setPoints(routePoints);
        mapView.getOverlayManager().add(trayecto);

        BoundingBox boundingBox = BoundingBox.fromGeoPoints(routePoints);
        mapView.zoomToBoundingBox(boundingBox, true, 50);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        stageMapBinding = FragmentStageMapBinding.inflate(inflater, container, false);
        mapView = stageMapBinding.mapView;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        return stageMapBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION}; //Podriamos añadir: WRITE_EXTERNAL_STORAGE
        requestPermissionsIfNecessary(permissions);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);

        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(16.5);



        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
        myLocationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationNewOverlay);
        //Brujula
        CompassOverlay compassOverlay = new CompassOverlay(requireContext(), new InternalCompassOrientationProvider(requireContext()), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        //Punto salida
        Marker m = new Marker(mapView);
        GeoPoint salida = new GeoPoint(39.9421442, -0.2114737);
        m.setPosition(salida);
        m.setTextLabelFontSize(40);
        m.setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_green_48));
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        m.setTitle("Sagrat cor");
        mapView.getOverlays().add(m);

        //Punto llegada

        Marker m2 = new Marker(mapView);
        GeoPoint llegada = new GeoPoint(39.9416247,-0.2177557);
        m2.setPosition(llegada);
        m2.setTextLabelFontSize(40);
        m2.setIcon(getResources().getDrawable(R.drawable.ic_baseline_location_red_48));
        m2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        m2.setTitle("Rodaor");
        mapView.getOverlays().add(m2);

        //double startLatitude = (m2.getPosition().getLatitude() + m.getPosition().getLatitude())/2;
        //double startLongtude = (m2.getPosition().getLongitude() + m.getPosition().getLongitude())/2;
        //GeoPoint startPoint = new GeoPoint(startLatitude, startLongtude);
        mapController.setCenter(salida);

        Polyline polyline = new Polyline(mapView, true);

        List<GeoPoint> points = new ArrayList<>();
        points.add(salida);
        points.add(new GeoPoint(39.943386, -0.209726));
        points.add(new GeoPoint(39.942551, -0.208674));
        points.add(new GeoPoint(39.942603, -0.208432));
        points.add(new GeoPoint(39.942298, -0.208022));
        points.add(new GeoPoint(39.942870, -0.206300));
        points.add(new GeoPoint(39.944292, -0.207585));
        points.add(new GeoPoint(39.944452, -0.207322));
        points.add(new GeoPoint(39.945163, -0.207531));
        points.add(new GeoPoint(39.945439, -0.207778));
        points.add(new GeoPoint(39.945855, -0.207156));
        points.add(new GeoPoint(39.946175, -0.207060));
        points.add(new GeoPoint(39.946882, -0.207816));
        points.add(new GeoPoint(39.947014, -0.208447));
        points.add(new GeoPoint(39.947927, -0.210543));
        points.add(new GeoPoint(39.946207, -0.212266));
        points.add(llegada);
        polyline.setPoints(points);

        polyline.setColor(Color.RED);
        polyline.setWidth(5);
        polyline.setVisible(true);

        mapView.getOverlays().add(polyline);
// Add the Polyline to the MapVie

        stageMapBinding.buttonCenterLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicked", "yes");
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request the location permission if it has not been granted
                    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION}; //Podriamos añadir: WRITE_EXTERNAL_STORAGE
                    ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
                } else {
                    // Get the last known location
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(
                            location -> {
                                if (location != null) {
                                    mLastLocation = location;
                                    mapView.getController().animateTo(new GeoPoint(location));
                                }
                            });

                }
            }
        });

        stageMapBinding.buttonDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!isGpsEnabled) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("GPS desactivado");
                    builder.setMessage("Activa el GPS para obtener las direcciones");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    builder.show(); //buider.create.show()  builder.setNegativeButton("Cancel", null);
                    return;
                }

                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                    return;
                }

                MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
                myLocationNewOverlay.enableMyLocation();

                startLocationUpdates();

                if (userLocation == null) {
                    Toast.makeText(requireContext(), "No se pudo obtener la ubicación del usuario", Toast.LENGTH_SHORT).show();
                    return;
                }


                String endLocation = "longitude,latitude"; // replace with your desired end location
                getDirections(userLocation, salida);
            }
        });
    }

    /*private void createMarker(double latitude, double longitude, String title, String description) {
        Marker marker = new Marker(new Coordinate(latitude, longitude));
        marker.setTitle(title);
        marker.setDescription(description);
        marker.setVisible(true);
        marker.setGraphic(new Circle(7, Color.BLUE));

        mapView.addMarker(marker);

        marker.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                displayRoute(new Coordinate(latitude, longitude));
            }
        });
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}