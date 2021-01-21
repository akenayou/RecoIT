package com.example.chareco;



import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ImageActivity extends Base {

    private Button mButton;
    private Button button_cam;
    private Button button_save;

    private static final int WRITE_EXTERNAL_STORAGE_CODE=1;

    String txt;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);


        button_cam = findViewById(R.id.button_cam);
        mButton = findViewById(R.id.button_text);
        textView = findViewById(R.id.text_display);
        button_save = findViewById(R.id.save_text);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                txt=textView.getText().toString().trim();

                if(txt.isEmpty()) {
                    Toast.makeText(ImageActivity.this, "No text to save !!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {

                        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED){
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions, WRITE_EXTERNAL_STORAGE_CODE);
                        }

                        else{
                            saveToTxtFile(txt);
                        }
                    }
                    else
                    {

                        saveToTxtFile(txt);

                    }
                }



            }
        });



        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBitmap != null) {
                    runTextRecognition();
                } else {
                    showToast("Choose a proper image");
                }

            }
        });
        button_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });

    }




    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToTxtFile(txt);
            } else {
                Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT);
            }
        }

    }



    private void saveToTxtFile(String txt) {

        String timestamp =new SimpleDateFormat("yyyyMMdd_HHmmss"
                , Locale.getDefault()).format(System.currentTimeMillis());

        try{
            File path= Environment.getExternalStorageDirectory();
            File dir=new File(path +"/Chareco/");
            dir.mkdirs();
            String filename= "Character_Recognition"+ timestamp+ ".txt";
            File file = new File(dir,filename);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(txt);
            bw.close();

            Toast.makeText(this,filename + "is saved to \n "+dir,Toast.LENGTH_SHORT ).show();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage() ,Toast.LENGTH_SHORT ).show();


        }

    }

    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(myBitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                .getVisionTextDetector();
        mButton.setEnabled(false);
        detector.detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                mButton.setEnabled(true);
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                mButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {

        StringBuilder t = new StringBuilder();

        List<FirebaseVisionText.Block> blocks = texts.getBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }

        for (int i = 0; i < blocks.size(); i++) {
            t.append(" ").append(blocks.get(i).getText());
        }

        showToast(t.toString());
    }

    private void showToast(String message) {
        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        textView.setText(message);
    }



}