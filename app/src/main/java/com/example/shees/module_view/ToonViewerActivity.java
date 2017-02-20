package com.example.shees.module_view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class ToonViewerActivity extends AppCompatActivity {
    private ArrayList<String> imageUrls;
    private int imagecount = 0;
    private String imgeUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_viewr);
    }
}
