package com.example.shariqkhan.chatnsnap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.security.NetworkSecurityPolicy;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.R.attr.bitmap;

public class SettingsAccount extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private DatabaseReference forlogindbreference;
    private FirebaseUser firebaseUser;
    private TextView DisplayName;
    private TextView Status;
    private CircleImageView circleImageView;
    private Button ChangeStatus;
    private Button change_image;
    private static final int GALLERY_PICK = 1;
    private StorageReference storageRef;
    private ProgressDialog progressDialog;
    private String uid;
    private FirebaseAuth firebaseAuth;
    String image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_account);

        DisplayName = (TextView) findViewById(R.id.Display_name);
        Status = (TextView) findViewById(R.id.Status);
        circleImageView = (CircleImageView) findViewById(R.id.cir_img);
        ChangeStatus = (Button) findViewById(R.id.btn_status);


        firebaseAuth = FirebaseAuth.getInstance();
        forlogindbreference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        Log.e("user", uid);
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        databaseReference2.child("online").setValue(true);
        storageRef = FirebaseStorage.getInstance().getReference();


        change_image = (Button) findViewById(R.id.btn_image);
        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryintent = new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryintent, "SELECT IMAGE"), GALLERY_PICK);

//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsAccount.this);
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        databaseReference.keepSynced(true);
        //Now to retrieve data we use valueeventlistener

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Toast.makeText(SettingsAccount.this, dataSnapshot.toString(), Toast.LENGTH_LONG).show();
                String token = dataSnapshot.child("device_token").getValue().toString();
                String get_name = dataSnapshot.child("name").getValue().toString();
                String get_status = dataSnapshot.child("status").getValue().toString();
              image = dataSnapshot.child("image").getValue().toString();

                String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();

                DisplayName.setText(get_name);
                Status.setText(get_status);

                if (!image.equals("default_image")) {

                    //Picasso.with(SettingsAccount.this).load(image).placeholder(R.drawable.default_image).into(circleImageView);
                    Picasso.with(SettingsAccount.this).load(image).placeholder(R.drawable.default_image).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = Status.getText().toString();

                Intent intent = new Intent(SettingsAccount.this, StatusActivity.class);
                intent.putExtra("status_value", status);

                startActivity(intent);
            }
        });

    circleImageView.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Intent intent = new Intent(SettingsAccount.this, FullScreenImage.class);
            intent.putExtra("image", image);
            startActivity(intent);

            return false;
        }
    });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {


            // for toast
            String imageUri = data.getDataString();
            //Toast.makeText(this, imageUri, Toast.LENGTH_SHORT).show();
            Uri uri = data.getData();
            CropImage.activity(uri).setAspectRatio(1, 1)
                    .setMinCropResultSize(500, 500)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(SettingsAccount.this);
                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Please Wait ");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();

                File thumbnail = new File(resultUri.getPath());

                //for thumbnail
                //Uploading in bitmap in firebase

                byte[] imageData = null;

                try
                {

                    final int THUMBNAIL_SIZE = 64;

                    FileInputStream fis = new FileInputStream(thumbnail);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    imageData = baos.toByteArray();

                }

                catch(Exception ex) {

                }

                String uid = firebaseUser.getUid();

                StorageReference filePath = storageRef.child("profile_images").child(uid + ".jpg");

                StorageReference thumbFilePath = storageRef.child("profile_images").child("thumb_images").child(uid+ ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            @SuppressWarnings("VisibleForTests") String download_url = task.getResult().getDownloadUrl().toString();


                            databaseReference.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {


                                        //thumb and main yahan upload houngi


                                        progressDialog.dismiss();
                                        Toast.makeText(SettingsAccount.this, "Profile Picture Changed Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(SettingsAccount.this, "Fialed something went wrong!", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        forlogindbreference.child("online").setValue(ServerValue.TIMESTAMP);
//        Toast.makeText(this, "Inside onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //databaseReference2.child("online").setValue(true);
        // Toast.makeText(this, forlogindbreference.child("online").getKey(), Toast.LENGTH_SHORT).show();
        forlogindbreference.child("online").setValue("true");
        //Toast.makeText(this, forlogindbreference.child("online").getKey(), Toast.LENGTH_SHORT).show();


    }
    protected void onStop() {
        super.onStop();
//        forlogindbreference.child("online").setValue(false);
//        FirebaseUser current_user = firebaseAuth.getCurrentUser();
//        if(current_user != null){
//            forlogindbreference.child("online").setValue(ServerValue.TIMESTAMP);
//        }

    }
}