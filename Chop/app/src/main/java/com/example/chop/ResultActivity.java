package com.example.chop;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ResultActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    NavigationView nDrawer;
    ImageButton back, menu;
    ChopChopDB chopDB;
    SQLiteDatabase db;
    String date;
    RecyclerView snackRecycler;
    private SnackAdapter adapter;
    TextView text_home, pic_tv, morning, lunch, dinner;
    ImageView pic, morning_pic, lunch_pic, dinner_pic;
    Cursor diet = null, change = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Init();

        text_home.setText(date);
        change.moveToFirst();
        if(change.getCount()!=0){
            pic_tv.setText("눈바디 "+change.getString(2)+"kg");
            File file_ = new File(change.getString(1));
            Uri fileUri = Uri.fromFile(file_);
            Bitmap bitmap=null;
            ContentResolver resolver = getContentResolver();
            try {
            InputStream instream = resolver.openInputStream(fileUri);
            bitmap = BitmapFactory.decodeStream(instream);
            if (bitmap != null) {
                pic.setImageBitmap(bitmap);
            }
            instream.close();
            } catch (Exception e) {
            }
        } else
            pic_tv.setText("변화를 기록해주세요");
        diet.moveToFirst();
        while (!diet.isAfterLast()) {
            File file = new File(diet.getString(3));
            Uri fileUri = Uri.fromFile(file);
            Bitmap bitmap=null;
            ContentResolver resolver = getContentResolver();
            try {
                InputStream instream = resolver.openInputStream(fileUri);
                bitmap = BitmapFactory.decodeStream(instream);
                instream.close();
            } catch (Exception e) {
            }
            if(diet.getString(4).equals("아침")){
                morning.setText(diet.getString(5)+" "+diet.getInt(6)+"kcal");
                try {
                    InputStream instream = resolver.openInputStream(fileUri);
                    bitmap = BitmapFactory.decodeStream(instream);
                    if (bitmap != null) {
                        morning_pic.setImageBitmap(bitmap);
                    }
                    instream.close();
                } catch (Exception e) {
                }
            }else if(diet.getString(4).equals("점심")){
                lunch.setText(diet.getString(5)+" "+diet.getInt(6)+"kcal");
                if (bitmap != null) {
                    lunch_pic.setImageBitmap(bitmap);
                }
            }else if(diet.getString(4).equals("저녁")){
                dinner.setText(diet.getString(5)+" "+diet.getInt(6)+"kcal");
                if (bitmap != null) {
                    dinner_pic.setImageBitmap(bitmap);
                }
            }else if(diet.getString(4).equals("간식")){
                Snack_Item item = new Snack_Item();
                item.setName(diet.getString(5));
                item.setKcal(diet.getString(6)+"kcal");
                item.setImage(bitmap);
                adapter.addItem(item);
            }
            diet.moveToNext();
        }
    }

    void Init(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        nDrawer = (NavigationView)findViewById(R.id.nDrawer);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        back = findViewById(R.id.back);
        chopDB = new ChopChopDB(getApplicationContext());
        db = chopDB.getReadableDatabase();
        naviItem();
        menu = findViewById(R.id.menu);
        Intent i = getIntent();
        date = i.getStringExtra("date");
        diet = db.rawQuery("select * from DIET WHERE date =='"+date+"';", null);
        change = db.rawQuery("select * from CHANGE WHERE date =='"+date+"';", null);
        Log.d("TAG", date);
        date = "- "+date.substring(0, 4)+"년 "+date.substring(4, 6)+"월 "+date.substring(6)+"일 -";
        text_home = findViewById(R.id.text_home);
        pic_tv = findViewById(R.id.pic_tv);
        morning = findViewById(R.id.morning_tv);
        lunch = findViewById(R.id.lunch_tv);
        dinner = findViewById(R.id.dinner_tv);
        snackRecycler = findViewById(R.id.snack_recycler);
        snackRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        snackRecycler.setLayoutManager(linearLayoutManager);
        adapter = new SnackAdapter();
        snackRecycler.setAdapter(adapter);
        pic = findViewById(R.id.pic);
        morning_pic = findViewById(R.id.morning_pic);
        lunch_pic = findViewById(R.id.lunch_pic);
        dinner_pic = findViewById(R.id.dinner_pic);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void naviItem() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.close();
                finish();
            }
        });

        nDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() { //Navigation Drawer 사용
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.calendar:
                        Intent i = new Intent(ResultActivity.this, CalendarActivity.class);
                        finish();
                        startActivity(i);
                        break;
                    case R.id.graph:
                        Intent j = new Intent(ResultActivity.this, GraphActivity.class);
                        finish();
                        startActivity(j);
                        break;
                    case R.id.my:
                        Intent k = new Intent(ResultActivity.this, MyActivity.class);
                        finish();
                        startActivity(k);
                        break;
                }
                return true;
            }
        });
    }
}
