package com.example.registar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.registar.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_PERMISSION_CODE = 100;
    public static final String[] CAMERA_PERMISSION = { Manifest.permission.CAMERA };
    public static ActivityResultLauncher<Intent> takePictureLauncher;
    public static Uri photoURI;

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

    public static void openCamera(Context context){
        // Check if camera permission is already granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions((Activity) context, CAMERA_PERMISSION, CAMERA_PERMISSION_CODE);
        } else {
            // Permission is already granted
            takePicture(context);
        }
    }

    private static void takePicture(Context context){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure the camera intent can be handled
        if (cameraIntent.resolveActivity(context.getPackageManager()) != null)
            try {
                File photoFile = createImageFile(context);
                photoURI = FileProvider.getUriForFile(context, "com.example.registar.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                takePictureLauncher.launch(cameraIntent);
            }
            catch (IOException ex) {
                Toast.makeText(context, R.string.error_creating_file, Toast.LENGTH_SHORT).show();
            }
    }

    private static File createImageFile(Context context) throws IOException {
        // Create a unique file name
        String timeStamp = String.valueOf(LocalDateTime.now());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES); // Private app storage

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                takePicture(this);
            } else {
                // Permission denied
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    // "Don't Ask Again" scenario detected
                    openAppSettings();
                } else {
                    // Permission denied without "Don't Ask Again"
                    Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openAppSettings() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_denied_title)
                .setMessage(R.string.permission_permanent_denied)
                .setPositiveButton(R.string.settings, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}