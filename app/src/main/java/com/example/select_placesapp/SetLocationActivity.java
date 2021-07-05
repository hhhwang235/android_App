package com.example.select_placesapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SetLocationActivity extends AppCompatActivity {
    DBUtils db;
    ArrayList<String> types = new ArrayList<String>();
    EditText txtPlaceName,txtAddress;
    Spinner cmbTypes;
    Button btnSave;
    RatingBar rateRate;
    Location loc;
    List<Address> address;
    double lati,longi;
    boolean isSave = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        db = new DBUtils(getApplicationContext());
        initialize();
    }

    //각각의 아이디 값을 얻어오는 메서드
    private void initialize(){
        address = new ArrayList<>();
        txtPlaceName = (EditText)findViewById(R.id.txtPlaceName);
        txtAddress = (EditText)findViewById(R.id.txtAddress);
        cmbTypes = (Spinner)findViewById(R.id.cmbTypes);
        btnSave = (Button)findViewById(R.id.btnSave);
        rateRate = (RatingBar)findViewById(R.id.rateRate);
        prepareSpinner();
        setIfUpdate();
        setEvents();
    }

    //Intent 로부터 값을 가져와서 뿌리는 메서드
    private void setIfUpdate(){
        Intent intent = getIntent();

        //intent 의 값을 받을때 사용하는 메서드 defaultValue 는 해당값이 없을경우 출력됨
        isSave = intent.getBooleanExtra("isSave",true);
        if(!isSave) {
            //MainActivity 의 location ArrayList 를 리턴하는 intent
            loc = (Location) intent.getSerializableExtra("location");
            //위도 경도에 해당하는 값을 가져온다.
            lati = loc.getLati();
            longi = loc.getLongi();
            //장소와 주소 평점및 Spinner 값을 화면에 뿌린다.
            txtPlaceName.setText(loc.getPlaceName());
            txtAddress.setText(loc.getAddressName());
            rateRate.setRating((float)loc.getRate());
            String type = loc.getType();
            for(int i = 0; i < cmbTypes.getCount();i++){
                String t = cmbTypes.getItemAtPosition(i).toString();
                if(t.equals(type))
                    cmbTypes.setSelection(i);
            }
        }
    }

    //스피너에 표시할 정보들 출력
    private void prepareSpinner(){
        types.clear();
        types.add("병원");
        types.add("식당");
        types.add("학교");
        types.add("주차장");
        types.add("공장");
        types.add("집");
        types.add("버스정류장");
        types.add("가게");
        types.add("공항");
        bindValueToList();
    }

    //simple_list_item_1 형태로 스피너 뿌리는 메서드
    private void bindValueToList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SetLocationActivity.this, android.R.layout.simple_list_item_1, types);
        cmbTypes.setAdapter(adapter);
        cmbTypes.setSelection(0);
    }

    //위치 정보 작성창에 대한 수정일지 저장일지에 대한 작업 선택 메서드
    private void setEvents(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSave && isAllValid()){
                    saveDB();
                }else if(!isSave && isAllValid()) {
                    updateDB();
                }
            }
        });
    }

    //db에 값을 저장하는 메서드
    private void saveDB(){
        //저장할 값을 파라미터로 넣어준다.
        long res = db.addLocation(LoginActivity.loggedInUser,txtPlaceName.getText().toString(),
                rateRate.getRating(),lati,longi,txtAddress.getText().toString(), cmbTypes.getSelectedItem().toString());
        if(res > -1){
            Toast.makeText(getApplicationContext(),"저장되었습니다.",Toast.LENGTH_SHORT).show();
            setResult(1);
            finish();
        }
    }

    //db에 값을 업데이트 하는 메서드
    private void updateDB(){
        //업데이트할 값을 파라미터로 넣어준다.
        long res = db.updateLocation(loc.getLocationID(),txtPlaceName.getText().toString(),
                rateRate.getRating(),lati,longi,txtAddress.getText().toString(),cmbTypes.getSelectedItem().toString());
        if(res > -1){
            Toast.makeText(getApplicationContext(),"정보가 수정되었습니다.",Toast.LENGTH_SHORT).show();
            setResult(1);
            finish();
        }
    }

    //작성한 멘트에 대해서 조건의 참, 거짓 여부를 판단해 값을 출력하는 메서드
    private boolean isAllValid(){
        //위치정보를 공백을잘라서 set
        txtPlaceName.setText(txtPlaceName.getText().toString().trim());
        //주소정보를 공백을잘라서 set
        txtAddress.setText(txtAddress.getText().toString().trim());
        //둘중 하나라도 공백일경우
        if(txtAddress.getText().toString().equals("") || txtPlaceName.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"모든 항목을 작성하세요.",Toast.LENGTH_SHORT).show();
            return false;
        }

        //주소 정보를 담을 List 초기화
        address.clear();
        //주소 좌표를 가져온다.
        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
        String loc = txtAddress.getText().toString();
        try{
            //주소이름, 읽을 개수를 세팅한다.
            address = geo.getFromLocationName(loc,5);
            if(address.size() > 0){
                //주소에 따른 위도와 경도를 가져온다.
                lati = address.get(0).getLatitude();
                longi = address.get(0).getLongitude();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"서버에서 주소 변환시 에러발생",Toast.LENGTH_SHORT).show();
            return false;
        }

        //동일한 이름의 위치정보가 있는지 확인
        if(isSave) {
            if (db.isLocationExist(txtPlaceName.getText().toString())) {
                Toast.makeText(getApplicationContext(), "이미 존재하는 위치이름 입니다. 다른 이름을 사용해 주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }
            //같은 계정의 다른 ListView 에서 이름을 변경시 동일한 이름의 위치정보가 있는지 확인
        }else{
            if(db.isLocationExistOnUpdate(txtPlaceName.getText().toString(),String.valueOf(this.loc.getLocationID()))){
                Toast.makeText(getApplicationContext(), "이미 존재하는 위치이름 입니다. 다른 이름을 사용해 주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
