package com.example.shariqkhan.chatnsnap;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private RecyclerView mfriendsList;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserDatabaseRef;
    private FirebaseAuth mAuth;
    //private DatabaseReference extra;
    public View mView;
    private String mCurrernt_user_id;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_friends, container, false);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth == null) {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
            getActivity().getSupportFragmentManager().popBackStack();
        }
        mCurrernt_user_id = mAuth.getCurrentUser().getUid();

        Log.e("id", mCurrernt_user_id);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrernt_user_id);
        mDatabaseReference.keepSynced(true);
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        mUserDatabaseRef.keepSynced(true);
        mfriendsList = (RecyclerView) mView.findViewById(R.id.friends_recyclerview);
        // mfriendsList.setHasFixedSize(true);
        mfriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mView;

    }

    @Override
    public void onStart() {
        super.onStart();

       FirebaseRecyclerAdapter<FriendsModelClass, FreindsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<FriendsModelClass, FreindsViewHolder>(FriendsModelClass.class,
                        R.layout.friends_single_layout, FreindsViewHolder.class, mDatabaseReference) {
                    @Override
                    protected void populateViewHolder(final FreindsViewHolder viewHolder, FriendsModelClass model, int position) {

                        //           Toast.makeText(getContext(), model.getMdate(), Toast.LENGTH_SHORT).show();
                        viewHolder.txt.setText("Friends Since " + model.getMdate());

                        final String get_id = getRef(position).getKey();
//                        Toast.makeText(getContext(), get_id, Toast.LENGTH_SHORT).show();
//                        Log.e("user_id",get_id);
                        mUserDatabaseRef.child(get_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final String get_name = dataSnapshot.child("name").getValue().toString();
                                String get_image = dataSnapshot.child("image").getValue().toString();
                                String get_Status = dataSnapshot.child("status").getValue().toString();
                                //    String onlineStatus = dataSnapshot.child("online").getValue().toString();


                                if (dataSnapshot.hasChild("online")) {
                                    String user_online =  dataSnapshot.child("online").getValue().toString();
                                    if (user_online.equals("true")) {
                                        viewHolder.image.setVisibility(View.VISIBLE);
                                    } else {
                                        viewHolder.image.setVisibility(View.INVISIBLE);
                                    }

                                }
                                viewHolder.txt.setText(get_Status);
                                viewHolder.User_name.setText(get_name);
                                Picasso.with(getContext()).load(get_image).into(viewHolder.circleImageView);

                                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Options");
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                if (i == 0) {
                                                    Intent intent = new Intent(getContext(), UserProfile.class);
                                                    intent.putExtra("u_id", get_id);
                                                    startActivity(intent);
                                                } else if (i== 1){

                                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                                    intent.putExtra("u_id", get_id);
                                                    intent.putExtra("u_name", get_name);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                        builder.show();
                                    }

                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }


                };
        mfriendsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FreindsViewHolder extends RecyclerView.ViewHolder {
        View view;
        private TextView txt;
        private ImageView image;
        private TextView User_name;
        private CircleImageView circleImageView;


        public FreindsViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            image = (ImageView) view.findViewById(R.id.imageView);
            txt = (TextView) view.findViewById(R.id.friend_list_single_status);
            User_name = (TextView) view.findViewById(R.id.friend_list_single_name);
            circleImageView = (CircleImageView) view.findViewById(R.id.friend_list_small_image);

            //User_name = (TextView) view.findViewById(R.id.user_single_name);


            //circleImageView=(CircleImageView)view.findViewById(R.id.user_small_image);
        }

    }

}
