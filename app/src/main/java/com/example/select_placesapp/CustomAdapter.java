package com.example.select_placesapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.List;


public class CustomAdapter extends BaseAdapter {
    private DBUtils db;
    private Activity activity;
    private LayoutInflater inflater;
    List<Location> locations;

    //생성자
    public CustomAdapter(Activity a, List<Location> locs) {
        this.activity = a;
        this.locations = locs;
        this.db = new DBUtils(a.getApplicationContext());
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public Object getItem(int i) {
        return locations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return locations.get(i).getLocationID();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //listem.xml 이라는 틀을 가져온다.
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = inflater.inflate(R.layout.listitem, null);
        }

        TextView lblPlaceName = (TextView)view.findViewById(R.id.lblPlaceName); //장소창
        TextView lblAddress = (TextView)view.findViewById(R.id.lblAddress); //주소창
        TextView lblType = (TextView)view.findViewById(R.id.lblType);   //종류창
        RatingBar rateRating = (RatingBar)view.findViewById(R.id.rateRating);   //평점창
        Button btnViewMap = (Button)view.findViewById(R.id.btnViewMap); //장소 확인하기 버튼
        ImageButton btnDeleteLoc = (ImageButton)view.findViewById(R.id.btnDeleteLoc);   // X창 버튼

        Location l = locations.get(i);

        //adpater 을통해 받아온 값을 읽는다.
        lblPlaceName.setText(String.format("LOCATION : %s", l.getPlaceName()));
        lblAddress.setText(String.format("ADDRESS : %s", l.getAddressName()));
        lblType.setText(String.format("KINDS : %s", l.getType()));
        rateRating.setRating((float)l.getRate());

        final int index = i;

        //장소 확인하기 버튼 클릭시
        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,MapViewActivity.class);
                intent.putExtra("index",index);
                activity.startActivity(intent);
            }
        });

        //X창 버튼을 클릭시
        btnDeleteLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(activity);
                b.setTitle("이 항목을 삭제 할까요?");
                b.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //위치 정보에 해당하는 아이디를 찾는다.
                        long res = db.deleteLocation(locations.get(index).getLocationID());
                        //존재 할 경우
                        if(res > -1){
                            locations.remove(index);
                            notifyDataSetChanged();
                            Toast.makeText(activity,"삭제되었습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                b.show();
            }
        });

        return view;
    }
}
