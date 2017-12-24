package com.example.shariqkhan.chatnsnap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FullScreenImage extends AppCompatActivity {

    ImageView imageview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        String url = getIntent().getStringExtra("image");

        imageview = (ImageView)findViewById(R.id.full_screen);
        Picasso.with(this).load(url).into(imageview);




    }
}
