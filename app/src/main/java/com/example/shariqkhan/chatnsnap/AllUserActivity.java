package com.example.shariqkhan.chatnsnap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView userRecyclerView;
    //    private static TextView User_name;
//    private static TextView User_status;
    private CircleImageView circleImageView;
    private static boolean value = true;
    private DatabaseReference databaseReference2;
    private String getname;

    private DatabaseReference databaseReference;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);


        userRecyclerView = (RecyclerView) findViewById(R.id.user_recyclerview);


        //getting everything from firebase database firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        Log.e("user", FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getname = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        toolbar = (Toolbar) findViewById(R.id.all_user_app_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }
    //for realtime data fetch

//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        databaseReference2.child("online").setValue(true);
//    }

    @Override
    protected void onStart() {

        super.onStart();
        databaseReference2.child("online").setValue(true);
//databaseReference2.child("online").setValue(true);

//        Toast.makeText(this, "inside onstart", Toast.LENGTH_SHORT).show();
        FirebaseRecyclerAdapter<UsersModelClass, UserViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UsersModelClass,
                        UserViewHolder>(UsersModelClass.class,
                        R.layout.users_single_layout, UserViewHolder.class, databaseReference) {
                    @Override
                    protected void populateViewHolder(UserViewHolder viewHolder, UsersModelClass model, int position) {
                        String url = model.getImage();

//
//  Toast.makeText(AllUserActivity.this, "inside populateViewHolder", Toast.LENGTH_SHORT).show();

                        if (model.getName().equals(getname)) {
                            viewHolder.User_name.setText("You");
                            Picasso.with(AllUserActivity.this).load(url).placeholder(R.drawable.default_image).into(viewHolder.circleImageView);
                            viewHolder.User_status.setText(model.getStatus());
                        } else {
                            viewHolder.User_name.setText(model.getName());
                            viewHolder.User_status.setText(model.getStatus());

                            Picasso.with(AllUserActivity.this).load(url).placeholder(R.drawable.default_image).into(viewHolder.circleImageView);

                            final String user_id = getRef(position).getKey();

                            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(AllUserActivity.this, UserProfile.class);
                                    intent.putExtra("u_id", user_id);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                };
//        userRecyclerView.setAdapter(firebaseRecyclerAdapter);
        userRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View view;
        private final TextView User_status;
        private final TextView User_name;
        private CircleImageView circleImageView;

        public UserViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            User_name = (TextView) view.findViewById(R.id.friend_list_single_name);
            User_status = (TextView) view.findViewById(R.id.friend_list_single_status);
            circleImageView = (CircleImageView) view.findViewById(R.id.friend_list_small_image);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //  databaseReference2.child("online").setValue(false);

    }
}
