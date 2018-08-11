package com.example.whz.testapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MyViewPager myViewPager;
    Handler mHandler = new Handler();
    private ArrayList<View> test = new ArrayList<>();
    private ArrayList<Integer> colors = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myViewPager = findViewById(R.id.myViewPager);
    }
}
