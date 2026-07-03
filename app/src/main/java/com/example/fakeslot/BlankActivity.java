package com.example.fakeslot;

import android.app.Activity;
import android.os.Bundle;

public class BlankActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finishAndRemoveTask();
        overridePendingTransition(0, 0);
    }
}
