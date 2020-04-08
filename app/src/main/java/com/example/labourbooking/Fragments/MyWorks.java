package com.example.labourbooking.Fragments;

import android.app.Dialog;
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
import com.example.labourbooking.adapters.OffererdWorksAdapter;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.controlers.Controlers;
import com.example.labourbooking.obj.Offer;
import com.example.labourbooking.obj.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyWorks extends Fragment {
    RecyclerView wokrs;
    ArrayList<Post> myWorks;
    ArrayList<Post> worksList;
    ArrayList<String> worksid;
    ArrayList<String> ids;
    FirebaseUser user;
    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.posted_works, container, false);
        initGui(v);
        return v;
    }

    void initGui(View v) {
        myWorks = new ArrayList<>();
        worksList = new ArrayList<>();
        worksid = new ArrayList<>();
        ids = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        wokrs = v.findViewById(R.id.posted_works);
        wokrs.setLayoutManager(new LinearLayoutManager(getContext()));
        getWorks();
    }

    void getWorks() {
        dialog = Controlers.setProcessing(getActivity());
        dialog.show();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.POST_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                worksList.clear();
                worksid.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    worksList.add(post);
                    worksid.add(snapshot.getKey());
                }
                getMyWorks();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void getMyWorks() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.OFFERS_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ids.clear();
                myWorks.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Offer offer = snapshot.getValue(Offer.class);
                    if (offer.getOffererId().equals(user.getUid())) {
                        for (int i = 0; i < worksid.size(); i++) {
                            String id = worksid.get(i);
                            if (id.equals(offer.getOfferId())) {
                                Post post = worksList.get(i);
                                myWorks.add(post);
                                ids.add(worksid.get(i));
                            }
                        }
                    }
                }
                dialog.dismiss();
                OffererdWorksAdapter adapter = new OffererdWorksAdapter(getActivity(), myWorks, ids);
                wokrs.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
