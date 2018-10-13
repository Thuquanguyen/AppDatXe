package com.example.ibct.appdatxe.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ibct.appdatxe.Adapter.CustomMakerOption;
import com.example.ibct.appdatxe.Contact.Contact;
import com.example.ibct.appdatxe.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog mProgressDialog;
    private Location myLocation;
    private double check_location =0;
    private View mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //hiển thị nút location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //load kiểu hiển thị của google map
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));
            if (!success) {
                Log.e("", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("", "Can't find style. Error: ", e);
        }
        //lấy vị trí hiện tại của người dùng
        myLocation = googleMap.getMyLocation();
        //hiển thị macker biểu diễn vị trí của người dùng
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        if (check_location ==0) {
                            LatLng myLatLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(myLatLng)
                                    .title("MyLocation")
                                    .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

                            Contact contact=new Contact();
                            contact.setHoVaTen("Nguyễn Văn A");
                            contact.setSoGhe("4 chỗ");
                            contact.setNhanHieu("Honda - ");
                            contact.setBienSo("29H9-666666");
                            contact.setGiaThanh("8000đ/km");
                            contact.setSoSanhGia("Trung Bình");
                            contact.setTrangThai("1");
                            contact.setSoDienThoai("0969551162");

                            CustomMakerOption customInfoWindow = new CustomMakerOption(MapsActivity.this);
                            mMap.setInfoWindowAdapter(customInfoWindow);

                            Marker m = mMap.addMarker(markerOptions);
                            m.setTag(contact);
                            m.showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
                        }
                        check_location += 1;
                    }
                });


    }
}
