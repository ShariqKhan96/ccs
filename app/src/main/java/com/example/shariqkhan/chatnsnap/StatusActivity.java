package com.example.shariqkhan.chatnsnap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout textInputLayout;
    private Button buttonChanger;
    //vars of firebase
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseCurrentUser;

    //progressDialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        progressDialog=new ProgressDialog(this);
        String status =getIntent().getStringExtra("status_value");


//firebase
        firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseCurrentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        textInputLayout = (TextInputLayout) findViewById(R.id.status_change);
        textInputLayout.getEditText().setText(status);

        toolbar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonChanger = (Button) findViewById(R.id.status_button);

        buttonChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Changing Status");
                progressDialog.setMessage("Wait for a while");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String status = textInputLayout.getEditText().getText().toString();
                databaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            finish();

                        }else
                            {
                                Toast.makeText(StatusActivity.this, "Some errors while changing status..", Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                            }
                    }
                });
            }
        });
    }
}
