package com.example.select_placesapp;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//선택한 리스트 정보를 클릭 했을시 그에대한 클래스
public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap gMap;
    private CameraPosition position;
    private int index = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        index = getIntent().getIntExtra("index",-1);

        //화면에 구글 지도를 뿌려주는 역할을 담당한다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //구글 지도를 만드는 역할을 담당한다.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        //기본 지도를 그릴것이라 정의
        //지정된 위치가 없을 경우
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(index == -1){
            //데이터에 저장된 정보만큼 반복해서 마커를 그린다.
            for(int i = 0; i < MainActivity.locations.size();i++){
                //위치 정보를 가져온다.
                Location l = MainActivity.locations.get(i);
                if(i == 0){
                    //위치 정보에 따라서 화면에 띄울 위치를 나타낸다.
                    position = new CameraPosition(new LatLng(l.getLati(),l.getLongi()),15,0,0);
                    CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                    //화면에 표현한다.
                    gMap.animateCamera(update);
                }
                //마커의 크기를 지정한다.
                int height = 60;
                int width = 60;
                //입력했던 자료에 따라서 표현할 마커를 선택한다.
                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(getDrawable(l.getType()));
                Bitmap b=bitmapdraw.getBitmap();
                //가로 60 세로 60 크기로 마커를 선택한다.
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                //마커를 활용하여 그린다.
                gMap.addMarker(new MarkerOptions().position(new LatLng(l.getLati(),l.getLongi())).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .title(l.getPlaceName()).snippet(l.getAddressName()).draggable(false));
            }
            //지정된 위치가 있을 경우
        }else{
            Location l = MainActivity.locations.get(index);
            position = new CameraPosition(new LatLng(l.getLati(),l.getLongi()),15,0,0);
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            gMap.animateCamera(update);
            int height = 60;
            int width = 60;
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(getDrawable(l.getType()));
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            gMap.addMarker(new MarkerOptions().position(new LatLng(l.getLati(),l.getLongi())).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .title(l.getPlaceName()).snippet(l.getAddressName()).draggable(false));
        }

        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    //마커로 나타낼 위치에 따른 사진들이다.
    private int getDrawable(String type){
        switch (type){
            case "병원":return R.drawable.hospital;
            case "식당":return R.drawable.restaurant;
            case "학교":return R.drawable.school;
            case "주차장":return  R.drawable.park;
            case "공장":return  R.drawable.factory;
            case "집":return  R.drawable.house;
            case "버스정류장":return  R.drawable.bus;
            case "가게":return  R.drawable.shop;
            case "공항":return  R.drawable.airport;
            default: return R.mipmap.ic_launcher_round;
        }
    }
}
