package com.example.ibct.appdatxe.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.ibct.appdatxe.R;
import com.example.ibct.appdatxe.data.Car;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomMakerOption implements GoogleMap.InfoWindowAdapter {
    //Khởi tạo 1 context
    private Context mContext;

    //Khởi tạo 1 makeroption
    public CustomMakerOption(Context context) {
        this.mContext = context;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    //Thiết lập các thuộc tính ứng với View trong makeroption để hiển thị lên cho người dùng
    @Override
    public View getInfoContents(Marker marker) {
        //Khởi tạo view cho Makeroption
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.custom_maker_option, null);
        //Ánh xạ các id có trong View
        TextView txt_hovaten = view.findViewById(R.id.txt_hovaten);
        TextView txt_nhanHieu = view.findViewById(R.id.txt_nhanhieu);
        TextView txt_soghe = view.findViewById(R.id.txt_soghe);
        TextView txt_bienso = view.findViewById(R.id.txt_bienso);
        TextView txt_giathanh = view.findViewById(R.id.txt_giathanh);
        TextView txt_sosanhgia = view.findViewById(R.id.txt_sosanhgia);
        TextView txt_soXe = view.findViewById(R.id.txt_soxe);
        TextView txt_sodienthoai = view.findViewById(R.id.txt_sodienthoai);
        CircleImageView btn_datxe = view.findViewById(R.id.btn_datxe);

        //Gán dữ liệu vào các option trong view
        Car contact = (Car) marker.getTag();
        txt_hovaten.setText(contact.getTenTaiXe());
        txt_nhanHieu.setText(contact.getHangXe());
        txt_soghe.setText("4 chỗ");
        txt_bienso.setText(contact.getBienSo());
        txt_giathanh.setText(contact.getGiaTien());
        txt_sosanhgia.setText("Trung bình");
        txt_soXe.setText(contact.getId());
        txt_sodienthoai.setText(contact.getPhone());
        //Gán đường dẫn ảnh
        Picasso.with(mContext).load(contact.getArrImage()).into(btn_datxe);
        //trả về 1 view chứa thông tin của xe
        return view;
    }
}
