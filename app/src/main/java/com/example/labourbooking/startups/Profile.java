package com.example.labourbooking.startups;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.labourbooking.R;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.controlers.Controlers;
import com.example.labourbooking.obj.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    FirebaseUser mUser;
    TextView name, email, mobile, uidT;
    Dialog processing;
    ImageView updateImg;
    CircleImageView userImg;
    Button  deleteAcc;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initGui();
    }

    private void initGui() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        ImageButton back = (ImageButton) findViewById(R.id.backToMain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        name = findViewById(R.id.user_name_profile);
        email = findViewById(R.id.user_email_profile);
        mobile = findViewById(R.id.user_mobile_profile);
        uidT = findViewById(R.id.user_id_profile);
        updateImg = findViewById(R.id.update_img_profile);
        updateImg.setOnClickListener(this);
        userImg = findViewById(R.id.profile_image_profile);
        deleteAcc = findViewById(R.id.delete_my_account);
        deleteAcc.setOnClickListener(this);
        getUser(mUser.getUid());
    }

    private void getUser(final String uid) {
        processing = Controlers.setProcessing(this);
        processing.show();
        final DatabaseReference reff = FirebaseDatabase.getInstance().getReference();
        reff.child(Constants.USER_TABLE).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                processing.dismiss();
                name.setText(user.getName());
                email.setText(user.getEmail());
                mobile.setText(user.getMobile());
                uidT.setText(user.getUid());
                Glide.with(Profile.this)
                        .load(user.getImg())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(userImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                processing.dismiss();
                Controlers.topToast(Profile.this, databaseError.getMessage());
            }
        });
    }

    void askPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }

    void setUplodImg(Uri uri) {
        processing = Controlers.setProcessing(this);
        processing.show();
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        reference.child("images/" + mUser.getUid()).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(Constants.USER_TABLE).child(mUser.getUid()).child("img").setValue(imageUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        processing.dismiss();
                                    }
                                });
                    }
                });


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Controlers.topToast(Profile.this, e.getMessage());
                        processing.dismiss();
                    }
                });

    }


    void setUplodImg(Bitmap bitmap) {
        processing = Controlers.setProcessing(this);
        processing.show();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        StorageReference reference = FirebaseStorage.getInstance().getReference();
        reference.child("images/" + mUser.getUid()).putBytes(byteArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(Constants.USER_TABLE).child(mUser.getUid()).child("img").setValue(imageUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        processing.dismiss();
                                    }
                                });
                    }
                });


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Controlers.topToast(Profile.this, e.getMessage());
                        processing.dismiss();
                    }
                });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_img_profile:
                askPermission();
                AlertDialog.Builder builder = Controlers.selectImage(Profile.this);
                builder.show();
                break;
            case R.id.delete_my_account:
                setDeleteAcc();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        userImg.setImageBitmap(selectedImage);
                        setUplodImg(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                userImg.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                setUplodImg(selectedImage);
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }

    void setDeleteAcc() {
        final Dialog delAcc = new Dialog(this);
        delAcc.setContentView(R.layout.del_acc);
        delAcc.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText email = delAcc.findViewById(R.id.email_dell);
        final EditText pass = delAcc.findViewById(R.id.pass_dell);
        Button dell = delAcc.findViewById(R.id.confirm_dell);
        dell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    email.setError("empty");
                    return;
                }
                if (TextUtils.isEmpty(pass.getText().toString())) {
                    pass.setError("empty");
                } else {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email.getText().toString(), pass.getText().toString());

                    // Prompt the user to re-provide their sign-in credentials
                    mUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mUser.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        delAcc.dismiss();
                                                        Toast.makeText(Profile.this, "Account Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        startActivity(new Intent(Profile.this, Login.class));
                                                    }
                                                }
                                            });

                                }
                            });
                }
            }
        });
        delAcc.show();
    }

}
