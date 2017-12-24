package com.example.shariqkhan.chatnsnap;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    FirebaseAuth mAuth;
    DatabaseReference retreiveChat;
    RecyclerView recyclerview;
    RecyclerView.LayoutManager layoutManager;
    String id;
    private DatabaseReference databaseReference;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        mAuth = FirebaseAuth.getInstance();
        id = mAuth.getCurrentUser().getUid();

        recyclerview = (RecyclerView) v.findViewById(R.id.chats_recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setLayoutManager(layoutManager);
        retreiveChat = FirebaseDatabase.getInstance().getReference().child("chats").child(id);
        retreiveChat.keepSynced(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        retreiveChat.keepSynced(true);
        return v;


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<ChatModel, ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatModel, ChatViewHolder>(ChatModel.class,
                R.layout.friends_single_layout, ChatViewHolder.class, retreiveChat) {
            @Override
            protected void populateViewHolder(final ChatViewHolder viewHolder, ChatModel model, int position) {

                final String getKey = getRef(position).getKey();


                databaseReference.child(getKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        String imageurl = dataSnapshot.child("image").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        viewHolder.User_name.setText(name);
                        Picasso.with(getContext()).load(imageurl).into(viewHolder.circleImageView);
                        viewHolder.txt.setText("Resume your conversation :)");
                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), ChatActivity.class);
                                intent.putExtra("u_id", getKey);
                                intent.putExtra("u_name", name);
                                startActivity(intent);


                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

        };
        recyclerview.setAdapter(firebaseRecyclerAdapter);

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        View view;
        private TextView txt;

        private TextView User_name;
        private CircleImageView circleImageView;


        public ChatViewHolder(View itemView) {
            super(itemView);
            view = itemView;


            User_name = (TextView) view.findViewById(R.id.friend_list_single_name);
            circleImageView = (CircleImageView) view.findViewById(R.id.friend_list_small_image);
            txt = (TextView) view.findViewById(R.id.friend_list_single_status);
        }
    }
}
