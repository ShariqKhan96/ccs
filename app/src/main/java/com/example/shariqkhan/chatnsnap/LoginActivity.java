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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

/**
 * Created by ShariqKhan on 7/12/2017.
 */


public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button loginButton;
    TextView txt;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_passwod);
        loginButton = (Button) findViewById(R.id.login_button);
        txt = (TextView) findViewById(R.id.login_log);
        progressDialog = new ProgressDialog(this);
        toolbar = (Toolbar) findViewById(R.id.login_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Please Wait");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Checking Authentication");
                progressDialog.show();

                String get_email = email.getText().toString();
                String get_password = password.getText().toString();

                if (!TextUtils.isEmpty(get_email) || !TextUtils.isEmpty(get_password)) {

                    loginUser(get_email, get_password);
                }
            }
        });
    }

    private void loginUser(String get_email, String get_password) {
        mAuth.signInWithEmailAndPassword(get_email, get_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    databaseReference.child(user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                } else {

                    progressDialog.hide();
                    Toast.makeText(LoginActivity.this, "Account not exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
