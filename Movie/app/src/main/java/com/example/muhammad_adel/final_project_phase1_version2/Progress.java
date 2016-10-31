package com.example.muhammad_adel.final_project_phase1_version2;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


public class Progress extends AppCompatActivity {
    public static AppCompatActivity fa; // to access progress activity form FragMovie fragment so i can finish it
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
        fa = this;

    }

}
