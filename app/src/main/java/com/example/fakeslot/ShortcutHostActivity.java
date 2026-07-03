package com.example.fakeslot;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ShortcutHostActivity extends Activity {
    private static final ShortcutSpec[] SHORTCUTS = new ShortcutSpec[] {
            new ShortcutSpec("tools", R.string.shortcut_tools, Color.rgb(53, 117, 246), Color.WHITE, 0),
            new ShortcutSpec("notes", R.string.shortcut_notes, Color.rgb(242, 137, 64), Color.WHITE, 1),
            new ShortcutSpec("space", R.string.shortcut_space, Color.rgb(40, 45, 64), Color.WHITE, 2),
            new ShortcutSpec("guide", R.string.shortcut_guide, Color.rgb(20, 166, 127), Color.WHITE, 3)
    };

    private static final String[] ALIASES = new String[] {
            "com.example.fakeslot.AliasTools",
            "com.example.fakeslot.AliasNotes",
            "com.example.fakeslot.AliasSpace"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createContentView());
    }

    private View createContentView() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(Color.rgb(247, 248, 252));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(28), dp(24), dp(28));
        scrollView.addView(root);

        TextView title = new TextView(this);
        title.setText(R.string.app_name);
        title.setTextSize(28);
        title.setTextColor(Color.rgb(25, 28, 36));
        title.setGravity(Gravity.START);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        root.addView(title, matchWrap());

        TextView intro = paragraph(getString(R.string.intro));
        root.addView(intro, topMargin(12));

        root.addView(sectionTitle(getString(R.string.shortcut_section)), topMargin(24));
        for (ShortcutSpec spec : SHORTCUTS) {
            Button button = button(getString(R.string.add_shortcut_button, getString(spec.labelResId)));
            button.setOnClickListener(view -> requestShortcut(spec));
            root.addView(button, topMargin(10));
        }

        root.addView(sectionTitle(getString(R.string.alias_section)), topMargin(28));
        TextView aliasTip = paragraph(getString(R.string.alias_tip));
        root.addView(aliasTip, topMargin(8));

        Button enableAliases = button(getString(R.string.enable_aliases_button));
        enableAliases.setOnClickListener(view -> setAliasesEnabled(true));
        root.addView(enableAliases, topMargin(10));

        Button disableAliases = button(getString(R.string.disable_aliases_button));
        disableAliases.setOnClickListener(view -> setAliasesEnabled(false));
        root.addView(disableAliases, topMargin(10));

        Button launcherSettings = button(getString(R.string.open_home_settings_button));
        launcherSettings.setOnClickListener(view -> openHomeSettings());
        root.addView(launcherSettings, topMargin(20));

        return scrollView;
    }

    private void requestShortcut(ShortcutSpec spec) {
        ShortcutManager manager = getSystemService(ShortcutManager.class);
        if (manager == null || !manager.isRequestPinShortcutSupported()) {
            Toast.makeText(this, R.string.shortcut_not_supported, Toast.LENGTH_LONG).show();
            return;
        }

        String label = getString(spec.labelResId);

        Intent launchIntent = new Intent(this, BlankActivity.class)
                .setAction(Intent.ACTION_VIEW)
                .putExtra("slot_id", spec.id);

        ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "slot_" + spec.id)
                .setShortLabel(label)
                .setLongLabel(label)
                .setIcon(Icon.createWithAdaptiveBitmap(createAdaptiveBitmap(spec)))
                .setIntent(launchIntent)
                .build();

        Intent callbackIntent = new Intent(this, ShortcutPinnedReceiver.class)
                .putExtra("label", label);
        PendingIntent callback = PendingIntent.getBroadcast(
                this,
                spec.id.hashCode(),
                callbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        manager.requestPinShortcut(shortcut, callback.getIntentSender());
        Toast.makeText(this, getString(R.string.shortcut_requested, label), Toast.LENGTH_SHORT).show();
    }

    private Bitmap createAdaptiveBitmap(ShortcutSpec spec) {
        int size = 432;
        float unit = size / 108f;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new LinearGradient(
                0,
                0,
                size,
                size,
                lighten(spec.backgroundColor, 0.16f),
                spec.backgroundColor,
                Shader.TileMode.CLAMP
        ));
        canvas.drawRect(0, 0, size, size, paint);

        paint.setShader(null);
        paint.setColor(adjustAlpha(Color.WHITE, 0.13f));
        canvas.drawCircle(size * 0.82f, size * 0.16f, size * 0.28f, paint);

        Paint glyph = new Paint(Paint.ANTI_ALIAS_FLAG);
        glyph.setColor(spec.foregroundColor);
        glyph.setStyle(Paint.Style.STROKE);
        glyph.setStrokeWidth(7.5f * unit);
        glyph.setStrokeCap(Paint.Cap.ROUND);
        glyph.setStrokeJoin(Paint.Join.ROUND);

        switch (spec.glyph) {
            case 0:
                drawToolsGlyph(canvas, glyph, unit);
                break;
            case 1:
                drawNotesGlyph(canvas, glyph, unit);
                break;
            case 2:
                drawSpaceGlyph(canvas, glyph, unit);
                break;
            default:
                drawGuideGlyph(canvas, glyph, unit);
                break;
        }
        return bitmap;
    }

    private void drawToolsGlyph(Canvas canvas, Paint glyph, float unit) {
        float left = 32f * unit;
        float top = 30f * unit;
        float right = 76f * unit;
        float bottom = 78f * unit;
        canvas.drawRoundRect(new RectF(left, top, right, bottom), 10f * unit, 10f * unit, glyph);
        canvas.drawLine(44f * unit, 30f * unit, 44f * unit, 78f * unit, glyph);
        canvas.drawLine(32f * unit, 46f * unit, 76f * unit, 46f * unit, glyph);
        canvas.drawLine(32f * unit, 62f * unit, 76f * unit, 62f * unit, glyph);
    }

    private void drawNotesGlyph(Canvas canvas, Paint glyph, float unit) {
        canvas.drawLine(32f * unit, 56f * unit, 48f * unit, 72f * unit, glyph);
        canvas.drawLine(48f * unit, 72f * unit, 78f * unit, 34f * unit, glyph);
    }

    private void drawSpaceGlyph(Canvas canvas, Paint glyph, float unit) {
        canvas.drawCircle(54f * unit, 54f * unit, 25f * unit, glyph);
        canvas.drawCircle(54f * unit, 54f * unit, 8f * unit, glyph);
    }

    private void drawGuideGlyph(Canvas canvas, Paint glyph, float unit) {
        canvas.drawRoundRect(new RectF(32f * unit, 34f * unit, 76f * unit, 76f * unit), 8f * unit, 8f * unit, glyph);
        canvas.drawLine(44f * unit, 44f * unit, 64f * unit, 44f * unit, glyph);
        canvas.drawLine(44f * unit, 56f * unit, 64f * unit, 56f * unit, glyph);
        canvas.drawLine(44f * unit, 68f * unit, 58f * unit, 68f * unit, glyph);
    }

    private void setAliasesEnabled(boolean enabled) {
        PackageManager packageManager = getPackageManager();
        int state = enabled
                ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        for (String alias : ALIASES) {
            packageManager.setComponentEnabledSetting(
                    new ComponentName(this, alias),
                    state,
                    PackageManager.DONT_KILL_APP
            );
        }
        Toast.makeText(
                this,
                enabled ? R.string.aliases_enabled : R.string.aliases_disabled,
                Toast.LENGTH_LONG
        ).show();
    }

    private void openHomeSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, R.string.home_settings_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    private Button button(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setMinHeight(dp(52));
        return button;
    }

    private TextView sectionTitle(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(Color.rgb(25, 28, 36));
        view.setTextSize(18);
        view.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        return view;
    }

    private TextView paragraph(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(Color.rgb(82, 88, 102));
        view.setTextSize(15);
        view.setLineSpacing(dp(2), 1.0f);
        return view;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private LinearLayout.LayoutParams topMargin(int dp) {
        LinearLayout.LayoutParams params = matchWrap();
        params.topMargin = dp(dp);
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private int lighten(int color, float amount) {
        int red = Math.min(255, (int) (Color.red(color) + (255 - Color.red(color)) * amount));
        int green = Math.min(255, (int) (Color.green(color) + (255 - Color.green(color)) * amount));
        int blue = Math.min(255, (int) (Color.blue(color) + (255 - Color.blue(color)) * amount));
        return Color.rgb(red, green, blue);
    }

    private int adjustAlpha(int color, float factor) {
        return Color.argb(
                Math.round(Color.alpha(color) * factor),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    private static class ShortcutSpec {
        final String id;
        final int labelResId;
        final int backgroundColor;
        final int foregroundColor;
        final int glyph;

        ShortcutSpec(String id, int labelResId, int backgroundColor, int foregroundColor, int glyph) {
            this.id = id;
            this.labelResId = labelResId;
            this.backgroundColor = backgroundColor;
            this.foregroundColor = foregroundColor;
            this.glyph = glyph;
        }
    }
}
