package com.example.databaseexercisetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExerciseActivity extends AppCompatActivity {

    private int seconds = 0;
    private boolean running;
    private boolean wasRunning;



    ArrayList<ExerciseModel> list;
    DBHelper dbHelper;
    TextView currentExercise, currentExerciseDescription;
    ImageView currentExerciseImage;
    int sizeOfExercises = 0;
    int currentId = 0;
    int[] ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        if (savedInstanceState != null){
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
            currentId = savedInstanceState.getInt("currentId");
        }

        ids = getIntent().getIntArrayExtra(MainActivity.PLAYLIST);
        list = getDataFromDataBase(ids);
        sizeOfExercises = ids.length;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.list_exercise);
        setSupportActionBar(toolbar);
//        currentExercise = (TextView) toolbar.findViewById(R.id.currentExercise);
//        currentExercise.setText(list.get(currentId).getName());

        currentExercise = (TextView) findViewById(R.id.currentExercise);
        currentExerciseDescription = (TextView) findViewById(R.id.currentExerciseDescription);

        currentExercise.setText(list.get(currentId).getName());
        currentExerciseDescription.setText(list.get(currentId).getDescription());

        currentExerciseImage = (ImageView) findViewById(R.id.imageInExerciseActivity);
        currentExerciseImage.setImageBitmap(Utils.getImage(list.get(currentId).getImage()));

        runTimer();
    }

    private ArrayList<ExerciseModel> getDataFromDataBase(int[] idsInt) {

        String[] idsString = new String[idsInt.length];

        for (int i = 0; i < idsInt.length; i++){
            idsString[i] = Integer.toString(idsInt[i]);
        }


        ArrayList<ExerciseModel> listExercise = new ArrayList<>();

        dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DBHelper.TABLE_Name, new String[] {DBHelper.COL_ID, DBHelper.COL_Name, DBHelper.COL_Description, DBHelper.COL_Image},
                null, null, null, null, null);

        if (cursor.moveToFirst()){

            int idIndex = cursor.getColumnIndex(DBHelper.COL_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.COL_Name);
            int descriptionIndex = cursor.getColumnIndex(DBHelper.COL_Description);
            int imageIndex = cursor.getColumnIndex(DBHelper.COL_Image);

            do {

                boolean idInList = false;

                for (int i : ids){
                    if (i == cursor.getInt(idIndex)){
                        idInList = true;
                        break;
                    }
                }

                if (idInList) {
                    listExercise.add(new ExerciseModel(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getString(descriptionIndex), cursor.getBlob(imageIndex)));
                }
            } while (cursor.moveToNext());

        }
        else {
            Log.d("mLog", "0 rows");
            Toast.makeText(getApplicationContext(), "show", Toast.LENGTH_SHORT).show();
        }

        return listExercise;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);

        outState.putInt("currentId", currentId);

        super.onSaveInstanceState(outState);
    }

    private void runTimer(){
        final TextView timeView = (TextView)findViewById(R.id.timeView);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);
                if (running){
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });

    }

    public void onClickStart(View v){
        running = true;
    }

    public void onClickStop(View v){
        running = false;
    }
    
    public void onClickReset(View v){
        running = false;
        seconds = 0;
    }

    public void onClickNextExercise(View v){
        if ((currentId + 1) < ids.length) {
            currentId++;
//            currentExercise.setText(list.get(currentId).getName());
            //getActionBar().setTitle(list.get(currentId).getName());
            currentExercise.setText(list.get(currentId).getName());

            currentExerciseDescription.setText(list.get(currentId).getDescription());

            currentExerciseImage.setImageBitmap(Utils.getImage(list.get(currentId).getImage()));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        wasRunning = running;
        running = false;

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (wasRunning){
            running = true;
        }

    }
}