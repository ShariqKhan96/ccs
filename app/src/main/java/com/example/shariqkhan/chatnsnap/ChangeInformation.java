package com.example.shariqkhan.chatnsnap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeInformation extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String mGetId;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);
        mAuth = FirebaseAuth.getInstance();
        mGetId = mAuth.getCurrentUser().getUid();
     //   databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mGetId).child("req_type").setValue();



    }
}
