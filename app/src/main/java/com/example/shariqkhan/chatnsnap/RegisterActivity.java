package com.example.shariqkhan.chatnsnap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

/**
 * Created by ShariqKhan on 7/12/2017.
 */

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText password;
    private Button createBtn;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.reg_name);
        email = (EditText) findViewById(R.id.reg_email);
        password = (EditText) findViewById(R.id.reg_password);
        createBtn = (Button) findViewById(R.id.reg_create_btn);
        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();


        progressDialog = new ProgressDialog(this);

        toolbar = (Toolbar) findViewById(R.id.register_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String get_name = name.getText().toString();
                String get_email = email.getText().toString();
                String get_password = password.getText().toString();

                if (!TextUtils.isEmpty(get_name) || !TextUtils.isEmpty(get_email) || !TextUtils.isEmpty(get_password)) {
                    progressDialog.show();
                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("Please Wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    registerUserMethod(get_name, get_email, get_password);
                } else {
                    if (TextUtils.isEmpty(get_name) || TextUtils.isEmpty(get_email) || TextUtils.isEmpty(get_password)) {
                        Toast.makeText(RegisterActivity.this, "Fill in the details", Toast.LENGTH_SHORT).show();
                        name.setText("");
                        email.setText("");
                        password.setText("");
                    }
                }
            }
        });
    }

    private void registerUserMethod(final String get_name, final String get_email, String get_password) {

        mAuth.createUserWithEmailAndPassword(get_email, get_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Registration process.

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();
                    databaseReference = firebaseDatabase.getReference().child("users").child(uid);

                    //   String user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<String, String>();
                    userMap.put("device_token", deviceToken);
                    userMap.put("req_type", get_name);
                    userMap.put("status", "Hey there using ChatnSnap");
                    userMap.put("image", "default_image");
                    userMap.put("thumbnail", "def thumbnail");

                    //Now putting inside through reference of fbdb.

                    databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                progressDialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {
                    progressDialog.hide();
                    Toast.makeText(RegisterActivity.this, "You got some errors", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
