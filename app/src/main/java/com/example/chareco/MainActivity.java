package com.example.chareco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button botonspch;
    Button botonimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonspch = (Button) findViewById(R.id.botondialspch);
        botonimg  = (Button) findViewById(R.id.botomdialimg);

        botonspch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenSpeechActivity();
            }
        });

        botonimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImageActivity();
            }
        });

    }

    public void OpenSpeechActivity(){
        Intent speechintent = new Intent(this, SpeechActivity.class);
        startActivity(speechintent);
    }
    public void OpenImageActivity(){
        Intent imageintent = new Intent(this, ImageActivity.class);
        startActivity(imageintent);
    }



}