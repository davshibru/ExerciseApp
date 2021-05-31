package com.example.databaseexercisetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddDataInDataBase extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    ImageButton imageButton;
    EditText editTextName, editTextDescription;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data_in_data_base);

        dbHelper = new DBHelper(this);

        imageButton = (ImageButton) findViewById(R.id.imageButtonImageOfExercise);
        editTextName = (EditText)findViewById(R.id.nameEditTextDialog);
        editTextDescription = (EditText) findViewById(R.id.descriptionEditTextDialog);

        Button button = (Button) findViewById(R.id.addDataInBaseButtonDialog);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String description = editTextDescription.getText().toString();

                if (!(name.equals("") && description.equals(""))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(AddDataInDataBase.this);
                    builder.setTitle(R.string.save);

                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SQLiteDatabase database = dbHelper.getWritableDatabase();
                            ContentValues contentValues = new ContentValues();

                            contentValues.put(DBHelper.COL_Name, name);
                            contentValues.put(DBHelper.COL_Description, description);

                            BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();

                            int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                            contentValues.put(DBHelper.COL_Image, Utils.getBytes(scaled) );

                            database.insert(DBHelper.TABLE_Name, null, contentValues);

                            dbHelper.close();
                            database.close();

                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);

                            finish();
                        }
                    });

                    builder.show();

                }
            }
        });
        
        
        //Выборка изображение

        imageButton.setOnClickListener(this::onClick);
    }


    public void onClick(View v) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_take_photo_or_use_gallery);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        ImageButton ibCamera = (ImageButton) dialog.findViewById(R.id.imageFromCamera);
        ImageButton ibGallery = (ImageButton) dialog.findViewById(R.id.imageFromGallery);

        ibGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFromGallery();
            }
        });

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        dialog.show();
    }


    //Get I mage From Gallery

    private void takePhotoFromGallery(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            }
            else {
                pickImageFromGallery();
            }
        }
        else {
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                }
                else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
        }
    }


    //Take Image in Camera

    public void takePhoto() {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageTakeIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(imageTakeIntent,REQUEST_IMAGE_CAPTURE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageButton.setImageURI(data.getData());
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageButton.setImageBitmap(imageBitmap);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}