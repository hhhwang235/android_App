package com.example.select_placesapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//내가 선택한 장소에 대한 정보를 저장하고 있는 클래스
public class MainActivity extends AppCompatActivity {
    DBUtils db;
    ListView lstPlaces;
    CustomAdapter adapter;
    Button btnViewAll;
    public static List<Location> locations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBUtils(getApplicationContext());
        locations = new ArrayList<>();
        lstPlaces = (ListView)findViewById(R.id.lstPlaces);
        btnViewAll = (Button)findViewById(R.id.btnViewAll);
        loadList();
        setEvents();
    }

    //안드로이드에서 옵션 메뉴를 로드하는 Activity 의 메서드(옵션 메뉴 로드를 위한 첫번째 과정)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //layout/menu/simplemenu.xml을 로드한다.
        getMenuInflater().inflate(R.menu.simplemenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //안드로이드에서 옵션 메뉴를 로드하는 Activity 의 메서드(옵션 메뉴 로드를 위한 두번째 과정)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //simplemenu.xml 의 menuAdd 버튼을 눌럿을시.
        if(id == R.id.menuAdd){
            //위치 정보를 선택하는 창으로 intent 한다.
            Intent intent = new Intent(MainActivity.this,SetLocationActivity.class);
            startActivityForResult(intent,1);
            //simplemenu.xml 의 menuLogout 버튼을 눌럿을시.
        }else if(id == R.id.menuLogout){
            //로그아웃 창으로 intent 한다.
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            //로그아웃 시키는 과정
            LoginActivity.loggedInUser = "";
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }//int locID, int userID, String pName, double rate, double latitude, double longitude,String addName,String type

    //내가 저장했던 장소들에 대한 정보를 불러오는 메서드
    private void loadList(){
        //정보를 담기전 배열을 초기화 한 후
        locations.clear();
        //내 계정에 알맞은 장소정보를 가져와서 Cursor 객체에 담는다.
        Cursor c = db.selectCursor("SELECT * FROM tblLocation WHERE userID=" + LoginActivity.loggedInUser +
        " ORDER BY rate DESC");
        //내 계정의 장소정보를 처음부터 하나씩 읽어 내려간다.
        if(c.moveToFirst()){
            do{
                int locID = c.getInt(c.getColumnIndex("locationID"));
                int uID = c.getInt(c.getColumnIndex("userID"));
                String pName = c.getString(c.getColumnIndex("placeName"));
                double rate = c.getDouble(c.getColumnIndex("rate"));
                double latitude = c.getDouble(c.getColumnIndex("lati"));
                double longi = c.getDouble(c.getColumnIndex("longi"));
                String addName = c.getString(c.getColumnIndex("addressName"));
                String type = c.getString(c.getColumnIndex("type"));
                //내 계정의 장소정보를 Location.java 에서 만들어둔 Location객체 l 에 담는다.
                Location l = new Location(locID,uID,pName,rate,latitude,longi,addName,type);
                //그 객체를 배열에 추가한다.
                locations.add(l);
            }while(c.moveToNext());
            // View와 그 View에 올릴 Data를 연결하는 adapter 객체에 위에서 만든 정보를 담는다.
            adapter = new CustomAdapter(MainActivity.this,locations);
            //ListView 에 해당하는 정보를 추가한다.
            lstPlaces.setAdapter(adapter);
        }
        //db 연결정보 해제
        db.closeConnection();
    }

    //버튼을 클릭시 해당하는 이벤트를 발생시키는 역할을 담당하는 메서드
    private void setEvents(){

        lstPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //리스트뷰의 아이템을 클릭 할 경우 이벤트 발생시키는 메서드 추가
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //리스트뷰의 해당 항목을 클릭하면 정보를 수정할 수 있는 창이 나온다.
                //인텐트를 활용해서 값을  SetLocationActivity 에게 넘겨준다
                Intent intent = new Intent(MainActivity.this,SetLocationActivity.class);
                intent.putExtra("isSave",false);
                intent.putExtra("location",locations.get(i));
                //SetLocationActivity 로부터 처리된 결과를 받아 온다.
                startActivityForResult(intent,1);
            }
        });

        //가장 아래 버튼을 클릭시 화면에 지도를 뿌려주는 메서드
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapViewActivity.class);
                //index 라는 파리미터 값을 넘긴다.
                intent.putExtra("index",-1);
                startActivity(intent);
            }
        });
    }

    //다른 액티비티로 넘어 갔다가 다시 되 돌아올때 사용되는 메서드이다.
    /*
        int requestCode : subActivity를 호출했던 startActivityForResult()의 두번째 인수값
        int resultCode : 호출된 액티비티에서 설정한 성공(RESULT_OK)/실패(RESULT_CANCEL) 값
        Intent data : 호출된 액티비티에서 저장한 값
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 1){
            //되 돌아 오면 다시 정보를 읽어 리스트를 만들어 화면에 띄어준다.
            loadList();
        }
    }
}
