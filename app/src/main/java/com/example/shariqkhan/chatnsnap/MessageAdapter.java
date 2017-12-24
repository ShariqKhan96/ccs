package com.example.shariqkhan.chatnsnap;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ShariqKhan on 7/28/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.RecyclerViewHolder> {

    private List<Messages> messagesList;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private String otherName;
    private String name;
    Context c;
    private String url;

    private String mGetUid;

    public MessageAdapter(List<Messages> messagesList, Context context) {
        this.messagesList = messagesList;
        this.c = context;

    }

    @Override
    public MessageAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);


        mAuth = FirebaseAuth.getInstance();
        mGetUid = mAuth.getCurrentUser().getUid();

        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageAdapter.RecyclerViewHolder holder, int position) {


        Messages m = messagesList.get(position);
        holder.messageText.setText(m.getMessage());

        final String from = m.getFrom();


        if (!from.equals(null)){
            databaseReference2 = FirebaseDatabase.getInstance().getReference().child("users").child(from);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                //         url = dataSnapshot.child("image").getValue().toString();

                if (from.equals(mGetUid)) {
                    holder.relativeLayout.setGravity(Gravity.RIGHT);

                    //   holder.profileImage.setVisibility(View.INVISIBLE);
//                    holder.messageText.setBackgroundColor(Color.WHITE);
                    holder.messageText.setTextColor(Color.BLACK);
                    holder.messageText.setBackgroundResource(R.drawable.my_message_layout);
                    //   holder.req_type.setText("You"+" "+"said");

                    // holder.relativeLayout.setBackgroundResource(R.drawable.my_message_layout);
                } else {
                    holder.relativeLayout.setGravity(Gravity.LEFT);
                    holder.messageText.setTextColor(Color.WHITE);
                    holder.messageText.setBackgroundResource(R.drawable.message_text_single_style);

                }

//
//                Picasso.with(holder.profileImage.getContext()).load(url)
//                        .placeholder(R.drawable.default_image).into(holder.profileImage);
//
//                Log.e("url",url);
//                Log.e("fromname",otherName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }else {
            Toast.makeText(c, "You havent started the conversation !", Toast.LENGTH_SHORT).show();
        }
//        Picasso.with(c).load(url).into(holder.profileImage);


    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        //  public CircleImageView profileImage;


        public RelativeLayout relativeLayout;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            View view;
            view = itemView;
            relativeLayout = (RelativeLayout) view.findViewById(R.id.chat_single_message_relative_layout);

            messageText = (TextView) view.findViewById(R.id.message_recieve_or_Send);


        }
    }
}
