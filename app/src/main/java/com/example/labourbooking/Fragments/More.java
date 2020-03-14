package com.example.labourbooking.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.labourbooking.R;
import com.example.labourbooking.controlers.Controlers;
import com.example.labourbooking.startups.Login;
import com.example.labourbooking.startups.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class More extends Fragment implements View.OnClickListener {
    TextView profile, changepassword, logout;
    Button deleteAccount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.more_fragment, container, false);
        initGui(v);
        return v;
    }

    private void initGui(View v) {
        profile = v.findViewById(R.id.my_profile_more);
        profile.setOnClickListener(this);
        changepassword = v.findViewById(R.id.change_password_more);
        changepassword.setOnClickListener(this);
        logout = v.findViewById(R.id.logout_more);
        logout.setOnClickListener(this);
        deleteAccount = v.findViewById(R.id.delete_account_more);
        deleteAccount.setOnClickListener(this);
    }

    void resetPassword() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String emailAddress = user.getEmail();

        new AlertDialog.Builder(getActivity())
                .setTitle("Reset Password Confirmation")
                .setIcon(R.drawable.cartoon)
                .setMessage("We will send a password reset link at\n" + emailAddress)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final Dialog process = Controlers.setProcessing(getActivity());
                        process.show();
                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        }
                                        process.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        process.dismiss();
                                        Controlers.topToast(getContext(), e.getMessage());
                                    }
                                });
                    }
                })

                .setNegativeButton(android.R.string.no, null)
                .show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_more:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
                break;
            case R.id.my_profile_more:
                startActivity(new Intent(getActivity(), Profile.class));
                break;
            case R.id.change_password_more:
                resetPassword();
                break;
            case R.id.delete_account_more:
                setDeleteAccount();

                break;
        }
    }

    private void setDeleteAccount() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Account")
                .setIcon(R.drawable.cartoon)
                .setMessage("Do you really want to delete you account?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
