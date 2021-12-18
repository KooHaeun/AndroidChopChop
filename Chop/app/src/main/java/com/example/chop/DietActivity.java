package com.example.chop;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DietActivity extends AppCompatActivity {//완료

    private DrawerLayout drawerLayout;
    NavigationView nDrawer;
    ImageButton back, menuBtn;
    ChopChopDB chopDB;
    SQLiteDatabase db;
    Cursor c = null;
    Button mealTimeBtn, pictureBtn, save;
    Spinner sortSp;
    EditText menuEdit, kcalEdit;
    String date="", time="", picPATH="", sort="", menu="", kcal="";
    DatePicker datePicker;
    TimePicker timePicker;
    ImageView menuImg;
    TextView pictureTv;
    Integer sortNum, dayKcal=0, usecal, limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        Init();//초기화
        setMealTime();//식사시간선택
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuImg.setVisibility(View.VISIBLE);
                setPicPath();//사진선택
            }
        });

        setElse();//그외 입력값 받아오기
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dietSave();//DB에 저장
            }
        });

        menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPicPath();
            }
        });


    }

    void setMealTime(){
        mealTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dlg = new AlertDialog.Builder(DietActivity.this);
                View mealTimeDlg = View.inflate(getApplicationContext(), R.layout.mealtime_dialog,null);

                datePicker = mealTimeDlg.findViewById(R.id.date);
                timePicker = mealTimeDlg.findViewById(R.id.time);

                dlg.setTitle("식사시간 선택");
                dlg.setView(mealTimeDlg);
                dlg.setIcon(R.drawable.main);
                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        if ((monthOfYear + 1) < 9) {
                            if (dayOfMonth < 9)
                                date = String.valueOf(year) + "0" + String.valueOf(monthOfYear + 1) + "0" + String.valueOf(dayOfMonth);
                            else
                                date = String.valueOf(year) + "0" + String.valueOf(monthOfYear + 1) + String.valueOf(dayOfMonth);

                        } else {
                            if (dayOfMonth < 9)
                                date = String.valueOf(year) + String.valueOf(monthOfYear + 1) + "0" + String.valueOf(dayOfMonth);
                            else
                                date = String.valueOf(year) + String.valueOf(monthOfYear + 1) + String.valueOf(dayOfMonth);
                        }
                    }
                });

                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        if(hourOfDay<10&&minute<10)
                            time = "0"+hourOfDay+":0"+minute;
                        else if(hourOfDay<10)
                            time = "0"+hourOfDay+":"+minute;
                        else if (minute<10)
                            time = hourOfDay+":0"+minute;
                        else
                            time = hourOfDay+":"+minute;
                    }
                });
                dlg.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mealTimeBtn.setTextSize((float) 10.1);
                        mealTimeBtn.setText(date+"/ "+time);
                    }
                });
                dlg.show();
            }
        });
    }

    void setPicPath(){
        final AlertDialog.Builder dlg = new AlertDialog.Builder(DietActivity.this);
        dlg.setTitle("사진업로드");
        dlg.setIcon(R.drawable.main);
        dlg.setPositiveButton("앨범", new DialogInterface.OnClickListener() {//사진 앨범에서 불러오기
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                try {
                    pictureBtn.setVisibility(View.GONE);
                    pictureTv.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                }

            }
        });
        dlg.setNegativeButton("촬영", new DialogInterface.OnClickListener() {//사진 촬영 후 업로드
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }

                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                            "com.example.chop.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, 0);
                }

                pictureBtn.setVisibility(View.GONE);
                pictureTv.setVisibility(View.INVISIBLE);

            }

        });
        dlg.show();

    }
    public static String createCopyAndReturnRealPath(@NonNull Context context, @NonNull Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null)
            return null;
        String filePath = context.getApplicationInfo().dataDir + File.separator+ System.currentTimeMillis();
        File file = new File(filePath);
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);

            if (inputStream == null)
                return null;

            // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.

            OutputStream outputStream = new FileOutputStream(file);

            byte[] buf = new byte[1024];

            int len;

            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);
            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }
        return file.getAbsolutePath();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                ContentResolver resolver = getContentResolver();
                try {
                    picPATH = createCopyAndReturnRealPath(this, fileUri);
                    InputStream instream = resolver.openInputStream(fileUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                    menuImg.setImageBitmap(imgBitmap);
                    instream.close();
                } catch (Exception e) {
                }
            }
        }
        else if(requestCode == 0){
            if (resultCode == RESULT_OK) {
                File file = new File(picPATH);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media
                            .getBitmap(getContentResolver(), Uri.fromFile(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    menuImg.setImageBitmap(bitmap);
                }

            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        picPATH = image.getAbsolutePath();
        return image;
    }

    void setElse(){
        sortSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        sortNum = 0;
                        sort = "아침";
                        break;
                    case 1:
                        sortNum = 1;
                        sort = "점심";
                        break;
                    case 2:
                        sortNum = 2;
                        sort = "저녁";
                        break;
                    case 3:
                        sortNum = 3;
                        sort = "간식";
                        break;
                    default:
                        sortNum = 0;
                        sort = "아침";
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                sort = "아침";
                sortNum=0;
            }
        });
    }
    void dietSave() {
        boolean number = false;
        menu = menuEdit.getText().toString();
        kcal = kcalEdit.getText().toString();
        if (date.equals("") || time.equals("") || picPATH.equals("") || sort.equals("") || menu.equals("") || kcal.equals(""))
            Toast.makeText(getApplicationContext(), "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show();
        else {

            for(int i=0; i<kcal.length(); i++){
                char kcalNum = kcal.charAt(i);
                if(!Character.isDigit(kcalNum))
                    break;
                else if(i==kcal.length()-1&&Character.isDigit(kcalNum))
                    number = true;
            }
            c = db.rawQuery("SELECT * FROM MYCAL;", null);//칼로리 제한
            while(c.moveToNext()){
                usecal = c.getInt(0);
                limit = c.getInt(1);
            }
            c.close();
            if(usecal==0){
                if (number) {
                    elseSen();
                }
                else{
                    Toast.makeText(getApplicationContext(), "칼로리는 숫자만 입력해주세요", Toast.LENGTH_SHORT).show();
                    kcalEdit.setText("");
                    kcal = "";
                }
            }else if(usecal==1) {
                Cursor table = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name ='DIET'",null);
                if(table.getCount()!=0){
                    Cursor cal = db.rawQuery("SELECT * FROM DIET WHERE date=='"+date+"';", null);

                    while(cal.moveToNext()){
                        dayKcal += cal.getInt(6);
                    }
                    if (number) {

                        if (dayKcal>limit||Integer.parseInt(kcal)>limit){
                            AlertDialog.Builder dlg = new AlertDialog.Builder(DietActivity.this);
                            dlg.setTitle("경고");
                            dlg.setIcon(R.mipmap.ic_launcher);
                            dlg.setMessage("제한 칼로리를 초과했습니다.");
                            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    elseSen();
                                }
                            });
                            dlg.show();
                        }
                        else{
                            elseSen();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "칼로리는 숫자만 입력해주세요", Toast.LENGTH_SHORT).show();
                        kcalEdit.setText("");
                        kcal = "";
                    }
                    
                    cal.close();
                }
                else{
                    if (number) {
                        elseSen();

                    }else{
                        Toast.makeText(getApplicationContext(), "칼로리는 숫자만 입력해주세요", Toast.LENGTH_SHORT).show();
                        kcalEdit.setText("");
                        kcal = "";
                    }
                }
                
                table.close();
            }
        }
    }

    void elseSen(){
        db.execSQL("INSERT INTO DIET(date, time, picPATH, sort, menu, kcal) VALUES ('" + date + "', '" + time + "', '" + picPATH + "', '" + sort + "', '" + menu + "', '" + kcal + "');");
        Toast.makeText(getApplicationContext(), "저장완료!", Toast.LENGTH_SHORT).show();
        db.close();
        finish();
    }

    void Init(){

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        nDrawer = (NavigationView)findViewById(R.id.nDrawer);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        back = findViewById(R.id.back);
        chopDB = new ChopChopDB(getApplicationContext());
        db = chopDB.getWritableDatabase();
        naviItem();
        mealTimeBtn = findViewById(R.id.mealTimebtn);
        pictureBtn = findViewById(R.id.pictureBtn);
        sortSp = findViewById(R.id.sortSp);
        menuEdit = findViewById(R.id.menuEdit);
        kcalEdit = findViewById(R.id.calEdit);
        save = findViewById(R.id.saveBtn);
        menuImg = findViewById(R.id.menuImg);
        pictureTv = findViewById(R.id.pictureTv);
        menuBtn = findViewById(R.id.menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
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
                Intent i = new Intent(DietActivity.this, MainActivity.class);
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
                        db.close();
                        Intent i = new Intent(DietActivity.this, CalendarActivity.class);
                        finish();
                        startActivity(i);
                        break;
                    case R.id.graph:
                        db.close();
                        Intent j = new Intent(DietActivity.this, GraphActivity.class);
                        finish();
                        startActivity(j);
                        break;
                    case R.id.my:
                        db.close();
                        Intent k = new Intent(DietActivity.this, MyActivity.class);
                        finish();
                        startActivity(k);
                        break;
                }

                return true;
            }
        });
    }
}
