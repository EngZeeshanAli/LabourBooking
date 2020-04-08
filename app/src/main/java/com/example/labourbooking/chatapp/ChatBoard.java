package com.example.labourbooking.chatapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labourbooking.R;
import com.example.labourbooking.chatapp.NotifationsAndServices.Token;
import com.example.labourbooking.controlers.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;

public class ChatBoard extends Fragment {
    RecyclerView chats;
    ArrayList<Message> msgs;
    ArrayList<String> ids;
    FirebaseUser user;
    ArrayList<Chats> chatList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chatboard_layout, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        chats = v.findViewById(R.id.chat_recycler);
        chats.setLayoutManager(new LinearLayoutManager(getContext()));
        user = FirebaseAuth.getInstance().getCurrentUser();
        msgs = new ArrayList<>();
        ids = new ArrayList<>();
        chatList = new ArrayList<>();
        getWhoMessageMe();
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(user.getUid()).setValue(token1);
    }

    void getWhoMessageMe() {
        DatabaseReference dbs = FirebaseDatabase.getInstance().getReference();
        dbs.child("testing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);
                    if (chats.getReciver().equals(user.getUid()) || chats.getSender().equals(user.getUid())) {
                        chatList.add(chats);
                    }
                }
                getMesssages();
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
                    for (Chats chat : chatList) {
                        if (msgs.isEmpty()) {
                            msgs.add(message);
                            ids.add(snapshot.getKey());
                        } else {
                            if (!chat.getSender().equals(user.getUid()) && ids.contains(message.getSender())) {
                                msgs.add(message);
                                ids.add(snapshot.getKey());
                            }
                        }
                    }

                }


                Collections.reverse(msgs);
                chats.setAdapter(new ChatBoardAdapter(getActivity(), msgs, ids));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
