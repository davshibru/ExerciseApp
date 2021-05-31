package com.example.databaseexercisetest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_NEW_EXERCISE = 1200;
    private static final int DETAIL_WORK_WITH_ITEM = 1201;
    public static final String POSITION = "position";
    public static final String PLAYLIST = "playList";
    public static final String DETAIL_WORK_WITH_ITEM_STRING = "DETAIL_WORK_WITH_ITEM_STRING";

    ExerciseAdapter adapter;
    Button addExerciseButton, addDataInBaseButton, startTraining;
    FloatingActionButton fab;
    List<ExerciseModel> listDialog;
    ExerciseAdapter adapterDialog;


    ArrayList<ExerciseModel> list;

    DBHelper dbHelper;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addExerciseButton = (Button) findViewById(R.id.addExerciseButton);
        addDataInBaseButton = (Button) findViewById(R.id.addDataInBaseButton);
        startTraining = (Button) findViewById(R.id.startExerciseButton);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        addDataInBaseButton.setOnClickListener(this::onClick);
        addExerciseButton.setOnClickListener(this::onClick);
        startTraining.setOnClickListener(this::onClick);
        fab.setOnClickListener(this::onClick);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewExerciseActivity);

        list = new ArrayList<>();
        adapter = new ExerciseAdapter(list);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setLongListener(new ExerciseAdapter.LongListener() {
            @Override
            public void onClick(int id, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.remove);

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                });

                builder.show();
            }
        });


    }

    private void onClick(View v){
        Intent intent = null;

        switch (v.getId()){

            case R.id.addDataInBaseButton:

                intent = new Intent(this, AddDataInDataBase.class);
                startActivityForResult(intent, ADD_NEW_EXERCISE);

                break;
            case R.id.addExerciseButton:

                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_add_data_in_data_base);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setGravity(Gravity.CENTER);

                RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerViewExerciseDialog);

                listDialog = getDataFromDataBase();
                adapterDialog = new ExerciseAdapter(listDialog);

                recyclerView.setAdapter(adapterDialog);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                adapterDialog.setListener(new ExerciseAdapter.Listener() {
                    @Override
                    public void onClick(int id, int position) {
                        addDataInPlayList(id);
                    }
                });

                adapterDialog.setLongListener(new ExerciseAdapter.LongListener() {
                    @Override
                    public void onClick(int id, int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(getString(R.string.change_or_delete_exercise));

                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), DetailExerciseActivity.class);
                                intent.putExtra(DETAIL_WORK_WITH_ITEM_STRING, id);
                                startActivityForResult(intent, DETAIL_WORK_WITH_ITEM);
                            }
                        });

                        builder.show();
                    }
                });

                dialog.show();
                break;

            case R.id.startExerciseButton:

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.start_training);

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), ExerciseActivity.class);

                        int[] idArray = new int[list.size()];

                        for (int i = 0; i < list.size(); i++){
                            idArray[i] = list.get(i).getId();
                        }

                        intent.putExtra(PLAYLIST, idArray);
                        startActivity(intent);

                    }
                });

                builder.show();

                break;
            case R.id.fab:

                AlertDialog.Builder builderFab = new AlertDialog.Builder(MainActivity.this);
                builderFab.setTitle(getString(R.string.do_you_want_watch_more_information_about_sport));

                builderFab.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderFab.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), InformationAboutSport.class);
                        startActivity(intent);
                    }
                });

                builderFab.show();
                break;

        }
    }

    private void addDataInPlayList(int id) {

        boolean isHave = false;
        for (ExerciseModel model : list){

            if (model.getId() == id) {
                isHave = true;
                break;
            }
        }

        if (!isHave){
            list.add(getDataFromDataBase(id));
            adapter.notifyItemInserted(list.size());
        }
        else {

            Toast.makeText(getApplicationContext(), "Allready have in playlist", Toast.LENGTH_SHORT).show();
        }

    }

    private ExerciseModel getDataFromDataBase(int id) {

        ExerciseModel exerciseModel = null;

        dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        cursor = database.query(DBHelper.TABLE_Name, new String[] {DBHelper.COL_ID, DBHelper.COL_Name, DBHelper.COL_Description, DBHelper.COL_Image},
                DBHelper.COL_ID + " = ?", new String[] {Integer.toString(id)}, null, null, null);

        if (cursor.moveToFirst()){

            int idIndex = cursor.getColumnIndex(DBHelper.COL_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.COL_Name);
            int descriptionIndex = cursor.getColumnIndex(DBHelper.COL_Description);
            int imageIndex = cursor.getColumnIndex(DBHelper.COL_Image);



           exerciseModel =  new ExerciseModel(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(descriptionIndex), cursor.getBlob(imageIndex));

        }
        else {
            Log.d("mLog", "0 rows");
            Toast.makeText(getApplicationContext(), "show", Toast.LENGTH_SHORT).show();
        }

        return exerciseModel;

    }


    private ArrayList<ExerciseModel> getDataFromDataBase() {

        ArrayList<ExerciseModel> listExercise = new ArrayList<>();

        dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        cursor = database.query(DBHelper.TABLE_Name, new String[] {DBHelper.COL_ID, DBHelper.COL_Name, DBHelper.COL_Description, DBHelper.COL_Image},
                null, null, null, null, null);

        if (cursor.moveToFirst()){

            int idIndex = cursor.getColumnIndex(DBHelper.COL_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.COL_Name);
            int descriptionIndex = cursor.getColumnIndex(DBHelper.COL_Description);
            int imageIndex = cursor.getColumnIndex(DBHelper.COL_Image);

            do {

                listExercise.add(new ExerciseModel(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(descriptionIndex), cursor.getBlob(imageIndex)));

            } while (cursor.moveToNext());

        }
        else {
            Log.d("mLog", "0 rows");
            Toast.makeText(getApplicationContext(), "show", Toast.LENGTH_SHORT).show();
        }

        return listExercise;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


//        if (resultCode == RESULT_OK && requestCode == ADD_NEW_EXERCISE){
//            list.clear();
//            list.addAll(getDataFromDataBase());
//            adapter.notifyItemInserted(list.size());
//        }


        if (resultCode == RESULT_OK && requestCode == DETAIL_WORK_WITH_ITEM){


            if (data.getExtras().get("action").equals("save")){

                listDialog.clear();
                listDialog.addAll(getDataFromDataBase());
                adapterDialog.notifyDataSetChanged();
            }

            if (data.getExtras().get("action").equals("delete")){

                listDialog.clear();
                listDialog.addAll(getDataFromDataBase());
                adapterDialog.notifyDataSetChanged();

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        dbHelper.close();
    }
}