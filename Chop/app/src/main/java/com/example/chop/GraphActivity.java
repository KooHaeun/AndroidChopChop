package com.example.chop;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Boolean.TRUE;

public class GraphActivity extends AppCompatActivity {//완료

    private DrawerLayout drawerLayout;
    NavigationView nDrawer;
    ImageButton back, menu;
    LineChart chart;
    TextView startDay, endDay;
    String sDate, eDate;
    String sort="";
    Integer dayScore[];
    Button showBtn;
    ChopChopDB chopDB;
    SQLiteDatabase db;
    ArrayList<String> days;
    ArrayList<Entry> kg, waist, thigh;
    LineDataSet set;
    Spinner sortSpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Init();
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        DateFormat Mydf = new SimpleDateFormat("MMdd");
        chart.setNoDataText("날짜를 선택해주세요");

        startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dlg = new DatePickerDialog(GraphActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        sDate = year+".";
                        if(month<10)
                            sDate += "0"+(month+1);
                        else
                            sDate += (month+1);
                        if(dayOfMonth<10)
                            sDate += ".0"+dayOfMonth;
                        else
                            sDate += "."+ dayOfMonth;
                        startDay.setText(sDate);
                    }
                }, 2021, 10, 24);
                dlg.show();
            }
        });
        endDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dlg = new DatePickerDialog(GraphActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eDate = year+".";
                        if(month<10)
                            eDate += "0"+(month+1);
                        else
                            eDate += (month+1);
                        if(dayOfMonth<10)
                            eDate += ".0"+dayOfMonth;
                        else
                            eDate += "."+ dayOfMonth;

                        endDay.setText(eDate);
                    }
                }, 2021, 10, 24);
                dlg.show();
            }
        });
        sortSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                    sort = "체중";
                else if(position==1)
                    sort = "허벅지 둘레";
                else if(position==2)
                    sort = "허리 둘레";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sort = "체중";
            }
        });
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chart.clear();
                try {
                    Date d1 = df.parse( sDate );
                    Date d2 = df.parse( eDate );
                    if(currentTime.after(d2)){
                        if(d2.after(d1)) {
                            Calendar c1 = Calendar.getInstance();
                            Calendar c2 = Calendar.getInstance();
                            c1.setTime(d1);
                            c2.setTime(d2);
                            days=new ArrayList<>();
                            int i = 0;
                            while (c1.compareTo(c2) != 1) {

                                days.add(Mydf.format(c1.getTime()));
                                Log.d("abcdef", days.get(i));
                                c1.add(Calendar.DATE, 1);
                                i++;
                            }
                            setChart(i);
                        }
                        else
                            showDlg1();
                    }else
                        showDlg2();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        kg = new ArrayList<>();
        thigh = new ArrayList<>();
        waist = new ArrayList<>();
    }

    void showDlg1(){
        AlertDialog.Builder dlg = new AlertDialog.Builder(GraphActivity.this);
        dlg.setTitle("경고");
        dlg.setMessage("마지막 날짜가 시작 날짜보다 커야합니다. 다시 선택하세요");
        dlg.setIcon(R.drawable.main);
        dlg.setPositiveButton("네", null);
        dlg.show();
        startDay.setText("시작 날짜");
        endDay.setText("마지막 날짜");
    }

    void showDlg2(){
        AlertDialog.Builder dlg = new AlertDialog.Builder(GraphActivity.this);
        dlg.setTitle("경고");
        dlg.setIcon(R.drawable.main);
        dlg.setMessage("마지막 날짜는 오늘까지만 가능합니다. 다시 선택하세요");
        dlg.setPositiveButton("네", null);
        dlg.show();
        startDay.setText("시작 날짜");
        endDay.setText("마지막 날짜");
    }

    public void setChart(int len){

        for (int i = 0; i < len; i++) {
            dayScore = new Integer[len];
            dayScore[i] = 0;
            final Cursor[] c = {null};


            c[0] = db.rawQuery("SELECT * FROM CHANGE", null);

            c[0].moveToFirst();

            while (!c[0].isAfterLast()) {
                if (c[0].getString(0).substring(4).equals(days.get(i)))
                    if(sort.equals("체중")) {
                        dayScore[i] = c[0].getInt(2);
                    }
                else if(sort.equals("허벅지 둘레"))
                    dayScore[i] = c[0].getInt(3);
                else if(sort.equals("허리 둘레"))
                    dayScore[i] = c[0].getInt(4);
                c[0].moveToNext();
            }
            c[0].close();
            if(sort.equals("체중"))
                kg.add(new Entry(Integer.parseInt(days.get(i).substring(2)), dayScore[i]));
            else if(sort.equals("허벅지 둘레"))
                thigh.add(new Entry(Integer.parseInt(days.get(i).substring(2)), dayScore[i]));
            else if (sort.equals("허리 둘레"))
                waist.add(new Entry(Integer.parseInt(days.get(i).substring(2)), dayScore[i]));
        }

        if(sort.equals("체중"))
            set = new LineDataSet(kg, "체중");
        else if(sort.equals("허벅지 둘레"))
            set = new LineDataSet(thigh, "허벅지 둘레");
        else if(sort.equals("허리 둘레"))
            set = new LineDataSet(waist, "허리 둘레");

        set.setColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));
        set.setCircleColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));
        set.setCircleHoleColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));


        LineData lineData = new LineData();
        lineData.addDataSet(set);

        lineData.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        lineData.setValueTextSize(10);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(len, true);
        xAxis.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        xAxis.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        yAxisLeft.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        //y축의 활성화를 제거함

        chart.setVisibleXRangeMinimum(60 * 60 * 24 * 1000 * 5);
        chart.setDescription(null);

        Legend legend = chart.getLegend();
        legend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        chart.animateY(2000);
        chart.setData(lineData);

    }

    void Init(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        nDrawer = (NavigationView)findViewById(R.id.nDrawer);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        back = findViewById(R.id.back);
        naviItem();
        chopDB = new ChopChopDB(getApplicationContext());
        db = chopDB.getReadableDatabase();
        chart = findViewById(R.id.chart);
        menu = findViewById(R.id.menu);
        startDay = findViewById(R.id.startDay);
        endDay = findViewById(R.id.endDay);
        showBtn = findViewById(R.id.showBtn);
        sortSpin = findViewById(R.id.sortSpin);
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
                Intent i = new Intent(GraphActivity.this, MainActivity.class);
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
                        Intent i = new Intent(GraphActivity.this, CalendarActivity.class);
                        finish();
                        startActivity(i);
                        break;
                    case R.id.graph:
                        Intent j = new Intent(GraphActivity.this, GraphActivity.class);
                        finish();
                        startActivity(j);
                        break;
                    case R.id.my:
                        Intent k = new Intent(GraphActivity.this, MyActivity.class);
                        finish();
                        startActivity(k);
                        break;
                }
                return true;
            }
        });
    }
}
