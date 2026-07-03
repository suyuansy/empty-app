package com.example.fakeslot;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class BlankActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new View(this);
        view.setBackgroundColor(Color.rgb(250, 250, 252));
        setContentView(view);
    }
}
