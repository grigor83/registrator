package com.example.registar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.registar.adapter.ViewPagerAdapter;
import com.example.registar.helper.ExecutorHelper;
import com.example.registar.model.Employee;
import com.example.registar.model.Location;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.concurrent.ExecutorService;

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
        if (item.getItemId() == R.id.action_settings)
            Toast.makeText(this, R.string.settings, Toast.LENGTH_SHORT).show();
        else if (item.getItemId() == R.id.action_languages)
            Toast.makeText(this, R.string.languages, Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    public static void showCustomToast(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_custom, null);
        //TextView text = layout.findViewById(R.id.toast_text);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        registarDB.cleanUp();
        ExecutorHelper.executor.shutdown();
        super.onDestroy();
    }

    public void createEmployee(View v){
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            Employee employee = new Employee("John", "Don", "IT");
            MainActivity.registarDB.employeeDao().insert(employee);
            Location location = new Location("Banja Luka", "ulica Braće Kavića");
            MainActivity.registarDB.locationDao().insert(location);
            location = new Location("Kotor Varoš", "Svetosavska ulica");
            MainActivity.registarDB.locationDao().insert(location);
        });
    }

}