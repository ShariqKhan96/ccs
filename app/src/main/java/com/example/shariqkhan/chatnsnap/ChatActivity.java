package com.example.shariqkhan.chatnsnap;

import android.content.Context;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String mChatUserId;
    String mCurrentUser_name;
    private Toolbar toolbar;
    private DatabaseReference mRootRef;
    private TextView name;
    private TextView last_Seen;
    private FirebaseAuth mAuth;
    private String mCurrentUser;
    private EditText mMessage;
    private ImageButton mSendButton;
    private ImageButton mAddButton;
    private CircleImageView circleImagView;
    private RecyclerView mRecyclerMessagesList;

    private SwipeRefreshLayout refreshLayout;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mlinearLayoutManager;
    private MessageAdapter mAdapter;
    private static final int TOTAL_ITEMS_LOAD_ALWAYS = 10;
    private int mCurrentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        toolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        mCurrentUser_name = getIntent().getStringExtra("u_name");
        mChatUserId = getIntent().getStringExtra("u_id");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mMessage = (EditText) findViewById(R.id.send_message_for_chat);

        mSendButton = (ImageButton) findViewById(R.id.send_message_button);
        mAddButton = (ImageButton) findViewById(R.id.add_chat_image_button);

        mRecyclerMessagesList = (RecyclerView) findViewById(R.id.chat_messages_recyclerview);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_view_for_messages);
        mlinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerMessagesList.setLayoutManager(mlinearLayoutManager);

        mAdapter = new MessageAdapter(messagesList, ChatActivity.this);

        mRecyclerMessagesList.setAdapter(mAdapter);
        loadMessages();


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(view);
        // actionBar.setTitle(mCurrentUser_name);

        name = (TextView) findViewById(R.id.chat_person_name);
        last_Seen = (TextView) findViewById(R.id.chat_last_seen);
        circleImagView = (CircleImageView) findViewById(R.id.chat_user_image);

        name.setText(mCurrentUser_name);

        mRootRef.child("users").child(mChatUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                if (online.equals("true")) {
                    last_Seen.setText("Online");
                } else {
                    TimeAgo timeAgo = new TimeAgo();
                    long lastTime = Long.parseLong(online);

                    String time = timeAgo.getTimeAgo(lastTime, ChatActivity.this);

                    last_Seen.setText(time);
                }
                Picasso.with(ChatActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(circleImagView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ChatActivity.this).load(image).into(circleImagView);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("chats").child(mCurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUserId)) {
                    Map chatMap = new HashMap();
                    chatMap.put("seen", false);
                    chatMap.put("timeago", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("chats/" + mCurrentUser + "/" + mChatUserId, chatMap);
                    chatUserMap.put("chats/" + mChatUserId + "/" + mCurrentUser, chatMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                Log.d("DbError", databaseError.getMessage().toString());
                            }
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //------//----//

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                messagesList.clear();
                loadMessages();
            }
        });

    }


    //-----for retreiving messages------//
    private void loadMessages() {

        DatabaseReference myref = mRootRef.child("messages").child(mCurrentUser).child(mChatUserId);
        Query messageQuery = myref.limitToLast(mCurrentPage * TOTAL_ITEMS_LOAD_ALWAYS);
        Query q = mRootRef.child("messages").orderByKey();

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                refreshLayout.setRefreshing(false);

                mRecyclerMessagesList.scrollToPosition(messagesList.size() - 1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void sendMessage() {

        //   Toast.makeText(this, "Button Clicked!", Toast.LENGTH_SHORT).show();
        String gettingText = mMessage.getText().toString();
        if (!TextUtils.isEmpty(gettingText)) {
            //Defining strings takay har bar reference mai likhna na paray

            String current_user_reference = "messages/" + mCurrentUser + "/" + mChatUserId;
            String chat_user_reference = "messages/" + mChatUserId + "/" + mCurrentUser;
            //for push id of user

            DatabaseReference user_message_push_id = mRootRef.child("messages")
                    .child(mCurrentUser).child(mChatUserId).push();

            String push_id = user_message_push_id.getKey();

            Map messageMap = new HashMap();

            messageMap.put("message", gettingText);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUser);

            Map messageUserMap = new HashMap();

            messageUserMap.put(current_user_reference + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_reference + "/" + push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Log.d("DbError", databaseError.getMessage().toString());
//                        mMessage.setText("");
                    }
                    mMessage.setText("");
                }
            });


        }
    }


}
