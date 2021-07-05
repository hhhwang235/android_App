package com.example.select_placesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBUtils extends SQLiteOpenHelper {
    private static int version = 16844060;
    private static String dbName = "selectPlace";

    private String tblUser = "tblUser";
    private String userID = "userID";
    private String fullname = "fullname";
    private String username = "username";
    private String password = "password";

    private String tblLocation = "tblLocation";
    private String locationID = "locationID";
    private String locUserID = "userID";
    private String placeName = "placeName";
    private String rate = "rate";
    private String lati = "lati";
    private String longi = "longi";
    private String addressName = "addressName";
    private String type = "type";

    private Context context;
    SQLiteDatabase db;

    //생성자
    public DBUtils(Context appContext) {
        super(appContext, dbName, null, version);
        this.context = appContext;
    }

    //테이블을 생성하는 메서드
    @Override
    public void onCreate(SQLiteDatabase db) {
        //회원테이블 생성
        String user = "CREATE TABLE " + tblUser + " ( " +
                userID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                fullname + " TEXT," +
                username + " TEXT," +
                password + " TEXT);";
        db.execSQL(user);

        //장소정보 테이블 생성
        String location = "CREATE TABLE " + tblLocation + " ( " +
                locationID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                locUserID + " INTEGER," +
                placeName + " TEXT," +
                rate + " DOUBLE," +
                lati + " DOUBLE," +
                longi + " DOUBLE," +
                addressName + " TEXT," +
                type + " TEXT);";
        db.execSQL(location);
    }

    //테이블을 삭제하는 메서드
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //회원 테이블 삭제
        String query = "DROP TABLE " + tblUser;
        db.execSQL(query);

        //장소정보 테이블 삭제
        query = "DROP TABLE " + tblLocation;
        db.execSQL(query);
        onCreate(db);
    }
    //db 자원 생성 메서드
    public void openConnection() {
        db = this.getWritableDatabase();
    }

    //db 자원 해제 메서드
    public void closeConnection() {
        this.close();
    }

    //아이디의 존재 여부를 확인하는 메서드
    public boolean isUserExist(String uname){
        openConnection();
        String query = "SELECT * FROM " + tblUser + " WHERE " + username + "='" + uname + "'";
        Cursor c = db.rawQuery(query,null);
        boolean retVal = false;
        //db에 저장된 데이터에 일치하는 값이 있으면 true 없으면 false 리턴
        if(c.moveToFirst())
            retVal = true;
        else
            retVal = false;
        closeConnection();
        return retVal;
    }

    //동일한 이름의 위치가 정보가 존재하는지 확인하는 메서드
    public boolean isLocationExist(String pName){
        openConnection();
        String query = "SELECT * FROM " + tblLocation + " WHERE " + placeName + "='" + pName + "' AND " +
                locUserID + "=" + LoginActivity.loggedInUser;
        Cursor c = db.rawQuery(query,null);
        boolean retVal = false;
        if(c.moveToFirst())
            retVal = true;
        else
            retVal = false;
        closeConnection();
        return retVal;
    }

    //동일한 이름의 위치가 정보가 존재하는지 확인하는 메서드 + 테이블 전체라는 조건 즉, 계정의 ListView 탐색
    public boolean isLocationExistOnUpdate(String pName, String locID){
        openConnection();
        String query = "SELECT * FROM " + tblLocation + " WHERE " + placeName + "='" + pName + "' AND " +
                locUserID + "=" + LoginActivity.loggedInUser + " AND " + locationID + "!=" + locID;
        Cursor c = db.rawQuery(query,null);
        boolean retVal = false;
        if(c.moveToFirst())
            retVal = true;
        else
            retVal = false;
        closeConnection();
        return retVal;
    }

    //회원 가입을 완료하는 메서드
    public long registerUser(String fname, String uname, String pass){
        openConnection();
        ContentValues values = new ContentValues();
        values.put(fullname,fname);
        values.put(username,uname);
        values.put(password,pass);
        long success = db.insert(tblUser,null,values);
        closeConnection();
        return success;
    }

    //작성한 위치 정보를 Insert 하는 메서드
    public long addLocation(String uID,String pName, double star, double la, double lo, String addName, String type){
        openConnection();
        ContentValues values = new ContentValues();
        values.put(locUserID,uID);
        values.put(placeName,pName);
        values.put(rate,star);
        values.put(lati,la);
        values.put(longi,lo);
        values.put(addressName,addName);
        values.put(this.type,type);
        long success = db.insert(tblLocation,null,values);
        closeConnection();
        return success;
    }

    //작성한 위치 정보를 Update 하는 메서드
    public long updateLocation(int locID,String pName, double star, double la, double lo, String addName, String type){
        openConnection();
        ContentValues values = new ContentValues();
        values.put(placeName,pName);
        values.put(rate,star);
        values.put(lati,la);
        values.put(longi,lo);
        values.put(addressName,addName);
        values.put(this.type,type);
        long success = db.update(tblLocation,values,locationID + "=?",new String[]{String.valueOf(locID)});
        closeConnection();
        return success;
    }

    //저장된 위치 정보를 제거하는 메서드 자동 증가되는 locationID 를 찾아서 삭제한다.
    public long deleteLocation(int locID){
        openConnection();
        long success = db.delete(tblLocation,locationID+"=?",new String[]{String.valueOf(locID)});
        closeConnection();
        return success;
    }

    //아이디 비밀번호를 찾아내어 파라미터 값이랑 비교해서 일치하면 로그인 시키는 메서드 리턴값으로 1씩증가하게 해둔 userID 를 리턴
    public String getUserIDIfExist(String uname, String pass){
        openConnection();
        String uID ="";
        String query = "SELECT * FROM " + tblUser + " WHERE " + username + "='" + uname + "' LIMIT 1";
        Cursor c = db.rawQuery(query,null);
        if(c.moveToFirst()){
            String oUname = c.getString(c.getColumnIndex(username));
            String oPass = c.getString(c.getColumnIndex(password));
            if(oUname.equals(uname) && oPass.equals(pass)){
                uID = c.getString(c.getColumnIndex(userID));
            }
        }
        closeConnection();
        return uID;
    }


    public Cursor selectCursor(String query){
        openConnection();
        Cursor c = db.rawQuery(query,null);
        return c;
    }
}
