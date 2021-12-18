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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class ChangeActivity extends AppCompatActivity {//완료

    private DrawerLayout drawerLayout;
    NavigationView nDrawer;
    ImageButton back, menu;
    ChopChopDB chopDB;
    SQLiteDatabase db;
    Button pictureBtn, save;
    EditText weightEdit, waistEdit, thighEdit;
    String picPATH="", weight="", waist="", thigh="";
    ImageView bodyImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        Init();
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPicPath();
            }
        });

        bodyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPicPath();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSave();
            }
        });


    }
    void setPicPath(){
        final AlertDialog.Builder dlg = new AlertDialog.Builder(ChangeActivity.this);
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
                    bodyImg.setVisibility(View.VISIBLE);   // 내부 저장소에 저장된 이미지를 이미지뷰에 셋
                    pictureBtn.setVisibility(View.GONE);
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

                Toast.makeText(getApplicationContext(), picPATH, Toast.LENGTH_SHORT).show();

            }

        });
        dlg.show();

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
                    bodyImg.setImageBitmap(imgBitmap);
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
                    bodyImg.setImageBitmap(bitmap);
                }

            }
        }
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

    void changeSave() {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Cursor c = null;
        c = db.rawQuery("SELECT date FROM CHANGE WHERE date== '" + date + "';", null);
        if (c.getCount()!=0) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(ChangeActivity.this);
            dlg.setTitle("경고");
            dlg.setIcon(R.mipmap.ic_launcher);
            dlg.setMessage("변화기록은 하루에 한번만 입력가능합니다.");
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.close();
                    finish();
                }
            });
            dlg.show();

        } else {
            boolean number[] = {false, false, false};
            weight = weightEdit.getText().toString();
            waist = waistEdit.getText().toString();
            thigh = thighEdit.getText().toString();
            if (picPATH.equals("") || weight.equals("") || waist.equals("") || thigh.equals("")) {
                Toast.makeText(getApplicationContext(), "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {

                for (int i = 0; i < weight.length(); i++) {
                    char weightNum = weight.charAt(i);
                    if (!Character.isDigit(weightNum))
                        break;
                    else if (i == weight.length() - 1 && Character.isDigit(weightNum))
                        number[0] = true;
                }

                for (int i = 0; i < waist.length(); i++) {
                    char waistNum = waist.charAt(i);
                    if (!Character.isDigit(waistNum))
                        break;
                    else if (i == waist.length() - 1 && Character.isDigit(waistNum))
                        number[1] = true;
                }

                for (int i = 0; i < thigh.length(); i++) {
                    char thighNum = thigh.charAt(i);
                    if (!Character.isDigit(thighNum))
                        break;
                    else if (i == thigh.length() - 1 && Character.isDigit(thighNum))
                        number[2] = true;
                }
                if (number[0] && number[1] && number[2]) {
                    db.execSQL("INSERT INTO CHANGE(date, picPATH, weight, waist, thigh) VALUES ('" + date + "', '" + picPATH + "', '" + weight + "', '" + waist + "', '" + thigh + "');");
                    Toast.makeText(getApplicationContext(), "저장완료!", Toast.LENGTH_SHORT).show();
                    db.close();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "각 수치는 숫자만 입력해주세요", Toast.LENGTH_SHORT).show();
                    if (!number[0]) {
                        weightEdit.setText("");
                        weight = "";
                    }

                    if (!number[1]) {
                        waistEdit.setText("");
                        waist = "";
                    }

                    if (!number[2]) {
                        thighEdit.setText("");
                        thigh = "";
                    }
                }
            }
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
        pictureBtn = findViewById(R.id.pic_btn);
        weightEdit = findViewById(R.id.weight_edit);
        waistEdit = findViewById(R.id.waist_edit);
        thighEdit = findViewById(R.id.thigh_edit);
        bodyImg = findViewById(R.id.bodyImg);
        save = findViewById(R.id.saveBtn);
        menu = findViewById(R.id.menu);
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
                Intent i = new Intent(ChangeActivity.this, MainActivity.class);
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
                        Intent i = new Intent(ChangeActivity.this, CalendarActivity.class);
                        startActivity(i);
                        break;
                    case R.id.graph:
                        Intent j = new Intent(ChangeActivity.this, GraphActivity.class);
                        startActivity(j);
                        break;
                    case R.id.my:
                        Intent k = new Intent(ChangeActivity.this, MyActivity.class);
                        startActivity(k);
                        break;
                }

                return true;
            }
        });
    }
}