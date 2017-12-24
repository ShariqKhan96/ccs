package com.example.shariqkhan.chatnsnap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ShariqKhan on 7/12/2017.
 */

public class StartActivity extends AppCompatActivity {

    private Button reg_btn;
    private TextView Welcome;
private Button AlreadyExist;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);
        Welcome=(TextView)findViewById(R.id.Welcome);


        reg_btn= (Button)findViewById(R.id.Reg_btn);
        AlreadyExist= (Button)findViewById(R.id.alr_Reg_btn);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
AlreadyExist.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
});

    }
}
