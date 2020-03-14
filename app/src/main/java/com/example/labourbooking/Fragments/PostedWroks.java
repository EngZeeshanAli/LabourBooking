package com.example.labourbooking.Fragments;

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
import com.example.labourbooking.adapters.MyPostedAdapter;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.controlers.Controlers;
import com.example.labourbooking.obj.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostedWroks extends Fragment {
    RecyclerView postedWorks;
    ArrayList<Post> posts;
    ArrayList<String> ids;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.posted_works, container, false);
        initGui(v);
        return v;
    }

    private void initGui(View v) {
        posts = new ArrayList<>();
        ids = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        postedWorks = v.findViewById(R.id.posted_works);
        postedWorks.setLayoutManager(new LinearLayoutManager(getContext()));
        getPostedWorks();
    }


    void getPostedWorks() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.POST_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                ids.clear();
                for (DataSnapshot snapshotda : dataSnapshot.getChildren()) {
                    Post post = snapshotda.getValue(Post.class);
                    if (post.getPosterId().equals(user.getUid())) {
                        posts.add(post);
                        ids.add(snapshotda.getKey());
                    }
                }
                postedWorks.setAdapter(new MyPostedAdapter(getActivity(), posts, ids));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Controlers.topToast(getActivity(), databaseError.getMessage());
            }
        });

    }
}


