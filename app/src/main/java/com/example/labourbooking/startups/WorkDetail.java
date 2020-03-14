package com.example.labourbooking.startups;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.labourbooking.R;
import com.example.labourbooking.adapters.OfferAdapter;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.controlers.Controlers;
import com.example.labourbooking.obj.Offer;
import com.example.labourbooking.obj.Post;
import com.example.labourbooking.obj.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkDetail extends AppCompatActivity implements View.OnClickListener {
    RecyclerView offers;
    TextView name, title, description;
    CircleImageView img;
    Button makeOffer, submitOffer;
    ImageView back;
    EditText offer;
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    FirebaseUser user;
    ArrayList<Offer> ofersList;
    ArrayList<String> ofersId;
    Dialog processing;
    ImageView taskImage;
    LatLng latLng;
    NestedScrollView scrollTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_detail);
        initGui();
    }

    void initGui() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        back = findViewById(R.id.exit_work_detail);
        back.setOnClickListener(this);
        scrollTo = findViewById(R.id.scrool_to);
        name = findViewById(R.id.Offer_by_name);
        img = findViewById(R.id.profile_image_offer);
        makeOffer = findViewById(R.id.make_offer);
        makeOffer.setOnClickListener(this);
        submitOffer = findViewById(R.id.submi_offer);
        submitOffer.setOnClickListener(this);
        offer = findViewById(R.id.offer);
        title = findViewById(R.id.title_work_detail);
        description = findViewById(R.id.description_work_detail);
        offers = findViewById(R.id.offers_work_detail);
        offers.setLayoutManager(new LinearLayoutManager(this));
        processing = Controlers.setProcessing(this);
        taskImage = findViewById(R.id.task_image);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_posted);
        mapFragment.getMapAsync(callback);
        getTask(getKey());
        getOffers();
    }

    String getKey() {
        Intent intent = getIntent();
        return intent.getStringExtra("id");
    }



    private void getUser(final String uid) {
        final DatabaseReference reff = FirebaseDatabase.getInstance().getReference();
        reff.child(Constants.USER_TABLE).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                Glide.with(WorkDetail.this)
                        .load(user.getImg())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(img);
                processing.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                processing.dismiss();
                Controlers.topToast(WorkDetail.this, databaseError.getMessage());
            }
        });
    }

    OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
        }
    };

    void getTask(String key) {
        processing.show();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.POST_TABLE).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (!post.getTitle().isEmpty()) {
                        title.setText(post.getTitle());
                        description.setText(post.getDescription());
                    }
                    if (!post.getImg().isEmpty()) {
                        taskImage.setVisibility(View.VISIBLE);
                        Glide.with(WorkDetail.this).load(post.getImg()).into(taskImage);
                    }
                    if (!post.getLocation().isEmpty()) {
                        String[] lat = post.getLocation().split("#", 5);
                        latLng = new LatLng(Double.parseDouble(lat[0]), Double.parseDouble(lat[1]));
                        if (latLng != null) {
                            mMap.addMarker(new MarkerOptions().position(latLng).title("Work Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                        }
                    }
                    getUser(post.getPosterId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                processing.dismiss();
                Controlers.topToast(WorkDetail.this, databaseError.getMessage());
            }
        });
    }

    void getOffers() {
        ofersList = new ArrayList<>();
        ofersId = new ArrayList<>();
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference();
        rf.child(Constants.OFFERS_TABLE).child(getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ofersList.clear();
                ofersId.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Offer offer = snapshot.getValue(Offer.class);
                    ofersList.add(offer);
                    ofersId.add(snapshot.getKey());
                }
                Collections.reverse(ofersList);
                Collections.reverse(ofersId);
                    offers.setAdapter(new OfferAdapter(WorkDetail.this, ofersList, ofersId, getKey()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    void setOffer() {
        processing.show();
        if (TextUtils.isEmpty(offer.getText().toString())) {
            offer.setError("empty");
            return;
        } else {
            final Offer offerPost = new Offer(user.getUid(), this.offer.getText().toString());
            DatabaseReference rf = FirebaseDatabase.getInstance().getReference();
            rf.child(Constants.OFFERS_TABLE).child(getKey()).child(user.getUid()).setValue(offerPost)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Controlers.topToast(WorkDetail.this, "submitted successfully");
                            processing.dismiss();
                            offer.setText("");
                            offer.setVisibility(View.GONE);
                            submitOffer.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Controlers.topToast(WorkDetail.this, e.getMessage());
                            processing.dismiss();
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.make_offer:
                offer.setVisibility(View.VISIBLE);
                submitOffer.setVisibility(View.VISIBLE);
focusOnView();
                break;
            case R.id.submi_offer:

                setOffer();
                break;
            case R.id.exit_work_detail:
                onBackPressed();
                break;
        }
    }

    private void focusOnView() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollTo.scrollTo(0, offers.getTop());
            }
        });
    }
}
