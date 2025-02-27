    package com.dorvis.textrecognitionandroid;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.Locale;

public class SpeakOut extends AppCompatActivity implements OnClickListener, TextToSpeech.OnInitListener {

    TextView tv_curText;
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    ImageView speak,save;
    String curText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak_out);
        Intent intent = getIntent();
         curText= intent.getStringExtra("curText");

        tv_curText=findViewById(R.id.tv_curText);
        tv_curText.setText(curText);
        tv_curText.setMovementMethod(new ScrollingMovementMethod());
        speak=findViewById(R.id.speak);
        save=findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             DBHelper mydb=new DBHelper(getApplicationContext());
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
             mydb.insertContact(ts,curText);
             Toast.makeText(getApplicationContext(),"Saved", Toast.LENGTH_SHORT).show();
            }
        });
        speak.setOnClickListener(this);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);
                myTTS.setSpeechRate(1.5f);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        myTTS.speak(curText, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(myTTS !=null){
            myTTS.stop();
            myTTS.shutdown();
        }
    }
}