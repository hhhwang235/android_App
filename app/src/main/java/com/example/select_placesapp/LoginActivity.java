package com.example.select_placesapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.select_placesapp.R.*;

public class LoginActivity extends AppCompatActivity {
    DBUtils db;
    ClassCollection cc;
    EditText txtUsername,txtPassword;
    Button btnLogin;
    TextView lblRegister;
    public static String loggedInUser = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_login);

        //타이틀바 없애기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        db = new DBUtils(getApplicationContext());
        cc = new ClassCollection(getApplicationContext());
        initialize();
        setEvents();
    }

    //각각의 아이디 값을 얻어오는 메서드
    private void initialize(){
        txtUsername = (EditText)findViewById(id.txtUsername);   //아이디
        txtPassword = (EditText)findViewById(id.txtPassword);   //비밀번호
        btnLogin = (Button)findViewById(id.btnLogin);   //로그인 버튼
        lblRegister = (TextView) findViewById(id.lblRegister);  //회원가입 버튼
    }

    //클릭했을시 이벤트를 나타내는 메서드들을 모아둔 집합.
    private void setEvents(){
        txtUsername.setFilters(new InputFilter[]{cc.getAlphaNumericFilter()});
        txtPassword.setFilters(new InputFilter[]{cc.getAlphaNumericFilter()});

        //회원가입을 클릭했을시 발생하는 이벤트
        lblRegister.setOnClickListener(new View.OnClickListener() {
            //회원가입 페이지로 이동한다.
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //로그인 버튼을 클릭했을시 발생하는 이벤트
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //아이디와 패스워드를 확인해 로그인 여부를 결정
               loggedInUser = db.getUserIDIfExist(txtUsername.getText().toString(),txtPassword.getText().toString());
               //두 가지가 존재할 경우
                if(!loggedInUser.equals("")){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    //존재하지 않을 경우
                }else{
                    Toast.makeText(getApplicationContext(),"존재하지 않는 계정 입니다!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
