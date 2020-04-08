package com.example.labourbooking.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.labourbooking.R;
import com.example.labourbooking.chatapp.ChatRoom;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.controlers.Controlers;
import com.example.labourbooking.obj.Offer;
import com.example.labourbooking.obj.Post;
import com.example.labourbooking.obj.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.Item> {
    Context c;
    ArrayList offers;
    ArrayList ids;
    FirebaseUser user;
    String key;
    boolean show = false;


    public OfferAdapter(Context c, ArrayList offers, ArrayList ids, String key) {
        this.c = c;
        this.offers = offers;
        this.ids = ids;
        this.key = key;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.offer_view, parent, false);
        return new Item(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Item holder, final int position) {
        Offer offer = (Offer) offers.get(position);
        getUser(offer.getOffererId(), holder.name, holder.img);
        holder.offer.setText(offer.getOffer());
        if (offer.getOffererId().equals(user.getUid())) {
            holder.delte.setVisibility(View.VISIBLE);
            holder.delte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOffer((String) ids.get(position), position);
                }
            });
        }

        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show) {
                    holder.offer.setMaxLines(3);
                    holder.open.setText("expand");
                    show = false;
                } else {
                    holder.offer.setMaxLines(100);
                    holder.open.setText("minimize");
                    show = true;
                }

            }
        });

        getPost(holder.message, offer.getOffererId());


    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        CircleImageView img;
        Button delte, open, message;
        TextView name, offer;

        public Item(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.offer_view_image);
            name = itemView.findViewById(R.id.offerer_name);
            offer = itemView.findViewById(R.id.offer_descrption);
            delte = itemView.findViewById(R.id.delete_Offer);
            open = itemView.findViewById(R.id.open_offer);
            message = itemView.findViewById(R.id.message_Offer);
        }
    }

    private void getUser(final String uid, final TextView name, final CircleImageView img) {
        final DatabaseReference reff = FirebaseDatabase.getInstance().getReference();
        reff.child(Constants.USER_TABLE).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    name.setText(user.getName());
                    Glide.with(c)
                            .load(user.getImg())
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Controlers.topToast(c, databaseError.getMessage());
            }
        });
    }

    private void deleteOffer(final String id, final int position) {
        notifyDataSetChanged();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.OFFERS_TABLE).child(id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(c, "Deleted Succesfully..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void getPost(final Button message, final String id) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.POST_TABLE).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if (post.getPosterId().equals(user.getUid())) {
                    message.setVisibility(View.VISIBLE);
                    message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(c, ChatRoom.class);
                            i.putExtra("id", id);
                            c.startActivity(i);
                        }
                    });
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
