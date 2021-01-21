package com.example.chareco;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SpeechActivity extends AppCompatActivity {
    Button speechButton;
    Button saveButton;
    TextView recognizedText;
    String txt;
    private static final int WRITE_EXTERNAL_STORAGE_CODE=1;
    private static final int reco_result=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        speechButton=findViewById(R.id.record_btn);
        recognizedText=findViewById(R.id.recotext);
        saveButton=findViewById(R.id.save);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt=recognizedText.getText().toString().trim();
                if(txt.isEmpty()) {
                    Toast.makeText(SpeechActivity.this, "No text to save !!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {

                        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED){
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions, WRITE_EXTERNAL_STORAGE_CODE);
                        }

                        else{
                            saveToTxtFile(txt);
                        }
                    }
                    else{
                        saveToTxtFile(txt);
                    }
                }

            }
        });



        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speachIntent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speachIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speachIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speach to text recognition");
                startActivityForResult(speachIntent,reco_result);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == reco_result && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            recognizedText.setText(matches.get(0).toString());
        }
    }

}