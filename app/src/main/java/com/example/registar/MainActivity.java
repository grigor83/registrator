package com.example.registar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.registar.adapter.ViewPagerAdapter;
import com.example.registar.util.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static RegistarDatabase registarDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences preferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);
        String languageCode = preferences.getString(Constants.APP_PREFERENCES, "sr");
        setLocale(languageCode);

        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle(R.string.app_name);

        // ViewPager je kontejner koji se nalazi u activity_main i koji sadrži fragmente
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(this));
        // Connect TabLayout with ViewPager2
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText(R.string.assets_tab_name); break;
                        case 1: tab.setText(R.string.employers_tab_name); break;
                        case 2: tab.setText(R.string.locations_tab_name); break;
                        case 3: tab.setText(R.string.census_lists_tab_name); break;
                        case 4: tab.setText(R.string.department_tab_name); break;
                    }
                }).attach();

        registarDB = RegistarDatabase.getInstance(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.popup, menu);

        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem editItem = menu.findItem(R.id.action_edit);
        deleteItem.setVisible(false);
        editItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_languages){
            showLanguageDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        registarDB.cleanUp();
        //ExecutorHelper.executor.shutdown();
        super.onDestroy();
    }

    public static void showCustomToast(Context context, String message){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_custom, null);

        Toast toast = new Toast(context);
        if (message != null){
            TextView text = layout.findViewById(R.id.toast_text);
            text.setText(message);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0); // Center of the screen
        toast.setView(layout);
        toast.show();
    }

    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_language);
        String[] options = { getString(R.string.serbian), getString(R.string.english)};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0)
                changeLocale("sr");
            else if (which == 1)
                changeLocale("en");
        });
        builder.show();
    }
    private void changeLocale(String languageCode) {
        SharedPreferences preferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.APP_PREFERENCES, languageCode);
        editor.apply();

        restartApp();
    }
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void restartApp(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        // Clear the activity stack so the app is restarted fresh
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        // Finish the current activity (optional, for a complete restart)
        finish();
    }

}