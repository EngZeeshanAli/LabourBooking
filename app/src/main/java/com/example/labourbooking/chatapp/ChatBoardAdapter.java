package com.example.labourbooking.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.labourbooking.R;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.obj.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBoardAdapter extends RecyclerView.Adapter<ChatBoardAdapter.Item> {
    Context c;
    ArrayList<Message> msgsList;
    ArrayList<String> ids;
    FirebaseUser user;

    public ChatBoardAdapter(Context c, ArrayList<Message> msgsList, ArrayList<String> ids) {
        this.c = c;
        this.msgsList = msgsList;
        this.ids = ids;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.chatboard_item, parent, false);
        return new Item(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        final Message message = msgsList.get(position);
        if (message.getSender().equals(user.getUid())) {
            getSender(message.getRecievier(), holder.img, holder.name);
        } else {
            getSender(message.getSender(), holder.img, holder.name);
        }
        holder.message.setText(message.getMsg());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, ChatRoom.class);
                if (message.getSender().equals(user.getUid())) {
                    intent.putExtra("id", message.getRecievier());
                } else {
                    intent.putExtra("id", message.getSender());
                }
                c.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return msgsList.size();
    }

    public class Item extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView name, message;
        LinearLayout layout;

        public Item(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.sender_img);
            name = itemView.findViewById(R.id.sender_name);
            message = itemView.findViewById(R.id.sender_message);
            layout = itemView.findViewById(R.id.layout_chat_board_view);
        }
    }

    void getSender(String uid, final CircleImageView cirImg, final TextView nameS) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.USER_TABLE).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(c).load(user.getImg()).placeholder(R.drawable.placeholder).into(cirImg);
                nameS.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
