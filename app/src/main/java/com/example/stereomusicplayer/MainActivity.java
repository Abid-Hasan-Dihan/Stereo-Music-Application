package com.example.stereomusicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;
    private long backPressedTime;
    private Toast backToast;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Music List");


        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.black));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.custom)));


        listView = findViewById(R.id.listViewSong);

        runtimePermission();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.aboutUsId){
            Intent intent = new Intent(MainActivity.this, com.example.stereomusicplayer.AboutUs.class);
            startActivity(intent);
        }
        if (item.getItemId()==R.id.feedbackId){
            Intent intent = new Intent(MainActivity.this,com.example.stereomusicplayer.FeedbackActivity.class);
            startActivity(intent);
        }
        if (item.getItemId()==R.id.ShareId){

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            String subject = "Music App";
            String body = "Download now and enjoy your favourite song.\ncom.example.final_music_player; ";

            intent.putExtra(Intent.EXTRA_SUBJECT,subject);
            intent.putExtra(Intent.EXTRA_TEXT,body);

            startActivity(Intent.createChooser(intent," Share with"));
        }


        return super.onOptionsItemSelected(item);
    }

    //Menu Item code segment


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }else {
            backToast = Toast.makeText(getBaseContext(), "Tap Again to push the app to background", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    public void runtimePermission() {

        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();

                    }
                }).check();
    }







    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();
        for (File signlefile:files){

            if (signlefile.isDirectory() && !signlefile.isHidden()) {

                arrayList.addAll(findSong(signlefile));

            } else {

                if (signlefile.getName().endsWith(".mp3") || signlefile.getName().endsWith(".wav")) {

                    arrayList.add(signlefile);
                }
            }
        }
        return arrayList;

    }

    void displaySongs () {

        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

        items = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {

            items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");


        }


        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = (String) listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(),com.example.stereomusicplayer.PlayerActivity.class)
                        .putExtra("songs",mySongs)
                        .putExtra("songname",songName)
                        .putExtra("pos",position)); //position of the song
            }
        });
    }


    class customAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View myView = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textsong = myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items[position]);

            return myView;
        }
    }



}


