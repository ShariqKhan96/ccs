package com.example.shariqkhan.chatnsnap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SelectToChange extends AppCompatActivity {
    private Button mChangeName;
    private Button mChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_to_change);
    }
}
