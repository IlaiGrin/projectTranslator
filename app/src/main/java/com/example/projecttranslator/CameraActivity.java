package com.example.projecttranslator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    ProcessCameraProvider cameraProvider;
    CameraSelector cameraSelector;
    TextRecognizer recognizer;
    PreviewView previewView;
    Preview preview;
    TextView display;
    ImageAnalysis imageAnalysis;
    UseCaseGroup useCaseGroup;
    final int PIC_CROP = 1;
    Bitmap selectedBitmap;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        display = findViewById(R.id.display_txt);

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        // Request camera permissions
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, 1);

        imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        findViewById(R.id.take_photo_img).setOnClickListener(view -> {
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy ->{
                @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
                if(mediaImage != null){
                    InputImage image =
                            InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                    recognizer.process(image).addOnSuccessListener(visionText -> {
                        Toast.makeText(this, "analysis succeeded", Toast.LENGTH_SHORT).show();
                        display.setText(visionText.getText()+"");
                    }).addOnCompleteListener(complete->{ mediaImage.close();imageProxy.close();})
                            .addOnFailureListener(e ->Log.d("debug1", e+""));
                }
                imageAnalysis.clearAnalyzer();
            });
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, useCaseGroup);
            });

        findViewById(R.id.backward_img).setOnClickListener(view -> startActivity(new Intent(this, AddWordActivity.class)));
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else
                    Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void startCamera(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {    //verify that its initialization succeeded
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview();
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this,"binding failed",Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void bindPreview() {
        preview = new Preview.Builder().build();
        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ViewPort viewPort = new ViewPort.Builder(new Rational(1, 1),
                getDisplay().getRotation()).build();
        useCaseGroup = new UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalysis)
                .setViewPort(viewPort)
                .build();
        try {
            cameraProvider.unbindAll();      // Unbind use cases before rebinding
            cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, useCaseGroup);
        } catch(Exception exception) {
            Toast.makeText(this,"binding failed because "+exception,Toast.LENGTH_LONG).show();
            previewView.setVisibility(View.GONE);
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 3);
            cropIntent.putExtra("aspectY", 2);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 200);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException e) {   // respond to users whose devices do not support the crop action
            Toast.makeText(this,"failed because "+e,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIC_CROP) {
            if (data != null) {
                Bundle extras = data.getExtras();
                selectedBitmap = extras.getParcelable("data");       // get the cropped bitmap
                InputImage image = InputImage.fromBitmap(selectedBitmap, 2);
                recognizer.process(image).addOnSuccessListener(visionText -> {
                    Toast.makeText(this, "analysis succeeded", Toast.LENGTH_SHORT).show();
                    display.setText(visionText.getText() + "");
                });
            }
        }
    }
    private Bitmap cropImage(Image image, int xOffset, int yOffset, int cropWidth, int cropHeight) {
        //Convert image to Bitmap
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //Crop the Bitmap
        bitmap = Bitmap.createBitmap(bitmap, xOffset, yOffset, cropWidth, cropHeight);

        return bitmap;
    }
}