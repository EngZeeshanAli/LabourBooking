package com.example.labourbooking.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.labourbooking.R;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.controlers.Controlers;
import com.example.labourbooking.obj.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class PostTask extends Fragment implements View.OnClickListener {
    EditText title, description;
    Button addImg, post, addLocation;
    ImageView uploadedImage;
    GoogleMap mMap;
    Dialog processing;
    FirebaseUser mUser;
    SupportMapFragment mapFragment;
    String location = "", cordinated = "";
    int checkImageAction;
    Bitmap selectedImage;
    Uri selectedImages;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.post_task, container, false);

        askGpsPermissions();
        if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initGui(v);
        } else {
            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                permissionDenied();
            }
        }


        return v;
    }

    private void initGui(View v) {
        if (checkGpsStatus() == false) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage("Turn On GPS To Use Location Service....");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "Go",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gps);
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(callback);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        title = v.findViewById(R.id.title_post_task);
        description = v.findViewById(R.id.decription_post_task);
        uploadedImage = v.findViewById(R.id.image_post_upload);
        uploadedImage.setOnClickListener(this);
        addImg = v.findViewById(R.id.add_image_topost);
        addImg.setOnClickListener(this);
        addLocation = v.findViewById(R.id.add_location_topost);
        addLocation.setOnClickListener(this);
        post = v.findViewById(R.id.add_post);
        post.setOnClickListener(this);
        processing = Controlers.setProcessing(getActivity());
    }


    void setUplodImg(Uri uri, String name) {
        processing.show();
        final StorageReference reference = FirebaseStorage.getInstance().getReference();
        reference.child(Constants.STORE_IMAGE_POST + name).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                final Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        String time = String.valueOf(Calendar.getInstance().getTime());
                        Post post = new Post(mUser.getUid(), title.getText().toString(), description.getText().toString(), imageUrl, cordinated, "", time);
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(Constants.POST_TABLE).push().setValue(post)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        processing.dismiss();
                                        reset();
                                    }
                                });
                    }
                });


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Controlers.topToast(getActivity(), e.getMessage());
                        processing.dismiss();
                    }
                });

    }


    void setUplodImg(Bitmap bitmap, String name) {
        processing.show();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        reference.child(Constants.STORE_IMAGE_POST + name).putBytes(byteArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        String time = String.valueOf(Calendar.getInstance().getTime());
                        Post post = new Post(mUser.getUid(), title.getText().toString(), description.getText().toString(), imageUrl, cordinated, "", time);
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(Constants.POST_TABLE).push().setValue(post)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        processing.dismiss();
                                        reset();
                                    }
                                });
                    }
                });


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Controlers.topToast(getActivity(), e.getMessage());
                        processing.dismiss();
                    }
                });
    }

    OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            // Add a marker in Sydney, Australia, and move the camera.
            if (mMap != null) {
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 18));
                        location = String.valueOf(arg0.getLatitude() +"#"+ arg0.getLongitude());
                    }
                });
            }
        }

    };

    public boolean checkGpsStatus() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gpsStatus;
    }

    void askGpsPermissions() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        permissionDenied();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();
    }

    private void permissionDenied() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Location  permission is neccesary.\n Go to setting to give permission? ");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Go",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getActivity().getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    void reset() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
        title.setText("");
        description.setText("");
        cordinated = "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_image_topost:
                AlertDialog.Builder builder = Controlers.selectImage(getActivity());
                builder.show();
                break;
            case R.id.image_post_upload:

                break;
            case R.id.add_location_topost:
                cordinated = location;
                Controlers.topToast(getContext(), "Location Added");
                break;
            case R.id.add_post:
                EditText[] texts = {title, description};
                boolean verify = Controlers.validateForm(getActivity(), texts, "Empty");
                if (verify) {
                    String currentTime = Calendar.getInstance().getTime() + ":" + mUser.getUid();

                    switch (checkImageAction) {
                        case 1:
                            setUplodImg(selectedImage, currentTime);
                            break;
                        case 2:
                            setUplodImg(selectedImages, currentTime);
                            break;
                        default:
                            processing.show();

                            String time = String.valueOf(Calendar.getInstance().getTime());
                            Post post = new Post(mUser.getUid(), title.getText().toString(), description.getText().toString(), "", cordinated, "", time);
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                            db.child(Constants.POST_TABLE).push().setValue(post)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            processing.dismiss();
                                            reset();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Controlers.topToast(getActivity(), e.getMessage());
                                    processing.dismiss();
                                }
                            });

                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        selectedImage = (Bitmap) data.getExtras().get("data");
                        uploadedImage.setImageBitmap(selectedImage);
                        checkImageAction = 1;
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                uploadedImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                selectedImages=selectedImage;
                                checkImageAction = 2;
                                cursor.close();
                            }
                        }

                    }


                    break;
            }
        }
    }

}