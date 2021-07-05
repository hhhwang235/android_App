package com.example.select_placesapp;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    ClassCollection cc;
    DBUtils db;
    EditText txtRegFullname, txtRegUsername,txtRegPassword, txtRegRetype;
    Button btnRegRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DBUtils(getApplicationContext());
        cc = new ClassCollection(getApplicationContext());
        initialize();
        setEvents();
    }

    //각각의 아이디 값을 얻어오는 메서드
    private void initialize(){
        txtRegFullname = (EditText)findViewById(R.id.txtRegFullname);   //실제 이름을 가져오는 메서드
        txtRegPassword = (EditText)findViewById(R.id.txtRegPassword);   //패스워드
        txtRegUsername = (EditText)findViewById(R.id.txtRegUsername);   //아이디
        btnRegRegister = (Button)findViewById(R.id.btnRegRegister);     //가입완료 버튼
        txtRegRetype = (EditText)findViewById(R.id.txtRegRetype);       //패스워드 재확인
    }

    //클릭했을시 이벤트를 나타내는 메서드들을 모아둔 집합.
    private void setEvents(){
        txtRegUsername.setFilters(new InputFilter[]{cc.getAlphaNumericFilter()});
        txtRegPassword.setFilters(new InputFilter[]{cc.getAlphaNumericFilter()});

        //가입완료 버튼을 클릭했을시 발생하는 이벤트
        btnRegRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //조건이 모두 true 일 경우
                if(isAllValid()){
                    long res = db.registerUser(txtRegFullname.getText().toString(),
                            txtRegUsername.getText().toString(),
                            txtRegPassword.getText().toString());
                    if(res > -1){
                        Toast.makeText(getApplicationContext(),"계정이 생성 되었습니다.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    //작성한 멘트에 대해서 조건의 참, 거짓 여부를 판단해 값을 출력하는 메서드
    private boolean isAllValid(){
        //4가지 작성해야 할 항목중 하나라도 비어있다는 조건일 경우
        if(txtRegPassword.getText().toString().equals("") || txtRegUsername.getText().toString().equals("") ||
                txtRegRetype.getText().toString().equals("") || txtRegFullname.getText().toString().equals("")){
            //내용을 입력하라는 멘트를 출력
            Toast.makeText(getApplicationContext(),"빈 내용을 입력해 주세요.",Toast.LENGTH_SHORT).show();
            return false;
        }
        //입력한 비밀번호와 비밀번호 확인이 같지 않다는 조건일 경우
        if(!txtRegPassword.getText().toString().equals(txtRegRetype.getText().toString())){
            //일치하지 않는패스워드 라는 멘트를 출력
            Toast.makeText(getApplicationContext(),"패스워드가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
            return false;
        }
        //이미 존재하는 아이디일 경우
        if(db.isUserExist(txtRegUsername.getText().toString())){
            //이미 존재하는 아이디 라는 멘트를 출력
            Toast.makeText(getApplicationContext(),"이미 존재하는 아이디 입니다.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
