package com.example.fakeslot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ShortcutPinnedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String label = intent.getStringExtra("label");
        if (label == null) {
            label = context.getString(R.string.shortcut_fallback_label);
        }
        Toast.makeText(context, context.getString(R.string.shortcut_pinned, label), Toast.LENGTH_SHORT).show();
    }
}
