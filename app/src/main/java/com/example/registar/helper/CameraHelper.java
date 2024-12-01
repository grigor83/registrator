package com.example.registar.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.registar.R;
import com.example.registar.util.Constants;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

public class CameraHelper extends AppCompatActivity {

    public static ActivityResultLauncher<Intent> pickImageLauncher, takePictureLauncher, scanBarcodeLauncher;
    public static Uri photoURI;
    public static String imagePath;

    public static void showImageOptions(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.select_image_source);
        String[] options = {context.getString(R.string.pick_from_gallery), context.getString(R.string.take_photo)};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0)
                // Pick from Gallery
                openImagePicker();
            else if (which == 1)
                // Take Photo
                requestCameraPermission(context);
        });
        builder.show();
    }

    private static void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent); // Launch the image picker
    }

    public static void requestCameraPermission(Context context){
        // Check if camera permission is already granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions((Activity) context, Constants.CAMERA_PERMISSION, Constants.CAMERA_PERMISSION_CODE);
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
                File photoFile = createFile(context);
                imagePath = photoFile.getAbsolutePath();
                photoURI = FileProvider.getUriForFile(context, "com.example.registar.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureLauncher.launch(cameraIntent);
            }
            catch (IOException ex) {
                Toast.makeText(context, R.string.error_creating_file, Toast.LENGTH_SHORT).show();
                photoURI = null;
            }
    }

    private static File createFile(Context context) throws IOException {
        //  App-Specific Directory does not require WRITE_EXTERNAL_STORAGE permissions; files in this dir are not
        // accessible from MediaStore content provider
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                Log.e("File Creation", "Failed to create directory: " + storageDir.getAbsolutePath());
                return null;
            } else {
                Log.d("File Creation", "Directory created or exists: " + storageDir.getAbsolutePath());
            }
        } else {
            Log.e("File Creation", "Failed to get external directory.");
            return null;
        }

        // Create a unique file name
        String timeStamp = String.valueOf(LocalDateTime.now());
        String fileName = "IMG_" + timeStamp + ".jpg";
        // Create the file
        return new File(storageDir, fileName);
        //return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public static void createFileFromUri(Context context, ImageView imageView, Uri uri) {
        ExecutorService executor = ExecutorHelper.getExecutor();
        executor.execute(() -> {
            try {
                File newFile = createFile(context);
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                OutputStream outputStream = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close(); outputStream.close();

                imagePath = newFile.getAbsolutePath();
                photoURI = FileProvider.getUriForFile(context, "com.example.registar.fileprovider", newFile);
                BitmapHelper.processImageInBackground(context, imageView, photoURI, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Constants.CAMERA_PERMISSION_CODE) {
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
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public static void showBarcodeInputDialog(EditText barcodeView) {
        // Create a dialog to choose between entering manually or scanning
        AlertDialog.Builder builder = new AlertDialog.Builder(barcodeView.getContext());
        builder.setTitle(R.string.choose_barcode_input_method);
        String[] options = {barcodeView.getContext().getString(R.string.enter_number), barcodeView.getContext().getString(R.string.scan_barcode)};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0)
                // User chooses to enter the barcode manually (input as number)
                barcodeView.setInputType(InputType.TYPE_CLASS_NUMBER);
            else if (which == 1)
                scanBarcode(barcodeView);
        });
        builder.show();
    }

    private static void scanBarcode(EditText barcodeView) {
        // Start the barcode scanner activity (using a library like Zxing or ZBar)
        Intent intent = new Intent(barcodeView.getContext(), CaptureActivity.class); // Zxing scanner activity
        scanBarcodeLauncher.launch(intent);
    }

}
