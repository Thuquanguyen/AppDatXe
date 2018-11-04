package com.example.ibct.appdatxe.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibct.appdatxe.Adapter.CustomMakerOption;
import com.example.ibct.appdatxe.Contact.Contact;
import com.example.ibct.appdatxe.R;
import com.example.ibct.appdatxe.data.Car;
import com.example.ibct.appdatxe.data.CarResult;
import com.example.ibct.appdatxe.data.Image;
import com.example.ibct.appdatxe.network.api.HomeApi;
import com.example.ibct.appdatxe.network.callback.getCarCallBack;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog mProgressDialog;
    private Location myLocation;
    private double check_location = 0;
    private View mapView;
    Polyline polyline;
    static double latitude;
    static double longitude;
    static LatLng vtHienTai;
    static String address = "";
    private ArrayList<Car> listcar;
    private TextView tvDatXe;
    private CustomMakerOption customCar;
    static Marker mk;
    private CompositeDisposable compositeDisposable;
    private static final int SPEECH_REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        listcar = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        initView();
    }

    private void initView() {
        HomeApi.getCar(compositeDisposable, carCallBack);
      //promptSpeechInput();
    }

    private void geoLocate() {
        String search = address;
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(search, 1);
        } catch (IOException e) {
            System.out.println("hihi" + e.getMessage());
        }
        if (list.size() > 0) {
            mMap.clear();
            Address address = list.get(0);
            LatLng myLatLng = new LatLng(address.getLatitude(),
                    address.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLatLng)
                    .title("Điểm đến")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.maker_car));
            mMap.addMarker(markerOptions);
            String url = null;
            if (polyline != null) polyline.remove();
            if (vtHienTai != null) {
                url = getDirectionsUrl(vtHienTai, myLatLng);
            }
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
            double distance = SphericalUtil.computeDistanceBetween(vtHienTai, myLatLng);
            Toast.makeText(this, "Quãng Đường : " + distance, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private static final String TAG = "MapsActivity";
    private getCarCallBack carCallBack = new getCarCallBack() {
        @Override
        public void onSuccess(CarResult carResult) {
            for (int i = 0; i < carResult.getData().size(); i++) {
                listcar.add(new Car(carResult.getData().get(i).getId(),
                        carResult.getData().get(i).getTenXe(),
                        carResult.getData().get(i).getTenTaiXe(),
                        carResult.getData().get(i).getBienSo(),
                        carResult.getData().get(i).getGiaTien(),
                        carResult.getData().get(i).getHangXe(),
                        carResult.getData().get(i).getPhone(),
                        carResult.getData().get(i).getKinhDo(),
                        carResult.getData().get(i).getViDo(),
                        carResult.getData().get(i).isTrangThai(),
                        carResult.getData().get(i).getArrImage()));
            }
        }

        @Override
        public void onFailure(String error) {
            Log.e(TAG, "onFailure: ");
        }

        @Override
        public void onComplete() {
            displayCar();
        }
    };

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
                Log.e("hhh", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("hhhg", "Can't find style. Error: ", e);
        }
        //lấy vị trí hiện tại của người dùng
        myLocation = googleMap.getMyLocation();
        //hiển thị macker biểu diễn vị trí của người dùng
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                vtHienTai = new LatLng(latitude, longitude);
                LatLng myLatLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                if (check_location == 0) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                }
                check_location += 1;
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mk = marker;
                String url = null;
                LatLng desLatlg = marker.getPosition();
                if (polyline != null) polyline.remove();
                if (vtHienTai != null) {
                    url = getDirectionsUrl(vtHienTai, desLatlg);
                }
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
                double distance = SphericalUtil.computeDistanceBetween(vtHienTai, desLatlg);
                Toast.makeText(MapsActivity.this, "Quãng đường : " + distance, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public void displayCar() {
        if (listcar.size() > 0) {
            for (int i = 0; i < listcar.size(); i++) {
                LatLng myLatLng = new LatLng(listcar.get(i).getKinhDo(),
                        listcar.get(i).getViDo());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLatLng)
                        .title("MyLocation")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maker_car));
                Car contact = new Car();
                contact.setId(listcar.get(i).getId());
                contact.setTenTaiXe(listcar.get(i).getTenTaiXe());
                contact.setHangXe(listcar.get(i).getHangXe());
                contact.setTenXe(listcar.get(i).getTenXe());
                contact.setBienSo(listcar.get(i).getBienSo());
                contact.setGiaTien(listcar.get(i).getGiaTien());
                contact.setPhone(listcar.get(i).getPhone());
                contact.setArrImage(listcar.get(i).getArrImage());
                CustomMakerOption customInfoWindow = new CustomMakerOption(MapsActivity.this);
                mMap.setInfoWindowAdapter(customInfoWindow);
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(contact);
                m.showInfoWindow();
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyD9nQFa5-xClQ4QdJ7b8-mtP3nT50FDgfg";//AIzaSyDyxNr_NasV1Cky-b9JZO5eYLuWHmcU8fY
        return url;
    }

    /**
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;

            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Directions parser = new Directions();
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.YELLOW);
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                polyline = mMap.addPolyline(lineOptions);
            }
        }
    }

    // Create an intent that can start the Speech Recognizer activity
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "a");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "a",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    address = result.get(0);
                    if (!address.isEmpty()) {
                        geoLocate();
                        Toast.makeText(this, "Địa điểm : " + result.get(0), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }
}
