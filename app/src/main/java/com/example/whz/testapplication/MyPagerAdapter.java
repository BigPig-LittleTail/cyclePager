package com.example.whz.testapplication;

import android.view.ViewGroup;

public abstract class MyPagerAdapter {
    public abstract Object getViewAtPosition(ViewGroup parent,int position);

    public abstract void destroyViewAtPosition(ViewGroup parent,int position);

    public abstract int getCount();

}
