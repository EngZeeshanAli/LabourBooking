package com.example.labourbooking.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labourbooking.R;
import com.example.labourbooking.chatapp.NotifationsAndServices.APIService;
import com.example.labourbooking.chatapp.NotifationsAndServices.Client;
import com.example.labourbooking.chatapp.NotifationsAndServices.Data;
import com.example.labourbooking.chatapp.NotifationsAndServices.MyResponse;
import com.example.labourbooking.chatapp.NotifationsAndServices.Sender;
import com.example.labourbooking.chatapp.NotifationsAndServices.Token;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.obj.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoom extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    ImageView send;
    EditText msgEt;
    FirebaseUser user;
    RecyclerView msgsRecycler;
    ArrayList<Message> msgs;
    ArrayList<String> ids;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        init();
    }

    private void init() {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        toolbar = findViewById(R.id.chatroom_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        send = findViewById(R.id.send_message);
        send.setOnClickListener(this);
        msgEt = findViewById(R.id.message_box);
        user = FirebaseAuth.getInstance().getCurrentUser();
        msgs = new ArrayList<>();
        ids = new ArrayList<>();
        msgsRecycler = findViewById(R.id.chat_messages);
        msgsRecycler.setLayoutManager(new LinearLayoutManager(this));
        getUserDetail();
        getMesssages();
    }

    private String getId() {
        Intent i = getIntent();
        String id = i.getStringExtra("id");
        return id;
    }

    private void getUserDetail() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.USER_TABLE).child(getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                toolbar.setTitle(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(user.getUid(), R.mipmap.ic_launcher, username + ": " + message, "New Message",
                            getId());

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(ChatRoom.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    void getMesssages() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.MESSAGE_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                msgs.clear();
                ids.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message.getRecievier().equals(user.getUid()) && message.getSender().equals(getId()) ||
                            message.getRecievier().equals(getId()) && message.getSender().equals(user.getUid())) {
                        msgs.add(message);
                        ids.add(snapshot.getKey());
                    }
                }
                msgsRecycler.setAdapter(new MessagedListAddapter(ChatRoom.this, msgs, ids));
                msgsRecycler.scrollToPosition(msgsRecycler.getAdapter().getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String msg) {
        String dateAndTime = String.valueOf(System.currentTimeMillis());
        Message message = new Message(user.getUid(), getId(), dateAndTime, msg);
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.MESSAGE_TABLE).push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference dbs = FirebaseDatabase.getInstance().getReference();
                Chats chats = new Chats(user.getUid(), getId());
                dbs.child("testing").child(user.getUid()).setValue(chats);
                sendNotifiaction(getId(), user.getUid(), msg);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message:
                String msg = msgEt.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    sendMessage(msg);
                    msgEt.setText("");
                } else {
                    msgEt.setError("Empty !");
                }
                break;
        }
    }
}
