package com.example.chop;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static java.lang.Boolean.TRUE;

public class CalendarActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    NavigationView nDrawer;
    ImageButton back, menu;
    MaterialCalendarView cal;
    RecyclerView menuRecycler;
    private CalendarAdapter adapter;
    ChopChopDB chopDB;
    SQLiteDatabase db;
    Integer usecal=0, limit=0, dayCal=0;
    String findDay;
    ArrayList<String> limitDay;
    SimpleDateFormat yearFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Init();

        Cursor cCal = db.rawQuery("SELECT * FROM MYCAL;", null);//칼로리 제한
        cCal.moveToFirst();

        usecal = Integer.parseInt(cCal.getString(0));
        limit = Integer.parseInt(cCal.getString(1));


        cCal.close();


        cal.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                 final Calendar selectCal = date.getCalendar();
                 adapter.clearItem();
                 adapter.notifyDataSetChanged();


                 findDay = yearFormat.format(date.getDate());
                 Cursor c = null;
                 try {

                     selectCal.setTime(yearFormat.parse(yearFormat.format(selectCal.getTime())));

                     c = db.rawQuery("SELECT * FROM DIET WHERE date=='"+findDay+"';", null);
                     if(c.getCount()==0){
                         Date_Menu item = new Date_Menu();
                         item.setDate_menu("아직 등록된 식단이 없습니다");
                         item.setSort(" ");
                         item.setDate_time(" ");
                         item.setDate(" ");
                         adapter.addItem(item);
                         adapter.notifyDataSetChanged();

                     }else{
                         c.moveToFirst();
                         while(!c.isAfterLast()){
                             Date_Menu item = new Date_Menu();
                             dayCal+=Integer.parseInt(c.getString(6));
                             item.setKcal(c.getString(6)+"kcal");
                             item.setDate_menu(c.getString(5));
                             item.setDate_time(c.getString(2));
                             item.setSort(c.getString(4));
                             item.setDate(c.getString(1));
                             c.moveToNext();
                             adapter.addItem(item);
                          }
                     }
                     c.close();
                 }
                 catch (ParseException e) {
                     e.printStackTrace();
                 }

                 adapter.notifyDataSetChanged();

             }
         });
       
        cal.addDecorator(new EventDecorator(Color.RED, Collections.singleton(CalendarDay.today())));
        if(usecal==1) {//칼로리 제한 지킨 날
            try {
                getLimitDay();
                Log.d("limitDay", String.valueOf(limitDay.size()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for(int i=0; i<limitDay.size();i++) {
                int finalI = i;
                cal.addDecorator(new DayViewDecorator() {
                    Calendar customDay = Calendar.getInstance();
                    Calendar keepDay = Calendar.getInstance();
                    @Override
                    public boolean shouldDecorate(CalendarDay day) {
                        try {
                            customDay.setTime(yearFormat.parse(yearFormat.format(day.getCalendar().getTime())));
                            keepDay.setTime(yearFormat.parse(limitDay.get(finalI)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return customDay.compareTo(keepDay) == 0;
                    }
                    @Override
                    public void decorate(DayViewFacade view) {
                        view.addSpan(new ForegroundColorSpan(Color.BLUE) {
                        });
                    }
                });
            }
        }

    }

    void getLimitDay() throws ParseException {
        Cursor c = db.rawQuery("SELECT * FROM DIET", null);
        String s = "";
        c.moveToFirst();
        if(!(c.getString(1).equals(""))) {
            s = c.getString(1);
        }
        for(int i=0; i<c.getCount(); i++){
            if(!(c.getString(1).equals(s))||c.isLast()==TRUE){
                if(!s.equals("")) {
                    if (dayCal <= limit) {
                        limitDay.add(s);
                    }
                }
                s = c.getString(1);
                dayCal=0;
                dayCal+=Integer.parseInt(c.getString(6));
            }else{
                dayCal+=Integer.parseInt(c.getString(6));
            }
            c.moveToNext();
        }
    }

    void Init(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        nDrawer = (NavigationView)findViewById(R.id.nDrawer);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        back = findViewById(R.id.back);
        chopDB = new ChopChopDB(getApplicationContext());
        db = chopDB.getWritableDatabase();
        naviItem();
        menu = findViewById(R.id.menu);
        cal = findViewById(R.id.cal);
        yearFormat= new SimpleDateFormat("yyyyMMdd");
        menuRecycler = findViewById(R.id.menuRecycler);
        menuRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        menuRecycler.setLayoutManager(linearLayoutManager);
        adapter = new CalendarAdapter();
        menuRecycler.setAdapter(adapter);

        limitDay = new ArrayList<>();
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
                Intent i = new Intent(CalendarActivity.this, MainActivity.class);
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
                        Intent i = new Intent(CalendarActivity.this, CalendarActivity.class);
                        finish();
                        startActivity(i);
                        break;
                    case R.id.graph:
                        Intent j = new Intent(CalendarActivity.this, GraphActivity.class);
                        finish();
                        startActivity(j);
                        break;
                    case R.id.my:
                        Intent k = new Intent(CalendarActivity.this, MyActivity.class);
                        finish();
                        startActivity(k);
                        break;
                }
                return true;
            }
        });
    }
}
