package com.example.labourbooking.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labourbooking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MessagedListAddapter extends RecyclerView.Adapter<MessagedListAddapter.MessageItem> {
    Context c;
    ArrayList<Message> msgsList;
    ArrayList<String> ids;
    FirebaseUser user;

    public MessagedListAddapter(Context c, ArrayList<Message> msgsList, ArrayList<String> ids) {
        this.c = c;
        this.msgsList = msgsList;
        this.ids = ids;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MessageItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.chat_item_view, parent, false);
        return new MessageItem(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageItem holder, int position) {
        Message message = msgsList.get(position);

        if (message.getSender().equals(user.getUid())) {
            holder.my.setText(message.getMsg());
            holder.myLay.setVisibility(View.VISIBLE);
            String dateTime = changeDateTime(Long.parseLong(message.getDateAndTime()));
            holder.timeMy.setText(dateTime);
        } else {
            holder.sender.setText(message.getMsg());
            holder.senderLay.setVisibility(View.VISIBLE);
            String dateTime = changeDateTime(Long.parseLong(message.getDateAndTime()));
            holder.timeSender.setText(dateTime);
        }
    }

    @Override
    public int getItemCount() {
        return msgsList.size();
    }

    public class MessageItem extends RecyclerView.ViewHolder {
        TextView sender, my, timeSender, timeMy;
        LinearLayout myLay, senderLay;

        public MessageItem(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sendermsg);
            my = itemView.findViewById(R.id.mymsg);
            senderLay = itemView.findViewById(R.id.sender);
            myLay = itemView.findViewById(R.id.my);
            timeSender = itemView.findViewById(R.id.sendertime);
            timeMy = itemView.findViewById(R.id.mytime);
        }
    }


    private String changeDateTime(long millies) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(millies);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        String dateAndTime = dateFormat.format(cal1.getTime());
        return dateAndTime;
    }

}
