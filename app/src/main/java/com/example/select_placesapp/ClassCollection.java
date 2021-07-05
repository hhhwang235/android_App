package com.example.select_placesapp;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;


public class ClassCollection {
    Context context;

    //Context 를 가져오는 생성자
    public ClassCollection(Context c){
        this.context = c;
    }

    //
    public InputFilter getAlphaNumericFilter(){
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    //지정된 유니코드 문자가 글자나 10진수인지 여부를 나타낸다.
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        return filter;
    }
}
