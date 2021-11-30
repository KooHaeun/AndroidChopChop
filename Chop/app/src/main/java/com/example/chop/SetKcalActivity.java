package com.example.chop;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class SetKcalActivity extends AppCompatActivity {//완료

    private DrawerLayout drawerLayout;
    NavigationView nDrawer;
    ImageButton back;
    ChopChopDB chopDB;
    SQLiteDatabase db;
    RadioGroup RG;
    TextView kcalty;
    EditText kcalEdit;
    Button save;
    Integer use=0, kcal=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_kcal);

        Init();

        RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (RG.getCheckedRadioButtonId()){
                    case R.id.yes:
                        use = 1;
                        kcalty.setVisibility(View.VISIBLE);
                        kcalEdit.setVisibility(View.VISIBLE);
                        break;
                    case R.id.no:
                        use = 0;
                        kcalty.setVisibility(View.INVISIBLE);
                        kcalEdit.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        use = -1;
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(use==-1)
                    Toast.makeText(getApplicationContext(),"사용 여부를 선택해주세요", Toast.LENGTH_SHORT).show();
                else if(RG.getCheckedRadioButtonId()==R.id.yes){
                    kcal = Integer.parseInt(kcalEdit.getText().toString());
                    db.execSQL("UPDATE MYCAL SET use = '"+use+"', cal = '"+kcal+"';");
                    finish();
                    Toast.makeText(getApplicationContext(),"저장 완료", Toast.LENGTH_SHORT).show();
                }
                else if(RG.getCheckedRadioButtonId()==R.id.no){
                    db.execSQL("UPDATE MYCAL SET use = '"+use+"', cal = '"+kcal+"';");
                    finish();
                    Toast.makeText(getApplicationContext(),"저장 완료", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }
        });

    }
    void Init(){

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        nDrawer = (NavigationView)findViewById(R.id.nDrawer);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        back = findViewById(R.id.back);
        chopDB = new ChopChopDB(getApplicationContext());
        db = chopDB.getWritableDatabase();
        naviItem();
        RG = findViewById(R.id.RG);
        kcalty = findViewById(R.id.kcaltv);
        kcalEdit = findViewById(R.id.kcalEdit);
        save = findViewById(R.id.saveBtn);

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
                Intent i = new Intent(SetKcalActivity.this, MainActivity.class);
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
                        Intent i = new Intent(SetKcalActivity.this, CalendarActivity.class);
                        finish();
                        startActivity(i);
                        break;
                    case R.id.graph:
                        Intent j = new Intent(SetKcalActivity.this, GraphActivity.class);
                        finish();
                        startActivity(j);
                        break;
                    case R.id.my:
                        Intent k = new Intent(SetKcalActivity.this, MyActivity.class);
                        finish();
                        startActivity(k);
                        break;
                }

                return true;
            }
        });
    }
}

