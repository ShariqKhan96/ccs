package com.example.shariqkhan.chatnsnap;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class RequestFragment extends Fragment {

    private RecyclerView mFriendRequestList;
    private DatabaseReference mUserDatabaseRef;
    private FirebaseAuth mAuth;
    //  private RecyclerView.LayoutManager layoutManager;
    public View mView;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_request, container, false);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth == null) {
            Intent intent = new Intent(getContext(), RegisterActivity.class);
            startActivity(intent);
            //getActivity().getSupportFragmentManager().popBackStack();

        }

        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_requests").child(mAuth.getCurrentUser().getUid());

        mUserDatabaseRef.keepSynced(true);
        mFriendRequestList = (RecyclerView) mView.findViewById(R.id.friend_request_list);

        mFriendRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<RequestModel, FriendRequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter
                <RequestModel, FriendRequestViewHolder>(
                RequestModel.class, R.layout.friend_request_single_layout, FriendRequestViewHolder.class, mUserDatabaseRef) {
            @Override
            protected void populateViewHolder(final FriendRequestViewHolder viewHolder, RequestModel model, int position) {

                final String hisFriendRequest = getRef(position).getKey();
//                DatabaseReference toCheck = FirebaseDatabase.getInstance().getReference().child("Friend_requests").child(mAuth.getCurrentUser().
//                        getUid()).child(hisFriendRequest).child("request_type");




                    DatabaseReference dataBaseRefForImage = FirebaseDatabase.getInstance().getReference().child("users").child(hisFriendRequest);
                    dataBaseRefForImage.keepSynced(true);
                    dataBaseRefForImage.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String getname = dataSnapshot.child("req_type").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();
                            viewHolder.User_name.setText(getname);
                            viewHolder.status.setText(status);


                            Toast.makeText(getContext(), getname, Toast.LENGTH_SHORT).show();
                            Picasso.with(getContext()).load(image).placeholder(R.drawable.default_image).into(viewHolder.circleImageView);

                            viewHolder.deciderButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), UserProfile.class);
                                    intent.putExtra("u_id", hisFriendRequest);
                                    Log.e("logging", hisFriendRequest);
                                    startActivity(intent);
                                    getActivity().getSupportFragmentManager().popBackStack();

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });




            }

        };
        mFriendRequestList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        View view;

        Button deciderButton;
        TextView User_name;
        CircleImageView circleImageView;
        TextView status;


        public FriendRequestViewHolder(View itemView) {
            super(itemView);
            view = itemView;


            User_name = (TextView) view.findViewById(R.id.frined_request_display_username);
            status = (TextView) view.findViewById(R.id.friend_request_status);
            circleImageView = (CircleImageView) view.findViewById(R.id.frined_request_user_image);
            deciderButton = (Button) view.findViewById(R.id.friend_request_decider_button);
            //User_name = (TextView) view.findViewById(R.id.user_single_name);


            //circleImageView=(CircleImageView)view.findViewById(R.id.user_small_image);
        }

    }
}

