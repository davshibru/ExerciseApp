package com.example.databaseexercisetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import android.widget.ImageView;
import android.widget.Toast;

public class DetailExerciseActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    ImageButton imageButton;
    EditText editTextName, editTextDescription;
    Button saveButton, deleteButton;

    String oldName = "";
    String oldDescription = "";
    boolean isPhotoChanged = false;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_exercise);

        imageButton = (ImageButton) findViewById(R.id.imageButtonImageOfExercise);
        editTextName = (EditText) findViewById(R.id.nameEditText);
        editTextDescription = (EditText) findViewById(R.id.descriptionEditText);
        saveButton = (Button) findViewById(R.id.changeDataInBaseButton);
        deleteButton = (Button) findViewById(R.id.deleteDataFromBaseButton);

        id = (Integer) getIntent().getExtras().get(MainActivity.DETAIL_WORK_WITH_ITEM_STRING);

        SQLiteOpenHelper sqLiteOpenHelper = new DBHelper(this);

        try {
            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
            Cursor cursor = db.query(DBHelper.TABLE_Name, new String[]{DBHelper.COL_Name, DBHelper.COL_Description, DBHelper.COL_Image},
                    DBHelper.COL_ID + " = ?",
                    new String[]{Integer.toString(id)},
                    null, null, null);
            if (cursor.moveToFirst()) {

                int nameIndex = cursor.getColumnIndex(DBHelper.COL_Name);
                int descriptionIndex = cursor.getColumnIndex(DBHelper.COL_Description);
                int imageIndex = cursor.getColumnIndex(DBHelper.COL_Image);

                oldName = cursor.getString(nameIndex);
                oldDescription = cursor.getString(descriptionIndex);
                byte[] oldImage = cursor.getBlob(imageIndex);

                editTextName.setText(oldName);
                editTextDescription.setText(oldDescription);

                Bitmap bitmap = Utils.getImage(oldImage);
                imageButton.setImageBitmap(bitmap);

            }

            db.close();
            cursor.close();
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "problems", Toast.LENGTH_SHORT).show();
        }

        saveButton.setOnClickListener(this::onClick);
        deleteButton.setOnClickListener(this::onClick);

        imageButton.setOnClickListener(this::onClickPhoto);

    }

    public void onClickPhoto(View v) {
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

    public void takePhotoFromGallery(){

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

    public void onClick(View v) {
        SQLiteOpenHelper dbHelper = new DBHelper(getApplicationContext());

        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();



        switch (v.getId()){
            case R.id.changeDataInBaseButton:

                if (!name.equals(oldName) || !description.equals(oldName) || isPhotoChanged) {

                    ContentValues contentValues = new ContentValues();

                    if (!name.equals(oldName)) {
                        contentValues.put(DBHelper.COL_Name, name);
                    }

                    if (!description.equals(oldName)) {
                        contentValues.put(DBHelper.COL_Description, description);
                    }

                    if (isPhotoChanged) {

                        BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                        contentValues.put(DBHelper.COL_Image, Utils.getBytes(scaled));
                    }

                    try {
                        SQLiteDatabase database = dbHelper.getWritableDatabase();

                        database.update(DBHelper.TABLE_Name,
                                contentValues,
                                "id=" + id,
                                null);

                        database.close();
                        dbHelper.close();

                        Intent intent = new Intent();
                        intent.putExtra("action", "save");
                        intent.putExtra(MainActivity.POSITION, (Integer) getIntent().getExtras().get(MainActivity.POSITION));
                        intent.putExtra(MainActivity.POSITION + 1, id);
                        setResult(RESULT_OK, intent);

                        finish();

                    } catch (SQLException e) {
                        Toast.makeText(getApplicationContext(), "problems", Toast.LENGTH_SHORT).show();
                        dbHelper.close();
                    }
                }

                break;
            case R.id.deleteDataFromBaseButton:

                try {
                    SQLiteDatabase database = dbHelper.getWritableDatabase();

                    database.execSQL("DELETE FROM "+ DBHelper.TABLE_Name +" WHERE " + DBHelper.COL_ID + "='"+id+"'");

                    database.close();
                    dbHelper.close();

                    Intent intent = new Intent();
                    intent.putExtra("action", "delete");
                    intent.putExtra(MainActivity.POSITION, (Integer) getIntent().getExtras().get(MainActivity.POSITION));
                    intent.putExtra(MainActivity.POSITION + 1, id);
                    setResult(RESULT_OK, intent);

                    finish();
                }
                catch (SQLException e){
                    Toast.makeText(getApplicationContext(), "problems", Toast.LENGTH_SHORT).show();
                    dbHelper.close();
                }

                break;
            }

        }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageButton.setImageURI(data.getData());
            isPhotoChanged = true;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageButton.setImageBitmap(imageBitmap);

        }

        super.onActivityResult(requestCode, resultCode, data);
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

    public void takePhoto() {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageTakeIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(imageTakeIntent,REQUEST_IMAGE_CAPTURE);

        }
    }
}