package com.example.labourbooking.controlers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.labourbooking.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class Controlers {
    public Controlers() {
    }

    static public boolean validateForm(Context c, EditText[] edits, String msg) {
        boolean validation = false;

        for (int pos = 0; pos < edits.length; pos++) {
            if (TextUtils.isEmpty(edits[pos].getText().toString())) {
                edits[pos].setError(msg);
                validation = false;
                break;

            } else {
                validation = true;
            }
        }
        return validation;
    }

    static public void topToast(Context c, String msg) {
        Toast toast = Toast.makeText(c, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 50);
        toast.show();
    }

    static public Dialog setProcessing(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle(null);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.processing_dialog);
        return dialog;
    }

    public static void removeStackFragments(FragmentManager fragmentManager) {
        for (Fragment fragment : fragmentManager.getFragments()) {
            FragmentManager manager = fragmentManager;
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(fragment).commit();
            manager.popBackStack();
        }
    }


    public static AlertDialog.Builder  selectImage(final Activity c) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    c.startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    c.startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        return builder;
    }

}
