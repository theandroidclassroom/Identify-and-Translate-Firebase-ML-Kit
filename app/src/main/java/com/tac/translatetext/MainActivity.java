package com.tac.translatetext;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;


public class MainActivity extends AppCompatActivity {


    private TextView mSourceLang;
    private EditText mSourcetext;
    private Button mTranslateBtn;
    private TextView mTranslatedText;
    private String sourceText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSourceLang = findViewById(R.id.sourceLang);
        mSourcetext = findViewById(R.id.sourceText);
        mTranslateBtn = findViewById(R.id.translate);
        mTranslatedText = findViewById(R.id.translatedText);

        mTranslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifyLanguage();
            }
        });





    }

    private void identifyLanguage() {
        sourceText = mSourcetext.getText().toString();

        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                .getLanguageIdentification();

        mSourceLang.setText("Detecting..");
        identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals("und")){
                    Toast.makeText(getApplicationContext(),"Language Not Identified",Toast.LENGTH_SHORT).show();

                }
                else {
                    getLanguageCode(s);
                }
            }
        });
    }

    private void getLanguageCode(String language) {
        int langCode;
        switch (language){
            case "hi":
                langCode = FirebaseTranslateLanguage.HI;
                mSourceLang.setText("Hindi");
                break;
            case "ar":
                langCode = FirebaseTranslateLanguage.AR;
                mSourceLang.setText("Arabic");

                break;
            case "ur":
                langCode = FirebaseTranslateLanguage.UR;
                mSourceLang.setText("Urdu");

                break;
                default:
                    langCode = 0;
        }

        translateText(langCode);
    }

    private void translateText(int langCode) {
        mTranslatedText.setText("Translating..");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                //from language
                .setSourceLanguage(langCode)
                // to language
                .setTargetLanguage(FirebaseTranslateLanguage.EN)
                .build();

        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .build();


        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        mTranslatedText.setText(s);
                    }
                });
            }
        });
    }


}
