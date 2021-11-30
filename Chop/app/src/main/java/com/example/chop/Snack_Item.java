package com.example.chop;

import android.graphics.Bitmap;

public class Snack_Item {
    String name;
    String kcal;
    Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKcal() { return kcal; }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

}
