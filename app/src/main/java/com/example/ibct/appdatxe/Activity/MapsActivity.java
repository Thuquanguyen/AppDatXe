package com.example.ibct.appdatxe.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibct.appdatxe.Adapter.CustomMakerOption;
import com.example.ibct.appdatxe.R;
import com.example.ibct.appdatxe.Until;
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
import org.w3c.dom.Text;

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

    //Khơi tạo các biến cần thiết
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
    private ArrayList<Car> listcarCopy;
    private TextView tvDatXe;
    private CustomMakerOption customCar;
    static Marker mk;
    private CompositeDisposable compositeDisposable;
    private static final int SPEECH_REQUEST_CODE = 0;
    private TextView txt_diemden, txt_danhsachxe;
    private ImageView img_voice;
    private LinearLayout lil_chon;
    private Button btn_datxe;
    private int check = 0;
    private int soXe = 0;
    private Until until;
    private String title = "";

//Hàm này chạy đầu tiên khi ứng dụng được mở lên
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Khởi tạo mảng các xe
        listcar = new ArrayList<>();
        //Khởi tạo mảng các xe thứ 2
        listcarCopy = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
        // Nhận SupportMapFragment và được thông báo khi bản đồ đã sẵn sàng để sử dụng.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        initView();
    }

    private void initView() {
        //Gọi hàm getCar trong class HomeApi để lấy danh sách các xe có trong database
        HomeApi.getCar(compositeDisposable, carCallBack);
        //Ánh xạ các id
        txt_diemden = (TextView) findViewById(R.id.txt_diemden);
        img_voice = (ImageView) findViewById(R.id.img_voice);
        lil_chon = (LinearLayout) findViewById(R.id.lil_chon);
        btn_datxe = (Button) findViewById(R.id.btn_chonxe);
        txt_danhsachxe = (TextView) findViewById(R.id.txt_danhsachxe);
        //Bắt sự kiện click vào nút Voice
        img_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Thực hiện lệnh khi click
                promptSpeechInput();
            }
        });
        //Bắt sự kiện khi click vào nút đặt xe
        btn_datxe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //So sánh text trên button để thwucj hiện các lệnh tương ứng
                if (btn_datxe.getText().toString().trim().equals("Click để chọn xe")) {
                    //Đặt xe nếu text trên button = "Click để chọn xe"
                    datxe();
                } else if (btn_datxe.getText().toString().trim().equals("Chọn điểm đến")) {
                    btn_datxe.setText("Tính tiền");
                    //Chọn điểm đến khi text trên button = "Chọn điểm đến"
                    chondiadiem();
                } else if(btn_datxe.getText().toString().trim().equals("Tính tiền"))
                {
                    //Tính  tiền khi text tren button = "Tính tiền"
                    mMap.clear();
                    txt_diemden.setText(title);
                    btn_datxe.setText("Xác nhận thanh toán");
                } else if (btn_datxe.getText().toString().trim().equals("Xác nhận thanh toán")) {
                    //Khi click vào xác nhận thanh toán
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    // Trả về trạng thái ban đầu của ứng dụng
                                    btn_datxe.setText("Đặt xe");
                                    txt_danhsachxe.setText("Quanh bạn chưa có xe nào");
                                    txt_diemden.setText("");
                                    mMap.clear();
                                    btn_datxe.setVisibility(View.GONE);
                                    lil_chon.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    };
                    // Hiển thị dialog lên activity và đưa ra thông báo
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setMessage("Đặt xe thành công").setPositiveButton("Xong", dialogClickListener).show();
                }
            }
        });
        //Khởi tạo lớp Until
        until = new Until();
    }
    // Phương thức lấy điểm đến của người dùng và chỉ đường tới điểm đến đó
    private void geoLocate() {
        //Tạo biến để lưu tên điểm đến
        String search = address;
        //Khởi tạo Geocoder lấy thông tin điểm đến
        Geocoder geocoder = new Geocoder(this);
        //Khởi tạo 1 mảng các đối tượng điểm đến trả về
        List<Address> list = new ArrayList<>();
        try {
            //Gán thông tin điểm đến vào danh sách điểm đến
            list = geocoder.getFromLocationName(search, 1);
        } catch (IOException e) {
            System.out.println("hihi" + e.getMessage());
        }
        //Duyệt điểm đến trả về lấy ra phần tử gần nhất đó là điểm đến mà người dùng đã xác định
        if (list.size() > 0) {
            mMap.clear();
            Address address = list.get(0);
            LatLng myLatLng = new LatLng(address.getLatitude(),
                    address.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLatLng)
                    .title("Điểm đến")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMap.addMarker(markerOptions);
            String url = null;
            //Nếu trước đó người dùng đã yêu cầu chỉ đường đến điểm đến rồi
            //Thù lúc này biến polyline sẽ kiểm tra và xóa đường đi trước đó
            if (polyline != null) polyline.remove();
            if (vtHienTai != null) {
                // Tính khoảng các từ vị trí hiện tịa của người dùng tới điểm đến
                url = getDirectionsUrl(vtHienTai, myLatLng);
            }
            //Thưc hiện vẽ đường từ vị tría hiện tại đến điểm đến
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
            double distance = SphericalUtil.computeDistanceBetween(vtHienTai, myLatLng);
            Toast.makeText(this, "Quãng Đường : " + distance, Toast.LENGTH_SHORT).show();
            //Tính ra quãng đường, giá thành và tổng tiền hiển thị lên cho người dùng
            title  = "\tQuãng đường " + ((double) Math.round(distance * 10) / 10) + " " +
                    "m\n\tGiá thành " + listcarCopy.get(soXe).getGiaTien() + " đ/km\n\tTổng tiền phải trả " + (Double.parseDouble(listcarCopy.get(soXe).getGiaTien()) * ((double) Math.round(distance * 10) / 10)) + "đ ";
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
            //Khi API gọi xong trong hàm onSuccess ta lấy ra danh sách thông tin các xe
            //Lưu vào mảng các xe
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
            //Lưu tiếp vào 1 mảng các xe khác
            listcarCopy = listcar;
        }
    };

    //Phương thức onMapready được chạy khi Google map đã load được lên
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
        //Sự kiện lick vào maker và chỉ đường tới maker đó
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

    //Phương thức displayCar dùng để hiển thị thông tin các xe quanh người dùng
    //Hiển thị thông tin lên trên makeroption
    public void displayCar(ArrayList<Car> list) {
        txt_danhsachxe.setText("Quanh bạn hiện tại đang có " + list.size() + " xe");
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                LatLng myLatLng = new LatLng(list.get(i).getKinhDo(),
                        list.get(i).getViDo());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLatLng)
                        .title("MyLocation")
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.maker_car)));
                Car contact = new Car();
                contact.setId(list.get(i).getId());
                contact.setTenTaiXe(list.get(i).getTenTaiXe());
                contact.setHangXe(list.get(i).getHangXe());
                contact.setTenXe(list.get(i).getTenXe());
                contact.setBienSo(list.get(i).getBienSo());
                contact.setGiaTien(list.get(i).getGiaTien());
                contact.setPhone(list.get(i).getPhone());
                contact.setArrImage(list.get(i).getArrImage());
                CustomMakerOption customInfoWindow = new CustomMakerOption(MapsActivity.this);
                mMap.setInfoWindowAdapter(customInfoWindow);
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(contact);
                m.showInfoWindow();

            }
        }
    }

    //Hiển thị danh sách các xe đã được lọc theo bán kính
    //VD người dùng chọn 5km sẽ heienr thị cách xe cách người dùng 5km
    public void displayCarDetails(ArrayList<Car> list, int soxe) {
        mMap.clear();
        txt_danhsachxe.setText("Quanh bạn hiện tại đang có " + list.size() + " xe");
        if (soxe < list.size()) {
            if (soxe > 0) {
                soXe = soxe - 1;
                LatLng myLatLng = new LatLng(list.get(soxe - 1).getKinhDo(),
                        list.get(soxe - 1).getViDo());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLatLng)
                        .title("MyLocation")
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.maker_car)));
                Car contact = new Car();
                contact.setId(list.get(soxe - 1).getId());
                contact.setTenTaiXe(list.get(soxe - 1).getTenTaiXe());
                contact.setHangXe(list.get(soxe - 1).getHangXe());
                contact.setTenXe(list.get(soxe - 1).getTenXe());
                contact.setBienSo(list.get(soxe - 1).getBienSo());
                contact.setGiaTien(list.get(soxe - 1).getGiaTien());
                contact.setPhone(list.get(soxe - 1).getPhone());
                contact.setArrImage(list.get(soxe - 1).getArrImage());
                CustomMakerOption customInfoWindow = new CustomMakerOption(MapsActivity.this);
                mMap.setInfoWindowAdapter(customInfoWindow);
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(contact);
                m.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            } else {
                //Người dùng sẽ chọn xe thông qua giọng nói. Nếu có xe tồn tại googole map sẽ tự động clear tới
                //Địa điểm đó còn không sẽ hiển thị thông báo tới người dùng
                LatLng myLatLng = new LatLng(list.get(0).getKinhDo(),
                        list.get(0).getViDo());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLatLng)
                        .title("MyLocation")
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.maker_car)));
                Car contact = new Car();
                contact.setId(list.get(0).getTenTaiXe());
                contact.setTenTaiXe(list.get(0).getTenTaiXe());
                contact.setHangXe(list.get(0).getHangXe());
                contact.setTenXe(list.get(0).getTenXe());
                contact.setBienSo(list.get(0).getBienSo());
                contact.setGiaTien(list.get(0).getGiaTien());
                contact.setPhone(list.get(0).getPhone());
                contact.setArrImage(list.get(0).getArrImage());
                CustomMakerOption customInfoWindow = new CustomMakerOption(MapsActivity.this);
                mMap.setInfoWindowAdapter(customInfoWindow);
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(contact);
                m.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
            btn_datxe.setText("Chọn điểm đến");
        } else {
            Toast.makeText(this, "Số xe bạn đặt không tồn tại", Toast.LENGTH_SHORT).show();
        }

    }

    // Tính khoảng cách từ người dùng tới các xe
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
            // Tạo một kết nối http để giao tiếp với url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Đang kết nối với url
            urlConnection.connect();
            // Đọc dữ liệu từ url
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

    // Tìm nạp dữ liệu từ url được chuyển
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Tải xuống dữ liệu trong chuỗi không phải là chủ đề
        @Override
        protected String doInBackground(String... url) {
            //
            //Để lưu trữ dữ liệu từ dịch vụ web
            String data = "";
            try {
                //
                //Tìm nạp dữ liệu từ dịch vụ web
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        //
        //Thực hiện trong chuỗi giao diện người dùng, sau khi thực thi
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Gọi luồng để phân tích cú pháp dữ liệu JSON
            parserTask.execute(result);
        }
    }

    /**
     *
     Một lớp để phân tích Google Địa điểm ở định dạng JSON
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Phân tích dữ liệu trong chuỗi không phải là ui
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;

            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Directions parser = new Directions();
                // Bắt đầu dữ liệu phân tích cú pháp
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Thực hiện trong chuỗi giao diện người dùng, sau quá trình phân tích cú pháp
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            //
            //Đi ngang qua tất cả các tuyến đường
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                //
                //Đang tìm nạp tuyến đường thứ i
                List<HashMap<String, String>> path = result.get(i);
                // Tìm nạp tất cả các điểm trong tuyến đường thứ i
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                //
                //Thêm tất cả các điểm trong tuyến đường vào LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.YELLOW);
            }
            // Vẽ đa giác trong Bản đồ Google cho tuyến đường thứ i
            if (lineOptions != null) {
                polyline = mMap.addPolyline(lineOptions);
            }
        }
    }

    //
    //Tạo mục đích có thể bắt đầu hoạt động Trình nhận dạng giọng nói
    private void promptSpeechInput() {
        check = 1;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Mời bạn lựa chọn\n Bản đồ - Vị trí - Đặt xe - Cài đặt");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {

        }
    }

    //Phương thức đặt xe
    private void datxe() {
        check = 2;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Mời bạn đọc số xe cần đặt\n Vd : 1");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {

        }
    }
    //Phương thức chọn địa điểm - điểm đeens
    private void chondiadiem() {
        check = 2;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Mời bạn đọc địa chỉ");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {

        }
    }
    //Phương thức cài đặt bán kính
    private void caidat() {
        check = 3;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Mời bạn đọc số km cần hiển thị xe\n Vd : 1 hoặc 2 ,...");
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {

        }
    }

    //Kết quả trả về được lấy ra từ hàm onActivityResult khi người dùng click vào nút Voice để đọc
    // để thực thi
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    //Lấy ra danh sách các từ gần giống nhhaats
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //So sánh text mà người dùng đọc với các từ sau để thực hiện các chức năng đặt xe bằng giọng nói
                    if (btn_datxe.getText().toString().trim().equals("Tính tiền")) {
                        address = result.get(0);
                        btn_datxe.setText("Tính tiền");
                        geoLocate();
                        txt_diemden.setText(address);
                    }else
                    {
                        if (result.get(0).equals("hủy")) {
                            lil_chon.setVisibility(View.VISIBLE);
                            btn_datxe.setVisibility(View.GONE);
                            mMap.clear();
                            txt_danhsachxe.setText("Quanh bạn hiện tại không có xe nào");
                        }
                        if (!result.get(0).isEmpty()) {
                            txt_diemden.setText(result.get(0));
                            if (check == 1) {
                                callCar(result.get(0));
                            } else if (check == 2) {
                                if (result.get(0).equals("hài")) {
                                    displayCarDetails(listcarCopy, 2);
                                } else {
                                    try {
                                        int num = Integer.parseInt(result.get(0));
                                        displayCarDetails(listcarCopy, num);
                                    } catch (NumberFormatException e) {
                                        Toast.makeText(this, result.get(0) + " Không phải số!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else if (check == 3) {
                                String text = result.get(0);
                                switch (text) {
                                    case "1":
                                        mMap.clear();
                                        listcarCopy = until.CalculationByDistance(listcar, new LatLng(latitude, longitude), 1);
                                        displayCar(listcarCopy);
                                        break;
                                    case "3":
                                        mMap.clear();
                                        listcarCopy = until.CalculationByDistance(listcar, new LatLng(latitude, longitude), 3);
                                        displayCar(listcarCopy);
                                        break;
                                    case "5":
                                        mMap.clear();
                                        listcarCopy = until.CalculationByDistance(listcar, new LatLng(latitude, longitude), 5);
                                        displayCar(listcarCopy);
                                        break;
                                    case "7":
                                        mMap.clear();
                                        listcarCopy = until.CalculationByDistance(listcar, new LatLng(latitude, longitude), 7);
                                        displayCar(listcarCopy);
                                        break;
                                    case "tất cả":
                                        mMap.clear();
                                        listcarCopy = until.CalculationByDistance(listcar, new LatLng(latitude, longitude), 99999);
                                        displayCar(listcarCopy);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    // thực hiện các chức năng có trong ứng dụng
    public void callCar(String title) {
        switch (title) {
            // Hiển thị các dạng bản đồ
            case "bản đồ":
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_bando);
                Button btn_thongthuo, btn_giaothong, btn_diahinh;
                btn_diahinh = (Button) dialog.findViewById(R.id.btn_diahinh);
                btn_giaothong = (Button) dialog.findViewById(R.id.btn_giaothong);
                btn_thongthuo = (Button) dialog.findViewById(R.id.btn_thongthuong);
                btn_diahinh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Dạng địa hình
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        dialog.dismiss();
                    }

                });
                btn_giaothong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dạng giao thông
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        dialog.dismiss();
                    }
                });
                btn_thongthuo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Dạng thông thường
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
                //Hiển thị vị trí khi người dùng đọc "vị trí"
            //Google map tự động clear tới vị trí hiện tại của người dùng
            //Và thêm maker vào vị trí đó
            case "vị trí":
                Toast.makeText(this, "Vị trí của bạn", Toast.LENGTH_SHORT).show();
                LatLng myLatLng = new LatLng(latitude,
                        longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLatLng)
                        .title("Vị trí của bạn")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                break;
                //Khi người dùng nói đặt xe sẽ thwujc hiện chức năng đặt xe
            case "đặt xe":
                displayCar(listcarCopy);
                btn_datxe.setVisibility(View.VISIBLE);
                lil_chon.setVisibility(View.GONE);
                break;
                //Người dùng chọn cài đặt sẽ hiển thị lên danhy mục bán kính để người dùng lựa chọn cài đặt
            case "cài đặt":
                caidat();
                break;
                //Tương tự tự như đặt xe
            case "đạp xe":
                displayCar(listcarCopy);
                btn_datxe.setVisibility(View.VISIBLE);
                lil_chon.setVisibility(View.GONE);
                txt_diemden.setText("đặt xe");
                break;
            default:
                break;
        }
    }
// Phương thức vẽ đường đi trong ứng dụng
    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_maker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.img_marker);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
}
