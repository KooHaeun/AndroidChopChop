package com.example.chop;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MyActivity extends AppCompatActivity {//완료

    private DrawerLayout drawerLayout;
    NavigationView nDrawer;
    ImageButton back, menu;
    TextView settingKcal, info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Init();

        settingKcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyActivity.this, SetKcalActivity.class);
                startActivity(i);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MyActivity.this);
                dlg.setTitle("개발자");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setMessage("이름: 구하은\n연락처: 010-8602-1325\n이메일: haeni1325@naver.com");
                dlg.setPositiveButton("확인", null);
                dlg.show();
            }
        });
    }
    
    void Init(){

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        nDrawer = (NavigationView)findViewById(R.id.nDrawer);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        back = findViewById(R.id.back);
        menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        naviItem();
        settingKcal = findViewById(R.id.settingKcal);
        info = findViewById(R.id.info);
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
                Intent i = new Intent(MyActivity.this, MainActivity.class);
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
                        Intent i = new Intent(MyActivity.this, CalendarActivity.class);
                        startActivity(i);
                        break;
                    case R.id.graph:
                        Intent j = new Intent(MyActivity.this, GraphActivity.class);
                        startActivity(j);
                        break;
                    case R.id.my:
                        Intent k = new Intent(MyActivity.this, MyActivity.class);
                        startActivity(k);
                        break;
                }

                return true;
            }
        });
    }
}
