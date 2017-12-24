package com.example.shariqkhan.chatnsnap;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private DatabaseReference forlogindbreference;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.main_tab);

        getSupportActionBar().setTitle("ChatnSnap");






        firebaseAuth = FirebaseAuth.getInstance();




        forlogindbreference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());
        //forlogindbreference.child("online").setValue(true);
        viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

       // mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
//        forlogindbreference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot != null){
//                    forlogindbreference.child("online").onDisconnect().setValue(false);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        forlogindbreference.child("online").setValue(true);
//    }

    @Override
    protected void onStart() {
        super.onStart();
       FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            forlogindbreference.child("online").setValue("true");
        }else{
            SendToStart();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            forlogindbreference.child("online").setValue(ServerValue.TIMESTAMP);

        }


    }

    private void SendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut();
            forlogindbreference.child("online").setValue(ServerValue.TIMESTAMP);
            SendToStart();
        }

        if (item.getItemId() == R.id.account_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsAccount.class);
            startActivity(intent);

        }
        if (item.getItemId() == R.id.all_users) {
            Intent intent = new Intent(MainActivity.this, AllUserActivity.class);
            startActivity(intent);

        }
        if(item.getItemId() == R.id.change_authentication){
            Intent intent = new Intent(MainActivity.this, SelectToChange.class);
        startActivity(intent);
        }

        return true;
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        FirebaseAuth fa;
//        FirebaseUser fb;
//
//        fb = firebaseAuth.getCurrentUser();
//        if (fb != null){
//            forlogindbreference.child("online").onDisconnect().setValue(false);
//        }
//
//
//
//    }
}
