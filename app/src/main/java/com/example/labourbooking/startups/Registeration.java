package com.example.labourbooking.startups;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.labourbooking.R;
import com.example.labourbooking.controlers.Constants;
import com.example.labourbooking.controlers.Controlers;
import com.example.labourbooking.obj.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registeration extends AppCompatActivity implements View.OnClickListener {
    EditText name, email, password, mobile;
    Button register;
    TextView singIn;
    Dialog processing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        initUI();
    }

    void initUI() {
        name = findViewById(R.id.name_register);
        email = findViewById(R.id.email_register);
        password = findViewById(R.id.pass_register);
        mobile = findViewById(R.id.mob_register);
        register = findViewById(R.id.signUp_register);
        register.setOnClickListener(this);
        singIn = findViewById(R.id.singin_resgister);
        singIn.setOnClickListener(this);
    }

    private void setRegister() {
        processing = Controlers.setProcessing(this);
        processing.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.sendEmailVerification();
                            User newUser = new User(name.getText().toString(), email.getText().toString(), mobile.getText().toString(), user.getUid());
                            saveUserData(newUser);
                            Controlers.topToast(Registeration.this, "Registered Successfully..");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Controlers.topToast(Registeration.this, e.getMessage());
                        processing.dismiss();
                    }
                });

    }

    private void saveUserData(User user) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.USER_TABLE).child(user.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                processing.dismiss();
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Controlers.topToast(Registeration.this, e.getMessage());
                processing.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUp_register:
                EditText[] texts = {name, email, password, mobile};
                boolean validate = Controlers.validateForm(this, texts, "Empty!");
                if (validate) {
                    setRegister();
                }
                break;
            case R.id.singin_resgister:
                finish();
                break;
        }
    }
}
