package com.example.ibct.appdatxe.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.ibct.appdatxe.Contact.Contact;
import com.example.ibct.appdatxe.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomMakerOption implements GoogleMap.InfoWindowAdapter {
    private Context mContext;

    public CustomMakerOption(Context context) {
        this.mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.custom_maker_option, null);
        TextView txt_hovaten = view.findViewById(R.id.txt_hovaten);
        TextView txt_nhanHieu = view.findViewById(R.id.txt_nhanhieu);
        TextView txt_soghe = view.findViewById(R.id.txt_soghe);
        TextView txt_bienso = view.findViewById(R.id.txt_bienso);
        TextView txt_giathanh = view.findViewById(R.id.txt_giathanh);
        TextView txt_sosanhgia = view.findViewById(R.id.txt_sosanhgia);
        RadioButton radio_khack = view.findViewById(R.id.radio_khack);
        TextView txt_sodienthoai = view.findViewById(R.id.txt_sodienthoai);
        Button btn_datxe = view.findViewById(R.id.btn_datxe);

        Contact contact = (Contact) marker.getTag();
        txt_hovaten.setText(contact.getHoVaTen());
        txt_nhanHieu.setText(contact.getNhanHieu());
        txt_soghe.setText(contact.getSoGhe());
        txt_bienso.setText(contact.getBienSo());
        txt_giathanh.setText(contact.getGiaThanh());
        txt_sosanhgia.setText(contact.getSoSanhGia());
        if (contact.getTrangThai().equals("0")) {
            radio_khack.setChecked(false);
        } else {
            radio_khack.setChecked(true);
        }
        txt_sodienthoai.setText(contact.getSoDienThoai());
        btn_datxe.setText("Đặt xe");

        return view;
    }
}
