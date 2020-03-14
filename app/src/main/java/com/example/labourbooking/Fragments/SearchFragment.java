package com.example.labourbooking.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labourbooking.R;
import com.example.labourbooking.adapters.WorksAdapter;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.controlers.Controlers;
import com.example.labourbooking.obj.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {
    RecyclerView works;
    ArrayList<Post> posts;
    ArrayList<String> ids;
    WorksAdapter adapter;
    Dialog processing;
    SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragement, container, false);
        initGui(view);
        return view;
    }

    private void initGui(final View view) {
        posts = new ArrayList<>();
        ids = new ArrayList<>();
        searchView = view.findViewById(R.id.search_work);
        searchView.setOnQueryTextListener(this);
        processing = Controlers.setProcessing(getActivity());
        works = view.findViewById(R.id.works_fragments);
        works.setLayoutManager(new LinearLayoutManager(getActivity()));
        getTasks();
    }


    void getTasks() {
        processing.show();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.POST_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (processing.isShowing()) {
                    processing.dismiss();
                }
                posts.clear();
                ids.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    posts.add(post);
                    String id = snapshot.getKey();
                    ids.add(id);
                }
                Collections.reverse(posts);
                Collections.reverse(ids);
                adapter = new WorksAdapter(getActivity(), posts, ids);
                works.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                processing.dismiss();
                Controlers.topToast(getActivity(), databaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userQuery = newText.toLowerCase();
        ArrayList<Post> newposts = new ArrayList<>();
        ArrayList<String> newids = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            if (post.getTitle().toLowerCase().contains(userQuery) || post.getDescription().toLowerCase().contains(userQuery)) {
                newposts.add(post);
                newids.add(ids.get(i));
            }
        }
        if (!newposts.isEmpty()) {
            adapter.updateList(newposts, newids);
        }

        return true;
    }
}
