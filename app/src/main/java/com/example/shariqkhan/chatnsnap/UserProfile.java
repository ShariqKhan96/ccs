package com.example.shariqkhan.chatnsnap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.Format;
import java.util.Date;
import java.util.HashMap;

public class UserProfile extends AppCompatActivity {

    private TextView profile_friends;
    private TextView profile_status;
    private TextView profile_name;
    private ImageView image;
    private Button buttonfriend_request;
    private Button buttonfriend_request_cancel;
    DatabaseReference databaseReference;
    private DatabaseReference Friend_request_reference;
    private DatabaseReference databaseReference2;
    private DatabaseReference Friends_reference;
    private FirebaseUser Current_user;
    private ProgressDialog progressDialog;
    private String mCurrent_status = "not_friends";
    private DatabaseReference Notificationdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profile_name = (TextView) findViewById(R.id.user_profile_name);
        profile_status = (TextView) findViewById(R.id.user_profile_status);
        profile_friends = (TextView) findViewById(R.id.user_prfiles_friends);
        image = (ImageView) findViewById(R.id.user_profile_picture);
        buttonfriend_request = (Button) findViewById(R.id.friend_request_button);
        buttonfriend_request_cancel = (Button) findViewById(R.id.cancel_request_button);
        final String id = getIntent().getStringExtra("u_id");


        Current_user = FirebaseAuth.getInstance().getCurrentUser();


        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Log.e("user", FirebaseAuth.getInstance().getCurrentUser().getUid());
        //  databaseReference2.child("online").setValue(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(id);
        Friends_reference = FirebaseDatabase.getInstance().getReference().child("friends");
        Friend_request_reference = FirebaseDatabase.getInstance().getReference().child("Friend_requests");
        Notificationdatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dname = dataSnapshot.child("name").getValue().toString();
                String dstatus = dataSnapshot.child("status").getValue().toString();
                final String dimage = dataSnapshot.child("image").getValue().toString();
//
//                progressDialog = new ProgressDialog(UserProfile.this);
//                progressDialog.setTitle("Please wait");
//                progressDialog.setMessage("Loading user data");
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.show();

                profile_name.setText(dname);
                profile_status.setText(dstatus);


                Picasso.with(UserProfile.this).load(dimage).networkPolicy(NetworkPolicy.OFFLINE).
                        placeholder(R.drawable.default_image).into(image, new Callback() {
                    @Override
                    public void onSuccess() {

//                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError() {
                        Picasso.with(UserProfile.this).load(dimage).
                                placeholder(R.drawable.default_image).into(image);
//                        progressDialog.dismiss();
                    }
                });
//                progressDialog.dismiss();

                image.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        Intent intent = new Intent(UserProfile.this, FullScreenImage.class);
                        intent.putExtra("image", dimage);
                        startActivity(intent);
                        return false;
                    }
                });


                Friends_reference.child(Current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(id)) {
                            mCurrent_status = "friends";
                            buttonfriend_request.setText("Unfriend");
                            buttonfriend_request.setEnabled(true);
                            buttonfriend_request_cancel.setVisibility(View.INVISIBLE);
                            buttonfriend_request_cancel.setEnabled(false);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Friend_request_reference.child(Current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        buttonfriend_request_cancel.setVisibility(View.INVISIBLE);
                        buttonfriend_request_cancel.setEnabled(false);
                        if (dataSnapshot.hasChild(id)) {
                            String request_type = dataSnapshot.child(id).child("request_type").getValue().toString();

                            if (request_type.equals("recieved")) {

                                buttonfriend_request.setText("Accept Friend Request");
                                buttonfriend_request.setEnabled(true);
                                mCurrent_status = "req_recieved";
                                buttonfriend_request_cancel.setVisibility(View.VISIBLE);
                                buttonfriend_request_cancel.setEnabled(true);


                            } else if (request_type.equals("sent")) {

                                mCurrent_status = "req_sent";
                                buttonfriend_request.setText("Tap to Cancel");
                                buttonfriend_request.setEnabled(true);
                                buttonfriend_request_cancel.setVisibility(View.INVISIBLE);
                                buttonfriend_request_cancel.setEnabled(false);
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        buttonfriend_request.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                buttonfriend_request.setEnabled(false);

                if (mCurrent_status.equals("not_friends")) {
                    Friend_request_reference.child(Current_user.getUid()).
                            child(id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Friend_request_reference.child(id).
                                        child(Current_user.getUid()).
                                        child("request_type").setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notificationData = new HashMap<String, String>();
                                        notificationData.put("from", Current_user.getUid());
                                        notificationData.put("type", "request");

                                        Notificationdatabase.child(id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UserProfile.this, "Request Sent!", Toast.LENGTH_SHORT).show();
                                                buttonfriend_request.setText("Tap to cancel");
                                                buttonfriend_request.setEnabled(true);
                                                mCurrent_status = "req_sent";
                                                buttonfriend_request_cancel.setVisibility(View.INVISIBLE);
                                                buttonfriend_request_cancel.setEnabled(false);
                                            }
                                        });


                                    }
                                });
                            } else {
                                Toast.makeText(UserProfile.this, "Request not Sent!", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }


                if (mCurrent_status.equals("req_sent")) {
                    Friend_request_reference.child(Current_user.getUid()).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Friend_request_reference.child(id).child(Current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mCurrent_status = "not_friends";
                                    buttonfriend_request.setText("Friend Request");
                                    buttonfriend_request.setEnabled(true);
                                    buttonfriend_request_cancel.setVisibility(View.INVISIBLE);
                                    buttonfriend_request_cancel.setEnabled(false);

                                }
                            });


                        }
                    });
                }


                if (mCurrent_status.equals("req_recieved")) {
                    final String dateFormat = DateFormat.getDateTimeInstance().format(new Date());
                    Friends_reference.child(Current_user.getUid()).child(id).child("date").setValue(dateFormat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Friends_reference.child(id).child(Current_user.getUid()).child("date").setValue(dateFormat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Friend_request_reference.child(Current_user.getUid()).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Friend_request_reference.child(id).child(Current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mCurrent_status = "friends";
                                                    buttonfriend_request.setText("Unfriend");
                                                    buttonfriend_request.setEnabled(true);
                                                    buttonfriend_request_cancel.setVisibility(View.INVISIBLE);
                                                    buttonfriend_request_cancel.setEnabled(false);
                                                }
                                            });


                                        }
                                    });


                                }
                            });
                        }
                    });
                }


                if (mCurrent_status.equals("friends")) {
                    Friends_reference.child(Current_user.getUid()).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Friends_reference.child(id).child(Current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UserProfile.this, "Friendship Ends..! :(", Toast.LENGTH_SHORT).show();
                                    mCurrent_status = "not_friends";
                                    buttonfriend_request.setText("Friend Request");
                                    buttonfriend_request.setEnabled(true);

                                }
                            });

                        }
                    });
                }

            }
        });
        buttonfriend_request_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrent_status.equals("req_recieved")) {
                    Friend_request_reference.child(Current_user.getUid()).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Friend_request_reference.child(id).child(Current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mCurrent_status = "not_friends";
                                    buttonfriend_request.setText("Friend Request");
                                    buttonfriend_request_cancel.setText("Cancel Request");
                                    buttonfriend_request.setEnabled(true);

                                }
                            });
                        }
                    });
                }
            }
        });

    }


}
