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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorksAdapter extends RecyclerView.Adapter<WorksAdapter.Item> {
    ArrayList<Post> posts;
    ArrayList<String> id;
    Context c;

    public WorksAdapter(Context c, ArrayList<Post> posts, ArrayList<String> id) {
        this.posts = posts;
        this.id = id;
        this.c = c;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.work_view, parent, false);
        return new Item(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, final int position) {
        Post currentPost = posts.get(position);
        holder.title.setText(currentPost.getTitle());
        holder.description.setText(currentPost.getDescription());
        getUser(currentPost.getPosterId(), holder.image);
        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent work=new Intent(c, WorkDetail.class);
                        work.putExtra("id",id.get(position));
                c.startActivity(work);
            }
        });
    }

    @Override
    public int getItemCount() {
        return id.size();
    }


    public void updateList(ArrayList<Post> postNew, ArrayList idNew) {
        posts = new ArrayList<>();
        posts.addAll(postNew);
        id = new ArrayList<>();
        id.addAll(idNew);
        notifyDataSetChanged();
    }

    public class Item extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView title, description;
        Button open;

        public Item(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.circleImageView_work_view);
            title = v.findViewById(R.id.title_work_view);
            description = v.findViewById(R.id.description_work_view);
            open = v.findViewById(R.id.open_work_view);
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
