package com.example.labourbooking.startups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.labourbooking.R;
import com.example.labourbooking.controlers.Controlers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText email, password;
    Button login, join;
    Dialog processing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initGui();
    }

    void initGui() {
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.pass_login);
        login = findViewById(R.id.login_login);
        login.setOnClickListener(this);
        join = findViewById(R.id.join_login);
        join.setOnClickListener(this);
    }

    void setSignIn() {
        processing = Controlers.setProcessing(Login.this);
        processing.show();
        String email = this.email.getText().toString().trim();
        String pass = this.password.getText().toString().trim();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            processing.dismiss();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified()) {
                                startActivity(new Intent(Login.this, Dashboard.class));
                                finish();
                            } else {
                                Controlers.topToast(Login.this, "Verify you email.");
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Controlers.topToast(Login.this, e.getMessage());
                        processing.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login:
                EditText[] text = {email, password};
                boolean validate = Controlers.validateForm(this, text, "Empty!");
                if (validate) {
                    setSignIn();
                }
                break;
            case R.id.join_login:
                startActivity(new Intent(Login.this, Registeration.class));
                break;
        }
    }
}
