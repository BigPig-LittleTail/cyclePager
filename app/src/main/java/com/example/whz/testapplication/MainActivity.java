package com.example.whz.testapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TestPager mTestPager;
    Handler mHandler = new Handler();
    private ArrayList<View> test = new ArrayList<>();
    private ArrayList<Integer> colors = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTestPager = findViewById(R.id.myViewPager);

        View view0;
        View view1;
        View view2;
        View view3;
        View view4;
        view0 = new View(mTestPager.getContext());
        view0.setBackgroundColor(Color.RED);
        view1 = new View(mTestPager.getContext());
        view1.setBackgroundColor(Color.BLACK);
        view2 = new View(mTestPager.getContext());
        view2.setBackgroundColor(Color.BLUE);
        view3 = new View(mTestPager.getContext());
        view3.setBackgroundColor(Color.YELLOW);
        view4 = new View(mTestPager.getContext());
        view4.setBackgroundColor(Color.GREEN);

        test.add(view0);
        test.add(view1);
        test.add(view2);
//        test.add(view3);
//        test.add(view4);

        mTestPager.setViewList(test);


        //myViewPager = findViewById(R.id.myViewPager);
    }
}

//public class MainActivity extends AppCompatActivity {
//    ViewPager mvpGoods;
//    ArrayList<Integer>mGoodsList;
//    ArrayList<ImageView> mivGoodsList;
//    GoodsAdapter mAdapter;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.testviewpager);
//        initView();
//    }
//    private void initView(){
//        //准备数据源
//        mGoodsList=new ArrayList<>();
//        mGoodsList.add(R.drawable.ic_launcher_background);
////        mGoodsList.add(R.drawable.ic_launcher_foreground);
////        mGoodsList.add(R.drawable.ic_launcher_background);
////        mGoodsList.add(R.drawable.ic_launcher_foreground);
//
//        mivGoodsList=new ArrayList<>();
//        for(int i=0;i<mGoodsList.size();i++){
//            ImageView iv=new ImageView(this);
//            iv.setImageResource(mGoodsList.get(i));
//            mivGoodsList.add(iv);
//        }
//        mAdapter=new GoodsAdapter(this,mivGoodsList);
//        mvpGoods= (ViewPager) findViewById(R.id.viewTest);
//        mvpGoods.setAdapter(mAdapter);
//    }
//    //准备适配器类
//    class GoodsAdapter extends PagerAdapter {
//        Context context;
//        ArrayList<ImageView> ivGoodsList;
//        public GoodsAdapter(Context context,ArrayList<ImageView>ivGoodsList){
//            this.context=context;
//            this.ivGoodsList=ivGoodsList;
//        }
//
//        @Override
//        public int getCount() {
//            return Integer.MAX_VALUE;
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view==object;
//        }
//
//        @Override
//        public ImageView instantiateItem(ViewGroup container, int position) {
//            ImageView imageView=ivGoodsList.get(position%ivGoodsList.size());
//            container.addView(imageView);
//            return imageView;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            ImageView imageView=(ImageView)object;
//            container.removeView(imageView);
//        }
//    }
//}
