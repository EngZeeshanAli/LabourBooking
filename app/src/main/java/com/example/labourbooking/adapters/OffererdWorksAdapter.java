package com.example.labourbooking.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.labourbooking.R;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.obj.Post;
import com.example.labourbooking.obj.User;
import com.example.labourbooking.startups.WorkDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OffererdWorksAdapter extends RecyclerView.Adapter<OffererdWorksAdapter.Item> {
    Context c;
    ArrayList<Post> posts;
    ArrayList<String> ids;
    FirebaseUser user;

    public OffererdWorksAdapter(Context c, ArrayList<Post> posts, ArrayList<String> ids) {
        this.c = c;
        this.posts = posts;
        this.ids = ids;
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.offer_view, parent, false);
        return new Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, final int position) {
        Post post = posts.get(position);
        getUser(post.getPosterId(), holder.img);
        holder.title.setText(post.getTitle());
        holder.descriptoin.setText(post.getDescription());
        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, WorkDetail.class);
                intent.putExtra("id", ids.get(position));
                c.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        TextView title, descriptoin;
        Button open;
        CircleImageView img;

        public Item(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.offer_view_image);
            title = itemView.findViewById(R.id.offerer_name);
            descriptoin = itemView.findViewById(R.id.offer_descrption);
            open = itemView.findViewById(R.id.open_offer);

        }
    }


    private void getUser(String uid, final CircleImageView image) {
        final DatabaseReference reff = FirebaseDatabase.getInstance().getReference();
        reff.child(Constants.USER_TABLE).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(c)
                        .load(user.getImg())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
